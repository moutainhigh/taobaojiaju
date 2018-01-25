package com.xinshan.controller.inventory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xinshan.model.Employee;
import com.xinshan.model.Order;
import com.xinshan.model.OrderCommodity;
import com.xinshan.model.extend.inventory.InventoryOutCommodityExtend;
import com.xinshan.model.extend.inventory.InventoryOutExtend;
import com.xinshan.pojo.commodity.CommoditySearchOption;
import com.xinshan.pojo.inventory.InventorySearchOption;
import com.xinshan.service.ActivityService;
import com.xinshan.service.InventoryOutService;
import com.xinshan.service.InventoryService;
import com.xinshan.service.OrderService;
import com.xinshan.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by mxt on 16-11-11.
 */
@Controller
public class InventoryOutController {
    @Autowired
    private InventoryOutService inventoryOutService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ActivityService activityService;

    /**
     * 出库列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/inventoryOut/inventoryOutList")
    public void inventoryOutList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        InventorySearchOption inventorySearchOption = Request2ModelUtils.covert(InventorySearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(inventorySearchOption);
        List<Integer> inventoryOutIds = inventoryOutService.inventoryOutIds(inventorySearchOption);
        if (inventoryOutIds != null && inventoryOutIds.size() > 0) {
            Integer count = inventoryOutService.countInventoryOut(inventorySearchOption);
            inventorySearchOption.setInventoryOutIds(inventoryOutIds);
            List<InventoryOutExtend> list = inventoryOutService.inventoryOutList(inventorySearchOption);
            ResponseUtil.sendListResponse(request, response, list, count, inventorySearchOption);
        } else {
            ResponseUtil.sendListResponse(request, response, new ArrayList(), 0, inventorySearchOption);
        }
    }

    /**
     * 确认出库
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/inventoryOut/confirmInventoryOut")
    public void confirmInventoryOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        //JSONArray jsonArray = JSON.parseArray(RequestUtils.getRequestUtils().postData(request));
        String postData = RequestUtils.getRequestUtils().postData(request);
        JSONObject jsonObject = JSON.parseObject(postData);
        JSONArray jsonArray = JSONArray.parseArray(jsonObject.get("jsonArray").toString());
        String inventory_out_remark = null;
        if (jsonObject.get("inventory_out_remark") != null) {
            inventory_out_remark = jsonObject.get("inventory_out_remark").toString();
        }
        Date inventoryDate = DateUtil.currentDate();
        if (jsonObject.get("inventory_date") != null) {
            String s = DateUtil.format(inventoryDate, "HH:mm:ss");//15:00:00
            String dateStr = jsonObject.get("inventory_date").toString();
            dateStr = DateUtil.format(DateUtil.stringToDate(dateStr), "yyyy-MM-dd");//2017-05-05
            dateStr = dateStr + " " + s;//2017-05-05 15:00:00
            inventoryDate = DateUtil.stringToDate(dateStr);
        }
        try {
            inventoryOutService.commodityOut(jsonArray, employee, inventory_out_remark, inventoryDate);
            ResponseUtil.sendSuccessResponse(request, response, postData);
        }catch (Exception e) {
            e.printStackTrace();
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", null, postData));
        }
    }

    /**
     * 全部退货，不许要出库
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/inventoryOut/noNeedInventoryOut")
    public void noNeedInventoryOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String inventory_out_commodity_ids = request.getParameter("inventory_out_commodity_id");
        inventoryOutService.noNeedInventoryOut(inventory_out_commodity_ids);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 确认出库列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/inventoryOut/inventoryOutCommodityList")
    public void inventoryOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int inventory_out_id = Integer.parseInt(request.getParameter("inventory_out_id"));
        List<InventoryOutCommodityExtend> list = inventoryOutService.inventoryOutCommodities(inventory_out_id);
        Order order =  null;
        for (int i = 0; i < list.size(); i++) {
            InventoryOutCommodityExtend inventoryOutCommodity = list.get(i);
            CommoditySearchOption commoditySearchOption = new CommoditySearchOption();
            commoditySearchOption.setCommodity_id(inventoryOutCommodity.getCommodity_id());
            commoditySearchOption.setCommodity_num(0);
            inventoryOutCommodity.setCommodityNa(inventoryService.commodityNumList(commoditySearchOption));
            if (order == null) {
                if (inventoryOutCommodity.getOrder_commodity_id() != null) {
                    int order_commodity_id = inventoryOutCommodity.getOrder_commodity_id();
                    OrderCommodity orderCommodity = orderService.getOrderCommodity(order_commodity_id);
                    order = orderService.getOrderById(orderCommodity.getOrder_id());
                }
            }

            if (inventoryOutCommodity.getOrderCommodity() != null && inventoryOutCommodity.getOrderCommodity().getActivity_commodity_id() != null) {
                //inventoryOutCommodity.setOrderCommodityActivity(ActivityComponents.(inventoryOutCommodity.getOrderCommodity().getActivity_commodity_id()));
                Integer activity_commodity_id = inventoryOutCommodity.getOrderCommodity().getActivity_commodity_id();
                inventoryOutCommodity.setOrderCommodityActivity(activityService.getActivityCommodityById(activity_commodity_id));
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("order", order);
        ResponseUtil.sendSuccessResponse(request, response, map);
    }

    @RequestMapping("/order/inventoryOut/inventoryOutDetail")
    public void inventoryOutDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = (Employee) request.getSession().getAttribute("employee");
        InventorySearchOption inventorySearchOption = new InventorySearchOption();
        int inventory_out_id = Integer.parseInt(request.getParameter("inventory_out_id"));
        List<Integer> inventoryOutIds = new ArrayList<>();
        inventoryOutIds.add(inventory_out_id);
        inventorySearchOption.setInventoryOutIds(inventoryOutIds);
        List<InventoryOutExtend> list = inventoryOutService.inventoryOutList(inventorySearchOption);
        if (list != null && list.size() >0) {
            InventoryOutExtend inventoryOutExtend = list.get(0);
            inventoryOutExtend.setPrintEmployee(employee);
            ResponseUtil.sendSuccessResponse(request, response, inventoryOutExtend);
        }else {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "id不正确"));
        }
    }
}
