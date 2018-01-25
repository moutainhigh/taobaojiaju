package com.xinshan.dao.extend.pay;

import com.xinshan.model.OrderPay;
import com.xinshan.model.OrderPayReturn;
import com.xinshan.pojo.pay.PaySearchOption;

import java.util.List;

/**
 * Created by mxt on 17-4-13.
 */
public interface PayExtendMapper {

    void createPayReturn(OrderPayReturn orderPayReturn);

    void createOrderPay(OrderPay orderPay);

    List<OrderPay> orderPays(PaySearchOption paySearchOption);

    List<OrderPayReturn> orderPayReturns(PaySearchOption paySearchOption);
}
