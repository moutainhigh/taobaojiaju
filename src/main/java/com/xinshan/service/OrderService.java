package com.xinshan.service;

import com.xinshan.components.activity.ActivityComponents;
import com.xinshan.components.employee.EmployeeComponent;
import com.xinshan.components.pay.PayComponents;
import com.xinshan.dao.OrderCommodityMapper;
import com.xinshan.dao.OrderCommodityReturnMapper;
import com.xinshan.dao.OrderCommoditySupplierMapper;
import com.xinshan.dao.extend.order.OrderExtendMapper;
import com.xinshan.model.*;
import com.xinshan.model.extend.commodity.CommodityActivity;
import com.xinshan.model.extend.employee.EmployeeExtend;
import com.xinshan.model.extend.order.OrderCommodityExtend;
import com.xinshan.model.extend.order.OrderExtend;
import com.xinshan.model.extend.user.UserExtend;
import com.xinshan.model.extend.user.UserShoppingCommodityExtend;
import com.xinshan.model.extend.user.UserShoppingExtend;
import com.xinshan.pojo.order.OrderSearchOption;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.SplitUtils;
import com.xinshan.utils.constant.activity.ActivityConstant;
import com.xinshan.utils.constant.order.OrderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mxt on 16-10-24.
 */
@Service
public class OrderService {

    @Autowired
    private OrderExtendMapper orderExtendMapper;
    @Autowired
    private OrderCommodityMapper orderCommodityMapper;
    @Autowired
    private CommodityService commodityService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private OrderCommodityReturnMapper orderCommodityReturnMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ValueAddedService valueAddedService;
    @Autowired
    private CashBackService cashBackService;
    @Autowired
    private OrderCommoditySupplierMapper orderCommoditySupplierMapper;

    @Transactional
    public synchronized void createOrder(Object o, Employee employee) {
        if (o instanceof OrderExtend) {
            createOrder((OrderExtend) o, employee);
        }else if (o instanceof UserShoppingExtend) {
            userShoppingCreateOrder((UserShoppingExtend) o);
        }
    }


    private void userShoppingCreateOrder(UserShoppingExtend userShoppingExtend) {
        if (!hasOrderCommodity(userShoppingExtend.getUserShoppingCommodities())) {
            //没有订单商品
            System.out.println("没有订单商品");
            return;
        }
        String employee_code = userShoppingExtend.getEmployee_code();
        EmployeeExtend employeeByCode = EmployeeComponent.getEmployeeByCode(employee_code);
        userService.createUser(userShoppingExtend.getUser_name(), userShoppingExtend.getUser_first_phone(),
                userShoppingExtend.getUser_address(), userShoppingExtend.getEmployee_code(), userShoppingExtend.getEmployee_name(),
                employeeByCode.getPosition_id(), null, null, null, userShoppingExtend.getUser_second_phone());
        OrderExtend order = new OrderExtend();
        order.setOrder_date(DateUtil.currentDate());
        order.setRecord_date(DateUtil.currentDate());
        order.setOrder_code(orderCode());
        order.setEmployee_code(userShoppingExtend.getEmployee_code());
        order.setEmployee_name(userShoppingExtend.getEmployee_name());
        order.setRecord_employee_name(userShoppingExtend.getEmployee_name());
        order.setRecord_employee_code(userShoppingExtend.getEmployee_code());
        order.setCustomer_name(userShoppingExtend.getUser_name());
        order.setCustomer_phone_number(userShoppingExtend.getUser_first_phone());
        order.setPosition_id(employeeByCode.getPosition_id());
        order.setDelivery_address(userShoppingExtend.getUser_address());
        order.setOrder_status(1);
        order.setPreferential_amount(new BigDecimal(0));
        order.setPosition_id(employeeByCode.getPosition_id());
        order.setTrans_purchase(0);
        orderExtendMapper.createOrder(order);
        List<UserShoppingCommodityExtend> userShoppingCommodities = userShoppingExtend.getUserShoppingCommodities();
        for (int i = 0; i < userShoppingCommodities.size(); i++) {
            UserShoppingCommodityExtend userShoppingCommodity = userShoppingCommodities.get(i);
            if (userShoppingCommodity.getCommodity_id() != null) {
                OrderCommodity orderCommodity = new OrderCommodity();
                orderCommodity.setCommodity_id(userShoppingCommodity.getCommodity_id());
                orderCommodity.setOrder_id(order.getOrder_id());
                orderCommodity.setOrder_commodity_status(OrderConstants.ORDER_COMMODITY_STATUS);
                orderCommodity.setReturn_commodity(0);
                orderCommodity.setOrder_commodity_return_status(0);
                orderCommodity.setCommodity_num(0);
                orderCommodity.setOrder_commodity_return_num(0);
                orderCommodity.setOrder_commodity_num(0);
                orderCommodity.setOrder_commodity_type(OrderConstants.ORDER_COMMODITY_TYPE_ORDER);
                orderExtendMapper.createOrderCommodity(orderCommodity);
                createOrderCommoditySupplier(orderCommodity);
            }
        }
    }

