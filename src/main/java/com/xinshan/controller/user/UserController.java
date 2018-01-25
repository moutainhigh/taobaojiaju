package com.xinshan.controller.user;

import com.xinshan.model.Employee;
import com.xinshan.model.User;
import com.xinshan.model.extend.user.UserExtend;
import com.xinshan.service.UserService;
import com.xinshan.utils.*;
import com.xinshan.pojo.user.UserSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by mxt on 16-10-21.
 */
@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping({"/user/user/createUser","/user/user/updateUser"})
    public void createUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = Request2ModelUtils.covert(User.class, request);
        if (user.getUser_create_date() == null) {
            user.setUser_create_date(DateUtil.currentDate());
        }
        String user_phone = user.getUser_phone();
        boolean chinaPhoneLegal = PhoneNumberUtil.isChinaPhoneLegal(user_phone);
        if (!chinaPhoneLegal) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0015", "手机号不正确"));
            return;
        }
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        user.setUser_employee_code(employee.getEmployee_code());
        user.setUser_employee_name(employee.getEmployee_name());
        if (user.getUser_id() == null) {
            UserExtend userExtend = userService.getUserByPhone(user.getUser_phone());
            if (userExtend != null) {
                ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "手机号重复"));
                return;
            }
            userService.createUser(user);
            ResponseUtil.sendSuccessResponse(request, response);
        }else {
            if (userService.updateUser(user)) {
                ResponseUtil.sendSuccessResponse(request, response);
            }else {
                ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "手机号重复"));
            }
        }
    }

    @RequestMapping({"/user/user/userList", "/user/workRecord/userList", "/order/order/userList"})
    public void userList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserSearchOption userSearchOption = Request2ModelUtils.covert(UserSearchOption.class, request);
        Employee employee = (Employee) request.getSession().getAttribute("employee");
        if (userSearchOption.getAll() == null || userSearchOption.getAll() != 1) {
            SearchOptionUtil.getSearchOptionUtil().positionLimit(userSearchOption, employee);
        }
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(userSearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(userSearchOption);
        List<UserExtend> list = userService.userList(userSearchOption);
        Integer count = userService.countUser(userSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, userSearchOption);
    }

}
