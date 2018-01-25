package com.xinshan.dao.extend.activity;

import com.xinshan.model.CashBack;
import com.xinshan.model.CashBackCommodity;
import com.xinshan.model.extend.activity.CashBackExtend;
import com.xinshan.pojo.activity.CashBackSearchOption;

import java.util.List;
import java.util.Map;

/**
 * Created by mxt on 17-4-17.
 */
public interface CashBackExtendMapper {

    void createCashBack(CashBack cashBack);
    void createCashBackCommodity(CashBackCommodity cashBackCommodity);

    List<CashBackExtend> cashBackList(CashBackSearchOption cashBackSearchOption);
    List<Integer> cashBackIds(CashBackSearchOption cashBackSearchOption);
    Integer countCashBack(CashBackSearchOption cashBackSearchOption);

    CashBackCommodity getCashBackCommodity(int order_commodity_id);

    Map statistics(CashBackSearchOption cashBackSearchOption);
}
