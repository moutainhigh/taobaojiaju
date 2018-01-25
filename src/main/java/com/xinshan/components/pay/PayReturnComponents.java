package com.xinshan.components.pay;

import com.xinshan.model.Employee;
import com.xinshan.model.OrderPayReturn;
import com.xinshan.pojo.pay.PaySearchOption;
import com.xinshan.service.PayService;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.SplitUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mxt on 17-4-13.
 */
@Component
public class PayReturnComponents {
    public static final int pay_return_source_cash = 1;//现金退款
    public static final int pay_return_source_zhongyuan_bank = 2;//中原银行
    public static final int pay_return_source_xingye_bank = 3;//兴业银行
    public static final int pay_return_source_tihuoquan = 4;//4提货券

    public static final int pay_return_type_value_added = 1;//增值卡退款
    public static final int pay_return_type_return_commodity = 2;//退货退款

    private static PayService payService;

    @Autowired
    public void setPayService(PayService payService) {
        PayReturnComponents.payService = payService;
    }

    public static OrderPayReturn payReturn(BigDecimal return_amount, String pay_return_remark, Employee employee, int pay_source, int pay_type) {
        OrderPayReturn orderPayReturn = new OrderPayReturn();
        orderPayReturn.setReturn_amount(return_amount);
        orderPayReturn.setReturn_create_date(DateUtil.currentDate());
        orderPayReturn.setReturn_employee_code(employee.getEmployee_code());
        orderPayReturn.setReturn_employee_name(employee.getEmployee_name());
        orderPayReturn.setOrder_pay_return_source(pay_source);
        orderPayReturn.setOrder_pay_return_type(pay_type);
        orderPayReturn.setPay_return_remark(pay_return_remark);
        payService.createPayReturn(orderPayReturn);
        return orderPayReturn;
    }

    public static List<OrderPayReturn> payReturns(String orderPayReturnIds) {
        List<Integer> list = SplitUtils.splitToList(orderPayReturnIds, ",");
        if (list == null || list.size() == 0) {
            return new ArrayList<>();
        }
        PaySearchOption paySearchOption = new PaySearchOption();
        paySearchOption.setOrderPayReturnIds(list);
        return payService.orderPayReturns(paySearchOption);
    }
}
