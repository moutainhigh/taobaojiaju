package com.xinshan.service;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.xinshan.dao.SellTypeMapper;
import com.xinshan.dao.SupplierMapper;
import com.xinshan.dao.SupplierSeriesMapper;
import com.xinshan.dao.extend.supplier.SupplierExtendMapper;
import com.xinshan.model.*;
import com.xinshan.model.extend.supplier.SupplierExtend;
import com.xinshan.model.extend.supplier.SupplierSeriesExtend;
import com.xinshan.pojo.supplier.SupplierSearchOption;
import com.xinshan.utils.EncryptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by mxt on 16-10-14.
 */
@Service
public class SupplierService {
    @Autowired
    private SupplierExtendMapper supplierExtendMapper;
    @Autowired
    private SupplierMapper supplierMapper;
    @Autowired
    private SellTypeMapper sellTypeMapper;
    @Autowired
    private SupplierSeriesMapper supplierSeriesMapper;
    @Autowired
    private ProvinceService provinceService;
    @Autowired
    private EmployeeService employeeService;

    public Employee createSupplierContacts(String contacts) {
        if (contacts == null || contacts.equals("")) {
            return null;
        }
        String employee_code = null;
        try {
            employee_code = PinyinHelper.convertToPinyinString(contacts, "", PinyinFormat.WITHOUT_TONE);
            System.out.println(contacts + "\t" + employee_code);
        }catch (Exception e) {
            e.printStackTrace();
        }
        if (employee_code == null) {
            return null;
        }
        Employee employeeByCode = employeeService.getEmployeeByCode(employee_code);
        if (employeeByCode == null) {
            employeeByCode = new Employee();
            employeeByCode.setEmployee_code(employee_code);
            employeeByCode.setEmployee_name(contacts);
            employeeByCode.setEmployee_status(1);
            employeeByCode.setEmployee_password(EncryptionUtils.encryption("123456"));
            employeeByCode.setPosition_id(0);
            employeeByCode.setRole_ids("56");
            employeeService.createEmployee(employeeByCode);
        }
        return employeeByCode;
    }

    @Transactional
    public void createSupplier(Supplier supplier) {
        /*String contacts = supplier.getContacts();
        Employee supplierContacts = createSupplierContacts(contacts);
        if (supplierContacts != null) {
            supplier.setContacts_code(supplierContacts.getEmployee_code());
        }*/
        supplierExtendMapper.createSupplier(supplier);
    }
    @Transactional
    public void updateSupplier(Supplier supplier) {
        supplierMapper.updateByPrimaryKey(supplier);
    }

    @Transactional
    public void deleteSupplier(int supplier_id) {
        supplierMapper.deleteByPrimaryKey(supplier_id);
    }

    public SupplierExtend getSupplierById(int supplier_id) {
        SupplierSearchOption supplierSearchOption = new SupplierSearchOption();
        supplierSearchOption.setSupplier_id(supplier_id);
        List<SupplierExtend> list = supplierExtendMapper.supplierList(supplierSearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }
    public SupplierExtend getSupplierByName(String supplier_name) {
        SupplierSearchOption supplierSearchOption = new SupplierSearchOption();
        supplierSearchOption.setSupplier_name(supplier_name);
        List<SupplierExtend> list = supplierExtendMapper.supplierList(supplierSearchOption);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public List<SupplierExtend> supplierList(SupplierSearchOption supplierSearchOption) {
        return supplierExtendMapper.supplierList(supplierSearchOption);
    }
    public Integer countSupplier(SupplierSearchOption supplierSearchOption) {
        return supplierExtendMapper.countSupplier(supplierSearchOption);
    }

    @Transactional
    public void createSellType(SellType sellType) {
        supplierExtendMapper.createSellType(sellType);
    }
    @Transactional
    public void deleteSellType(int sell_type_id) {
        sellTypeMapper.deleteByPrimaryKey(sell_type_id);
    }

    public SellType getSellTypeByName(String sell_type_name) {
        return supplierExtendMapper.getSellTypeByName(sell_type_name);
    }

    public List<SellType> sellTypeList() {
        return sellTypeMapper.selectAll();
    }

    @Transactional
    public void createSeries(SupplierSeries supplierSeries) {
        supplierExtendMapper.createSeries(supplierSeries);
    }

    @Transactional
    public void updateSeries(SupplierSeries supplierSeries) {
        supplierSeriesMapper.updateByPrimaryKey(supplierSeries);
    }

    public List<SupplierSeriesExtend> seriesList(SupplierSearchOption supplierSearchOption) {
        return supplierExtendMapper.seriesList(supplierSearchOption);
    }

    public Integer countSeries(SupplierSearchOption supplierSearchOption) {
        return supplierExtendMapper.countSeries(supplierSearchOption);
    }

    public SupplierSeries getSeriesByName(int supplier_id, String series_name) {
        SupplierSearchOption supplierSearchOption = new SupplierSearchOption();
        supplierSearchOption.setSupplier_id(supplier_id);
        supplierSearchOption.setSeries_name(series_name);
        List<SupplierSeriesExtend> list = supplierExtendMapper.seriesList(supplierSearchOption);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Transactional
    public void supplierImport(List<Object[]> list) {
        for (int i = 0; i < list.size(); i++) {
            Object[] o = list.get(i);
            SupplierExtend supplierExtend = (SupplierExtend) o[0];
            String province_name = (String) o[1];
            String city_name = (String) o[2];
            String district_name = (String) o[3];
            String sell_type_name = (String) o[4];
            String province_zip = null, city_zip = null, district_zip = null;

            if (province_name != null && !"".equals(province_name)) {
                Province province = provinceService.getProvinceByName(province_name);
                if (province != null) {
                    province_zip = province.getProvince_zip();
                    if (city_name != null && !"".equals(city_name)) {
                        City city = provinceService.getCityByName(province_zip, city_name);
                        if (city != null) {
                            city_zip = city.getCity_zip();
                            if (district_name != null && !"".equals(district_name)) {
                                District district = provinceService.getDistrictByName(city_zip, district_name);
                                if (district != null) {
                                    district_zip = district.getDistrict_zip();
                                }
                            }
                        }
                    }
                }
            }

            supplierExtend.setProvince_zip(province_zip);
            supplierExtend.setCity_zip(city_zip);
            supplierExtend.setDistrict_zip(district_zip);

            if (sell_type_name != null && !"".equals(sell_type_name)) {
                SellType sellType = getSellTypeByName(sell_type_name);
                if (sellType != null) {
                    supplierExtend.setSell_type_id(sellType.getSell_type_id());
                }else {
                    sellType = new SellType();
                    sellType.setSell_type_name(sell_type_name);
                    createSellType(sellType);
                    supplierExtend.setSell_type_id(sellType.getSell_type_id());
                }
            }
            Supplier supplier = getSupplierByName(supplierExtend.getSupplier_name());
            if (supplier != null) {
                supplierExtend.setSupplier_id(supplier.getSupplier_id());
                supplierMapper.updateByPrimaryKey(supplierExtend);
            }else {
                createSupplier(supplierExtend);
            }
        }
    }

    public List<String> supplierContacts(SupplierSearchOption supplierSearchOption) {
        return supplierExtendMapper.supplierContacts(supplierSearchOption);
    }

    public Integer countSupplierContracts(SupplierSearchOption supplierSearchOption) {
        return supplierExtendMapper.countSupplierContracts(supplierSearchOption);
    }
}
