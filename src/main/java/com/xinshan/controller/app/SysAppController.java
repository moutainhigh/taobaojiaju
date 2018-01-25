package com.xinshan.controller.app;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.app.AppComponents;
import com.xinshan.components.app.PermitComponents;
import com.xinshan.model.Employee;
import com.xinshan.model.SysApp;
import com.xinshan.model.SysAppButton;
import com.xinshan.model.extend.app.SysAppExtend;
import com.xinshan.service.AppService;
import com.xinshan.service.EmployeeService;
import com.xinshan.utils.Request2ModelUtils;
import com.xinshan.utils.RequestUtils;
import com.xinshan.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mxt on 16-10-10.
 */
@Controller
public class SysAppController {
    @Autowired
    private AppService appService;
    @Autowired
    private EmployeeService employeeService;

    /**
     * 添加菜单
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/sys/app/createApp","/sys/app/updateApp"})
    public void createRole(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SysApp sysApp = Request2ModelUtils.covert(SysApp.class, request);
        if (sysApp.getApp_status() == null) {
            sysApp.setApp_status(1);
        }
        if (sysApp.getApp_id() != null) {
            appService.updateSysApp(sysApp);
        }else {
            appService.createSysApp(sysApp);
        }
        ResponseUtil.sendSuccessResponse(request, response);
        AppComponents.clear();
    }

    @RequestMapping("/sys/app/appDetail")
    public void appDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int app_id = Integer.parseInt(request.getParameter("app_id"));
        SysApp sysApp = appService.getSysAppById(app_id);
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(sysApp));
        if (sysApp.getParent_app() != null && sysApp.getParent_app() > 0) {
            SysApp parentApp = appService.getSysAppById(sysApp.getParent_app());
            jsonObject.put("parent_app_title", parentApp.getApp_title());
        }
        ResponseUtil.sendSuccessResponse(request, response, jsonObject);
    }

    /**
     * 添加菜单下操作
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/sys/app/createButton", "/sys/app/updateButton"})
    public void createAppButton(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SysAppButton appButton = Request2ModelUtils.covert(SysAppButton.class, request);
        if (appButton.getButton_status() == null) {
            appButton.setButton_status(1);
        }
        if (appButton != null && appButton.getSys_app_button_id() != null) {
            appService.updateSysAppButton(appButton);
        }else {
            appService.createSysAppButton(appButton);
        }
        ResponseUtil.sendSuccessResponse(request, response);
        AppComponents.clear();
    }

    @RequestMapping("/sys/app/buttonDetail")
    public void buttonDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int button_id = Integer.parseInt(request.getParameter("sys_app_button_id"));
        SysAppButton sysAppButton  = appService.getButtonById(button_id);
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(sysAppButton));
        if (sysAppButton.getApp_id() != null) {
            SysApp app = appService.getSysAppById(sysAppButton.getApp_id());
            jsonObject.put("app_title", app.getApp_title());
        }
        ResponseUtil.sendSuccessResponse(request, response, jsonObject);
    }

    /**
     * 菜单列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/sys/app/list", "/sys/role/appList"})
    public void appList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String employee_code = employee.getEmployee_code();
        List<SysAppExtend> list = AppComponents.getList(employee_code);
        ResponseUtil.sendSuccessResponse(request, response, list);
    }

    @RequestMapping("/sys/app/appDelete")
    public void appDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int app_id = Integer.parseInt(request.getParameter("app_id"));
        appService.appDelete(app_id);
        ResponseUtil.sendSuccessResponse(request, response);
        AppComponents.clear();
    }

    @RequestMapping("/sys/app/buttonDelete")
    public void buttonDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int button_id = Integer.parseInt(request.getParameter("sys_app_button_id"));
        appService.buttonDelete(button_id);
        ResponseUtil.sendSuccessResponse(request, response);
        AppComponents.clear();
    }

    /**
     * 用户菜单列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sys/app/employeeApps")
    public void employeeAppList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee e = (Employee) request.getSession().getAttribute("employee");
        String employee_code = e.getEmployee_code();
        e = employeeService.getEmployeeByCode(employee_code);
        List<SysAppExtend> list = PermitComponents.getAppByEmployee(e);
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("employee_code", e.getEmployee_code());
        map.put("employee_name", e.getEmployee_name());
        ResponseUtil.sendSuccessResponse(request, response, map);
    }


}
