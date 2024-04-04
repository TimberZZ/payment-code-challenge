package com.payment.paymentcodechallenge.service.vo;

import com.payment.paymentcodechallenge.model.vo.CardInfoVO;
import lombok.Builder;
import lombok.Data;

/**
 * @author Z.Zhang
 */
@Data
@Builder
public class PaymentParams {
    private String type;
    private String encryptedCardNumber;
    private String encryptedSecurityCode;
    private String encryptedExpiryMonth;
    private String encryptedExpiryYear;
    private String holderName;

    public static PaymentParams fromCardInfo(CardInfoVO cardInfoVO) {
        String prefix = "test_";
        return PaymentParams.builder()
                .type(cardInfoVO.getCardType())
                .encryptedCardNumber(prefix + cardInfoVO.getCardNumber())
                .encryptedSecurityCode(prefix + cardInfoVO.getCvv())
                .encryptedExpiryMonth(prefix + cardInfoVO.getExpireMonth())
                .encryptedExpiryYear(prefix + cardInfoVO.getExpireYear())
                .holderName(cardInfoVO.getHolderName())
                .build();
    }
}
