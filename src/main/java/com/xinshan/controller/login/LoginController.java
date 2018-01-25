package com.xinshan.controller.login;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.activity.ActivityComponents;
import com.xinshan.components.app.AppComponents;
import com.xinshan.components.app.PermitComponents;
import com.xinshan.components.category.CategoryComponent;
import com.xinshan.components.position.PositionComponent;
import com.xinshan.components.role.RoleComponents;
import com.xinshan.components.supplier.SupplierComponents;
import com.xinshan.model.Employee;
import com.xinshan.service.EmployeeService;
import com.xinshan.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jonson.xu on 15-5-2.
 */
@Controller
public class LoginController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 用户登录
     *
     * @param response
     * @param request
     * @return
     */
    @RequestMapping({"/login", "/login/supplier"})
    public void loginAction(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String employee_code = request.getParameter("employee_code");
        String employee_password = request.getParameter("employee_password");
        if (employee_code == null || employee_password == null || employee_password.equals("")) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x00014", "用户名或密码为空"));
            return;//用户名或密码不能为空
        }

        Employee employee = employeeService.getEmployeeByCode1(employee_code);
        if (employee == null || employee.getEmployee_status() != 1 || !employee.getEmployee_password().equals(EncryptionUtils.encryption(employee_password))) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x00015", "用户名或密码错误"));
            return;//用户名或密码错误
        }
        Date date = DateUtil.currentDate();
        String token = EncryptionUtils.get32BitMD5(employee.getEmployee_code() + employee.getEmployee_password() + date.getTime());
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(employee));
        boolean result = TokenUtils.setToken(token, jsonObject);
        if (result) {//登录成功
            request.getSession().setAttribute("employee", employee);
            ResponseUtil.sendSuccessResponse(request, response, token);
        }else {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0008", "登录失败!"));
        }
    }

    /**
     * 登出
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/logout")
    public void logoutAction(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().removeAttribute("employee");
        ResponseUtil.sendSuccessResponse(request, response);
    }

    @RequestMapping("/isLogin")
    public void isLogin(HttpServletResponse response, HttpServletRequest request) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        if (employee == null) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0000"));
        }else {
            Map<String, Object> map = new HashMap<>();
            map.put("employee", employee);
            ResponseUtil.sendSuccessResponse(request, response, map);
        }
    }

    @RequestMapping("/reset")
    public void reset(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivityComponents.clear();
        AppComponents.clear();
        PermitComponents.clear();
        CategoryComponent.clear();
        RoleComponents.clear();
        PositionComponent.clear();
        SupplierComponents.clear();
        ResponseUtil.sendSuccessResponse(request, response);
    }
}
