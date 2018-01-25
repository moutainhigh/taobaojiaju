package com.xinshan.utils;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.employee.EmployeeComponent;
import com.xinshan.model.Employee;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by mxt on 15-8-31.
 */
public class RequestUtils {
    private static RequestUtils requestUtils;

    public static RequestUtils getRequestUtils() {
        if (requestUtils == null) {
            requestUtils = new RequestUtils();
        }
        return requestUtils;
    }

    public Employee getEmployeeCode(HttpServletRequest request) {
        Employee employee = (Employee) request.getSession().getAttribute("employee");
        if (employee == null) {
            return null;
        }
        employee = EmployeeComponent.getEmployeeByCode(employee.getEmployee_code());
        return employee;
    }

    public Map<String, String> getParam(HttpServletRequest request) {
        Map<String, String> param = new HashMap<String, String>();
        try {
            Set<String> set = request.getParameterMap().keySet();
            for (String key:set) {
                param.put(key, request.getParameter(key));
            }

            //application/json
            //String contentType=request.getContentType();
            //if (contentType != null && contentType.indexOf("application/json")>=0) {
            jsonParam(request, param);
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return param;
    }
    private void jsonParam(HttpServletRequest request, Map<String, String> param) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String s = null;
            StringBuilder stringBuilder = new StringBuilder();
            while ((s = reader.readLine()) != null) {
                stringBuilder.append(s);
            }
            JSONObject jsonObject = JSONObject.parseObject(stringBuilder.toString());
            Set<String> set = jsonObject.keySet();
            for (String key:set) {
                param.put(key, jsonObject.getString(key));
            }
        }catch (Exception e) {

        }
    }

    public String postData(HttpServletRequest request) throws IOException {
        InputStream inputStream = request.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String s = null;
        StringBuilder stringBuilder = new StringBuilder();
        while ((s = reader.readLine()) != null) {
            stringBuilder.append(s);
        }
        return stringBuilder.toString();
    }

    public void printParam(HttpServletRequest request) {
        Iterator<Map.Entry<String, String[]>> iterator = request.getParameterMap().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String[]> entry = iterator.next();
            System.out.println(entry.getKey()+"\t" + Arrays.toString(entry.getValue()));
        }
    }

    public String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public void printHeaders(HttpServletRequest request) {
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            System.out.println(key+"\t"+request.getHeader(key));
        }
    }

    /**
     * 获取请求域名
     * @param request
     * @return
     */
    public String requestDomain(HttpServletRequest request) {
        try {
            String domain = request.getHeader("Referer");//http://order.hnxinshan.com:8089/js/views/supplier/commodity-print.html?commodity_ids=149
            String[] ss = domain.split("/");
            ss = Arrays.copyOf(ss, 3);
            domain = ss[0]+"/"+ss[1]+"/"+ss[2];
            return domain;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 请求接口
     * @param request
     * @return
     */
    public String requestUrl(HttpServletRequest request) {
        String requestUrl = request.getRequestURI();
        return requestUrl;
    }
}
