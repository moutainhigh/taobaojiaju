package com.xinshan.dao;

import com.xinshan.model.UserStatus;
import java.util.List;

public interface UserStatusMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_status
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer user_status_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_status
     *
     * @mbggenerated
     */
    int insert(UserStatus record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_status
     *
     * @mbggenerated
     */
    UserStatus selectByPrimaryKey(Integer user_status_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_status
     *
     * @mbggenerated
     */
    List<UserStatus> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_status
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(UserStatus record);
}