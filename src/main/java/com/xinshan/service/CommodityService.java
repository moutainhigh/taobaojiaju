package com.xinshan.service;

import com.xinshan.components.activity.ActivityComponents;
import com.xinshan.components.commodity.CommodityComponent;
import com.xinshan.components.commodity.CommodityPriceComponent;
import com.xinshan.dao.*;
import com.xinshan.dao.extend.commodity.CommodityExtendMapper;
import com.xinshan.dao.extend.supplier.SupplierExtendMapper;
import com.xinshan.model.*;
import com.xinshan.model.extend.commodity.CommodityActivity;
import com.xinshan.model.extend.commodity.CommodityExtend;
import com.xinshan.pojo.commodity.CommoditySearchOption;
import com.xinshan.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mxt on 16-10-17.
 */
@Service
public class CommodityService {
    @Autowired
    private CommodityExtendMapper commodityExtendMapper;
    @Autowired
    private CommodityColorMapper commodityColorMapper;
    @Autowired
    private CommodityUnitMapper commodityUnitMapper;
    @Autowired
    private CommodityStoreMapper commodityStoreMapper;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private SupplierExtendMapper supplierExtendMapper;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private SupplierMapper supplierMapper;
    @Autowired
    private CommodityMapper commodityMapper;

    @Transactional
    public void createColor (CommodityColor commodityColor) {
        commodityExtendMapper.createColor(commodityColor);
    }
    @Transactional
    public void updateColor(CommodityColor commodityColor) {
        commodityColorMapper.updateByPrimaryKey(commodityColor);
    }
    @Transactional
    public void createUnit (CommodityUnit commodityUnit) {
        commodityExtendMapper.createUnit(commodityUnit);
    }
    @Transactional
    public void updateUnit(CommodityUnit commodityUnit) {
        commodityUnitMapper.updateByPrimaryKey(commodityUnit);
    }

    @Transactional
    public void deleteUnit(int unit_id) {
        commodityUnitMapper.deleteByPrimaryKey(unit_id);
    }
    @Transactional
    public void deleteColor(int color_id) {
        commodityColorMapper.deleteByPrimaryKey(color_id);
    }

    private int supplierCommodityCode(){
        int min = 100000000;
        String s = commodityExtendMapper.maxSupplierCommodityCode();
        if (s == null) {
            return min;
        }
        int n = Integer.parseInt(s);
        if (n < min) {
            return min;
        }else {
            return ++n;
        }
    }

    @Transactional
    public void createCommodity(CommodityExtend commodity) {
        commodity.setCommodity_status(1);
        if (commodity.getCommodity_freight() == null) {
            commodity.setCommodity_freight(new BigDecimal("0"));
        }
        if (commodity.getCommodity_sample() == null) {
            commodity.setCommodity_sample(0);
        }
        if (commodity.getGuangdong() == null) {
            commodity.setGuangdong(0);
        }
        CommodityPriceComponent.createCommodityPrice(commodity, commodity.getRecord_employee_name(), commodity.getRecord_employee_code());
        commodity.setSupplier_commodity_code(String.valueOf(supplierCommodityCode()));
        commodityExtendMapper.createCommodity(commodity);
        commodityQRCode(commodity);

        List<CommodityAttribute> list = commodity.getCommodityAttributes();
        for (int i = 0; i < list.size(); i++) {
            CommodityAttribute attribute = list.get(i);
            attribute.setCommodity_id(commodity.getCommodity_id());
            commodityExtendMapper.createCommodityAttribute(attribute);
        }
    }

    public void createCommodity(Commodity commodity) {
        commodity.setCommodity_status(1);
        if (commodity.getCommodity_freight() == null) {
            commodity.setCommodity_freight(new BigDecimal("0"));
        }
        if (commodity.getCommodity_sample() == null) {
            commodity.setCommodity_sample(0);
        }

        if (commodity.getSell_price() == null) {
            commodity.setSell_price(new BigDecimal("0"));
        }
        if (commodity.getPurchase_price() == null) {
            commodity.setPurchase_price(new BigDecimal("0"));
        }
        if (commodity.getCommodity_freight() == null) {
            commodity.setCommodity_freight(new BigDecimal("0"));
        }
        if (commodity.getGuangdong() == null) {
            commodity.setGuangdong(0);
        }
        CommodityPriceComponent.createCommodityPrice(commodity, commodity.getRecord_employee_name(), commodity.getRecord_employee_code());
        commodity.setSupplier_commodity_code(String.valueOf(supplierCommodityCode()));
        commodityExtendMapper.createCommodity(commodity);
        commodityQRCode(commodity);
    }

