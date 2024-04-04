package com.payment.paymentcodechallenge.model.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author Z.Zhang
 */
@Data
@Builder
public class CardInfoVO {
    private int cardId;
    private String cardNumber;
    private String cvv;
    private String cardType;
    private String expireMonth;
    private String expireYear;
    private String holderName;
    private String timeCreated;
}
