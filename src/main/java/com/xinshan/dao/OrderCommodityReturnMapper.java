package com.xinshan.dao;

import com.xinshan.model.OrderCommodityReturn;
import java.util.List;

public interface OrderCommodityReturnMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_commodity_return
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer order_commodity_return_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_commodity_return
     *
     * @mbggenerated
     */
    int insert(OrderCommodityReturn record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_commodity_return
     *
     * @mbggenerated
     */
    OrderCommodityReturn selectByPrimaryKey(Integer order_commodity_return_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_commodity_return
     *
     * @mbggenerated
     */
    List<OrderCommodityReturn> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_commodity_return
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(OrderCommodityReturn record);
}