    private void commodityQRCode(Commodity commodity) {
        String qrcode = CommodityComponent.qrcode(commodity);
        commodity.setQrcode(qrcode);
        commodityExtendMapper.updateCommodityQrcode(commodity);
    }

    private void commodityImport(Commodity commodity, String supplier_name,
                                 String series_name, String store_name, Employee employee,
                                 int num, int sichuan, int sample, String contacts,
                                 String contacts_phone_number, String contacts_address) {
        Supplier supplier = null;
        SupplierSeries supplierSeries = null;
        CommodityStore commodityStore = null;
        if (supplier_name != null && !supplier_name.equals("")) {
            supplier = supplierService.getSupplierByName(supplier_name);
            if (supplier == null) {
                supplier = new Supplier();
                supplier.setSupplier_name(supplier_name);
                supplier.setContacts(contacts);
                supplier.setContacts_phone_number(contacts_phone_number);
                supplier.setContacts_address(contacts_address);
                supplier.setRecord_employee_code(employee.getEmployee_code());
                supplier.setRecord_employee_name(employee.getEmployee_name());
                supplier.setSupplier_status(1);
                supplierService.createSupplier(supplier);
            }else {
                supplier.setSupplier_name(supplier_name);
                supplier.setContacts(contacts);
                supplier.setContacts_phone_number(contacts_phone_number);
                supplier.setContacts_address(contacts_address);
                supplierMapper.updateByPrimaryKey(supplier);
            }

            if (series_name != null && !series_name.equals("")) {
                supplierSeries = supplierService.getSeriesByName(supplier.getSupplier_id(), series_name);
                if (supplierSeries == null) {
                    supplierSeries = new SupplierSeries();
                    supplierSeries.setSeries_name(series_name);
                    supplierSeries.setSupplier_id(supplier.getSupplier_id());
                    supplierSeries.setSeries_status(1);
                    supplierExtendMapper.createSeries(supplierSeries);
                }
            }
        }
        if (store_name != null && !store_name.equals("")) {
            commodityStore = inventoryService.getStoreByName(store_name);
            if (commodityStore == null) {
                commodityStore = new CommodityStore();
                commodityStore.setStore_name(store_name);
                commodityStore.setStore_enable(1);
                inventoryService.createStore(commodityStore);
            }
        }

        if (supplier != null) {
            commodity.setSupplier_id(supplier.getSupplier_id());
        }
        if (supplierSeries != null) {
            commodity.setSupplier_series_id(supplierSeries.getSupplier_series_id());
        }
        Commodity commodityOld = getCommodityByCode(commodity.getCommodity_code(), sichuan);
        if (commodityOld != null) {
            commodity.setCommodity_id(commodityOld.getCommodity_id());
            commodity.setSichuan(sichuan);
            commodity.setCommodity_status(commodityOld.getCommodity_status());
            if (commodityOld.getRecord_employee_code() != null) {
                commodity.setRecord_employee_code(commodityOld.getRecord_employee_code());
                commodity.setRecord_employee_name(commodityOld.getRecord_employee_name());
            }
            if (commodity.getCommodity_name() == null || "".equals(commodity.getCommodity_name())) {
                commodity.setCommodity_name(commodityOld.getCommodity_name());
            }
            if (commodity.getSupplier_commodity_code() == null  || "".equals(commodity.getSupplier_commodity_code())) {
                commodity.setSupplier_commodity_code(commodityOld.getSupplier_commodity_code());
            }
            if (commodity.getPurchase_price() == null  || "".equals(commodity.getPurchase_price())) {
                commodity.setPurchase_price(commodityOld.getPurchase_price());
            }
            if (commodity.getSell_price() == null  || "".equals(commodity.getSell_price())) {
                commodity.setSell_price(commodityOld.getSell_price());
            }
            if (commodity.getCommodity_size() == null  || "".equals(commodity.getCommodity_size())) {
                commodity.setCommodity_size(commodityOld.getCommodity_size());
            }
            if (commodity.getGuangdong() == null) {
                commodity.setGuangdong(0);
            }
            commodity.setEdit_employee_code(employee.getEmployee_code());
            commodity.setEdit_employee_name(employee.getEmployee_name());
            commodity.setRecord_date(commodityOld.getRecord_date());
            commodity.setRecord_employee_code(commodityOld.getRecord_employee_code());
            commodity.setRecord_employee_name(commodityOld.getRecord_employee_name());
            commodity.setEdit_date(DateUtil.currentDate());
            CommodityPriceComponent.createCommodityPrice(commodity, commodity.getRecord_employee_name(), commodity.getRecord_employee_code());
            commodityExtendMapper.updateCommodity(commodity);
        }else {
            commodity.setCommodity_status(1);
            commodity.setRecord_date(DateUtil.currentDate());
            commodity.setSichuan(sichuan);
            commodity.setCommodity_status(1);
            if (commodity.getCommodity_freight() == null) {
                commodity.setCommodity_freight(new BigDecimal("0"));
            }
            if (commodity.getCommodity_sample() == null) {
                commodity.setCommodity_sample(0);
            }
            if (commodity.getGuangdong() == null) {
                commodity.setGuangdong(0);
            }
            CommodityPriceComponent.createCommodityPrice(commodity, commodity.getRecord_employee_name(), commodity.getRecord_employee_code());
            commodity.setSupplier_commodity_code(String.valueOf(supplierCommodityCode()));
            commodityExtendMapper.createCommodity(commodity);
            commodityQRCode(commodity);
        }

        CommodityNum commodityNum = inventoryService.getNumByCommodityIdAndStoreId(commodity.getCommodity_id(),
                commodityStore.getCommodity_store_id(), sample, 0);
        if (commodityNum == null) {
            inventoryService.createCommodityNum(commodity.getCommodity_id(), commodityStore.getCommodity_store_id(), sample, num, 0);
        }else {
            commodityNum.setNum(num);
            inventoryService.updateCommodityNum(commodityNum);
        }
    }

