package com.payment.paymentcodechallenge.service;

import com.payment.paymentcodechallenge.model.vo.CardInfoVO;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PaymentServiceTest {

    @Resource
    private PaymentService paymentService;

    @Test
    void validCardTest() {
        CardInfoVO cardInfoVO = CardInfoVO.builder()
                .cardNumber("4000000000000000")
                .expireYear("2030")
                .expireMonth("05")
                .build();
        Assertions.assertDoesNotThrow(() -> paymentService.validateCardInfo(cardInfoVO));
    }

    @Test
    void invalidCardNumberTest() {
        CardInfoVO cardInfoVO = CardInfoVO.builder()
                .cardNumber("400000000000000000")
                .expireYear("2030")
                .expireMonth("05")
                .build();
        Assertions.assertThrows(IllegalArgumentException.class, () -> paymentService.validateCardInfo(cardInfoVO));
    }

    @Test
    void invalidCardExpireMonthTest() {
        CardInfoVO cardInfoVO = CardInfoVO.builder()
                .cardNumber("4000000000000000")
                .expireYear("2024")
                .expireMonth("01")
                .build();
        Assertions.assertThrows(IllegalArgumentException.class, () -> paymentService.validateCardInfo(cardInfoVO));
    }

    @Test
    void invalidCardExpireYearTest() {
        CardInfoVO cardInfoVO = CardInfoVO.builder()
                .cardNumber("4000000000000000")
                .expireYear("2020")
                .expireMonth("05")
                .build();
        Assertions.assertThrows(IllegalArgumentException.class, () -> paymentService.validateCardInfo(cardInfoVO));
    }

}