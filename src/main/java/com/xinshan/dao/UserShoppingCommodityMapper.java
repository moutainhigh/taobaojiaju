package com.xinshan.dao;

import com.xinshan.model.UserShoppingCommodity;
import java.util.List;

public interface UserShoppingCommodityMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_shopping_commodity
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer user_shopping_commodity_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_shopping_commodity
     *
     * @mbggenerated
     */
    int insert(UserShoppingCommodity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_shopping_commodity
     *
     * @mbggenerated
     */
    UserShoppingCommodity selectByPrimaryKey(Integer user_shopping_commodity_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_shopping_commodity
     *
     * @mbggenerated
     */
    List<UserShoppingCommodity> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_shopping_commodity
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(UserShoppingCommodity record);
}