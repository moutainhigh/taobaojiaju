package com.xinshan.service;

import com.xinshan.dao.EmployeePerformanceMapper;
import com.xinshan.dao.extend.statistics.EmployeePerformanceExtendMapper;
import com.xinshan.model.EmployeePerformance;
import com.xinshan.model.extend.statistics.Performance;
import com.xinshan.pojo.statistics.PerformanceSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

/**
 * Created by mxt on 17-8-25.
 */
@Service
public class EmployeePerformanceService {
    @Autowired
    private EmployeePerformanceExtendMapper employeePerformanceExtendMapper;
    @Autowired
    private EmployeePerformanceMapper employeePerformanceMapper;

    @Transactional
    public void createPerformance(EmployeePerformance employeePerformance) {
        employeePerformanceExtendMapper.createPerformance(employeePerformance);
    }
    @Transactional
    public void updatePerformance(EmployeePerformance employeePerformance) {
        employeePerformanceMapper.updateByPrimaryKey(employeePerformance);
    }
    public Performance getPerformance(int year, int month, String employee_code) {
        PerformanceSearchOption performanceSearchOption = new PerformanceSearchOption();
        performanceSearchOption.setPerformance_year(year);
        performanceSearchOption.setPerformance_month(month);
        performanceSearchOption.setEmployee_code(employee_code);
        List<Performance> performances = performanceList(performanceSearchOption);
        if (performances != null && performances.size() == 1) {
            return performances.get(0);
        }
        return null;
    }

    public List<Performance> performanceList(PerformanceSearchOption performanceSearchOption) {
        return employeePerformanceExtendMapper.performanceList(performanceSearchOption);
    }

    public Integer countPerformance(PerformanceSearchOption performanceSearchOption) {
        return employeePerformanceExtendMapper.countPerformance(performanceSearchOption);
    }
}
