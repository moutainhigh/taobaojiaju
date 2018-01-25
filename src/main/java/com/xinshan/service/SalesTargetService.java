package com.xinshan.service;

import com.xinshan.dao.SalesTargetAmountMapper;
import com.xinshan.dao.SalesTargetAnalysisMapper;
import com.xinshan.dao.SalesTargetMapper;
import com.xinshan.dao.extend.salesTarget.SalesTargetExtendMapper;
import com.xinshan.model.SalesTargetAmount;
import com.xinshan.model.SalesTargetAnalysis;
import com.xinshan.model.extend.salesTarget.SalesTargetAnalysisExtend;
import com.xinshan.model.extend.salesTarget.SalesTargetExtend;
import com.xinshan.pojo.salesTarget.SalesTargetSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by mxt on 17-8-22.
 */
@Service
public class SalesTargetService {
    @Autowired
    private SalesTargetExtendMapper salesTargetExtendMapper;
    @Autowired
    private SalesTargetMapper salesTargetMapper;
    @Autowired
    private SalesTargetAmountMapper salesTargetAmountMapper;
    @Autowired
    private SalesTargetAnalysisMapper salesTargetAnalysisMapper;

    @Transactional
    public void createSalesTarget(SalesTargetExtend salesTargetExtend) {
        Map<Integer, SalesTargetAmount> salesTargetAmountMap = amountMap(salesTargetExtend);
        SalesTargetAmount salesTargetAmount1 = salesTargetAmountMap.get(0);
        BigDecimal amount = salesTargetAmount1.getSales_target_amount();
        salesTargetExtend.setSales_target_year_amount(amount);
        salesTargetExtendMapper.createSalesTarget(salesTargetExtend);
        for (int month = 1; month <= 12; month++) {
            SalesTargetAmount salesTargetAmount = salesTargetAmountMap.get(month);
            if (salesTargetAmount == null) {
                salesTargetAmount = new SalesTargetAmount();
                salesTargetAmount.setSales_target_amount(new BigDecimal("0"));
                salesTargetAmount.setSales_target_id(salesTargetExtend.getSales_target_id());
                salesTargetAmount.setSales_target_month(month);
            }else {
                salesTargetAmount.setSales_target_id(salesTargetExtend.getSales_target_id());
            }
            salesTargetExtendMapper.createSalesTargetAmount(salesTargetAmount);
        }
    }

    /**
     * 编辑
     * @param salesTargetExtend
     */
    @Transactional
    public void updateSalesTarget(SalesTargetExtend salesTargetExtend) {
        SalesTargetExtend salesTarget = getSalesTargetById(salesTargetExtend.getSales_target_id());
        List<SalesTargetAmount> list = salesTarget.getTargetAmounts();
        Map<Integer, SalesTargetAmount> salesTargetAmountMap = amountMap(salesTargetExtend);
        BigDecimal amount = new BigDecimal("0");
        for (int i = 0; i < list.size(); i++) {
            SalesTargetAmount salesTargetAmount = list.get(i);
            Integer month = salesTargetAmount.getSales_target_month();
            SalesTargetAmount salesTargetAmount1 = salesTargetAmountMap.get(month);
            if (salesTargetAmount1 != null && !salesTargetAmount.getSales_target_amount().equals(salesTargetAmount1.getSales_target_amount())) {
                salesTargetAmount.setSales_target_amount(salesTargetAmount1.getSales_target_amount());
                salesTargetAmountMapper.updateByPrimaryKey(salesTargetAmount);
            }
            amount = amount.add(salesTargetAmount.getSales_target_amount());
        }
        salesTargetExtend.setSales_target_year_amount(amount);
        salesTargetMapper.updateByPrimaryKey(salesTargetExtend);
    }

