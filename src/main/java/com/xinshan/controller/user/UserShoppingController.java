package com.xinshan.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.model.Employee;
import com.xinshan.model.extend.user.UserShoppingExtend;
import com.xinshan.pojo.user.UserSearchOption;
import com.xinshan.service.UserShoppingService;
import com.xinshan.utils.*;
import com.xinshan.utils.websocket.server.UserServerSyncHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by mxt on 17-7-31.
 */
@Controller
public class UserShoppingController {
    @Autowired
    private UserShoppingService userShoppingService;

    /**
     * 添加进店记录
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/user/userShopping/createUserShopping")
    public void createUserShopping(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        UserShoppingExtend userShoppingExtend = Request2ModelUtils.covert(UserShoppingExtend.class, request);
        userShoppingService.createUserShopping(userShoppingExtend, employee);
        ResponseUtil.sendSuccessResponse(request, response);
        Integer user_shopping_id = userShoppingExtend.getUser_shopping_id();
        UserShoppingExtend userShopping = userShoppingService.getUserShoppingById(user_shopping_id);
        UserServerSyncHandler.getUserServerSyncHandler().sendCreateUserShopping(userShopping);
    }

    /**
     * 编辑进店记录
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/user/userShopping/updateUserShopping")
    public void updateUserShopping(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        UserShoppingExtend userShoppingExtend = Request2ModelUtils.covert(UserShoppingExtend.class, request);
        userShoppingService.updateUserShopping(userShoppingExtend, employee);
        ResponseUtil.sendSuccessResponse(request, response);
        Integer user_shopping_id = userShoppingExtend.getUser_shopping_id();
        UserShoppingExtend userShopping = userShoppingService.getUserShoppingById(user_shopping_id);
        UserServerSyncHandler.getUserServerSyncHandler().sendUpdateUserShopping(userShopping);
    }

    /**
     * 进店记录列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/user/userShopping/userShoppingList")
    public void userShoppingList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        UserSearchOption userSearchOption = Request2ModelUtils.covert(UserSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(userSearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(userSearchOption);

        SearchOptionUtil.getSearchOptionUtil().positionLimit(userSearchOption, employee);

        List<Integer> userShoppingIds = userShoppingService.userShoppingIds(userSearchOption);
        List<UserShoppingExtend> list = userShoppingService.userShoppingList(userShoppingIds);
        Integer count = userShoppingService.countUserShopping(userSearchOption);

        ResponseUtil.sendListResponse(request, response, list, count, userSearchOption);
    }

    /**
     * 生成订单
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/user/userShopping/createOrder")
    public void userShoppingOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int user_shopping_id = Integer.parseInt(request.getParameter("user_shopping_id"));
        UserShoppingExtend userShopping = userShoppingService.getUserShoppingById(user_shopping_id);
        if (userShopping.getGenerate_order() != null && userShopping.getGenerate_order() == 1) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "已经生成订单"));
            return;
        }

        userShoppingService.createOrder(userShopping);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 生成用户
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/user/userShopping/createUser")
    public void userShoppingCreateUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int user_shopping_id = Integer.parseInt(request.getParameter("user_shopping_id"));
        UserShoppingExtend userShopping = userShoppingService.getUserShoppingById(user_shopping_id);
        if (userShopping.getGenerate_order() != null && userShopping.getGenerate_order() == 1) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "已经生成订单"));
            return;
        }

        userShoppingService.createUser(userShopping);
        ResponseUtil.sendSuccessResponse(request, response);
    }
}
