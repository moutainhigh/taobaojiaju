package com.xinshan.service;

import com.xinshan.components.order.OrderComponents;
import com.xinshan.components.orderFee.OrderFeeComponents;
import com.xinshan.components.pay.PayComponents;
import com.xinshan.components.pay.PayReturnComponents;
import com.xinshan.dao.*;
import com.xinshan.dao.extend.order.OrderExtendMapper;
import com.xinshan.dao.extend.order.OrderReturnExtendMapper;
import com.xinshan.model.*;
import com.xinshan.model.extend.afterSales.AfterSalesCommodityExtend;
import com.xinshan.model.extend.afterSales.AfterSalesExtend;
import com.xinshan.model.extend.inventory.InventoryInExtend;
import com.xinshan.model.extend.order.OrderCommodityExtend;
import com.xinshan.model.extend.order.OrderExtend;
import com.xinshan.model.extend.order.OrderReturnCommodityExtend;
import com.xinshan.model.extend.order.OrderReturnExtend;
import com.xinshan.model.extend.orderFee.OrderFeeExtend;
import com.xinshan.model.extend.purchase.PurchaseCommodityExtend;
import com.xinshan.pojo.inventory.InventorySearchOption;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.ResponseUtil;
import com.xinshan.utils.ResultData;
import com.xinshan.utils.SplitUtils;
import com.xinshan.utils.constant.ConstantUtils;
import com.xinshan.pojo.order.OrderSearchOption;
import com.xinshan.utils.constant.order.OrderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mxt on 17-3-24.
 */
@Service
public class OrderReturnService {
    @Autowired
    private OrderReturnExtendMapper orderReturnExtendMapper;
    @Autowired
    private OrderReturnMapper orderReturnMapper;
    @Autowired
    private OrderReturnCommodityMapper orderReturnCommodityMapper;
    @Autowired
    private OrderExtendMapper orderExtendMapper;
    @Autowired
    private CommodityService commodityService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderCommodityMapper orderCommodityMapper;
    @Autowired
    private InventoryInService inventoryInService;
    @Autowired
    private InventoryHistoryService inventoryHistoryService;

