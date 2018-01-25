package com.xinshan.components.pay;

import com.xinshan.components.order.OrderComponents;
import com.xinshan.model.Employee;
import com.xinshan.model.OrderPay;
import com.xinshan.model.extend.order.OrderExtend;
import com.xinshan.model.extend.order.OrderReturnExtend;
import com.xinshan.pojo.pay.PaySearchOption;
import com.xinshan.service.PayService;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.SplitUtils;
import com.xinshan.utils.constant.order.OrderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mxt on 17-4-13.
 */
@Component
public class PayComponents {

    public static final int pay_source_cash = 1;//现金
    public static final int pay_source_zhongyuan_bank = 2;//中原银行
    public static final int pay_source_xingye_bank = 3;//兴业银行
    public static final int pay_source_tihuoquan = 4;//4提货券

    public static final int pay_type_order = 1;//,1下单付款，
    public static final int pay_type_order_add = 2;//2退货新增商品付款，
    public static final int pay_type_return_commodity = 3;//退换货退款或收款
    public static final int pay_type_after_sales_return = 4;//4售后退货退款，
    public static final int pay_type_value_card = 5;//5增值卡收款
    public static final int pay_type_special_right_sell = 6;//6特价权活动
    public static final int pay_type_ = 7;//退换货费用

    public static final int PAY_RETURN_RECEIPT = 1;//收款
    public static final int PAY_RETURN_REFUNDS = 2;//退款

    private static PayService payService;
    @Autowired
    public void setPayService(PayService payService) {
        PayComponents.payService = payService;
    }

    /**
     * 收款
     * @param
     * @param pay_amount    收款金额
     * @param employee      收款人
     * @param pay_source    收款渠道 1现金，2中原银行，3兴业银行，4提货券
     * @param pay_type      收款类型 付款类型,1下单付款，2退货新增商品付款，3退货退款（金额为负）,4售后退货退款，5增值卡收款
     */
    public static OrderPay pay(BigDecimal pay_amount, Employee employee, int pay_source, int pay_type) {
        OrderPay orderPay = new OrderPay();
        if (orderPay.getPay_amount() != null && orderPay.getPay_amount().doubleValue() > 0) {
            //付款金额>0 收款
            orderPay.setPay_return(PAY_RETURN_RECEIPT);
        }else if (orderPay.getPay_amount() != null && orderPay.getPay_amount().doubleValue() < 0){
            //付款金额<0 退款
            orderPay.setPay_return(PAY_RETURN_REFUNDS);
        }
        orderPay.setNeed_amount(pay_amount);
        orderPay.setPay_amount(pay_amount);
        orderPay.setPay_date(DateUtil.currentDate());
        orderPay.setCashier_employee_code(employee.getEmployee_code());
        orderPay.setCashier_employee_name(employee.getEmployee_name());
        if (employee.getPosition_id() != null) {
            orderPay.setPosition_id(employee.getPosition_id());
        }else {
            orderPay.setPosition_id(0);
        }
        //导购岗位id
        orderPay.setDaogou_position_id(orderPay.getPosition_id());
        orderPay.setPay_source(pay_source);
        orderPay.setOrder_pay_type(pay_type);
        orderPay.setPay_return(PAY_RETURN_RECEIPT);
        payService.createOrderPay(orderPay);
        return orderPay;
    }

    /**
     * 订单付款
     * @param orderExtend
     * @param employee
     * @return
     */
    public static List<OrderPay> createOrderPay(OrderExtend orderExtend, Employee employee) {
        List<OrderPay> orderPays = orderExtend.getOrderPays();
        List<OrderPay> list = new ArrayList<>();
        for (int i = 0; i < orderPays.size(); i++) {
            OrderPay orderPay = orderPays.get(i);
            if ((orderPay.getNeed_amount() == null || orderPay.getNeed_amount().doubleValue() == 0)
                    && (orderPay.getPay_amount() == null || orderPay.getPay_amount().doubleValue() == 0)){
                continue;
            }
            if (orderPay.getPay_amount() != null && orderPay.getPay_amount().doubleValue() > 0) {
                //付款金额>0 收款
                orderPay.setPay_return(PAY_RETURN_RECEIPT);
            }else if (orderPay.getPay_amount() != null && orderPay.getPay_amount().doubleValue() < 0){
                //付款金额<0 退款
                orderPay.setPay_return(PAY_RETURN_REFUNDS);
            }
            orderPay.setOrder_id(orderExtend.getOrder_id());
            orderPay.setPay_date(DateUtil.currentDate());
            orderPay.setCashier_employee_code(employee.getEmployee_code());
            orderPay.setCashier_employee_name(employee.getEmployee_name());
            if (employee.getPosition_id() != null) {
                orderPay.setPosition_id(employee.getPosition_id());
            }else {
                orderPay.setPosition_id(0);
            }

            orderPay.setDaogou_position_id(orderExtend.getPosition_id());//导购岗位id
            orderPay.setDaogou_employee_code(orderExtend.getEmployee_code());//导购工号
            orderPay.setDaogou_employee_name(orderExtend.getEmployee_name());//导购名

            orderPay.setOrder_pay_type(OrderConstants.order_pay_type_add_order);
            payService.createOrderPay(orderPay);
            list.add(orderPay);
        }
        return list;
    }

