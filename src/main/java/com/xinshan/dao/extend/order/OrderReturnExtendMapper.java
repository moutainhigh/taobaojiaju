package com.xinshan.dao.extend.order;

import com.xinshan.model.OrderReturn;
import com.xinshan.model.OrderReturnCommodity;
import com.xinshan.model.extend.order.OrderReturnCommodityExtend;
import com.xinshan.model.extend.order.OrderReturnExtend;
import com.xinshan.pojo.order.OrderSearchOption;

import java.util.List;

/**
 * Created by mxt on 17-3-24.
 */
public interface OrderReturnExtendMapper {
    void createOrderReturn(OrderReturn orderReturn);
    void updateOrderReturnOrderPayIds(OrderReturn orderReturn);
    void updateOrderReturnOrderReturnPayIds(OrderReturn orderReturn);

    void createOrderReturnCommodity(OrderReturnCommodity orderReturnCommodity);

    String orderReturnCode(String dateStr);

    List<OrderReturnExtend> orderReturnList(OrderSearchOption orderSearchOption);

    Integer countOrderReturn(OrderSearchOption orderSearchOption);

    List<Integer> orderReturnIds(OrderSearchOption orderSearchOption);

    List<OrderReturnCommodityExtend> orderReturnCommodities(OrderSearchOption orderSearchOption);

    List<OrderReturnCommodityExtend> orderReturnReport(OrderSearchOption orderSearchOption);

    Integer countOrderReturnReport(OrderSearchOption orderSearchOption);
}
