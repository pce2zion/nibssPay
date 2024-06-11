package com.example.nibsstransfer.converter;

import com.example.nibsstransfer.dto.AccountNumberAndAccountNameDto;
import com.example.nibsstransfer.dto.requestDto.PaymentRequestDto;

public class AccountDetailsConvert {

    public static AccountNumberAndAccountNameDto convertToSenderAccountDto(PaymentRequestDto paymentRequestDto){

        AccountNumberAndAccountNameDto dto = new AccountNumberAndAccountNameDto();
        dto.setAccountName(paymentRequestDto.getSender().getSenderName());
        dto.setAccountNumber(paymentRequestDto.getSender().getSenderAccountNumber());

        return dto;
    }

    public static AccountNumberAndAccountNameDto convertToBeneficiaryAccountDto(PaymentRequestDto paymentRequestDto){

        AccountNumberAndAccountNameDto dto = new AccountNumberAndAccountNameDto();
        dto.setAccountName(paymentRequestDto.getBeneficiary().getBeneficiaryAccountName());
        dto.setAccountNumber(paymentRequestDto.getBeneficiary().getBeneficiaryAccountNumber());

        return dto;
    }

}
