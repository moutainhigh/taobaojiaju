package com.xinshan.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.orderFee.OrderFeeComponents;
import com.xinshan.components.pay.PayReturnComponents;
import com.xinshan.dao.AfterSalesCommodityMapper;
import com.xinshan.dao.AfterSalesMapper;
import com.xinshan.dao.extend.afterSales.AfterSalesExtendMapper;
import com.xinshan.dao.extend.order.OrderExtendMapper;
import com.xinshan.model.*;
import com.xinshan.model.extend.afterSales.AfterSalesCommodityExtend;
import com.xinshan.model.extend.afterSales.AfterSalesExtend;
import com.xinshan.model.extend.orderFee.OrderFeeExtend;
import com.xinshan.pojo.orderFee.OrderFeeSearchOption;
import com.xinshan.utils.DateUtil;
import com.xinshan.pojo.afterSales.AfterSalesSearchOption;
import com.xinshan.utils.SplitUtils;
import com.xinshan.utils.constant.afterSales.AfterSalesConstant;
import com.xinshan.utils.constant.order.OrderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mxt on 16-12-15.
 */
@Service
public class AfterSalesService {
    @Autowired
    private AfterSalesCommodityMapper afterSalesCommodityMapper;
    @Autowired
    private AfterSalesExtendMapper afterSalesExtendMapper;
    @Autowired
    private AfterSalesMapper afterSalesMapper;
    @Autowired
    private SettlementService settlementService;

    @Autowired
    private OrderReturnService orderReturnService;

    @Transactional
    public void createAfterSales(AfterSalesExtend afterSales, Employee employee) {
        afterSales.setRecord_date(DateUtil.currentDate());
        afterSales.setRecord_employee_name(employee.getEmployee_name());
        afterSales.setRecord_employee_code(employee.getEmployee_code());
        afterSales.setAfter_sales_status(0);
        afterSales.setAfter_sales_fee_status(-1);
        afterSalesExtendMapper.createAfterSales(afterSales);
        List<AfterSalesCommodityExtend> list = afterSales.getAfterSalesCommodities();
        for (int i = 0; i < list.size(); i++) {
            AfterSalesCommodityExtend afterSalesCommodity = list.get(i);
            afterSalesCommodity.setAfter_sales_id(afterSales.getAfter_sales_id());
            afterSalesCommodity.setCommodity_problem_fix_status(0);
            afterSalesCommodity.setCommodity_problem_return_status(0);
            afterSalesCommodity.setReturn_pay_status(0);
            afterSalesExtendMapper.createAfterSalesCommodity(afterSalesCommodity);
        }
    }

    public List<Integer> afterSalesIds(AfterSalesSearchOption afterSalesSearchOption) {
        return afterSalesExtendMapper.afterSalesIds(afterSalesSearchOption);
    }
    public Integer countAfterSales(AfterSalesSearchOption afterSalesSearchOption) {
        return afterSalesExtendMapper.countAfterSales(afterSalesSearchOption);
    }

    public List<AfterSalesExtend> afterSalesList(AfterSalesSearchOption afterSalesSearchOption) {
        List<AfterSalesExtend> list = afterSalesExtendMapper.afterSalesList(afterSalesSearchOption);
        for (int i = 0; i < list.size(); i++) {
            AfterSalesExtend afterSalesExtend = list.get(i);
            afterSalesExtend.setAfterSalesCommodities(afterSalesCommodities(afterSalesExtend.getAfter_sales_id()));
        }
        return list;
    }

    public List<AfterSalesCommodityExtend> afterSalesCommodities(int after_sales_id) {
        AfterSalesSearchOption afterSalesSearchOption = new AfterSalesSearchOption();
        afterSalesSearchOption.setAfter_sales_id(after_sales_id);
        return afterSalesExtendMapper.afterSalesCommodities(afterSalesSearchOption);
    }

    public List<AfterSalesCommodityExtend> afterSalesCommodities(int after_sales_id, int supplier_id) {
        AfterSalesSearchOption afterSalesSearchOption = new AfterSalesSearchOption();
        afterSalesSearchOption.setAfter_sales_id(after_sales_id);
        afterSalesSearchOption.setSupplier_id(supplier_id);
        afterSalesSearchOption.setAfter_sales_type(1);
        return afterSalesExtendMapper.afterSalesCommodities(afterSalesSearchOption);
    }

