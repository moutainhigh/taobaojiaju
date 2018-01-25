package com.xinshan.model.extend.activity;

import com.xinshan.model.Activity;
import com.xinshan.model.BrandBoost;
import com.xinshan.model.User;

/**
 * Created by mxt on 17-4-19.
 */
public class BrandBoostExtend extends BrandBoost {
    private User user;
    private Activity activity;

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
}
