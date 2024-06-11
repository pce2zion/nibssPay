package com.example.nibsstransfer.dto.requestDto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Peace Obute
 * @since 06/06/2024
 */
@Data
public class PaymentRequestDto {

    private BigDecimal amount;

    private SenderRequestDto sender;

    private BeneficiaryRequestDto beneficiary;

    private OtherDetailsDto otherDetails;
}
