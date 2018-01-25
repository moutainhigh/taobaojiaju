package com.xinshan.service;

import com.xinshan.dao.extend.activity.CashBackExtendMapper;
import com.xinshan.model.CashBack;
import com.xinshan.model.CashBackCommodity;
import com.xinshan.model.Employee;
import com.xinshan.model.extend.activity.CashBackCommodityExtend;
import com.xinshan.model.extend.activity.CashBackExtend;
import com.xinshan.model.extend.order.OrderReturnCommodityExtend;
import com.xinshan.pojo.activity.CashBackSearchOption;
import com.xinshan.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mxt on 17-4-17.
 */
@Service
public class CashBackService {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderReturnService orderReturnService;
    @Autowired
    private CashBackExtendMapper cashBackExtendMapper;

    @Transactional
    public void createCashBack(CashBackExtend cashBack, Employee employee) {
        if (cashBack.getCash_back_date() == null) {
            cashBack.setCash_back_date(DateUtil.currentDate());
        }
        cashBack.setCash_back_create_date(DateUtil.currentDate());
        cashBack.setCash_back_employee_name(employee.getEmployee_name());
        cashBack.setCash_back_employee_code(employee.getEmployee_code());
        cashBackExtendMapper.createCashBack(cashBack);

        List<CashBackCommodityExtend> list = cashBack.getCashBackCommodityExtends();
        for (int i = 0; i < list.size(); i++) {
            CashBackCommodityExtend cashBachCommodity = list.get(i);
            cashBachCommodity.setCash_back_id(cashBack.getCash_back_id());
            cashBackExtendMapper.createCashBackCommodity(cashBachCommodity);
        }
    }

    public List<CashBackExtend> cashBackList(List<Integer> cashBackIds) {
        if (cashBackIds == null || cashBackIds.size() == 0) {
            return new ArrayList<>();
        }
        CashBackSearchOption cashBackSearchOption = new CashBackSearchOption();
        cashBackSearchOption.setCashBackIds(cashBackIds);
        List<CashBackExtend> list = cashBackExtendMapper.cashBackList(cashBackSearchOption);
        for (int i = 0; i < list.size(); i++) {
            CashBackExtend cashBack = list.get(i);
            Integer cash_back_type = cashBack.getCash_back_type();
            List<CashBackCommodityExtend> cashBackCommodities = cashBack.getCashBackCommodityExtends();
            for (int j = 0; j < cashBackCommodities.size(); j++) {
                CashBackCommodityExtend cashBackCommodity = cashBackCommodities.get(j);
                if (cash_back_type == 1) {
                    if (cashBackCommodity.getOrder_commodity_id() != null) {
                        cashBackCommodity.setOrderCommodityExtend(orderService.getOrderCommodityExtend(cashBackCommodity.getOrder_commodity_id()));
                    }
                }else if (cash_back_type == 2) {
                    if (cashBackCommodity.getOrder_return_commodity_id() != null) {
                        OrderReturnCommodityExtend orderReturnCommodityExtend = orderReturnService.getOrderReturnCommodityById(cashBackCommodity.getOrder_return_commodity_id());
                        cashBackCommodity.setOrderReturnCommodityExtend(orderReturnCommodityExtend);
                    }
                }
            }
        }
        return list;
    }

    public List<Integer> cashBackIds(CashBackSearchOption cashBackSearchOption) {
        return cashBackExtendMapper.cashBackIds(cashBackSearchOption);
    }
    public Integer countCashBack(CashBackSearchOption cashBackSearchOption) {
        return cashBackExtendMapper.countCashBack(cashBackSearchOption);
    }

    public CashBackCommodity getCashBackCommodity(int order_commodity_id) {
        return cashBackExtendMapper.getCashBackCommodity(order_commodity_id);
    }

    public Map statistics(CashBackSearchOption cashBackSearchOption) {
        return cashBackExtendMapper.statistics(cashBackSearchOption);
    }

}
