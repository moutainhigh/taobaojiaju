package com.xinshan.dao;

import com.xinshan.model.Province;
import java.util.List;

public interface ProvinceMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table province
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer province_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table province
     *
     * @mbggenerated
     */
    int insert(Province record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table province
     *
     * @mbggenerated
     */
    Province selectByPrimaryKey(Integer province_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table province
     *
     * @mbggenerated
     */
    List<Province> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table province
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(Province record);
}