package com.xinshan.dao.extend.employee;

import com.xinshan.model.Employee;
import com.xinshan.model.extend.employee.EmployeeExtend;
import com.xinshan.pojo.employee.EmployeeSearchOption;

import java.util.List;

/**
 * Created by mxt on 16-10-9.
 */
public interface EmployeeExtendMapper {

    void createEmployee(Employee employee);

    void updateEmployee(Employee employee);

    void updateEmployeePassword(Employee employee);

    Employee getEmployeeByCode(String code);

    List<EmployeeExtend> employeeList(EmployeeSearchOption employeeSearchOption);

    Integer countEmployee(EmployeeSearchOption employeeSearchOption);


}
