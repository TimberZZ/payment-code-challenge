package com.payment.paymentcodechallenge.model;

import com.payment.paymentcodechallenge.model.vo.CardInfoVO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Z.Zhang
 */
@Component
public class PaymentModel {

    private final Logger log = LoggerFactory.getLogger(PaymentModel.class);

    @Resource
    private ConnectDB connectDB;

    /**
     *
     * @param walletId id in wallet table
     * @return list of card infos
     */
    public List<CardInfoVO> getCardByWallet(int walletId) {
        String sql = "SELECT * FROM card WHERE card.wallet_id = ?";

        try (Connection conn = connectDB.connect(); PreparedStatement preState = conn.prepareStatement(sql)) {

            // set the value
            preState.setInt(1, walletId);
            //
            ResultSet rs = preState.executeQuery();

            List<CardInfoVO> cardInfoVOList = new ArrayList<>();
            // loop through the result set
            while (rs.next()) {
                CardInfoVO cardInfoVO = CardInfoVO.builder()
                        .cardId(rs.getInt("id"))
                        .cardNumber(rs.getString("card_number"))
                        .cardType(rs.getString("card_type"))
                        .expireMonth(rs.getString("expire_month"))
                        .expireYear(rs.getString("expire_year"))
                        .holderName(rs.getString("holder_name"))
                        .timeCreated(rs.getString("time_created"))
                        .cvv(rs.getString("cvv"))
                        .build();
                cardInfoVOList.add(cardInfoVO);
            }
            return cardInfoVOList;
        } catch (SQLException e) {
            log.error("getCardByWallet error, e:", e);
        }
        return new ArrayList<>();
    }

    /**
     *
     * @param cardId card id in table
     * @param amount payment amount
     * @param currency payment currency
     * @return payment id as ref number
     */
    public int createPayment(int cardId, Long amount, String currency) {
        String sql = "INSERT INTO payment(card_id, amount, currency, time_created) VALUES(?, ?, ?, ?)";
        String rowIdSql = "select last_insert_rowid()";
        int rowId = 0;

        try (Connection conn = connectDB.connect();
             PreparedStatement preState = conn.prepareStatement(sql)) {
            // set the value
            preState.setInt(1, cardId);
            preState.setInt(2, amount.intValue());
            preState.setString(3, currency);
            preState.setString(4, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
            preState.executeUpdate();

            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(rowIdSql);
            while (rs.next()) {
                rowId = rs.getInt("last_insert_rowid()");
            }
        } catch (SQLException e) {
            log.error("createPayment error, e:", e);
        }
        return rowId;
    }

    /**
     *
     * @param id payment id
     * @param status final status after process payment
     */
    public void updatePaymentStatus(int id, int status) {
        String sql = "UPDATE payment SET status = ? , "
                + "time_finalised = ?"
                + "WHERE id = ?";

        try (Connection conn = connectDB.connect();
             PreparedStatement preState = conn.prepareStatement(sql)) {

            // set the param
            preState.setInt(1, status);
            preState.setString(2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
            preState.setInt(3, id);
            // update
            preState.executeUpdate();
        } catch (SQLException e) {
            log.error("updatePaymentStatus error, e:", e);
        }
    }

    /**
     *
     * @param cardId id in card table
     * @return card info
     */
    public CardInfoVO getCardByCardId(Integer cardId) {
        String sql = "SELECT * FROM card WHERE card.id = ?";

        try (Connection conn = connectDB.connect(); PreparedStatement preState = conn.prepareStatement(sql)) {

            // set the value
            preState.setInt(1, cardId);
            //
            ResultSet rs = preState.executeQuery();

            CardInfoVO cardInfoVO = null;
            // loop through the result set
            while (rs.next()) {
                cardInfoVO = CardInfoVO.builder()
                        .cardId(rs.getInt("id"))
                        .cardNumber(rs.getString("card_number"))
                        .cardType(rs.getString("card_type"))
                        .expireMonth(rs.getString("expire_month"))
                        .expireYear(rs.getString("expire_year"))
                        .holderName(rs.getString("holder_name"))
                        .timeCreated(rs.getString("time_created"))
                        .cvv(rs.getString("cvv"))
                        .build();
            }
            return cardInfoVO;
        } catch (SQLException e) {
            log.error("getCardByCardId error, e:", e);
        }
        return null;
    }
}
