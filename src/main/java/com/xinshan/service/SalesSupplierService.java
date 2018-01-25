package com.xinshan.service;

import com.xinshan.dao.SalesContactsMapper;
import com.xinshan.dao.SalesSupplierMapper;
import com.xinshan.dao.extend.statistics.SalesSupplierExtendMapper;
import com.xinshan.model.SalesContacts;
import com.xinshan.model.SalesSupplier;
import com.xinshan.model.extend.statistics.SalesSupplierExtend;
import com.xinshan.pojo.order.OrderSearchOption;
import com.xinshan.pojo.statistics.StatisticsSearchOption;
import com.xinshan.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by mxt on 17-9-5.
 */
@Service
public class SalesSupplierService {
    @Autowired
    private SalesSupplierExtendMapper salesSupplierExtendMapper;
    @Autowired
    private SalesSupplierMapper salesSupplierMapper;
    @Autowired
    private SalesContactsMapper salesContactsMapper;

    @Transactional
    public void createSalesSupplier(SalesSupplier salesSupplier) {
        SalesSupplier salesSupplier1 = getSalesSupplier(salesSupplier.getSupplier_id(), salesSupplier.getMonth());
        if (salesSupplier1 == null) {
            salesSupplierExtendMapper.createSalesSupplier(salesSupplier);
        }else {
            salesSupplier.setSales_supplier_id(salesSupplier1.getSales_supplier_id());
            salesSupplierMapper.updateByPrimaryKey(salesSupplier);
        }
    }

    @Transactional
    public void createSalesContacts(SalesContacts salesContacts) {
        SalesContacts salesContacts1 = getSalesContacts(salesContacts.getContacts(), salesContacts.getMonth());
        if (salesContacts1 == null) {
            salesSupplierExtendMapper.createSalesContacts(salesContacts);
        }else {
            salesContacts.setSales_contacts_id(salesContacts1.getSales_contacts_id());
            salesContactsMapper.updateByPrimaryKey(salesContacts);
        }
    }

    public Map orderSales(OrderSearchOption orderSearchOption) {
        return salesSupplierExtendMapper.orderSales(orderSearchOption);
    }

    public Map orderReturnSales(OrderSearchOption orderSearchOption) {
        return salesSupplierExtendMapper.orderReturnSales(orderSearchOption);
    }



    public SalesSupplier getSalesSupplier(int supplier_id, String month) {
        StatisticsSearchOption statisticsSearchOption = new StatisticsSearchOption();
        statisticsSearchOption.setSupplier_id(supplier_id);
        statisticsSearchOption.setMonth(month);
        List<SalesSupplierExtend> salesSuppliers = salesSupplierList(statisticsSearchOption);
        if (salesSuppliers != null && salesSuppliers.size() == 1) {
            return salesSuppliers.get(0);
        }
        return null;
    }

    public List<SalesSupplierExtend> salesSupplierList(StatisticsSearchOption statisticsSearchOption) {
        return salesSupplierExtendMapper.salesSupplierList(statisticsSearchOption);
    }

    public Integer countSalesSupplier(StatisticsSearchOption statisticsSearchOption) {
        return salesSupplierExtendMapper.countSalesSupplier(statisticsSearchOption);
    }


    public SalesContacts getSalesContacts(String contacts, String month) {
        StatisticsSearchOption statisticsSearchOption = new StatisticsSearchOption();
        statisticsSearchOption.setContacts(contacts);
        statisticsSearchOption.setMonth(month);
        List<SalesContacts> salesContactsList = salesContactsList(statisticsSearchOption);
        if (salesContactsList != null && salesContactsList.size() == 1) {
            return salesContactsList.get(0);
        }
        return null;
    }
    public List<SalesContacts> salesContactsList(StatisticsSearchOption statisticsSearchOption) {
        return salesSupplierExtendMapper.salesContactsList(statisticsSearchOption);
    }

    public Integer countSalesContacts(StatisticsSearchOption statisticsSearchOption) {
        return salesSupplierExtendMapper.countSalesContacts(statisticsSearchOption);
    }

    public Map contactsAmount(StatisticsSearchOption statisticsSearchOption) {
        return salesSupplierExtendMapper.contactsAmount(statisticsSearchOption);
    }

    public Map supplierAmount(StatisticsSearchOption statisticsSearchOption) {
        return salesSupplierExtendMapper.supplierAmount(statisticsSearchOption);
    }

    public BigDecimal monthSupplierAmount(String month) {
        StatisticsSearchOption statisticsSearchOption = new StatisticsSearchOption();
        statisticsSearchOption.setMonth(month);
        Map map = supplierAmount(statisticsSearchOption);
        if (map != null && map.get("sales_amount") != null) {
            return new BigDecimal(map.get("sales_amount").toString());
        }
        return new BigDecimal("0");
    }

    public BigDecimal monthContactsAmount(String month) {
        StatisticsSearchOption statisticsSearchOption = new StatisticsSearchOption();
        statisticsSearchOption.setMonth(month);
        Map map = contactsAmount(statisticsSearchOption);
        if (map != null && map.get("sales_amount") != null) {
            return new BigDecimal(map.get("sales_amount").toString());
        }
        return new BigDecimal("0");
    }
}
