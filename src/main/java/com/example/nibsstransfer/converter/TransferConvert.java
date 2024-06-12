package com.example.nibsstransfer.converter;

import com.alibaba.fastjson.JSON;
import com.example.nibsstransfer.client.req.TransferClientRequest;
import com.example.nibsstransfer.client.res.TransferClientResponse;
import com.example.nibsstransfer.constants.NibssConstants;
import com.example.nibsstransfer.dto.request.PaymentRequest;
import com.example.nibsstransfer.dto.requestDto.BeneficiaryRequestDto;
import com.example.nibsstransfer.dto.requestDto.OtherDetailsDto;
import com.example.nibsstransfer.dto.requestDto.PaymentRequestDto;
import com.example.nibsstransfer.dto.requestDto.SenderRequestDto;
import com.example.nibsstransfer.dto.response.PaymentResponse;
import com.example.nibsstransfer.dto.responseDto.PaymentResponseModel;
import com.example.nibsstransfer.entity.AccountEntity;
import com.example.nibsstransfer.entity.TransactionEntity;
import com.example.nibsstransfer.util.NibssUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.nibsstransfer.constants.NibssConstants.*;

/**
 * @author Peace Obute
 * @since 06/06/2024
 */
public class TransferConvert {

    public static PaymentRequestDto convert(PaymentRequest request){

        PaymentRequestDto requestDto = new PaymentRequestDto();
        requestDto.setAmount(request.getAmount());

        buildSenderDtoDetails(request, requestDto);
        buildBeneficiaryDtoDetails(request, requestDto);
        buildOtherDetailsDto(request, requestDto);

        return requestDto;
    }

    private static void buildSenderDtoDetails(PaymentRequest request, PaymentRequestDto requestDto) {
        SenderRequestDto senderRequestDto = new SenderRequestDto();
        senderRequestDto.setSenderName(request.getSender().getSenderAccountName());
        senderRequestDto.setSenderAccountNumber(request.getSender().getSenderAccountNumber());

        requestDto.setSender(senderRequestDto);
    }

    private static void buildBeneficiaryDtoDetails(PaymentRequest request, PaymentRequestDto requestDto) {
        BeneficiaryRequestDto beneficiaryRequestDto = new BeneficiaryRequestDto();
        beneficiaryRequestDto.setBeneficiaryAccountName(request.getBeneficiary().getBeneficiaryAccountName());
        beneficiaryRequestDto.setBeneficiaryAccountNumber(request.getBeneficiary().getBeneficiaryAccountNumber());

        requestDto.setBeneficiary(beneficiaryRequestDto);
    }

    private static void buildOtherDetailsDto(PaymentRequest request, PaymentRequestDto requestDto){
        OtherDetailsDto otherDetailsDto = new OtherDetailsDto();
        otherDetailsDto.setDescription(request.getOtherDetailsRequest().getDescription());

        requestDto.setOtherDetails(otherDetailsDto);
    }

    public static PaymentResponse convertToResponse(PaymentResponseModel model){
        if(model == null){
            return null;
        }

        PaymentResponse response = new PaymentResponse();
        response.setTransactionReference(model.getTransactionReference());
        response.setAmount(model.getAmount());
        response.setBilledAmount(model.getBilledAmount());
        response.setSenderAccountNumber(model.getSenderAccountNumber());
        response.setSenderAccountName(model.getSenderAccountName());
        response.setSenderBvn(model.getSenderBvn());
        response.setSenderBankCode(model.getSenderBankCode());
        response.setBeneficiaryAccountNumber(model.getBeneficiaryAccountNumber());
        response.setBeneficiaryAccountName(model.getBeneficiaryAccountName());
        response.setBeneficiaryBvn(model.getBeneficiaryBvn());
        response.setBeneficiaryBankCode(model.getBeneficiaryBankCode());
        response.setGmtCreated(model.getGmtCreated());
        response.setGmtModified(model.getGmtModified());
        response.setTransactionFee(model.getTransactionFee());
        response.setStatus(model.getStatus());
        response.setCommission(model.getCommission());
        response.setTransactionIdFromBeneficiary(model.getTransactionIdFromBeneficiary());

        return response;
    }

