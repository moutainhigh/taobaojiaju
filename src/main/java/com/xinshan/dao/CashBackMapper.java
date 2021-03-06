package com.xinshan.dao;

import com.xinshan.model.CashBack;
import java.util.List;

public interface CashBackMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cash_back
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer cash_back_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cash_back
     *
     * @mbggenerated
     */
    int insert(CashBack record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cash_back
     *
     * @mbggenerated
     */
    CashBack selectByPrimaryKey(Integer cash_back_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cash_back
     *
     * @mbggenerated
     */
    List<CashBack> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cash_back
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(CashBack record);
}