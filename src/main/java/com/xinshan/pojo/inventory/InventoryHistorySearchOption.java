package com.xinshan.pojo.inventory;

import com.xinshan.pojo.SearchOption;

import java.util.List;

/**
 * Created by mxt on 17-4-24.
 */
public class InventoryHistorySearchOption extends SearchOption {
    private List<Integer> inventoryHistoryIds;
    private Integer inventory_in_id;
    private Integer commodity_id;
    private Integer commodity_store_id;
    private String commodity_code;
    private Integer supplier_id;
    private List<Integer> inventoryInIds;
    private Integer inventory_in_commodity_id;
    private List<Integer> inventoryOutIds;
    private Integer order_id;
    private Integer purchase_commodity_id;
    private String inventory_out_status;
    private Integer inventory_history_id;
    private String inventory_type;//0入库，1出库
    private Integer inventory_out_id;
    private String create_employee_code;
    private String confirm_employee_code;
    private Integer inventory_move_id;
    private Integer order_return_id;
    private String inventory_out_fee_check_status;
    private Integer gift_id;
    private Integer gift_return_id;
    private Integer inventory_history_in_out;
    private String logistics_status;
    private Integer inventory_history_settlement_status;

    public Integer getInventory_history_settlement_status() {
        return inventory_history_settlement_status;
    }

    public void setInventory_history_settlement_status(Integer inventory_history_settlement_status) {
        this.inventory_history_settlement_status = inventory_history_settlement_status;
    }

    public String getLogistics_status() {
        return logistics_status;
    }

    public void setLogistics_status(String logistics_status) {
        this.logistics_status = logistics_status;
    }

    public Integer getInventory_history_in_out() {
        return inventory_history_in_out;
    }

    public void setInventory_history_in_out(Integer inventory_history_in_out) {
        this.inventory_history_in_out = inventory_history_in_out;
    }

    public Integer getGift_return_id() {
        return gift_return_id;
    }

    public void setGift_return_id(Integer gift_return_id) {
        this.gift_return_id = gift_return_id;
    }

    public Integer getGift_id() {
        return gift_id;
    }

    public void setGift_id(Integer gift_id) {
        this.gift_id = gift_id;
    }

    public String getInventory_out_fee_check_status() {
        return inventory_out_fee_check_status;
    }

    public void setInventory_out_fee_check_status(String inventory_out_fee_check_status) {
        this.inventory_out_fee_check_status = inventory_out_fee_check_status;
    }

    private Integer inventory_history_id_order;

    public Integer getInventory_history_id_order() {
        return inventory_history_id_order;
    }

    public void setInventory_history_id_order(Integer inventory_history_id_order) {
        this.inventory_history_id_order = inventory_history_id_order;
    }

    public List<Integer> getInventoryHistoryIds() {
        return inventoryHistoryIds;
    }

    public void setInventoryHistoryIds(List<Integer> inventoryHistoryIds) {
        this.inventoryHistoryIds = inventoryHistoryIds;
    }

    public Integer getInventory_in_id() {
        return inventory_in_id;
    }

    public void setInventory_in_id(Integer inventory_in_id) {
        this.inventory_in_id = inventory_in_id;
    }

    public Integer getCommodity_id() {
        return commodity_id;
    }

    public void setCommodity_id(Integer commodity_id) {
        this.commodity_id = commodity_id;
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

    public Integer getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }

    public List<Integer> getInventoryInIds() {
        return inventoryInIds;
    }

    public void setInventoryInIds(List<Integer> inventoryInIds) {
        this.inventoryInIds = inventoryInIds;
    }

    public Integer getInventory_in_commodity_id() {
        return inventory_in_commodity_id;
    }

    public void setInventory_in_commodity_id(Integer inventory_in_commodity_id) {
        this.inventory_in_commodity_id = inventory_in_commodity_id;
    }

    public List<Integer> getInventoryOutIds() {
        return inventoryOutIds;
    }

    public void setInventoryOutIds(List<Integer> inventoryOutIds) {
        this.inventoryOutIds = inventoryOutIds;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public Integer getPurchase_commodity_id() {
        return purchase_commodity_id;
    }

    public void setPurchase_commodity_id(Integer purchase_commodity_id) {
        this.purchase_commodity_id = purchase_commodity_id;
    }

    public String getInventory_out_status() {
        return inventory_out_status;
    }

    public void setInventory_out_status(String inventory_out_status) {
        this.inventory_out_status = inventory_out_status;
    }

    public Integer getInventory_history_id() {
        return inventory_history_id;
    }

    public void setInventory_history_id(Integer inventory_history_id) {
        this.inventory_history_id = inventory_history_id;
    }

    public String getInventory_type() {
        return inventory_type;
    }

    public void setInventory_type(String inventory_type) {
        this.inventory_type = inventory_type;
    }

    public Integer getInventory_out_id() {
        return inventory_out_id;
    }

    public void setInventory_out_id(Integer inventory_out_id) {
        this.inventory_out_id = inventory_out_id;
    }

    public String getCreate_employee_code() {
        return create_employee_code;
    }

    public void setCreate_employee_code(String create_employee_code) {
        this.create_employee_code = create_employee_code;
    }

    public String getConfirm_employee_code() {
        return confirm_employee_code;
    }

    public void setConfirm_employee_code(String confirm_employee_code) {
        this.confirm_employee_code = confirm_employee_code;
    }

    public Integer getInventory_move_id() {
        return inventory_move_id;
    }

    public void setInventory_move_id(Integer inventory_move_id) {
        this.inventory_move_id = inventory_move_id;
    }

    public Integer getOrder_return_id() {
        return order_return_id;
    }

    public void setOrder_return_id(Integer order_return_id) {
        this.order_return_id = order_return_id;
    }
}
