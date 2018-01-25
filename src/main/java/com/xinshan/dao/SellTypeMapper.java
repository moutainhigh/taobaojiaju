package com.xinshan.dao;

import com.xinshan.model.SellType;
import java.util.List;

public interface SellTypeMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sell_type
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer sell_type_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sell_type
     *
     * @mbggenerated
     */
    int insert(SellType record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sell_type
     *
     * @mbggenerated
     */
    SellType selectByPrimaryKey(Integer sell_type_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sell_type
     *
     * @mbggenerated
     */
    List<SellType> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sell_type
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(SellType record);
}