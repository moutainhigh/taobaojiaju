package com.xinshan.controller.statistics;

import com.xinshan.model.extend.statistics.SaleRank;
import com.xinshan.service.StatisticsService;
import com.xinshan.utils.Request2ModelUtils;
import com.xinshan.utils.ResponseUtil;
import com.xinshan.utils.SearchOptionUtil;
import com.xinshan.pojo.statistics.StatisticsSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by mxt on 17-3-13.
 */
@Controller
public class SaleRankController {
    @Autowired
    private StatisticsService statisticsService;

    /**
     * 商品销售排行
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/statistics/rank/commoditySaleRank")
    public void commoditySaleRank(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StatisticsSearchOption statisticsSearchOption = Request2ModelUtils.covert(StatisticsSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(statisticsSearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(statisticsSearchOption);
        List<SaleRank> list = statisticsService.commoditySaleRank(statisticsSearchOption);
        Integer count = statisticsService.countCommoditySaleRank(statisticsSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, statisticsSearchOption);
    }

    /**
     * 职员销售排行
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/statistics/rank/employeeSaleRank")
    public void employeeSaleRank(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StatisticsSearchOption statisticsSearchOption = Request2ModelUtils.covert(StatisticsSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(statisticsSearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(statisticsSearchOption);
        List<SaleRank> list = statisticsService.employeeSaleRank(statisticsSearchOption);
        Integer count = statisticsService.countEmployeeSaleRank(statisticsSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, statisticsSearchOption);
    }

    /**
     * 店面销售排行
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/statistics/rank/positionSaleRank")
    public void positionSaleRank(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StatisticsSearchOption statisticsSearchOption = Request2ModelUtils.covert(StatisticsSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(statisticsSearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(statisticsSearchOption);
        List<SaleRank> list = statisticsService.positionSaleRank(statisticsSearchOption);
        Integer count = statisticsService.countPositionSaleRank(statisticsSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, statisticsSearchOption);
    }

    /**
     * 品牌销售排行
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/statistics/rank/supplierSaleRank")
    public void supplierSaleRank(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StatisticsSearchOption statisticsSearchOption = Request2ModelUtils.covert(StatisticsSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(statisticsSearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(statisticsSearchOption);
        List<SaleRank> list = statisticsService.supplierSaleRank(statisticsSearchOption);
        Integer count = statisticsService.countSupplierSaleRank(statisticsSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, statisticsSearchOption);
    }

    /**
     * 品类销售排行
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/statistics/rank/categorySaleRank")
    public void categorySaleRank(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StatisticsSearchOption statisticsSearchOption = Request2ModelUtils.covert(StatisticsSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(statisticsSearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(statisticsSearchOption);
        List<SaleRank> list = statisticsService.categorySaleRank(statisticsSearchOption);
        Integer count = statisticsService.countCategorySaleRank(statisticsSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, statisticsSearchOption);
    }
}
