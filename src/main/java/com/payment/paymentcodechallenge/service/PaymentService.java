package com.payment.paymentcodechallenge.service;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.checkout.*;
import com.adyen.service.checkout.PaymentsApi;
import com.google.gson.Gson;
import com.payment.paymentcodechallenge.model.PaymentModel;
import com.payment.paymentcodechallenge.model.vo.CardInfoVO;
import com.payment.paymentcodechallenge.service.vo.PaymentParams;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author Z.Zhang
 */
@Service
public class PaymentService {

    private final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final String TEST_PREFIX = "test_";

    private final String RETURN_URL = "https://www.google.com";

    private final String MERCHANT_ACCOUNT = "HenriqueCerqueiraLdaECOM";

    private final String API_KEY = "AQEvhmfuXNWTK0Qc+iSYl2oqrvWOTahIH4JfXWxEx1Svn8hraJ64gQ5iXQug5TyeDo0QwV1bDb7kfNy1WIxIIkxgBw==-G4N6q9q+auj9gXvSRNVxGYijOk35fUvEtaRMl0RWnPQ=-;t{@Vg6@,T.D@A^5";

    List<PaymentResponse.ResultCodeEnum> FINAL_CODE_LIST = Arrays.asList(PaymentResponse.ResultCodeEnum.AUTHORISED,
            PaymentResponse.ResultCodeEnum.CANCELLED, PaymentResponse.ResultCodeEnum.ERROR, PaymentResponse.ResultCodeEnum.REFUSED);

    @Resource
    private PaymentModel paymentModel;


    /**
     * getCardsByWalletId
     * @param walletId id in wallet table
     * @return list of card infos
     */
    public List<CardInfoVO> getCardsByWalletId(Integer walletId) {
    if (walletId == null || walletId == 0) {
        return new ArrayList<>();
    }
    return paymentModel.getCardByWallet(walletId);
}

    /**
     *
     * @param currency can be null
     * @param value payment amount can be null
     * @param countryCode can be null
     * @param shopperLocale can be null
     * @return payment methods details
     */
    public PaymentMethodsResponse getPaymentMethod(String currency, Long value, String countryCode, String shopperLocale) {
        Client client = new Client(API_KEY, Environment.TEST);
        PaymentsApi checkout = new PaymentsApi(client);
        PaymentMethodsRequest paymentMethodsRequest = new PaymentMethodsRequest();
        paymentMethodsRequest.setMerchantAccount(MERCHANT_ACCOUNT);
        paymentMethodsRequest.setCountryCode(countryCode);
        paymentMethodsRequest.setShopperLocale(shopperLocale);
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setValue(value);
        paymentMethodsRequest.setAmount(amount);
        paymentMethodsRequest.setChannel(PaymentMethodsRequest.ChannelEnum.WEB);

        PaymentMethodsResponse response;
        try {
            response = checkout.paymentMethods(paymentMethodsRequest);
        } catch (Exception e) {
            log.error("send getPaymentMethod request failed, e:", e);
            throw new RuntimeException("get payment method failed! please try again later!");
        }
        return response;
    }

    /**
     *
     * @param currency payment currency
     * @param value payment amount
     * @param cardId payment card id in DB
     * @return payment result
     */
    public String processPayment(String currency, Long value, Integer cardId) {
        // Set API key
        Client client = new Client(API_KEY, Environment.TEST);
        PaymentsApi checkout = new PaymentsApi(client);

        PaymentRequest paymentRequest = new PaymentRequest();

        paymentRequest.setMerchantAccount(MERCHANT_ACCOUNT);

        CardInfoVO cardInfoVO = paymentModel.getCardByCardId(cardId);
        validateCardInfo(cardInfoVO);
        PaymentParams paymentParams = PaymentParams.fromCardInfo(cardInfoVO);

        // generate json string for paymentParams
        Gson gson = new Gson();
        String jsonString = gson.toJson(paymentParams);

        System.out.println(jsonString);
        // Deserialize the payment method from PaymentParams.
        CheckoutPaymentMethod checkoutPaymentMethod;
        try {
            checkoutPaymentMethod = CheckoutPaymentMethod.fromJson(jsonString);
        } catch (Exception e) {
            log.error("convert payment params failed, e:", e);
            throw new RuntimeException("card validate failed! please try again later!");
        }
        paymentRequest.setPaymentMethod(checkoutPaymentMethod);

        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setValue(value);

        // initial payment record
        int paymentId = paymentModel.createPayment(cardId, value, currency);

        paymentRequest.setAmount(amount);
        paymentRequest.setReference(TEST_PREFIX + paymentId);
        paymentRequest.setReturnUrl(RETURN_URL);

        PaymentResponse response;
        try {
            response = checkout.payments(paymentRequest);
        } catch (Exception e) {
            log.error("send payment request failed, e:", e);
            paymentModel.updatePaymentStatus(paymentId, statusCodeFromResultCode(PaymentResponse.ResultCodeEnum.CANCELLED));
            throw new RuntimeException("payment failed! please try again later!");
        }

        if (FINAL_CODE_LIST.contains(response.getResultCode())) {
            // if meet final stage, update status
            paymentModel.updatePaymentStatus(paymentId, statusCodeFromResultCode(response.getResultCode()));
        }

        return response.getResultCode().getValue();
    }

    private void validateCardInfo(CardInfoVO cardInfoVO) {
        if (cardInfoVO == null) {
            throw new IllegalArgumentException("cannot find this card!");
        }
        String cardNumber = cardInfoVO.getCardNumber();
        if (cardNumber.isBlank() || cardNumber.length() > 16 || cardNumber.length() < 13) {
            throw new IllegalArgumentException("Invalid card number!");
        }
        LocalDate currentDate = LocalDate.now();
        if (Integer.parseInt(cardInfoVO.getExpireYear()) < currentDate.getYear() ||
                (Integer.parseInt(cardInfoVO.getExpireYear()) == currentDate.getYear() && Integer.parseInt(cardInfoVO.getExpireMonth()) < currentDate.getMonthValue() )) {
            throw new IllegalArgumentException("card expired!");
        }
    }

    /**
     * switch enum to code to store in DB
     * @param resultCode result enum
     * @return convert to int
     */
    private int statusCodeFromResultCode(PaymentResponse.ResultCodeEnum resultCode) {
        return switch (resultCode) {
            // success
            case AUTHORISED -> 0;
            case REFUSED -> 1;
            default -> 2;
        };
    }
}
