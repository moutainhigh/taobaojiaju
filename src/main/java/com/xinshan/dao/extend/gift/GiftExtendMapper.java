package com.xinshan.dao.extend.gift;

import com.xinshan.model.Gift;
import com.xinshan.model.GiftCommodity;
import com.xinshan.model.extend.gift.GiftCommodityExtend;
import com.xinshan.model.extend.gift.GiftExtend;
import com.xinshan.pojo.gift.GiftSearchOption;

import java.util.List;

/**
 * Created by mxt on 17-4-15.
 */
public interface GiftExtendMapper {

    void createGift(Gift gift);

    void createGiftCommodity(GiftCommodity giftCommodity);

    List<Integer> giftIds(GiftSearchOption giftSearchOption);

    List<GiftExtend> giftList(GiftSearchOption giftSearchOption);

    Integer countGift(GiftSearchOption giftSearchOption);

    List<GiftCommodityExtend> giftCommodities(GiftSearchOption giftSearchOption);

    Integer countGiftCommodities(GiftSearchOption giftSearchOption);

    String todayGiftNum(String s);
}
