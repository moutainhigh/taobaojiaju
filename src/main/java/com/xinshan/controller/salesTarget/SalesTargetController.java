package com.xinshan.controller.salesTarget;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.salesTarget.SalesTargetComponent;
import com.xinshan.model.SalesTarget;
import com.xinshan.model.extend.commodity.SampleInExtend;
import com.xinshan.model.extend.salesTarget.SalesTargetAnalysisExtend;
import com.xinshan.model.extend.salesTarget.SalesTargetExtend;
import com.xinshan.pojo.salesTarget.SalesTargetSearchOption;
import com.xinshan.service.SalesTargetService;
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
 * Created by mxt on 17-8-22.
 */
@Controller
public class SalesTargetController {
    @Autowired
    private SalesTargetService salesTargetService;

    /**
     * 添加销售任务
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sales/target/createSalesTarget")
    public void createSalesTarget(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String s = RequestUtils.getRequestUtils().postData(request);
        SalesTargetExtend salesTargetExtend = JSONObject.parseObject(s, SalesTargetExtend.class);
        SalesTargetExtend salesTargetByPosition = salesTargetService.getSalesTargetByPosition(salesTargetExtend.getSales_target_year(), salesTargetExtend.getPosition_id());
        if (salesTargetByPosition != null) {
            salesTargetExtend.setSales_target_id(salesTargetByPosition.getSales_target_id());
            salesTargetService.updateSalesTarget(salesTargetExtend);
        }else {
            salesTargetService.createSalesTarget(salesTargetExtend);
        }
        ResponseUtil.sendSuccessResponse(request, response, salesTargetExtend);
    }

    /**
     * 编辑销售任务
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sales/target/updateSalesTarget")
    public void updateSalesTarget(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String s = RequestUtils.getRequestUtils().postData(request);
        SalesTargetExtend salesTargetExtend = JSONObject.parseObject(s, SalesTargetExtend.class);
        salesTargetService.updateSalesTarget(salesTargetExtend);
        ResponseUtil.sendSuccessResponse(request, response, salesTargetExtend);
    }

    /**
     * 销售任务列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sales/target/salesTargetList")
    public void salesTargetList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SalesTargetSearchOption salesTargetSearchOption = Request2ModelUtils.covert(SalesTargetSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(salesTargetSearchOption);
        List<SalesTargetExtend> list = salesTargetService.salesTargetList(salesTargetSearchOption);
        Integer count = salesTargetService.countSalesTarget(salesTargetSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, salesTargetSearchOption);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sales/target/salesTargetAnalysis")
    public void salesTargetAnalysis(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int year = Integer.parseInt(request.getParameter("year"));
        int month = Integer.parseInt(request.getParameter("month"));
        int position_id = Integer.parseInt(request.getParameter("position_id"));
        SalesTargetComponent.salesTargetAnalysis(year, month, position_id);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sales/target/analysisList")
    public void salesTargetAnalysisList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SalesTargetSearchOption salesTargetSearchOption = Request2ModelUtils.covert(SalesTargetSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(salesTargetSearchOption);
        List<SalesTargetAnalysisExtend> list = salesTargetService.analysisList(salesTargetSearchOption);
        Integer count = salesTargetService.countAnalysis(salesTargetSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, salesTargetSearchOption);
    }
}
