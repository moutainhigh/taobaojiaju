package com.xinshan.service;

import com.xinshan.components.category.CategoryComponent;
import com.xinshan.dao.CommodityPrepareMapper;
import com.xinshan.dao.extend.commodity.CommodityPrepareExtendMapper;
import com.xinshan.model.Commodity;
import com.xinshan.model.CommodityColor;
import com.xinshan.model.CommodityPrepare;
import com.xinshan.model.Employee;
import com.xinshan.model.extend.category.CategoryExtend;
import com.xinshan.model.extend.commodity.CommodityPrepareExtend;
import com.xinshan.model.extend.supplier.SupplierExtend;
import com.xinshan.pojo.commodity.CommoditySearchOption;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.Request2ModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by mxt on 17-9-6.
 */
@Service
public class CommodityPrepareService {
    @Autowired
    private CommodityPrepareExtendMapper commodityPrepareExtendMapper;
    @Autowired
    private CommodityPrepareMapper commodityPrepareMapper;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CommodityService commodityService;

    @Transactional
    public void createCommodityPrepare(CommodityPrepare commodityPrepare) {
        CommodityPrepareExtend commodityPrepare1 = getCommodityPrepare(commodityPrepare.getCommodity_code());
        if (commodityPrepare1 == null) {
            commodityPrepareExtendMapper.createCommodityPrepare(commodityPrepare);
        }else {
            commodityPrepare.setCommodity_prepare_id(commodityPrepare1.getCommodity_prepare_id());
            commodityPrepareMapper.updateByPrimaryKey(commodityPrepare);
        }
    }

    @Transactional
    public void updateCommodityPrepare(CommodityPrepare commodityPrepare) {
        commodityPrepareMapper.updateByPrimaryKey(commodityPrepare);
    }

    public CommodityPrepareExtend getCommodityPrepare(String commodity_code) {
        CommoditySearchOption commoditySearchOption = new CommoditySearchOption();
        commoditySearchOption.setCommodity_code(commodity_code);
        List<CommodityPrepareExtend> commodityPrepareExtends = commodityPrepareList(commoditySearchOption);
        if (commodityPrepareExtends != null && commodityPrepareExtends.size() == 1 && commodityPrepareExtends.get(0).getCommodity_code().equals(commodity_code)) {
            return commodityPrepareExtends.get(0);
        }
        return null;
    }

    public List<CommodityPrepareExtend> commodityPrepareList(CommoditySearchOption commoditySearchOption) {
        return commodityPrepareExtendMapper.commodityPrepareList(commoditySearchOption);
    }

    public Integer countCommodityPrepare(CommoditySearchOption commoditySearchOption) {
        return commodityPrepareExtendMapper.countCommodityPrepare(commoditySearchOption);
    }

    @Transactional
    public void commodityPrepareImport(List<Object[]> list, Employee employee) {
        for (int i = 0; i < list.size(); i++) {
            Object[] objects = list.get(i);
            CommodityPrepare commodityPrepare = (CommodityPrepare) objects[0];
            String supplier_name = (String) objects[1];
            String category_name = (String) objects[2];
            String color = (String) objects[3];
            if (commodityPrepare.getCommodity_code() == null) {
                continue;
            }
            if (supplier_name != null && !supplier_name.equals("")) {
                SupplierExtend supplierByName = supplierService.getSupplierByName(supplier_name);
                if (supplierByName != null) {
                    commodityPrepare.setSupplier_id(supplierByName.getSupplier_id());
                }
            }
            if (category_name != null && !category_name.equals("")) {
                CategoryExtend categoryName = CategoryComponent.getCategoryName(category_name);
                if (categoryName != null) {
                    commodityPrepare.setCategory_id(categoryName.getCategory_id());
                }
            }

            if (color != null && !color.equals("")) {
                CommodityColor commodityColor = commodityService.getCommodityColor(color);
                if (commodityColor != null) {
                    commodityPrepare.setCommodity_color_id(commodityColor.getCommodity_color_id());
                }
            }

            CommodityPrepareExtend commodityPrepare1 = getCommodityPrepare(commodityPrepare.getCommodity_code());
            if (commodityPrepare1 == null) {
                commodityPrepare.setRecord_employee_code(employee.getEmployee_code());
                commodityPrepare.setRecord_employee_name(employee.getEmployee_name());
                commodityPrepare.setRecord_date(DateUtil.currentDate());
                commodityPrepare.setPrepare_status(0);
                createCommodityPrepare(commodityPrepare);
            }else {
                commodityPrepare.setEdit_date(DateUtil.currentDate());
                commodityPrepare.setEdit_employee_code(employee.getEmployee_code());
                commodityPrepare.setEdit_employee_name(employee.getEmployee_name());
                commodityPrepare.setPrepare_status(commodityPrepare1.getPrepare_status());
                commodityPrepare.setCommodity_prepare_id(commodityPrepare1.getCommodity_prepare_id());
                updateCommodityPrepare(commodityPrepare);
            }
        }
    }


    @Transactional
    public void commodityPrepareCheck(List<CommodityPrepareExtend> list, Employee employee) {
        for (int i = 0; i < list.size(); i++) {
            CommodityPrepareExtend commodityPrepareExtend = list.get(i);
            commodityPrepareCheck(commodityPrepareExtend, employee);
        }
    }

    public void commodityPrepareCheck(CommodityPrepareExtend commodityPrepareExtend, Employee employee) {
        int prepare_status = 1;
        try {
            createCommodity(commodityPrepareExtend);
        }catch (Exception e) {
            e.printStackTrace();
            prepare_status = -1;
        }
        commodityPrepareExtend.setCheck_employee_code(employee.getEmployee_code());
        commodityPrepareExtend.setCheck_employee_code(employee.getEmployee_name());
        commodityPrepareExtend.setPrepare_status(prepare_status);
        commodityPrepareMapper.updateByPrimaryKey(commodityPrepareExtend);
    }
    @Transactional
    public void createCommodity(CommodityPrepare commodityPrepare) {
        Commodity commodity = new Commodity();
        Request2ModelUtils.covertObj(commodity, commodityPrepare);
        commodityService.createCommodity(commodity);
    }
}
