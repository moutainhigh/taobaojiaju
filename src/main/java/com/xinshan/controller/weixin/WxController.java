package com.xinshan.controller.weixin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xinshan.model.User;
import com.xinshan.model.UserCheckCode;
import com.xinshan.model.extend.order.OrderExtend;
import com.xinshan.model.extend.user.UserExtend;
import com.xinshan.service.CheckCodeService;
import com.xinshan.service.OrderService;
import com.xinshan.service.UserService;
import com.xinshan.utils.*;
import com.xinshan.utils.weixin.UserInfo;
import com.xinshan.utils.weixin.WeiXinServiceJSticket;
import com.xinshan.utils.weixin.WeiXinServiceUtils;
import com.xinshan.pojo.checkCode.CheckCodeSearchOption;
import com.xinshan.pojo.order.OrderSearchOption;
import com.xinshan.utils.weixin.WeiXinSignUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by mxt on 16-12-23.
 */
@Controller
public class WxController {
    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;
    @Autowired
    private CheckCodeService checkCodeService;

    /**
     * 微信获取openid
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/wx/userOrder")
    public void openid(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String openid = (String) request.getSession().getAttribute("openid");
        UserExtend user = null;
        if (openid == null) {
            if (request.getParameter("user_id") != null && !"".equals(request.getParameter("user_id"))) {
                user = userService.getUserById(Integer.parseInt(request.getParameter("user_id")));
                openid = user.getUserOpenid().getOpenid();
            }else {
                String code = request.getParameter("code");
                openid = WeiXinServiceUtils.getOpenidByCode(code);
                if (!CommonUtils.TEST) {//正式环境,需判断是否关注服务号
                    UserInfo userInfo = WeiXinServiceUtils.getUserInfo(openid);
                    if (userInfo == null || userInfo.getSubscribe() == null || userInfo.getSubscribe() != 1) {
                        ResponseUtil.sendResponse(request, response, new ResultData("0x0015", "未关注"));
                        return;
                    }
                    Integer[] tagid_list = userInfo.getTagid_list();
                    List<Integer> list = Arrays.asList(tagid_list);
                    if (!list.contains(Integer.valueOf(105))) {//未在订单客户标签下,把用户添加到订单客户标签
                        WeiXinServiceUtils.userTag(openid, 105);
                    }
                }
            }
        }
        request.getSession().setAttribute("openid", openid);
        user = userService.getUserByOpenid(openid);
        if (user == null) {//未绑定
            ResponseUtil.sendResponse(request, response, new ResultData("0x0016"));
        }else {
            OrderSearchOption orderSearchOption = new OrderSearchOption();
            int limit = 5 , currentPage = 1;
            if (request.getParameter("limit") != null && !request.getParameter("limit").equals("")) {
                limit = Integer.parseInt(request.getParameter("limit"));
            }
            if (request.getParameter("currentPage") != null && !request.getParameter("currentPage").equals("")) {
                currentPage = Integer.parseInt(request.getParameter("currentPage"));
            }
            orderSearchOption.setCurrentPage(currentPage);
            orderSearchOption.setLimit(limit);
            orderSearchOption.setStart(orderSearchOption.getLimit() * (orderSearchOption.getCurrentPage() - 1));
            orderSearchOption.setCustomer_phone_number(user.getUser_phone());
            if (orderSearchOption.getOrder_status() == null && orderSearchOption.getOrderStatuses() == null) {
                orderSearchOption.setOrder_status(1);
            }
            List<Integer> orderIds = orderService.orderIds(orderSearchOption);
            int count = orderService.countOrder(orderSearchOption);
            orderSearchOption.setUser_id(user.getUser_id());
            if (orderIds == null || orderIds.size() == 0) {
                ResponseUtil.sendListResponse(request, response, new ArrayList(), orderIds.size(), orderSearchOption);
                return;
            }
            List<OrderExtend> orderExtends = orderService.orderList(orderIds);
            ResponseUtil.sendListResponse(request, response, orderExtends, count, orderSearchOption);
        }
    }

    /**
     * 绑定用户手机
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/wx/userPhone")
    public void userPhone(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String openid = (String) request.getSession().getAttribute("openid");
        if (openid == null) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0005", "登录已失效，请重新登录"));
            return;
        }
        String phone_number = request.getParameter("phone_number");
        if (!PhoneNumberUtil.isChinaPhoneLegal(phone_number)) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0018", "手机号码不正确"));
            return;
        }
        String check_code = request.getParameter("check_code");
        CheckCodeSearchOption checkCodeSearchOption = new CheckCodeSearchOption();
        checkCodeSearchOption.setInvalid_type(0);
        checkCodeSearchOption.setCheck_code(check_code);
        checkCodeSearchOption.setPhone_number(phone_number);
        List<UserCheckCode> userCheckCodes = checkCodeService.checkCodes(checkCodeSearchOption);
        if (userCheckCodes == null || userCheckCodes.size() != 1) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0016", "验证码错误"));
            return;
        }
        UserCheckCode userCheckCode = userCheckCodes.get(0);
        Date date = userCheckCode.getEffective_date();
        int n = (int) (date.getTime()/1000);
        int currentTime = DateUtil.currentTime();
        if (currentTime - n > 60 * 5) {
            userCheckCode.setInvalid_date(DateUtil.currentDate());
            userCheckCode.setInvalid_type(3);
            checkCodeService.updateCheckCode(userCheckCode);
            ResponseUtil.sendResponse(request, response, new ResultData("0x0017", "验证码超时失效"));
            return;
        }

        if (userCheckCode.getInvalid_type()!= null && userCheckCode.getInvalid_type() == 1) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0022", "验证码已使用"));
            return;
        }

        System.out.println("openid:"+openid);
        UserExtend user = userService.getUserByPhone(phone_number);
        if (user == null) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0021", "用户不存在"));
            return;
        }
        if (user != null && user.getUserOpenid().getUser_openid_id() != null && !user.getUserOpenid().getOpenid().equals(openid)) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0019", "手机号已经绑定其他微信"));
            return;
        }
        User user1 = userService.getUserByOpenid(openid);
        if (user1 != null && !user1.getUser_phone().equals(phone_number)) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0020", "此微信已绑定其他手机"));
            return;
        }
        checkCodeService.userCheckCode(userCheckCode, openid, user.getUser_id());
        ResponseUtil.sendSuccessResponse(request, response, user);
    }

    /**
     * 请求验证码
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/wx/userCheckCode")
    public void userCheckCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String phone_number = request.getParameter("phone_number");
        if (!PhoneNumberUtil.isChinaPhoneLegal(phone_number)) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0018", "手机号码不正确"));
            return;
        }
        UserExtend userByPhone = userService.getUserByPhone(phone_number);
        if (userByPhone == null) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0015"));
            return;
        }
        UserCheckCode userCheckCode = checkCodeService.createCheckCode(phone_number);
        if (userCheckCode != null && userCheckCode.getUser_check_code_id() != null) {
            ResponseUtil.sendSuccessResponse(request, response);
        }else {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014"));
        }
    }

    @RequestMapping("/wx/wxUserList")
    public void userList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject jsonObject = WeiXinServiceUtils.userList(null);
        JSONObject jsonObject1 = JSON.parseObject(jsonObject.get("data").toString());
        JSONArray jsonArray = JSON.parseArray(jsonObject1.get("openid").toString());
        List<String[]> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            String openid = jsonArray.get(i).toString();
            UserInfo userInfo = WeiXinServiceUtils.getUserInfo(openid);
            String[] sss = new String[2];
            sss[0] = userInfo.getOpenid();
            sss[1] = userInfo.getNickname();
            list.add(sss);
        }
        ResponseUtil.sendSuccessResponse(request, response, list);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/wx/wxTagList")
    public void tagList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject jsonObject = WeiXinServiceUtils.tagList();
        JSONArray tags = JSONArray.parseArray(jsonObject.get("tags").toString());
        for (int i = 0; i < tags.size(); i++) {
            Object o = tags.get(i);
            System.out.println(o.toString());
        }
        ResponseUtil.sendSuccessResponse(request, response, jsonObject);
    }

    /**
     * 给用户打标签
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/wx/userTag")
    public void userTag(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String openid = request.getParameter("openid");
        int tagid = Integer.parseInt(request.getParameter("tagid"));
        UserInfo userInfo = WeiXinServiceUtils.getUserInfo(openid);
        if(userInfo.getSubscribe() == null || userInfo.getSubscribe() != 1) {//没有关注
            ResponseUtil.sendResponse(request, response, new ResultData("0x0015", "未关注"));
            return;
        }

        Integer[] tagid_list = userInfo.getTagid_list();
        List<Integer> list = Arrays.asList(tagid_list);
        if (list.contains(tagid)) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0016", "已在次标签下"));
            return;
        }

        JSONObject jsonObject = WeiXinServiceUtils.userTag(openid, tagid);
        if (jsonObject.get("errcode").toString().equals("0")) {
            ResponseUtil.sendSuccessResponse(request, response);
        }else {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "失败"));
        }
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/wx/sign")
    public void weixinSign(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String url = request.getParameter("url");
        String ticket = WeiXinServiceJSticket.getTicket();
        Map<String, String> map = WeiXinSignUtils.sign(ticket, url);
        map.put("appid", CommonUtils.WX_SERVICE_APPID);
        ResponseUtil.sendSuccessResponse(request, response, map);
    }

}
