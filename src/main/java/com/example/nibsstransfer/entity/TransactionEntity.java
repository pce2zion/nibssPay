package com.example.nibsstransfer.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Peace Obute
 * @since 06/06/2024
 */
@Data
@Entity
@Table(name = "transaction_order")
public class TransactionEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String transactionReference;

    private BigDecimal amountToSend;

    private BigDecimal transactionFee;

    private BigDecimal billedAmount;

    private String senderAccountName;

    private String senderAccountNumber;

    private String senderBvn;

    private String SenderBankCode;

    private String beneficiaryAccountName;

    private String beneficiaryAccountNumber;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date gmtCreated;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date gmtModified;

    private String status;

    private Boolean isCommissionWorthy;

    private BigDecimal commission;

    private Boolean isTransactionProcessed;



}
