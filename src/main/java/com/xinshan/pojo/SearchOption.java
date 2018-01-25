package com.xinshan.pojo;

import java.util.Date;
import java.util.List;

/**
 * Created by mxt on 16-10-12.
 */
public class SearchOption {
    private Integer start;
    private Integer limit;
    private Integer currentPage;

    private String positionIds;//数据权限
    private String record_employee_code;

    private String param;//模糊查询
    private Date startDate;
    private Date endDate;
    private Long startTime;
    private Long endTime;
    private List<String> permitEmployeeCodes;//数据权限

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }


    public List<String> getPermitEmployeeCodes() {
        return permitEmployeeCodes;
    }

    public void setPermitEmployeeCodes(List<String> permitEmployeeCodes) {
        this.permitEmployeeCodes = permitEmployeeCodes;
    }

    public String getRecord_employee_code() {
        return record_employee_code;
    }

    public void setRecord_employee_code(String record_employee_code) {
        this.record_employee_code = record_employee_code;
    }

    public String getPositionIds() {
        return positionIds;
    }

    public void setPositionIds(String positionIds) {
        this.positionIds = positionIds;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }
}