    public static PaymentResponseModel convertToPaymentResponseModel(PaymentRequestDto dto){

        PaymentResponseModel model = new PaymentResponseModel();
        model.setTransactionReference(null);
        model.setAmount(dto.getAmount());
        model.setSenderAccountNumber(dto.getSender().getSenderAccountNumber());
        model.setSenderAccountName(dto.getSender().getSenderName());
        model.setSenderBvn(null);
        model.setSenderBankCode(null);
        model.setBeneficiaryAccountNumber(dto.getBeneficiary().getBeneficiaryAccountNumber());
        model.setBeneficiaryAccountName(dto.getBeneficiary().getBeneficiaryAccountName());
        model.setBeneficiaryBvn(null);
        model.setBeneficiaryBankCode(null);
        model.setGmtCreated(new Date());
        model.setGmtModified(new Date());
        model.setTransactionFee(null);
        model.setStatus(ACCOUNT_NOT_FOUND);

        return model;
    }

    public static PaymentResponseModel convertToPaymentResponseModel(TransactionEntity transaction){

        PaymentResponseModel model = new PaymentResponseModel();
        model.setTransactionReference(transaction.getTransactionReference());
        model.setAmount(transaction.getAmountToSend());
        model.setSenderAccountNumber(transaction.getSenderAccountNumber());
        model.setSenderAccountName(transaction.getSenderAccountName());
        model.setSenderBvn(transaction.getSenderBvn());
        model.setSenderBankCode(transaction.getSenderBankCode());
        model.setBeneficiaryAccountNumber(transaction.getBeneficiaryAccountNumber());
        model.setBeneficiaryAccountName(transaction.getBeneficiaryAccountName());
        model.setGmtCreated(transaction.getGmtCreated());
        model.setGmtModified(transaction.getGmtModified());
        model.setTransactionFee(null);
        model.setBilledAmount(null);
        model.setStatus(INSUFFICIENT_FUNDS);

        return model;
    }

    public static PaymentResponseModel convertToAllPaymentResponseModel(TransactionEntity transaction){

        PaymentResponseModel model = new PaymentResponseModel();
        model.setTransactionReference(transaction.getTransactionReference());
        model.setAmount(transaction.getAmountToSend());
        model.setSenderAccountNumber(transaction.getSenderAccountNumber());
        model.setSenderAccountName(transaction.getSenderAccountName());
        model.setSenderBvn(transaction.getSenderBvn());
        model.setSenderBankCode(transaction.getSenderBankCode());
        model.setBeneficiaryAccountNumber(transaction.getBeneficiaryAccountNumber());
        model.setBeneficiaryAccountName(transaction.getBeneficiaryAccountName());
        model.setGmtCreated(transaction.getGmtCreated());
        model.setGmtModified(transaction.getGmtModified());
        model.setTransactionFee(transaction.getTransactionFee());
        model.setBilledAmount(transaction.getBilledAmount());
        model.setStatus(transaction.getStatus());
        model.setTransactionIdFromBeneficiary(transaction.getTransactionIdFromBeneficiary());
        model.setCommission(transaction.getCommission());

        return model;
    }

    public static TransactionEntity convertToEntity(PaymentRequestDto requestDto, AccountEntity senderAccount){

        TransactionEntity transactionToSave = new TransactionEntity();
        transactionToSave.setTransactionReference(NibssUtils.generateRandomNumberString(10));
        transactionToSave.setAmountToSend(requestDto.getAmount());
        transactionToSave.setSenderAccountNumber(senderAccount.getAccountNumber());
        transactionToSave.setSenderAccountName(senderAccount.getAccountName());
        transactionToSave.setSenderBvn(senderAccount.getBvn());
        transactionToSave.setSenderBankCode(senderAccount.getBankCode());
        transactionToSave.setBeneficiaryAccountName(requestDto.getBeneficiary().getBeneficiaryAccountName());
        transactionToSave.setBeneficiaryAccountNumber(requestDto.getBeneficiary().getBeneficiaryAccountNumber());
        transactionToSave.setGmtCreated(new Date());
        transactionToSave.setGmtModified(new Date());

        //this would be set to true when we get the amount is sent to the beneficiary.
        transactionToSave.setIsTransactionProcessed(false);
        transactionToSave.setAmountToSend(requestDto.getAmount());
        transactionToSave.setIsCommissionWorthy(false);
        transactionToSave.setCommission(null);
        transactionToSave.setStatus(PENDING);
        transactionToSave.setTransactionFee(null);
        transactionToSave.setBilledAmount(null);
        transactionToSave.setTransactionIdFromBeneficiary(null);

        return transactionToSave;
    }