    private boolean hasOrderCommodity(List<UserShoppingCommodityExtend> userShoppingCommodities) {
        boolean b = false;
        for (int i = 0; i < userShoppingCommodities.size(); i++) {
            UserShoppingCommodityExtend userShoppingCommodity = userShoppingCommodities.get(i);
            if (userShoppingCommodity.getCommodity_id() != null) {
                b = true;
                break;
            }
        }
        return b;
    }

    private void createOrder(OrderExtend orderExtend, Employee employee) {
        orderExtend.setRecord_employee_code(employee.getEmployee_code());
        orderExtend.setRecord_employee_name(employee.getEmployee_name());
        orderExtend.setRecord_date(DateUtil.currentDate());
        if (orderExtend.getPreferential_amount() == null) {
            orderExtend.setPreferential_amount(new BigDecimal("0"));
        }
        if (orderExtend.getOrder_status() == null) {
            orderExtend.setOrder_status(1);
        }
        if (orderExtend.getOrder_date() == null) {
            orderExtend.setOrder_date(DateUtil.currentDate());
        }
        //补全订单时间 时分秒
        orderExtend.setOrder_date(DateUtil.date(orderExtend.getOrder_date()));
        orderExtend.setTrans_purchase(0);
        List<OrderCommodityExtend> list = orderExtend.getOrderCommodities();
        if (list == null || list.size() == 0) {
            throw new RuntimeException("订单中必须包含商品");
        }
        totalPrice(orderExtend);
        orderExtend.setOrder_code(orderCode());
        if (orderExtend.getPosition_id() == null) {
            if (employee.getPosition_id() == null) {
                orderExtend.setPosition_id(0);
            }else {
                orderExtend.setPosition_id(employee.getPosition_id());
            }
        }
        userService.createUser(orderExtend.getCustomer_name(), orderExtend.getCustomer_phone_number(),
                orderExtend.getDelivery_address(), orderExtend.getEmployee_code(), orderExtend.getEmployee_name(),
                orderExtend.getPosition_id(), orderExtend.getProvince_zip(), orderExtend.getCity_zip(),
                orderExtend.getDistrict_zip(), orderExtend.getCustomer_second_phone());
        orderExtendMapper.createOrder(orderExtend);
        String valueAddedCardCode = null;
        for (int i = 0; i < list.size(); i++) {
            OrderCommodityExtend orderCommodity = list.get(i);
            orderCommodity.setOrder_id(orderExtend.getOrder_id());
            if (orderCommodity.getReturn_commodity() == null) {
                orderCommodity.setReturn_commodity(0);
            }
            orderCommodity.setOrder_commodity_status(OrderConstants.ORDER_COMMODITY_STATUS);
            orderCommodity.setOrder_commodity_return_status(0);

            //商品退货数量，新添加为0
            orderCommodity.setOrder_commodity_return_num(0);
            //商品退货前数量，新添加为订单商品数量
            orderCommodity.setOrder_commodity_num(orderCommodity.getCommodity_num());
            if (orderCommodity.getRevision_fee() == null) {
                orderCommodity.setRevision_fee(new BigDecimal("0"));
            }
            orderCommodity.setOrder_commodity_type(OrderConstants.ORDER_COMMODITY_TYPE_ORDER);
            orderExtendMapper.createOrderCommodity(orderCommodity);

            createOrderCommoditySupplier(orderCommodity);

            OrderCommodityValueAddedCard orderCommodityValueAddedCard = orderCommodity.getOrderCommodityValueAddedCard();
            if (orderCommodityValueAddedCard != null && orderCommodityValueAddedCard.getCard_code() != null
                    && !orderCommodityValueAddedCard.getCard_code().equals("")) {
                orderCommodityValueAddedCard.setOrder_commodity_id(orderCommodity.getOrder_commodity_id());
                valueAddedCardCode = orderCommodityValueAddedCard.getCard_code();
                ValueAddedCard valueAddedCard = valueAddedService.getValueAddedCardByCode(valueAddedCardCode);
                if (valueAddedCard.getCard_enable() == 0 || valueAddedCard.getOrder_id() != null) {
                    //增值卡不可用
                    throw new RuntimeException("增值卡已使用");
                }
                orderCommodityValueAddedCard.setCard_amount(valueAddedCard.getPay_amount());
                orderExtendMapper.createOrderCommodityValueAddedCard(orderCommodityValueAddedCard);
            }
        }
        List<OrderPay> orderPays = orderExtend.getOrderPays();
        if (orderPays == null || orderPays.size() == 0) {
            throw new RuntimeException("没有付款信息");
        }

        orderPays = PayComponents.createOrderPay(orderExtend, employee);
        orderExtend.setOrder_pay_ids(PayComponents.orderPayIds(orderPays));
        orderExtendMapper.updateOrderPayIds(orderExtend);

        List<OrderCarryFee> orderCarryFees = orderExtend.getOrderCarryFees();
        if (orderCarryFees != null) {
            createCarryFee(orderCarryFees, orderExtend.getOrder_id());
        }

        //增值卡
        if (valueAddedCardCode != null) {
            valueAddedService.valueAddedCardOrder(valueAddedCardCode, orderExtend);
        }
    }

