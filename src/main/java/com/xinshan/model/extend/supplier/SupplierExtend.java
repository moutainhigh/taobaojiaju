package com.xinshan.model.extend.supplier;

import com.xinshan.model.*;

/**
 * Created by mxt on 16-10-14.
 */
public class SupplierExtend extends Supplier {
    private Province province;
    private City city;
    private District district;
    private SellType sellType;
    private Employee employee;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public SellType getSellType() {
        return sellType;
    }

    public void setSellType(SellType sellType) {
        this.sellType = sellType;
    }
}
