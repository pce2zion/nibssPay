package com.example.nibsstransfer.client.res;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Data
@Slf4j
public class TransferClientResponse {

    private String id;

    private BigDecimal amount;

    private String date;

    private SenderClientRes sender;

    private BeneficiaryClientRes beneficiary;

    private String status;


    public static TransferClientResponse build(String responseStr) {
        try {
            return JSONObject.parseObject(responseStr, TransferClientResponse.class);
        } catch (Exception e) {
            log.error("pars from string exception response Str: {}",responseStr, e);
        }
        return null;
    }

}
