package com.xinshan.controller.statistics;

import com.xinshan.components.statistics.PerformanceComponent;
import com.xinshan.model.Employee;
import com.xinshan.model.extend.employee.EmployeeExtend;
import com.xinshan.model.extend.statistics.Performance;
import com.xinshan.pojo.employee.EmployeeSearchOption;
import com.xinshan.pojo.statistics.PerformanceSearchOption;
import com.xinshan.service.EmployeePerformanceService;
import com.xinshan.service.EmployeeService;
import com.xinshan.utils.*;
import com.xinshan.utils.thread.PerformanceInit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 员工业绩统计
 * Created by mxt on 17-8-25.
 */
@Controller
public class EmployeePerformanceController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EmployeePerformanceService employeePerformanceService;

    @RequestMapping("/statistics/performance/performance")
    private void performance(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int year = Integer.parseInt(request.getParameter("performance_year"));
        int month = Integer.parseInt(request.getParameter("performance_month"));
        String employee_code = request.getParameter("employee_code");
        PerformanceComponent.performance(year, month, employee_code);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/statistics/performance/performanceInit")
    private void performanceInit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String employeeCodes = request.getParameter("employee_codes");
        List<String> list = new ArrayList<>();
        if (employeeCodes == null) {
            EmployeeSearchOption employeeSearchOption = new EmployeeSearchOption();
            employeeSearchOption.setRole_id(String.valueOf("53"));
            List<EmployeeExtend> employeeExtends = employeeService.employeeList(employeeSearchOption);
            for (int i = 0; i < employeeExtends.size(); i++) {
                list.add(employeeExtends.get(i).getEmployee_code());
            }
        }else {
            list = SplitUtils.splitToStrList(employeeCodes, ",");
        }
        int month = Integer.parseInt(request.getParameter("month"));
        int year = Integer.parseInt(request.getParameter("year"));
        if (PerformanceInit.status.get()) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "正在计算请稍等"));
            return;
        }
        PerformanceInit performanceInit = new PerformanceInit(list, month, year);
        ThreadPool threadPool = new ThreadPool(1);
        threadPool.getExecutorService().submit(performanceInit);
        threadPool.getExecutorService().shutdown();
        ResponseUtil.sendSuccessResponse(request, response);
    }

    @RequestMapping("/statistics/performance/performanceList")
    public void performanceList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PerformanceSearchOption performanceSearchOption = Request2ModelUtils.covert(PerformanceSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(performanceSearchOption);
        List<Performance> list = employeePerformanceService.performanceList(performanceSearchOption);
        Integer count = employeePerformanceService.countPerformance(performanceSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, performanceSearchOption);
    }
}
