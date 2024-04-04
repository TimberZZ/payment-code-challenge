package com.payment.paymentcodechallenge.controller.request;

import lombok.Data;

/**
 * @author Z.Zhang
 */
@Data
public class PaymentMethodRequest {
    private String currency;
    private Long amount;
    private String countryCode;
    private String shopperLocale;
}
