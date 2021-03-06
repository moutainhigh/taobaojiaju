package com.xinshan.model;

import java.math.BigDecimal;

public class SalesTarget {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sales_target.sales_target_id
     *
     * @mbggenerated
     */
    private Integer sales_target_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sales_target.position_id
     *
     * @mbggenerated
     */
    private Integer position_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sales_target.sales_target_year
     *
     * @mbggenerated
     */
    private Integer sales_target_year;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sales_target.sales_target_year_amount
     *
     * @mbggenerated
     */
    private BigDecimal sales_target_year_amount;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sales_target.sales_target_id
     *
     * @return the value of sales_target.sales_target_id
     *
     * @mbggenerated
     */
    public Integer getSales_target_id() {
        return sales_target_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sales_target.sales_target_id
     *
     * @param sales_target_id the value for sales_target.sales_target_id
     *
     * @mbggenerated
     */
    public void setSales_target_id(Integer sales_target_id) {
        this.sales_target_id = sales_target_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sales_target.position_id
     *
     * @return the value of sales_target.position_id
     *
     * @mbggenerated
     */
    public Integer getPosition_id() {
        return position_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sales_target.position_id
     *
     * @param position_id the value for sales_target.position_id
     *
     * @mbggenerated
     */
    public void setPosition_id(Integer position_id) {
        this.position_id = position_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sales_target.sales_target_year
     *
     * @return the value of sales_target.sales_target_year
     *
     * @mbggenerated
     */
    public Integer getSales_target_year() {
        return sales_target_year;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sales_target.sales_target_year
     *
     * @param sales_target_year the value for sales_target.sales_target_year
     *
     * @mbggenerated
     */
    public void setSales_target_year(Integer sales_target_year) {
        this.sales_target_year = sales_target_year;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sales_target.sales_target_year_amount
     *
     * @return the value of sales_target.sales_target_year_amount
     *
     * @mbggenerated
     */
    public BigDecimal getSales_target_year_amount() {
        return sales_target_year_amount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sales_target.sales_target_year_amount
     *
     * @param sales_target_year_amount the value for sales_target.sales_target_year_amount
     *
     * @mbggenerated
     */
    public void setSales_target_year_amount(BigDecimal sales_target_year_amount) {
        this.sales_target_year_amount = sales_target_year_amount;
    }
}