package com.xinshan.model.extend.user;

import com.xinshan.model.Order;
import com.xinshan.model.User;
import com.xinshan.model.UserBringUp;
import com.xinshan.model.UserBringUpCashBack;
import com.xinshan.utils.DateUtil;

/**
 * Created by mxt on 17-6-9.
 */
public class UserBringUpExtend extends UserBringUp{
    private User oldUser;
    private User newUser;
    private Order order;
    private Integer inventoryOutDay;
    private UserBringUpCashBack bringUpCashBack;

    public UserBringUpCashBack getBringUpCashBack() {
        return bringUpCashBack;
    }

    public void setBringUpCashBack(UserBringUpCashBack bringUpCashBack) {
        this.bringUpCashBack = bringUpCashBack;
    }

    public Integer getInventoryOutDay() {
        if (getOrder_complete_date() == null) {
            setInventoryOutDay(null);
        }else {
            setInventoryOutDay(DateUtil.getDayBetween(getOrder_complete_date(), DateUtil.currentDate()));
        }
        return inventoryOutDay;
    }

    public void setInventoryOutDay(Integer inventoryOutDay) {
        this.inventoryOutDay = inventoryOutDay;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public User getOldUser() {
        return oldUser;
    }

    public void setOldUser(User oldUser) {
        this.oldUser = oldUser;
    }

    public User getNewUser() {
        return newUser;
    }

    public void setNewUser(User newUser) {
        this.newUser = newUser;
    }
}
