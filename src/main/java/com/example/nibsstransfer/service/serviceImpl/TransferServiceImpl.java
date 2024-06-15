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

import static com.example.nibsstransfer.constants.NibssConstants.*;
import static com.example.nibsstransfer.converter.TransferConvert.covertToTransactionsList;

/**
 * @author Peace Obute
 * @since 06/06/2024
 */

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
//        Start by verifying that the sender account is valid. Normally, this would be done by an API call,
//        but we can mock it by calling accounts already existing in a database

        AccountEntity senderAccount = accountRepository.findByAccountNumberAndAccountName(
                requestDto.getSender().getSenderAccountNumber(), requestDto.getSender().getSenderName());
        log.info("Sender account details {}", senderAccount);

        if(senderAccount == null || senderAccount.getBvn() == null || !senderAccount.getAccountNumber().equals(requestDto.getSender().getSenderAccountNumber())){
            log.warn("sender account details are not valid. This transaction cannot be processed {}", requestDto);
            return TransferConvert.convertToPaymentResponseModel(requestDto);
        }
        //save every transaction that comes into the system once account has been validated, generate a reference for the transaction and
        //save status as PENDING
        transaction = transferRepository.save(TransferConvert.convertToEntity(requestDto, senderAccount));

        try {
            transaction = transferRepository.findAccountByTransactionReference(transaction.getTransactionReference());

            //check if sender account has sufficient funds
            if (senderAccount.getAccountBalance().compareTo(transaction.getAmountToSend()) <= 0) {

                log.warn("Insufficient funds. Transaction failed for transaction {} with reference number {}",
                        transaction, transaction.getTransactionReference());
                return TransferConvert.convertToPaymentResponseModel(transaction);
            }

            //to calculate the transaction fee
            BigDecimal fee = TXN_FEE_PERCENTAGE.multiply(transaction.getAmountToSend());
            // Cap the fee at N100 if it is more than N100
            BigDecimal transactionFee = fee.min(FEE_CAP);
            BigDecimal billedAmount = transaction.getAmountToSend().add(transactionFee);

            //perform transfer client call to beneficiary bank account. NB sender can only be debited when beneficiary has received
            //funds.
            HttpResponse<String> clientResponse= client.sendTransactionToBeneficiaryBank(transaction);

            if(clientResponse.statusCode() == 200){
                //build response object from client
                TransferClientResponse clientRes = TransferClientResponse.build(clientResponse.body(), transaction);

                if(clientRes != null) {
                    updateAccountDetails(billedAmount, senderAccount, clientRes);
                    //update transaction status in the database and only debit sender when transaction is successful
                    transaction = transferRepository.save(TransferConvert.upDateTxnWhenTransactionIsProcessed(transaction,
                            billedAmount, transactionFee, clientRes));
                }
            }
            //if transaction to beneficiary account was not successful, sender cannot be debited
            if(clientResponse.statusCode() != 200) {
                transaction = transferRepository.save(TransferConvert.upDateTxnWhenTransactionIsFail(transaction));
            }
            return TransferConvert.convertToAllPaymentResponseModel(transaction);
        }catch (DuplicateKeyException e){
            log.error("This transaction with reference {} already exists {}", transaction.getTransactionReference(), transaction);
            return TransferConvert.convertToAllPaymentResponseModel(transaction);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    this implementation is to get a list of transactions in the database by some optional parameters
     */
    @Override
    public List<PaymentResponseModel> getAllTransactions(String status, String senderAccountNumber, Date startDate,
                                                                                            Date endDate) {
        List<PaymentResponseModel> responseModelList = new ArrayList<>();
        List<TransactionEntity> transactionList = new ArrayList<>();

        try {
            // Adjust startDate  and endDate to the beginning of the day
            if(startDate != null && endDate != null) {
                startDate = NibssUtils.getStartOfDay(startDate);
                endDate = NibssUtils.getEndOfDay(endDate);
            }
            //since parameters are optional, perform checks for them differently
            if (status != null && senderAccountNumber != null && startDate != null && endDate != null) {
                transactionList = transferRepository.findByStatusAndSenderAccountNumberAndGmtCreatedBetween(status,
                                                                             senderAccountNumber, startDate, endDate);

            }else if(status != null && senderAccountNumber != null){
                transactionList = transferRepository.findByStatusAndSenderAccountNumber(status, senderAccountNumber);
            }else if (status != null) {
                transactionList = transferRepository.findByStatus(status);
            }else if (senderAccountNumber != null) {
                transactionList = transferRepository.findBySenderAccountNumber(senderAccountNumber);
            }else if(startDate != null && endDate != null){
                transactionList = transferRepository.findByGmtCreatedBetween(startDate, endDate);
            }
            else{
                transactionList = transferRepository.findAll();
            }
            return covertToTransactionsList(responseModelList, transactionList);
        }catch(Exception e){
            log.error("Exception occurred while trying to fetch transactions with parameters {} {} {} {}", status,
                    senderAccountNumber, startDate, endDate);
            return covertToTransactionsList(responseModelList, transactionList);
        }
    }

    /*
    this impl is to get the daily summary of all transactions in a day
     */

    @Override
    public List<PaymentResponseModel> getDailySummary(Date date) {

        if(date != null) {
            Date startDate = NibssUtils.getStartOfDay(date);
            Date endDate = NibssUtils.getEndOfDay(date);

            List<PaymentResponseModel> responseModelList = new ArrayList<>();
            List<TransactionEntity> dailyTransactions = new ArrayList<>();

            try {
                dailyTransactions = transferRepository.findByGmtCreatedBetween(startDate, endDate);
                if (CollectionUtils.isEmpty(dailyTransactions)) {
                    return responseModelList;
                }
                return covertToTransactionsList(responseModelList, dailyTransactions);
            } catch (Exception e) {
                log.error("Exception occurred while trying to fetch transactions with parameters {}", date);
                return covertToTransactionsList(responseModelList, dailyTransactions);
            }
        }
        log.error("no date inputted");
        return null;
    }

    private void updateAccountDetails(BigDecimal billedAmount, AccountEntity senderAccount, TransferClientResponse res) {

        //subtract billed amount from sender account balance
        senderAccount.setAccountBalance(senderAccount.getAccountBalance().subtract(billedAmount));

        //create a beneficiary entity and store details
        AccountEntity beneficiaryAccount = new AccountEntity();
        if(res.getBeneficiary() != null) {
            beneficiaryAccount.setAccountName(res.getBeneficiary().getBeneficiaryAccountName());
            beneficiaryAccount.setAccountNumber(res.getBeneficiary().getBeneficiaryAccountNumber());
            beneficiaryAccount.setAccountBalance(res.getAmount().add(BigDecimal.valueOf(20000)));
            beneficiaryAccount.setBankName(res.getBeneficiary().getBeneficiaryBankName());
            beneficiaryAccount.setBvn(res.getBeneficiary().getBeneficiaryBvn());
            beneficiaryAccount.setBankCode(res.getBeneficiary().getBeneficiaryBankCode());
        }

        accountRepository.save(senderAccount);
        accountRepository.save(beneficiaryAccount);
    }
}
