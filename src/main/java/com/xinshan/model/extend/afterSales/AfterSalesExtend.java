package com.xinshan.model.extend.afterSales;

import com.xinshan.model.AfterSales;
import com.xinshan.model.Order;

import java.util.List;

/**
 * Created by mxt on 16-12-15.
 */
public class AfterSalesExtend extends AfterSales {
    private Order order;
    private List<AfterSalesCommodityExtend> afterSalesCommodities;

    public List<AfterSalesCommodityExtend> getAfterSalesCommodities() {
        return afterSalesCommodities;
    }

    public void setAfterSalesCommodities(List<AfterSalesCommodityExtend> afterSalesCommodities) {
        this.afterSalesCommodities = afterSalesCommodities;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

}
