package com.xinshan.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.employee.EmployeeComponent;
import com.xinshan.components.order.OrderComponents;
import com.xinshan.components.orderFee.OrderFeeComponents;
import com.xinshan.components.position.PositionComponent;
import com.xinshan.dao.OrderFeeMapper;
import com.xinshan.dao.OrderFeeTypeMapper;
import com.xinshan.dao.extend.orderFee.OrderFeeExtendMapper;
import com.xinshan.model.*;
import com.xinshan.model.extend.afterSales.AfterSalesExtend;
import com.xinshan.model.extend.employee.EmployeeExtend;
import com.xinshan.model.extend.gift.GiftCommodityExtend;
import com.xinshan.model.extend.inventory.InventoryHistoryDetailExtend;
import com.xinshan.model.extend.inventory.InventoryHistoryExtend;
import com.xinshan.model.extend.orderFee.OrderFeeExtend;
import com.xinshan.model.extend.position.PositionExtend;
import com.xinshan.pojo.afterSales.AfterSalesSearchOption;
import com.xinshan.pojo.orderFee.OrderFeeSearchOption;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.constant.inventory.InventoryHistoryConstant;
import com.xinshan.utils.constant.inventory.LogisticsConstants;
import com.xinshan.utils.constant.order.OrderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mxt on 16-11-23.
 */
@Service
public class OrderFeeService {
    @Autowired
    private OrderFeeExtendMapper orderFeeExtendMapper;
    @Autowired
    private OrderFeeTypeMapper orderFeeTypeMapper;
    @Autowired
    private InventoryHistoryService inventoryHistoryService;
    @Autowired
    private InventoryOutService inventoryOutService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private AfterSalesService afterSalesService;
    @Autowired
    private OrderFeeMapper orderFeeMapper;
    @Autowired
    private GiftService giftService;
    @Autowired
    private UserBringUpService userBringUpService;

