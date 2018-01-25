package com.xinshan.controller.activity;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.model.Employee;
import com.xinshan.model.SpecialRightSell;
import com.xinshan.model.extend.specialRightSell.SpecialRightSellExtend;
import com.xinshan.pojo.SearchOption;
import com.xinshan.pojo.specialRightSell.SpecialRightSellSearchOption;
import com.xinshan.service.SpecialRightSellService;
import com.xinshan.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by mxt on 17-4-20.
 */
@Controller
public class SpecialRightSellController {
    @Autowired
    private SpecialRightSellService specialRightSellService;

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/special/createSpecialRightSell")
    public void createSpecialRightSell(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        SpecialRightSell specialRightSell = Request2ModelUtils.covert(SpecialRightSell.class, request);
        try {
            specialRightSellService.createSpecialRightSell(specialRightSell, employee);
            ResponseUtil.sendSuccessResponse(request, response);
            return;
        }catch (Exception e) {
            e.printStackTrace();
        }
        ResponseUtil.sendResponse(request, response, new ResultData("0x0014", ""));
    }

    @RequestMapping("/activity/special/specialRightSellList")
    public void specialRightSellList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SpecialRightSellSearchOption specialRightSellSearchOption = Request2ModelUtils.covert(SpecialRightSellSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(specialRightSellSearchOption);
        List<SpecialRightSellExtend> list = specialRightSellService.specialRightSells(specialRightSellSearchOption);
        Integer count = specialRightSellService.countSpecialRightSell(specialRightSellSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, specialRightSellSearchOption);
    }
}
