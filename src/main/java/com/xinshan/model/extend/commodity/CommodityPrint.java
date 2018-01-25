package com.xinshan.model.extend.commodity;

import com.xinshan.components.activity.ActivityComponents;
import com.xinshan.model.ActivityCommodity;
import com.xinshan.model.CommodityAttribute;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mxt on 16-10-24.
 */
public class CommodityPrint {
    public CommodityPrint(CommodityExtend commodityExtend) {
        this.commodity_name = commodityExtend.getCommodity_name();
        if (commodityExtend.getCategory() != null) {
            this.category_name = commodityExtend.getCategory().getCategory_name();
        }
        //商品属性
        List<CommodityAttribute> commodityAttributes = commodityExtend.getCommodityAttributes();
        if (commodityAttributes != null) {
            Map<String, String> attributes = new HashMap<>();
            for (int i = 0; i < commodityAttributes.size(); i++) {
                CommodityAttribute attribute = commodityAttributes.get(i);
                attributes.put(attribute.getCommodity_attribute_name(), attribute.getCommodity_attribute_value());
            }
            this.attributes = attributes;

        }
        //型号
        this.commodity_code = commodityExtend.getCommodity_code();
        this.remark = commodityExtend.getCommodity_remark();
        this.price = commodityExtend.getSell_price();
        this.commodity_id = commodityExtend.getCommodity_id();
        this.commodity_size = commodityExtend.getCommodity_size();
        this.supplier_commodity_code = commodityExtend.getSupplier_commodity_code();
        this.commodity_remark = commodityExtend.getCommodity_remark();
        if (commodityExtend.getSupplier() != null && commodityExtend.getSupplier().getSupplier_name() != null) {
            this.supplier_name = commodityExtend.getSupplier().getSupplier_name();
        }

        CommodityActivity commodityActivity = ActivityComponents.getByCommodity(commodityExtend);
        if (commodityActivity != null) {
            ActivityCommodity activityCommodity = commodityActivity.getActivityCommodity();
            this.activity_price = activityCommodity.getActivity_sell_price();
            this.activity_name = commodityActivity.getActivity_name();
        }
    }

    public String getSupplier_name() {
        return supplier_name;
    }

    public void setSupplier_name(String supplier_name) {
        this.supplier_name = supplier_name;
    }

    private String supplier_name;
    private String commodity_name;//商品名
    private String category_name;//类别名称
    private Map<String, String> attributes;//商品属性
    private String commodity_code;//型号
    private String remark;
    private BigDecimal price;
    private Integer commodity_id;
    private String commodity_size;
    private String supplier_commodity_code;
    private String commodity_remark;
    private String activity_name;
    private BigDecimal activity_price;

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public BigDecimal getActivity_price() {
        return activity_price;
    }

    public void setActivity_price(BigDecimal activity_price) {
        this.activity_price = activity_price;
    }

    public String getCommodity_remark() {
        return commodity_remark;
    }

    public void setCommodity_remark(String commodity_remark) {
        this.commodity_remark = commodity_remark;
    }

    public String getSupplier_commodity_code() {
        return supplier_commodity_code;
    }

    public void setSupplier_commodity_code(String supplier_commodity_code) {
        this.supplier_commodity_code = supplier_commodity_code;
    }

    public String getCommodity_size() {
        return commodity_size;
    }

    public void setCommodity_size(String commodity_size) {
        this.commodity_size = commodity_size;
    }

    public Integer getCommodity_id() {
        return commodity_id;
    }

    public void setCommodity_id(Integer commodity_id) {
        this.commodity_id = commodity_id;
    }

    public String getCommodity_name() {
        return commodity_name;
    }

    public void setCommodity_name(String commodity_name) {
        this.commodity_name = commodity_name;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getCommodity_code() {
        return commodity_code;
    }

    public void setCommodity_code(String commodity_code) {
        this.commodity_code = commodity_code;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
