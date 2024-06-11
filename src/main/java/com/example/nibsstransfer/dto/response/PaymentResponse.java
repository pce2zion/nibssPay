package com.example.nibsstransfer.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Peace Obute
 * @since 06/06/2024
 */
@Data
public class PaymentResponse {

    private String transactionReference;

    private BigDecimal amount;

    private BigDecimal transactionFee;

    private BigDecimal billedAmount;

    private String senderAccountName;

    private String senderAccountNumber;

    private String senderBvn;

    private String senderBankCode;

    private String beneficiaryAccountName;

    private String beneficiaryAccountNumber;

    private String beneficiaryBvn;

    private String beneficiaryBankCode;

    private Date gmtCreated;

    private Date gmtModified;

    private String status;
}
