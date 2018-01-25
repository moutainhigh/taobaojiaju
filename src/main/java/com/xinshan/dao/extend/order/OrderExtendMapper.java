package com.xinshan.dao.extend.order;

import com.xinshan.model.*;
import com.xinshan.model.extend.order.OrderCommodityExtend;
import com.xinshan.model.extend.order.OrderExtend;
import com.xinshan.pojo.order.OrderSearchOption;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mxt on 16-10-24.
 */
public interface OrderExtendMapper {
    Order getOrderById(int order_id);
    void createOrder(OrderExtend orderExtend);
    void updateOrder(Order order);

    void createOrderCommodity(OrderCommodity commodity);

    void createOrderCommodityValueAddedCard(OrderCommodityValueAddedCard orderCommodityValueAddedCard);

    void deleteOrderCommodityValueAddedCard(int order_commodity_id);

    List<OrderExtend> orderList(OrderSearchOption orderSearchOption);

    List<Integer> orderIds(OrderSearchOption orderSearchOption);

    void deleteOrder(int order_id);

    String todayOrderNum(String dateStr);

    void updateTransPurchase(OrderExtend orderExtend);

    List<OrderCommodity> getOrderCommodities(int order_id);

    void createOrderCommodityReturn(OrderCommodityReturn orderCommodityReturn);

    List<OrderCommodityReturn> orderCommodityReturns(int order_id);

    Integer countOrder(OrderSearchOption orderSearchOption);

    OrderCommodity getOrderCommodity(int order_id, int commodity_id);

    void createCarryFee(OrderCarryFee orderCarryFee);

    void deleteCarryFeeByOrderId(int order_id);

    void deleteCarryFeeByOrderReturnId(int order_return_id);

    List<HashMap> orderFeeStatics(OrderSearchOption orderSearchOption);

    BigDecimal orderCarryFeeStatics(OrderSearchOption orderSearchOption);

    List<OrderCommodityExtend> orderCommodityExtends(OrderSearchOption orderSearchOption);

    Integer countOrderCommodity(OrderSearchOption orderSearchOption);

    Map orderCommodityStatics(OrderSearchOption orderSearchOption);

    List<Map> simpleOrderCommodities(OrderSearchOption orderSearchOption);

    BigDecimal purchaseTotalPrice(OrderSearchOption orderSearchOption);

    OrderCommodityValueAddedCard getOrderCommodityCard(int order_commodity);

    void updateOrderPayIds(Order order);

    List<OrderCommodityExtend> cashBackCommodities(OrderSearchOption orderSearchOption);

    List<OrderCarryFee> orderCarryFees(int order_id);

    List<Map> daogouList(OrderSearchOption orderSearchOption);

    BigDecimal countPreferential(OrderSearchOption orderSearchOption);

    void createOrderCommoditySupplier(OrderCommoditySupplier orderCommoditySupplier);

    void deleteOrderCommoditySupplier(int order_commodity_id);

    Integer orderGiftNum(int order_id);
    Integer goldEggNum(int order_id);

    List<Map> orderExport(List<Integer> orderIds);
}
