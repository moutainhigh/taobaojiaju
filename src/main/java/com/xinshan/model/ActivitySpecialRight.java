package com.xinshan.model;

import java.math.BigDecimal;

public class ActivitySpecialRight {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column activity_special_right.activity_special_right_id
     *
     * @mbggenerated
     */
    private Integer activity_special_right_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column activity_special_right.activity_id
     *
     * @mbggenerated
     */
    private Integer activity_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column activity_special_right.order_amount
     *
     * @mbggenerated
     */
    private BigDecimal order_amount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column activity_special_right.special_right_commodity_ids
     *
     * @mbggenerated
     */
    private String special_right_commodity_ids;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column activity_special_right.special_right_remark
     *
     * @mbggenerated
     */
    private String special_right_remark;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column activity_special_right.activity_special_right_id
     *
     * @return the value of activity_special_right.activity_special_right_id
     *
     * @mbggenerated
     */
    public Integer getActivity_special_right_id() {
        return activity_special_right_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column activity_special_right.activity_special_right_id
     *
     * @param activity_special_right_id the value for activity_special_right.activity_special_right_id
     *
     * @mbggenerated
     */
    public void setActivity_special_right_id(Integer activity_special_right_id) {
        this.activity_special_right_id = activity_special_right_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column activity_special_right.activity_id
     *
     * @return the value of activity_special_right.activity_id
     *
     * @mbggenerated
     */
    public Integer getActivity_id() {
        return activity_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column activity_special_right.activity_id
     *
     * @param activity_id the value for activity_special_right.activity_id
     *
     * @mbggenerated
     */
    public void setActivity_id(Integer activity_id) {
        this.activity_id = activity_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column activity_special_right.order_amount
     *
     * @return the value of activity_special_right.order_amount
     *
     * @mbggenerated
     */
    public BigDecimal getOrder_amount() {
        return order_amount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column activity_special_right.order_amount
     *
     * @param order_amount the value for activity_special_right.order_amount
     *
     * @mbggenerated
     */
    public void setOrder_amount(BigDecimal order_amount) {
        this.order_amount = order_amount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column activity_special_right.special_right_commodity_ids
     *
     * @return the value of activity_special_right.special_right_commodity_ids
     *
     * @mbggenerated
     */
    public String getSpecial_right_commodity_ids() {
        return special_right_commodity_ids;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column activity_special_right.special_right_commodity_ids
     *
     * @param special_right_commodity_ids the value for activity_special_right.special_right_commodity_ids
     *
     * @mbggenerated
     */
    public void setSpecial_right_commodity_ids(String special_right_commodity_ids) {
        this.special_right_commodity_ids = special_right_commodity_ids == null ? null : special_right_commodity_ids.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column activity_special_right.special_right_remark
     *
     * @return the value of activity_special_right.special_right_remark
     *
     * @mbggenerated
     */
    public String getSpecial_right_remark() {
        return special_right_remark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column activity_special_right.special_right_remark
     *
     * @param special_right_remark the value for activity_special_right.special_right_remark
     *
     * @mbggenerated
     */
    public void setSpecial_right_remark(String special_right_remark) {
        this.special_right_remark = special_right_remark == null ? null : special_right_remark.trim();
    }
}