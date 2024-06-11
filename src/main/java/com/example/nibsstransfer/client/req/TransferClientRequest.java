package com.example.nibsstransfer.client.req;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferClientRequest {

    private String transactionId;

    private BigDecimal amount;

    private String senderAccountName;

    private String senderAccountNumber;

    private String beneficiaryAccountNumber;

    private String beneficiaryAccountName;

}
