package com.xinshan.model.extend.activity;

import com.xinshan.model.CashBackCommodity;
import com.xinshan.model.extend.order.OrderCommodityExtend;
import com.xinshan.model.extend.order.OrderReturnCommodityExtend;

/**
 * Created by mxt on 17-4-17.
 */
public class CashBackCommodityExtend extends CashBackCommodity {
    private OrderCommodityExtend orderCommodityExtend;
    private OrderReturnCommodityExtend orderReturnCommodityExtend;

    public OrderReturnCommodityExtend getOrderReturnCommodityExtend() {
        return orderReturnCommodityExtend;
    }

    public void setOrderReturnCommodityExtend(OrderReturnCommodityExtend orderReturnCommodityExtend) {
        this.orderReturnCommodityExtend = orderReturnCommodityExtend;
    }

    public OrderCommodityExtend getOrderCommodityExtend() {
        return orderCommodityExtend;
    }

    public void setOrderCommodityExtend(OrderCommodityExtend orderCommodityExtend) {
        this.orderCommodityExtend = orderCommodityExtend;
    }
}
