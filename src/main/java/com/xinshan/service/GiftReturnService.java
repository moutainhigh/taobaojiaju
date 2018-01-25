package com.xinshan.service;

import com.xinshan.dao.GiftReturnMapper;
import com.xinshan.dao.extend.gift.GiftReturnExtendMapper;
import com.xinshan.model.Employee;
import com.xinshan.model.GiftCommodity;
import com.xinshan.model.GiftReturn;
import com.xinshan.model.extend.gift.GiftCommodityExtend;
import com.xinshan.model.extend.gift.GiftReturnCommodityExtend;
import com.xinshan.model.extend.gift.GiftReturnExtend;
import com.xinshan.model.extend.inventory.InventoryInExtend;
import com.xinshan.pojo.gift.GiftSearchOption;
import com.xinshan.pojo.inventory.InventorySearchOption;
import com.xinshan.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by mxt on 17-5-26.
 */
@Service
public class GiftReturnService {
    @Autowired
    private GiftReturnExtendMapper giftReturnExtendMapper;
    @Autowired
    private InventoryInService inventoryInService;
    @Autowired
    private GiftService giftService;
    @Autowired
    private InventoryHistoryService inventoryHistoryService;
    @Autowired
    private GiftReturnMapper giftReturnMapper;
    @Autowired
    private SettlementService settlementService;

    @Transactional
    public void createGiftReturn(GiftReturnExtend giftReturnExtend, Employee employee) {
        giftReturnExtend.setGift_return_employee_code(employee.getEmployee_code());
        giftReturnExtend.setGift_return_employee_name(employee.getEmployee_name());
        giftReturnExtend.setGift_return_status(0);
        giftReturnExtend.setGift_return_create_date(DateUtil.currentDate());
        giftReturnExtendMapper.createGiftReturn(giftReturnExtend);
        List<GiftReturnCommodityExtend> giftReturnCommodityExtends = giftReturnExtend.getGiftReturnCommodityExtends();
        for (int i = 0; i < giftReturnCommodityExtends.size(); i++) {
            GiftReturnCommodityExtend giftReturnCommodityExtend = giftReturnCommodityExtends.get(i);
            Integer return_num = giftReturnCommodityExtend.getReturn_num();
            if (return_num <= 0) {
                continue;
            }
            Integer gift_commodity_id = giftReturnCommodityExtend.getGift_commodity_id();
            GiftCommodityExtend giftCommodity = giftService.getGiftCommodityById(gift_commodity_id);
            giftCommodity.setGift_commodity_return_status(1);
            giftService.updateGiftCommodity(giftCommodity);
            giftReturnCommodityExtend.setGift_return_id(giftReturnExtend.getGift_return_id());
            giftReturnExtendMapper.createGiftReturnCommodity(giftReturnCommodityExtend);
        }
    }

    public GiftReturnExtend giftReturnExtend(int gift_return_id) {
        GiftSearchOption giftSearchOption = new GiftSearchOption();
        giftSearchOption.setGift_return_id(gift_return_id);
        List<GiftReturnExtend> giftReturnExtends = giftReturnExtends(giftSearchOption);
        if (giftReturnExtends != null && giftReturnExtends.size() == 1) {
            return giftReturnExtends.get(0);
        }
        return null;
    }

    public List<GiftReturnExtend> giftReturnExtends(GiftSearchOption giftSearchOption) {
        List<GiftReturnExtend> giftReturnExtends = giftReturnExtendMapper.giftReturnList(giftSearchOption);
        for (int i = 0; i < giftReturnExtends.size(); i++) {
            GiftReturnExtend giftReturnExtend = giftReturnExtends.get(i);
            giftReturnExtend.setGiftReturnCommodityExtends(giftReturnCommodities(giftReturnExtend.getGift_return_id()));
        }
        return giftReturnExtends;
    }

    public Integer count(GiftSearchOption giftSearchOption) {
        return giftReturnExtendMapper.countGiftReturn(giftSearchOption);
    }

    public List<GiftReturnCommodityExtend> giftReturnCommodities(int gift_return_id) {
        GiftSearchOption giftSearchOption = new GiftSearchOption();
        giftSearchOption.setGift_return_id(gift_return_id);
        return giftReturnExtendMapper.giftReturnCommodities(giftSearchOption);
    }

    public List<GiftReturnCommodityExtend> giftReturnCommodities(GiftSearchOption giftSearchOption) {
        return giftReturnExtendMapper.giftReturnCommodities(giftSearchOption);
    }
    public Integer countGiftReturnCommodities(GiftSearchOption giftSearchOption) {
        return giftReturnExtendMapper.countGiftReturnCommodities(giftSearchOption);
    }

    public void updateGiftReturn(GiftReturn giftReturn) {
        giftReturnMapper.updateByPrimaryKey(giftReturn);
    }

    @Transactional
    public void giftReturnInventoryIn(int gift_return_id, int commodity_store_id, Employee employee) {
        GiftReturnExtend giftReturnExtend = giftReturnExtend(gift_return_id);
        inventoryInService.createInventoryIn(giftReturnExtend, employee);
        InventorySearchOption inventorySearchOption = new InventorySearchOption();
        inventorySearchOption.setGift_return_id(gift_return_id);
        List<Integer> list = inventoryInService.inventoryInIds(inventorySearchOption);
        inventorySearchOption.setInventoryInIds(list);
        List<InventoryInExtend> inventoryInExtends = inventoryInService.inventoryInList(inventorySearchOption);
        inventoryHistoryService.createGiftReturnInventoryHistory(giftReturnExtend, inventoryInExtends, commodity_store_id, employee);
        giftReturnExtend.setGift_return_status(2);
        giftReturnMapper.updateByPrimaryKey(giftReturnExtend);
        List<GiftReturnCommodityExtend> giftReturnCommodityExtends = giftReturnExtend.getGiftReturnCommodityExtends();
        for (int i = 0; i < giftReturnCommodityExtends.size(); i++) {
            GiftReturnCommodityExtend giftReturnCommodityExtend = giftReturnCommodityExtends.get(i);
            GiftCommodity giftCommodity = giftReturnCommodityExtend.getGiftCommodity();
            giftCommodity.setGift_commodity_return_status(2);
            giftService.updateGiftCommodity(giftCommodity);
        }
        settlementService.giftReturnSettlement(gift_return_id, employee);
    }

    public GiftReturnCommodityExtend getGiftReturnCommodityById(int gift_return_commodity_id) {
        GiftSearchOption giftSearchOption = new GiftSearchOption();
        giftSearchOption.setGift_return_commodity_id(gift_return_commodity_id);
        List<GiftReturnCommodityExtend> giftReturnCommodities = giftReturnExtendMapper.giftReturnCommodities(giftSearchOption);
        if (giftReturnCommodities != null && giftReturnCommodities.size() == 1) {
            return giftReturnCommodities.get(0);
        }
        return null;
    }
}
