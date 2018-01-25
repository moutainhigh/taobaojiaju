package com.xinshan.components.websocket;

import com.xinshan.model.extend.order.OrderExtend;
import com.xinshan.service.OrderService;
import com.xinshan.service.sync.OrderSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by mxt on 17-7-27.
 */
@Component
public class OrderSyncComponent {

    private static OrderService orderService;
    private static OrderSyncService orderSyncService;
    @Autowired
    public void setOrderService(OrderService orderService) {
        OrderSyncComponent.orderService = orderService;
    }
    @Autowired
    public void setOrderSyncService(OrderSyncService orderSyncService) {
        OrderSyncComponent.orderSyncService = orderSyncService;
    }

    public static OrderExtend syncCreateOrder(int order_id) {
        OrderExtend orderExtend = orderService.getOrderById(order_id);
        return orderExtend;
    }

    public static void syncCreateOrder(OrderExtend orderExtend) {
        orderSyncService.syncCreateOrder(orderExtend);
    }

}
