package com.xinshan.controller.activity;

import com.xinshan.model.BrandBoost;
import com.xinshan.model.Employee;
import com.xinshan.model.User;
import com.xinshan.model.extend.activity.BrandBoostExtend;
import com.xinshan.pojo.activity.BrandBoostSearchOption;
import com.xinshan.service.BrandBoostService;
import com.xinshan.service.UserService;
import com.xinshan.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by mxt on 17-4-19.
 */
@Controller
public class BrandBoostController {

    @Autowired
    private BrandBoostService brandBoostService;

    @Autowired
    private UserService userService;

    @RequestMapping("/activity/brandBoost/createBrandBoost")
    public void createBrandBoost(HttpServletRequest request, HttpServletResponse response)throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        BrandBoost brandBoost = Request2ModelUtils.covert(BrandBoost.class, request);
        String user_name = request.getParameter("user_name");
        String user_phone = request.getParameter("user_phone");
        String user_address = request.getParameter("user_address");
        String user_second_phone = request.getParameter("user_second_phone");
        User user = userService.createUser(user_name, user_phone, user_address, employee.getEmployee_code(),
                employee.getEmployee_name(), null, null, null, null, user_second_phone);
        brandBoost.setUser_id(user.getUser_id());
        brandBoost.setBrand_boost_create_employee_code(employee.getEmployee_code());
        brandBoost.setBrand_boost_create_employee_name(employee.getEmployee_name());
        brandBoost.setBrand_boost_create_date(DateUtil.currentDate());
        brandBoostService.createBrandBoost(brandBoost);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    @RequestMapping("/activity/brandBoost/brandBoostList")
    public void brandBoostList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BrandBoostSearchOption brandBoostSearchOption =  Request2ModelUtils.covert(BrandBoostSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(brandBoostSearchOption);
        List<BrandBoostExtend> list = brandBoostService.brandBoostList(brandBoostSearchOption);
        Integer count = brandBoostService.countBrandBoost(brandBoostSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, brandBoostSearchOption);
    }
}