    /**
     * 费用添加
     * @param jsonObject
     * @param employee
     * @param date
     */
    private List<OrderFee> createOrderFee(JSONObject jsonObject, Employee employee, Date date) {
        int supplier_id = Integer.parseInt(jsonObject.get("supplier_id").toString());
        Integer order_id = 0;
        Integer inventory_history_id = null;
        Integer after_sales_id = null;
        InventoryHistoryExtend inventoryHistoryExtend = null;
        if (jsonObject.get("inventory_history_id") != null) {
            inventory_history_id = Integer.parseInt(jsonObject.get("inventory_history_id").toString());
            inventoryHistoryExtend = inventoryHistoryService.getInventoryHistoryById(inventory_history_id);;
            order_id = inventoryHistoryExtend.getOrder_id();
        }

        if (jsonObject.get("after_sales_id") != null) {
            after_sales_id = Integer.parseInt(jsonObject.get("after_sales_id").toString());
            AfterSalesExtend afterSalesExtend = afterSalesService.getAfterSalesById(after_sales_id);
            order_id = afterSalesExtend.getOrder_id();
        }
        Integer position_id = null;
        PositionExtend positionExtend = null;
        if (jsonObject.get("position_id") != null && !"".equals(jsonObject.get("position_id"))) {
            position_id = Integer.parseInt(jsonObject.get("position_id").toString());
        }
        if (position_id != null) {
            positionExtend = PositionComponent.getPositionById(position_id);
        }

        List<OrderFee> orderFees = new ArrayList<>();
        String remark = jsonObject.get("remark").toString();
        JSONArray jsonArray = JSON.parseArray(jsonObject.get("fee").toString());
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject json = JSON.parseObject(jsonArray.get(i).toString());

            BigDecimal supplier_fee = new BigDecimal("0");
            BigDecimal fhc_fee = new BigDecimal("0");
            BigDecimal customer_fee = new BigDecimal("0");
            if (json.get("supplier_fee") != null && !json.get("supplier_fee").toString().equals("")) {
                supplier_fee = new BigDecimal(json.get("supplier_fee").toString());
            }
            if (json.get("fhc_fee") != null && !json.get("fhc_fee").toString().equals("")) {
                fhc_fee = new BigDecimal(json.get("fhc_fee").toString());
            }
            if (json.get("customer_fee") != null && !"".equals(json.get("customer_fee").toString())) {
                customer_fee = new BigDecimal(json.get("customer_fee").toString());
            }

            BigDecimal fee = supplier_fee.add(fhc_fee).add(customer_fee);
            if (fee.doubleValue() == 0) {
                continue;
            }
            int worker_id = Integer.parseInt(json.get("worker_id").toString());
            int order_fee_type_id = Integer.parseInt(json.get("order_fee_type_id").toString());
            OrderFee orderFee = new OrderFee();
            orderFee.setOrder_fee_worker_status(0);
            orderFee.setOrder_fee_supplier_status(0);
            orderFee.setSupplier_fee(supplier_fee);
            orderFee.setFhc_fee(fhc_fee);
            orderFee.setCustomer_fee(customer_fee);
            orderFee.setInventory_history_id(inventory_history_id);
            orderFee.setOrder_fee_remark(remark);
            orderFee.setRecord_date(date);
            orderFee.setRecord_employee_code(employee.getEmployee_code());
            orderFee.setRecord_employee_name(employee.getEmployee_name());
            orderFee.setFee(fee);
            orderFee.setWorker_id(worker_id);
            orderFee.setSupplier_id(supplier_id);
            orderFee.setOrder_fee_type_id(order_fee_type_id);
            orderFee.setOrder_id(order_id);
            orderFee.setAfter_sales_id(after_sales_id);
            orderFee.setOrder_fee_check_status(OrderConstants.order_fee_no_check_status);
            if (inventory_history_id != null) {
                orderFee.setOrder_fee_source(OrderFeeComponents.FEE_SOURCE_INVENTORY_OUT);
            }else if (after_sales_id != null) {
                orderFee.setOrder_fee_source(OrderFeeComponents.FEE_SOURCE_AFTER_SALES);
            }
            orderFee.setOrder_fee_enable(1);
            if (positionExtend != null) {
                orderFee.setPosition_id(positionExtend.getPosition_id());
                orderFee.setPosition_name(positionExtend.getPosition_name());
            }
            orderFeeExtendMapper.createOrderFee(orderFee);
            orderFees.add(orderFee);
        }
        if (inventoryHistoryExtend != null) {
            inventoryHistoryExtend.setInventory_out_fee_check_status(OrderConstants.inventory_out_fee_check_status_not_check);
            inventoryHistoryService.updateInventoryHistory(inventoryHistoryExtend);
        }
        return orderFees;
    }

    private void inventoryOutOrderCommodityStatus(InventoryHistoryExtend inventoryHistoryExtend) {
        //订单商品状态
        List<InventoryHistoryDetailExtend> list = inventoryHistoryExtend.getInventoryHistoryDetails();
        for (int i = 0; i < list.size(); i++) {
            InventoryHistoryDetailExtend inventoryHistoryDetail = list.get(i);
            InventoryOutCommodity inventoryOutCommodity = inventoryHistoryDetail.getInventoryOutCommodity();
            Integer inventory_type = inventoryHistoryExtend.getInventory_type();
            if (inventory_type.equals(InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_OUT)) {
                int order_commodity_id = inventoryOutCommodity.getOrder_commodity_id();
                OrderCommodity orderCommodity = orderService.getOrderCommodity(order_commodity_id);
                if (inventoryOutCommodity.getInventory_out_total_num() >= inventoryOutCommodity.getOrder_commodity_num()) {//全部出库，全部到货
                    orderCommodity.setOrder_commodity_status(OrderConstants.ORDER_COMMODITY_STATUS_ALL_DONE);//全部到货
                }else {
                    orderCommodity.setOrder_commodity_status(OrderConstants.ORDER_COMMODITY_STATUS_PART_DONE);//部分到货
                }
                orderService.updateOrderCommodity(orderCommodity);
            }else if (inventory_type.equals(InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_GIFT_OUT)) {
                Integer gift_commodity_id = inventoryOutCommodity.getGift_commodity_id();
                GiftCommodityExtend giftCommodity = giftService.getGiftCommodityById(gift_commodity_id);
                if (inventoryOutCommodity.getInventory_out_total_num() >= inventoryOutCommodity.getOrder_commodity_num()) {//全部出库，全部到货
                    giftCommodity.setGift_commodity_out_status(OrderConstants.ORDER_COMMODITY_STATUS_ALL_DONE);//全部到货
                }else {
                    giftCommodity.setGift_commodity_out_status(OrderConstants.ORDER_COMMODITY_STATUS_PART_DONE);//部分到货
                }
                giftService.updateGiftCommodity(giftCommodity);
            }
        }
    }

    /**
     *
     * @param jsonArray
     * @param employee
     */
    @Transactional
    public void createAfterSalesOrderFee(JSONArray jsonArray, Employee employee) {
        int after_sales_id = getAfterSalesId(jsonArray);
        Date date = DateUtil.currentDate();
        List<OrderFee> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = JSON.parseObject(jsonArray.get(i).toString());
            List<OrderFee> orderFees = createOrderFee(jsonObject, employee, date);
            list.addAll(orderFees);
        }
        AfterSales afterSales = afterSalesService.getAfterSalesById(after_sales_id);
        afterSales.setResolve_date(DateUtil.currentDate());
        afterSales.setAfter_sales_fee_status(0);
        afterSales.setOrder_fee_ids(OrderFeeComponents.orderFeeIds(list));
        afterSalesService.updateAfterSales(afterSales);
    }

    private int getAfterSalesId(JSONArray jsonArray) {
        JSONObject jsonObject = JSON.parseObject(jsonArray.get(0).toString());
        int after_sales_id = Integer.parseInt(jsonObject.get("after_sales_id").toString());
        return after_sales_id;
    }

    public void createOrderFeeType(OrderFeeType orderFeeType) {
        orderFeeExtendMapper.createOrderFeeType(orderFeeType);
    }

    public List<OrderFeeType> orderFeeTypes() {
        return orderFeeTypeMapper.selectAll();
    }

    public List<OrderFeeExtend> orderFees(AfterSalesSearchOption afterSalesSearchOption) {
        List<OrderFeeExtend> list = orderFeeExtendMapper.orderFees(afterSalesSearchOption);
        for (int i = 0; i < list.size(); i++) {
            OrderFeeExtend orderFeeExtend = list.get(i);
            HashMap hashMap = orderFeeExtendMapper.fixDesc(orderFeeExtend.getOrder_fee_id());
            if (hashMap != null && hashMap.get("sample_fix_code") != null) {
                orderFeeExtend.setSampleFixCode(hashMap.get("sample_fix_code").toString());
            }
            if (hashMap != null && hashMap.get("fix_desc") != null) {
                orderFeeExtend.setSampleFixRemark(hashMap.get("fix_desc").toString());
            }
        }
        return list;
    }

    public List<OrderFeeExtend> orderFees1(OrderFeeSearchOption orderFeeSearchOption) {
        return orderFeeExtendMapper.orderFees1(orderFeeSearchOption);
    }

    public Integer countOrderFee(AfterSalesSearchOption afterSalesSearchOption) {
        return orderFeeExtendMapper.countOrderFee(afterSalesSearchOption);
    }

    /**
     * 送货结束，添加送货费用，未审核
     * @param jsonArray
     * @param employee
     */
    @Transactional
    public void createOrderFee(JSONArray jsonArray, Employee employee) {
        Date date = DateUtil.currentDate();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = JSON.parseObject(jsonArray.get(i).toString());
            createOrderFee(jsonObject, employee, date);
        }
        JSONObject js = JSON.parseObject(jsonArray.get(0).toString());
        int inventory_history_id = Integer.parseInt(js.get("inventory_history_id").toString());
        InventoryHistoryExtend inventoryHistory = inventoryHistoryService.getInventoryHistoryById(inventory_history_id);
        inventoryHistory.setInventory_out_fee_check_status(OrderConstants.inventory_out_fee_check_status_not_check);
        inventoryOutOrderCommodityStatus(inventoryHistory);
        inventoryHistoryService.updateInventoryHistory(inventoryHistory);

        //到货
        Logistics logistics = inventoryHistory.getLogistics();
        logistics.setLogistics_status(LogisticsConstants.LOGISTICS_STATUS_DONE);
        inventoryOutService.updateLogistics(logistics);
        if (inventoryHistory.getOrder_id() != null) {
            OrderComponents.orderStep(inventoryHistory.getOrder_id());
            userBringUpService.orderComplete(inventoryHistory.getOrder_id());
        }
    }

    public void createOrderFee(OrderFee orderFee) {
        orderFee.setOrder_fee_enable(1);
        orderFeeExtendMapper.createOrderFee(orderFee);
    }

    /**
     * 送货结束，送货费用审核
     * @param inventory_history_id
     * @param employee
     */
    @Transactional
    public void checkOrderFee(int inventory_history_id, Employee employee) {
        InventoryHistoryExtend inventoryHistoryExtend = inventoryHistoryService.getInventoryHistoryById(inventory_history_id);
        if (inventoryHistoryExtend.getInventory_out_fee_check_status() == OrderConstants.order_fee_check_status) {//已经审核
            return;
        }

        AfterSalesSearchOption afterSalesSearchOption = new AfterSalesSearchOption();
        afterSalesSearchOption.setInventory_history_id(inventory_history_id);
        List<OrderFeeExtend> list = orderFeeExtendMapper.orderFees(afterSalesSearchOption);
        checkOrderFee(list, employee);

        //已审核
        inventoryHistoryExtend.setInventory_out_fee_check_status(OrderConstants.inventory_out_fee_check_status_check);
        inventoryHistoryService.updateInventoryHistory(inventoryHistoryExtend);
    }

    public void checkOrderFee(List<OrderFeeExtend> list, Employee employee) {
        Date date = DateUtil.currentDate();
        for (int i = 0; i < list.size(); i++) {
            OrderFee orderFee = list.get(i);
            orderFee.setOrder_fee_check_status(OrderConstants.order_fee_check_status);
            orderFee.setOrder_fee_check_date(date);
            orderFee.setOrder_fee_check_employee_code(employee.getEmployee_code());
            orderFee.setOrder_fee_check_employee_name(employee.getEmployee_name());
            orderFeeMapper.updateByPrimaryKey(orderFee);
        }
    }

    public OrderFee getOrderFeeById(int order_fee_id) {
        return orderFeeMapper.selectByPrimaryKey(order_fee_id);
    }
    @Transactional
    public void updateOrderFeeWithTran(OrderFee orderFee) {
        orderFeeMapper.updateByPrimaryKey(orderFee);
    }
}
