package com.xinshan.dao;

import com.xinshan.model.CommodityStore;
import java.util.List;

public interface CommodityStoreMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table commodity_store
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer commodity_store_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table commodity_store
     *
     * @mbggenerated
     */
    int insert(CommodityStore record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table commodity_store
     *
     * @mbggenerated
     */
    CommodityStore selectByPrimaryKey(Integer commodity_store_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table commodity_store
     *
     * @mbggenerated
     */
    List<CommodityStore> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table commodity_store
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(CommodityStore record);
}