    public static List<PaymentResponseModel> covertToTransactionsList(List<PaymentResponseModel> responseModelList, List<TransactionEntity> transactionList) {

        if(CollectionUtils.isEmpty(transactionList)){
            return new ArrayList<>();
        }
        for(TransactionEntity transaction: transactionList){
            PaymentResponseModel model = new PaymentResponseModel();

            model.setTransactionReference(transaction.getTransactionReference());
            model.setAmount(transaction.getAmountToSend());
            model.setSenderAccountNumber(transaction.getSenderAccountNumber());
            model.setSenderAccountName(transaction.getSenderAccountName());
            model.setSenderBvn(transaction.getSenderBvn());
            model.setSenderBankCode(transaction.getSenderBankCode());
            model.setBeneficiaryAccountNumber(transaction.getBeneficiaryAccountNumber());
            model.setBeneficiaryAccountName(transaction.getBeneficiaryAccountName());
            model.setGmtCreated(transaction.getGmtCreated());
            model.setGmtModified(transaction.getGmtModified());
            model.setTransactionFee(transaction.getTransactionFee());
            model.setBilledAmount(transaction.getBilledAmount());
            model.setStatus(transaction.getStatus());

            responseModelList.add(model);
        }
        return responseModelList;
    }
    public static TransactionEntity upDateTxnWhenTransactionIsProcessed(TransactionEntity transaction, BigDecimal billedAmount,
                                                                        BigDecimal transactionFee, TransferClientResponse res) {
        transaction.setIsTransactionProcessed(true);
        transaction.setIsCommissionWorthy(true);
        transaction.setCommission(null);
        transaction.setStatus(res.getStatus().toUpperCase());
        transaction.setTransactionFee(transactionFee);
        transaction.setBilledAmount(billedAmount);
        transaction.setBeneficiaryAccountName(res.getBeneficiary().getBeneficiaryAccountName());
        transaction.setBeneficiaryAccountNumber(res.getBeneficiary().getBeneficiaryAccountNumber());
        transaction.setTransactionIdFromBeneficiary(res.getTransactionId());

        return transaction;
    }

    public static TransactionEntity upDateTxnWhenTransactionIsPending(TransactionEntity transaction) {
        transaction.setIsTransactionProcessed(false);
        transaction.setIsCommissionWorthy(false);
        transaction.setCommission(null);
        transaction.setStatus(PENDING);
        transaction.setTransactionFee(null);
        transaction.setBilledAmount(null);

        return transaction;
    }


    public static List<PaymentResponse> convertToResponse(List<PaymentResponseModel> modelList){
        if(CollectionUtils.isEmpty(modelList)){
           return new ArrayList<>();
        }

        List<PaymentResponse> responseList = new ArrayList<>();

        for(PaymentResponseModel model : modelList) {
            PaymentResponse response = new PaymentResponse();
            response.setTransactionReference(model.getTransactionReference());
            response.setAmount(model.getAmount());
            response.setSenderAccountNumber(model.getSenderAccountNumber());
            response.setSenderAccountName(model.getSenderAccountName());
            response.setSenderBvn(model.getSenderBvn());
            response.setSenderBankCode(model.getSenderBankCode());
            response.setBeneficiaryAccountNumber(model.getBeneficiaryAccountNumber());
            response.setBeneficiaryAccountName(model.getBeneficiaryAccountName());
            response.setBeneficiaryBvn(model.getBeneficiaryBvn());
            response.setBeneficiaryBankCode(model.getBeneficiaryBankCode());
            response.setGmtCreated(model.getGmtCreated());
            response.setGmtModified(model.getGmtModified());
            response.setTransactionFee(model.getTransactionFee());
            response.setBilledAmount(model.getBilledAmount());
            response.setStatus(model.getStatus());

            responseList.add(response);
        }
        return responseList;
    }

    public static String beneficiaryClientRequestMapper(TransactionEntity transaction) {

        TransferClientRequest req = new TransferClientRequest();
        req.setTransactionId(transaction.getTransactionReference());
        req.setAmount(transaction.getAmountToSend());
        req.setBeneficiaryAccountName(transaction.getBeneficiaryAccountName());
        req.setBeneficiaryAccountNumber(transaction.getBeneficiaryAccountNumber());
        req.setSenderAccountName(transaction.getSenderAccountName());
        req.setSenderAccountNumber(transaction.getSenderAccountNumber());

        return JSON.toJSONString(req);
    }

    public static void buildTransactionClientResponse(TransactionEntity entity, TransferClientResponse res) {
        res.setTransactionId(NibssUtils.generateRandomNumberString(10));
        res.setAmount(entity.getAmountToSend());
        res.setDate(new Date());
        res.getSender().setSenderAccountName(entity.getSenderAccountName());
        res.getSender().setSenderAccountNumber(entity.getSenderAccountNumber());
        res.getBeneficiary().setBeneficiaryAccountName(entity.getBeneficiaryAccountName());
        res.getBeneficiary().setBeneficiaryAccountNumber(entity.getBeneficiaryAccountNumber());
    }
}
