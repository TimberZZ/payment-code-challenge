package com.payment.paymentcodechallenge.controller.response;

import com.payment.paymentcodechallenge.model.vo.CardInfoVO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author Z.Zhang
 */
@Data
@AllArgsConstructor
public class GetCardsResponse {
    private List<CardInfoVO> cards;
}
