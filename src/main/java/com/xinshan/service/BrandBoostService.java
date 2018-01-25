package com.xinshan.service;

import com.xinshan.dao.extend.activity.BrandBoostExtendMapper;
import com.xinshan.model.BrandBoost;
import com.xinshan.model.extend.activity.BrandBoostExtend;
import com.xinshan.pojo.activity.BrandBoostSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by mxt on 17-4-19.
 */
@Service
public class BrandBoostService {

    @Autowired
    private BrandBoostExtendMapper brandBoostExtendMapper;

    @Transactional
    public void createBrandBoost(BrandBoost brandBoost){
        brandBoostExtendMapper.createBrandBoost(brandBoost);
    }

    public List<BrandBoostExtend> brandBoostList(BrandBoostSearchOption brandBoostSearchOption) {
        return brandBoostExtendMapper.brandBoostList(brandBoostSearchOption);
    }

    public Integer countBrandBoost(BrandBoostSearchOption brandBoostSearchOption) {
        return brandBoostExtendMapper.countBrandBoost(brandBoostSearchOption);
    }

}
