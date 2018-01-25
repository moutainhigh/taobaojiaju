package com.xinshan.service.sync;

import com.xinshan.dao.*;
import com.xinshan.dao.extend.commodity.CommodityExtendMapper;
import com.xinshan.model.Category;
import com.xinshan.model.Commodity;
import com.xinshan.model.CommodityColor;
import com.xinshan.model.CommodityUnit;
import com.xinshan.model.extend.commodity.CommodityExtend;
import com.xinshan.pojo.commodity.CommoditySearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by mxt on 17-7-28.
 */
@Service
public class CommoditySyncService {
    @Autowired
    private CommodityExtendMapper commodityExtendMapper;
    @Autowired
    private CommodityMapper commodityMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private CommodityUnitMapper commodityUnitMapper;
    @Autowired
    private CommodityColorMapper commodityColorMapper;
    @Autowired
    private CommodityAttributeMapper commodityAttributeMapper;

    public CommodityExtend getCommodityById(int commodity_id) {
        CommoditySearchOption commoditySearchOption = new CommoditySearchOption();
        commoditySearchOption.setCommodity_id(commodity_id);
        List<CommodityExtend> list = commodityExtendMapper.commodityList(commoditySearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    @Transactional
    public void commoditySync(CommodityExtend commodityExtend) {
        Commodity commodity = commodityMapper.selectByPrimaryKey(commodityExtend.getCommodity_id());
        if (commodity == null) {
            commodityMapper.insert(commodityExtend);
        }else {
            commodityMapper.updateByPrimaryKey(commodityExtend);
        }

        Category category = commodityExtend.getCategory();
        if (category != null && category.getCategory_id() != null) {
            Category category1 = categoryMapper.selectByPrimaryKey(category.getCategory_id());
            if (category1 == null) {
                categoryMapper.insert(category);
            }else {
                categoryMapper.updateByPrimaryKey(category);
            }
        }

        CommodityUnit commodityUnit = commodityExtend.getCommodityUnit();
        if (commodityUnit != null && commodityUnit.getCommodity_unit_id() != null) {
            CommodityUnit commodityUnit1 = commodityUnitMapper.selectByPrimaryKey(commodityUnit.getCommodity_unit_id());
            if (commodityUnit1 == null) {
                commodityUnitMapper.insert(commodityUnit);
            }else {
                commodityUnitMapper.updateByPrimaryKey(commodityUnit);
            }
        }

        CommodityColor commodityColor = commodityExtend.getCommodityColor();
        if (commodityColor != null && commodityColor.getCommodity_color_id() != null) {
            CommodityColor commodityColor1 = commodityColorMapper.selectByPrimaryKey(commodityColor.getCommodity_color_id());
            if (commodityColor1 == null) {
                commodityColorMapper.insert(commodityColor);
            }else {
                commodityColorMapper.updateByPrimaryKey(commodityColor);
            }
        }

    }

}