    @Transactional
    public void commodityImport(List<Object[]> list, Employee employee) {
        for (int i = 0; i < list.size(); i++) {
            Object[] o = list.get(i);
            Commodity commodity = (Commodity) o[0];
            String supplier_name = null;
            if (o[1] != null) {
                supplier_name = o[1].toString();
            }
            String series_name = null;
            if (o[2] != null) {
                series_name = o[2].toString();
            }
            String store_name = null;
            if (o[3] != null) {
                store_name = o[3].toString();
            }
            int num = 0,sichuan = 0, sample = 0, guangdong = 0;
            if (o[4] != null) {
                num = Integer.parseInt(o[4].toString());
            }
            if (o[5] != null) {
                sichuan = Integer.parseInt(o[5].toString());
            }
            if (o[6] != null) {
                sample = Integer.parseInt(o[6].toString());
            }
            String contacts = null;
            String contacts_phone_number = null;
            String contacts_address = null;
            if (o[7] != null) {
                contacts =o[7].toString();
            }
            if (o[8] != null) {
                contacts_phone_number =o[8].toString();
            }
            if (o[9] != null) {
                contacts_address =o[9].toString();
            }
            if (o[10] != null) {
                guangdong = Integer.parseInt(o[10].toString());
            }
            commodity.setGuangdong(guangdong);
            commodityImport(commodity, supplier_name, series_name, store_name, employee, num,
                    sichuan, sample, contacts, contacts_phone_number, contacts_address);
        }
    }
    @Transactional
    public void commodityInit(List<Object[]> list, Employee employee) {
        for (int i = 0; i < list.size(); i++) {
            Object[] o = list.get(i);
            Commodity commodity = (Commodity) o[0];
            String supplier_name = (String) o[1];
            String contacts = (String) o[2];
            int sample = (int) o[3];
            Supplier supplier = supplierService.getSupplierByName(supplier_name);
            if (supplier == null) {
                supplier = new Supplier();
                supplier.setContacts(contacts);
                supplier.setSupplier_name(supplier_name);
                supplier.setRecord_employee_code(employee.getEmployee_code());
                supplier.setRecord_employee_name(employee.getEmployee_name());
                supplier.setSupplier_status(1);
                supplierService.createSupplier(supplier);
            }else {
                supplier.setContacts(contacts);
                supplier.setEdit_employee_code(employee.getEmployee_code());
                supplier.setEdit_employee_name(employee.getEmployee_name());
                supplier.setEdit_date(DateUtil.currentDate());
                supplierService.updateSupplier(supplier);
            }
            Commodity commodity1 = getCommodityByCode(commodity.getCommodity_code());
            commodity.setSupplier_id(supplier.getSupplier_id());
            if (commodity1 != null) {
                commodity1.setSupplier_id(commodity.getSupplier_id());
                commodity1.setSupplier_commodity_code(commodity.getSupplier_commodity_code());//原货号
                commodity1.setCommodity_name(commodity.getCommodity_name());//商品名称
                commodity1.setCommodity_size(commodity.getCommodity_size());//商品规格
                commodity1.setSell_price(commodity.getSell_price());//售价
                commodity1.setPurchase_price(commodity.getPurchase_price());//进价
                //commodity1.setRecord_employee_code(employee.getEmployee_code());
                //commodity1.setRecord_employee_name(employee.getEmployee_name());
                commodity1.setCommodity_freight(commodity.getCommodity_freight());
                if (commodity1.getCommodity_freight() == null) {
                    commodity1.setCommodity_freight(new BigDecimal("0"));
                }
                if (commodity.getGuangdong() == null) {
                    commodity.setGuangdong(0);
                }
                commodity1.setEdit_employee_name(employee.getEmployee_name());
                commodity1.setEdit_employee_code(employee.getEmployee_code());
                CommodityPriceComponent.createCommodityPrice(commodity1, commodity.getRecord_employee_name(), commodity.getRecord_employee_code());
                commodityExtendMapper.updateCommodity(commodity1);
            }else {
                commodity.setCommodity_status(1);
                if (commodity.getCommodity_freight() == null) {
                    commodity.setCommodity_freight(new BigDecimal("0"));
                }
                if (commodity.getCommodity_sample() == null) {
                    commodity.setCommodity_sample(0);
                }
                if (commodity.getGuangdong() == null) {
                    commodity.setGuangdong(0);
                }
                CommodityPriceComponent.createCommodityPrice(commodity, commodity.getRecord_employee_name(), commodity.getRecord_employee_code());
                commodity.setSupplier_commodity_code(String.valueOf(supplierCommodityCode()));
                commodityExtendMapper.createCommodity(commodity);
                commodityQRCode(commodity);
            }
        }
    }

