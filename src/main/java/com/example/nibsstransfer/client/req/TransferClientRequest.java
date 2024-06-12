package com.example.nibsstransfer.client.req;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Peace Obute
 * @since 09/06/2024
 */
@Data
public class TransferClientRequest {

    private String transactionId;

    private BigDecimal amount;

    private String senderAccountName;

    private String senderAccountNumber;

    private String beneficiaryAccountNumber;

    private String beneficiaryAccountName;

}
