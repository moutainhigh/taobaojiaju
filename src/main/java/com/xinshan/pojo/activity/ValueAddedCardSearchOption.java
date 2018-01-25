package com.xinshan.pojo.activity;

import com.xinshan.pojo.SearchOption;

/**
 * Created by mxt on 17-4-13.
 */
public class ValueAddedCardSearchOption extends SearchOption {
    private Integer value_added_card_booking_id;
    private Integer activity_id;
    private Integer user_id;
    private String value_added_card_code;
    private String user_phone;
    private Integer card_enable;
    private Integer card_return_status;
    private Integer gift_status;
    private Integer return_gift_status;
    private Integer order_id;
    private Integer booking_status;

    public Integer getBooking_status() {
        return booking_status;
    }

    public void setBooking_status(Integer booking_status) {
        this.booking_status = booking_status;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public Integer getCard_return_status() {
        return card_return_status;
    }

    public void setCard_return_status(Integer card_return_status) {
        this.card_return_status = card_return_status;
    }

    public Integer getGift_status() {
        return gift_status;
    }

    public void setGift_status(Integer gift_status) {
        this.gift_status = gift_status;
    }

    public Integer getReturn_gift_status() {
        return return_gift_status;
    }

    public void setReturn_gift_status(Integer return_gift_status) {
        this.return_gift_status = return_gift_status;
    }

    public Integer getCard_enable() {
        return card_enable;
    }

    public void setCard_enable(Integer card_enable) {
        this.card_enable = card_enable;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getValue_added_card_code() {
        return value_added_card_code;
    }

    public void setValue_added_card_code(String value_added_card_code) {
        this.value_added_card_code = value_added_card_code;
    }

    public Integer getValue_added_card_booking_id() {
        return value_added_card_booking_id;
    }

    public void setValue_added_card_booking_id(Integer value_added_card_booking_id) {
        this.value_added_card_booking_id = value_added_card_booking_id;
    }

    public Integer getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(Integer activity_id) {
        this.activity_id = activity_id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
}
