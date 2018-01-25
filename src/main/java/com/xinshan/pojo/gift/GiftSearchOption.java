package com.xinshan.pojo.gift;

import com.xinshan.pojo.SearchOption;

import java.util.List;

/**
 * Created by mxt on 17-4-15.
 */
public class GiftSearchOption extends SearchOption {
    private Integer order_id;
    private Integer gift_id;
    private Integer commodity_id;
    private Integer user_id;
    private Integer supplier_id;
    private List<Integer> giftIds;
    private Integer gift_return_status;
    private Integer gift_commodity_id;
    private Integer gift_return_commodity_id;
    private Integer gift_return_id;
    private Integer gift_enable;
    private Integer position_id;
    private Integer gift_type;
    private String employee_code;

    public Integer getGift_return_commodity_id() {
        return gift_return_commodity_id;
    }

    public void setGift_return_commodity_id(Integer gift_return_commodity_id) {
        this.gift_return_commodity_id = gift_return_commodity_id;
    }

    public String getEmployee_code() {
        return employee_code;
    }

    public void setEmployee_code(String employee_code) {
        this.employee_code = employee_code;
    }

    public Integer getGift_type() {
        return gift_type;
    }

    public void setGift_type(Integer gift_type) {
        this.gift_type = gift_type;
    }

    public Integer getPosition_id() {
        return position_id;
    }

    public void setPosition_id(Integer position_id) {
        this.position_id = position_id;
    }

    public Integer getGift_enable() {
        return gift_enable;
    }

    public void setGift_enable(Integer gift_enable) {
        this.gift_enable = gift_enable;
    }

    public Integer getGift_return_id() {
        return gift_return_id;
    }

    public void setGift_return_id(Integer gift_return_id) {
        this.gift_return_id = gift_return_id;
    }

    public Integer getGift_commodity_id() {
        return gift_commodity_id;
    }

    public void setGift_commodity_id(Integer gift_commodity_id) {
        this.gift_commodity_id = gift_commodity_id;
    }

    public Integer getGift_return_status() {
        return gift_return_status;
    }

    public void setGift_return_status(Integer gift_return_status) {
        this.gift_return_status = gift_return_status;
    }

    public List<Integer> getGiftIds() {
        return giftIds;
    }

    public void setGiftIds(List<Integer> giftIds) {
        this.giftIds = giftIds;
    }

    public Integer getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public Integer getGift_id() {
        return gift_id;
    }

    public void setGift_id(Integer gift_id) {
        this.gift_id = gift_id;
    }

    public Integer getCommodity_id() {
        return commodity_id;
    }

    public void setCommodity_id(Integer commodity_id) {
        this.commodity_id = commodity_id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
}
