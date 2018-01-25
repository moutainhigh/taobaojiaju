package com.xinshan.pojo.settlement;

import com.xinshan.utils.SplitUtils;
import com.xinshan.pojo.SearchOption;

import java.util.List;

/**
 * Created by mxt on 16-11-23.
 */
public class SettlementSearchOption extends SearchOption {
    private Integer purchase_id;
    private Integer supplier_id;
    private Integer order_id;
    private Integer settlement_status;
    private Integer settlement_id;
    private Integer inventory_history_id;
    private Integer after_sales_id;
    private Integer checking_status;
    private String settlement_ids;
    private List<Integer> settlementIdList;
    private Integer settlement_type;
    private String contacts;
    private Integer verify_status;
    private Integer guangdong;

    public Integer getGuangdong() {
        return guangdong;
    }

    public void setGuangdong(Integer guangdong) {
        this.guangdong = guangdong;
    }

    public Integer getVerify_status() {
        return verify_status;
    }

    public void setVerify_status(Integer verify_status) {
        this.verify_status = verify_status;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public Integer getSettlement_type() {
        return settlement_type;
    }

    public void setSettlement_type(Integer settlement_type) {
        this.settlement_type = settlement_type;
    }

    public String getSettlement_ids() {
        return settlement_ids;
    }

    public void setSettlement_ids(String settlement_ids) {
        this.settlement_ids = settlement_ids;
        if (settlement_ids != null && !"".equals(settlement_ids)) {
            List<Integer> settlementIdList = SplitUtils.splitToList(settlement_ids, ",");
            if (settlementIdList.size() > 0) {
                setSettlementIdList(settlementIdList);
            }
        }
    }

    public List<Integer> getSettlementIdList() {
        return settlementIdList;
    }

    public void setSettlementIdList(List<Integer> settlementIdList) {
        this.settlementIdList = settlementIdList;
    }

    public Integer getChecking_status() {
        return checking_status;
    }

    public void setChecking_status(Integer checking_status) {
        this.checking_status = checking_status;
    }

    public Integer getAfter_sales_id() {
        return after_sales_id;
    }

    public void setAfter_sales_id(Integer after_sales_id) {
        this.after_sales_id = after_sales_id;
    }

    public Integer getInventory_history_id() {
        return inventory_history_id;
    }

    public void setInventory_history_id(Integer inventory_history_id) {
        this.inventory_history_id = inventory_history_id;
    }

    public Integer getSettlement_id() {
        return settlement_id;
    }

    public void setSettlement_id(Integer settlement_id) {
        this.settlement_id = settlement_id;
    }

    public Integer getPurchase_id() {
        return purchase_id;
    }

    public void setPurchase_id(Integer purchase_id) {
        this.purchase_id = purchase_id;
    }

    public Integer getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public Integer getSettlement_status() {
        return settlement_status;
    }

    public void setSettlement_status(Integer settlement_status) {
        this.settlement_status = settlement_status;
    }
}
