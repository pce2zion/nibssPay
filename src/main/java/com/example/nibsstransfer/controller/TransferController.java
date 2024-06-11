package com.example.nibsstransfer.controller;

import com.example.nibsstransfer.converter.TransferConvert;
import com.example.nibsstransfer.dto.requestDto.PaymentRequestDto;
import com.example.nibsstransfer.dto.responseDto.PaymentResponseModel;
import com.example.nibsstransfer.dto.request.PaymentRequest;
import com.example.nibsstransfer.dto.response.PaymentResponse;
import com.example.nibsstransfer.service.TransferService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author Peace Obute
 * @since 06/06/2024
 */
@Slf4j
@RestController
@RequestMapping("nibss")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/doTransfer")
    ResponseEntity<PaymentResponse> receiveAndProcessTransaction(@Valid @RequestBody PaymentRequest request) {
        PaymentRequestDto paymentRequestDto = TransferConvert.convert(request);
        PaymentResponseModel responseModel = transferService.receiveAndProcess(paymentRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(TransferConvert.convertToResponse(responseModel));

    }

    @GetMapping("/getTranscations")
    ResponseEntity<List<PaymentResponse>> getAllTransactions(@RequestParam(required = false) String status,
                                                             @RequestParam(required = false) String senderAccountNumber,
                                                             @RequestParam(required = false)  @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                             @RequestParam(required = false)  @DateTimeFormat(pattern = "yyyy-MM-dd")Date endDate){
        List<PaymentResponseModel> responseModelList = transferService.getAllTransactions(status, senderAccountNumber, startDate, endDate);
        return ResponseEntity.status(HttpStatus.OK).body(TransferConvert.convertToResponse(responseModelList));
//accoutnt id is supposed tobe senderAccountnumber
    }

    @GetMapping("/dailySummary")
    ResponseEntity<List<PaymentResponse>> getDailSummary( @RequestParam(required = false)
                                                          @DateTimeFormat(pattern = "yyyy-MM-dd") Date date){
        List<PaymentResponseModel> responseModelList = transferService.getDailySummary(date);
        return ResponseEntity.status(HttpStatus.OK).body(TransferConvert.convertToResponse(responseModelList));
    }
}
