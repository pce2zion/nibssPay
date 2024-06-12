package com.example.nibsstransfer.client.res;

import com.alibaba.fastjson.JSONObject;
import com.example.nibsstransfer.converter.TransferConvert;
import com.example.nibsstransfer.entity.TransactionEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Slf4j
public class TransferClientResponse {

    private String transactionId;

    private BigDecimal amount;

    private Date date;

    private SenderClientRes sender;

    private BeneficiaryClientRes beneficiary;

    private String status;


    public static TransferClientResponse build(String responseStr, TransactionEntity entity) {
        try {
           TransferClientResponse res =  JSONObject.parseObject(responseStr, TransferClientResponse.class);
           return TransferConvert.buildTransactionClientResponse(entity, res);
        } catch (Exception e) {
            log.error("parse from string exception response Str: {}",responseStr, e);
        }
        return null;
    }



}
