package com.xinshan.dao;

import com.xinshan.model.Gift;
import java.util.List;

public interface GiftMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gift
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer gift_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gift
     *
     * @mbggenerated
     */
    int insert(Gift record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gift
     *
     * @mbggenerated
     */
    Gift selectByPrimaryKey(Integer gift_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gift
     *
     * @mbggenerated
     */
    List<Gift> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gift
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(Gift record);
}