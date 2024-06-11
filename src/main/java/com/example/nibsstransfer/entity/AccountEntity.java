package com.example.nibsstransfer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Peace Obute
 * @since 06/06/2024
 */

@Data
@AllArgsConstructor
@Entity
@Table(name = "account_details_order")
public class AccountEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String accountId;

    private String accountNumber;

    private String accountName;

    private BigDecimal accountBalance;

    private String bvn;

    private String bankName;

    private String bankCode;


    public AccountEntity() {

    }
}
