package com.xinshan.dao;

import com.xinshan.model.ActivityGoldEgg;
import java.util.List;

public interface ActivityGoldEggMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table activity_gold_egg
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer activity_gold_egg_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table activity_gold_egg
     *
     * @mbggenerated
     */
    int insert(ActivityGoldEgg record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table activity_gold_egg
     *
     * @mbggenerated
     */
    ActivityGoldEgg selectByPrimaryKey(Integer activity_gold_egg_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table activity_gold_egg
     *
     * @mbggenerated
     */
    List<ActivityGoldEgg> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table activity_gold_egg
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(ActivityGoldEgg record);
}