    private void createOrderCommoditySupplier(OrderCommodity orderCommodity) {
        OrderCommoditySupplier orderCommoditySupplier = new OrderCommoditySupplier();
        orderCommoditySupplier.setOrder_commodity_id(orderCommodity.getOrder_commodity_id());
        orderCommoditySupplier.setOrder_commodity_supplier_status(0);
        orderExtendMapper.createOrderCommoditySupplier(orderCommoditySupplier);
    }

    @Transactional
    public void createOrderCommoditySupplier(OrderCommoditySupplier orderCommoditySupplier) {
        orderExtendMapper.createOrderCommoditySupplier(orderCommoditySupplier);
    }
    @Transactional
    public void updateOrderCommoditySupplier(OrderCommoditySupplier orderCommoditySupplier) {
        orderCommoditySupplierMapper.updateByPrimaryKey(orderCommoditySupplier);
    }

    private void createCarryFee(List<OrderCarryFee> orderCarryFees, int order_id) {
        for (int i = 0; i < orderCarryFees.size(); i++) {
            OrderCarryFee orderCarryFee = orderCarryFees.get(i);
            orderCarryFee.setOrder_id(order_id);
            if (orderCarryFee.getOrder_carry_fee() == null) {
                orderCarryFee.setOrder_carry_fee(new BigDecimal("0"));
            }
            orderCarryFee.setCarry_fee_type(OrderConstants.order_pay_type_add_order);
            orderExtendMapper.createCarryFee(orderCarryFee);
        }
    }

    public List<OrderCarryFee> orderCarryFees(int order_id) {
        return orderExtendMapper.orderCarryFees(order_id);
    }

    public Integer countOrder(OrderSearchOption orderSearchOption) {
        return orderExtendMapper.countOrder(orderSearchOption);
    }

    private String orderCode() {
        String n = orderExtendMapper.todayOrderNum(DateUtil.format(DateUtil.currentDate(), "yyMMdd"));//O161031001
        if (n == null) {
            return "O"+Long.parseLong(DateUtil.format(DateUtil.currentDate(), "yyMMdd"))*100+1l;
        }
        n = n.substring(1, n.length());
        int m = Integer.parseInt(n);
        m++;
        return "O"+m;
    }

