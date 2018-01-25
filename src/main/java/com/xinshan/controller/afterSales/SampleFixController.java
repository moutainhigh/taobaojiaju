package com.xinshan.controller.afterSales;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.orderFee.OrderFeeComponents;
import com.xinshan.model.Employee;
import com.xinshan.model.Settlement;
import com.xinshan.model.extend.afterSales.SampleFixExtend;
import com.xinshan.model.extend.commodity.SampleOutExtend;
import com.xinshan.model.extend.orderFee.OrderFeeExtend;
import com.xinshan.pojo.afterSales.SampleFixSearchOption;
import com.xinshan.pojo.commodity.SampleOutSearchOption;
import com.xinshan.service.SampleFixService;
import com.xinshan.service.SettlementService;
import com.xinshan.utils.Request2ModelUtils;
import com.xinshan.utils.RequestUtils;
import com.xinshan.utils.ResponseUtil;
import com.xinshan.utils.SearchOptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by mxt on 17-4-21.
 */
@Controller
public class SampleFixController {

    @Autowired
    private SampleFixService sampleFixService;
    @Autowired
    private SettlementService settlementService;

    /**
     * 添加场地维修
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/afterSales/sampleFix/createSampleFix")
    public void createSampleFix(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String postData = RequestUtils.getRequestUtils().postData(request);
        SampleFixExtend sampleFixExtend = JSONObject.parseObject(postData, SampleFixExtend.class);
        sampleFixService.createSampleFix(sampleFixExtend, employee);
        ResponseUtil.sendSuccessResponse(request, response, postData);
    }

    /**
     * 场地维修列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/afterSales/sampleFix/sampleFixList")
    public void sampleFixList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SampleFixSearchOption sampleFixSearchOption = Request2ModelUtils.covert(SampleFixSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(sampleFixSearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(sampleFixSearchOption);
        List<SampleFixExtend> list = sampleFixService.sampleFixList(sampleFixSearchOption);
        Integer count = sampleFixService.countSampleFix(sampleFixSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, sampleFixSearchOption);
    }

    /**
     * 费用审核
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/afterSales/sampleFix/checkSampleFix")
    public void checkSampleFix(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int sample_fix_id = Integer.parseInt(request.getParameter("sample_fix_id"));
        sampleFixService.sampleFixFeeCheck(sample_fix_id, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 场地维修结算
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/afterSales/sampleFix/sampleFixSettlement")
    public void sampleFixSettlement(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int sample_fix_id = Integer.parseInt(request.getParameter("sample_fix_id"));
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        settlementService.createSettlementSampleFix(sample_fix_id, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }
}
