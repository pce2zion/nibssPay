package com.example.nibsstransfer.service;

import com.example.nibsstransfer.dto.requestDto.PaymentRequestDto;
import com.example.nibsstransfer.dto.responseDto.PaymentResponseModel;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Peace Obute
 * @since 09/06/2024
 */
@Service
public interface TransferService {
    PaymentResponseModel receiveAndProcess(PaymentRequestDto requestDto);

   List<PaymentResponseModel> getAllTransactions(String status, String senderAccountNumber, Date startDate, Date endDate);

    List<PaymentResponseModel> getDailySummary(Date date);

}
