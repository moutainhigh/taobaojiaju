package com.xinshan.controller.inventory;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.model.Employee;
import com.xinshan.model.extend.inventory.InventoryMoveExtend;
import com.xinshan.service.InventoryMoveService;
import com.xinshan.utils.Request2ModelUtils;
import com.xinshan.utils.RequestUtils;
import com.xinshan.utils.ResponseUtil;
import com.xinshan.utils.SearchOptionUtil;
import com.xinshan.pojo.inventory.InventorySearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by mxt on 17-2-7.
 */
@Controller
public class InventoryMoveController {
    @Autowired
    private InventoryMoveService inventoryMoveService;

    /**
     * 库存调拨记录列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/inventory/inventoryMoveList")
    public void inventoryMove(HttpServletRequest request, HttpServletResponse response) throws IOException {
        InventorySearchOption inventorySearchOption = Request2ModelUtils.covert(InventorySearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(inventorySearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(inventorySearchOption);
        List<InventoryMoveExtend> list = inventoryMoveService.inventoryMoveList1(inventorySearchOption);
        Integer count = inventoryMoveService.countInventoryMove(inventorySearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, inventorySearchOption);
    }

    /**
     * 添加库存调拨
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/inventory/createInventoryMove")
    public void inventoryMoveCreate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String postData = RequestUtils.getRequestUtils().postData(request);
        InventoryMoveExtend inventoryMoveExtend = JSONObject.parseObject(postData, InventoryMoveExtend.class);
        inventoryMoveService.createInventoryMove(inventoryMoveExtend, employee);
        ResponseUtil.sendSuccessResponse(request, response, inventoryMoveExtend, postData);
    }

    /**
     * 确认库存调拨
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/inventory/createInventoryConfirm")
    public void inventoryMoveConfirm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int inventory_move_id = Integer.parseInt(request.getParameter("inventory_move_id"));
        inventoryMoveService.confirmInventoryMove(inventory_move_id, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

}
