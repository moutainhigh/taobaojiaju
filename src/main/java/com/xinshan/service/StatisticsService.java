package com.xinshan.service;

import com.xinshan.dao.extend.statistics.StatisticsExtendMapper;
import com.xinshan.model.extend.statistics.SaleRank;
import com.xinshan.pojo.statistics.StatisticsSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by mxt on 17-3-13.
 */
@Service
public class StatisticsService {
    @Autowired
    private StatisticsExtendMapper statisticsExtendMapper;

    public List<SaleRank> commoditySaleRank(StatisticsSearchOption statisticsSearchOption) {
        return statisticsExtendMapper.commoditySaleRank(statisticsSearchOption);
    }

    public Integer countCommoditySaleRank(StatisticsSearchOption statisticsSearchOption) {
        return statisticsExtendMapper.countCommoditySaleRank(statisticsSearchOption);
    }

    public List<SaleRank> employeeSaleRank(StatisticsSearchOption statisticsSearchOption) {
        return statisticsExtendMapper.employeeSaleRank(statisticsSearchOption);
    }

    public Integer countEmployeeSaleRank(StatisticsSearchOption statisticsSearchOption) {
        return statisticsExtendMapper.countEmployeeSaleRank(statisticsSearchOption);
    }

    public List<SaleRank> positionSaleRank(StatisticsSearchOption statisticsSearchOption) {
        return statisticsExtendMapper.positionSaleRank(statisticsSearchOption);
    }

    public Integer countPositionSaleRank(StatisticsSearchOption statisticsSearchOption) {
        return statisticsExtendMapper.countPositionSaleRank(statisticsSearchOption);
    }

    public List<SaleRank> supplierSaleRank(StatisticsSearchOption statisticsSearchOption) {
        return statisticsExtendMapper.supplierSaleRank(statisticsSearchOption);
    }

    public Integer countSupplierSaleRank(StatisticsSearchOption statisticsSearchOption) {
        return statisticsExtendMapper.countSupplierSaleRank(statisticsSearchOption);
    }

    public List<SaleRank> categorySaleRank(StatisticsSearchOption statisticsSearchOption) {
        return statisticsExtendMapper.categorySaleRank(statisticsSearchOption);
    }

    public Integer countCategorySaleRank(StatisticsSearchOption statisticsSearchOption) {
        return statisticsExtendMapper.countCategorySaleRank(statisticsSearchOption);
    }
}
