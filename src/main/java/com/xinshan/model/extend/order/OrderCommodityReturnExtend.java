package com.xinshan.model.extend.order;

import com.xinshan.model.Order;
import com.xinshan.model.OrderCommodityReturn;

import java.util.List;

/**
 * Created by mxt on 16-11-18.
 */
public class OrderCommodityReturnExtend extends OrderCommodityReturn {
    private List<OrderCommodityExtend> orderCommodities;
    private Order order;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderCommodityExtend> getOrderCommodities() {
        return orderCommodities;
    }

    public void setOrderCommodities(List<OrderCommodityExtend> orderCommodities) {
        this.orderCommodities = orderCommodities;
    }
}
