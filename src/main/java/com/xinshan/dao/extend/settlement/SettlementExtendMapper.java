package com.xinshan.dao.extend.settlement;

import com.xinshan.model.Settlement;
import com.xinshan.model.SettlementCommodityPurchase;
import com.xinshan.model.SettlementHistory;
import com.xinshan.model.extend.orderFee.OrderFeeExtend;
import com.xinshan.model.extend.settlement.SettlementExtend;
import com.xinshan.model.extend.settlement.SettlementInventoryOutCommodity;
import com.xinshan.pojo.settlement.SettlementSearchOption;

import java.util.List;
import java.util.Map;

/**
 * Created by mxt on 16-11-23.
 */
public interface SettlementExtendMapper {

    void createSettlementCommodityPurchase(SettlementCommodityPurchase settlementCommodityPurchase);
    List<SettlementExtend> settlementList(SettlementSearchOption settlementSearchOption);
    Integer countSettlement(SettlementSearchOption settlementSearchOption);

    Map settlementStatistics(SettlementSearchOption settlementSearchOption);

    List<SettlementInventoryOutCommodity> settlementCommodities(SettlementSearchOption settlementSearchOption);
    List<SettlementInventoryOutCommodity> settlementCommodities1(SettlementSearchOption settlementSearchOption);
    List<SettlementInventoryOutCommodity> giftOutSettlementCommodities(SettlementSearchOption settlementSearchOption);
    List<OrderFeeExtend> orderFees(SettlementSearchOption settlementSearchOption);

    void createSettlement(Settlement settlement);

    void createSettlementHistory(SettlementHistory settlementHistory);

    List<SettlementHistory> settlementHistories(int settlement_id);

    void updateSettlementCheckingStatus(Settlement settlement);
    void updateSettlementVerifyStatus(Settlement settlement);

    SettlementCommodityPurchase getSettlementCommodityPurchase(int settlement_id, int obj_id);

}
