package com.xinshan.dao.extend.statistics;

import com.xinshan.model.extend.statistics.SaleRank;
import com.xinshan.pojo.statistics.StatisticsSearchOption;

import java.util.List;

/**
 * Created by mxt on 17-3-13.
 */
public interface StatisticsExtendMapper {

    List<SaleRank> commoditySaleRank(StatisticsSearchOption statisticsSearchOption);

    Integer countCommoditySaleRank(StatisticsSearchOption statisticsSearchOption);

    List<SaleRank> employeeSaleRank(StatisticsSearchOption statisticsSearchOption);

    Integer countEmployeeSaleRank(StatisticsSearchOption statisticsSearchOption);

    List<SaleRank> positionSaleRank(StatisticsSearchOption statisticsSearchOption);

    Integer countPositionSaleRank(StatisticsSearchOption statisticsSearchOption);

    List<SaleRank> supplierSaleRank(StatisticsSearchOption statisticsSearchOption);

    Integer countSupplierSaleRank(StatisticsSearchOption statisticsSearchOption);

    List<SaleRank> categorySaleRank(StatisticsSearchOption statisticsSearchOption);

    Integer countCategorySaleRank(StatisticsSearchOption statisticsSearchOption);
}
