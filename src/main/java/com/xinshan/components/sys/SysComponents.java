package com.xinshan.components.sys;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.model.Employee;
import com.xinshan.model.RequestHistory;
import com.xinshan.service.SystemService;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.RequestUtils;
import com.xinshan.utils.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by mxt on 16-11-24.
 */
@Component
public class SysComponents {
    private static SystemService systemService;
    @Autowired
    public void setSystemService(SystemService systemService) {
        SysComponents.systemService = systemService;
    }

    public static void requestHistory(HttpServletRequest request, ResultData resultData, String requestData) {
        String requestUrl = null;
        try {
            requestUrl = RequestUtils.getRequestUtils().requestUrl(request);
        }catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
            RequestHistory requestHistory = new RequestHistory();
            if (employee != null) {
                requestHistory.setEmployee_name(employee.getEmployee_name());
                requestHistory.setEmployee_code(employee.getEmployee_code());
            }
            requestHistory.setRequest_url(requestUrl);
            Long requestTime = (Long) request.getAttribute("requestTime");
            requestHistory.setRequest_time(requestTime);
            requestHistory.setResponse_time(DateUtil.currentDate().getTime());
            String response_data = "";
            if (resultData != null) {
                response_data = JSONObject.toJSONString(resultData);
                requestHistory.setResponse_data_len(response_data.length());
                if (requestUrl.endsWith("List") || requestUrl.endsWith("Detail")
                        || requestUrl.endsWith("Details") || requestUrl.endsWith("detail")
                        || requestUrl.endsWith("History") || requestUrl.endsWith("eports") || requestUrl.endsWith("report")
                        || requestUrl.startsWith("/order/inventory/inventoryHistoryDetails") || requestUrl.equals("/sys/app/employeeApps")
                        || requestUrl.equals("/sys/app/list")) {

                }else {
                    requestHistory.setResponse_data(response_data);
                }
            }
            requestHistory.setRequest_param(JSONObject.toJSONString(request.getParameterMap()));
            requestHistory.setRequest_data(requestData);
            requestHistory.setRequest_method(request.getMethod());
            systemService.createRequestHistory(requestHistory);
        }catch (Exception e) {
            System.out.println(requestUrl);
            e.printStackTrace();
        }
    }
}
