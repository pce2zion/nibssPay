package com.example.nibsstransfer.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Peace Obute
 * @since 06/06/2024
 */
@Data
public class PaymentRequest {

    @NotNull(message = "amount should not be null")
    private BigDecimal amount;

    @NotNull(message = "sender details should not be null")
    private SenderRequest sender;

    @NotNull(message = "beneficiary should not be null")
    private BeneficiaryRequest beneficiary;

    @NotNull(message = "other details should not be null")
    private OtherDetailsRequest otherDetailsRequest;
}
