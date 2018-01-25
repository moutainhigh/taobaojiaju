package com.xinshan.controller.commodity;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.supplier.SupplierComponents;
import com.xinshan.model.Commodity;
import com.xinshan.model.Employee;
import com.xinshan.model.extend.commodity.SampleInDetailExtend;
import com.xinshan.model.extend.commodity.SampleInExtend;
import com.xinshan.pojo.commodity.SampleInSearchOption;
import com.xinshan.service.CommodityService;
import com.xinshan.service.SampleInService;
import com.xinshan.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by mxt on 17-5-15.
 * 上样
 */
@Controller
public class SampleInController {
    @Autowired
    private CommodityService commodityService;
    @Autowired
    private SampleInService sampleInService;


    private boolean sampleCommodityCheck(SampleInExtend sampleInExtend) {
        List<SampleInDetailExtend> sampleInDetailExtends = sampleInExtend.getSampleInDetailExtends();
        Integer sample_in_supplier_id = sampleInExtend.getSample_in_supplier_id();
        for (int i = 0; i < sampleInDetailExtends.size(); i++) {
            SampleInDetailExtend sampleInDetailExtend = sampleInDetailExtends.get(i);
            Commodity commodity = commodityService.getCommodityById(sampleInDetailExtend.getCommodity_id());
            if (!commodity.getSupplier_id().equals(sample_in_supplier_id)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 添加上样单
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/inventory/sampleIn/createSampleIn")
    public void createSampleIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String postData = RequestUtils.getRequestUtils().postData(request);
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        SampleInExtend sampleInExtend = JSONObject.parseObject(postData, SampleInExtend.class);
        if (!sampleCommodityCheck(sampleInExtend)) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014"));
            return;
        }
        sampleInService.createSampleIn(sampleInExtend, employee);
        ResponseUtil.sendSuccessResponse(request, response, postData);
    }

    /**
     * 编辑上样单
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/inventory/sampleIn/updateSampleIn")
    public void updateSampleIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String postData = RequestUtils.getRequestUtils().postData(request);
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        SampleInExtend sampleInExtend = JSONObject.parseObject(postData, SampleInExtend.class);
        if (!sampleCommodityCheck(sampleInExtend)) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014"));
            return;
        }
        sampleInService.updateSampleIn(sampleInExtend, employee);
        ResponseUtil.sendSuccessResponse(request, response, postData);
    }

    /**
     * 上样列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/inventory/sampleIn/sampleInList")
    public void sampleInList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        SampleInSearchOption sampleInSearchOption = Request2ModelUtils.covert(SampleInSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(sampleInSearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(sampleInSearchOption);
        if (employee.getPosition_id() == 0) {
            /*List<Integer> list = SupplierComponents.concractsSupplierIds(employee.getEmployee_code());
            sampleInSearchOption.setSupplierIdList(list);*/
            sampleInSearchOption.setRecord_employee_code(employee.getEmployee_code());
        }
        List<SampleInExtend> list = sampleInService.sampleInExtends(sampleInSearchOption);
        Integer count = sampleInService.countSampleIn(sampleInSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, sampleInSearchOption);
    }

    /**
     * 确认
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/inventory/sampleIn/sampleInConfirm")
    public void sampleInConfirm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int commodity_sample_in_id = Integer.parseInt(request.getParameter("commodity_sample_in_id"));
        SampleInExtend sampleInExtend = sampleInService.sampleInExtend(commodity_sample_in_id);
        sampleInService.sampleInConfirm(sampleInExtend, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 确认
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/inventory/sampleIn/sampleInConfirm1")
    public void sampleInConfirm1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int commodity_sample_in_id = Integer.parseInt(request.getParameter("commodity_sample_in_id"));
        int commodity_store_id = Integer.parseInt(request.getParameter("commodity_store_id"));

        SampleInExtend sampleInExtend = sampleInService.sampleInExtend(commodity_sample_in_id);
        sampleInExtend.setCommodity_store_id(commodity_store_id);
        sampleInService.updateSampleIn(sampleInExtend);

        sampleInExtend = sampleInService.sampleInExtend(commodity_sample_in_id);
        sampleInService.sampleInConfirm(sampleInExtend, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 上样入库
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/inventory/sampleIn/sampleIn")
    public void sampleIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int commodity_sample_in_id = Integer.parseInt(request.getParameter("commodity_sample_in_id"));
        SampleInExtend sampleInExtend = sampleInService.sampleInExtend(commodity_sample_in_id);
        sampleInService.sampleInInventoryIn(sampleInExtend, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }
}