    public static List<OrderPay> createOrderPays(List<OrderPay> orderPays, Employee employee,
                                                 OrderReturnExtend orderReturnExtend, int pay_type) {
        List<OrderPay> list = new ArrayList<>();
        boolean remark = false;//是否添加备注
        for (int i = 0; i < orderPays.size(); i++) {
            OrderPay orderPay = orderPays.get(i);
            if ((orderPay.getNeed_amount() == null || orderPay.getNeed_amount().doubleValue() == 0)
                    && (orderPay.getPay_amount() == null || orderPay.getPay_amount().doubleValue() == 0) && remark){
                continue;
            }
            if (orderPay.getPay_amount() != null && orderPay.getPay_amount().doubleValue() > 0) {
                //付款金额>0 收款
                orderPay.setPay_return(PAY_RETURN_RECEIPT);
            }else if (orderPay.getPay_amount() != null && orderPay.getPay_amount().doubleValue() < 0){
                //付款金额<0 退款
                orderPay.setPay_return(PAY_RETURN_REFUNDS);
            }else {
                orderPay.setPay_return(0);
            }
            if (orderPay.getPay_amount() == null) {
                orderPay.setPay_amount(new BigDecimal("0"));
            }
            if (orderPay.getNeed_amount() == null) {
                orderPay.setNeed_amount(new BigDecimal("0"));
            }
            orderPay.setOrder_id(orderReturnExtend.getOrder_id());
            orderPay.setOrder_return_id(orderReturnExtend.getOrder_return_id());
            orderPay.setPay_date(DateUtil.currentDate());
            orderPay.setCashier_employee_code(employee.getEmployee_code());
            orderPay.setCashier_employee_name(employee.getEmployee_name());
            orderPay.setOrder_pay_type(pay_type);
            if (employee.getPosition_id() != null) {
                orderPay.setPosition_id(employee.getPosition_id());
            }else {
                orderPay.setPosition_id(0);
            }

            OrderExtend orderExtend = OrderComponents.getOrderById(orderReturnExtend.getOrder_id());
            orderPay.setDaogou_position_id(orderExtend.getPosition_id());//导购岗位id
            orderPay.setDaogou_employee_code(orderExtend.getEmployee_code());//导购工号
            orderPay.setDaogou_employee_name(orderExtend.getEmployee_name());//导购名
            remark = true;
            payService.createOrderPay(orderPay);
            list.add(orderPay);
        }
        return list;
    }

    /**
     * 返回支付记录id字符串，逗号分割
     * @param orderPays
     * @return
     */
    public static String orderPayIds(List<OrderPay> orderPays) {
        return SplitUtils.listToString(orderPayIdList(orderPays));
    }

    /**
     * 返回支付记录id list
     * @param orderPays
     * @return
     */
    public static List<Integer> orderPayIdList(List<OrderPay> orderPays) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < orderPays.size(); i++) {
            list.add(orderPays.get(i).getOrder_pay_id());
        }
        return list;
    }

    /**
     * 删除付款记录
     * @param orderPay
     */
    public static void deleteOrderPay(OrderPay orderPay) {
        payService.deleteOrderPayById(orderPay.getOrder_pay_id());
    }

    /**
     * 查询
     * @param order_return_id
     * @return
     */
    public static List<OrderPay> getOrderPaysByOrderReturnId(int order_return_id) {
        PaySearchOption paySearchOption = new PaySearchOption();
        paySearchOption.setOrder_return_id(order_return_id);
        return orderPays(paySearchOption);
    }

    /**
     * 查询
     * @param orderPayIds
     * @return
     */
    public static List<OrderPay> orderPays(String orderPayIds) {
        List<Integer> list = SplitUtils.splitToList(orderPayIds, ",");
        if (list == null || list.size() == 0) {
            return new ArrayList<>();
        }
        PaySearchOption paySearchOption = new PaySearchOption();
        paySearchOption.setOrderPayIds(list);
        return orderPays(paySearchOption);
    }

    /**
     * 查询
     * @param paySearchOption
     * @return
     */
    public static List<OrderPay> orderPays(PaySearchOption paySearchOption) {
        return payService.orderPays(paySearchOption);
    }
}
