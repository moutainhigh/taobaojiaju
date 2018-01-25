package com.xinshan.dao.extend.payment;

import com.xinshan.model.Payment;
import com.xinshan.model.extend.payment.PaymentExtend;
import com.xinshan.pojo.payment.PaymentSearchOption;

import java.util.List;

/**
 * Created by mxt on 17-3-8.
 */
public interface PaymentExtendMapper {

    void createPayment(Payment payment);

    String maxPaymentCode(String dateStr);

    List<PaymentExtend> paymentList(PaymentSearchOption paymentSearchOption);

    Integer countPayment(PaymentSearchOption paymentSearchOption);
}
