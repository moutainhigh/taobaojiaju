package com.xinshan.model.extend.user;

import com.xinshan.model.Position;
import com.xinshan.model.User;
import com.xinshan.model.UserDeliveryAddress;
import com.xinshan.model.UserOpenid;

/**
 * Created by mxt on 16-10-21.
 */
public class UserExtend extends User {
    private UserDeliveryAddress userDeliveryAddress;
    private UserOpenid userOpenid;
    private Integer orderNum;
    private String user_referrals_name;

    private Position position;

    public UserDeliveryAddress getUserDeliveryAddress() {
        return userDeliveryAddress;
    }

    public void setUserDeliveryAddress(UserDeliveryAddress userDeliveryAddress) {
        this.userDeliveryAddress = userDeliveryAddress;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getUser_referrals_name() {
        return user_referrals_name;
    }

    public void setUser_referrals_name(String user_referrals_name) {
        this.user_referrals_name = user_referrals_name;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public UserOpenid getUserOpenid() {
        return userOpenid;
    }

    public void setUserOpenid(UserOpenid userOpenid) {
        this.userOpenid = userOpenid;
    }
}
