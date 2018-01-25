package com.xinshan.components.orderFee;

import com.xinshan.model.Employee;
import com.xinshan.model.OrderFee;
import com.xinshan.model.OrderFeeType;
import com.xinshan.model.extend.order.OrderReturnExtend;
import com.xinshan.model.extend.orderFee.OrderFeeExtend;
import com.xinshan.pojo.afterSales.AfterSalesSearchOption;
import com.xinshan.pojo.orderFee.OrderFeeSearchOption;
import com.xinshan.service.OrderFeeService;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.SplitUtils;
import com.xinshan.utils.constant.order.OrderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by mxt on 17-2-28.
 */
@Component
public class OrderFeeComponents {
    private static OrderFeeService orderFeeService;

    @Autowired
    public void setOrderFeeService(OrderFeeService orderFeeService) {
        OrderFeeComponents.orderFeeService = orderFeeService;
    }

    private static Map<Integer, OrderFeeType> orderFeeTypeMap = new HashMap<>();

    public static Map<Integer, OrderFeeType> getOrderFeeTypeMap() {
        if (orderFeeTypeMap == null || orderFeeTypeMap.size() == 0) {
            initOrderFeeTypeMap();
        }
        return orderFeeTypeMap;
    }
    private static void initOrderFeeTypeMap() {
        List<OrderFeeType> list = orderFeeService.orderFeeTypes();
        for (int i = 0; i < list.size(); i++) {
            OrderFeeType orderFeeType = list.get(i);
            orderFeeTypeMap.put(orderFeeType.getOrder_fee_type_id(), orderFeeType);
        }
    }

    public OrderFeeType getOrderFeeById(int order_fee_type) {
        return getOrderFeeTypeMap().get(order_fee_type);
    }

    public static void clear() {
        orderFeeTypeMap.clear();
    }

    public static final int FEE_SOURCE_INVENTORY_OUT = 1;//出库费用
    public static final int FEE_SOURCE_AFTER_SALES = 2;//售后费用
    public static final int FEE_SOURCE_SAMPLE_FIX = 3;//场地维修费用
    public static final int FEE_SOURCE_ORDER_RETURN_DEDUCTION = 4;//退换货扣除费用


    /**
     * 退换货费用添加
     * @param orderFees
     * @param orderReturnExtend
     * @param employee
     * @return
     */
    public static List<OrderFee> createOrderFee(List<OrderFeeExtend> orderFees, OrderReturnExtend orderReturnExtend, Employee employee) {
        boolean remark = false;
        List<OrderFee> orderFeeList = new ArrayList<>();
        for (int i = 0; i < orderFees.size(); i++) {
            OrderFee orderFee = orderFees.get(i);
            orderFee.setOrder_fee_worker_status(0);
            orderFee.setOrder_fee_supplier_status(0);
            BigDecimal zero = new BigDecimal("0");
            if (orderFee.getSupplier_fee() == null) {
                orderFee.setSupplier_fee(zero);
            }
            if (orderFee.getFhc_fee() == null) {
                orderFee.setFhc_fee(zero);
            }
            if (orderFee.getCustomer_fee() == null) {
                orderFee.setCustomer_fee(zero);
            }
            orderFee.setFee(orderFee.getFhc_fee().add(orderFee.getSupplier_fee()).add(orderFee.getCustomer_fee()));
            if (orderFee.getFee().doubleValue() == 0 && remark) {
                continue;
            }
            orderFee.setOrder_id(orderReturnExtend.getOrder_id());
            orderFee.setRecord_date(DateUtil.currentDate());
            orderFee.setRecord_employee_code(employee.getEmployee_code());
            orderFee.setRecord_employee_name(employee.getEmployee_name());
            orderFee.setOrder_fee_check_status(OrderConstants.order_fee_no_check_status);
            orderFee.setOrder_fee_source(FEE_SOURCE_ORDER_RETURN_DEDUCTION);
            orderFee.setOrder_id(orderReturnExtend.getOrder_id());
            orderFeeService.createOrderFee(orderFee);
            orderFeeList.add(orderFee);
            remark = true;
        }
        return orderFeeList;
    }
    /**
     * 场地维修费用
     * @param orderFees
     * @param supplier_id
     * @param employee
     * @return
     */
    public static List<OrderFee> createOrderFee(List<OrderFeeExtend> orderFees, int supplier_id, Employee employee) {
        List<OrderFee> orderFeeList = new ArrayList<>();
        for (int i = 0; i < orderFees.size(); i++) {
            OrderFee orderFee = orderFees.get(i);
            orderFee.setOrder_fee_worker_status(0);
            orderFee.setOrder_fee_supplier_status(0);
            BigDecimal zero = new BigDecimal("0");
            if (orderFee.getSupplier_fee() == null) {
                orderFee.setSupplier_fee(zero);
            }
            if (orderFee.getFhc_fee() == null) {
                orderFee.setFhc_fee(zero);
            }
            if (orderFee.getCustomer_fee() == null) {
                orderFee.setCustomer_fee(zero);
            }
            orderFee.setFee(orderFee.getFhc_fee().add(orderFee.getSupplier_fee()).add(orderFee.getCustomer_fee()));
            if (orderFee.getFee().doubleValue() == 0) {
                continue;
            }
            orderFee.setRecord_date(DateUtil.currentDate());
            orderFee.setRecord_employee_code(employee.getEmployee_code());
            orderFee.setRecord_employee_name(employee.getEmployee_name());
            orderFee.setSupplier_id(supplier_id);
            orderFee.setOrder_fee_check_status(OrderConstants.order_fee_no_check_status);
            orderFee.setOrder_fee_source(FEE_SOURCE_SAMPLE_FIX);
            orderFeeService.createOrderFee(orderFee);
            orderFeeList.add(orderFee);
        }
        return orderFeeList;
    }

