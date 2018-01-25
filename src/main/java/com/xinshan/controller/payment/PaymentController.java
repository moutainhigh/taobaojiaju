package com.xinshan.controller.payment;

import com.xinshan.model.Employee;
import com.xinshan.model.Payment;
import com.xinshan.model.extend.payment.PaymentExtend;
import com.xinshan.service.PaymentService;
import com.xinshan.utils.*;
import com.xinshan.pojo.payment.PaymentSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by mxt on 17-3-8.
 */
@Controller
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/settlement/payment/createPayment")
    public void createPayment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Payment payment = Request2ModelUtils.covert(Payment.class, request);
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        paymentService.createPayment(payment, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/settlement/payment/paymentList")
    public void paymentList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PaymentSearchOption paymentSearchOption =  Request2ModelUtils.covert(PaymentSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(paymentSearchOption);
        List<PaymentExtend> list = paymentService.paymentList(paymentSearchOption);
        Integer count = paymentService.countPayment(paymentSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, paymentSearchOption);
    }
}