    @Transactional
    public void updateOrder(Order order) {
        orderExtendMapper.updateOrder(order);
    }

    @Transactional
    public void updateOrder(OrderExtend orderExtend, Employee employee) {
        OrderExtend order = getOrderById(orderExtend.getOrder_id());
        totalPrice(orderExtend);
        orderExtend.setOrder_code(order.getOrder_code());
        if (orderExtend.getOrder_status() == null) {
            orderExtend.setOrder_status(1);
        }
        if (orderExtend.getPreferential_amount() == null) {
            orderExtend.setPreferential_amount(new BigDecimal("0"));
        }
        if (orderExtend.getRecord_employee_code() == null) {
            orderExtend.setRecord_employee_code(employee.getEmployee_code());
            orderExtend.setRecord_employee_name(employee.getEmployee_name());
        }
        orderExtend.setOrder_date(DateUtil.date(orderExtend.getOrder_date()));
        userService.createUser(orderExtend.getCustomer_name(), orderExtend.getCustomer_phone_number(),
                orderExtend.getDelivery_address(), orderExtend.getEmployee_code(), orderExtend.getEmployee_name(),
                orderExtend.getPosition_id(), orderExtend.getProvince_zip(), orderExtend.getCity_zip(),
                orderExtend.getDistrict_zip(), orderExtend.getCustomer_second_phone());
        orderExtendMapper.updateOrder(orderExtend);

        List<OrderCommodity> orderCommodities = orderExtendMapper.getOrderCommodities(orderExtend.getOrder_id());
        for (int i = 0; i < orderCommodities.size(); i++) {
            OrderCommodity orderCommodity = orderCommodities.get(i);
            orderExtendMapper.deleteOrderCommodityValueAddedCard(orderCommodity.getOrder_commodity_id());
            orderCommodityMapper.deleteByPrimaryKey(orderCommodity.getOrder_commodity_id());
            orderExtendMapper.deleteOrderCommoditySupplier(orderCommodity.getOrder_commodity_id());
        }

        List<OrderCommodityExtend> list = orderExtend.getOrderCommodities();
        for (int i = 0; i < list.size(); i++) {
            OrderCommodityExtend orderCommodity = list.get(i);
            orderCommodity.setOrder_id(orderExtend.getOrder_id());
            if (orderCommodity.getReturn_commodity() == null) {
                orderCommodity.setReturn_commodity(0);
            }
            orderCommodity.setOrder_commodity_return_status(0);
            orderCommodity.setOrder_commodity_status(OrderConstants.ORDER_COMMODITY_STATUS);
            //商品退货数量，新添加为0
            orderCommodity.setOrder_commodity_return_num(0);
            //商品退货前数量，新添加为订单商品数量
            orderCommodity.setOrder_commodity_num(orderCommodity.getCommodity_num());
            if (orderCommodity.getRevision_fee() == null) {
                orderCommodity.setRevision_fee(new BigDecimal("0"));
            }
            orderCommodity.setOrder_commodity_type(OrderConstants.ORDER_COMMODITY_TYPE_ORDER);
            orderExtendMapper.createOrderCommodity(orderCommodity);

            createOrderCommoditySupplier(orderCommodity);

            OrderCommodityValueAddedCard orderCommodityValueAddedCard = orderCommodity.getOrderCommodityValueAddedCard();
            if (orderCommodityValueAddedCard != null && orderCommodityValueAddedCard.getCard_code() != null) {
                orderCommodityValueAddedCard.setOrder_commodity_id(orderCommodity.getOrder_commodity_id());
                String valueAddedCardCode = orderCommodityValueAddedCard.getCard_code();
                if (valueAddedCardCode != null && !valueAddedCardCode.equals("")) {
                    ValueAddedCard valueAddedCard = valueAddedService.getValueAddedCardByCode(valueAddedCardCode);
                    if (valueAddedCard != null) {
                        if (valueAddedCard.getCard_enable() == 0) {
                            //增值卡不可用

                        }
                        orderCommodityValueAddedCard.setCard_amount(valueAddedCard.getPay_amount());
                        orderExtendMapper.createOrderCommodityValueAddedCard(orderCommodityValueAddedCard);
                    }
                }
            }
        }

        String order_pay_ids = order.getOrder_pay_ids();
        List<OrderPay> orderPayList = PayComponents.orderPays(order_pay_ids);
        for (int i = 0; i < orderPayList.size(); i++) {
            OrderPay orderPay = orderPayList.get(i);
            PayComponents.deleteOrderPay(orderPay);
        }

        List<OrderPay> orderPays = PayComponents.createOrderPay(orderExtend, employee);
        orderExtend.setOrder_pay_ids(PayComponents.orderPayIds(orderPays));
        orderExtendMapper.updateOrderPayIds(orderExtend);

        orderExtendMapper.deleteCarryFeeByOrderId(order.getOrder_id());
        List<OrderCarryFee> orderCarryFees = orderExtend.getOrderCarryFees();
        if (orderCarryFees != null) {
            createCarryFee(orderCarryFees, orderExtend.getOrder_id());
        }
    }

