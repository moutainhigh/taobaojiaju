package com.xinshan.service;

import com.xinshan.dao.CityMapper;
import com.xinshan.dao.DistrictMapper;
import com.xinshan.dao.ProvinceMapper;
import com.xinshan.dao.extend.supplier.ProvinceExtendMapper;
import com.xinshan.model.City;
import com.xinshan.model.District;
import com.xinshan.model.Province;
import com.xinshan.pojo.supplier.SupplierSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by mxt on 16-10-14.
 */
@Service
public class ProvinceService {
    @Autowired
    private ProvinceExtendMapper provinceExtendMapper;
    @Autowired
    private ProvinceMapper provinceMapper;
    @Autowired
    private CityMapper cityMapper;
    @Autowired
    private DistrictMapper districtMapper;

    @Transactional
    public void createProvince(Province province) {
        provinceExtendMapper.createProvince(province);
    }
    @Transactional
    public void createCity(City city) {
        provinceExtendMapper.createCity(city);
    }
    @Transactional
    public void createDistrict(District district) {
        provinceExtendMapper.createDistrict(district);
    }
    @Transactional
    public void updateProvince(Province province) {
        provinceMapper.updateByPrimaryKey(province);
    }
    @Transactional
    public void updateCity(City city) {
        cityMapper.updateByPrimaryKey(city);
    }
    @Transactional
    public void updateDistrict(District district) {
        districtMapper.updateByPrimaryKey(district);
    }

    public Province getProvinceById(int province_id) {
        return provinceMapper.selectByPrimaryKey(province_id);
    }
    public City getCityById(int city_id) {
        return cityMapper.selectByPrimaryKey(city_id);
    }
    public District getDistrictById(int district_id) {
        return districtMapper.selectByPrimaryKey(district_id);
    }
    @Transactional
    public void deleteProvince(int province_id) {
        provinceMapper.deleteByPrimaryKey(province_id);
    }
    @Transactional
    public void deleteCity(int city_id) {
        cityMapper.deleteByPrimaryKey(city_id);
    }
    @Transactional
    public void deleteDistrict(int district_id) {
        districtMapper.deleteByPrimaryKey(district_id);
    }

    public List<Province> provinceList(SupplierSearchOption supplierSearchOption) {
        return provinceExtendMapper.provinceList(supplierSearchOption);
    }

    public List<City> cityList(SupplierSearchOption supplierSearchOption) {
        return provinceExtendMapper.cityList(supplierSearchOption);
    }

    public List<District> districtList(SupplierSearchOption supplierSearchOption) {
        return provinceExtendMapper.districtList(supplierSearchOption);
    }

    public Province getProvinceByName(String province_name) {
        SupplierSearchOption supplierSearchOption = new SupplierSearchOption();
        supplierSearchOption.setProvince_name(province_name);
        List<Province> list = provinceList(supplierSearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public City getCityByName(String province_zip, String city_name) {
        SupplierSearchOption supplierSearchOption = new SupplierSearchOption();
        supplierSearchOption.setProvince_zip(province_zip);
        supplierSearchOption.setCity_name(city_name);
        List<City> list = cityList(supplierSearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public District getDistrictByName(String city_zip, String district_name) {
        SupplierSearchOption supplierSearchOption = new SupplierSearchOption();
        supplierSearchOption.setCity_zip(city_zip);
        supplierSearchOption.setDistrict_name(district_name);
        List<District> list = districtList(supplierSearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }
}
