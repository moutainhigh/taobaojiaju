package com.xinshan.pojo.employee;

import com.xinshan.pojo.SearchOption;

/**
 * Created by mxt on 16-10-13.
 */
public class EmployeeSearchOption extends SearchOption {
    private String employee_code;
    private String role_id;
    private Integer position_id;
    private String handover_employee_code;
    private Integer employee_status;

    public Integer getEmployee_status() {
        return employee_status;
    }

    public void setEmployee_status(Integer employee_status) {
        this.employee_status = employee_status;
    }

    public String getHandover_employee_code() {
        return handover_employee_code;
    }

    public void setHandover_employee_code(String handover_employee_code) {
        this.handover_employee_code = handover_employee_code;
    }

    public Integer getPosition_id() {
        return position_id;
    }

    public void setPosition_id(Integer position_id) {
        this.position_id = position_id;
    }

    public String getRole_id() {
        return role_id;
    }

    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }

    public String getEmployee_code() {
        return employee_code;
    }

    public void setEmployee_code(String employee_code) {
        this.employee_code = employee_code;
    }
}
