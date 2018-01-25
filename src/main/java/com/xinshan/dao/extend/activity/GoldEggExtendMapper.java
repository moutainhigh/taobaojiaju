package com.xinshan.dao.extend.activity;

import com.xinshan.model.ActivityGoldEgg;
import com.xinshan.model.GoldEgg;
import com.xinshan.model.extend.activity.GoldEggExtend;
import com.xinshan.pojo.activity.GoldEggSearchOption;

import java.util.List;

/**
 * Created by mxt on 17-4-18.
 */
public interface GoldEggExtendMapper {

    void createGoldEgg(GoldEgg goldEgg);

    List<GoldEggExtend> goldEggExtends(GoldEggSearchOption goldEggSearchOption);

    Integer countGoldEgg(GoldEggSearchOption goldEggSearchOption);
}
