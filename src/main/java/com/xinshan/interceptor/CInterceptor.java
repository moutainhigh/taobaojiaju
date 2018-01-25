package com.xinshan.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.app.PermitComponents;
import com.xinshan.model.Employee;
import com.xinshan.model.PermitNocheck;
import com.xinshan.utils.*;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jonson.xu on 15-3-22.
 */
public class CInterceptor extends HandlerInterceptorAdapter {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean bool = true;
        CommonUtils.init(request);
        String token = request.getParameter("token");

        request.setAttribute("requestTime", DateUtil.currentDate().getTime());
        if (token != null && !token.equals("")) {
            String eplInfo;
            try {
                eplInfo = RedisUtils.hget("websocket", token);
            } catch (Exception e) {
                response.setContentType("application/json");
                ResultData resultData = new ResultData("0x0005", "登录已失效，请重新登录");
                response.getOutputStream().write(JSON.toJSONBytes(resultData));
                return false;
            }
            if (eplInfo != null && !eplInfo.equals("")) {
                JSONObject jsonObject = JSON.parseObject(eplInfo);
                JSONObject eplObject = jsonObject.getJSONObject("linked");
                Employee employee = JSONObject.parseObject(eplObject.toJSONString(), Employee.class);
                request.getSession().setAttribute("employee", employee);
            }
        }
        bool = permitNoCheck(request.getRequestURI());
        if (bool) {
            return true;
        }
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        if (employee == null) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0004", "没有登录"));
            return false;
        }
        //
        if (InspectPermit.check(employee, request.getRequestURI())) {
            return true;
        }else {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0003","没有接口权限"));
            return false;
        }
    }

    private boolean permitNoCheck(String url) {
        List<PermitNocheck> list = PermitComponents.getNoCheckUrl();
        for (PermitNocheck permitNocheck: list) {
            String s = permitNocheck.getUrl();
            if (s.equals(url)){
                return true;
            }else {
                boolean b = matcherUrl(url, s);
                if (b) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matcherUrl(String url, String noCheckUrl) {
        String regexp = "\\{[a-z,A-Z,0-9]*\\}";
        Pattern pattern = Pattern.compile(regexp);
        String[] requestUrls = url.split("/");
        String[] noCheckUrls = noCheckUrl.split("/");
        boolean match = false;
        if (requestUrls.length == noCheckUrls.length) {
            for (int i = 0; i < requestUrls.length; i++) {
                if (requestUrls[i] != null && !requestUrls[i].equals("")) {
                    Matcher matcher = pattern.matcher(noCheckUrls[i]);
                    if (matcher.find() && match) {
                        match = true;
                    } else if (requestUrls[i].equals(noCheckUrls[i])) {
                        match = true;
                    } else {
                        match = false;
                        break;
                    }
                }
            }
        }
        return match;
    }

    private boolean checkToken(HttpServletRequest request) {
        try {
            String token = request.getParameter("token");
            if (token == null || token.equals("")) {
                return false;
            }

            Object o = TokenUtils.getToken(token);
            if (o == null) {
                return false;
            }
            JSONObject jsonObject = JSONObject.parseObject(o.toString());
            Employee employee = JSONObject.parseObject(jsonObject.get("linkeds").toString(), Employee.class);
            request.getSession().setAttribute("employee", employee);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
