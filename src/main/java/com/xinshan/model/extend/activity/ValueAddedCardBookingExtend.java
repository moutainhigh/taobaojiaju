package com.xinshan.model.extend.activity;

import com.xinshan.model.*;

/**
 * Created by mxt on 17-4-13.
 */
public class ValueAddedCardBookingExtend extends ValueAddedCardBooking {
    private ValueAddedCard valueAddedCard;
    private User user;
    private Activity activity;
    private ActivityValueAdded activityValueAdded;
    private Order order;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public ValueAddedCard getValueAddedCard() {
        return valueAddedCard;
    }

    public void setValueAddedCard(ValueAddedCard valueAddedCard) {
        this.valueAddedCard = valueAddedCard;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public ActivityValueAdded getActivityValueAdded() {
        return activityValueAdded;
    }

    public void setActivityValueAdded(ActivityValueAdded activityValueAdded) {
        this.activityValueAdded = activityValueAdded;
    }
}
