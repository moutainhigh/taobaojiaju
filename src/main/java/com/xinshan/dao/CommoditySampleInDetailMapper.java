package com.xinshan.dao;

import com.xinshan.model.CommoditySampleInDetail;
import java.util.List;

public interface CommoditySampleInDetailMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table commodity_sample_in_detail
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer commodity_sample_in_detail_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table commodity_sample_in_detail
     *
     * @mbggenerated
     */
    int insert(CommoditySampleInDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table commodity_sample_in_detail
     *
     * @mbggenerated
     */
    CommoditySampleInDetail selectByPrimaryKey(Integer commodity_sample_in_detail_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table commodity_sample_in_detail
     *
     * @mbggenerated
     */
    List<CommoditySampleInDetail> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table commodity_sample_in_detail
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(CommoditySampleInDetail record);
}