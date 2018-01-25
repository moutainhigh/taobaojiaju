package com.xinshan.dao.extend.purchase;

import com.xinshan.model.Purchase;
import com.xinshan.model.PurchaseCommodity;
import com.xinshan.model.PurchaseCommodityArrival;
import com.xinshan.model.extend.purchase.PurchaseExtend;
import com.xinshan.model.extend.purchase.PurchaseInCommodity;
import com.xinshan.model.extend.purchase.PurchaseReports;
import com.xinshan.pojo.purchase.PurchaseSearchOption;

import java.util.List;
import java.util.Map;

/**
 * Created by mxt on 16-11-2.
 */
public interface PurchaseExtendMapper {
    void createPurchase(Purchase purchase);

    void createPurchaseCommodity(PurchaseCommodity purchaseCommodity);

    String purchaseCode(String dateStr);

    List<Integer> purchaseIds(PurchaseSearchOption purchaseSearchOption);

    Integer countPurchase(PurchaseSearchOption purchaseSearchOption);

    List<PurchaseExtend> purchaseList(PurchaseSearchOption purchaseSearchOption);

    void purchaseStatus(Purchase purchase);

    void createPurchaseCommodityArrival(PurchaseCommodityArrival purchaseCommodityArrival);

    PurchaseCommodity getPurchaseCommodityByOrderCommodityId(int order_commodity_id);

    PurchaseCommodity getPurchaseCommodityByGiftCommodityId(int gift_commodity_id);

    List<PurchaseReports> purchaseReportses(PurchaseSearchOption purchaseSearchOption);

    Integer countPurchaseReports(PurchaseSearchOption purchaseSearchOption);

    Map purchaseReportsStatistics(PurchaseSearchOption purchaseSearchOption);

    List<PurchaseInCommodity> purchaseInventoryIn(int purchase_commodity_id);
}
