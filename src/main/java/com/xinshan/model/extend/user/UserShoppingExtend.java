package com.xinshan.model.extend.user;

import com.xinshan.model.User;
import com.xinshan.model.UserShopping;

import java.util.List;

/**
 * Created by mxt on 17-7-31.
 */
public class UserShoppingExtend extends UserShopping{
    private User user;
    private List<UserShoppingCommodityExtend> userShoppingCommodities;
    private String commodityIds;

    public String getCommodityIds() {
        return commodityIds;
    }

    public void setCommodityIds(String commodityIds) {
        this.commodityIds = commodityIds;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<UserShoppingCommodityExtend> getUserShoppingCommodities() {
        return userShoppingCommodities;
    }

    public void setUserShoppingCommodities(List<UserShoppingCommodityExtend> userShoppingCommodities) {
        this.userShoppingCommodities = userShoppingCommodities;
    }
}
