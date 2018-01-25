package com.xinshan.service;

import com.xinshan.dao.GiftCommodityMapper;
import com.xinshan.dao.GiftMapper;
import com.xinshan.dao.extend.gift.GiftExtendMapper;
import com.xinshan.model.*;
import com.xinshan.model.extend.gift.GiftCommodityExtend;
import com.xinshan.model.extend.gift.GiftExtend;
import com.xinshan.pojo.gift.GiftSearchOption;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.constant.ConstantUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mxt on 17-4-15.
 */
@Service
public class GiftService {
    @Autowired
    private UserService userService;
    @Autowired
    private GiftExtendMapper giftExtendMapper;
    @Autowired
    private GiftCommodityMapper giftCommodityMapper;
    @Autowired
    private GiftMapper giftMapper;
    @Autowired
    private InventoryOutService inventoryOutService;


    @Transactional
    public synchronized void createGift(GiftExtend gift, Employee employee) {
        User user = gift.getUser();
        user = userService.createUser(user.getUser_name(), user.getUser_phone(), user.getUser_address(),
                null, null, user.getPosition_id(), user.getProvince_zip(), user.getCity_zip(),
                user.getDistrict_zip(), user.getUser_second_phone());
        gift.setUser_id(user.getUser_id());
        gift.setGift_create_date(DateUtil.currentDate());
        gift.setGift_create_employee_code(employee.getEmployee_code());
        gift.setGift_create_employee_name(employee.getEmployee_name());
        gift.setGift_code(giftCode(null));
        gift.setGift_return_status(0);
        gift.setGift_settlement_status(0);
        gift.setGift_out_status(0);
        gift.setGift_enable(1);
        gift.setGift_purchase_status(0);
        giftExtendMapper.createGift(gift);
        List<GiftCommodityExtend> list = gift.getGiftCommodities();
        for (int i = 0; i < list.size(); i++) {
            GiftCommodity giftCommodity = list.get(i);
            giftCommodity.setGift_id(gift.getGift_id());
            giftCommodity.setGift_commodity_out_status(0);
            giftCommodity.setGift_commodity_return_status(0);
            giftExtendMapper.createGiftCommodity(giftCommodity);
        }
    }

    /**
     *
     * @return
     */
    private String giftCode(String gift_code) {
        String n = null;
        if (gift_code == null) {
            String s = ConstantUtils.GIFT_CODE_HEADER + DateUtil.format(DateUtil.currentDate(), "yyMMdd") + "[0-9]{4}";
            n = giftExtendMapper.todayGiftNum(s);//DZD161031001
        }else {
            n = gift_code;
        }
        if (n == null) {
            return ConstantUtils.GIFT_CODE_HEADER + Long.parseLong(DateUtil.format(DateUtil.currentDate(), "yyMMdd"))*1000+1l;
        }
        n = n.substring(ConstantUtils.GIFT_CODE_HEADER.length(), n.length());
        int m = Integer.parseInt(n);
        m++;
        return ConstantUtils.GIFT_CODE_HEADER + m;
    }

    public List<Integer> giftIds(GiftSearchOption giftSearchOption) {
        return giftExtendMapper.giftIds(giftSearchOption);
    }

    public List<GiftExtend> giftList(GiftSearchOption giftSearchOption) {
        List<GiftExtend> list = giftExtendMapper.giftList(giftSearchOption);
        for (int i = 0; i < list.size(); i++) {
            GiftExtend gift = list.get(i);
            gift.setGiftCommodities(getGiftCommodityByGiftId(gift.getGift_id()));
        }
        return list;
    }

    public GiftExtend getGiftById(int gift_id) {
        GiftSearchOption giftSearchOption = new GiftSearchOption();
        List<Integer> giftIds = new ArrayList<>();
        giftIds.add(gift_id);
        giftSearchOption.setGiftIds(giftIds);
        List<GiftExtend> list = giftExtendMapper.giftList(giftSearchOption);
        if (list != null && list.size() == 1) {
            GiftExtend gift = list.get(0);
            gift.setGiftCommodities(getGiftCommodityByGiftId(gift.getGift_id()));
            return gift;
        }
        return null;
    }
    @Transactional
    public void updateGift(Gift gift) {
        giftMapper.updateByPrimaryKey(gift);
    }

    public Integer countGift(GiftSearchOption giftSearchOption) {
        return giftExtendMapper.countGift(giftSearchOption);
    }

    public List<GiftCommodityExtend> getGiftCommodityByGiftId(int gift_id) {
        GiftSearchOption giftSearchOption = new GiftSearchOption();
        giftSearchOption.setGift_id(gift_id);
        return giftExtendMapper.giftCommodities(giftSearchOption);
    }

    public List<GiftCommodityExtend> giftCommodities(GiftSearchOption giftSearchOption) {
        return giftExtendMapper.giftCommodities(giftSearchOption);
    }
    public Integer countGiftCommodities(GiftSearchOption giftSearchOption) {
        return giftExtendMapper.countGiftCommodities(giftSearchOption);
    }

    public GiftCommodityExtend getGiftCommodityById(int gift_commodity_id) {
        GiftSearchOption giftSearchOption = new GiftSearchOption();
        giftSearchOption.setGift_commodity_id(gift_commodity_id);
        List<GiftCommodityExtend> list = giftExtendMapper.giftCommodities(giftSearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public GiftCommodity getGiftCommodity(int gift_commodity_id) {
        return giftCommodityMapper.selectByPrimaryKey(gift_commodity_id);
    }

    public void updateGiftCommodity(GiftCommodity giftCommodity) {
        giftCommodityMapper.updateByPrimaryKey(giftCommodity);
    }

}
