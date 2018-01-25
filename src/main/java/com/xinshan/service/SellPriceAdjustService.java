package com.xinshan.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.commodity.CommodityPriceComponent;
import com.xinshan.dao.CommodityPriceAdjustDetailMapper;
import com.xinshan.dao.CommodityPriceAdjustMapper;
import com.xinshan.dao.extend.commodity.CommodityPriceAdjustExtendMapper;
import com.xinshan.model.Commodity;
import com.xinshan.model.CommodityPriceAdjust;
import com.xinshan.model.CommodityPriceAdjustDetail;
import com.xinshan.model.Employee;
import com.xinshan.model.extend.commodity.CommodityPriceAdjustDetailExtend;
import com.xinshan.model.extend.commodity.CommodityPriceAdjustExtend;
import com.xinshan.pojo.commodity.CommoditySearchOption;
import com.xinshan.utils.DateUtil;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mxt on 17-4-21.
 */
@Service
public class SellPriceAdjustService {
    @Autowired
    private CommodityPriceAdjustExtendMapper commodityPriceAdjustExtendMapper;
    @Autowired
    private CommodityPriceAdjustDetailMapper commodityPriceAdjustDetailMapper;
    @Autowired
    private CommodityService commodityService;
    @Autowired
    private CommodityPriceAdjustMapper commodityPriceAdjustMapper;

    @Transactional
    public void createCommodityPriceAdjust(CommodityPriceAdjustExtend commodityPriceAdjustExtend, Employee employee) {
        commodityPriceAdjustExtend.setAdjust_create_date(DateUtil.currentDate());
        commodityPriceAdjustExtend.setAdjust_create_employee_code(employee.getEmployee_code());
        commodityPriceAdjustExtend.setAdjust_create_employee_name(employee.getEmployee_name());
        commodityPriceAdjustExtend.setAdjust_status(0);
        commodityPriceAdjustExtendMapper.createCommodityPriceAdjust(commodityPriceAdjustExtend);
        List<CommodityPriceAdjustDetailExtend> list = commodityPriceAdjustExtend.getCommodityPriceAdjustDetails();
        createCommodityPriceAdjustDetail(list, commodityPriceAdjustExtend.getCommodity_price_adjust_id());
    }

    /**
     * 导入
     * @param list
     * @param employee
     */
    @Transactional
    public void createCommodityPriceAdjust(List<CommodityPriceAdjustDetailExtend> list, Employee employee) {
        CommodityPriceAdjust commodityPriceAdjust = new CommodityPriceAdjust();
        commodityPriceAdjust.setAdjust_create_date(DateUtil.currentDate());
        commodityPriceAdjust.setAdjust_create_employee_code(employee.getEmployee_code());
        commodityPriceAdjust.setAdjust_create_employee_name(employee.getEmployee_name());
        commodityPriceAdjust.setAdjust_status(0);
        commodityPriceAdjustExtendMapper.createCommodityPriceAdjust(commodityPriceAdjust);
        createCommodityPriceAdjustDetail(list, commodityPriceAdjust.getCommodity_price_adjust_id());
    }

    private void createCommodityPriceAdjustDetail(List<CommodityPriceAdjustDetailExtend> list, int commodity_price_adjust_id) {
        for (int i = 0; i < list.size(); i++) {
            CommodityPriceAdjustDetailExtend commodityPriceAdjustDetailExtend = list.get(i);
            commodityPriceAdjustDetailExtend.setCommodity_price_adjust_id(commodity_price_adjust_id);
            commodityPriceAdjustDetailExtend.setPrice_adjust_check_status(0);
            commodityPriceAdjustDetailExtend.setPrice_adjust_enable(1);
            commodityPriceAdjustExtendMapper.createAdjustDetail(commodityPriceAdjustDetailExtend);
        }
    }

    public List<CommodityPriceAdjustExtend> commodityPriceAdjustExtends(CommoditySearchOption commoditySearchOption) {
        List<CommodityPriceAdjustExtend> list = commodityPriceAdjustExtendMapper.commodityPriceAdjustExtends(commoditySearchOption);
        return list;
    }

    public Integer count(CommoditySearchOption commoditySearchOption) {
        Integer count = commodityPriceAdjustExtendMapper.countCommodityPriceAdjust(commoditySearchOption);
        return count;
    }

    public List<CommodityPriceAdjustDetailExtend> detailExtends(CommoditySearchOption commoditySearchOption) {
        return commodityPriceAdjustExtendMapper.detailExtends(commoditySearchOption);
    }

    public Integer countPriceAdjustDetail(CommoditySearchOption commoditySearchOption){
        return commodityPriceAdjustExtendMapper.countPriceAdjustDetail(commoditySearchOption);
    }

