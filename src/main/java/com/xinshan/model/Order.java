package com.xinshan.model;

import java.math.BigDecimal;
import java.util.Date;

public class Order {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.order_id
     *
     * @mbggenerated
     */
    private Integer order_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.order_date
     *
     * @mbggenerated
     */
    private Date order_date;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.order_code
     *
     * @mbggenerated
     */
    private String order_code;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.record_employee_code
     *
     * @mbggenerated
     */
    private String record_employee_code;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.record_employee_name
     *
     * @mbggenerated
     */
    private String record_employee_name;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.total_num
     *
     * @mbggenerated
     */
    private Integer total_num;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.total_price
     *
     * @mbggenerated
     */
    private BigDecimal total_price;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.record_date
     *
     * @mbggenerated
     */
    private Date record_date;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.customer_name
     *
     * @mbggenerated
     */
    private String customer_name;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.customer_phone_number
     *
     * @mbggenerated
     */
    private String customer_phone_number;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.delivery_type_id
     *
     * @mbggenerated
     */
    private Integer delivery_type_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.delivery_date
     *
     * @mbggenerated
     */
    private Date delivery_date;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.order_status
     *
     * @mbggenerated
     */
    private Integer order_status;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.seller
     *
     * @mbggenerated
     */
    private String seller;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.employee_code
     *
     * @mbggenerated
     */
    private String employee_code;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.employee_name
     *
     * @mbggenerated
     */
    private String employee_name;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.trans_purchase
     *
     * @mbggenerated
     */
    private Integer trans_purchase;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.order_paper_code
     *
     * @mbggenerated
     */
    private String order_paper_code;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.order_total_price
     *
     * @mbggenerated
     */
    private BigDecimal order_total_price;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.order_step
     *
     * @mbggenerated
     */
    private Integer order_step;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.position_id
     *
     * @mbggenerated
     */
    private Integer position_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.preferential_amount
     *
     * @mbggenerated
     */
    private BigDecimal preferential_amount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.return_status
     *
     * @mbggenerated
     */
    private Integer return_status;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.order_remark
     *
     * @mbggenerated
     */
    private String order_remark;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.customer_second_phone
     *
     * @mbggenerated
     */
    private String customer_second_phone;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.delivery_address
     *
     * @mbggenerated
     */
    private String delivery_address;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.order_pay_ids
     *
     * @mbggenerated
     */
    private String order_pay_ids;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.order_id
     *
     * @return the value of order.order_id
     *
     * @mbggenerated
     */
    public Integer getOrder_id() {
        return order_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.order_id
     *
     * @param order_id the value for order.order_id
     *
     * @mbggenerated
     */
    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.order_date
     *
     * @return the value of order.order_date
     *
     * @mbggenerated
     */
    public Date getOrder_date() {
        return order_date;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.order_date
     *
     * @param order_date the value for order.order_date
     *
     * @mbggenerated
     */
    public void setOrder_date(Date order_date) {
        this.order_date = order_date;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.order_code
     *
     * @return the value of order.order_code
     *
     * @mbggenerated
     */
    public String getOrder_code() {
        return order_code;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.order_code
     *
     * @param order_code the value for order.order_code
     *
     * @mbggenerated
     */
    public void setOrder_code(String order_code) {
        this.order_code = order_code == null ? null : order_code.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.record_employee_code
     *
     * @return the value of order.record_employee_code
     *
     * @mbggenerated
     */
    public String getRecord_employee_code() {
        return record_employee_code;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.record_employee_code
     *
     * @param record_employee_code the value for order.record_employee_code
     *
     * @mbggenerated
     */
    public void setRecord_employee_code(String record_employee_code) {
        this.record_employee_code = record_employee_code == null ? null : record_employee_code.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.record_employee_name
     *
     * @return the value of order.record_employee_name
     *
     * @mbggenerated
     */
    public String getRecord_employee_name() {
        return record_employee_name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.record_employee_name
     *
     * @param record_employee_name the value for order.record_employee_name
     *
     * @mbggenerated
     */
    public void setRecord_employee_name(String record_employee_name) {
        this.record_employee_name = record_employee_name == null ? null : record_employee_name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.total_num
     *
     * @return the value of order.total_num
     *
     * @mbggenerated
     */
    public Integer getTotal_num() {
        return total_num;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.total_num
     *
     * @param total_num the value for order.total_num
     *
     * @mbggenerated
     */
    public void setTotal_num(Integer total_num) {
        this.total_num = total_num;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.total_price
     *
     * @return the value of order.total_price
     *
     * @mbggenerated
     */
    public BigDecimal getTotal_price() {
        return total_price;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.total_price
     *
     * @param total_price the value for order.total_price
     *
     * @mbggenerated
     */
    public void setTotal_price(BigDecimal total_price) {
        this.total_price = total_price;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.record_date
     *
     * @return the value of order.record_date
     *
     * @mbggenerated
     */
    public Date getRecord_date() {
        return record_date;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.record_date
     *
     * @param record_date the value for order.record_date
     *
     * @mbggenerated
     */
    public void setRecord_date(Date record_date) {
        this.record_date = record_date;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.customer_name
     *
     * @return the value of order.customer_name
     *
     * @mbggenerated
     */
    public String getCustomer_name() {
        return customer_name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.customer_name
     *
     * @param customer_name the value for order.customer_name
     *
     * @mbggenerated
     */
    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name == null ? null : customer_name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.customer_phone_number
     *
     * @return the value of order.customer_phone_number
     *
     * @mbggenerated
     */
    public String getCustomer_phone_number() {
        return customer_phone_number;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.customer_phone_number
     *
     * @param customer_phone_number the value for order.customer_phone_number
     *
     * @mbggenerated
     */
    public void setCustomer_phone_number(String customer_phone_number) {
        this.customer_phone_number = customer_phone_number == null ? null : customer_phone_number.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.delivery_type_id
     *
     * @return the value of order.delivery_type_id
     *
     * @mbggenerated
     */
    public Integer getDelivery_type_id() {
        return delivery_type_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.delivery_type_id
     *
     * @param delivery_type_id the value for order.delivery_type_id
     *
     * @mbggenerated
     */
    public void setDelivery_type_id(Integer delivery_type_id) {
        this.delivery_type_id = delivery_type_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.delivery_date
     *
     * @return the value of order.delivery_date
     *
     * @mbggenerated
     */
    public Date getDelivery_date() {
        return delivery_date;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.delivery_date
     *
     * @param delivery_date the value for order.delivery_date
     *
     * @mbggenerated
     */
    public void setDelivery_date(Date delivery_date) {
        this.delivery_date = delivery_date;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.order_status
     *
     * @return the value of order.order_status
     *
     * @mbggenerated
     */
    public Integer getOrder_status() {
        return order_status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.order_status
     *
     * @param order_status the value for order.order_status
     *
     * @mbggenerated
     */
    public void setOrder_status(Integer order_status) {
        this.order_status = order_status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.seller
     *
     * @return the value of order.seller
     *
     * @mbggenerated
     */
    public String getSeller() {
        return seller;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.seller
     *
     * @param seller the value for order.seller
     *
     * @mbggenerated
     */
    public void setSeller(String seller) {
        this.seller = seller == null ? null : seller.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.employee_code
     *
     * @return the value of order.employee_code
     *
     * @mbggenerated
     */
    public String getEmployee_code() {
        return employee_code;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.employee_code
     *
     * @param employee_code the value for order.employee_code
     *
     * @mbggenerated
     */
    public void setEmployee_code(String employee_code) {
        this.employee_code = employee_code == null ? null : employee_code.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.employee_name
     *
     * @return the value of order.employee_name
     *
     * @mbggenerated
     */
    public String getEmployee_name() {
        return employee_name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.employee_name
     *
     * @param employee_name the value for order.employee_name
     *
     * @mbggenerated
     */
    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name == null ? null : employee_name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.trans_purchase
     *
     * @return the value of order.trans_purchase
     *
     * @mbggenerated
     */
    public Integer getTrans_purchase() {
        return trans_purchase;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.trans_purchase
     *
     * @param trans_purchase the value for order.trans_purchase
     *
     * @mbggenerated
     */
    public void setTrans_purchase(Integer trans_purchase) {
        this.trans_purchase = trans_purchase;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.order_paper_code
     *
     * @return the value of order.order_paper_code
     *
     * @mbggenerated
     */
    public String getOrder_paper_code() {
        return order_paper_code;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.order_paper_code
     *
     * @param order_paper_code the value for order.order_paper_code
     *
     * @mbggenerated
     */
    public void setOrder_paper_code(String order_paper_code) {
        this.order_paper_code = order_paper_code == null ? null : order_paper_code.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.order_total_price
     *
     * @return the value of order.order_total_price
     *
     * @mbggenerated
     */
    public BigDecimal getOrder_total_price() {
        return order_total_price;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.order_total_price
     *
     * @param order_total_price the value for order.order_total_price
     *
     * @mbggenerated
     */
    public void setOrder_total_price(BigDecimal order_total_price) {
        this.order_total_price = order_total_price;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.order_step
     *
     * @return the value of order.order_step
     *
     * @mbggenerated
     */
    public Integer getOrder_step() {
        return order_step;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.order_step
     *
     * @param order_step the value for order.order_step
     *
     * @mbggenerated
     */
    public void setOrder_step(Integer order_step) {
        this.order_step = order_step;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.position_id
     *
     * @return the value of order.position_id
     *
     * @mbggenerated
     */
    public Integer getPosition_id() {
        return position_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.position_id
     *
     * @param position_id the value for order.position_id
     *
     * @mbggenerated
     */
    public void setPosition_id(Integer position_id) {
        this.position_id = position_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.preferential_amount
     *
     * @return the value of order.preferential_amount
     *
     * @mbggenerated
     */
    public BigDecimal getPreferential_amount() {
        return preferential_amount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.preferential_amount
     *
     * @param preferential_amount the value for order.preferential_amount
     *
     * @mbggenerated
     */
    public void setPreferential_amount(BigDecimal preferential_amount) {
        this.preferential_amount = preferential_amount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.return_status
     *
     * @return the value of order.return_status
     *
     * @mbggenerated
     */
    public Integer getReturn_status() {
        return return_status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.return_status
     *
     * @param return_status the value for order.return_status
     *
     * @mbggenerated
     */
    public void setReturn_status(Integer return_status) {
        this.return_status = return_status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.order_remark
     *
     * @return the value of order.order_remark
     *
     * @mbggenerated
     */
    public String getOrder_remark() {
        return order_remark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.order_remark
     *
     * @param order_remark the value for order.order_remark
     *
     * @mbggenerated
     */
    public void setOrder_remark(String order_remark) {
        this.order_remark = order_remark == null ? null : order_remark.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.customer_second_phone
     *
     * @return the value of order.customer_second_phone
     *
     * @mbggenerated
     */
    public String getCustomer_second_phone() {
        return customer_second_phone;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.customer_second_phone
     *
     * @param customer_second_phone the value for order.customer_second_phone
     *
     * @mbggenerated
     */
    public void setCustomer_second_phone(String customer_second_phone) {
        this.customer_second_phone = customer_second_phone == null ? null : customer_second_phone.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.delivery_address
     *
     * @return the value of order.delivery_address
     *
     * @mbggenerated
     */
    public String getDelivery_address() {
        return delivery_address;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.delivery_address
     *
     * @param delivery_address the value for order.delivery_address
     *
     * @mbggenerated
     */
    public void setDelivery_address(String delivery_address) {
        this.delivery_address = delivery_address == null ? null : delivery_address.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.order_pay_ids
     *
     * @return the value of order.order_pay_ids
     *
     * @mbggenerated
     */
    public String getOrder_pay_ids() {
        return order_pay_ids;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.order_pay_ids
     *
     * @param order_pay_ids the value for order.order_pay_ids
     *
     * @mbggenerated
     */
    public void setOrder_pay_ids(String order_pay_ids) {
        this.order_pay_ids = order_pay_ids == null ? null : order_pay_ids.trim();
    }
}