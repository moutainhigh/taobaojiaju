package com.xinshan.components.statistics;

import com.xinshan.components.supplier.SupplierComponents;
import com.xinshan.model.SalesContacts;
import com.xinshan.model.SalesSupplier;
import com.xinshan.model.extend.supplier.SupplierExtend;
import com.xinshan.pojo.order.OrderSearchOption;
import com.xinshan.pojo.statistics.StatisticsSearchOption;
import com.xinshan.service.SalesSupplierService;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.constant.order.OrderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by mxt on 17-9-5.
 */
@Component
public class SalesSupplierComponent {
    private static SalesSupplierService salesSupplierService;

    @Autowired
    public void setSalesSupplierService(SalesSupplierService salesSupplierService) {
        SalesSupplierComponent.salesSupplierService = salesSupplierService;
    }

    /**
     *
     * @param month 2017-01
     */
    public static void init(String month) {
        Map<Integer, SupplierExtend> supplierMap = SupplierComponents.getSupplierMap();
        Iterator<Map.Entry<Integer, SupplierExtend>> iterator = supplierMap.entrySet().iterator();
        Set<String> contactsSet = new HashSet<>();
        while (iterator.hasNext()) {
            Map.Entry<Integer, SupplierExtend> next = iterator.next();
            SupplierExtend supplier = next.getValue();
            String contacts = supplier.getContacts();
            if (contacts != null && !contacts.equals("")) {
                contactsSet.add(supplier.getContacts());
                SalesSupplier salesSupplier = new SalesSupplier();
                salesSupplier.setSupplier_id(supplier.getSupplier_id());
                salesSupplier.setContacts(supplier.getContacts());
                salesSupplier.setMonth(month);
                salesSupplier(salesSupplier);
                salesSupplierService.createSalesSupplier(salesSupplier);
            }
        }

        salesContacts(contactsSet, month);
    }

