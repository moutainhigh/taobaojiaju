package com.xinshan.service;

import com.xinshan.components.role.RoleComponents;
import com.xinshan.dao.EmployeeMapper;
import com.xinshan.dao.extend.employee.EmployeeExtendMapper;
import com.xinshan.model.Employee;
import com.xinshan.model.Role;
import com.xinshan.model.extend.employee.EmployeeExtend;
import com.xinshan.utils.SplitUtils;
import com.xinshan.pojo.employee.EmployeeSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mxt on 16-10-9.
 */
@Service
public class EmployeeService {
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private EmployeeExtendMapper employeeExtendMapper;

    @Transactional
    public void createEmployee(Employee employee) {
        employeeExtendMapper.createEmployee(employee);
    }

    /**
     * 编辑用户，不修改密码
     * @param employee
     */
    @Transactional
    public void updateEmployee(Employee employee) {
        employeeExtendMapper.updateEmployee(employee);
    }

    /**
     * 修改密码
     * @param employee
     */
    @Transactional
    public void updateEmployeePassword(Employee employee) {
        employeeExtendMapper.updateEmployeePassword(employee);
    }

    public Employee getEmployeeByCode1(String employee_code) {
        return employeeExtendMapper.getEmployeeByCode(employee_code);
    }

    public EmployeeExtend getEmployeeByCode(String code) {
        EmployeeSearchOption employeeSearchOption = new EmployeeSearchOption();
        employeeSearchOption.setEmployee_code(code);
        List<EmployeeExtend> list = employeeList(employeeSearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public List<EmployeeExtend> employeeList(EmployeeSearchOption employeeSearchOption) {
        List<EmployeeExtend> list = employeeExtendMapper.employeeList(employeeSearchOption);
        for (int i = 0; i < list.size(); i++) {
            EmployeeExtend employee = list.get(i);
            String role_ids = employee.getRole_ids();
            List<Role> roles = new ArrayList<>();
            List<Integer> roleIds = SplitUtils.splitToList(role_ids, ",");
            for (int j = 0; j < roleIds.size(); j++) {
                int role_id = roleIds.get(j);
                Role role = RoleComponents.getRoleById(role_id);
                if (role != null) {
                    roles.add(role);
                }
            }
            employee.setRoles(roles);
            employee.setEmployee_password(null);
            if (employee.getHandover_employee_code() != null) {
                employee.setHandover_employee_name(getEmployeeByCode1(employee.getHandover_employee_code()).getEmployee_name());
            }
        }
        return list;
    }

    public Integer countEmployee(EmployeeSearchOption employeeSearchOption) {
        return employeeExtendMapper.countEmployee(employeeSearchOption);
    }

    public List<EmployeeExtend> getEmployeeByHandover(String handover_employee_code) {
        EmployeeSearchOption employeeSearchOption = new EmployeeSearchOption();
        employeeSearchOption.setHandover_employee_code(handover_employee_code);
        return employeeList(employeeSearchOption);
    }

    @Transactional
    public void employeeImport(List<Employee> list) {
        for (int i = 0; i < list.size(); i++) {
            Employee employee = list.get(i);
            Employee employeeByCode = getEmployeeByCode1(employee.getEmployee_code());
            if (employeeByCode == null) {
                employeeExtendMapper.createEmployee(employee);
            }else {
                employeeByCode.setEmployee_name(employee.getEmployee_name());
                employeeByCode.setPhone_number(employee.getPhone_number());
                employeeMapper.updateByPrimaryKey(employeeByCode);
            }
        }
    }


}
