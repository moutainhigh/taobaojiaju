package com.xinshan.service;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.pay.PayComponents;
import com.xinshan.model.Employee;
import com.xinshan.model.OrderPay;
import com.xinshan.model.extend.order.OrderReturnExtend;
import com.xinshan.utils.SplitUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mxt on 17-4-25.
 */
@Service
public class ReceiptService {
    @Autowired
    private OrderReturnService orderReturnService;

    @Transactional
    public void receipt(String postData, Employee employee) {
        OrderReturnExtend orderReturnExtend = JSONObject.parseObject(postData, OrderReturnExtend.class);
        Integer order_return_id = orderReturnExtend.getOrder_return_id();
        List<OrderPay> orderPays = orderReturnExtend.getOrderPays();
        orderReturnExtend = orderReturnService.getOrderReturnById(order_return_id);
        orderPays = PayComponents.createOrderPays(orderPays, employee, orderReturnExtend, PayComponents.pay_type_return_commodity);
        BigDecimal order_return_amount = orderReturnExtend.getOrder_return_amount();
        BigDecimal order_return_pay_amount = orderReturnExtend.getOrder_return_pay_amount();
        List<Integer> orderPayIds = SplitUtils.splitToList(orderReturnExtend.getOrder_pay_ids(), ",");
        for (int i = 0; i < orderPays.size(); i++) {
            OrderPay orderPay = orderPays.get(i);
            order_return_pay_amount = order_return_pay_amount.add(orderPay.getPay_amount());
            orderPayIds.add(orderPay.getOrder_pay_id());
        }
        orderReturnExtend.setOrder_pay_ids(SplitUtils.listToString(orderPayIds));
        orderReturnExtend.setOrder_return_pay_amount(order_return_pay_amount);
        orderReturnExtend.setOrder_return_need_amount(order_return_amount.subtract(order_return_pay_amount));
        if (orderReturnExtend.getOrder_return_amount().doubleValue() >= 0) {//收款
            if (orderReturnExtend.getOrder_return_pay_amount().doubleValue() < orderReturnExtend.getOrder_return_amount().doubleValue()) {
                orderReturnExtend.setOrder_return_pay_status(1);
            }else if(orderReturnExtend.getOrder_return_pay_amount().doubleValue() >= orderReturnExtend.getOrder_return_amount().doubleValue()){
                orderReturnExtend.setOrder_return_pay_status(2);
            }
        }else if (orderReturnExtend.getOrder_return_amount().doubleValue() < 0) {//退款
            if (orderReturnExtend.getOrder_return_pay_amount().abs().doubleValue() < orderReturnExtend.getOrder_return_amount().abs().doubleValue()) {
                orderReturnExtend.setOrder_return_pay_status(1);
            }else if(orderReturnExtend.getOrder_return_pay_amount().abs().doubleValue() >= orderReturnExtend.getOrder_return_amount().abs().doubleValue()){
                orderReturnExtend.setOrder_return_pay_status(2);
            }
        }
        orderReturnService.updateOrderReturn(orderReturnExtend);
    }

}
