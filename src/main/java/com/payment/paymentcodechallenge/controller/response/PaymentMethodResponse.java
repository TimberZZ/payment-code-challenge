package com.payment.paymentcodechallenge.controller.response;

import com.adyen.model.checkout.PaymentMethod;
import com.adyen.model.checkout.StoredPaymentMethod;
import lombok.Data;

import java.util.List;

/**
 * @author Z.Zhang
 */
@Data
public class PaymentMethodResponse {
    private List<PaymentMethod> paymentMethods;
    private List<StoredPaymentMethod> storedPaymentMethods;
}
