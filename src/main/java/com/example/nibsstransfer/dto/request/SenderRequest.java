package com.example.nibsstransfer.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Peace Obute
 * @since 06/06/2024
 */
@Data
public class SenderRequest {

    @NotBlank(message = "sender name must not be blank")
    private String senderAccountName;

    @NotBlank(message = "sender accountNumber must not be blank")
    private String senderAccountNumber;

}