    @Transactional
    public void updateOrderPayIds(Order order) {
        orderExtendMapper.updateOrderPayIds(order);
    }

    public void totalPrice(OrderExtend orderExtend) {
        List<OrderCommodityExtend> list = orderExtend.getOrderCommodities();
        int total_num = 0;
        BigDecimal order_total_price = new BigDecimal("0");
        for (int i = 0; i < list.size(); i++) {
            OrderCommodityExtend orderCommodity = list.get(i);
            BigDecimal bargain_price = orderCommodity.getBargain_price();
            BigDecimal revision_fee = orderCommodity.getRevision_fee();
            if (revision_fee == null) {
                revision_fee = new BigDecimal("0");
            }
            orderCommodity.setRevision_fee(revision_fee);
            BigDecimal commodity_total_price = (bargain_price.add(revision_fee)).multiply(new BigDecimal(orderCommodity.getCommodity_num()));

            //增值卡
            OrderCommodityValueAddedCard orderCommodityValueAddedCard = orderCommodity.getOrderCommodityValueAddedCard();
            if (orderCommodityValueAddedCard != null && orderCommodityValueAddedCard.getOrder_commodity_value_added_card_id() != null) {
                commodity_total_price = commodity_total_price.subtract(orderCommodityValueAddedCard.getCard_real_amount());
            }

            orderCommodity.setCommodity_total_price(commodity_total_price);
            total_num += orderCommodity.getCommodity_num();

            order_total_price = order_total_price.add(orderCommodity.getCommodity_total_price());
        }
        orderExtend.setTotal_num(total_num);

        orderExtend.setOrder_total_price(order_total_price);//订单商品总费用
        /*BigDecimal total_price = order_total_price;
        List<OrderCarryFee> orderCarryFees = orderExtend.getOrderCarryFees();
        if (orderCarryFees != null) {
            for (int i = 0; i < orderCarryFees.size(); i++) {
                OrderCarryFee orderCarryFee = orderCarryFees.get(i);
                BigDecimal order_carry_fee = orderCarryFee.getOrder_carry_fee();
                if (order_carry_fee == null) {
                    order_carry_fee = new BigDecimal("0");
                }
                total_price = total_price.add(order_carry_fee);
            }
        }

        if (orderExtend.getPreferential_amount() != null) {//商品总金额 - 优惠金额
            total_price = total_price.subtract(orderExtend.getPreferential_amount());
        }
        orderExtend.setTotal_price(total_price);*///订单总费用，包括搬运费优惠费用
    }

    @Transactional
    public void deleteOrder(int order_id) {
        orderExtendMapper.deleteOrder(order_id);
    }

