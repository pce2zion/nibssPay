package com.example.nibsstransfer.service.serviceImpl;

import com.example.nibsstransfer.client.TransferClient;
import com.example.nibsstransfer.client.res.TransferClientResponse;
import com.example.nibsstransfer.converter.TransferConvert;
import com.example.nibsstransfer.dto.requestDto.PaymentRequestDto;
import com.example.nibsstransfer.dto.responseDto.PaymentResponseModel;
import com.example.nibsstransfer.entity.AccountEntity;
import com.example.nibsstransfer.entity.TransactionEntity;
import com.example.nibsstransfer.repository.AccountRepository;
import com.example.nibsstransfer.repository.TransferRepository;
import com.example.nibsstransfer.service.TransferService;
import com.example.nibsstransfer.util.NibssUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.nibsstransfer.constants.NibssConstants.FEE_CAP;
import static com.example.nibsstransfer.constants.NibssConstants.TXN_FEE_PERCENTAGE;
import static com.example.nibsstransfer.converter.TransferConvert.covertToTransactionsList;

@Service
@Slf4j
public class TransferServiceImpl implements TransferService {
    private final TransferRepository transferRepository;
   private final AccountRepository accountRepository;

   private final TransferClient client;


    public TransferServiceImpl(AccountRepository accountRepository, TransferRepository transferRepository, TransferClient client) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
        this.client = client;
    }

    @Override
    public PaymentResponseModel receiveAndProcess(PaymentRequestDto requestDto) {

        /*
          every transaction received into nibss system is first saved, before being processed. We would check if the transaction is
          duplicate before saving first
        */
        TransactionEntity transaction;
//        Start by verifying that the sender account and beneficiary account are both valid. Normally, this would be done by an API call,
//        but we can mock it by calling accounts already existing in a database

        AccountEntity senderAccount = accountRepository.findByAccountNumberAndAccountName(
                requestDto.getSender().getSenderAccountNumber(), requestDto.getSender().getSenderName());
        log.info("Sender account details {}", senderAccount);

        if(senderAccount == null){
            log.warn("Account details were not found. This transaction cannot be processed {}", requestDto);
            return TransferConvert.convertToPaymentResponseModel(requestDto);
        }
        //save every transaction that comes into the system
        transaction = transferRepository.save(TransferConvert.convertToEntity(requestDto, senderAccount));

        try {
            transaction = transferRepository.findAccountByTransactionReference(transaction.getTransactionReference());

            if (senderAccount.getAccountBalance().compareTo(transaction.getAmountToSend()) <= 0) {

                log.warn("Insufficient funds. Transaction failed for transaction {} with reference number {}",
                        transaction, transaction.getTransactionReference());
                return TransferConvert.convertToPaymentResponseModel(transaction);
            }

            BigDecimal fee = TXN_FEE_PERCENTAGE.multiply(transaction.getAmountToSend());
            // Cap the fee at N100 if it is more than N100
            BigDecimal transactionFee = fee.min(FEE_CAP);
            BigDecimal billedAmount = transaction.getAmountToSend().add(transactionFee);


            //perform transfer client call to beneficiary bank account
            HttpResponse<String> clientResponse= client.sendTransactionToBeneficiaryBank(transaction);

            if(clientResponse.statusCode() == 200){
                //build response object from client
                TransferClientResponse clientRes = TransferClientResponse.build(clientResponse.body());

                if(clientRes != null) {
                    updateAccountDetails(billedAmount, senderAccount, clientRes, transaction);
                    //update transaction status in the database
                    transaction = transferRepository.save(TransferConvert.upDateTxnWhenTransactionIsProcessed(transaction,
                            billedAmount, transactionFee, clientRes));
                }
            }
            if(clientResponse.statusCode() != 200) {
                transaction = transferRepository.save(TransferConvert.upDateTxnWhenTransactionIsPending(transaction,
                        billedAmount, transactionFee));
            }
            return TransferConvert.convertToAllPaymentResponseModel(transaction);
        }catch (DuplicateKeyException e){
            log.error("This transaction with reference {} already exists {}", transaction.getTransactionReference(), transaction);
            return TransferConvert.convertToAllPaymentResponseModel(transaction);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PaymentResponseModel> getAllTransactions(String status, String senderAccountNumber, Date startDate,
                                                                                            Date endDate) {
        List<PaymentResponseModel> responseModelList = new ArrayList<>();
        List<TransactionEntity> transactionList = new ArrayList<>();

        try {
            // Adjust startDate  and endDate to the beginning of the day
            startDate = NibssUtils.getStartOfDay(startDate);
            endDate = NibssUtils.getEndOfDay(endDate);

            //since parameters are optional, perform checks for them differently
            if (status != null && senderAccountNumber != null) {
                transactionList = transferRepository.findByStatusAndSenderAccountNumberAndGmtCreatedBetween(status, senderAccountNumber, startDate, endDate);
            } else if (status != null) {
                transactionList = transferRepository.findByStatusAndGmtCreatedBetween(status, startDate, endDate);
            } else if (senderAccountNumber != null) {
                transactionList = transferRepository.findBySenderAccountNumberAndGmtCreatedBetween(senderAccountNumber, startDate, endDate);
            } else {
                transactionList = transferRepository.findByGmtCreatedBetween(startDate, endDate);
            }
            return covertToTransactionsList(responseModelList, transactionList);

        }catch(Exception e){
            log.error("Exception occurred while trying to fetch transactions with parameters {} {} {} {}", status,
                    senderAccountNumber, startDate, endDate);
            return covertToTransactionsList(responseModelList, transactionList);
        }
    }

    @Override
    public List<PaymentResponseModel> getDailySummary(Date date) {

        Date startDate = NibssUtils.getStartOfDay(date);
        Date  endDate = NibssUtils.getEndOfDay(date);

        List<PaymentResponseModel> responseModelList = new ArrayList<>();
        List<TransactionEntity> dailyTransactions = new ArrayList<>();

        try {
            dailyTransactions = transferRepository.findByGmtCreatedBetween(startDate, endDate);
            if (CollectionUtils.isEmpty(dailyTransactions)) {
                return responseModelList;
            }
            return covertToTransactionsList(responseModelList, dailyTransactions);
        }catch (Exception e){
            log.error("Exception occurred while trying to fetch transactions with parameters {}", date);
            return covertToTransactionsList(responseModelList, dailyTransactions);
        }
    }

    private void updateAccountDetails(BigDecimal billedAmount, AccountEntity senderAccount, TransferClientResponse res, TransactionEntity transaction) {

        senderAccount.setAccountBalance(senderAccount.getAccountBalance().subtract(billedAmount));
        //get beneficiary details from the database and save the new balance

        AccountEntity beneficiaryAccount = new AccountEntity();
        beneficiaryAccount.setAccountName(transaction.getBeneficiaryAccountName());
        beneficiaryAccount.setAccountNumber(20000 + transaction.getBeneficiaryAccountNumber());
        beneficiaryAccount.setAccountBalance(transaction.getAmountToSend());
        beneficiaryAccount.setBankName("Bank");
        beneficiaryAccount.setBvn("8678289737");
        beneficiaryAccount.setBankCode("503");


       // beneficiaryAccount.setAccountBalance(beneficiaryAccount.getAccountBalance().add(transaction.getAmountToSend()));

        accountRepository.save(senderAccount);
        accountRepository.save(beneficiaryAccount);
    }
}
