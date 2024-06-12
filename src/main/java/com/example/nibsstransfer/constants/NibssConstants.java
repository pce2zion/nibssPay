package com.example.nibsstransfer.constants;

import java.math.BigDecimal;

/**
 * @author Peace Obute
 * @since 09/06/2024
 */
public class NibssConstants {

    public static final String DEMO_POSTMAN_MOCK_SERVER_URL = "https://ea388149-c762-42f2-94be-7ea764de8b7e.mock.pstmn.io/doTransfer";
    public static final BigDecimal TXN_FEE_PERCENTAGE = new BigDecimal("0.005");
    public static final BigDecimal FEE_CAP = new BigDecimal("100");

    public static final BigDecimal COMMISSION_PERCENTAGE = new BigDecimal("0.2");

    public static final String SUCCESS = "SUCCESS";

    public static final String FAILED = "FAILED";
    public static final String PENDING = "PENDING";

    public static final String ACCOUNT_NOT_FOUND = "ACCOUNT NUMBER NOT FOUND";

    public static final String INSUFFICIENT_FUNDS = "INSUFFICIENT FUNDS";

    public static final String CONTENT_TYPE = "Content-Type";

    public static final String APPLICATION_JSON = "application/json";

    public static final String BENEFICIARY_BVN = "8678289737";

    public static final String BENEFICIARY_BANK_CODE = "503";

    public static final String BENEFICIARY_BANK = "Sun Bank";
}
