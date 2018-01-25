package com.xinshan.pojo.statistics;

import com.xinshan.pojo.SearchOption;

/**
 * Created by mxt on 17-8-25.
 */
public class PerformanceSearchOption extends SearchOption {
    private String employee_code;
    private Integer performance_month;
    private Integer performance_year;
    private Integer sell_amount;

    public Integer getSell_amount() {
        return sell_amount;
    }

    public void setSell_amount(Integer sell_amount) {
        this.sell_amount = sell_amount;
    }

    public Integer getPerformance_month() {
        return performance_month;
    }

    public void setPerformance_month(Integer performance_month) {
        this.performance_month = performance_month;
    }

    public Integer getPerformance_year() {
        return performance_year;
    }

    public void setPerformance_year(Integer performance_year) {
        this.performance_year = performance_year;
    }

    public String getEmployee_code() {
        return employee_code;
    }

    public void setEmployee_code(String employee_code) {
        this.employee_code = employee_code;
    }
}
