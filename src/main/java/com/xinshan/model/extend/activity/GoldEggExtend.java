package com.xinshan.model.extend.activity;

import com.xinshan.model.Activity;
import com.xinshan.model.ActivityGoldEgg;
import com.xinshan.model.GoldEgg;
import com.xinshan.model.Order;

/**
 * Created by mxt on 17-4-18.
 */
public class GoldEggExtend extends GoldEgg {
    private Activity activity;
    private ActivityGoldEgg activityGoldEgg;
    private Order order;

    public ActivityGoldEgg getActivityGoldEgg() {
        return activityGoldEgg;
    }

    public void setActivityGoldEgg(ActivityGoldEgg activityGoldEgg) {
        this.activityGoldEgg = activityGoldEgg;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