    /**
     * 订单退货
     * @param orderReturnExtend
     * @param employee
     */
    @Transactional
    public void createOrderReturn(OrderReturnExtend orderReturnExtend, Employee employee) {
        String orderReturnCode = orderReturnCode();
        orderReturnExtend.setOrder_return_code(orderReturnCode);
        orderReturnExtend.setOrder_return_employee_code(employee.getEmployee_code());
        orderReturnExtend.setOrder_return_employee_name(employee.getEmployee_name());
        orderReturnExtend.setOrder_return_record_date(DateUtil.currentDate());
        if (orderReturnExtend.getOrder_return_type() == null) {
            orderReturnExtend.setOrder_return_type(OrderConstants.order_return_type_return);//默认订单退货
        }

        if (orderReturnExtend.getOrder_return_date() == null) {
            orderReturnExtend.setOrder_return_date(orderReturnExtend.getOrder_return_record_date());
        }
        orderReturnExtend.setOrder_return_check_status(0);
        if (orderReturnExtend.getOrder_return_preferential_amount() == null) {
            orderReturnExtend.setOrder_return_preferential_amount(new BigDecimal("0"));
        }
        setDefaultAmount(orderReturnExtend);

        orderReturnExtend.setOrder_return_pay_amount(new BigDecimal("0"));//付款金额
        orderReturnExtend.setOrder_return_need_amount(orderReturnExtend.getOrder_return_amount());//未付金额
        orderReturnExtend.setOrder_return_pay_status(0);//退货付款状态，0未付款，1部分付款，2付款完成
        orderReturnExtend.setOrder_return_deduction_amount_pay_status(0);//扣除费用未付款
        orderReturnExtend.setOrder_return_settlement_status(0);
        orderReturnExtend.setOrder_return_fee_check_status(-1);//费用未添加
        orderReturnExtendMapper.createOrderReturn(orderReturnExtend);

        List<OrderReturnCommodityExtend> list = orderReturnExtend.getOrderReturnCommodities();
        if (list == null || list.size() == 0) {
            throw new RuntimeException("没有退货商品");
        }

        for (int i = 0; i < list.size(); i++) {
            OrderReturnCommodity orderReturnCommodity = list.get(i);
            orderReturnCommodity.setOrder_return_id(orderReturnExtend.getOrder_return_id());
            if (orderReturnCommodity.getSample() == null) {
                orderReturnCommodity.setSample(0);
            }
            if (orderReturnCommodity.getOrder_return_commodity_type() != OrderConstants.ORDER_RETURN_COMMODITY_TYPE_ADD) {
                int order_return_commodity_num = Math.abs(orderReturnCommodity.getOrder_return_commodity_num());
                orderReturnCommodity.setOrder_return_commodity_num(order_return_commodity_num);
                OrderCommodity orderCommodity = orderService.getOrderCommodity(orderReturnCommodity.getOrder_commodity_id());
                int commodity_num = orderCommodity.getCommodity_num();
                int return_commodity_num = orderReturnCommodity.getOrder_return_commodity_num();
                if (return_commodity_num > commodity_num) {
                    throw new RuntimeException("退货商品数量大于订单商品数量");
                }
            }
            orderReturnCommodity.setCommodity_total_price(orderReturnCommodity.getBargain_price().
                    add(orderReturnCommodity.getRevision_fee()).multiply(new BigDecimal(orderReturnCommodity.getOrder_return_commodity_num())));
            orderReturnExtendMapper.createOrderReturnCommodity(orderReturnCommodity);
        }

        //搬运费
        List<OrderCarryFee> orderCarryFees = orderReturnExtend.getOrderCarryFees();
        if (orderCarryFees != null) {
            createOrderCarryFee(orderCarryFees, orderReturnExtend.getOrder_id(), orderReturnExtend.getOrder_return_id());
        }

        if (orderReturnExtend.getOrder_return_type() == OrderConstants.order_return_type_return) {//订单退货
            Order order = orderService.getOrderById(orderReturnExtend.getOrder_id());
            order.setReturn_status(1);
            orderExtendMapper.updateOrder(order);
        }
        /*else if (orderReturnExtend.getOrder_return_type() == OrderConstants.order_return_type_after_sales){//售后退货
            orderReturnExtend.setOrder_return_check_status(1);
            orderReturnExtend.setOrder_return_check_employee_code(employee.getEmployee_code());
            orderReturnExtend.setOrder_return_check_employee_name(employee.getEmployee_name());

            //退货，修改售后状态
            int after_sales_id = afterSalesStatus(list);
            orderReturnExtend.setAfter_sales_id(after_sales_id);
            orderReturnMapper.updateByPrimaryKey(orderReturnExtend);

            //退货，准备退货商品入库
            orderReturnInventoryIn(orderReturnExtend, employee);
        }*/
    }

    private void setDefaultAmount(OrderReturnExtend orderReturnExtend) {
        BigDecimal zero = new BigDecimal("0");
        if (orderReturnExtend.getReturn_commodity_amount() == null) {
            orderReturnExtend.setReturn_commodity_amount(zero);
        }
        if (orderReturnExtend.getReturn_carry_fee() == null) {
            orderReturnExtend.setReturn_carry_fee(zero);
        }
        if (orderReturnExtend.getReturn_amount() == null) {
            orderReturnExtend.setReturn_amount(zero);
        }
        if (orderReturnExtend.getAdd_commodity_amount() == null) {
            orderReturnExtend.setAdd_commodity_amount(zero);
        }
        if (orderReturnExtend.getAdd_carry_fee() == null) {
            orderReturnExtend.setAdd_carry_fee(zero);
        }
        if (orderReturnExtend.getAdd_amount() == null) {
            orderReturnExtend.setAdd_amount(zero);
        }
        if (orderReturnExtend.getOrder_return_preferential_amount() == null ){
            orderReturnExtend.setOrder_return_preferential_amount(zero);
        }
        if (orderReturnExtend.getOrder_return_commodity_amount() == null) {
            orderReturnExtend.setOrder_return_commodity_amount(zero);
        }
        if (orderReturnExtend.getOrder_return_received_amount() == null) {
            orderReturnExtend.setOrder_return_received_amount(zero);
        }
    }

