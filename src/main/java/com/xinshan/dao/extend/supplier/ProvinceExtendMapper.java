package com.xinshan.dao.extend.supplier;

import com.xinshan.model.City;
import com.xinshan.model.District;
import com.xinshan.model.Province;
import com.xinshan.pojo.supplier.SupplierSearchOption;

import java.util.List;

/**
 * Created by mxt on 16-10-14.
 */
public interface ProvinceExtendMapper {
    void createProvince(Province province);
    void createCity(City city);
    void createDistrict(District district);
    List<Province> provinceList(SupplierSearchOption supplierSearchOption);
    List<City> cityList(SupplierSearchOption supplierSearchOption);
    List<District> districtList(SupplierSearchOption supplierSearchOption);
}
