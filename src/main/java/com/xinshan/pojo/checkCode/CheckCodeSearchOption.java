package com.xinshan.pojo.checkCode;

import com.xinshan.pojo.SearchOption;

/**
 * Created by mxt on 16-12-28.
 */
public class CheckCodeSearchOption extends SearchOption {
    private String phone_number;
    private String check_code;
    private Integer invalid_type;

    public Integer getInvalid_type() {
        return invalid_type;
    }

    public void setInvalid_type(Integer invalid_type) {
        this.invalid_type = invalid_type;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getCheck_code() {
        return check_code;
    }

    public void setCheck_code(String check_code) {
        this.check_code = check_code;
    }
}