    private String orderReturnCode() {
        String n = orderReturnExtendMapper.orderReturnCode(ConstantUtils.ORDER_RETURN_CODE_HEADER + DateUtil.format(DateUtil.currentDate(), "yyMMdd") + "[0-9]{3}");//O161031001
        if (n == null) {
            return ConstantUtils.ORDER_RETURN_CODE_HEADER + Long.parseLong(DateUtil.format(DateUtil.currentDate(), "yyMMdd"))*100+1l;
        }
        n = n.substring(ConstantUtils.ORDER_RETURN_CODE_HEADER.length(), n.length());
        int m = Integer.parseInt(n);
        m++;
        return ConstantUtils.ORDER_RETURN_CODE_HEADER+m;
    }

    @Transactional
    public void updateOrderReturn(OrderReturnExtend orderReturnExtend, OrderReturnExtend orderReturnExtendOld, Employee employee) {
        setDefaultAmount(orderReturnExtend);
        orderReturnExtendOld.setOrder_return_check_employee_code(employee.getEmployee_code());
        orderReturnExtendOld.setOrder_return_check_employee_name(employee.getEmployee_name());
        if (orderReturnExtend.getOrder_return_date() != null) {
            orderReturnExtendOld.setOrder_return_date(orderReturnExtend.getOrder_return_date());
        }
        orderReturnExtendOld.setOrder_return_commodity_amount(orderReturnExtend.getOrder_return_commodity_amount());
        if (orderReturnExtendOld.getOrder_return_commodity_amount() == null) {
            orderReturnExtendOld.setOrder_return_commodity_amount(new BigDecimal("0"));
        }
        orderReturnExtendOld.setOrder_return_received_amount(orderReturnExtend.getOrder_return_received_amount());
        if (orderReturnExtendOld.getOrder_return_received_amount() == null) {
            orderReturnExtendOld.setOrder_return_received_amount(new BigDecimal("0"));
        }
        orderReturnExtendOld.setOrder_return_preferential_amount(orderReturnExtend.getOrder_return_preferential_amount());
        if (orderReturnExtendOld.getOrder_return_preferential_amount() == null) {
            orderReturnExtendOld.setOrder_return_preferential_amount(new BigDecimal("0"));
        }
        orderReturnExtendOld.setOrder_return_deduction_amount(orderReturnExtend.getOrder_return_deduction_amount());
        if (orderReturnExtendOld.getOrder_return_deduction_amount() == null) {
            orderReturnExtendOld.setOrder_return_deduction_amount(new BigDecimal("0"));
        }
        orderReturnExtendOld.setOrder_return_amount(orderReturnExtend.getOrder_return_amount());
        if (orderReturnExtendOld.getOrder_return_amount() == null) {
            orderReturnExtendOld.setOrder_return_amount(new BigDecimal("0"));
        }
        orderReturnExtendOld.setOrder_return_remark(orderReturnExtend.getOrder_return_remark());

        /*String order_pay_ids = orderReturnExtendOld.getOrder_pay_ids();
        List<OrderPay> orderPays = PayComponents.orderPays(order_pay_ids);
        for (int i = 0; i < orderPays.size(); i++) {
            OrderPay orderPay = orderPays.get(i);
            PayComponents.deleteOrderPay(orderPay);
        }
        orderPays = PayComponents.createOrderPays(orderReturnExtend.getOrderPays(), employee, orderReturnExtendOld.getOrder_id(), orderReturnExtendOld.getOrder_return_id());
        orderReturnExtend.setOrder_pay_ids(PayComponents.orderPayIds(orderPays));*/
        orderReturnMapper.updateByPrimaryKey(orderReturnExtendOld);


        List<OrderReturnCommodityExtend> orcs = orderReturnExtendOld.getOrderReturnCommodities();
        for (int i = 0; i < orcs.size(); i++) {
            OrderReturnCommodity orderReturnCommodity = orcs.get(i);
            orderReturnCommodityMapper.deleteByPrimaryKey(orderReturnCommodity.getOrder_return_commodity_id());
        }
        List<OrderReturnCommodityExtend> list = orderReturnExtend.getOrderReturnCommodities();
        for (int i = 0; i < list.size(); i++) {
            OrderReturnCommodity orderReturnCommodity = list.get(i);
            orderReturnCommodity.setOrder_return_id(orderReturnExtendOld.getOrder_return_id());
            orderReturnExtendMapper.createOrderReturnCommodity(orderReturnCommodity);
        }

        orderExtendMapper.deleteCarryFeeByOrderReturnId(orderReturnExtendOld.getOrder_return_id());
        List<OrderCarryFee> orderCarryFees = orderReturnExtend.getOrderCarryFees();
        if (orderCarryFees != null) {
            createOrderCarryFee(orderCarryFees, orderReturnExtendOld.getOrder_id(), orderReturnExtendOld.getOrder_return_id());
        }
    }

