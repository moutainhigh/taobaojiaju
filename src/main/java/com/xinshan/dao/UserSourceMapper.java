package com.xinshan.dao;

import com.xinshan.model.UserSource;
import java.util.List;

public interface UserSourceMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_source
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer user_source_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_source
     *
     * @mbggenerated
     */
    int insert(UserSource record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_source
     *
     * @mbggenerated
     */
    UserSource selectByPrimaryKey(Integer user_source_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_source
     *
     * @mbggenerated
     */
    List<UserSource> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_source
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(UserSource record);
}