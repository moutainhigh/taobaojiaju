package com.xinshan.model.extend.order;

import com.xinshan.model.*;
import com.xinshan.model.extend.orderFee.OrderFeeExtend;

import java.util.List;

/**
 * Created by mxt on 17-3-24.
 */
public class OrderReturnExtend extends OrderReturn {
    private Order order;
    private List<OrderReturnCommodityExtend> orderReturnCommodities;
    private List<OrderPay> orderPays;
    private List<OrderCarryFee> orderCarryFees;
    private List<OrderPayReturn> orderPayReturns;
    private List<OrderFeeExtend> orderFees;

    public List<OrderFeeExtend> getOrderFees() {
        return orderFees;
    }

    public void setOrderFees(List<OrderFeeExtend> orderFees) {
        this.orderFees = orderFees;
    }

    public List<OrderPayReturn> getOrderPayReturns() {
        return orderPayReturns;
    }

    public void setOrderPayReturns(List<OrderPayReturn> orderPayReturns) {
        this.orderPayReturns = orderPayReturns;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderCarryFee> getOrderCarryFees() {
        return orderCarryFees;
    }

    public void setOrderCarryFees(List<OrderCarryFee> orderCarryFees) {
        this.orderCarryFees = orderCarryFees;
    }

    public List<OrderPay> getOrderPays() {
        return orderPays;
    }

    public void setOrderPays(List<OrderPay> orderPays) {
        this.orderPays = orderPays;
    }

    public List<OrderReturnCommodityExtend> getOrderReturnCommodities() {
        return orderReturnCommodities;
    }

    public void setOrderReturnCommodities(List<OrderReturnCommodityExtend> orderReturnCommodities) {
        this.orderReturnCommodities = orderReturnCommodities;
    }


}
