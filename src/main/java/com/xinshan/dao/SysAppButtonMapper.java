package com.xinshan.dao;

import com.xinshan.model.SysAppButton;
import java.util.List;

public interface SysAppButtonMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_app_button
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer sys_app_button_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_app_button
     *
     * @mbggenerated
     */
    int insert(SysAppButton record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_app_button
     *
     * @mbggenerated
     */
    SysAppButton selectByPrimaryKey(Integer sys_app_button_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_app_button
     *
     * @mbggenerated
     */
    List<SysAppButton> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_app_button
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(SysAppButton record);
}