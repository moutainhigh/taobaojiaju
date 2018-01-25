package com.xinshan.controller.inventory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.xinshan.model.Employee;
import com.xinshan.model.extend.inventory.InventoryHistoryExtend;
import com.xinshan.model.extend.inventory.InventoryInExtend;
import com.xinshan.service.InventoryInService;
import com.xinshan.service.PurchaseService;
import com.xinshan.utils.*;
import com.xinshan.pojo.inventory.InventorySearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 入库
 * Created by mxt on 16-11-9.
 */
@Controller
public class InventoryInController {
    @Autowired
    private InventoryInService inventoryInService;

    /**
     * 入库
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/order/inventoryIn/confirmInventoryIn"})
    public void confirmInventoryIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String postData = RequestUtils.getRequestUtils().postData(request);
        JSONArray jsonArray = JSON.parseArray(postData);
        inventoryInService.confirmInventoryIn(jsonArray, employee);
        ResponseUtil.sendSuccessResponse(request, response, postData);
    }

    /**
     * 入库记录
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/order/inventoryIn/inventoryHistory", "/order/inventoryOut/inventoryHistory"})
    public void inventoryInCommodityDetails(HttpServletRequest request, HttpServletResponse response) throws IOException {
        InventorySearchOption inventorySearchOption = Request2ModelUtils.covert(InventorySearchOption.class, request);
        List<InventoryHistoryExtend> list = inventoryInService.inventoryHistories(inventorySearchOption);
        ResponseUtil.sendSuccessResponse(request, response, list);
    }

    /**
     * 入库单列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/inventoryIn/inventoryInList")
    public void inventoryList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        InventorySearchOption inventorySearchOption = Request2ModelUtils.covert(InventorySearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(inventorySearchOption);
        List<Integer> inventoryIds = inventoryInService.inventoryInIds(inventorySearchOption);
        if (inventoryIds != null && inventoryIds.size() > 0) {
            inventorySearchOption.setInventoryInIds(inventoryIds);
            List<InventoryInExtend> list = inventoryInService.inventoryInList(inventorySearchOption);
            int count = inventoryInService.countInventoryIn(inventorySearchOption);
            ResponseUtil.sendListResponse(request, response, list, count, inventorySearchOption);
        } else {
            ResponseUtil.sendListResponse(request, response, new ArrayList(), 0, inventorySearchOption);
        }
    }

    /**
     * 入库单详情
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/inventoryIn/inventoryDetail")
    public void inventoryDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = (Employee) request.getSession().getAttribute("employee");
        InventorySearchOption inventorySearchOption = new InventorySearchOption();
        int inventory_in_id = Integer.parseInt(request.getParameter("inventory_in_id"));
        List<Integer> inventoryIds = new ArrayList<>();
        inventoryIds.add(inventory_in_id);
        inventorySearchOption.setInventoryInIds(inventoryIds);
        List<InventoryInExtend> list = inventoryInService.inventoryInList(inventorySearchOption);
        if (list != null && list.size() > 0){
            InventoryInExtend inventoryInExtend = list.get(0);
            inventoryInExtend.setPrintEmployee(employee);
            ResponseUtil.sendSuccessResponse(request, response, inventoryInExtend);
        }else {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "id不正确"));
        }
    }

    /**
     * 添加入库
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/inventoryIn/createInventoryIn")
    public void inventoryIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String postData = RequestUtils.getRequestUtils().postData(request);
        JSONArray jsonArray = JSON.parseArray(postData);
        inventoryInService.createInventoryIn(jsonArray, employee);
        ResponseUtil.sendSuccessResponse(request, response, postData);
    }

    /**
     * 出入库列表
     * @param request
     * @param response
     * @throws IOException
     */
    /*@RequestMapping("/order/inventoryIn/inventoryHistoryList")
    public void inventoryHistoryList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        InventorySearchOption inventorySearchOption = Request2ModelUtils.covert(InventorySearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(inventorySearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(inventorySearchOption);
        List<InventoryHistoryExtend> list = inventoryInService.inventoryHistoryExtends(inventorySearchOption);
        Integer count = inventoryInService.countInventoryHistory(inventorySearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, inventorySearchOption);
    }*/
}