    @Transactional
    public void updateCommodity(Commodity commodity) {
        if (commodity.getGuangdong() == null) {
            commodity.setGuangdong(0);
        }
        CommodityPriceComponent.createCommodityPrice(commodity, commodity.getRecord_employee_name(), commodity.getRecord_employee_code());
        commodityExtendMapper.updateCommodity(commodity);
    }

    @Transactional
    public void updateCommodityQrcode(Commodity commodity) {
        commodityExtendMapper.updateCommodityQrcode(commodity);
    }

    public void updateCommodity1(Commodity commodity) {
        if (commodity.getGuangdong() == null) {
            commodity.setGuangdong(0);
        }
        commodityExtendMapper.updateCommodity(commodity);
    }
    @Transactional
    public void updateCommodity(CommodityExtend commodity, Employee employee) {
        Commodity commodityOld = getCommodityById(commodity.getCommodity_id());
        if (commodity.getSichuan() == null) {
            commodity.setSichuan(commodityOld.getSichuan());
        }
        if (commodity.getCommodity_status() == null) {
            commodity.setCommodity_status(commodityOld.getCommodity_status());
        }
        if (commodity.getCommodity_sample() == null) {
            commodity.setCommodity_sample(commodityOld.getCommodity_sample());
        }
        if (commodity.getGuangdong() == null) {
            commodity.setGuangdong(0);
        }
        commodity.setRecord_date(commodityOld.getRecord_date());
        commodity.setRecord_employee_code(commodityOld.getRecord_employee_code());
        commodity.setRecord_employee_name(commodityOld.getRecord_employee_name());
        commodity.setEdit_date(DateUtil.currentDate());
        commodity.setEdit_employee_code(employee.getEmployee_code());
        commodity.setEdit_employee_name(employee.getEmployee_name());
        CommodityPriceComponent.createCommodityPrice(commodity, employee.getEmployee_name(), employee.getEmployee_code());
        commodityExtendMapper.updateCommodity(commodity);
        commodityExtendMapper.deleteCommodityAttribute(commodity.getCommodity_id());
        List<CommodityAttribute> list = commodity.getCommodityAttributes();
        for (int i = 0; i < list.size(); i++) {
            CommodityAttribute attribute = list.get(i);
            attribute.setCommodity_id(commodity.getCommodity_id());
            commodityExtendMapper.createCommodityAttribute(attribute);
        }
    }

