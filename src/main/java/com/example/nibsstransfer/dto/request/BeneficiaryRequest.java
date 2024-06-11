package com.example.nibsstransfer.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Peace Obute
 * @since 06/06/2024
 */
@Data
public class BeneficiaryRequest {

    @NotBlank(message = "beneficiary account name should not be null")
    private String beneficiaryAccountName;

    @NotBlank(message = "beneficiary account number should not be null")
    private String beneficiaryAccountNumber;

}
