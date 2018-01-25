package com.xinshan.service;

import com.xinshan.dao.extend.activity.GoldEggExtendMapper;
import com.xinshan.model.GoldEgg;
import com.xinshan.model.extend.activity.GoldEggExtend;
import com.xinshan.pojo.activity.GoldEggSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by mxt on 17-4-18.
 */
@Service
public class GoldEggService {
    @Autowired
    private GoldEggExtendMapper goldEggExtendMapper;

    @Transactional
    public void createGoldEgg(GoldEgg goldEgg) {
        goldEggExtendMapper.createGoldEgg(goldEgg);
    }

    public List<GoldEggExtend> goldEggExtends(GoldEggSearchOption goldEggSearchOption) {
        return goldEggExtendMapper.goldEggExtends(goldEggSearchOption);
    }

    public Integer countGoldEgg(GoldEggSearchOption goldEggSearchOption) {
        return goldEggExtendMapper.countGoldEgg(goldEggSearchOption);
    }

    public GoldEggExtend getGoldEggByOrderId(int order_id) {
        GoldEggSearchOption goldEggSearchOption = new GoldEggSearchOption();
        goldEggSearchOption.setOrder_id(order_id);
        List<GoldEggExtend> list = goldEggExtends(goldEggSearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }
}

