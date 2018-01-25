package com.xinshan.interceptor;

import com.xinshan.components.app.PermitComponents;
import com.xinshan.model.Employee;
import com.xinshan.utils.SplitUtils;

import java.util.List;
import java.util.Set;

/**
 * Created by jonson.xu on 15-5-18.
 */
public class InspectPermit {

    public static boolean check(Employee employee, String url) {
        if (employee == null) {
            return false;
        }
        String role_ids = employee.getRole_ids();
        List<Integer> roles = SplitUtils.splitToList(role_ids, ",");
        boolean contains = PermitComponents.getAppPermitEmployees().contains(employee.getEmployee_code());
        if (roles.contains(1) || contains) {//管理员
            return true;
        }

        Set<String> employeeUrls = PermitComponents.employeeUrls(employee);
        if (!employeeUrls.contains(url)) {
            return false;
        }
        return true;
    }
}
