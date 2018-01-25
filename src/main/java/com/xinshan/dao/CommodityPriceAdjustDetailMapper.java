package com.xinshan.dao;

import com.xinshan.model.CommodityPriceAdjustDetail;
import java.util.List;

public interface CommodityPriceAdjustDetailMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table commodity_price_adjust_detail
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer commodity_price_adjust_detail_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table commodity_price_adjust_detail
     *
     * @mbggenerated
     */
    int insert(CommodityPriceAdjustDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table commodity_price_adjust_detail
     *
     * @mbggenerated
     */
    CommodityPriceAdjustDetail selectByPrimaryKey(Integer commodity_price_adjust_detail_id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table commodity_price_adjust_detail
     *
     * @mbggenerated
     */
    List<CommodityPriceAdjustDetail> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table commodity_price_adjust_detail
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(CommodityPriceAdjustDetail record);
}