    public static void salesSupplier(SalesSupplier salesSupplier) {
        Calendar calendar = DateUtil.parse(salesSupplier.getMonth(), "yyyy-MM");
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = DateUtil.startOfTheDay(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = DateUtil.endOfTheDay(calendar.getTime());
        //订单
        orderSalesSalesSupplier(salesSupplier, startDate, endDate);
        //退货
        orderReturnSalesSalesSupplier(salesSupplier, startDate, endDate);
        //退换货新增
        orderReturnAddSalesSalesSupplier(salesSupplier, startDate, endDate);

        salesSupplier.setCost_amount(salesSupplier.getOrder_commodity_cost().subtract(salesSupplier.getReturn_commodity_cost()).add(salesSupplier.getReturn_add_commodity_cost()));
        salesSupplier.setSales_num(salesSupplier.getOrder_commodity_num() - salesSupplier.getReturn_commodity_num() + salesSupplier.getReturn_add_commodity_num());
        salesSupplier.setSales_amount(salesSupplier.getOrder_commodity_amount().subtract(salesSupplier.getReturn_commodity_amount()).add(salesSupplier.getReturn_add_commodity_amount()));
        //累计
        addUpSalesSupplier(salesSupplier);
    }

    private static void orderSalesSalesSupplier(SalesSupplier salesSupplier, Date startDate, Date endDate) {
        OrderSearchOption orderSearchOption = new OrderSearchOption();
        orderSearchOption.setOrderStartDate(startDate);
        orderSearchOption.setOrderEndDate(endDate);
        orderSearchOption.setSupplier_id(salesSupplier.getSupplier_id());
        orderSearchOption.setOrderStatuses("1");
        orderSearchOption.setOrder_commodity_type(1);//订单
        Map map = salesSupplierService.orderSales(orderSearchOption);
        int salesNum = 0;
        BigDecimal salesAmount = new BigDecimal("0");
        BigDecimal costAmount = new BigDecimal("0");
        if (map != null && map.get("salesNum") != null) {
            salesNum = Integer.parseInt(map.get("salesNum").toString());
        }
        if (map != null && map.get("salesAmount") != null) {
            salesAmount = new BigDecimal(map.get("salesAmount").toString());
        }
        if (map != null && map.get("costAmount") != null) {
            costAmount = new BigDecimal(map.get("costAmount").toString());
        }
        salesSupplier.setOrder_commodity_amount(salesAmount);
        salesSupplier.setOrder_commodity_cost(costAmount);
        salesSupplier.setOrder_commodity_num(salesNum);
    }

    private static void orderReturnSalesSalesSupplier(SalesSupplier salesSupplier, Date startDate, Date endDate) {
        OrderSearchOption orderSearchOption = new OrderSearchOption();
        orderSearchOption.setStartDate(startDate);
        orderSearchOption.setEndDate(endDate);
        orderSearchOption.setSupplier_id(salesSupplier.getSupplier_id());
        orderSearchOption.setOrder_return_check_status("1");//已审核
        orderSearchOption.setOrder_return_commodity_type(OrderConstants.ORDER_RETURN_COMMODITY_TYPE_RETURN);//退货
        Map map = salesSupplierService.orderReturnSales(orderSearchOption);
        int salesNum = 0;
        BigDecimal salesAmount = new BigDecimal("0");
        BigDecimal costAmount = new BigDecimal("0");
        if (map != null && map.get("salesNum") != null) {
            salesNum = Integer.parseInt(map.get("salesNum").toString());
        }
        if (map != null && map.get("salesAmount") != null) {
            salesAmount = new BigDecimal(map.get("salesAmount").toString());
        }
        if (map != null && map.get("costAmount") != null) {
            costAmount = new BigDecimal(map.get("costAmount").toString());
        }
        salesSupplier.setReturn_commodity_num(salesNum);
        salesSupplier.setReturn_commodity_amount(salesAmount);
        salesSupplier.setReturn_commodity_cost(costAmount);
    }

    private static void orderReturnAddSalesSalesSupplier(SalesSupplier salesSupplier, Date startDate, Date endDate) {
        OrderSearchOption orderSearchOption = new OrderSearchOption();
        orderSearchOption.setStartDate(startDate);
        orderSearchOption.setEndDate(endDate);
        orderSearchOption.setSupplier_id(salesSupplier.getSupplier_id());
        orderSearchOption.setOrder_return_check_status("1");//已审核
        orderSearchOption.setOrder_return_commodity_type(OrderConstants.ORDER_RETURN_COMMODITY_TYPE_ADD);//退货
        Map map = salesSupplierService.orderReturnSales(orderSearchOption);
        int salesNum = 0;
        BigDecimal salesAmount = new BigDecimal("0");
        BigDecimal costAmount = new BigDecimal("0");
        if (map != null && map.get("salesNum") != null) {
            salesNum = Integer.parseInt(map.get("salesNum").toString());
        }
        if (map != null && map.get("salesAmount") != null) {
            salesAmount = new BigDecimal(map.get("salesAmount").toString());
        }
        if (map != null && map.get("costAmount") != null) {
            costAmount = new BigDecimal(map.get("costAmount").toString());
        }
        salesSupplier.setReturn_add_commodity_num(salesNum);
        salesSupplier.setReturn_add_commodity_amount(salesAmount);
        salesSupplier.setReturn_add_commodity_cost(costAmount);
    }

    private static void addUpSalesSupplier(SalesSupplier salesSupplier) {
        String month = salesSupplier.getMonth();
        Calendar calendar = DateUtil.parse(month, "yyyy-MM");
        StatisticsSearchOption statisticsSearchOption = new StatisticsSearchOption();
        statisticsSearchOption.setSupplier_id(salesSupplier.getSupplier_id());
        statisticsSearchOption.setY(calendar.get(Calendar.YEAR));
        statisticsSearchOption.setM(calendar.get(Calendar.MONTH));
        Map map = salesSupplierService.supplierAmount(statisticsSearchOption);
        int salesNum = salesSupplier.getSales_num();
        BigDecimal salesAmount = salesSupplier.getSales_amount();
        BigDecimal costAmount = salesSupplier.getCost_amount();
        if (map != null && map.get("sales_num") != null) {
            salesNum += Integer.parseInt(map.get("sales_num").toString());
        }
        if (map != null && map.get("sales_amount") != null) {
            salesAmount = salesAmount.add(new BigDecimal(map.get("sales_amount").toString()));
        }
        if (map != null && map.get("cost_amount") != null) {
            costAmount = costAmount.add(new BigDecimal(map.get("cost_amount").toString()));
        }

        salesSupplier.setAdd_up_amount(salesAmount);
        salesSupplier.setAdd_up_num(salesNum);
        salesSupplier.setAdd_up_cost_amount(costAmount);
    }

    private static void salesContacts(Set<String> set, String month) {
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String contacts = iterator.next();
            SalesContacts salesContacts = new SalesContacts();
            salesContacts.setContacts(contacts);
            salesContacts.setMonth(month);
            salesContacts(salesContacts);
            salesSupplierService.createSalesContacts(salesContacts);
        }
    }

