package com.xinshan.dao;

import com.xinshan.model.OrderReturn;
import java.util.List;

public interface OrderReturnMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_return
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer order_return_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_return
     *
     * @mbggenerated
     */
    int insert(OrderReturn record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_return
     *
     * @mbggenerated
     */
    OrderReturn selectByPrimaryKey(Integer order_return_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_return
     *
     * @mbggenerated
     */
    List<OrderReturn> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_return
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(OrderReturn record);
}