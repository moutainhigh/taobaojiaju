package com.xinshan.pojo.specialRightSell;

import com.xinshan.pojo.SearchOption;

/**
 * Created by mxt on 17-4-20.
 */
public class SpecialRightSellSearchOption extends SearchOption {
    private Integer order_id;
    private Integer special_right_sell_id;
    private Integer commodity_id;

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public Integer getSpecial_right_sell_id() {
        return special_right_sell_id;
    }

    public void setSpecial_right_sell_id(Integer special_right_sell_id) {
        this.special_right_sell_id = special_right_sell_id;
    }

    public Integer getCommodity_id() {
        return commodity_id;
    }

    public void setCommodity_id(Integer commodity_id) {
        this.commodity_id = commodity_id;
    }
}
