package com.xinshan.components.purchase;

import com.xinshan.model.PurchaseCommodity;
import com.xinshan.model.extend.purchase.PurchaseCommodityExtend;
import com.xinshan.model.extend.purchase.PurchaseExtend;
import com.xinshan.pojo.purchase.PurchaseSearchOption;
import com.xinshan.service.PurchaseService;
import com.xinshan.utils.constant.order.OrderConstants;
import com.xinshan.utils.constant.purchase.PurchaseConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by mxt on 17-4-24.
 */
@Component
public class PurchaseComponent {
    private static PurchaseService purchaseService;

    @Autowired
    public void setPurchaseService(PurchaseService purchaseService) {
        PurchaseComponent.purchaseService = purchaseService;
    }

    public static int purchaseStatus(int order_id) {
        PurchaseSearchOption purchaseSearchOption = new PurchaseSearchOption();
        purchaseSearchOption.setOrder_id(order_id);
        List<Integer> purchaseIds = purchaseService.purchaseIds(purchaseSearchOption);
        if (purchaseIds == null || purchaseIds.size() == 0) {
            return 0;
        }
        purchaseSearchOption.setPurchaseIds(purchaseIds);
        List<PurchaseExtend> purchaseExtends = purchaseService.purchaseList(purchaseSearchOption);
        int status = 0;
        int allArrival = 0, partArrival = 0, confirmPurchase = 0;
        for (int i = 0; i < purchaseExtends.size(); i++) {
            PurchaseExtend purchaseExtend = purchaseExtends.get(i);
            int purchaseStatus = purchaseStatus(purchaseExtend);
            if (purchaseStatus == 0) {//0未确认采购

            }else if (purchaseStatus == 1) {//1已确认采购
                confirmPurchase++;
            }else if (purchaseStatus == 2) {//2部分到货
                partArrival++;
            }else if (purchaseStatus == 3) {//3全部到货
                allArrival++;
            }
        }
        if (allArrival == 0 && partArrival == 0 && confirmPurchase == 0) {
            status = 0;
        }
        if (confirmPurchase >= purchaseExtends.size()) {
            status = 1;
        }
        if (allArrival >= purchaseExtends.size()) {
            status = 3;
        }else if (allArrival > 0 || partArrival > 0){
            status = 2;
        }
        /*if (purchaseStatus == 0) {//0未确认采购
            status = 0;break;
        }else if (purchaseStatus == 1) {//1已确认采购
            status = 1;break;
        }else if (purchaseStatus == 2) {//2部分到货
            status = 2; break;
        }else if (purchaseStatus == 3) {//3全部到货
            status = 3;
        }*/
        return status;
    }

    /**
     * 采购单状态，
     * 0未确认采购
     * 1已确认采购
     * 2部分到货
     * 3全部到货
     * @param purchaseExtend
     * @return
     */
    private static int purchaseStatus(PurchaseExtend purchaseExtend) {
        List<PurchaseCommodityExtend> purchaseCommodities = purchaseExtend.getPurchaseCommodities();
        int purchase_status = 0;
        int allArrival = 0;
        int partArrival = 0;
        for (int j = 0; j < purchaseCommodities.size(); j++) {
            PurchaseCommodityExtend purchaseCommodityExtend = purchaseCommodities.get(j);
            if (purchaseCommodityExtend.getPurchase_commodity_type() != PurchaseConstant.PURCHASE_COMMODITY_TYPE_ORDER) {
                continue;
            }
            //到货状态，0新添加，1确认采购，2审核，3部分到货，4全部到货
            Integer purchase_commodity_status = purchaseCommodityExtend.getPurchase_commodity_status();
            if (purchase_commodity_status == 2) {

            }else if (purchase_commodity_status == 3) {
                partArrival++;
                break;
            }else if (purchase_commodity_status == 4) {
                allArrival++;
            }
        }

        if (allArrival >= purchaseCommodities.size()) {//全部都是全部到货，全部到货
            purchase_status = 3;
        }else {
            if (partArrival > 0 || allArrival > 0) {//部分到货
                purchase_status = 2;
            }else {
                purchase_status = 1;
            }
        }
        return purchase_status;
    }

    /**
     *
     * @param purchaseCommodity
     * @return
     */
    public static int orderCommodityStatus(PurchaseCommodity purchaseCommodity) {
        int order_commodity_status = 0;
        if (purchaseCommodity.getPurchase_commodity_status() == 1) {//采购
            order_commodity_status = OrderConstants.ORDER_COMMODITY_STATUS_PURCHASE_CONFIRM;//确认采购
        }else if (purchaseCommodity.getPurchase_commodity_status() == 2) {//审核完成，开始采购
            order_commodity_status = OrderConstants.ORDER_COMMODITY_STATUS_PURCHASE_CHECK;//采购审核完成，开始采购
        }else if (purchaseCommodity.getPurchase_commodity_status() == 3) {//部分到货
            if (purchaseCommodity.getInventory_in_num()!= null && purchaseCommodity.getInventory_in_num() > 0) {
                //部分到货，部分入库
                order_commodity_status = OrderConstants.ORDER_COMMODITY_STATUS_PART_IN;
            }else {//部分到货，未入库
                order_commodity_status = OrderConstants.ORDER_COMMODITY_STATUS_PART_ARRIVAL;
            }
        }else if (purchaseCommodity.getPurchase_commodity_status() == 4) {//全部到货
            Integer inventory_in_num = purchaseCommodity.getInventory_in_num();
            if (inventory_in_num != null) {//入库数量不空
                if (inventory_in_num > 0 && inventory_in_num < purchaseCommodity.getPurchase_num()) {
                    //入库数量 > 0，并且小于采购数量，部分入库
                    order_commodity_status = OrderConstants.ORDER_COMMODITY_STATUS_PART_IN;
                }else if (inventory_in_num.equals(purchaseCommodity.getPurchase_num())){
                    //入库数量 = 采购数量 ，全部入库
                    order_commodity_status = OrderConstants.ORDER_COMMODITY_STATUS_ALL_IN;//4全部到货
                }else {
                    order_commodity_status = OrderConstants.ORDER_COMMODITY_STATUS_ALL_ARRIVAL;
                }
            }
            //状态0添加订单，100生成采购单，200确认采购，300采购审核,350部分到货,400全部到货（等待出库）,450不采购（等待出库）,500部分出库,550完全出库
        }else if (purchaseCommodity.getPurchase_commodity_status() == 5) {
            order_commodity_status = OrderConstants.ORDER_COMMODITY_STATUS_NO_PURCHASE;
        }
        return order_commodity_status;
    }
}
