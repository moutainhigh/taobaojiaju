package com.xinshan.components.commodity;

import com.xinshan.model.Commodity;
import com.xinshan.model.extend.commodity.CommodityExtend;
import com.xinshan.pojo.commodity.CommoditySearchOption;
import com.xinshan.service.CommodityService;
import com.xinshan.utils.EncryptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by mxt on 17-5-5.
 */
@Component
public class CommodityComponent {
    public static final String salt = "taobaojiaju";

    private static CommodityService commodityService;

    @Autowired
    public void setCommodityService(CommodityService commodityService) {
        CommodityComponent.commodityService = commodityService;
    }

    /**
     *
     * @param commodity
     * @return
     */
    public static String qrcode(Commodity commodity) {
        Integer commodity_id = commodity.getCommodity_id();
        String commodity_code = commodity.getCommodity_code();
        String bitMD5 = EncryptionUtils.get32BitMD5(salt + commodity_code + "" + commodity_id);
        return bitMD5;
    }

    public static CommodityExtend getCommodityById(int commodity_id) {
        CommoditySearchOption commoditySearchOption = new CommoditySearchOption();
        commoditySearchOption.setCommodity_id(commodity_id);
        List<CommodityExtend> list = commodityService.commodityList(commoditySearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }
    public static CommodityExtend getCommodityByCode(String commodity_code) {
        CommoditySearchOption commoditySearchOption = new CommoditySearchOption();
        commoditySearchOption.setCommodity_code(commodity_code);
        List<CommodityExtend> list = commodityService.commodityList(commoditySearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public static List<CommodityExtend> commodityExtends(CommoditySearchOption commoditySearchOption) {
        return commodityService.commodityList(commoditySearchOption);
    }
}