    public List<OrderExtend> orderList(List<Integer> orderIds) {
        if (orderIds.size() == 0) {
            return new ArrayList<OrderExtend>();
        }
        OrderSearchOption orderSearchOption = new OrderSearchOption();
        orderSearchOption.setOrderIds(orderIds);
        List<OrderExtend> list = orderExtendMapper.orderList(orderSearchOption);
        for (int i = 0; i < list.size(); i++) {
            OrderExtend orderExtend = list.get(i);

            orderExtend.setOrderPays(PayComponents.orderPays(orderExtend.getOrder_pay_ids()));

            List<OrderCommodityExtend> orderCommodityExtends = orderExtend.getOrderCommodities();
            for (int j = 0; j < orderCommodityExtends.size(); j++) {
                OrderCommodityExtend oce = orderCommodityExtends.get(j);
                int commodity_id = oce.getCommodity_id();
                Commodity commodity = commodityService.getCommodityById(commodity_id);
                oce.setCommodity(commodity);
                if (commodity != null && commodity.getSupplier_id() != null) {
                    oce.setSupplier(supplierService.getSupplierById(commodity.getSupplier_id()));
                }
                if (oce.getActivity_commodity_id() != null) {
                    oce.setActivityCommodity(activityService.getActivityCommodityById(oce.getActivity_commodity_id()));
                }
                oce.setSellLimit(ActivityComponents.sellLimitCommodityActivity(commodity));
                oce.setOrderCommodityValueAddedCard(orderExtendMapper.getOrderCommodityCard(oce.getOrder_commodity_id()));
                oce.setCashBackCommodity(cashBackService.getCashBackCommodity(oce.getOrder_commodity_id()));
            }
            BigDecimal pay_total_amount = new BigDecimal("0");
            List<OrderPay> orderPays = orderExtend.getOrderPays();
            for (int j = 0; j < orderPays.size(); j++) {
                OrderPay orderPay = orderPays.get(j);
                if (orderPay.getPay_amount() != null) {
                    pay_total_amount = pay_total_amount.add(orderPay.getPay_amount());
                }
            }

            orderExtend.setPay_total_amount(pay_total_amount);
            List<OrderCommodityReturn> orderCommodityReturns = orderExtendMapper.orderCommodityReturns(orderExtend.getOrder_id());
            orderExtend.setOrderCommodityReturns(orderCommodityReturns);

            UserExtend user = userService.getUserByPhone(orderExtend.getCustomer_phone_number());
            orderExtend.setUser(user);

            orderExtend.setGift(orderExtendMapper.orderGiftNum(orderExtend.getOrder_id()));
            orderExtend.setGold_egg(orderExtendMapper.goldEggNum(orderExtend.getOrder_id()));
        }
        return list;
    }

    public List<OrderExtend> orderList(List<Integer> orderIds, List<Integer> supplierIds) {
        if (orderIds.size() == 0) {
            return new ArrayList<OrderExtend>();
        }
        OrderSearchOption orderSearchOption = new OrderSearchOption();
        orderSearchOption.setOrderIds(orderIds);
        List<OrderExtend> list = orderExtendMapper.orderList(orderSearchOption);
        for (int i = 0; i < list.size(); i++) {
            OrderExtend orderExtend = list.get(i);

            orderExtend.setOrderPays(PayComponents.orderPays(orderExtend.getOrder_pay_ids()));

            List<OrderCommodityExtend> orderCommodityExtends = orderExtend.getOrderCommodities();
            for (int j = 0; j < orderCommodityExtends.size(); j++) {
                OrderCommodityExtend oce = orderCommodityExtends.get(j);
                int commodity_id = oce.getCommodity_id();
                Commodity commodity = commodityService.getCommodityById(commodity_id);
                if (!supplierIds.contains(commodity.getSupplier_id())) {
                    continue;
                }
                oce.setCommodity(commodity);
                if (commodity != null && commodity.getSupplier_id() != null) {
                    oce.setSupplier(supplierService.getSupplierById(commodity.getSupplier_id()));
                }
                if (oce.getActivity_commodity_id() != null) {
                    oce.setActivityCommodity(activityService.getActivityCommodityById(oce.getActivity_commodity_id()));
                }
                oce.setSellLimit(ActivityComponents.sellLimitCommodityActivity(commodity));
                oce.setOrderCommodityValueAddedCard(orderExtendMapper.getOrderCommodityCard(oce.getOrder_commodity_id()));
                oce.setCashBackCommodity(cashBackService.getCashBackCommodity(oce.getOrder_commodity_id()));
            }
            BigDecimal pay_total_amount = new BigDecimal("0");
            List<OrderPay> orderPays = orderExtend.getOrderPays();
            for (int j = 0; j < orderPays.size(); j++) {
                OrderPay orderPay = orderPays.get(j);
                if (orderPay.getPay_amount() != null) {
                    pay_total_amount = pay_total_amount.add(orderPay.getPay_amount());
                }
            }

            orderExtend.setPay_total_amount(pay_total_amount);
            List<OrderCommodityReturn> orderCommodityReturns = orderExtendMapper.orderCommodityReturns(orderExtend.getOrder_id());
            orderExtend.setOrderCommodityReturns(orderCommodityReturns);

            UserExtend user = userService.getUserByPhone(orderExtend.getCustomer_phone_number());
            orderExtend.setUser(user);

            orderExtend.setGift(orderExtendMapper.orderGiftNum(orderExtend.getOrder_id()));
            orderExtend.setGold_egg(orderExtendMapper.goldEggNum(orderExtend.getOrder_id()));
        }
        return list;
    }

