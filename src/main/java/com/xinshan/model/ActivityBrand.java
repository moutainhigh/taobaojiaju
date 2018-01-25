package com.xinshan.model;

import java.math.BigDecimal;

public class ActivityBrand {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column activity_brand.activity_brand_id
     *
     * @mbggenerated
     */
    private Integer activity_brand_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column activity_brand.activity_id
     *
     * @mbggenerated
     */
    private Integer activity_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column activity_brand.activity_brand_amount
     *
     * @mbggenerated
     */
    private BigDecimal activity_brand_amount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column activity_brand.brand_gift_num
     *
     * @mbggenerated
     */
    private Integer brand_gift_num;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column activity_brand.activity_brand_names
     *
     * @mbggenerated
     */
    private String activity_brand_names;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column activity_brand.activity_brand_remark
     *
     * @mbggenerated
     */
    private String activity_brand_remark;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column activity_brand.activity_brand_id
     *
     * @return the value of activity_brand.activity_brand_id
     *
     * @mbggenerated
     */
    public Integer getActivity_brand_id() {
        return activity_brand_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column activity_brand.activity_brand_id
     *
     * @param activity_brand_id the value for activity_brand.activity_brand_id
     *
     * @mbggenerated
     */
    public void setActivity_brand_id(Integer activity_brand_id) {
        this.activity_brand_id = activity_brand_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column activity_brand.activity_id
     *
     * @return the value of activity_brand.activity_id
     *
     * @mbggenerated
     */
    public Integer getActivity_id() {
        return activity_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column activity_brand.activity_id
     *
     * @param activity_id the value for activity_brand.activity_id
     *
     * @mbggenerated
     */
    public void setActivity_id(Integer activity_id) {
        this.activity_id = activity_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column activity_brand.activity_brand_amount
     *
     * @return the value of activity_brand.activity_brand_amount
     *
     * @mbggenerated
     */
    public BigDecimal getActivity_brand_amount() {
        return activity_brand_amount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column activity_brand.activity_brand_amount
     *
     * @param activity_brand_amount the value for activity_brand.activity_brand_amount
     *
     * @mbggenerated
     */
    public void setActivity_brand_amount(BigDecimal activity_brand_amount) {
        this.activity_brand_amount = activity_brand_amount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column activity_brand.brand_gift_num
     *
     * @return the value of activity_brand.brand_gift_num
     *
     * @mbggenerated
     */
    public Integer getBrand_gift_num() {
        return brand_gift_num;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column activity_brand.brand_gift_num
     *
     * @param brand_gift_num the value for activity_brand.brand_gift_num
     *
     * @mbggenerated
     */
    public void setBrand_gift_num(Integer brand_gift_num) {
        this.brand_gift_num = brand_gift_num;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column activity_brand.activity_brand_names
     *
     * @return the value of activity_brand.activity_brand_names
     *
     * @mbggenerated
     */
    public String getActivity_brand_names() {
        return activity_brand_names;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column activity_brand.activity_brand_names
     *
     * @param activity_brand_names the value for activity_brand.activity_brand_names
     *
     * @mbggenerated
     */
    public void setActivity_brand_names(String activity_brand_names) {
        this.activity_brand_names = activity_brand_names == null ? null : activity_brand_names.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column activity_brand.activity_brand_remark
     *
     * @return the value of activity_brand.activity_brand_remark
     *
     * @mbggenerated
     */
    public String getActivity_brand_remark() {
        return activity_brand_remark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column activity_brand.activity_brand_remark
     *
     * @param activity_brand_remark the value for activity_brand.activity_brand_remark
     *
     * @mbggenerated
     */
    public void setActivity_brand_remark(String activity_brand_remark) {
        this.activity_brand_remark = activity_brand_remark == null ? null : activity_brand_remark.trim();
    }
}