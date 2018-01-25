package com.xinshan.components.commodity;

import com.xinshan.model.*;
import com.xinshan.model.extend.commodity.CommodityExtend;
import com.xinshan.service.CommodityPriceService;
import com.xinshan.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Created by mxt on 17-5-9.
 */
@Component
public class CommodityPriceComponent {
    private static CommodityPriceService commodityPriceService;

    @Autowired
    public void setCommodityPriceService(CommodityPriceService commodityPriceService) {
        CommodityPriceComponent.commodityPriceService = commodityPriceService;
    }

    /**
     * 添加
     * @param commodity
     */
    public static void createCommodityPrice(Commodity commodity, String employee_name, String employee_code) {
        try {
            Commodity commodityOld = null;
            Integer commodity_id = commodity.getCommodity_id();
            if (commodity_id != null) {
                commodityOld = CommodityComponent.getCommodityById(commodity_id);
            }else {
                String commodity_code = commodity.getCommodity_code();
                if (commodity_code != null) {
                    commodityOld = CommodityComponent.getCommodityByCode(commodity_code);
                }
            }
            if (commodityOld == null) {
                //新增商品
                createCommodityPriceNewCommodity(commodity, employee_name, employee_code);
            }else {
                createCommodityPriceEditCommodity(commodity, commodityOld, employee_name, employee_code);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增商品记录价格
     * @param commodity
     * @param
     */
    private static void createCommodityPriceNewCommodity(Commodity commodity, String employee_name, String employee_code) {
        CommodityPriceRecord commodityPriceRecord = new CommodityPriceRecord();
        commodityPriceRecord.setAfter_price(commodity.getSell_price());
        commodityPriceRecord.setBefore_price(new BigDecimal(0));
        commodityPriceRecord.setAfter_purchase_price(commodity.getPurchase_price());
        commodityPriceRecord.setBefore_purchase_price(new BigDecimal("0"));
        commodityPriceRecord.setCommodity_id(commodity.getCommodity_id());
        commodityPriceRecord.setCommodity_code(commodity.getCommodity_code());
        commodityPriceRecord.setModify_date(DateUtil.currentDate());
        commodityPriceRecord.setModity_employee_code(employee_code);
        commodityPriceRecord.setModity_employee_name(employee_name);
        commodityPriceService.createCommodityPrice(commodityPriceRecord);
    }

    /**
     * 编辑商品 价格变动记录
     * @param commodity
     * @param
     */
    private static void createCommodityPriceEditCommodity(Commodity commodity, Commodity commodityOld, String employee_name, String employee_code) {
        if (commodityOld.getSell_price().equals(commodity.getSell_price()) && commodityOld.getPurchase_price().equals(commodity.getPurchase_price())) {
            return;
        }
        CommodityPriceRecord commodityPriceRecord = new CommodityPriceRecord();
        commodityPriceRecord.setAfter_price(commodity.getSell_price());
        commodityPriceRecord.setBefore_price(commodityOld.getSell_price());
        commodityPriceRecord.setAfter_purchase_price(commodity.getPurchase_price());
        commodityPriceRecord.setBefore_purchase_price(commodityOld.getPurchase_price());
        commodityPriceRecord.setCommodity_id(commodity.getCommodity_id());
        commodityPriceRecord.setCommodity_code(commodity.getCommodity_code());
        commodityPriceRecord.setModify_date(DateUtil.currentDate());
        commodityPriceRecord.setModity_employee_code(employee_code);
        commodityPriceRecord.setModity_employee_name(employee_name);
        commodityPriceService.createCommodityPrice(commodityPriceRecord);
    }

    /**
     * 调价
     * @param commodity
     * @param commodity_price_adjust_detail_id
     * @param employee
     */
    public static void priceAdjust(Commodity commodity, int commodity_price_adjust_detail_id, Employee employee) {
        Integer commodity_id = commodity.getCommodity_id();
        CommodityExtend commodityExtend = CommodityComponent.getCommodityById(commodity_id);
        CommodityPriceRecord commodityPriceRecord = new CommodityPriceRecord();
        commodityPriceRecord.setAfter_price(commodity.getSell_price());
        commodityPriceRecord.setBefore_price(commodityExtend.getSell_price());
        commodityPriceRecord.setAfter_purchase_price(commodity.getPurchase_price());
        commodityPriceRecord.setBefore_purchase_price(commodityExtend.getPurchase_price());
        commodityPriceRecord.setCommodity_id(commodity.getCommodity_id());
        commodityPriceRecord.setCommodity_code(commodity.getCommodity_code());
        commodityPriceRecord.setModify_date(DateUtil.currentDate());
        commodityPriceRecord.setModity_employee_code(employee.getEmployee_code());
        commodityPriceRecord.setModity_employee_name(employee.getEmployee_name());
        commodityPriceRecord.setSell_price_adjust_detail_id(commodity_price_adjust_detail_id);
        commodityPriceService.createCommodityPrice(commodityPriceRecord);
    }
}