    private Map<Integer, SalesTargetAmount> amountMap(SalesTargetExtend salesTargetExtend) {
        Map<Integer, SalesTargetAmount> salesTargetAmountMap = new HashMap<>();
        List<SalesTargetAmount> targetAmounts = salesTargetExtend.getTargetAmounts();
        BigDecimal amount = new BigDecimal("0");
        for (int i = 0; i < targetAmounts.size(); i++) {
            SalesTargetAmount salesTargetAmount = targetAmounts.get(i);
            salesTargetAmount.setSales_target_id(salesTargetExtend.getSales_target_id());
            if (salesTargetAmount.getSales_target_amount() == null) {
                salesTargetAmount.setSales_target_amount(new BigDecimal("0"));
            }
            amount = amount.add(salesTargetAmount.getSales_target_amount());
            Integer sales_target_month = salesTargetAmount.getSales_target_month();
            if (sales_target_month != null && sales_target_month > 0 && sales_target_month < 13) {
                salesTargetAmountMap.put(sales_target_month, salesTargetAmount);
            }
        }
        SalesTargetAmount salesTargetAmount = new SalesTargetAmount();
        salesTargetAmount.setSales_target_month(0);
        salesTargetAmount.setSales_target_amount(amount);
        salesTargetAmountMap.put(0, salesTargetAmount);
        return salesTargetAmountMap;
    }

    public List<SalesTargetExtend> salesTargetList(SalesTargetSearchOption salesTargetSearchOption) {
        List<Integer> list = salesTargetExtendMapper.salesTargetIds(salesTargetSearchOption);
        if (list != null && list.size() > 0) {
            salesTargetSearchOption.setSalesTargetIds(list);
            return salesTargetExtendMapper.salesTargetList(salesTargetSearchOption);
        }
        return new ArrayList<>();
    }

    public SalesTargetExtend getSalesTargetById(int salesTargetId) {
        SalesTargetSearchOption salesTargetSearchOption = new SalesTargetSearchOption();
        List<Integer> list = new ArrayList<>();
        list.add(salesTargetId);
        salesTargetSearchOption.setSalesTargetIds(list);
        List<SalesTargetExtend> salesTargetExtends = salesTargetExtendMapper.salesTargetList(salesTargetSearchOption);
        if (salesTargetExtends != null && salesTargetExtends.size() == 1) {
            return salesTargetExtends.get(0);
        }
        return null;
    }

    public SalesTargetExtend getSalesTargetByPosition(int year, int position_id) {
        SalesTargetSearchOption salesTargetSearchOption = new SalesTargetSearchOption();
        salesTargetSearchOption.setPosition_id(position_id);
        salesTargetSearchOption.setSales_target_year(year);
        List<SalesTargetExtend> salesTargetExtends = salesTargetList(salesTargetSearchOption);
        if (salesTargetExtends != null && salesTargetExtends.size() == 1) {
            return salesTargetExtends.get(0);
        }
        return null;
    }

    public Integer countSalesTarget(SalesTargetSearchOption salesTargetSearchOption) {
        return salesTargetExtendMapper.countSalesTarget(salesTargetSearchOption);
    }

    @Transactional
    public void createSalesTargetAnalysis(SalesTargetAnalysis salesTargetAnalysis) {
        salesTargetExtendMapper.createSalesTargetAnalysis(salesTargetAnalysis);
    }
    @Transactional
    public void updateSalesTargetAnalysis(SalesTargetAnalysis salesTargetAnalysis) {
        salesTargetAnalysisMapper.updateByPrimaryKey(salesTargetAnalysis);
    }

    public SalesTargetAnalysisExtend getSalesTargetAnalysis(int year, int month, int position_id) {
        SalesTargetSearchOption salesTargetSearchOption = new SalesTargetSearchOption();
        salesTargetSearchOption.setSales_target_year(year);
        salesTargetSearchOption.setSales_target_month(month);
        salesTargetSearchOption.setPosition_id(position_id);
        List<SalesTargetAnalysisExtend> list = analysisList(salesTargetSearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public List<SalesTargetAnalysisExtend> analysisList(SalesTargetSearchOption salesTargetSearchOption) {
        return salesTargetExtendMapper.targetAnalysisList(salesTargetSearchOption);
    }

    public Integer countAnalysis(SalesTargetSearchOption salesTargetSearchOption) {
        return salesTargetExtendMapper.countAnalysis(salesTargetSearchOption);
    }



}