    public void updateOrderReturn(OrderReturn orderReturn) {
        orderReturnMapper.updateByPrimaryKey(orderReturn);
    }

    public void updateOrderReturnCommodity(OrderReturnCommodity orderReturnCommodity) {
        orderReturnCommodityMapper.updateByPrimaryKey(orderReturnCommodity);
    }

    private void createOrderCarryFee(List<OrderCarryFee> orderCarryFees, int order_id, int order_return_id) {
        for (int i = 0; i < orderCarryFees.size(); i++) {
            OrderCarryFee orderCarryFee = orderCarryFees.get(i);
            if (orderCarryFee.getOrder_carry_fee() == null) {
                orderCarryFee.setOrder_carry_fee(new BigDecimal("0"));
            }
            orderCarryFee.setOrder_return_id(order_return_id);
            orderCarryFee.setOrder_id(order_id);
            orderExtendMapper.createCarryFee(orderCarryFee);
        }
    }

    public List<Integer> orderReturnIds(OrderSearchOption orderSearchOption) {
        return orderReturnExtendMapper.orderReturnIds(orderSearchOption);
    }

    public List<OrderReturnCommodityExtend> orderReturnCommodities(int order_return_id, int supplier_id) {
        OrderSearchOption orderSearchOption = new OrderSearchOption();
        orderSearchOption.setOrder_return_id(order_return_id);
        orderSearchOption.setSupplier_id(supplier_id);
        orderSearchOption.setOrder_return_commodity_type(OrderConstants.ORDER_RETURN_COMMODITY_TYPE_RETURN);
        return orderReturnExtendMapper.orderReturnCommodities(orderSearchOption);
    }