    public List<Map> orderExport(List<Integer> orderIds) {
        return orderExtendMapper.orderExport(orderIds);
    }

    public List<Integer> orderIds(OrderSearchOption orderSearchOption) {
        return orderExtendMapper.orderIds(orderSearchOption);
    }

    public Order getOrderById1(int order_id) {
        return orderExtendMapper.getOrderById(order_id);
    }

    public OrderExtend getOrderById(int order_id) {
        OrderSearchOption orderSearchOption = new OrderSearchOption();
        List<Integer> orderIds = new ArrayList<>();
        orderIds.add(order_id);
        orderSearchOption.setOrderIds(orderIds);
        List<OrderExtend> list = orderExtendMapper.orderList(orderSearchOption);
        if (list != null && list.size() > 0) {
            OrderExtend orderExtend = list.get(0);
            List<OrderCommodityExtend> orderCommodityExtends = orderExtend.getOrderCommodities();
            for (int j = 0; j < orderCommodityExtends.size(); j++) {
                OrderCommodityExtend oce = orderCommodityExtends.get(j);
                int commodity_id = oce.getCommodity_id();
                Commodity commodity = commodityService.getCommodityById(commodity_id);
                oce.setCommodity(commodity);
            }
            orderExtend.setGift(orderExtendMapper.orderGiftNum(order_id));
            orderExtend.setGold_egg(orderExtendMapper.goldEggNum(order_id));
            return orderExtend;
        }
        return null;
    }

    @Transactional
    public void createOrderCommodityReturn(OrderCommodityReturn orderCommodityReturn, Employee employee) {
        int order_id = orderCommodityReturn.getOrder_id();
        List<Integer> orderCommodityIds = SplitUtils.splitToList(orderCommodityReturn.getOrder_commodity_ids(), ",");
        orderCommodityReturn.setOrder_commodity_ids("");
        orderCommodityReturn.setOrder_commodity_return_date(DateUtil.currentDate());
        orderCommodityReturn.setReturn_employee_code(employee.getEmployee_code());
        orderCommodityReturn.setReturn_employee_name(employee.getEmployee_name());
        OrderExtend orderExtend = getOrderById(order_id);
        List<OrderCommodityExtend> orderCommodities = orderExtend.getOrderCommodities();
        for (int i = 0; i < orderCommodities.size(); i++) {
            OrderCommodityExtend orderCommodityExtend = orderCommodities.get(i);
            //确定退货，不是已经退货状态
            if (orderCommodityIds.contains(orderCommodityExtend.getOrder_commodity_id()) && orderCommodityExtend.getOrder_commodity_status() != -1) {
                if (orderCommodityReturn.getOrder_commodity_ids().equals("")) {
                    orderCommodityReturn.setOrder_commodity_ids(orderCommodityExtend.getOrder_commodity_id()+"");
                }else {
                    orderCommodityReturn.setOrder_commodity_ids(orderCommodityReturn.getOrder_commodity_ids() + "," +orderCommodityExtend.getOrder_commodity_id());
                }
                orderCommodityExtend.setOrder_commodity_return_status(-1);
            }
            orderCommodityMapper.updateByPrimaryKey(orderCommodityExtend);
        }
        orderCommodityReturn.setReturn_num(0);
        orderExtendMapper.createOrderCommodityReturn(orderCommodityReturn);
    }

