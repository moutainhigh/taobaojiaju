package com.xinshan.controller.activity;

import com.xinshan.model.Employee;
import com.xinshan.model.User;
import com.xinshan.model.ValueAddedCard;
import com.xinshan.model.ValueAddedCardBooking;
import com.xinshan.model.extend.activity.ValueAddedCardBookingExtend;
import com.xinshan.model.extend.activity.ValueAddedCardExtend;
import com.xinshan.pojo.activity.ValueAddedCardSearchOption;
import com.xinshan.service.UserService;
import com.xinshan.service.ValueAddedService;
import com.xinshan.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mxt on 17-4-13.
 */
@Controller
public class ValueAddedController {

    @Autowired
    private ValueAddedService valueAddedService;
    @Autowired
    private UserService userService;

    /**
     * 增值卡预订
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/valueAddedCard/booking")
    public void createValueAddedCardBooking(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int activity_id = Integer.parseInt(request.getParameter("activity_id"));
        String user_name = request.getParameter("user_name");
        String user_phone = request.getParameter("user_phone");
        String user_address = request.getParameter("user_address");
        String booking_remark = request.getParameter("booking_remark");
        String user_second_phone = request.getParameter("user_second_phone");
        ValueAddedCardBooking valueAddedCardBooking = new ValueAddedCardBooking();
        valueAddedCardBooking.setBooking_create_date(DateUtil.currentDate());
        valueAddedCardBooking.setBooking_create_employee_code(employee.getEmployee_code());
        valueAddedCardBooking.setBooking_create_employee_name(employee.getEmployee_name());
        valueAddedCardBooking.setBooking_remark(booking_remark);
        valueAddedCardBooking.setActivity_id(activity_id);
        valueAddedCardBooking.setBooking_status(0);
        User user = userService.createUser(user_name, user_phone, user_address, employee.getEmployee_code(),
                employee.getEmployee_name(), null, null, null, null, user_second_phone);
        valueAddedCardBooking.setUser_id(user.getUser_id());

        ValueAddedCardSearchOption valueAddedCardSearchOption = new ValueAddedCardSearchOption();
        valueAddedCardSearchOption.setUser_id(user.getUser_id());
        valueAddedCardSearchOption.setActivity_id(activity_id);
        List<ValueAddedCardBookingExtend> list = valueAddedService.valueAddedCardBookings(valueAddedCardSearchOption);
        if (list != null && list.size() > 0) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "已经预订"));
            return;
        }

        valueAddedService.createValueAddedCardBooking(valueAddedCardBooking);
        ResponseUtil.sendSuccessResponse(request, response, valueAddedService.getBookingById(valueAddedCardBooking.getValue_added_card_booking_id()));
    }

    /**
     * 预订列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/valueAddedCard/bookingList")
    public void valueAddedCardBookingList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ValueAddedCardSearchOption valueAddedCardSearchOption = Request2ModelUtils.covert(ValueAddedCardSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(valueAddedCardSearchOption);
        List<ValueAddedCardBookingExtend> list = valueAddedService.valueAddedCardBookings(valueAddedCardSearchOption);
        Integer count = valueAddedService.countValueAddedCardBookings(valueAddedCardSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, valueAddedCardSearchOption);
    }

    /**
     * 预订领卡
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/valueAddedCard/takeCard")
    public void valueAddedCardBooking(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String value_added_card_code = request.getParameter("value_added_card_code");
        BigDecimal pay_amount = new BigDecimal(request.getParameter("pay_amount"));
        int value_added_card_booking_id = Integer.parseInt(request.getParameter("value_added_card_booking_id"));
        ValueAddedCardBookingExtend valueAddedCardBookingExtend = valueAddedService.getBookingById(value_added_card_booking_id);
        ValueAddedCard valueAddedCard = valueAddedService.getValueAddedCardByCode(value_added_card_code);
        if (valueAddedCard == null) {
            valueAddedCard = valueAddedService.createValueAddedCard(value_added_card_code, valueAddedCardBookingExtend.getActivity_id(), employee);
        }
        if (valueAddedCard == null || valueAddedCardBookingExtend == null) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", ""));
            return;
        }
        if (valueAddedCard.getCard_enable() == 0) {//增值卡不可用
            return;
        }
        if (valueAddedCard.getOrder_id() != null) {//增值可已使用
            return;
        }
        valueAddedService.takeCard(valueAddedCardBookingExtend, valueAddedCard, pay_amount, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 添加增值卡
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/valueAddedCard/createCard")
    public void createValueAddedCard(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String value_added_card_code = request.getParameter("value_added_card_code");
        int activity_id = Integer.parseInt(request.getParameter("activity_id"));
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        valueAddedService.createValueAddedCard(value_added_card_code, activity_id, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 增值卡列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/valueAddedCard/cardList")
    public void valueAddedCardList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ValueAddedCardSearchOption valueAddedCardSearchOption = Request2ModelUtils.covert(ValueAddedCardSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(valueAddedCardSearchOption);
        List<ValueAddedCardExtend> list = valueAddedService.valueAddedCards(valueAddedCardSearchOption);
        Integer count = valueAddedService.countValueAddedCard(valueAddedCardSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, valueAddedCardSearchOption);
    }

    /**
     * 增值卡退款回收
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/valueAddedCard/recycling")
    public void cardRecycling(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int card_return_status = Integer.parseInt(request.getParameter("card_return_status"));
        int gift_status = Integer.parseInt(request.getParameter("gift_status"));
        BigDecimal return_amount = new BigDecimal(request.getParameter("return_amount"));
        String recycling_remark = request.getParameter("recycling_remark");
        int return_gift_status = Integer.parseInt(request.getParameter("return_gift_status"));

        String value_added_card_code = request.getParameter("value_added_card_code");
        ValueAddedCard valueAddedCard = valueAddedService.getValueAddedCardByCode(value_added_card_code);
        if (valueAddedCard == null || valueAddedCard.getCard_enable() != 1 || valueAddedCard.getCard_return_status() != 1) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", ""));
            return;
        }

        valueAddedCard.setReturn_gift_status(return_gift_status);
        valueAddedCard.setCard_return_status(card_return_status);//已退还
        valueAddedCard.setGift_status(gift_status);
        valueAddedCard.setReturn_amount(return_amount);
        valueAddedCard.setRecycling_remark(recycling_remark);
        valueAddedCard.setRecycling_date(DateUtil.currentDate());
        valueAddedService.recycling(valueAddedCard, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 禁用充值卡
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/valueAddedCard/disable")
    public void valueAddedCardDisable(HttpServletRequest request, HttpServletResponse response)throws IOException {
        int card_enable = Integer.parseInt(request.getParameter("card_enable"));
        String disable_remark = request.getParameter("disable_remark");
        String value_added_card_code = request.getParameter("value_added_card_code");
        ValueAddedCard valueAddedCard = valueAddedService.getValueAddedCardByCode(value_added_card_code);
        if (valueAddedCard == null) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014"));
            return;
        }
        valueAddedCard.setCard_enable(card_enable);
        valueAddedCard.setDisable_date(DateUtil.currentDate());
        valueAddedCard.setDisable_remark(disable_remark);
        valueAddedService.updateValueAddedCard(valueAddedCard);
        ResponseUtil.sendSuccessResponse(request, response);
    }

}
