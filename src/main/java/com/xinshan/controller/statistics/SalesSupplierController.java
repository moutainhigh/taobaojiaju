package com.xinshan.controller.statistics;

import com.xinshan.components.statistics.SalesSupplierComponent;
import com.xinshan.components.supplier.SupplierComponents;
import com.xinshan.model.SalesContacts;
import com.xinshan.model.SalesSupplier;
import com.xinshan.model.extend.statistics.SalesSupplierExtend;
import com.xinshan.pojo.statistics.StatisticsSearchOption;
import com.xinshan.service.SalesSupplierService;
import com.xinshan.utils.*;
import com.xinshan.utils.thread.SalesSupplierInit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mxt on 17-9-5.
 */
@Controller
public class SalesSupplierController {

    @Autowired
    private SalesSupplierService salesSupplierService;

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/statistics/sales/salesSupplierList")
    public void salesSupplierList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StatisticsSearchOption statisticsSearchOption = Request2ModelUtils.covert(StatisticsSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(statisticsSearchOption);
        List<SalesSupplierExtend> list = salesSupplierService.salesSupplierList(statisticsSearchOption);
        Integer count = salesSupplierService.countSalesSupplier(statisticsSearchOption);
        BigDecimal amount = new BigDecimal("0");
        if (statisticsSearchOption.getMonth() != null) {
            amount = salesSupplierService.monthSupplierAmount(statisticsSearchOption.getMonth());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("count", count);
        int allPage = count/statisticsSearchOption.getLimit();
        if (count % statisticsSearchOption.getLimit() > 0) {
            allPage++;
        }
        map.put("allPage", allPage);
        map.put("query", statisticsSearchOption);
        map.put("amount", amount);
        ResponseUtil.sendSuccessResponse(request, response, map);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/statistics/sales/salesContactsList")
    public void salesContactsList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StatisticsSearchOption statisticsSearchOption = Request2ModelUtils.covert(StatisticsSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(statisticsSearchOption);
        List<SalesContacts> list = salesSupplierService.salesContactsList(statisticsSearchOption);
        Integer count = salesSupplierService.countSalesContacts(statisticsSearchOption);
        BigDecimal amount = new BigDecimal("0");
        if (statisticsSearchOption.getMonth() != null) {
            amount = salesSupplierService.monthContactsAmount(statisticsSearchOption.getMonth());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("count", count);
        int allPage = count/statisticsSearchOption.getLimit();
        if (count % statisticsSearchOption.getLimit() > 0) {
            allPage++;
        }
        map.put("allPage", allPage);
        map.put("query", statisticsSearchOption);
        map.put("amount", amount);
        ResponseUtil.sendSuccessResponse(request, response, map);
    }

    @RequestMapping("/statistics/sales/salesSupplierInit")
    public void salesSupplierInit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String month = request.getParameter("month");
        if (SalesSupplierInit.status.get()) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "正在计算请稍等"));
            return;
        }
        SalesSupplierInit salesSupplierInit = new SalesSupplierInit(month);
        ThreadPool threadPool = new ThreadPool(1);
        threadPool.getExecutorService().submit(salesSupplierInit);
        threadPool.getExecutorService().shutdown();
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/statistics/sales/salesSupplierReset")
    public void salesSupplierReset(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int supplier_id = Integer.parseInt(request.getParameter("supplier_id"));
        String month = request.getParameter("month");
        SalesSupplier salesSupplier = salesSupplierService.getSalesSupplier(supplier_id, month);
        if (salesSupplier == null) {
            salesSupplier = new SalesSupplier();
            salesSupplier.setSupplier_id(supplier_id);
            salesSupplier.setMonth(month);
            salesSupplier.setContacts(SupplierComponents.getSupplierMap().get(supplier_id).getContacts());
        }

        SalesSupplierComponent.salesSupplier(salesSupplier);
        salesSupplierService.createSalesSupplier(salesSupplier);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/statistics/sales/salesContactsReset")
    public void salesContactsReset(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String month = request.getParameter("month");
        String contacts = request.getParameter("contacts");
        SalesContacts salesContacts = salesSupplierService.getSalesContacts(contacts, month);
        if (salesContacts == null) {
            salesContacts = new SalesContacts();
            salesContacts.setMonth(month);
            salesContacts.setContacts(contacts);
        }

        SalesSupplierComponent.salesContacts(salesContacts);
        salesSupplierService.createSalesContacts(salesContacts);
        ResponseUtil.sendSuccessResponse(request, response);
    }
}
