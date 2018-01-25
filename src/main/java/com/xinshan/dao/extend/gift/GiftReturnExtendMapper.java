package com.xinshan.dao.extend.gift;

import com.xinshan.model.GiftReturn;
import com.xinshan.model.GiftReturnCommodity;
import com.xinshan.model.extend.gift.GiftReturnCommodityExtend;
import com.xinshan.model.extend.gift.GiftReturnExtend;
import com.xinshan.pojo.gift.GiftSearchOption;

import java.util.List;

/**
 * Created by mxt on 17-5-26.
 */
public interface GiftReturnExtendMapper {

    void createGiftReturn(GiftReturn giftReturn);

    void createGiftReturnCommodity(GiftReturnCommodity giftReturnCommodity);

    List<GiftReturnExtend> giftReturnList(GiftSearchOption giftSearchOption);

    Integer countGiftReturn(GiftSearchOption giftSearchOption);

    List<GiftReturnCommodityExtend> giftReturnCommodities(GiftSearchOption giftSearchOption);

    Integer countGiftReturnCommodities(GiftSearchOption giftSearchOption);
}
