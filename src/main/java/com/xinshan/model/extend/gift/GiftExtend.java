package com.xinshan.model.extend.gift;

import com.xinshan.model.*;

import java.util.List;

/**
 * Created by mxt on 17-4-15.
 */
public class GiftExtend extends Gift {
    private User user;
    private Supplier supplier;
    private Order order;
    private List<GiftCommodityExtend> giftCommodities;
    private Activity activity;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<GiftCommodityExtend> getGiftCommodities() {
        return giftCommodities;
    }

    public void setGiftCommodities(List<GiftCommodityExtend> giftCommodities) {
        this.giftCommodities = giftCommodities;
    }
}
