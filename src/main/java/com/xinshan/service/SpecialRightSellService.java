package com.xinshan.service;

import com.xinshan.components.pay.PayComponents;
import com.xinshan.dao.extend.specialRightSell.SpecialRightSellExtendMapper;
import com.xinshan.model.ActivitySpecialRightCommodity;
import com.xinshan.model.Employee;
import com.xinshan.model.OrderPay;
import com.xinshan.model.SpecialRightSell;
import com.xinshan.model.extend.specialRightSell.SpecialRightSellExtend;
import com.xinshan.pojo.specialRightSell.SpecialRightSellSearchOption;
import com.xinshan.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by mxt on 17-4-20.
 */
@Service
public class SpecialRightSellService {
    @Autowired
    private SpecialRightSellExtendMapper specialRightSellExtendMapper;
    @Autowired
    private ActivityService activityService;

    @Transactional
    public synchronized void createSpecialRightSell(SpecialRightSell specialRightSell, Employee employee) {
        int activity_special_right_commodity_id = specialRightSell.getActivity_special_right_commodity_id();
        int sell_num = specialRightSell.getSpecial_right_commodity_num();
        ActivitySpecialRightCommodity activitySpecialRightCommodity = activityService.getSpecialRightCommodityById(activity_special_right_commodity_id);
        if (activitySpecialRightCommodity.getSpecial_right_commodity_status() != 1) {
            throw new RuntimeException("已禁用");
        }
        int remaining_num = activitySpecialRightCommodity.getSpecial_right_remaining_num();
        if (remaining_num < sell_num) {
            throw new RuntimeException("数量不足");
        }
        specialRightSell.setSpecial_right_sell_status(1);
        specialRightSell.setSell_create_employee_code(employee.getEmployee_code());
        specialRightSell.setSell_create_employee_name(employee.getEmployee_name());
        specialRightSell.setSell_create_date(DateUtil.currentDate());
        OrderPay orderPay = PayComponents.pay(specialRightSell.getPay_amount(), employee, PayComponents.pay_source_cash, PayComponents.pay_type_special_right_sell);
        specialRightSell.setOrder_pay_ids(orderPay.getOrder_pay_id() + "");
        specialRightSellExtendMapper.createSpecialRightSell(specialRightSell);

        activitySpecialRightCommodity.setSpecial_right_sell_num(activitySpecialRightCommodity.getSpecial_right_sell_num() + 1);
        activitySpecialRightCommodity.setSpecial_right_remaining_num(activitySpecialRightCommodity.getSpecial_right_num() - activitySpecialRightCommodity.getSpecial_right_sell_num());
        activityService.updateActivitySpecialCommodity(activitySpecialRightCommodity);
    }

    public List<SpecialRightSellExtend> specialRightSells(SpecialRightSellSearchOption specialRightSellSearchOption) {
        List<SpecialRightSellExtend> list = specialRightSellExtendMapper.specialRightSells(specialRightSellSearchOption);
        for (int i = 0; i < list.size(); i++) {
            SpecialRightSellExtend specialRightSell = list.get(i);
            specialRightSell.setOrderPays(PayComponents.orderPays(specialRightSell.getOrder_pay_ids()));
        }
        return list;
    }

    public Integer countSpecialRightSell(SpecialRightSellSearchOption specialRightSellSearchOption) {
        return specialRightSellExtendMapper.countSpecialRightSell(specialRightSellSearchOption);
    }
}
