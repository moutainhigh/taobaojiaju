package com.xinshan.dao;

import com.xinshan.model.InventoryMoveCommodity;
import java.util.List;

public interface InventoryMoveCommodityMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table inventory_move_commodity
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer inventory_move_commodity_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table inventory_move_commodity
     *
     * @mbggenerated
     */
    int insert(InventoryMoveCommodity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table inventory_move_commodity
     *
     * @mbggenerated
     */
    InventoryMoveCommodity selectByPrimaryKey(Integer inventory_move_commodity_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table inventory_move_commodity
     *
     * @mbggenerated
     */
    List<InventoryMoveCommodity> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table inventory_move_commodity
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(InventoryMoveCommodity record);
}