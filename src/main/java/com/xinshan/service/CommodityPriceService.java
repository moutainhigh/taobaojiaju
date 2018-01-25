package com.xinshan.service;

import com.xinshan.dao.CommodityPriceRecordMapper;
import com.xinshan.model.CommodityPriceRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mxt on 17-5-9.
 */
@Service
public class CommodityPriceService {
    @Autowired
    public CommodityPriceRecordMapper commodityPriceRecordMapper;

    public void createCommodityPrice(CommodityPriceRecord commodityPriceRecord) {
        commodityPriceRecordMapper.insert(commodityPriceRecord);
    }
}