    /**
     * 返回费用id的字符传
     * @param orderFees
     * @return
     */
    public static String orderFeeIds(List<OrderFee> orderFees) {
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < orderFees.size(); i++) {
            OrderFee orderFee = orderFees.get(i);
            set.add(orderFee.getOrder_fee_id());
        }
        return SplitUtils.setToString(set);
    }

    /**
     * 返回费用id的字符传
     * @param orderFees
     * @return
     */
    public static String orderFeeIds1(List<OrderFeeExtend> orderFees) {
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < orderFees.size(); i++) {
            OrderFee orderFee = orderFees.get(i);
            set.add(orderFee.getOrder_fee_id());
        }
        return SplitUtils.setToString(set);
    }

    /**
     * 费用审核
     * @param orderFees
     * @param employee
     */
    public static void checkOrderFee(List<OrderFeeExtend> orderFees, Employee employee) {
        orderFeeService.checkOrderFee(orderFees, employee);
    }

    /**
     * 查询费用
     * @param order_fee_ids
     * @return
     */
    public static List<OrderFeeExtend> orderFeeList(String order_fee_ids) {
        List<Integer> orderFeeIds = SplitUtils.splitToList(order_fee_ids, ",");
        if (orderFeeIds == null || orderFeeIds.size() == 0) {
            return new ArrayList<>();
        }
        OrderFeeSearchOption orderFeeSearchOption = new OrderFeeSearchOption();
        orderFeeSearchOption.setOrderFeeIds(orderFeeIds);
        List<OrderFeeExtend> list = orderFeeService.orderFees1(orderFeeSearchOption);
        return list;
    }

    /**
     * 查询费用
     * @param order_fee_ids
     * @return
     */
    public static List<OrderFeeExtend> orderFeeList(String order_fee_ids, int supplier_id) {
        List<Integer> orderFeeIds = SplitUtils.splitToList(order_fee_ids, ",");
        if (orderFeeIds == null || orderFeeIds.size() == 0) {
            return new ArrayList<>();
        }
        OrderFeeSearchOption orderFeeSearchOption = new OrderFeeSearchOption();
        orderFeeSearchOption.setOrderFeeIds(orderFeeIds);
        orderFeeSearchOption.setSupplier_id(supplier_id);
        List<OrderFeeExtend> list = orderFeeService.orderFees1(orderFeeSearchOption);
        return list;
    }

    /**
     * 查询费用
     * @param orderFeeSearchOption
     * @return
     */
    public static List<OrderFeeExtend> orderFeeList(OrderFeeSearchOption orderFeeSearchOption) {
        List<OrderFeeExtend> list = orderFeeService.orderFees1(orderFeeSearchOption);
        return list;
    }

    public static List<OrderFeeExtend> orderFeeList(AfterSalesSearchOption afterSalesSearchOption) {
        List<OrderFeeExtend> list = orderFeeService.orderFees(afterSalesSearchOption);
        return list;
    }

    /**
     * 查询售后费用
     * @param after_sales_id
     * @return
     */
    public static List<OrderFeeExtend> afterSalesOrderFeeList(int supplier_id, int after_sales_id) {
        OrderFeeSearchOption orderFeeSearchOption = new OrderFeeSearchOption();
        orderFeeSearchOption.setAfter_sales_id(after_sales_id);
        orderFeeSearchOption.setSupplier_id(supplier_id);
        List<OrderFeeExtend> list = orderFeeService.orderFees1(orderFeeSearchOption);
        return list;
    }

    /**
     * 查询出库费用
     * @param inventory_out_id
     * @return
     */
    public static List<OrderFeeExtend> inventoryOutOrderFeeList(int supplier_id, int inventory_out_id) {
        OrderFeeSearchOption orderFeeSearchOption = new OrderFeeSearchOption();
        orderFeeSearchOption.setInventory_history_id(inventory_out_id);
        orderFeeSearchOption.setSupplier_id(supplier_id);
        List<OrderFeeExtend> list = orderFeeService.orderFees1(orderFeeSearchOption);
        return list;
    }

    public static void updateOrderFee(OrderFee orderFee) {
        orderFeeService.updateOrderFeeWithTran(orderFee);
    }
}
