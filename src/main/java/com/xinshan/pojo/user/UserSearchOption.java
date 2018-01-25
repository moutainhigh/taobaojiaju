package com.xinshan.pojo.user;

import com.xinshan.pojo.SearchOption;

import java.util.List;

/**
 * Created by mxt on 16-10-21.
 */
public class UserSearchOption extends SearchOption {
    private Integer user_id;
    private Integer user_status_id;
    private Integer user_stage_id;
    private Integer user_source_id;
    private Integer user_record_id;
    private String record_employee_code;
    private String record_employee_codes;
    private Integer all;//全部角色,1全部角色
    private String user_phone;
    private String user_weixin_openid;
    private List<Integer> bringUpIds;
    private String phone_number;
    private Integer daogou;
    private Integer orderNum;//下单数量,-1无订单，
    private Integer position_id;
    private Integer user_referrals;
    private Integer user_type;//1老带新  2新客户 3老客户
    private String user_bring_up_status;
    private List<Integer> userShoppingIds;
    private Integer user_shopping_id;
    private String user_first_phone;
    private String employee_code;
    private Integer user_status;
    private Integer generate_order;

    public Integer getGenerate_order() {
        return generate_order;
    }

    public void setGenerate_order(Integer generate_order) {
        this.generate_order = generate_order;
    }

    public Integer getUser_status() {
        return user_status;
    }

    public void setUser_status(Integer user_status) {
        this.user_status = user_status;
    }

    public String getEmployee_code() {
        return employee_code;
    }

    public void setEmployee_code(String employee_code) {
        this.employee_code = employee_code;
    }

    public String getUser_first_phone() {
        return user_first_phone;
    }

    public void setUser_first_phone(String user_first_phone) {
        this.user_first_phone = user_first_phone;
    }

    public Integer getUser_shopping_id() {
        return user_shopping_id;
    }

    public void setUser_shopping_id(Integer user_shopping_id) {
        this.user_shopping_id = user_shopping_id;
    }

    public List<Integer> getUserShoppingIds() {
        return userShoppingIds;
    }

    public void setUserShoppingIds(List<Integer> userShoppingIds) {
        this.userShoppingIds = userShoppingIds;
    }

    public String getUser_bring_up_status() {
        return user_bring_up_status;
    }

    public void setUser_bring_up_status(String user_bring_up_status) {
        this.user_bring_up_status = user_bring_up_status;
    }

    public Integer getUser_type() {
        return user_type;
    }

    public void setUser_type(Integer user_type) {
        this.user_type = user_type;
    }

    public Integer getUser_referrals() {
        return user_referrals;
    }

    public void setUser_referrals(Integer user_referrals) {
        this.user_referrals = user_referrals;
    }

    public Integer getPosition_id() {
        return position_id;
    }

    public void setPosition_id(Integer position_id) {
        this.position_id = position_id;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getDaogou() {
        return daogou;
    }

    public void setDaogou(Integer daogou) {
        this.daogou = daogou;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public List<Integer> getBringUpIds() {
        return bringUpIds;
    }

    public void setBringUpIds(List<Integer> bringUpIds) {
        this.bringUpIds = bringUpIds;
    }

    public String getUser_weixin_openid() {
        return user_weixin_openid;
    }

    public void setUser_weixin_openid(String user_weixin_openid) {
        this.user_weixin_openid = user_weixin_openid;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getRecord_employee_codes() {
        return record_employee_codes;
    }

    public void setRecord_employee_codes(String record_employee_codes) {
        this.record_employee_codes = record_employee_codes;
    }

    public Integer getAll() {
        return all;
    }

    public void setAll(Integer all) {
        this.all = all;
    }

    public String getRecord_employee_code() {
        return record_employee_code;
    }

    public void setRecord_employee_code(String record_employee_code) {
        this.record_employee_code = record_employee_code;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getUser_status_id() {
        return user_status_id;
    }

    public void setUser_status_id(Integer user_status_id) {
        this.user_status_id = user_status_id;
    }

    public Integer getUser_stage_id() {
        return user_stage_id;
    }

    public void setUser_stage_id(Integer user_stage_id) {
        this.user_stage_id = user_stage_id;
    }

    public Integer getUser_source_id() {
        return user_source_id;
    }

    public void setUser_source_id(Integer user_source_id) {
        this.user_source_id = user_source_id;
    }

    public Integer getUser_record_id() {
        return user_record_id;
    }

    public void setUser_record_id(Integer user_record_id) {
        this.user_record_id = user_record_id;
    }
}
