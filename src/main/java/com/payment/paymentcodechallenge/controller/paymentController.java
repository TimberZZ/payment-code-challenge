package com.payment.paymentcodechallenge.controller;

import com.adyen.model.checkout.PaymentMethodsResponse;
import com.payment.paymentcodechallenge.controller.request.GetCardsRequest;
import com.payment.paymentcodechallenge.controller.request.PaymentMethodRequest;
import com.payment.paymentcodechallenge.controller.request.PaymentRequest;
import com.payment.paymentcodechallenge.controller.response.GetCardsResponse;
import com.payment.paymentcodechallenge.controller.response.PaymentMethodResponse;
import com.payment.paymentcodechallenge.controller.response.PaymentResponse;
import com.payment.paymentcodechallenge.service.PaymentService;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;


/**
 * @author Z.Zhang
 */
@RestController
@RequestMapping("/api")
public class paymentController {

    @Resource
    private PaymentService paymentService;

    @GetMapping("/home")
    public String home() {
        return "Welcome! The application has already started!";
    }

    /**
     *
     * @param getCardsRequest request params include walletId
     * @return list of cards info
     */
    @GetMapping("/getCards")
    public GetCardsResponse getCards(@RequestBody GetCardsRequest getCardsRequest) {
        return new GetCardsResponse(paymentService.getCardsByWalletId(getCardsRequest.getWalletId()));
    }

    /**
     *
     * @param paymentMethodRequest request params include required field
     * @return list of payment methods
     */
    @PostMapping("/paymentMethod")
    @SneakyThrows
    public PaymentMethodResponse paymentMethod(@RequestBody PaymentMethodRequest paymentMethodRequest) {
        PaymentMethodsResponse paymentMethodsResponse = paymentService.getPaymentMethod(paymentMethodRequest.getCurrency(),
                paymentMethodRequest.getAmount(), paymentMethodRequest.getCountryCode(), paymentMethodRequest.getShopperLocale());
        PaymentMethodResponse response = new PaymentMethodResponse();
        response.setPaymentMethods(paymentMethodsResponse.getPaymentMethods());
        response.setStoredPaymentMethods(paymentMethodsResponse.getStoredPaymentMethods());
        return response;
    }

    /**
     *
     * @param paymentRequest request params include required field
     * @return payment result
     * result:
     * Authorised - The payment was successfully authorised.
     * Cancelled - The payment was cancelled (by either the shopper or your own system) before processing was completed.
     * Error - 	There was an error when the payment was being processed.
     * Refused - The payment was refused.
     */
    @PostMapping("/payment")
    public PaymentResponse payment(@RequestBody PaymentRequest paymentRequest) {
        String result =  paymentService.processPayment(paymentRequest.getCurrency(), paymentRequest.getAmount(),
                paymentRequest.getCardId());
        return new PaymentResponse(result);
    }
}
