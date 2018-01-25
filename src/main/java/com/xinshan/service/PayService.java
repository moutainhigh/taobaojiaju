package com.xinshan.service;

import com.xinshan.dao.OrderPayMapper;
import com.xinshan.dao.OrderPayReturnMapper;
import com.xinshan.dao.extend.pay.PayExtendMapper;
import com.xinshan.model.OrderPay;
import com.xinshan.model.OrderPayReturn;
import com.xinshan.pojo.pay.PaySearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by mxt on 17-4-13.
 */
@Service
public class PayService {
    @Autowired
    private PayExtendMapper payExtendMapper;
    @Autowired
    private OrderPayMapper orderPayMapper;
    @Autowired
    private OrderPayReturnMapper orderPayReturnMapper;

    public void createPayReturn(OrderPayReturn orderPayReturn) {
        payExtendMapper.createPayReturn(orderPayReturn);
    }

    public void createOrderPay(OrderPay orderPay) {
        payExtendMapper.createOrderPay(orderPay);
    }

    public void deleteOrderPayById(int order_pay_id) {
        orderPayMapper.deleteByPrimaryKey(order_pay_id);
    }

    public List<OrderPay> orderPays(PaySearchOption paySearchOption) {
        return payExtendMapper.orderPays(paySearchOption);
    }

    public List<OrderPayReturn> orderPayReturns(PaySearchOption paySearchOption) {
        return payExtendMapper.orderPayReturns(paySearchOption);
    }


}
