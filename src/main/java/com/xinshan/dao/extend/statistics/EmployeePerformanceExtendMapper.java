package com.xinshan.dao.extend.statistics;

import com.xinshan.model.EmployeePerformance;
import com.xinshan.model.extend.statistics.Performance;
import com.xinshan.pojo.statistics.PerformanceSearchOption;

import java.util.List;

/**
 * Created by mxt on 17-8-25.
 */
public interface EmployeePerformanceExtendMapper {

    void createPerformance(EmployeePerformance employeePerformance);

    List<Performance> performanceList(PerformanceSearchOption performanceSearchOption);

    Integer countPerformance(PerformanceSearchOption performanceSearchOption);
}