    public OrderReturnCommodityExtend getOrderReturnCommodityById(int order_return_commodity_id) {
        OrderSearchOption orderSearchOption = new OrderSearchOption();
        orderSearchOption.setOrder_return_commodity_id(order_return_commodity_id);
        List<OrderReturnCommodityExtend> list = orderReturnReport(orderSearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public OrderReturnExtend getOrderReturnById(int order_return_id) {
        List<Integer> orderReturnIds = new ArrayList<>();
        orderReturnIds.add(order_return_id);
        List<OrderReturnExtend> list = orderReturnList(orderReturnIds);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public List<OrderReturn> orderReturns(){
        return orderReturnMapper.selectAll();
    }

    public List<OrderReturnExtend> orderReturnList(List<Integer> orderReturnIds) {
        if (orderReturnIds != null && orderReturnIds.size() > 0) {
            OrderSearchOption orderSearchOption = new OrderSearchOption();
            orderSearchOption.setOrderReturnIds(orderReturnIds);
            List<OrderReturnExtend> list = orderReturnExtendMapper.orderReturnList(orderSearchOption);
            for (int i = 0; i < list.size(); i++) {
                OrderReturnExtend orderReturnExtend = list.get(i);
                List<OrderReturnCommodityExtend> orderReturnCommodities = orderReturnExtend.getOrderReturnCommodities();
                for (int j = 0; j < orderReturnCommodities.size(); j++) {
                    OrderReturnCommodityExtend orderReturnCommodity = orderReturnCommodities.get(j);
                    if (orderReturnCommodity.getCommodity_id() != null) {
                        Commodity commodity = commodityService.getCommodityById(orderReturnCommodity.getCommodity_id());
                        orderReturnCommodity.setCommodity(commodity);
                        if (commodity != null && commodity.getSupplier_id() != null) {
                            orderReturnCommodity.setSupplier(supplierService.getSupplierById(commodity.getSupplier_id()));
                        }

                    }
                    if(orderReturnCommodity.getOrder_commodity_id() != null){
                        orderReturnCommodity.setOrderCommodity(orderService.getOrderCommodity(orderReturnCommodity.getOrder_commodity_id()));
                    }

                }
                orderReturnExtend.setOrderPays(PayComponents.orderPays(orderReturnExtend.getOrder_pay_ids()));
                orderReturnExtend.setOrderPayReturns(PayReturnComponents.payReturns(orderReturnExtend.getOrder_pay_return_ids()));
                orderReturnExtend.setOrderFees(OrderFeeComponents.orderFeeList(orderReturnExtend.getOrder_fee_ids()));
            }
            return list;
        }
        return new ArrayList<>();
    }

    public Integer countOrderReturn(OrderSearchOption orderSearchOption) {
        return orderReturnExtendMapper.countOrderReturn(orderSearchOption);
    }

    /**
     * 退换货确认
     * @param orderReturnExtend
     * @param employee
     */
    @Transactional
    public void checkOrderReturn(OrderReturnExtend orderReturnExtend, Employee employee) {
        int orderReturnType = orderReturnExtend.getOrder_return_type();
        switch (orderReturnType) {
            case OrderConstants.order_return_type_return:
                orderReturnConfirm(orderReturnExtend, employee);
                break;
            case OrderConstants.order_return_type_after_sales://退货确认,准备入库
                orderReturnAfterSales(orderReturnExtend, employee);
                break;
            default:
                break;
        }
    }

    /**
     * 售后退货
     * @param orderReturnExtend
     * @param employee
     */
    public void orderReturnAfterSales(OrderReturnExtend orderReturnExtend, Employee employee) {
        List<OrderReturnCommodityExtend> list = orderReturnExtend.getOrderReturnCommodities();
        OrderExtend orderExtend = orderService.getOrderById(orderReturnExtend.getOrder_id());
        List<OrderReturnCommodityExtend> orderReturnCommodities = orderReturnExtend.getOrderReturnCommodities();
        if (orderReturnCommodities == null || orderReturnCommodities.size() == 0) {
            throw new RuntimeException("没有退换货商品");
        }
        //新增商品加入订单
        for (int i = 0; i < list.size(); i++) {
            OrderReturnCommodityExtend orderReturnCommodity = list.get(i);
            Integer order_return_commodity_type = orderReturnCommodity.getOrder_return_commodity_type();
            if (order_return_commodity_type == OrderConstants.ORDER_RETURN_COMMODITY_TYPE_ADD) {//新增商品
                OrderCommodity orderCommodity = createReturnOrderCommodity(orderReturnCommodity, orderExtend.getOrder_id());
                orderReturnCommodity.setOrder_commodity_id(orderCommodity.getOrder_commodity_id());
                orderReturnCommodityMapper.updateByPrimaryKey(orderReturnCommodity);
            }else if (order_return_commodity_type == OrderConstants.ORDER_RETURN_COMMODITY_TYPE_RETURN){//退货
                Integer order_commodity_id = orderReturnCommodity.getOrder_commodity_id();
                OrderCommodityExtend orderCommodityExtend = null;
                List<OrderCommodityExtend> orderCommodities = orderExtend.getOrderCommodities();
                for (int j = 0; j < orderCommodities.size(); j++) {
                    orderCommodityExtend = orderCommodities.get(j);
                    if (orderCommodityExtend.getOrder_commodity_id().equals(order_commodity_id)) {
                        break;
                    }
                }
                //if (orderCommodityExtend != null && orderCommodityExtend.getOrder_commodity_status())
                //退货，修改订单商品数量
                orderCommodityExtend.setOrder_commodity_return_num(orderReturnCommodity.getOrder_return_commodity_num() + orderCommodityExtend.getOrder_commodity_return_num());//
                orderCommodityExtend.setCommodity_num(orderCommodityExtend.getOrder_commodity_num() - orderCommodityExtend.getOrder_commodity_return_num());
                orderCommodityExtend.setActivity_commodity_id(orderReturnCommodity.getActivity_commodity_id());
                orderCommodityExtend.setOrder_commodity_return_status(-1);
                orderCommodityMapper.updateByPrimaryKey(orderCommodityExtend);

                Integer inventory_history_detail_id = orderReturnCommodity.getInventory_history_detail_id();
                InventoryHistoryDetail inventoryHistoryDetail = inventoryHistoryService.getInventoryHistoryDetailById(inventory_history_detail_id);
                int inventory_history_return_num = inventoryHistoryDetail.getInventory_history_return_num();
                inventory_history_return_num = inventory_history_return_num + orderReturnCommodity.getOrder_return_commodity_num();
                if (inventory_history_return_num > inventoryHistoryDetail.getInventory_history_num()) {
                    throw new RuntimeException("退货数量不能大于出库数量");
                }
                inventoryHistoryDetail.setInventory_history_return_num(inventory_history_return_num);
                inventoryHistoryService.updateInventoryHistoryDetail(inventoryHistoryDetail);
            }
        }
        //退货商品添加入库信息
        inventoryInService.createInventoryIn(orderReturnExtend, employee);

        orderReturnExtend.setOrder_return_check_status(1);
        orderReturnExtend.setOrder_return_check_employee_code(employee.getEmployee_code());
        orderReturnExtend.setOrder_return_check_employee_name(employee.getEmployee_name());
        orderReturnMapper.updateByPrimaryKey(orderReturnExtend);
    }

    private OrderCommodity createReturnOrderCommodity(OrderReturnCommodityExtend orderReturnCommodity, int order_id) {
        OrderCommodity orderCommodity = new OrderCommodity();
        orderCommodity.setCommodity_id(orderReturnCommodity.getCommodity_id());
        orderCommodity.setOrder_id(order_id);
        orderCommodity.setUnit_price(orderReturnCommodity.getUnit_price());
        orderCommodity.setBargain_price(orderReturnCommodity.getBargain_price());
        orderCommodity.setSample(orderReturnCommodity.getSample());
        orderCommodity.setOrder_commodity_remark(orderReturnCommodity.getOrder_commodity_remark());
        orderCommodity.setDiscount(orderReturnCommodity.getDiscount());
        orderCommodity.setOrder_commodity_status(OrderConstants.ORDER_COMMODITY_STATUS);
        orderCommodity.setReturn_commodity(orderReturnCommodity.getReturn_commodity() == null ? 0 : orderReturnCommodity.getReturn_commodity());
        orderCommodity.setRevision_fee(orderReturnCommodity.getRevision_fee());
        orderCommodity.setActivity_commodity_id(orderReturnCommodity.getActivity_commodity_id());
        orderCommodity.setCommodity_num(orderReturnCommodity.getOrder_return_commodity_num());
        orderCommodity.setOrder_commodity_num(orderReturnCommodity.getOrder_return_commodity_num());
        orderCommodity.setOrder_commodity_return_num(0);
        orderCommodity.setCommodity_total_price(orderCommodity.getBargain_price().multiply(new BigDecimal(orderCommodity.getOrder_commodity_num())));
        orderCommodity.setOrder_commodity_return_status(0);
        orderCommodity.setOrder_commodity_type(OrderConstants.ORDER_COMMODITY_TYPE_RETURN);
        orderCommodity.setRevision_size(orderReturnCommodity.getRevision_size());
        orderExtendMapper.createOrderCommodity(orderCommodity);

        return orderCommodity;
    }

    public void orderReturnConfirm(OrderReturnExtend orderReturnExtend, Employee employee) {
        List<OrderReturnCommodityExtend> list = orderReturnExtend.getOrderReturnCommodities();
        OrderExtend orderExtend = orderService.getOrderById(orderReturnExtend.getOrder_id());
        List<OrderCommodityExtend> orderCommodities = orderExtend.getOrderCommodities();
        if (orderCommodities == null || orderCommodities.size() == 0) {
            throw new RuntimeException("没有退换货商品");
        }
        for (int i = 0; i < list.size(); i++) {
            OrderReturnCommodityExtend orderReturnCommodityExtend = list.get(i);
            Integer order_commodity_id = orderReturnCommodityExtend.getOrder_commodity_id();
            if (order_commodity_id != null && order_commodity_id > 0) {
                OrderCommodityExtend orderCommodityExtend = null;
                for (int j = 0; j < orderCommodities.size(); j++) {
                    orderCommodityExtend = orderCommodities.get(j);
                    if (orderCommodityExtend.getOrder_commodity_id().equals(order_commodity_id)) {
                        break;
                    }
                }
                //退货，修改订单商品数量
                orderCommodityExtend.setOrder_commodity_return_num(orderReturnCommodityExtend.getOrder_return_commodity_num() + orderCommodityExtend.getOrder_commodity_return_num());//
                orderCommodityExtend.setCommodity_num(orderCommodityExtend.getOrder_commodity_num() - orderCommodityExtend.getOrder_commodity_return_num());
                orderCommodityExtend.setActivity_commodity_id(orderReturnCommodityExtend.getActivity_commodity_id());
                if(orderCommodityExtend.getOrder_commodity_status() > 300) {//已经采购
                    //TODO
                }
                orderCommodityExtend.setOrder_commodity_return_status(-1);
                orderCommodityMapper.updateByPrimaryKey(orderCommodityExtend);
            }else {
                OrderCommodity orderCommodity = createReturnOrderCommodity(orderReturnCommodityExtend, orderExtend.getOrder_id());
                orderReturnCommodityExtend.setOrder_commodity_id(orderCommodity.getOrder_commodity_id());
                orderReturnCommodityMapper.updateByPrimaryKey(orderReturnCommodityExtend);
            }
        }

        orderReturnExtend.setOrder_return_check_status(1);
        orderReturnExtend.setOrder_return_check_employee_code(employee.getEmployee_code());
        orderReturnExtend.setOrder_return_check_employee_name(employee.getEmployee_name());
        orderReturnMapper.updateByPrimaryKey(orderReturnExtend);

        orderExtend = orderService.getOrderById(orderExtend.getOrder_id());
        orderExtend.setReturn_status(0);
        //orderService.totalPrice(orderExtend);
        orderExtendMapper.updateOrder(orderExtend);
    }

    /**
     * 退换货扣除费用
     * @param orderReturnExtend
     * @param employee
     */
    @Transactional
    public void orderReturnFee(OrderReturnExtend orderReturnExtend, Employee employee) {
        List<OrderFeeExtend> orderFees = orderReturnExtend.getOrderFees();

        OrderReturnExtend orderReturnById = getOrderReturnById(orderReturnExtend.getOrder_return_id());
        List<OrderFee> orderFeeList = OrderFeeComponents.createOrderFee(orderFees, orderReturnById, employee);
        String s = OrderFeeComponents.orderFeeIds(orderFeeList);
        orderReturnById.setOrder_fee_ids(s);
        orderReturnById.setOrder_return_deduction_amount_pay_status(1);
        orderReturnById.setOrder_return_fee_check_status(0);
        orderReturnMapper.updateByPrimaryKey(orderReturnById);
    }

    @Transactional
    public void orderReturnFeeCheck(int order_return_id, Employee employee) {
        OrderReturnExtend orderReturn = getOrderReturnById(order_return_id);
        if (orderReturn.getOrder_return_fee_check_status() == 1) {//已经审核
            return;
        }
        List<OrderFeeExtend> list = OrderFeeComponents.orderFeeList(orderReturn.getOrder_fee_ids());
        OrderFeeComponents.checkOrderFee(list, employee);
        orderReturn.setOrder_return_fee_check_status(1);
        orderReturnMapper.updateByPrimaryKey(orderReturn);
    }


    public List<OrderReturnCommodityExtend> orderReturnReport(OrderSearchOption orderSearchOption) {
        return orderReturnExtendMapper.orderReturnReport(orderSearchOption);
    }

    public Integer countOrderReturnReport(OrderSearchOption orderSearchOption) {
        return orderReturnExtendMapper.countOrderReturnReport(orderSearchOption);
    }



}
