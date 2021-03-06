package com.xinshan.dao;

import com.xinshan.model.OrderCommoditySupplier;
import java.util.List;

public interface OrderCommoditySupplierMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_commodity_supplier
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer order_commodity_supplier_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_commodity_supplier
     *
     * @mbggenerated
     */
    int insert(OrderCommoditySupplier record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_commodity_supplier
     *
     * @mbggenerated
     */
    OrderCommoditySupplier selectByPrimaryKey(Integer order_commodity_supplier_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_commodity_supplier
     *
     * @mbggenerated
     */
    List<OrderCommoditySupplier> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_commodity_supplier
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(OrderCommoditySupplier record);
}