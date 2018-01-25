package com.xinshan.service.sync;

import com.xinshan.components.pay.PayComponents;
import com.xinshan.dao.extend.order.OrderExtendMapper;
import com.xinshan.model.*;
import com.xinshan.model.extend.order.OrderCommodityExtend;
import com.xinshan.model.extend.order.OrderExtend;
import com.xinshan.service.PayService;
import com.xinshan.service.UserService;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.constant.order.OrderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mxt on 17-7-27.
 */
@Service
public class OrderSyncService {
    @Autowired
    private PayService payService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderExtendMapper orderExtendMapper;

    @Transactional
    public void syncCreateOrder(OrderExtend orderExtend) {
        userService.createUser(orderExtend.getCustomer_name(), orderExtend.getCustomer_phone_number(),
                orderExtend.getDelivery_address(), orderExtend.getEmployee_code(), orderExtend.getEmployee_name(),
                orderExtend.getPosition_id(), orderExtend.getProvince_zip(), orderExtend.getCity_zip(),
                orderExtend.getDistrict_zip(), orderExtend.getCustomer_second_phone());
        orderExtendMapper.createOrder(orderExtend);
        List<OrderCommodityExtend> orderCommodities = orderExtend.getOrderCommodities();
        for (int i = 0; i < orderCommodities.size(); i++) {
            OrderCommodityExtend orderCommodity = orderCommodities.get(i);
            orderExtendMapper.createOrderCommodity(orderCommodity);
        }
        List<OrderPay> orderPays = orderExtend.getOrderPays();
        if (orderPays != null && orderPays.size() > 0) {
            for (int i = 0; i < orderPays.size(); i++) {
                OrderPay orderPay = orderPays.get(i);
                payService.createOrderPay(orderPay);
            }
        }

        List<OrderCarryFee> orderCarryFees = orderExtend.getOrderCarryFees();
        if (orderCarryFees != null && orderCarryFees.size() > 0) {
            for (int i = 0; i < orderCarryFees.size(); i++) {
                OrderCarryFee orderCarryFee = orderCarryFees.get(i);
                orderExtendMapper.createCarryFee(orderCarryFee);
            }
        }
    }
}