    public List<OrderCommodityReturn> orderCommodityReturns (int order_id){
        return orderExtendMapper.orderCommodityReturns(order_id);
    }

    public OrderCommodityReturn getReturnById(int order_commodity_return_id) {
        return orderCommodityReturnMapper.selectByPrimaryKey(order_commodity_return_id);
    }

    public OrderCommodity getOrderCommodity(int order_id, int commodity_id) {
        return orderExtendMapper.getOrderCommodity(order_id, commodity_id);
    }

    public OrderCommodity getOrderCommodity(int order_commodity_id) {
        return orderCommodityMapper.selectByPrimaryKey(order_commodity_id);
    }

    public OrderCommodityExtend getOrderCommodityExtend(int order_commodity_id) {
        OrderSearchOption orderSearchOption = new OrderSearchOption();
        orderSearchOption.setOrder_commodity_id(order_commodity_id);
        List<OrderCommodityExtend> list = orderExtendMapper.orderCommodityExtends(orderSearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public void updateOrderCommodity(OrderCommodity orderCommodity) {
        orderCommodityMapper.updateByPrimaryKey(orderCommodity);
    }

    @Transactional
    public void updateOrderCommodityWithTran(OrderCommodity orderCommodity) {
        orderCommodityMapper.updateByPrimaryKey(orderCommodity);
    }

    public List<HashMap> orderFeeStatics(OrderSearchOption orderSearchOption) {
        return orderExtendMapper.orderFeeStatics(orderSearchOption);
    }

    public BigDecimal orderCarryFeeStatics(OrderSearchOption orderSearchOption) {
        return orderExtendMapper.orderCarryFeeStatics(orderSearchOption);
    }

    public List<OrderCommodityExtend> orderCommodityExtends(OrderSearchOption orderSearchOption) {
        List<OrderCommodityExtend> list = orderExtendMapper.orderCommodityExtends(orderSearchOption);
        for (int i = 0; i < list.size(); i++) {
            OrderCommodityExtend orderCommodity = list.get(i);
            if (orderCommodity.getActivity_commodity_id() != null) {
                orderCommodity.setActivityCommodity(activityService.getActivityCommodityById(orderCommodity.getActivity_commodity_id()));
            }
        }
        return list;
    }

    public Integer countOrderCommodity(OrderSearchOption orderSearchOption) {
        return orderExtendMapper.countOrderCommodity(orderSearchOption);
    }

    public Map orderCommodityStatics(OrderSearchOption orderSearchOption) {
        return orderExtendMapper.orderCommodityStatics(orderSearchOption);
    }

    public List<Map> simpleOrderCommodities(OrderSearchOption orderSearchOption) {
        return orderExtendMapper.simpleOrderCommodities(orderSearchOption);
    }
    public BigDecimal purchaseTotalPrice(OrderSearchOption orderSearchOption) {
        return orderExtendMapper.purchaseTotalPrice(orderSearchOption);
    }

    public List<OrderCommodityExtend> cashBackCommodities(int order_id) {
        OrderSearchOption orderSearchOption = new OrderSearchOption();
        orderSearchOption.setOrder_id(order_id);
        List<OrderCommodityExtend> list = orderExtendMapper.cashBackCommodities(orderSearchOption);
        for (int i = 0; i < list.size(); i++) {
            OrderCommodityExtend orderCommodityExtend = list.get(i);
            Commodity commodity = orderCommodityExtend.getCommodity();
            CommodityActivity commodityActivity = ActivityComponents.getByCommodity(commodity);
            orderCommodityExtend.setCommodityActivity(commodityActivity);
            orderCommodityExtend.setActivities(ActivityComponents.commodityActivities(ActivityConstant.ACTIVITY_TYPE_CASH_BACK, commodity));
        }
        return list;
    }

    public List<Map> daogouList(OrderSearchOption orderSearchOption) {
        return orderExtendMapper.daogouList(orderSearchOption);
    }

    public BigDecimal countPreferential(OrderSearchOption orderSearchOption) {
        return orderExtendMapper.countPreferential(orderSearchOption);
    }
}
