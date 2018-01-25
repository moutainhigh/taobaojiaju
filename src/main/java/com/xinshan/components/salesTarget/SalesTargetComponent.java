package com.xinshan.components.salesTarget;

import com.xinshan.model.Order;
import com.xinshan.model.SalesTarget;
import com.xinshan.model.SalesTargetAmount;
import com.xinshan.model.SalesTargetAnalysis;
import com.xinshan.model.extend.salesTarget.SalesTargetAnalysisExtend;
import com.xinshan.model.extend.salesTarget.SalesTargetExtend;
import com.xinshan.pojo.order.OrderSearchOption;
import com.xinshan.service.OrderService;
import com.xinshan.service.SalesTargetService;
import com.xinshan.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mxt on 17-8-22.
 */
@Component
public class SalesTargetComponent {

    private static SalesTargetService salesTargetService;
    private static OrderService orderService;

    @Autowired
    public void setOrderService(OrderService orderService) {
        SalesTargetComponent.orderService = orderService;
    }

    @Autowired
    public void setSalesTargetService(SalesTargetService salesTargetService) {
        SalesTargetComponent.salesTargetService = salesTargetService;
    }

    public static void salesTargetAnalysis(Order order) {
        Date order_date = order.getOrder_date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(order_date);
        int year = calendar.get(Calendar.DAY_OF_YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int position_id = order.getPosition_id();
        salesTargetAnalysis(year, month, position_id);
    }

    public static void salesTargetAnalysis(int year, int month, int position_id) {
        try {
            SalesTargetAnalysisExtend salesTargetAnalysis = salesTargetService.getSalesTargetAnalysis(year, month, position_id);
            SalesTargetExtend salesTarget = salesTargetService.getSalesTargetByPosition(year, position_id);
            List<SalesTargetAmount> targetAmounts = salesTarget.getTargetAmounts();
            BigDecimal add_up_target = new BigDecimal("0");
            SalesTargetAmount salesTargetAmount = null;
            for (int i = 0; i < targetAmounts.size(); i++) {
                SalesTargetAmount salesTargetAmount1 = targetAmounts.get(i);
                int sales_target_month = salesTargetAmount1.getSales_target_month();
                if (sales_target_month == month) {
                    salesTargetAmount = salesTargetAmount1;
                }
                if (sales_target_month >0 && sales_target_month <= month) {
                    add_up_target = add_up_target.add(salesTargetAmount1.getSales_target_amount());
                }
            }
            BigDecimal salesMonthAmount = salesMonthAmount(year, month, position_id);
            BigDecimal salesYearAmount = salesYearAmount(year, month, position_id);
            if (salesTargetAnalysis == null) {
                salesTargetAnalysis = new SalesTargetAnalysisExtend();
                salesTargetAnalysis.setSales_add_up_target(add_up_target);
                salesTargetAnalysis.setSales_target_amount_id(salesTargetAmount.getSales_target_amount_id());
                salesTargetAnalysis.setSales_target_id(salesTarget.getSales_target_id());
                salesTargetAnalysis.setSales_amount(salesMonthAmount);
                salesTargetAnalysis.setSales_add_up_amount(salesYearAmount);
                salesTargetService.createSalesTargetAnalysis(salesTargetAnalysis);
            }else {
                salesTargetAnalysis.setSales_add_up_target(add_up_target);
                salesTargetAnalysis.setSales_amount(salesMonthAmount);
                salesTargetAnalysis.setSales_add_up_amount(salesYearAmount);
                salesTargetService.updateSalesTargetAnalysis(salesTargetAnalysis);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static BigDecimal salesMonthAmount(int year, int month, int position_id) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        OrderSearchOption orderSearchOption = new OrderSearchOption();
        orderSearchOption.setOrderStartDate(DateUtil.startOfTheDay(calendar.getTime()));

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        orderSearchOption.setOrderEndDate(calendar.getTime());

        orderSearchOption.setOrderStatuses("1");
        orderSearchOption.setPositionIds(String.valueOf(position_id));
        System.out.print("start\t" + DateUtil.format(orderSearchOption.getOrderStartDate(), "yyyy-MM-dd HH:mm:ss"));
        System.out.println("end\t" + DateUtil.format(orderSearchOption.getOrderEndDate(), "yyyy-MM-dd HH:mm:ss"));
        return sumAmount(orderSearchOption);
    }

    private static BigDecimal salesYearAmount(int year, int month, int position_id) {
        OrderSearchOption orderSearchOption = new OrderSearchOption();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        orderSearchOption.setOrderStartDate(DateUtil.startOfTheDay(calendar.getTime()));

        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        orderSearchOption.setOrderEndDate(calendar.getTime());

        orderSearchOption.setOrderStatuses("1");
        orderSearchOption.setPositionIds(String.valueOf(position_id));
        System.out.print("start\t" + DateUtil.format(orderSearchOption.getOrderStartDate(), "yyyy-MM-dd HH:mm:ss"));
        System.out.println("end\t" + DateUtil.format(orderSearchOption.getOrderEndDate(), "yyyy-MM-dd HH:mm:ss"));
        return sumAmount(orderSearchOption);
    }

    private static BigDecimal sumAmount(OrderSearchOption orderSearchOption) {
        List<HashMap> list = orderService.orderFeeStatics(orderSearchOption);
        BigDecimal bigDecimal = new BigDecimal("0");
        for (int i = 0; i < list.size(); i++) {
            HashMap hashMap = list.get(i);
            BigDecimal pay_mount = new BigDecimal(hashMap.get("pay_amount").toString());
            System.out.println("pay_mount\t"+pay_mount);
            bigDecimal = bigDecimal.add(pay_mount);
        }
        return bigDecimal;
    }
}
