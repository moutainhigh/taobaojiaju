package com.xinshan.pojo.supplier;

import com.xinshan.pojo.SearchOption;

import java.util.List;

/**
 * Created by mxt on 16-10-14.
 */
public class SupplierSearchOption extends SearchOption {
    private String province_zip;
    private String city_zip;
    private String district_zip;
    private Integer supplier_id;
    private Integer sell_type_id;
    private Integer supplier_series_id;
    private String supplier_name;
    private String series_name;
    private Integer supplier_status;
    private String province_name;
    private String city_name;
    private String district_name;
    private String contacts_param;
    private List<Integer> supplier_ids;
    private String contacts_name;
    private String contacts_phone;
    private String contacts_code;

    public String getContacts_code() {
        return contacts_code;
    }

    public void setContacts_code(String contacts_code) {
        this.contacts_code = contacts_code;
    }

    public String getContacts_phone() {
        return contacts_phone;
    }

    public void setContacts_phone(String contacts_phone) {
        this.contacts_phone = contacts_phone;
    }

    public String getContacts_name() {
        return contacts_name;
    }

    public void setContacts_name(String contacts_name) {
        this.contacts_name = contacts_name;
    }

    public List<Integer> getSupplier_ids() {
        return supplier_ids;
    }

    public void setSupplier_ids(List<Integer> supplier_ids) {
        this.supplier_ids = supplier_ids;
    }

    public String getContacts_param() {
        return contacts_param;
    }

    public void setContacts_param(String contacts_param) {
        this.contacts_param = contacts_param;
    }

    public String getProvince_name() {
        return province_name;
    }

    public void setProvince_name(String province_name) {
        this.province_name = province_name;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getDistrict_name() {
        return district_name;
    }

    public void setDistrict_name(String district_name) {
        this.district_name = district_name;
    }

    public Integer getSupplier_status() {
        return supplier_status;
    }

    public void setSupplier_status(Integer supplier_status) {
        this.supplier_status = supplier_status;
    }

    public String getSeries_name() {
        return series_name;
    }

    public void setSeries_name(String series_name) {
        this.series_name = series_name;
    }

    public String getSupplier_name() {
        return supplier_name;
    }

    public void setSupplier_name(String supplier_name) {
        this.supplier_name = supplier_name;
    }

    public Integer getSupplier_series_id() {
        return supplier_series_id;
    }

    public void setSupplier_series_id(Integer supplier_series_id) {
        this.supplier_series_id = supplier_series_id;
    }

    public String getProvince_zip() {
        return province_zip;
    }

    public void setProvince_zip(String province_zip) {
        this.province_zip = province_zip;
    }

    public String getCity_zip() {
        return city_zip;
    }

    public void setCity_zip(String city_zip) {
        this.city_zip = city_zip;
    }

    public String getDistrict_zip() {
        return district_zip;
    }

    public void setDistrict_zip(String district_zip) {
        this.district_zip = district_zip;
    }

    public Integer getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }

    public Integer getSell_type_id() {
        return sell_type_id;
    }

    public void setSell_type_id(Integer sell_type_id) {
        this.sell_type_id = sell_type_id;
    }
}
