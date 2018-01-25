package com.xinshan.dao;

import com.xinshan.model.CommodityNum;
import java.util.List;

public interface CommodityNumMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table commodity_num
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer commodity_num_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table commodity_num
     *
     * @mbggenerated
     */
    int insert(CommodityNum record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table commodity_num
     *
     * @mbggenerated
     */
    CommodityNum selectByPrimaryKey(Integer commodity_num_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table commodity_num
     *
     * @mbggenerated
     */
    List<CommodityNum> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table commodity_num
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(CommodityNum record);
}