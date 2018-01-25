package com.xinshan.components.employee;

import com.xinshan.components.position.PositionComponent;
import com.xinshan.model.Employee;
import com.xinshan.model.Position;
import com.xinshan.model.extend.employee.EmployeeExtend;
import com.xinshan.service.EmployeeService;
import com.xinshan.utils.EncryptionUtils;
import com.xinshan.utils.SplitUtils;
import com.xinshan.pojo.employee.EmployeeSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mxt on 16-11-29.
 */
@Component
public class EmployeeComponent {
    private static EmployeeService employeeService;

    @Autowired
    public void setEmployeeService(EmployeeService employeeService) {
        EmployeeComponent.employeeService = employeeService;
    }

    public static EmployeeExtend getEmployeeByCode(String employee_code) {
        return employeeService.getEmployeeByCode(employee_code);
    }

    private static List<String> getEmployeeCodeByPosition(String visible_position_ids) {
        List<Integer> positionIds = SplitUtils.splitToList(visible_position_ids, ",");
        if (positionIds.size() == 0) {
            return new ArrayList<>();
        }
        EmployeeSearchOption employeeSearchOption = new EmployeeSearchOption();
        employeeSearchOption.setPositionIds(visible_position_ids);
        List<EmployeeExtend> list = employeeService.employeeList(employeeSearchOption);
        List<String> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Employee employee = list.get(i);
            result.add(employee.getEmployee_code());
        }
        return result;
    }

    public static List<String> employeePermit(Employee employee) {
        employee = getEmployeeByCode(employee.getEmployee_code());
        int position_id = employee.getPosition_id();
        Position position = PositionComponent.getPositionById(position_id);
        String visible_position_ids = position.getVisible_position_ids();
        String s = PositionComponent.childPositionIds(position.getPosition_id());
        if (visible_position_ids == null || visible_position_ids.equals("")) {
            visible_position_ids = s;
        }else {
            visible_position_ids += ","+s;
        }
        List<String> list = getEmployeeCodeByPosition(visible_position_ids);
        list.add(employee.getEmployee_code());

        //交接用户
        EmployeeSearchOption employeeSearchOption = new EmployeeSearchOption();
        employeeSearchOption.setHandover_employee_code(employee.getEmployee_code());
        List<EmployeeExtend> list1 = employeeService.employeeList(employeeSearchOption);
        if (list1 != null && list1.size() > 0) {
            for (int i = 0; i < list1.size(); i++) {
                EmployeeExtend e = list1.get(i);
                list.add(e.getEmployee_code());
            }
        }

        return list;
    }

    /**
     *
     * @param employee_code
     * @param employee_password
     * @return
     */
    public static Employee loginCheck(String employee_code, String employee_password) {
        if (employee_code == null || employee_password == null || "".equals(employee_code) || "".equals(employee_password)) {
            return null;
        }
        Employee employee = employeeService.getEmployeeByCode1(employee_code);
        if (employee == null || employee.getEmployee_status() != 1 || !employee.getEmployee_password().equals(EncryptionUtils.encryption(employee_password))) {
            return null;//用户名或密码错误
        }
        return employee;
    }
}
