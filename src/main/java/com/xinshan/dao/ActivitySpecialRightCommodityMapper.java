package com.xinshan.dao;

import com.xinshan.model.ActivitySpecialRightCommodity;
import java.util.List;

public interface ActivitySpecialRightCommodityMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table activity_special_right_commodity
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer activity_special_right_commodity_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table activity_special_right_commodity
     *
     * @mbggenerated
     */
    int insert(ActivitySpecialRightCommodity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table activity_special_right_commodity
     *
     * @mbggenerated
     */
    ActivitySpecialRightCommodity selectByPrimaryKey(Integer activity_special_right_commodity_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table activity_special_right_commodity
     *
     * @mbggenerated
     */
    List<ActivitySpecialRightCommodity> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table activity_special_right_commodity
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(ActivitySpecialRightCommodity record);
}