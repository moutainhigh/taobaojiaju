package com.xinshan.dao.extend.activity;

import com.xinshan.model.BrandBoost;
import com.xinshan.model.extend.activity.BrandBoostExtend;
import com.xinshan.pojo.activity.BrandBoostSearchOption;

import java.util.List;

/**
 * Created by mxt on 17-4-19.
 */
public interface BrandBoostExtendMapper {

    void createBrandBoost(BrandBoost brandBoost);

    List<BrandBoostExtend> brandBoostList(BrandBoostSearchOption brandBoostSearchOption);

    Integer countBrandBoost(BrandBoostSearchOption brandBoostSearchOption);
}
