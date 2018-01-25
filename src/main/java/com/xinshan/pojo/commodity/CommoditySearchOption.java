package com.xinshan.pojo.commodity;

import com.xinshan.pojo.SearchOption;

import java.util.List;

/**
 * Created by mxt on 16-10-18.
 */
public class CommoditySearchOption extends SearchOption {
    private Integer commodity_id;
    private Integer commodity_unit_id;
    private Integer commodity_color_id;
    private Integer commodity_store_id;
    private String commodity_code;
    private String commodity_ids;
    private Integer supplier_id;
    private String store_name;
    private Integer commodity_num;
    private Integer sample;
    private Integer commodity_status;
    private Integer return_commodity;
    private Integer commodity_num_id;
    private Integer sichuan;
    private Integer commodity_price_adjust_id;
    private Integer price_adjust_check_status;
    private Integer price_adjust_enable;
    private Integer sell_price_order;//0顺序，1到序
    private List<Integer> commodityIdList;
    private List<Integer> supplierIdList;
    private List<Integer> supplierSeriesList;
    private List<Integer> commodityPriceAdjustDetailIds;
    private Integer sync_status;
    private Integer priceOrder;//0正序，1倒序
    private Integer store_enable;
    private Integer adjust_price;//是否调价，0没有调价，1有过调价
    private String prepareStatuses;
    private List<Integer> commodityPrepareIds;
    private String supplierIds;
    private Integer commodity_prepare_id;
    private String color_name;
    private Integer activity_id;
    private Integer activity_price;//是否有活动价

    public Integer getActivity_price() {
        return activity_price;
    }

    public void setActivity_price(Integer activity_price) {
        this.activity_price = activity_price;
    }

    public Integer getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(Integer activity_id) {
        this.activity_id = activity_id;
    }

    public String getColor_name() {
        return color_name;
    }

    public void setColor_name(String color_name) {
        this.color_name = color_name;
    }

    public Integer getCommodity_prepare_id() {
        return commodity_prepare_id;
    }

    public void setCommodity_prepare_id(Integer commodity_prepare_id) {
        this.commodity_prepare_id = commodity_prepare_id;
    }

    public String getSupplierIds() {
        return supplierIds;
    }

    public void setSupplierIds(String supplierIds) {
        this.supplierIds = supplierIds;
    }

    public List<Integer> getCommodityPrepareIds() {
        return commodityPrepareIds;
    }

    public void setCommodityPrepareIds(List<Integer> commodityPrepareIds) {
        this.commodityPrepareIds = commodityPrepareIds;
    }

    public String getPrepareStatuses() {
        return prepareStatuses;
    }

    public void setPrepareStatuses(String prepareStatuses) {
        this.prepareStatuses = prepareStatuses;
    }

    public Integer getAdjust_price() {
        return adjust_price;
    }

    public void setAdjust_price(Integer adjust_price) {
        this.adjust_price = adjust_price;
    }

    public Integer getStore_enable() {
        return store_enable;
    }

    public void setStore_enable(Integer store_enable) {
        this.store_enable = store_enable;
    }

    public Integer getPriceOrder() {
        return priceOrder;
    }

    public void setPriceOrder(Integer priceOrder) {
        this.priceOrder = priceOrder;
    }

    public Integer getSync_status() {
        return sync_status;
    }

    public void setSync_status(Integer sync_status) {
        this.sync_status = sync_status;
    }

    public List<Integer> getCommodityPriceAdjustDetailIds() {
        return commodityPriceAdjustDetailIds;
    }

    public void setCommodityPriceAdjustDetailIds(List<Integer> commodityPriceAdjustDetailIds) {
        this.commodityPriceAdjustDetailIds = commodityPriceAdjustDetailIds;
    }

    public Integer getCommodity_price_adjust_id() {
        return commodity_price_adjust_id;
    }

    public void setCommodity_price_adjust_id(Integer commodity_price_adjust_id) {
        this.commodity_price_adjust_id = commodity_price_adjust_id;
    }

    public Integer getPrice_adjust_check_status() {
        return price_adjust_check_status;
    }

    public void setPrice_adjust_check_status(Integer price_adjust_check_status) {
        this.price_adjust_check_status = price_adjust_check_status;
    }

    public Integer getPrice_adjust_enable() {
        return price_adjust_enable;
    }

    public void setPrice_adjust_enable(Integer price_adjust_enable) {
        this.price_adjust_enable = price_adjust_enable;
    }

    public List<Integer> getSupplierSeriesList() {
        return supplierSeriesList;
    }

    public void setSupplierSeriesList(List<Integer> supplierSeriesList) {
        this.supplierSeriesList = supplierSeriesList;
    }

    public List<Integer> getCommodityIdList() {
        return commodityIdList;
    }

    public void setCommodityIdList(List<Integer> commodityIdList) {
        this.commodityIdList = commodityIdList;
    }

    public List<Integer> getSupplierIdList() {
        return supplierIdList;
    }

    public void setSupplierIdList(List<Integer> supplierIdList) {
        this.supplierIdList = supplierIdList;
    }

    public Integer getSichuan() {
        return sichuan;
    }

    public void setSichuan(Integer sichuan) {
        this.sichuan = sichuan;
    }

    public Integer getSell_price_order() {
        return sell_price_order;
    }

    public void setSell_price_order(Integer sell_price_order) {
        this.sell_price_order = sell_price_order;
    }

    public Integer getCommodity_num_id() {
        return commodity_num_id;
    }

    public void setCommodity_num_id(Integer commodity_num_id) {
        this.commodity_num_id = commodity_num_id;
    }

    public Integer getReturn_commodity() {
        return return_commodity;
    }

    public void setReturn_commodity(Integer return_commodity) {
        this.return_commodity = return_commodity;
    }

    public Integer getCommodity_status() {
        return commodity_status;
    }

    public void setCommodity_status(Integer commodity_status) {
        this.commodity_status = commodity_status;
    }

    public Integer getSample() {
        return sample;
    }

    public void setSample(Integer sample) {
        this.sample = sample;
    }

    public Integer getCommodity_num() {
        return commodity_num;
    }

    public void setCommodity_num(Integer commodity_num) {
        this.commodity_num = commodity_num;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public Integer getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }

    public String getCommodity_ids() {
        return commodity_ids;
    }

    public void setCommodity_ids(String commodity_ids) {
        this.commodity_ids = commodity_ids;
    }

    public Integer getCommodity_id() {
        return commodity_id;
    }

    public void setCommodity_id(Integer commodity_id) {
        this.commodity_id = commodity_id;
    }

    public Integer getCommodity_unit_id() {
        return commodity_unit_id;
    }

    public void setCommodity_unit_id(Integer commodity_unit_id) {
        this.commodity_unit_id = commodity_unit_id;
    }

    public Integer getCommodity_color_id() {
        return commodity_color_id;
    }

    public void setCommodity_color_id(Integer commodity_color_id) {
        this.commodity_color_id = commodity_color_id;
    }

    public Integer getCommodity_store_id() {
        return commodity_store_id;
    }

    public void setCommodity_store_id(Integer commodity_store_id) {
        this.commodity_store_id = commodity_store_id;
    }

    public String getCommodity_code() {
        return commodity_code;
    }

    public void setCommodity_code(String commodity_code) {
        this.commodity_code = commodity_code;
    }
}