    public AfterSalesCommodityExtend getAfterSalesCommodityById(int after_sales_commodity_id) {
        AfterSalesSearchOption afterSalesSearchOption = new AfterSalesSearchOption();
        afterSalesSearchOption.setAfter_sales_commodity_id(after_sales_commodity_id);
        List<AfterSalesCommodityExtend> list = afterSalesExtendMapper.afterSalesCommodities(afterSalesSearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    @Transactional
    public void updateAfterSalesCommodity(AfterSalesCommodity afterSalesCommodity) {
        afterSalesCommodityMapper.updateByPrimaryKey(afterSalesCommodity);
    }

    public AfterSalesExtend getAfterSalesById(int after_sales_id) {
        AfterSalesSearchOption afterSalesSearchOption = new AfterSalesSearchOption();
        List<Integer> afterSalesIds = new ArrayList<>();
        afterSalesIds.add(after_sales_id);
        afterSalesSearchOption.setAfterSalesIds(afterSalesIds);
        List<AfterSalesExtend> list = afterSalesList(afterSalesSearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public void updateAfterSales(AfterSales afterSales) {
        afterSalesMapper.updateByPrimaryKey(afterSales);
    }

    @Transactional
    public void afterSalesSettlement(AfterSales afterSales) {
        settlementService.createSettlementAfterSales(afterSales.getAfter_sales_id());
        afterSales.setAfter_sales_status(1);
        afterSalesMapper.updateByPrimaryKey(afterSales);
    }

    @Transactional
    public void afterSalesReturnPay(JSONObject jsonObject, Employee employee) {
        int after_sales_commodity_id = Integer.parseInt(jsonObject.get("after_sales_commodity_id").toString());
        AfterSalesCommodityExtend afterSalesCommodityExtend = getAfterSalesCommodityById(after_sales_commodity_id);
        OrderReturnCommodity orderReturnCommodity = afterSalesCommodityExtend.getOrderReturnCommodity();
        OrderReturn orderReturn = orderReturnService.getOrderReturnById(orderReturnCommodity.getOrder_return_id());
        JSONArray jsonArray = JSON.parseArray(jsonObject.get("order_return_pays").toString());
        List<Integer> orderPayReturnIds = new ArrayList<>();
        BigDecimal return_total_amount = new BigDecimal("0");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject js = JSONObject.parseObject(jsonArray.get(i).toString());
            BigDecimal return_amount = new BigDecimal(js.get("return_amount").toString());
            return_total_amount = return_total_amount.add(return_amount);
            int order_return_pay_source = Integer.parseInt(js.get("order_return_pay_source").toString());
            String pay_return_remark = null;
            if (js.get("pay_return_remark") != null) {
                pay_return_remark = js.get("pay_return_remark").toString();
            }
            OrderPayReturn orderPayReturn = PayReturnComponents.payReturn(return_amount, pay_return_remark, employee, order_return_pay_source, PayReturnComponents.pay_return_type_return_commodity);
            orderPayReturnIds.add(orderPayReturn.getOrder_pay_return_id());
        }
        //orderReturn.setReturn_commodity_amount();
        //orderReturn.setReturn_carry_fee();
        orderReturn.setReturn_amount(return_total_amount);
        orderReturn.setOrder_pay_return_ids(SplitUtils.listToString(orderPayReturnIds));
        orderReturnService.updateOrderReturn(orderReturn);

        afterSalesCommodityExtend.setReturn_pay_status(1);
        afterSalesCommodityMapper.updateByPrimaryKey(afterSalesCommodityExtend);
    }

    /**
     * 售后维修费用审核
     * @param after_sales_id
     * @param employee
     */
    @Transactional
    public void afterSalesFeeCheck(int after_sales_id, Employee employee) {
        AfterSalesExtend afterSalesExtend = getAfterSalesById(after_sales_id);
        if (afterSalesExtend.getAfter_sales_fee_status() != 0) {
            return;
        }
        List<OrderFeeExtend> list = null;
        if (afterSalesExtend.getOrder_fee_ids() != null) {
            list = OrderFeeComponents.orderFeeList(afterSalesExtend.getOrder_fee_ids());
        }else {
            OrderFeeSearchOption orderFeeSearchOption = new OrderFeeSearchOption();
            orderFeeSearchOption.setAfter_sales_id(after_sales_id);
            list = OrderFeeComponents.orderFeeList(orderFeeSearchOption);
        }
        if (list != null && list.size() > 0) {
            OrderFeeComponents.checkOrderFee(list, employee);
        }
        afterSalesExtend.setAfter_sales_fee_status(1);
        afterSalesMapper.updateByPrimaryKey(afterSalesExtend);
    }
}