    public static void salesContacts(SalesContacts salesContacts) {
        Calendar calendar = DateUtil.parse(salesContacts.getMonth(), "yyyy-MM");
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = DateUtil.startOfTheDay(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = DateUtil.endOfTheDay(calendar.getTime());
        //订单
        orderSalesSalesContacts(salesContacts, startDate, endDate);
        //退货
        orderReturnSalesSalesContacts(salesContacts, startDate, endDate);
        //退换货新增
        orderReturnAddSalesSalesContacts(salesContacts, startDate, endDate);

        salesContacts.setCost_amount(salesContacts.getOrder_commodity_cost().subtract(salesContacts.getReturn_commodity_cost()).add(salesContacts.getReturn_add_commodity_cost()));
        salesContacts.setSales_num(salesContacts.getOrder_commodity_num() - salesContacts.getReturn_commodity_num() + salesContacts.getReturn_add_commodity_num());
        salesContacts.setSales_amount(salesContacts.getOrder_commodity_amount().subtract(salesContacts.getReturn_commodity_amount()).add(salesContacts.getReturn_add_commodity_amount()));
        //累计
        addUpSalesContacts(salesContacts);
    }

    private static void orderSalesSalesContacts(SalesContacts salesContacts, Date startDate, Date endDate) {
        OrderSearchOption orderSearchOption = new OrderSearchOption();
        orderSearchOption.setOrderStartDate(startDate);
        orderSearchOption.setOrderEndDate(endDate);
        orderSearchOption.setContacts(salesContacts.getContacts());
        orderSearchOption.setOrderStatuses("1");
        orderSearchOption.setOrder_commodity_type(1);//订单
        Map map = salesSupplierService.orderSales(orderSearchOption);
        int salesNum = 0;
        BigDecimal salesAmount = new BigDecimal("0");
        BigDecimal costAmount = new BigDecimal("0");
        if (map != null && map.get("salesNum") != null) {
            salesNum = Integer.parseInt(map.get("salesNum").toString());
        }
        if (map != null && map.get("salesAmount") != null) {
            salesAmount = new BigDecimal(map.get("salesAmount").toString());
        }
        if (map != null && map.get("costAmount") != null) {
            costAmount = new BigDecimal(map.get("costAmount").toString());
        }
        salesContacts.setOrder_commodity_amount(salesAmount);
        salesContacts.setOrder_commodity_cost(costAmount);
        salesContacts.setOrder_commodity_num(salesNum);
    }

    private static void orderReturnSalesSalesContacts(SalesContacts salesContacts, Date startDate, Date endDate) {
        OrderSearchOption orderSearchOption = new OrderSearchOption();
        orderSearchOption.setStartDate(startDate);
        orderSearchOption.setEndDate(endDate);
        orderSearchOption.setContacts(salesContacts.getContacts());
        orderSearchOption.setOrder_return_check_status("1");//已审核
        orderSearchOption.setOrder_return_commodity_type(OrderConstants.ORDER_RETURN_COMMODITY_TYPE_RETURN);//退货
        Map map = salesSupplierService.orderReturnSales(orderSearchOption);
        int salesNum = 0;
        BigDecimal salesAmount = new BigDecimal("0");
        BigDecimal costAmount = new BigDecimal("0");
        if (map != null && map.get("salesNum") != null) {
            salesNum = Integer.parseInt(map.get("salesNum").toString());
        }
        if (map != null && map.get("salesAmount") != null) {
            salesAmount = new BigDecimal(map.get("salesAmount").toString());
        }
        if (map != null && map.get("costAmount") != null) {
            costAmount = new BigDecimal(map.get("costAmount").toString());
        }
        salesContacts.setReturn_commodity_num(salesNum);
        salesContacts.setReturn_commodity_amount(salesAmount);
        salesContacts.setReturn_commodity_cost(costAmount);
    }

    private static void orderReturnAddSalesSalesContacts(SalesContacts salesContacts, Date startDate, Date endDate) {
        OrderSearchOption orderSearchOption = new OrderSearchOption();
        orderSearchOption.setStartDate(startDate);
        orderSearchOption.setEndDate(endDate);
        orderSearchOption.setContacts(salesContacts.getContacts());
        orderSearchOption.setOrder_return_check_status("1");//已审核
        orderSearchOption.setOrder_return_commodity_type(OrderConstants.ORDER_RETURN_COMMODITY_TYPE_ADD);//退货
        Map map = salesSupplierService.orderReturnSales(orderSearchOption);
        int salesNum = 0;
        BigDecimal salesAmount = new BigDecimal("0");
        BigDecimal costAmount = new BigDecimal("0");
        if (map != null && map.get("salesNum") != null) {
            salesNum = Integer.parseInt(map.get("salesNum").toString());
        }
        if (map != null && map.get("salesAmount") != null) {
            salesAmount = new BigDecimal(map.get("salesAmount").toString());
        }
        if (map != null && map.get("costAmount") != null) {
            costAmount = new BigDecimal(map.get("costAmount").toString());
        }
        salesContacts.setReturn_add_commodity_num(salesNum);
        salesContacts.setReturn_add_commodity_amount(salesAmount);
        salesContacts.setReturn_add_commodity_cost(costAmount);
    }

    private static void addUpSalesContacts(SalesContacts salesContacts) {
        String month = salesContacts.getMonth();
        Calendar calendar = DateUtil.parse(month, "yyyy-MM");
        StatisticsSearchOption statisticsSearchOption = new StatisticsSearchOption();
        statisticsSearchOption.setY(calendar.get(Calendar.YEAR));
        statisticsSearchOption.setM(calendar.get(Calendar.MONTH));
        statisticsSearchOption.setContacts(salesContacts.getContacts());
        Map map = salesSupplierService.contactsAmount(statisticsSearchOption);
        int salesNum = salesContacts.getSales_num();
        BigDecimal salesAmount = salesContacts.getSales_amount();
        BigDecimal costAmount = salesContacts.getCost_amount();
        if (map != null && map.get("sales_num") != null) {
            salesNum += Integer.parseInt(map.get("sales_num").toString());
        }
        if (map != null && map.get("sales_amount") != null) {
            salesAmount = salesAmount.add(new BigDecimal(map.get("sales_amount").toString()));
        }
        if (map != null && map.get("cost_amount") != null) {
            costAmount = costAmount.add(new BigDecimal(map.get("cost_amount").toString()));
        }

        salesContacts.setAdd_up_amount(salesAmount);
        salesContacts.setAdd_up_num(salesNum);
        salesContacts.setAdd_up_cost_amount(costAmount);
    }
}
