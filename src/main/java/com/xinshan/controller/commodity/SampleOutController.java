package com.xinshan.controller.commodity;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.supplier.SupplierComponents;
import com.xinshan.model.CommoditySampleOutDetail;
import com.xinshan.model.Employee;
import com.xinshan.model.InventoryOut;
import com.xinshan.model.InventoryOutCommodity;
import com.xinshan.model.extend.commodity.SampleOutExtend;
import com.xinshan.pojo.SearchOption;
import com.xinshan.pojo.commodity.SampleOutSearchOption;
import com.xinshan.service.InventoryInService;
import com.xinshan.service.InventoryOutService;
import com.xinshan.service.SampleOutService;
import com.xinshan.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by mxt on 17-4-22.
 * 出样
 */
@Controller
public class SampleOutController {
    @Autowired
    private SampleOutService sampleOutService;
    /**
     * 下样
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/inventory/sampleOut/sampleOutList")
    public void sampleOutList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        SampleOutSearchOption sampleOutSearchOption = Request2ModelUtils.covert(SampleOutSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(sampleOutSearchOption);
        if (employee.getPosition_id() == 0) {
            /*List<Integer> list = SupplierComponents.concractsSupplierIds(employee.getEmployee_code());
            sampleOutSearchOption.setSupplierIdList(list);*/
            sampleOutSearchOption.setRecord_employee_code(employee.getEmployee_code());
        }
        List<SampleOutExtend> list = sampleOutService.sampleOutList(sampleOutSearchOption);
        Integer count = sampleOutService.countSampleOut(sampleOutSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, sampleOutSearchOption);
    }

    /**
     * 添加出样
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/inventory/sampleOut/createSampleOut")
    public void createSampleOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String postData = RequestUtils.getRequestUtils().postData(request);
        SampleOutExtend sampleOutExtend = JSONObject.parseObject(postData, SampleOutExtend.class);
        sampleOutService.createSampleOut(sampleOutExtend, employee);
        ResponseUtil.sendSuccessResponse(request, response, postData);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/inventory/sampleOut/updateSampleOut")
    public void updateSampleOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String postData = RequestUtils.getRequestUtils().postData(request);
        SampleOutExtend sampleOutExtend = JSONObject.parseObject(postData, SampleOutExtend.class);
        sampleOutService.updateSampleOut(sampleOutExtend);
        ResponseUtil.sendSuccessResponse(request, response, postData);
    }

    /**
     * 审核
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/inventory/sampleOut/confirmSampleOut")
    public void confirmSampleOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int sample_out_id = Integer.parseInt(request.getParameter("commodity_sample_out_id"));
        sampleOutService.confirmSampleOut(sample_out_id, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 下样出库
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/inventory/sampleOut/sampleOutInventoryOut")
    public void sampleOutInventoryOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int sample_out_id = Integer.parseInt(request.getParameter("commodity_sample_out_id"));
        sampleOutService.sampleOutInventoryOut(sample_out_id, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }
}
