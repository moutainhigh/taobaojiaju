package com.xinshan.components.statistics;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.employee.EmployeeComponent;
import com.xinshan.model.Commodity;
import com.xinshan.model.Employee;
import com.xinshan.model.OrderCommodity;
import com.xinshan.model.extend.employee.EmployeeExtend;
import com.xinshan.model.extend.gift.GiftCommodityExtend;
import com.xinshan.model.extend.gift.GiftExtend;
import com.xinshan.model.extend.order.OrderCommodityExtend;
import com.xinshan.model.extend.order.OrderExtend;
import com.xinshan.model.extend.order.OrderReturnCommodityExtend;
import com.xinshan.model.extend.order.OrderReturnExtend;
import com.xinshan.model.extend.statistics.Performance;
import com.xinshan.pojo.gift.GiftSearchOption;
import com.xinshan.pojo.order.OrderSearchOption;
import com.xinshan.pojo.statistics.PerformanceSearchOption;
import com.xinshan.service.EmployeePerformanceService;
import com.xinshan.service.GiftService;
import com.xinshan.service.OrderReturnService;
import com.xinshan.service.OrderService;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.constant.order.OrderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

/**
 * Created by mxt on 17-8-25.
 */
@Component
public class PerformanceComponent {

    private static EmployeePerformanceService employeePerformanceService;
    private static OrderReturnService orderReturnService;
    private static OrderService orderService;
    private static GiftService giftService;
    @Autowired
    public void setOrderService(OrderService orderService) {
        PerformanceComponent.orderService = orderService;
    }

    @Autowired
    public void setEmployeePerformanceService(EmployeePerformanceService employeePerformanceService) {
        PerformanceComponent.employeePerformanceService = employeePerformanceService;
    }
    @Autowired
    public void setOrderReturnService(OrderReturnService orderReturnService) {
        PerformanceComponent.orderReturnService = orderReturnService;
    }
    @Autowired
    public void setGiftService(GiftService giftService) {
        PerformanceComponent.giftService = giftService;
    }

