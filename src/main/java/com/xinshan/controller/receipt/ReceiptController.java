package com.xinshan.controller.receipt;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.model.Employee;
import com.xinshan.service.ReceiptService;
import com.xinshan.utils.RequestUtils;
import com.xinshan.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by mxt on 17-4-25.
 * 收款，退款
 */
@Controller
public class ReceiptController {
    @Autowired
    private ReceiptService receiptService;

    /**
     * 收款
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/receipt/receipt")
    public void receipt(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String postData = RequestUtils.getRequestUtils().postData(request);
        receiptService.receipt(postData, employee);
        ResponseUtil.sendSuccessResponse(request, response, postData);
    }
}
