package com.xinshan.model.extend.statistics;

import com.xinshan.model.Employee;
import com.xinshan.model.EmployeePerformance;

/**
 * Created by mxt on 17-8-25.
 */
public class Performance extends EmployeePerformance {
    private Employee employee;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
