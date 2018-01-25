package com.xinshan.pojo.inventory;

import com.xinshan.pojo.SearchOption;

/**
 * Created by mxt on 17-5-10.
 */
public class InventoryCheckSearchOption extends SearchOption {
    private Integer carry_over_status;
    private Integer inventory_id;
    private Integer supplier_id;
    private Integer commodity_store_id;
    private Integer sample;
    private Integer profit_status;

    public Integer getCarry_over_status() {
        return carry_over_status;
    }

    public void setCarry_over_status(Integer carry_over_status) {
        this.carry_over_status = carry_over_status;
    }

    public Integer getInventory_id() {
        return inventory_id;
    }

    public void setInventory_id(Integer inventory_id) {
        this.inventory_id = inventory_id;
    }

    public Integer getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }

    public Integer getCommodity_store_id() {
        return commodity_store_id;
    }

    public void setCommodity_store_id(Integer commodity_store_id) {
        this.commodity_store_id = commodity_store_id;
    }

    public Integer getSample() {
        return sample;
    }

    public void setSample(Integer sample) {
        this.sample = sample;
    }

    public Integer getProfit_status() {
        return profit_status;
    }

    public void setProfit_status(Integer profit_status) {
        this.profit_status = profit_status;
    }
}
