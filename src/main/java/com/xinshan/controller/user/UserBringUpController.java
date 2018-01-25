package com.xinshan.controller.user;

import com.xinshan.components.user.UserBringUpSettingComponent;
import com.xinshan.model.Employee;
import com.xinshan.model.UserBringUp;
import com.xinshan.model.UserBringUpCashBack;
import com.xinshan.model.UserBringUpSetting;
import com.xinshan.model.extend.user.UserBringUpExtend;
import com.xinshan.model.extend.user.UserExtend;
import com.xinshan.pojo.user.UserSearchOption;
import com.xinshan.service.UserBringUpService;
import com.xinshan.service.UserService;
import com.xinshan.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mxt on 17-6-9.
 */
@Controller
public class UserBringUpController {

    @Autowired
    private UserBringUpService userBringUpService;
    @Autowired
    private UserService userService;

    /**
     * 添加老带新
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/user/bringUp/createBringUp")
    public void createBringUp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        UserBringUp userBringUp = Request2ModelUtils.covert(UserBringUp.class, request);
        if (userBringUp.getNew_user_id().equals(userBringUp.getOld_user_id())) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", ""));
            return;
        }
        userBringUp.setUser_bring_up_create_date(DateUtil.currentDate());
        userBringUp.setUser_bring_up_create_employee_code(employee.getEmployee_code());
        userBringUp.setUser_bring_up_create_employee_name(employee.getEmployee_name());
        UserExtend userById = userService.getUserById(userBringUp.getNew_user_id());
        List<UserBringUpExtend> userBringUpExtends = userBringUpService.newUserBringUpIds(userById.getUser_phone());
        if (userBringUpExtends != null && userBringUpExtends.size() > 0) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0015", "次用户已经被带"));
            return;
        }
        userBringUpService.createBringUp(userBringUp);
        ResponseUtil.sendSuccessResponse(request, response, userBringUp);
    }

    /**
     * 老带新列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/user/bringUp/bringUpList")
    public void bringUpList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserSearchOption userSearchOption = Request2ModelUtils.covert(UserSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(userSearchOption);
        List<Integer> bringUpIds = userBringUpService.bringUpIds(userSearchOption);
        if (bringUpIds == null || bringUpIds.size() == 0) {
            ResponseUtil.sendListResponse(request, response, new ArrayList(), 0, userSearchOption);
            return;
        }

        userSearchOption.setBringUpIds(bringUpIds);
        List<UserBringUpExtend> list = userBringUpService.bringUpList(userSearchOption);
        int count = bringUpIds.size();
        ResponseUtil.sendListResponse(request, response, list, count, userSearchOption);
    }

    /**
     * 判断用户是否已被带
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/user/bringUp/newUserBringUp")
    public void bringUpUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String phone_number = request.getParameter("phone_number");
        List<UserBringUpExtend> userBringUpExtends = userBringUpService.newUserBringUpIds(phone_number);
        ResponseUtil.sendSuccessResponse(request, response, userBringUpExtends);
    }

    /**
     * 添加设置
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/user/bringUp/createSetting")
    public void createBringUpSetting(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        UserBringUpSetting userBringUpSetting = Request2ModelUtils.covert(UserBringUpSetting.class, request);
        userBringUpService.createBringUpSetting(userBringUpSetting, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 设置
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/user/bringUp/setting")
    public void bringUpSetting(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseUtil.sendSuccessResponse(request, response, UserBringUpSettingComponent.getUserBringUpSetting());
    }

    /**
     * 返点
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/user/bringUp/createBringUpCashBack")
    public void createBringUpCashBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserBringUpCashBack bringUpCashBack = Request2ModelUtils.covert(UserBringUpCashBack.class, request);
        userBringUpService.createBringUpCashBack(bringUpCashBack);
        ResponseUtil.sendSuccessResponse(request, response);
    }
}
