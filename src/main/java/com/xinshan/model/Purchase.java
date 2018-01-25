package com.xinshan.model;

import java.util.Date;

public class Purchase {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column purchase.purchase_id
     *
     * @mbggenerated
     */
    private Integer purchase_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column purchase.supplier_id
     *
     * @mbggenerated
     */
    private Integer supplier_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column purchase.purchase_code
     *
     * @mbggenerated
     */
    private String purchase_code;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column purchase.order_id
     *
     * @mbggenerated
     */
    private Integer order_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column purchase.purchase_complete_date
     *
     * @mbggenerated
     */
    private Date purchase_complete_date;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column purchase.purchase_start_date
     *
     * @mbggenerated
     */
    private Date purchase_start_date;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column purchase.purchase_status
     *
     * @mbggenerated
     */
    private Integer purchase_status;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column purchase.record_employee_code
     *
     * @mbggenerated
     */
    private String record_employee_code;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column purchase.record_employee_name
     *
     * @mbggenerated
     */
    private String record_employee_name;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column purchase.estimate_arrival_date
     *
     * @mbggenerated
     */
    private Date estimate_arrival_date;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column purchase.order_return_id
     *
     * @mbggenerated
     */
    private Integer order_return_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column purchase.purchase_type
     *
     * @mbggenerated
     */
    private Integer purchase_type;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column purchase.gift_id
     *
     * @mbggenerated
     */
    private Integer gift_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column purchase.purchase_remark
     *
     * @mbggenerated
     */
    private String purchase_remark;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column purchase.purchase_id
     *
     * @return the value of purchase.purchase_id
     *
     * @mbggenerated
     */
    public Integer getPurchase_id() {
        return purchase_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column purchase.purchase_id
     *
     * @param purchase_id the value for purchase.purchase_id
     *
     * @mbggenerated
     */
    public void setPurchase_id(Integer purchase_id) {
        this.purchase_id = purchase_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column purchase.supplier_id
     *
     * @return the value of purchase.supplier_id
     *
     * @mbggenerated
     */
    public Integer getSupplier_id() {
        return supplier_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column purchase.supplier_id
     *
     * @param supplier_id the value for purchase.supplier_id
     *
     * @mbggenerated
     */
    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column purchase.purchase_code
     *
     * @return the value of purchase.purchase_code
     *
     * @mbggenerated
     */
    public String getPurchase_code() {
        return purchase_code;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column purchase.purchase_code
     *
     * @param purchase_code the value for purchase.purchase_code
     *
     * @mbggenerated
     */
    public void setPurchase_code(String purchase_code) {
        this.purchase_code = purchase_code == null ? null : purchase_code.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column purchase.order_id
     *
     * @return the value of purchase.order_id
     *
     * @mbggenerated
     */
    public Integer getOrder_id() {
        return order_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column purchase.order_id
     *
     * @param order_id the value for purchase.order_id
     *
     * @mbggenerated
     */
    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column purchase.purchase_complete_date
     *
     * @return the value of purchase.purchase_complete_date
     *
     * @mbggenerated
     */
    public Date getPurchase_complete_date() {
        return purchase_complete_date;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column purchase.purchase_complete_date
     *
     * @param purchase_complete_date the value for purchase.purchase_complete_date
     *
     * @mbggenerated
     */
    public void setPurchase_complete_date(Date purchase_complete_date) {
        this.purchase_complete_date = purchase_complete_date;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column purchase.purchase_start_date
     *
     * @return the value of purchase.purchase_start_date
     *
     * @mbggenerated
     */
    public Date getPurchase_start_date() {
        return purchase_start_date;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column purchase.purchase_start_date
     *
     * @param purchase_start_date the value for purchase.purchase_start_date
     *
     * @mbggenerated
     */
    public void setPurchase_start_date(Date purchase_start_date) {
        this.purchase_start_date = purchase_start_date;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column purchase.purchase_status
     *
     * @return the value of purchase.purchase_status
     *
     * @mbggenerated
     */
    public Integer getPurchase_status() {
        return purchase_status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column purchase.purchase_status
     *
     * @param purchase_status the value for purchase.purchase_status
     *
     * @mbggenerated
     */
    public void setPurchase_status(Integer purchase_status) {
        this.purchase_status = purchase_status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column purchase.record_employee_code
     *
     * @return the value of purchase.record_employee_code
     *
     * @mbggenerated
     */
    public String getRecord_employee_code() {
        return record_employee_code;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column purchase.record_employee_code
     *
     * @param record_employee_code the value for purchase.record_employee_code
     *
     * @mbggenerated
     */
    public void setRecord_employee_code(String record_employee_code) {
        this.record_employee_code = record_employee_code == null ? null : record_employee_code.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column purchase.record_employee_name
     *
     * @return the value of purchase.record_employee_name
     *
     * @mbggenerated
     */
    public String getRecord_employee_name() {
        return record_employee_name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column purchase.record_employee_name
     *
     * @param record_employee_name the value for purchase.record_employee_name
     *
     * @mbggenerated
     */
    public void setRecord_employee_name(String record_employee_name) {
        this.record_employee_name = record_employee_name == null ? null : record_employee_name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column purchase.estimate_arrival_date
     *
     * @return the value of purchase.estimate_arrival_date
     *
     * @mbggenerated
     */
    public Date getEstimate_arrival_date() {
        return estimate_arrival_date;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column purchase.estimate_arrival_date
     *
     * @param estimate_arrival_date the value for purchase.estimate_arrival_date
     *
     * @mbggenerated
     */
    public void setEstimate_arrival_date(Date estimate_arrival_date) {
        this.estimate_arrival_date = estimate_arrival_date;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column purchase.order_return_id
     *
     * @return the value of purchase.order_return_id
     *
     * @mbggenerated
     */
    public Integer getOrder_return_id() {
        return order_return_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column purchase.order_return_id
     *
     * @param order_return_id the value for purchase.order_return_id
     *
     * @mbggenerated
     */
    public void setOrder_return_id(Integer order_return_id) {
        this.order_return_id = order_return_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column purchase.purchase_type
     *
     * @return the value of purchase.purchase_type
     *
     * @mbggenerated
     */
    public Integer getPurchase_type() {
        return purchase_type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column purchase.purchase_type
     *
     * @param purchase_type the value for purchase.purchase_type
     *
     * @mbggenerated
     */
    public void setPurchase_type(Integer purchase_type) {
        this.purchase_type = purchase_type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column purchase.gift_id
     *
     * @return the value of purchase.gift_id
     *
     * @mbggenerated
     */
    public Integer getGift_id() {
        return gift_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column purchase.gift_id
     *
     * @param gift_id the value for purchase.gift_id
     *
     * @mbggenerated
     */
    public void setGift_id(Integer gift_id) {
        this.gift_id = gift_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column purchase.purchase_remark
     *
     * @return the value of purchase.purchase_remark
     *
     * @mbggenerated
     */
    public String getPurchase_remark() {
        return purchase_remark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column purchase.purchase_remark
     *
     * @param purchase_remark the value for purchase.purchase_remark
     *
     * @mbggenerated
     */
    public void setPurchase_remark(String purchase_remark) {
        this.purchase_remark = purchase_remark == null ? null : purchase_remark.trim();
    }
}