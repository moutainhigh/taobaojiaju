package com.xinshan.model.extend.gift;

import com.xinshan.model.Gift;
import com.xinshan.model.GiftReturn;

import java.util.List;

/**
 * Created by mxt on 17-5-26.
 */
public class GiftReturnExtend extends GiftReturn{
    private Gift gift;
    private List<GiftReturnCommodityExtend> giftReturnCommodityExtends;

    public Gift getGift() {
        return gift;
    }

    public void setGift(Gift gift) {
        this.gift = gift;
    }

    public List<GiftReturnCommodityExtend> getGiftReturnCommodityExtends() {
        return giftReturnCommodityExtends;
    }

    public void setGiftReturnCommodityExtends(List<GiftReturnCommodityExtend> giftReturnCommodityExtends) {
        this.giftReturnCommodityExtends = giftReturnCommodityExtends;
    }
}
