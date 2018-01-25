package com.xinshan.utils;

import com.xinshan.components.app.PermitComponents;
import com.xinshan.components.employee.EmployeeComponent;
import com.xinshan.model.Employee;
import com.xinshan.pojo.SearchOption;

import java.util.List;

/**
 * Created by mxt on 16-10-13.
 */
public class SearchOptionUtil {
    private SearchOptionUtil() {
    }

    private static SearchOptionUtil searchOptionUtil;

    public static SearchOptionUtil getSearchOptionUtil() {
        if (searchOptionUtil == null) searchOptionUtil = new SearchOptionUtil();
        return searchOptionUtil;
    }

    public void searchOptionInit(SearchOption searchOption) {
        try {
            if (searchOption.getCurrentPage() == null || searchOption.getCurrentPage() <= 0) {
                searchOption.setCurrentPage(1);
            }
            if (searchOption.getLimit() == null || searchOption.getLimit() <= 0) {
                searchOption.setLimit(20);
            }
            searchOption.setStart(searchOption.getLimit() * (searchOption.getCurrentPage() - 1));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void searchOptionDateInit(SearchOption searchOption) {
        try {
            if (searchOption.getStartDate() != null && searchOption.getEndDate() != null) {
                searchOption.setEndDate(DateUtil.endOfTheDay(searchOption.getEndDate()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void positionLimit(SearchOption searchOption, Employee employee) {
        if (PermitComponents.getAppPermitEmployees().contains(employee.getEmployee_code())) {//超级管理员，全部数据权限
            return;
        }
        /*Position position = PositionComponent.getPositionById(employee.getPosition_id());
        if (position != null) {
            String visible_position_ids = "";//
            if (position.getVisible_position_ids() != null) {
                visible_position_ids = visible_position_ids +","+position.getVisible_position_ids();
            }
            String s = PositionComponent.childPositionIds(position.getPosition_id());
            visible_position_ids += ","+s;
            searchOption.setPositionIds(visible_position_ids);
            searchOption.setRecord_employee_code(employee.getEmployee_code());
        }*/
        List<String> employeePermit = EmployeeComponent.employeePermit(employee);
        searchOption.setPermitEmployeeCodes(employeePermit);
    }


}
