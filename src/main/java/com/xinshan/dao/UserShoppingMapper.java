package com.xinshan.dao;

import com.xinshan.model.UserShopping;
import java.util.List;

public interface UserShoppingMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_shopping
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer user_shopping_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_shopping
     *
     * @mbggenerated
     */
    int insert(UserShopping record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_shopping
     *
     * @mbggenerated
     */
    UserShopping selectByPrimaryKey(Integer user_shopping_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_shopping
     *
     * @mbggenerated
     */
    List<UserShopping> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_shopping
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(UserShopping record);
}