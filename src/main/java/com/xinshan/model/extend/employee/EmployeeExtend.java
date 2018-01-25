package com.xinshan.model.extend.employee;

import com.xinshan.model.Employee;
import com.xinshan.model.Position;
import com.xinshan.model.Role;

import java.util.List;

/**
 * Created by mxt on 16-10-13.
 */
public class EmployeeExtend extends Employee {
    private List<Role> roles;
    private Position position;
    private String handover_employee_name;

    public String getHandover_employee_name() {
        return handover_employee_name;
    }

    public void setHandover_employee_name(String handover_employee_name) {
        this.handover_employee_name = handover_employee_name;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

}
