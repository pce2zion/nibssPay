package com.example.nibsstransfer.service;

import com.example.nibsstransfer.dto.requestDto.PaymentRequestDto;
import com.example.nibsstransfer.dto.responseDto.PaymentResponseModel;
import com.example.nibsstransfer.entity.TransactionEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface TransferService {
    PaymentResponseModel receiveAndProcess(PaymentRequestDto requestDto);

   List<PaymentResponseModel> getAllTransactions(String status, String senderAccountNumber, Date startDate, Date endDate);

    List<PaymentResponseModel> getDailySummary(Date date);

    default List<TransactionEntity> getAllSuccessfulTransactions(String status) {
        throw new UnsupportedOperationException("This will be implemented in schedule job");
    }

}