    public static int performance(int year, int month, String employee_code) {
        try {
            EmployeeExtend employeeByCode = EmployeeComponent.getEmployeeByCode(employee_code);
            if (employeeByCode == null) {
                return 0;
            }
            Performance performance = employeePerformanceService.getPerformance(year, month, employee_code);
            if (performance == null) {
                performance = new Performance();
                performance.setPerformance_year(year);
                performance.setPerformance_month(month);
                performance.setEmployee_code(employee_code);
            }
            //订单
            orderAmount(performance);
            //退换货
            orderReturnAmount(performance);
            //赠品
            giftAmount(performance);
            //退赠品
            giftReturnAmount(performance);
            //实际销售额
            sellAmount(performance);
            //累计销售额
            addUp(performance);

            if (performance.getEmployee_performance_id() == null) {
                employeePerformanceService.createPerformance(performance);
            }else {
                employeePerformanceService.updatePerformance(performance);
            }
            return 1;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 订单业绩
     * @param performance
     */
    private static void orderAmount(Performance performance) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, performance.getPerformance_year());
        calendar.set(Calendar.MONTH, performance.getPerformance_month() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        OrderSearchOption orderSearchOption = new OrderSearchOption();
        orderSearchOption.setEmployee_code(performance.getEmployee_code());
        //订单时间
        orderSearchOption.setOrderStartDate(DateUtil.startOfTheDay(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        orderSearchOption.setOrderEndDate(calendar.getTime());
        orderSearchOption.setOrderStatuses("1");//正常状态订单
        orderSearchOption.setTrans_purchase(1);//订单已确认
        List<Integer> orderIds = orderService.orderIds(orderSearchOption);
        List<OrderExtend> list = orderService.orderList(orderIds);
        int orderCommodityNum = 0;
        BigDecimal orderCommodityAmount = new BigDecimal("0");//订单销售额，不含退换货
        for (int i = 0; i < list.size(); i++) {
            OrderExtend orderExtend = list.get(i);
            List<OrderCommodityExtend> orderCommodities = orderExtend.getOrderCommodities();
            for (int j = 0; j < orderCommodities.size(); j++) {
                OrderCommodityExtend orderCommodityExtend = orderCommodities.get(j);
                if (orderCommodityExtend.getOrder_commodity_type() == 1) {//订单商品
                    Integer order_commodity_num = orderCommodityExtend.getOrder_commodity_num();
                    orderCommodityNum += order_commodity_num;
                    BigDecimal bargain_price = orderCommodityExtend.getBargain_price();
                    BigDecimal revision_fee = orderCommodityExtend.getRevision_fee();
                    BigDecimal commodity_amount = bargain_price.add(revision_fee).multiply(new BigDecimal(order_commodity_num));
                    orderCommodityAmount = orderCommodityAmount.add(commodity_amount);
                    System.out.println(orderExtend.getOrder_code() + "\t" + bargain_price + "\t" +revision_fee + "\t" + order_commodity_num);
                }
            }
        }

        performance.setOrder_num(orderIds.size());
        performance.setOrder_commodity_num(orderCommodityNum);
        performance.setOrder_commodity_amount(orderCommodityAmount);
    }

    /**
     * 退还货
     * @param performance
     */
    private static void orderReturnAmount(Performance performance) {
        OrderSearchOption orderSearchOption = new OrderSearchOption();
        int year = performance.getPerformance_year();
        int month = performance.getPerformance_month();
        String employee_code = performance.getEmployee_code();
        orderSearchOption.setEmployee_code(employee_code);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        orderSearchOption.setStartDate(DateUtil.startOfTheDay(calendar.getTime()));

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        orderSearchOption.setEndDate(DateUtil.endOfTheDay(calendar.getTime()));

        orderSearchOption.setOrder_return_check_status("1");//已审核

        List<Integer> orderReturnIds = orderReturnService.orderReturnIds(orderSearchOption);
        List<OrderReturnExtend> list = orderReturnService.orderReturnList(orderReturnIds);
        int returnCommodityNum = 0;
        BigDecimal returnCommodityAmount = new BigDecimal("0");
        int addCommodityNum = 0;
        BigDecimal addCommodityAmount = new BigDecimal("0");
        for (int i = 0; i < list.size(); i++) {
            OrderReturnExtend orderReturnExtend = list.get(i);
            List<OrderReturnCommodityExtend> orderReturnCommodities = orderReturnExtend.getOrderReturnCommodities();
            for (int j = 0; j < orderReturnCommodities.size(); j++) {
                OrderReturnCommodityExtend orderReturnCommodityExtend = orderReturnCommodities.get(j);
                Integer order_return_commodity_type = orderReturnCommodityExtend.getOrder_return_commodity_type();
                Integer order_return_commodity_num = orderReturnCommodityExtend.getOrder_return_commodity_num();
                OrderCommodity orderCommodity = orderReturnCommodityExtend.getOrderCommodity();
                BigDecimal bargain_price = orderCommodity.getBargain_price();
                BigDecimal revision_fee = orderCommodity.getRevision_fee();
                if (order_return_commodity_type ==  OrderConstants.ORDER_RETURN_COMMODITY_TYPE_ADD) {//新增
                    addCommodityNum += order_return_commodity_num;
                    addCommodityAmount = addCommodityAmount.add(bargain_price.add(revision_fee).multiply(new BigDecimal(order_return_commodity_num)));
                }else if (order_return_commodity_type == OrderConstants.ORDER_RETURN_COMMODITY_TYPE_RETURN) {//退货
                    returnCommodityAmount = returnCommodityAmount.add(bargain_price.add(revision_fee).multiply(new BigDecimal(order_return_commodity_num)));
                    returnCommodityNum += order_return_commodity_num;
                }
            }
        }

        performance.setOrder_return_commodity_amount(returnCommodityAmount);
        performance.setOrder_return_commodity_num(returnCommodityNum);
        performance.setOrder_return_commodity_add_amount(addCommodityAmount);
        performance.setOrder_return_commodity_add_num(addCommodityNum);
    }

    private static void giftAmount(Performance performance) {
        int year = performance.getPerformance_year();
        int month = performance.getPerformance_month();
        String employee_code = performance.getEmployee_code();

        GiftSearchOption giftSearchOption = new GiftSearchOption();
        giftSearchOption.setEmployee_code(employee_code);
        giftSearchOption.setGift_enable(1);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        giftSearchOption.setStartDate(DateUtil.startOfTheDay(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        giftSearchOption.setEndDate(DateUtil.endOfTheDay(calendar.getTime()));

        int giftCommodityNum = 0;
        BigDecimal giftCommodityAmount = new BigDecimal("0");
        List<Integer> giftIds = giftService.giftIds(giftSearchOption);
        if (giftIds != null && giftIds.size() > 0) {
            giftSearchOption.setGiftIds(giftIds);
            List<GiftExtend> list = giftService.giftList(giftSearchOption);
            for (int i = 0; i < list.size(); i++) {
                GiftExtend giftExtend = list.get(i);
                List<GiftCommodityExtend> giftCommodities = giftExtend.getGiftCommodities();
                for (int j = 0; j < j; j++) {
                    GiftCommodityExtend giftCommodityExtend = giftCommodities.get(j);
                    Commodity commodity = giftCommodityExtend.getCommodity();
                    Integer gift_commodity_num = giftCommodityExtend.getGift_commodity_num();
                    giftCommodityNum += gift_commodity_num;
                    giftCommodityAmount = giftCommodityAmount.add(commodity.getSell_price().multiply(new BigDecimal(gift_commodity_num)));
                }
            }
        }

        performance.setGift_commodity_num(giftCommodityNum);
        performance.setGift_commodity_amount(giftCommodityAmount);
    }

    private static void giftReturnAmount(Performance performance) {
        performance.setGift_return_commodity_amount(new BigDecimal("0"));
        performance.setGift_return_commodity_num(0);
    }

    private static void sellAmount(Performance performance) {
        BigDecimal order_commodity_amount = performance.getOrder_commodity_amount();
        BigDecimal order_return_commodity_add_amount = performance.getOrder_return_commodity_add_amount();
        BigDecimal order_return_commodity_amount = performance.getOrder_return_commodity_amount();
        BigDecimal sellAmount = order_commodity_amount.add(order_return_commodity_add_amount).subtract(order_return_commodity_amount);
        performance.setSell_amount(sellAmount);
    }

    private static void addUp(Performance performance) {
        int add_up_order_num = performance.getOrder_num();
        BigDecimal add_up_sell_amount = performance.getSell_amount();
        int add_up_commodity_num = performance.getOrder_commodity_num();
        PerformanceSearchOption performanceSearchOption = new PerformanceSearchOption();
        performanceSearchOption.setPerformance_year(performance.getPerformance_year());
        performanceSearchOption.setEmployee_code(performance.getEmployee_code());
        List<Performance> performances = employeePerformanceService.performanceList(performanceSearchOption);
        for (int i = 0; i < performances.size(); i++) {
            Performance performance1 = performances.get(i);
            if (performance1.getPerformance_month() < performance.getPerformance_month()) {
                add_up_order_num += performance1.getOrder_num();
                add_up_sell_amount = add_up_sell_amount.add(performance1.getSell_amount());
                add_up_commodity_num += performance1.getAdd_up_commodity_num();
            }
        }
        performance.setAdd_up_order_num(add_up_order_num);
        performance.setAdd_up_commodity_num(add_up_commodity_num);
        performance.setAdd_up_sell_amount(add_up_sell_amount);
    }
}
