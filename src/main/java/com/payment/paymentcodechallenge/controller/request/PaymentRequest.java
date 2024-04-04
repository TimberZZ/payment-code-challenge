package com.payment.paymentcodechallenge.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Z.Zhang
 */
@Data
public class PaymentRequest {
    @JsonProperty(required = true)
    private String currency;
    @JsonProperty(required = true)
    private Long amount;
    @JsonProperty(required = true)
    private Integer cardId;
}
