package com.xinshan.dao.extend.statistics;

import com.xinshan.model.SalesContacts;
import com.xinshan.model.SalesSupplier;
import com.xinshan.model.extend.statistics.SalesSupplierExtend;
import com.xinshan.pojo.order.OrderSearchOption;
import com.xinshan.pojo.statistics.StatisticsSearchOption;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by mxt on 17-9-5.
 */
public interface SalesSupplierExtendMapper {

    void createSalesSupplier(SalesSupplier salesSupplier);

    void createSalesContacts(SalesContacts salesContacts);

    Map orderSales(OrderSearchOption orderSearchOption);
    Map orderReturnSales(OrderSearchOption orderSearchOption);

    List<SalesSupplierExtend> salesSupplierList(StatisticsSearchOption statisticsSearchOption);

    Integer countSalesSupplier(StatisticsSearchOption statisticsSearchOption);

    List<SalesContacts> salesContactsList(StatisticsSearchOption statisticsSearchOption);

    Integer countSalesContacts(StatisticsSearchOption statisticsSearchOption);

    Map supplierAmount(StatisticsSearchOption statisticsSearchOption);

    Map contactsAmount(StatisticsSearchOption statisticsSearchOption);
}