    @Transactional
    public void updatePriceAdjustDetail(JSONArray jsonArray, Employee employee) {
        Date date = DateUtil.currentDate();
        int size = jsonArray.size();
        for (int i = 0; i < size; i++) {
            Object o = jsonArray.get(i);
            CommodityPriceAdjustDetail commodityPriceAdjustDetail = JSONObject.parseObject(o.toString(), CommodityPriceAdjustDetail.class);
            Integer commodity_price_adjust_detail_id = commodityPriceAdjustDetail.getCommodity_price_adjust_detail_id();
            if (commodity_price_adjust_detail_id != null) {
                CommodityPriceAdjustDetail commodityPriceAdjustDetailOld = commodityPriceAdjustDetailMapper.selectByPrimaryKey(commodity_price_adjust_detail_id);
                if (commodityPriceAdjustDetail.getAfter_purchase_price() != null) {
                    commodityPriceAdjustDetailOld.setAfter_purchase_price(commodityPriceAdjustDetail.getAfter_purchase_price());
                }
                if (commodityPriceAdjustDetail.getAfter_sell_price() != null) {
                    commodityPriceAdjustDetailOld.setAfter_sell_price(commodityPriceAdjustDetail.getAfter_sell_price());
                }
                if (commodityPriceAdjustDetail.getBefore_purchase_price() != null) {
                    commodityPriceAdjustDetailOld.setBefore_purchase_price(commodityPriceAdjustDetail.getBefore_purchase_price());
                }
                if (commodityPriceAdjustDetail.getBefore_sell_price() != null) {
                    commodityPriceAdjustDetailOld.setBefore_sell_price(commodityPriceAdjustDetail.getBefore_sell_price());
                }
                if (commodityPriceAdjustDetail.getPrice_adjust_check_status() != null && commodityPriceAdjustDetail.getPrice_adjust_check_status() == 1) {
                    if (commodityPriceAdjustDetailOld.getPrice_adjust_check_status() == 0) {
                        commodityPriceAdjustDetailOld.setPrice_adjust_check_status(1);
                        commodityPriceAdjustDetailOld.setPrice_adjust_check_employee_name(employee.getEmployee_name());
                        commodityPriceAdjustDetailOld.setPrice_adjust_check_employee_code(employee.getEmployee_code());
                        commodityPriceAdjustDetailOld.setPrice_adjust_check_date(date);
                        //审核,商品价格变动
                        Commodity commodity = commodityService.getCommodityById(commodityPriceAdjustDetailOld.getCommodity_id());
                        commodity.setSell_price(commodityPriceAdjustDetailOld.getAfter_sell_price());
                        commodity.setPurchase_price(commodityPriceAdjustDetailOld.getAfter_purchase_price());
                        commodity.setEdit_date(DateUtil.currentDate());
                        commodity.setEdit_employee_code(employee.getEmployee_code());
                        commodity.setEdit_employee_name(employee.getEmployee_name());
                        CommodityPriceComponent.priceAdjust(commodity, commodity_price_adjust_detail_id, employee);
                        commodityService.updateCommodity1(commodity);
                    }
                }
                commodityPriceAdjustDetailMapper.updateByPrimaryKey(commodityPriceAdjustDetailOld);
            }else {
                commodityPriceAdjustExtendMapper.createAdjustDetail(commodityPriceAdjustDetail);
            }
        }
    }

    @Transactional
    public void disable(List<Integer> detailIds) {
        CommoditySearchOption commoditySearchOption = new CommoditySearchOption();
        commoditySearchOption.setCommodityPriceAdjustDetailIds(detailIds);
        List<CommodityPriceAdjustDetailExtend> list = commodityPriceAdjustExtendMapper.detailExtends(commoditySearchOption);
        for (int i = 0; i < list.size(); i++) {
            CommodityPriceAdjustDetailExtend commodityPriceAdjustDetailExtend = list.get(i);
            if (commodityPriceAdjustDetailExtend.getPrice_adjust_check_status() == 1 && commodityPriceAdjustDetailExtend.getPrice_adjust_enable() == 1) {
                commodityPriceAdjustDetailExtend.setPrice_adjust_enable(0);
                commodityPriceAdjustDetailMapper.updateByPrimaryKey(commodityPriceAdjustDetailExtend);
                Commodity commodity = commodityService.getCommodityById(commodityPriceAdjustDetailExtend.getCommodity_id());
                BigDecimal before_sell_price = commodityPriceAdjustDetailExtend.getBefore_sell_price();
                BigDecimal before_purchase_price = commodityPriceAdjustDetailExtend.getBefore_purchase_price();
                commodity.setSell_price(before_sell_price);
                commodity.setPurchase_price(before_purchase_price);
                commodityService.updateCommodity(commodity);
            }
        }
    }

    public CommodityPriceAdjust getPriceAdjust(int commodity_price_adjust_id) {
        return commodityPriceAdjustMapper.selectByPrimaryKey(commodity_price_adjust_id);
    }

    @Transactional
    public void updateCommodityPriceAdjust(CommodityPriceAdjust commodityPriceAdjust) {
        commodityPriceAdjustMapper.updateByPrimaryKey(commodityPriceAdjust);
    }

}
