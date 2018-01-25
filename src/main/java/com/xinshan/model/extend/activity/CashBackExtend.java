package com.xinshan.model.extend.activity;

import com.xinshan.model.Activity;
import com.xinshan.model.CashBack;
import com.xinshan.model.Order;

import java.util.List;

/**
 * Created by mxt on 17-4-17.
 */
public class CashBackExtend extends CashBack {
    private Activity activity;
    private Order order;
    private List<CashBackCommodityExtend> cashBackCommodityExtends;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public List<CashBackCommodityExtend> getCashBackCommodityExtends() {
        return cashBackCommodityExtends;
    }

    public void setCashBackCommodityExtends(List<CashBackCommodityExtend> cashBackCommodityExtends) {
        this.cashBackCommodityExtends = cashBackCommodityExtends;
    }
}
