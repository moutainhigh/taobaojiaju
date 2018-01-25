package com.xinshan.dao.extend.orderFee;

import com.xinshan.model.OrderFee;
import com.xinshan.model.OrderFeeType;
import com.xinshan.model.extend.orderFee.OrderFeeExtend;
import com.xinshan.pojo.afterSales.AfterSalesSearchOption;
import com.xinshan.pojo.orderFee.OrderFeeSearchOption;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mxt on 16-11-23.
 */
public interface OrderFeeExtendMapper {

    void createOrderFee(OrderFee orderFee);
    void createOrderFeeType(OrderFeeType orderFeeType);

    List<OrderFeeExtend> orderFees(AfterSalesSearchOption afterSalesSearchOption);
    List<OrderFeeExtend> orderFees1(OrderFeeSearchOption orderFeeSearchOption);
    Integer countOrderFee(AfterSalesSearchOption afterSalesSearchOption);

    HashMap fixDesc(int order_fee_id);
}