    public List<CommodityUnit> units(CommoditySearchOption commoditySearchOption) {
        return commodityExtendMapper.units(commoditySearchOption);
    }
    public CommodityColor getCommodityColor(String color_name) {
        CommoditySearchOption commoditySearchOption = new CommoditySearchOption();
        commoditySearchOption.setColor_name(color_name);
        List<CommodityColor> colors = colors(commoditySearchOption);
        if (colors != null && colors.size() == 1 && colors.get(0).getColor_name().equals(color_name)) {
            return colors.get(0);
        }
        return null;
    }
    public List<CommodityColor> colors(CommoditySearchOption commoditySearchOption) {
        return commodityExtendMapper.colors(commoditySearchOption);
    }

    public List<CommodityExtend> commodityList(CommoditySearchOption commoditySearchOption) {
        List<CommodityExtend> list = commodityExtendMapper.commodityList(commoditySearchOption);
        for (int i = 0; i < list.size(); i++) {
            CommodityExtend commodity = list.get(i);
            commodity.setCommodityAttributes(commodityExtendMapper.commodityAttributes(commodity.getCommodity_id()));
            CommodityActivity commodityActivity = ActivityComponents.getByCommodity(commodity);
            commodity.setCommodityActivity(commodityActivity);
            commodity.setActivities(ActivityComponents.commodityActivities(commodity));
        }
        return list;
    }

    public List<HashMap> commodityExport(CommoditySearchOption commoditySearchOption) {
        return commodityExtendMapper.commodityExport(commoditySearchOption);
    }


    public List<CommodityExtend> commodityEasyList(CommoditySearchOption commoditySearchOption) {
        return commodityExtendMapper.commodityEasyList(commoditySearchOption);
    }

    public Integer countCommodity(CommoditySearchOption commoditySearchOption){
        return commodityExtendMapper.countCommodity(commoditySearchOption);
    }

    public Commodity getCommodityById(int commodity_id) {
        return commodityMapper.selectByPrimaryKey(commodity_id);
    }
    public Commodity getCommodityByCode(String commodity_code) {
        CommoditySearchOption commoditySearchOption = new CommoditySearchOption();
        commoditySearchOption.setCommodity_code(commodity_code);
        List<CommodityExtend> list = commodityExtendMapper.commodityList(commoditySearchOption);
        if (list != null && list.size() >0) {
            return list.get(0);
        }
        return null;
    }

    public Commodity getCommodityByCode(String commodity_code, int sichuan) {
        CommoditySearchOption commoditySearchOption = new CommoditySearchOption();
        commoditySearchOption.setCommodity_code(commodity_code);
        //commoditySearchOption.setSichuan(sichuan);
        List<CommodityExtend> list = commodityExtendMapper.commodityList(commoditySearchOption);
        if (list != null && list.size() >0) {
            return list.get(0);
        }
        return null;
    }

    public CommodityStore getCommodityStoreById(int store_id) {
        return commodityStoreMapper.selectByPrimaryKey(store_id);
    }

    @Transactional
    public void commodityPrice(Commodity commodity, Employee employee) {
        if (commodity.getGuangdong() == null) {
            commodity.setGuangdong(0);
        }
        CommodityPriceComponent.createCommodityPrice(commodity, employee.getEmployee_name(), employee.getEmployee_code());
        commodityExtendMapper.updateCommodity(commodity);
    }

    public CommodityExtend getCommodityByQRCode(String qrcode) {
        return commodityExtendMapper.getCommodityByQRCode(qrcode);
    }

    /**
     *
     * @param commodity_ids
     * @param commodity_sample
     */
    @Transactional
    public void commoditySample(String commodity_ids,int commodity_sample, Employee employee) {
        CommoditySearchOption commoditySearchOption = new CommoditySearchOption();
        commoditySearchOption.setCommodity_ids(commodity_ids);
        List<CommodityExtend> list = commodityExtendMapper.commodityList(commoditySearchOption);
        for (int i = 0; i < list.size(); i++) {
            CommodityExtend commodityExtend = list.get(i);
            commodityExtend.setCommodity_sample(commodity_sample);
            if (commodityExtend.getGuangdong() == null) {
                commodityExtend.setGuangdong(0);
            }
            commodityExtend.setEdit_date(DateUtil.currentDate());
            commodityExtend.setEdit_employee_code(employee.getEmployee_code());
            commodityExtend.setEdit_employee_name(employee.getEmployee_name());
            commodityExtendMapper.updateCommodity(commodityExtend);
        }
    }

    @Transactional
    public void commodityGuangdong(int guangdong, int supplier_id){
        commodityExtendMapper.commodityGuangdong(guangdong, supplier_id);
    }
}
