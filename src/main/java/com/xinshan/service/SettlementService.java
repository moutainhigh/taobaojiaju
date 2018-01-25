package com.xinshan.service;

import com.xinshan.components.commodity.CommodityComponent;
import com.xinshan.components.orderFee.OrderFeeComponents;
import com.xinshan.dao.SettlementCommodityPurchaseMapper;
import com.xinshan.dao.SettlementMapper;
import com.xinshan.dao.extend.settlement.SettlementExtendMapper;
import com.xinshan.model.*;
import com.xinshan.model.extend.afterSales.AfterSalesCommodityExtend;
import com.xinshan.model.extend.afterSales.AfterSalesExtend;
import com.xinshan.model.extend.afterSales.SampleFixExtend;
import com.xinshan.model.extend.commodity.CommodityExtend;
import com.xinshan.model.extend.gift.GiftCommodityExtend;
import com.xinshan.model.extend.gift.GiftExtend;
import com.xinshan.model.extend.gift.GiftReturnCommodityExtend;
import com.xinshan.model.extend.gift.GiftReturnExtend;
import com.xinshan.model.extend.inventory.InventoryHistoryDetailExtend;
import com.xinshan.model.extend.inventory.InventoryHistoryExtend;
import com.xinshan.model.extend.order.OrderReturnCommodityExtend;
import com.xinshan.model.extend.order.OrderReturnExtend;
import com.xinshan.model.extend.orderFee.OrderFeeExtend;
import com.xinshan.model.extend.purchase.PurchaseCommodityExtend;
import com.xinshan.model.extend.settlement.SettlementExtend;
import com.xinshan.model.extend.settlement.SettlementInventoryOutCommodity;
import com.xinshan.pojo.gift.GiftSearchOption;
import com.xinshan.pojo.inventory.InventoryHistorySearchOption;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.Request2ModelUtils;
import com.xinshan.utils.checking.CheckingUtil;
import com.xinshan.pojo.settlement.SettlementSearchOption;
import com.xinshan.utils.constant.inventory.InventoryConstant;
import com.xinshan.utils.constant.inventory.InventoryHistoryConstant;
import com.xinshan.utils.constant.inventory.LogisticsConstants;
import com.xinshan.utils.constant.order.OrderConstants;
import com.xinshan.utils.constant.settlement.SettlementConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by mxt on 16-11-23.
 */
@Service
public class SettlementService {
    @Autowired
    private SettlementExtendMapper settlementExtendMapper;
    @Autowired
    private SettlementMapper settlementMapper;
    @Autowired
    private CommodityService commodityService;
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private AfterSalesService afterSalesService;
    @Autowired
    private SampleFixService sampleFixService;
    @Autowired
    private OrderReturnService orderReturnService;
    @Autowired
    private SettlementCommodityPurchaseMapper settlementCommodityPurchaseMapper;
    @Autowired
    private InventoryHistoryService inventoryHistoryService;
    @Autowired
    private GiftService giftService;
    @Autowired
    private GiftReturnService giftReturnService;
    @Autowired
    private OrderService orderService;

    @Transactional
    public void createSettlementCommodityPurchase(SettlementCommodityPurchase settlementCommodityPurchase, Employee employee) {
        SettlementCommodityPurchase settlementCommodityPurchase1 = settlementExtendMapper.getSettlementCommodityPurchase(settlementCommodityPurchase.getSettlement_id(), settlementCommodityPurchase.getObj_id());
        if (settlementCommodityPurchase1 != null) {
            settlementCommodityPurchase1.setSettlement_commodity_purchase_employee_name(employee.getEmployee_name());
            settlementCommodityPurchase1.setSettlement_commodity_purchase_employee_code(employee.getEmployee_code());
            settlementCommodityPurchase1.setPurchase_price(settlementCommodityPurchase.getPurchase_price());
            settlementCommodityPurchaseMapper.updateByPrimaryKey(settlementCommodityPurchase1);
        }else {
            settlementCommodityPurchase.setSettlement_commodity_purchase_employee_code(employee.getEmployee_code());
            settlementCommodityPurchase.setSettlement_commodity_purchase_employee_name(employee.getEmployee_name());
            settlementExtendMapper.createSettlementCommodityPurchase(settlementCommodityPurchase);
        }
    }

    private void createSettlementCommodityPurchase(List<SettlementCommodityPurchase> list, Employee employee, int settlement_id) {
        for (int i = 0; i < list.size(); i++) {
            SettlementCommodityPurchase settlementCommodityPurchase = list.get(i);
            settlementCommodityPurchase.setSettlement_commodity_purchase_employee_code(employee.getEmployee_code());
            settlementCommodityPurchase.setSettlement_commodity_purchase_employee_name(employee.getEmployee_name());
            settlementCommodityPurchase.setSettlement_id(settlement_id);
            settlementExtendMapper.createSettlementCommodityPurchase(settlementCommodityPurchase);
        }
    }

    public SettlementExtend getSettlementById(int settlement_id){
        SettlementSearchOption settlementSearchOption = new SettlementSearchOption();
        settlementSearchOption.setSettlement_id(settlement_id);
        List<SettlementExtend> list = settlementList(settlementSearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public List<SettlementExtend> settlementList(SettlementSearchOption settlementSearchOption) {
        List<SettlementExtend> list = settlementExtendMapper.settlementList(settlementSearchOption);
        for (int i = 0; i < list.size(); i++) {
            SettlementSearchOption sso = new SettlementSearchOption();
            SettlementExtend settlement = list.get(i);
            sso.setSupplier_id(settlement.getSupplier_id());
            Integer obj_id_type = settlement.getObj_id_type();
            Integer supplier_id = settlement.getSupplier_id();

            //出库结算信息
            Integer inventory_history_id = settlement.getInventory_history_id();
            if (obj_id_type != null && obj_id_type == SettlementConstant.OBJ_ID_TYPE_INVENTORY_OUT) {
                inventory_history_id = settlement.getObj_id();
            }
            if (inventory_history_id != null) {
                sso.setInventory_history_id(inventory_history_id);
                List<SettlementInventoryOutCommodity> settlementCommodities = settlementExtendMapper.settlementCommodities(sso);
                List<SettlementInventoryOutCommodity> list1 = new ArrayList<>();
                settlementCommodities(settlement, settlementCommodities, list1);
                settlement.setSettlementCommodities(list1);
                settlement.setOrderFees(OrderFeeComponents.inventoryOutOrderFeeList(supplier_id, inventory_history_id));
            }

            //售后运费等各项费用结算
            Integer after_sales_id = settlement.getAfter_sales_id();
            if (obj_id_type != null && obj_id_type == SettlementConstant.OBJ_ID_TYPE_AFTER_SALES) {
                after_sales_id = settlement.getObj_id();
            }
            if (after_sales_id != null) {
                sso.setAfter_sales_id(after_sales_id);
                settlement.setAfterSalesCommodities(afterSalesService.afterSalesCommodities(after_sales_id, settlement.getSupplier_id()));
                //售后退货商品
                settlement.setOrderFees(OrderFeeComponents.afterSalesOrderFeeList(supplier_id, after_sales_id));
            }

            //场地维修费用结算
            if (obj_id_type != null && obj_id_type == SettlementConstant.OBJ_ID_TYPE_SAMPLE_FIX) {
                int sample_fix_id = settlement.getObj_id();
                SampleFixExtend sampleFixExtend = sampleFixService.getSampleFixById(sample_fix_id);
                settlement.setOrderFees(OrderFeeComponents.orderFeeList(sampleFixExtend.getOrder_fee_ids()));
                settlement.setCommoditySampleFix(sampleFixExtend);
            }

            //售后退货结算
            if (obj_id_type != null && obj_id_type == SettlementConstant.OBJ_ID_TYPE_RETURN_COMMODITY) {
                int order_return_id = settlement.getObj_id();
                OrderReturnExtend orderReturnExtend = orderReturnService.getOrderReturnById(order_return_id);
                settlement.setOrderFees(OrderFeeComponents.orderFeeList(orderReturnExtend.getOrder_fee_ids(), settlement.getSupplier_id()));
                List<OrderReturnCommodityExtend> orderReturnCommodityExtends = orderReturnService.orderReturnCommodities(order_return_id, settlement.getSupplier_id());
                for (int j = 0; j < orderReturnCommodityExtends.size(); j++) {
                    OrderReturnCommodityExtend orderReturnCommodityExtend = orderReturnCommodityExtends.get(j);
                    SettlementCommodityPurchase settlementCommodityPurchase = settlementExtendMapper.getSettlementCommodityPurchase(settlement.getSettlement_id(),
                                    orderReturnCommodityExtend.getOrder_return_commodity_id());
                    orderReturnCommodityExtend.setSettlementCommodityPurchase(settlementCommodityPurchase);
                }
                settlement.setOrderReturnCommodities(orderReturnCommodityExtends);
            }

            //赠品结算
            if (obj_id_type != null && obj_id_type == SettlementConstant.OBJ_ID_TYPE_GIFT) {
                int gift_id = settlement.getObj_id();
                List<GiftCommodityExtend> giftCommodityExtendList = giftService.getGiftCommodityByGiftId(gift_id);
                for (int j = 0; j < giftCommodityExtendList.size(); j++) {
                    GiftCommodityExtend giftCommodityExtend = giftCommodityExtendList.get(j);
                    SettlementCommodityPurchase settlementCommodityPurchase = settlementExtendMapper.
                            getSettlementCommodityPurchase(settlement.getSettlement_id(),
                                    giftCommodityExtend.getGift_commodity_id());
                    giftCommodityExtend.setSettlementCommodityPurchase(settlementCommodityPurchase);
                }
                settlement.setGift(giftService.getGiftById(gift_id));
                settlement.setGiftCommodityExtendList(giftCommodityExtendList);
            }

            //退赠品结算
            if (obj_id_type != null && obj_id_type == SettlementConstant.OBJ_ID_TYPE_GIFT_RETURN) {
                int gift_return_id = settlement.getObj_id();
                List<GiftReturnCommodityExtend> giftReturnCommodities = giftReturnService.giftReturnCommodities(gift_return_id);
                for (int j = 0; j < giftReturnCommodities.size(); j++) {
                    GiftReturnCommodityExtend giftReturnCommodityExtend = giftReturnCommodities.get(j);
                    SettlementCommodityPurchase settlementCommodityPurchase = settlementExtendMapper.
                            getSettlementCommodityPurchase(settlement.getSettlement_id(),
                                    giftReturnCommodityExtend.getGift_return_commodity_id());
                    giftReturnCommodityExtend.setSettlementCommodityPurchase(settlementCommodityPurchase);
                }
                settlement.setGiftReturnCommodities(giftReturnCommodities);
            }
            //广东馆入库结算
            if (obj_id_type != null && obj_id_type == SettlementConstant.OBJ_ID_TYPE_INVENTORY_IN) {
                sso.setInventory_history_id(inventory_history_id);
                sso.setGuangdong(1);
                List<SettlementInventoryOutCommodity> settlementCommodities = settlementExtendMapper.settlementCommodities1(sso);
                for (int j = 0; j < settlementCommodities.size(); j++) {
                    SettlementInventoryOutCommodity settlementInventoryOutCommodity = settlementCommodities.get(j);
                    SettlementCommodityPurchase settlementCommodityPurchase = settlementExtendMapper.getSettlementCommodityPurchase(settlement.getSettlement_id(), settlementInventoryOutCommodity.getInventory_history_detail_id());
                    settlementInventoryOutCommodity.setSettlementCommodityPurchase(settlementCommodityPurchase);
                }
                settlement.setSettlementCommodities(settlementCommodities);
            }
            //赠品出库
            if (obj_id_type != null && obj_id_type == SettlementConstant.OBJ_ID_TYPE_GIFT_OUT) {
                sso.setInventory_history_id(inventory_history_id);
                List<SettlementInventoryOutCommodity> settlementCommodities = settlementExtendMapper.giftOutSettlementCommodities(sso);
                List<SettlementInventoryOutCommodity> list1 = new ArrayList<>();
                settlementCommodities(settlement, settlementCommodities, list1);
                settlement.setSettlementCommodities(list1);
                settlement.setOrderFees(OrderFeeComponents.inventoryOutOrderFeeList(supplier_id, inventory_history_id));
                InventoryHistory inventoryHistory = settlement.getInventoryHistory();
                if (inventoryHistory != null && inventoryHistory.getGift_id() != null) {
                    settlement.setGift(giftService.getGiftById(inventoryHistory.getGift_id()));
                }
            }
            //礼品退还供应商
            if (obj_id_type != null && obj_id_type == SettlementConstant.SETTLEMENT_TYPE_GIFT_RETURN_COMMODITY) {
                GiftReturnCommodityExtend giftReturnCommodity = giftReturnService.getGiftReturnCommodityById(settlement.getObj_id());
                SettlementCommodityPurchase settlementCommodityPurchase = settlementExtendMapper.getSettlementCommodityPurchase(settlement.getSettlement_id(), settlement.getObj_id());
                giftReturnCommodity.setSettlementCommodityPurchase(settlementCommodityPurchase);
                settlement.setGiftReturnCommodity(giftReturnCommodity);
            }
            //售前退货
            if (obj_id_type != null && obj_id_type == SettlementConstant.SETTLEMENT_TYPE_PRE_SALE_RETURN) {
                OrderReturnExtend orderReturnExtend = orderReturnService.getOrderReturnById(settlement.getObj_id());
                List<OrderFeeExtend> orderFees = orderReturnExtend.getOrderFees();
                List<OrderFeeExtend> arrayList = new ArrayList<>();
                for (int j = 0; j < orderFees.size(); j++) {
                    OrderFeeExtend orderFeeExtend = orderFees.get(j);
                    if (orderFeeExtend.getOrder_fee_enable() == 1 && orderFeeExtend.getSupplier_id().equals(supplier_id)) {
                        arrayList.add(orderFeeExtend);
                    }
                }
                settlement.setOrderFees(arrayList);
            }
            //广东馆到货结算
            if (obj_id_type != null && obj_id_type == SettlementConstant.SETTLEMENT_TYPE_PURCHASE_ARRIVAL) {
                PurchaseCommodity purchaseCommodity = purchaseService.getPurchaseCommodityById(settlement.getObj_id());
                PurchaseCommodityExtend purchaseCommodityExtend = new PurchaseCommodityExtend();
                Request2ModelUtils.init(purchaseCommodity, purchaseCommodityExtend);
                SettlementCommodityPurchase settlementCommodityPurchase = settlementExtendMapper.getSettlementCommodityPurchase(settlement.getSettlement_id(), settlement.getObj_id());
                purchaseCommodityExtend.setSettlementCommodityPurchase(settlementCommodityPurchase);
                purchaseCommodityExtend.setCommodity(CommodityComponent.getCommodityById(purchaseCommodityExtend.getCommodity_id()));
                if (purchaseCommodityExtend.getOrder_commodity_id() != null) {
                    purchaseCommodityExtend.setOrderCommodity(orderService.getOrderCommodity(purchaseCommodityExtend.getOrder_commodity_id()));
                }
                settlement.setPurchaseCommodity(purchaseCommodityExtend);
            }

            settlement.setSettlementHistories(settlementExtendMapper.settlementHistories(settlement.getSettlement_id()));
        }
        return list;
    }

    private void settlementCommodities(SettlementExtend settlement, List<SettlementInventoryOutCommodity> settlementCommodities, List<SettlementInventoryOutCommodity> list1) {
        for (int j = 0; j < settlementCommodities.size(); j++) {
            SettlementInventoryOutCommodity settlementInventoryOutCommodity = settlementCommodities.get(j);
            Commodity commodity = settlementInventoryOutCommodity.getCommodity();
            if ((commodity.getGuangdong() != null && commodity.getGuangdong() == 0)
                    || (commodity.getGuangdong() != null && commodity.getGuangdong() == 1 && settlementInventoryOutCommodity.getSample() == 1)){
                SettlementCommodityPurchase settlementCommodityPurchase = settlementExtendMapper.getSettlementCommodityPurchase(settlement.getSettlement_id(), settlementInventoryOutCommodity.getInventory_history_detail_id());
                settlementInventoryOutCommodity.setSettlementCommodityPurchase(settlementCommodityPurchase);
                list1.add(settlementInventoryOutCommodity);
            }
        }
    }

    public void updateSettlementCheckingStatus(Settlement settlement) {
        settlementExtendMapper.updateSettlementCheckingStatus(settlement);
    }
    @Transactional
    public void updateSettlementVerifyStatus(Settlement settlement) {
        settlementExtendMapper.updateSettlementVerifyStatus(settlement);
    }

    public Map settlementStatistics(SettlementSearchOption settlementSearchOption) {
        return settlementExtendMapper.settlementStatistics(settlementSearchOption);
    }

    public Integer countSettlement(SettlementSearchOption settlementSearchOption) {
        return settlementExtendMapper.countSettlement(settlementSearchOption);
    }

    /**
     * 出库结算
     * @param inventory_history_id
     */
    public void createSettlementInventoryOut(int inventory_history_id, Employee employee) {
        InventoryHistoryExtend inventoryHistory = inventoryHistoryService.getInventoryHistoryById(inventory_history_id);
        if (inventoryHistory.getInventory_history_settlement_status() == 1) {
            return;
        }
        List<InventoryHistoryDetailExtend> list = inventoryHistory.getInventoryHistoryDetails();
        int inventory_type = inventoryHistory.getInventory_type();
        if (inventory_type == InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_OUT) {
            createSettlementInventoryOut(inventoryHistory, list, employee);
        }else if (inventory_type == InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_GIFT_OUT) {
            createSettlementGiftInventoryOut(inventoryHistory, list, employee);
            createGiftSettlement(inventoryHistory.getGift_id(), employee);
        }

        inventoryHistory.setInventory_history_settlement_status(1);
        inventoryHistoryService.updateInventoryHistory(inventoryHistory);
    }

    /**
     * 赠品出库
     * @param inventoryHistory
     * @param list
     */
    private void createSettlementGiftInventoryOut(InventoryHistoryExtend inventoryHistory, List<InventoryHistoryDetailExtend> list, Employee employee) {
        Set<Integer> supplierIds = new HashSet<>();
        for (int i = 0; i < list.size(); i++) {
            InventoryHistoryDetailExtend inventoryHistoryDetail = list.get(i);
            int commodity_id = inventoryHistoryDetail.getCommodity_id();
            Commodity commodity = commodityService.getCommodityById(commodity_id);
            if ((commodity != null && commodity.getSupplier_id() != null
                    && commodity.getGuangdong() != null && commodity.getGuangdong() == 0)//非广东馆商品结算
                    || (commodity.getGuangdong() != null && commodity.getGuangdong() == 1 &&//广东馆商品样品非退货商品结算
                    inventoryHistoryDetail.getSample() == 1)) {
                supplierIds.add(commodity.getSupplier_id());
            }
        }

        Iterator<Integer> iterator = supplierIds.iterator();
        while (iterator.hasNext()) {
            int supplier_id = iterator.next();
            if (getByInventoryHistoryId(supplier_id, inventoryHistory.getInventory_history_id()) == null) {
                Settlement settlement = new Settlement();
                settlement.setSupplier_id(supplier_id);
                settlement.setInventory_history_id(inventoryHistory.getInventory_history_id());
                settlement.setOrder_id(inventoryHistory.getOrder_id());
                settlement.setSettlement_status(0);
                settlement.setChecking_status(0);
                settlement.setVerify_status(0);
                settlement.setSettlement_type(SettlementConstant.SETTLEMENT_TYPE_GIFT_OUT);
                settlement.setObj_id(inventoryHistory.getInventory_history_id());
                settlement.setObj_id_type(SettlementConstant.OBJ_ID_TYPE_GIFT_OUT);
                //结算金额
                List<SettlementCommodityPurchase> settlementCommodityPurchases = new ArrayList<>();
                setSettlementAmount1(settlement, settlementCommodityPurchases);
                settlementExtendMapper.createSettlement(settlement);
                createSettlementCommodityPurchase(settlementCommodityPurchases, employee, settlement.getSettlement_id());
            }
        }
    }

    /**
     * 结算
     * @param employee
     * @param settlement
     * @param settlement_amount
     * @param remark
     */
    @Transactional
    public void createSettlementHistory(Employee employee, Settlement settlement, BigDecimal settlement_amount,String remark) {
        settlement.setSettlement_paid_amount(settlement.getSettlement_paid_amount().add(settlement_amount));
        settlement.setSettlement_need_amount(settlement.getSettlement_amount().subtract(settlement.getSettlement_paid_amount()));
        SettlementHistory settlementHistory = new SettlementHistory();
        settlementHistory.setSettlement_date(DateUtil.currentDate());
        settlementHistory.setSettlement_employee_code(employee.getEmployee_code());
        settlementHistory.setSettlement_employee_name(employee.getEmployee_name());
        settlementHistory.setSettlement_history_amount(settlement_amount);
        settlementHistory.setSettlement_id(settlement.getSettlement_id());
        settlementHistory.setSettlement_remark(remark);
        settlementExtendMapper.createSettlementHistory(settlementHistory);
        if (settlement.getSettlement_need_amount().doubleValue() <= 0) {
            settlement.setSettlement_status(1);//已全部结算
        }else if(settlement.getSettlement_need_amount().doubleValue() > 0){
            settlement.setSettlement_status(2);//未全部结算
        }
        settlementMapper.updateByPrimaryKey(settlement);
    }

    /**
     * 出库结算
     * @param inventoryHistory
     * @param list
     */
    private void createSettlementInventoryOut(InventoryHistoryExtend inventoryHistory, List<InventoryHistoryDetailExtend> list, Employee employee) {
        Set<Integer> supplierIds = new HashSet<>();
        for (int i = 0; i < list.size(); i++) {
            InventoryHistoryDetailExtend inventoryHistoryDetail = list.get(i);
            int commodity_id = inventoryHistoryDetail.getCommodity_id();
            Commodity commodity = commodityService.getCommodityById(commodity_id);
            if ((commodity != null && commodity.getSupplier_id() != null
                    && commodity.getGuangdong() != null && commodity.getGuangdong() == 0)//非广东馆商品结算
                    || (commodity.getGuangdong() != null && commodity.getGuangdong() == 1 &&//广东馆商品样品非退货商品结算
                    inventoryHistoryDetail.getSample() == 1)) {
                supplierIds.add(commodity.getSupplier_id());
            }
        }

        Iterator<Integer> iterator = supplierIds.iterator();
        while (iterator.hasNext()) {
            int supplier_id = iterator.next();
            if (getByInventoryHistoryId(supplier_id, inventoryHistory.getInventory_history_id()) == null) {
                Settlement settlement = new Settlement();
                settlement.setSupplier_id(supplier_id);
                settlement.setInventory_history_id(inventoryHistory.getInventory_history_id());
                settlement.setOrder_id(inventoryHistory.getOrder_id());
                settlement.setSettlement_status(0);
                settlement.setChecking_status(0);
                settlement.setVerify_status(0);
                settlement.setSettlement_type(SettlementConstant.SETTLEMENT_TYPE_INVENTORY_OUT);
                settlement.setObj_id(inventoryHistory.getInventory_history_id());
                settlement.setObj_id_type(SettlementConstant.OBJ_ID_TYPE_INVENTORY_OUT);
                //结算金额
                List<SettlementCommodityPurchase> settlementCommodityPurchases = new ArrayList<>();
                setSettlementAmount1(settlement, settlementCommodityPurchases);
                settlementExtendMapper.createSettlement(settlement);
                for (int i = 0; i < settlementCommodityPurchases.size(); i++) {
                    SettlementCommodityPurchase settlementCommodityPurchase = settlementCommodityPurchases.get(i);
                    settlementCommodityPurchase.setSettlement_commodity_purchase_employee_code(employee.getEmployee_code());
                    settlementCommodityPurchase.setSettlement_commodity_purchase_employee_name(employee.getEmployee_name());
                    settlementCommodityPurchase.setSettlement_id(settlement.getSettlement_id());
                    settlementExtendMapper.createSettlementCommodityPurchase(settlementCommodityPurchase);
                }
            }
        }
    }

    /**
     * 计算结算金额
     * @param settlement
     */
    private void setSettlementAmount1(Settlement settlement,List<SettlementCommodityPurchase> settlementCommodityPurchases) {
        Integer settlement_type = settlement.getSettlement_type();
        switch (settlement_type) {
            case SettlementConstant.SETTLEMENT_TYPE_INVENTORY_OUT:
                inventoryOutSettlementAmount(settlement, settlementCommodityPurchases);
                break;
            case SettlementConstant.SETTLEMENT_TYPE_AFTER_SALES:
                afterSalesSettlementAmount(settlement);
                break;
            case SettlementConstant.SETTLEMENT_TYPE_SAMPLE_FIX:
                sampleFixSettlementAmount(settlement);
                break;
            case SettlementConstant.SETTLEMENT_TYPE_RETURN_COMMODITY:
                returnCommodityAmount(settlement, settlementCommodityPurchases);
                break;
            case SettlementConstant.SETTLEMENT_TYPE_GIFT:
                giftCommodityAmount(settlement, settlementCommodityPurchases);
                break;
            case SettlementConstant.SETTLEMENT_TYPE_GIFT_RETURN:
                giftReturnAmount(settlement, settlementCommodityPurchases);
                break;
            case SettlementConstant.SETTLEMENT_TYPE_INVENTORY_IN:
                inventoryInSettlementAmount(settlement, settlementCommodityPurchases);
                break;
            case SettlementConstant.SETTLEMENT_TYPE_GIFT_OUT:
                inventoryGiftOutSettlementAmount(settlement, settlementCommodityPurchases);
                break;
            case SettlementConstant.SETTLEMENT_TYPE_GIFT_RETURN_COMMODITY:
                giftReturnCommodityAmount(settlement, settlementCommodityPurchases);
                break;
            case SettlementConstant.SETTLEMENT_TYPE_PRE_SALE_RETURN:
                preSaleOrderReturn(settlement);
                break;
            case SettlementConstant.SETTLEMENT_TYPE_PURCHASE_ARRIVAL:
                purchaseArrival(settlement, settlementCommodityPurchases);
                break;
            default:
                break;
        }
    }

    /**
     * 出库结算
     * @param settlement
     */
    private void inventoryOutSettlementAmount(Settlement settlement, List<SettlementCommodityPurchase> settlementCommodityPurchases) {
        BigDecimal after_sales_amount = new BigDecimal("0");//售后费用
        BigDecimal purchase_amount = new BigDecimal("0");//商品采购费用
        BigDecimal return_commodity_all_amount = new BigDecimal("0");//退货退还费用

        Integer inventory_history_id = settlement.getInventory_history_id();
        Integer obj_id_type = settlement.getObj_id_type();
        if (obj_id_type == SettlementConstant.OBJ_ID_TYPE_INVENTORY_OUT) {
            inventory_history_id = settlement.getObj_id();
        }
        SettlementSearchOption settlementSearchOption = new SettlementSearchOption();
        settlementSearchOption.setSupplier_id(settlement.getSupplier_id());
        settlementSearchOption.setInventory_history_id(inventory_history_id);
        List<SettlementInventoryOutCommodity> settlementInventoryOutCommodities = settlementExtendMapper.settlementCommodities(settlementSearchOption);

        for (int i = 0; i < settlementInventoryOutCommodities.size(); i++) {
            SettlementInventoryOutCommodity settlementInventoryOutCommodity = settlementInventoryOutCommodities.get(i);
            Commodity commodity = settlementInventoryOutCommodity.getCommodity();
            if ((commodity.getGuangdong() != null && commodity.getGuangdong() == 0)
                    || (commodity.getGuangdong() != null && commodity.getGuangdong() == 1 && settlementInventoryOutCommodity.getSample() == 1)) {
                purchase_amount = getBigDecimal(settlement, purchase_amount, settlementInventoryOutCommodity, commodity, settlementCommodityPurchases);
            }
        }

        //供应商承担各项费用
        List<OrderFeeExtend> orderFees = OrderFeeComponents.inventoryOutOrderFeeList(settlement.getSupplier_id(), inventory_history_id);
        for (int i = 0; i < orderFees.size(); i++) {
            OrderFeeExtend orderFee = orderFees.get(i);
            if (orderFee.getOrder_fee_enable() == 1) {
                after_sales_amount = after_sales_amount.add(orderFee.getSupplier_fee());
            }
        }

        settlement.setSettlement_return_commodity_amount(return_commodity_all_amount);
        settlement.setSettlement_purchase_amount(purchase_amount);
        settlement.setSettlement_after_sales_amount(after_sales_amount);
        settlementAmount(settlement);
    }

    /**
     * 礼品出库结算
     * @param settlement
     */
    private void inventoryGiftOutSettlementAmount(Settlement settlement, List<SettlementCommodityPurchase> settlementCommodityPurchases) {
        BigDecimal after_sales_amount = new BigDecimal("0");//售后费用
        BigDecimal purchase_amount = new BigDecimal("0");//商品采购费用
        BigDecimal return_commodity_all_amount = new BigDecimal("0");//退货退还费用

        Integer inventory_history_id = settlement.getInventory_history_id();
        Integer obj_id_type = settlement.getObj_id_type();
        if (obj_id_type == SettlementConstant.OBJ_ID_TYPE_GIFT_OUT) {
            inventory_history_id = settlement.getObj_id();
        }
        SettlementSearchOption settlementSearchOption = new SettlementSearchOption();
        settlementSearchOption.setSupplier_id(settlement.getSupplier_id());
        settlementSearchOption.setInventory_history_id(inventory_history_id);
        List<SettlementInventoryOutCommodity> settlementInventoryOutCommodities = settlementExtendMapper.giftOutSettlementCommodities(settlementSearchOption);
        for (int i = 0; i < settlementInventoryOutCommodities.size(); i++) {
            SettlementInventoryOutCommodity settlementInventoryOutCommodity = settlementInventoryOutCommodities.get(i);
            purchase_amount = purchaseAmount(settlement, purchase_amount, settlementInventoryOutCommodity, settlementCommodityPurchases);
        }

        //供应商承担各项费用
        List<OrderFeeExtend> orderFees = OrderFeeComponents.inventoryOutOrderFeeList(settlement.getSupplier_id(), inventory_history_id);
        for (int i = 0; i < orderFees.size(); i++) {
            OrderFeeExtend orderFee = orderFees.get(i);
            if (orderFee.getOrder_fee_enable() == 1) {
                after_sales_amount = after_sales_amount.add(orderFee.getSupplier_fee());
            }
        }

        settlement.setSettlement_return_commodity_amount(return_commodity_all_amount);
        settlement.setSettlement_purchase_amount(purchase_amount);
        settlement.setSettlement_after_sales_amount(after_sales_amount);
        settlementAmount(settlement);
    }

    private BigDecimal purchaseAmount(Settlement settlement, BigDecimal purchase_amount,
                                      SettlementInventoryOutCommodity settlementInventoryOutCommodity, List<SettlementCommodityPurchase> settlementCommodityPurchases) {

        Commodity commodity = settlementInventoryOutCommodity.getCommodity();
        if ((commodity.getGuangdong() != null && commodity.getGuangdong() == 0)
                || (commodity.getGuangdong() != null && commodity.getGuangdong() == 1
                && settlementInventoryOutCommodity.getSample() == 1)) {
            purchase_amount = getBigDecimal(settlement, purchase_amount, settlementInventoryOutCommodity, commodity, settlementCommodityPurchases);
        }
        return purchase_amount;
    }

    private BigDecimal getBigDecimal(Settlement settlement, BigDecimal purchase_amount,
                                     SettlementInventoryOutCommodity settlementInventoryOutCommodity, Commodity commodity, List<SettlementCommodityPurchase> list) {
        int num = 0;
        boolean supplier_out = CheckingUtil.getCheckingUtil().supplierOut(settlementInventoryOutCommodity);
        if (supplier_out) {//供应商发货，出货数量未订单商品数量
            num = settlementInventoryOutCommodity.getOrderCommodity().getCommodity_num();
        }else {//正常出库，出货数量为出库数量
            num = settlementInventoryOutCommodity.getInventory_history_num();
        }

        BigDecimal purchase_total_price = null;
        Integer settlement_id = settlement.getSettlement_id();
        Integer inventory_history_detail_id = settlementInventoryOutCommodity.getInventory_history_detail_id();
        if (settlement_id != null) {//重新计算结算
            SettlementCommodityPurchase settlementCommodityPurchase = settlementExtendMapper.getSettlementCommodityPurchase(settlement_id, inventory_history_detail_id);
            if (settlementCommodityPurchase != null) {//自己编辑添加采购价
                purchase_total_price = settlementCommodityPurchase.getPurchase_price().multiply(new BigDecimal(num));
                purchase_amount = purchase_amount.add(purchase_total_price);
            }
        }
        if (purchase_total_price == null) {
            PurchaseCommodity pc = settlementInventoryOutCommodity.getPurchaseCommodity();
            BigDecimal purchase_unit_cost_price = pc.getPurchase_unit_cost_price();
            if (purchase_unit_cost_price != null) {
                purchase_total_price = new BigDecimal(num).multiply(purchase_unit_cost_price);//采购金额=出库数量*采购价格
                purchase_amount = purchase_amount.add(purchase_total_price);
            }else {
                purchase_total_price = new BigDecimal(num).multiply(commodity.getPurchase_price());//采购金额=出库数量*采购价格
                purchase_amount = purchase_amount.add(purchase_total_price);
                if (settlement_id == null) {
                    SettlementCommodityPurchase settlementCommodityPurchase = new SettlementCommodityPurchase();
                    settlementCommodityPurchase.setObj_id(inventory_history_detail_id);
                    settlementCommodityPurchase.setPurchase_price(commodity.getPurchase_price());
                    list.add(settlementCommodityPurchase);
                }
            }
        }
        return purchase_amount;
    }

    /**
     * 入库结算
     * @param settlement
     */
    private void inventoryInSettlementAmount(Settlement settlement, List<SettlementCommodityPurchase> settlementCommodityPurchases) {
        BigDecimal after_sales_amount = new BigDecimal("0");//售后费用
        BigDecimal purchase_amount = new BigDecimal("0");//商品采购费用
        BigDecimal return_commodity_all_amount = new BigDecimal("0");//退货退还费用

        Integer inventory_history_id = settlement.getInventory_history_id();
        Integer obj_id_type = settlement.getObj_id_type();
        if (obj_id_type == SettlementConstant.OBJ_ID_TYPE_INVENTORY_IN) {
            inventory_history_id = settlement.getObj_id();
        }
        SettlementSearchOption settlementSearchOption = new SettlementSearchOption();
        settlementSearchOption.setSupplier_id(settlement.getSupplier_id());
        settlementSearchOption.setInventory_history_id(inventory_history_id);
        settlementSearchOption.setGuangdong(1);
        List<SettlementInventoryOutCommodity> settlementInventoryOutCommodities = settlementExtendMapper.settlementCommodities1(settlementSearchOption);

        for (int i = 0; i < settlementInventoryOutCommodities.size(); i++) {
            SettlementInventoryOutCommodity settlementInventoryOutCommodity = settlementInventoryOutCommodities.get(i);
            Commodity commodity = settlementInventoryOutCommodity.getCommodity();
            if (commodity.getGuangdong() != null && commodity.getGuangdong() == 1 && settlementInventoryOutCommodity.getSample() == 0) {
                //广东馆商品结算
                purchase_amount = getBigDecimal(settlement, purchase_amount, settlementInventoryOutCommodity, commodity, settlementCommodityPurchases);
            }
        }

        //供应商承担各项费用
        List<OrderFeeExtend> orderFees = OrderFeeComponents.inventoryOutOrderFeeList(settlement.getSupplier_id(), inventory_history_id);
        for (int i = 0; i < orderFees.size(); i++) {
            OrderFeeExtend orderFee = orderFees.get(i);
            if (orderFee.getOrder_fee_enable() == 1) {
                after_sales_amount = after_sales_amount.add(orderFee.getSupplier_fee());
            }
        }

        settlement.setSettlement_return_commodity_amount(return_commodity_all_amount);
        settlement.setSettlement_purchase_amount(purchase_amount);
        settlement.setSettlement_after_sales_amount(after_sales_amount);
        settlementAmount(settlement);
    }

    /**
     * 售后结算金额计算
     * @param settlement
     */
    private void afterSalesSettlementAmount(Settlement settlement) {
        Integer after_sales_id = settlement.getAfter_sales_id();
        Integer obj_id_type = settlement.getObj_id_type();
        if (obj_id_type == SettlementConstant.OBJ_ID_TYPE_AFTER_SALES) {
            after_sales_id = settlement.getObj_id();
        }
        AfterSalesExtend afterSales = afterSalesService.getAfterSalesById(after_sales_id);
        if (afterSales != null) {

            BigDecimal after_sales_amount = new BigDecimal("0");//售后费用
            BigDecimal purchase_amount = new BigDecimal("0");//商品采购费用
            BigDecimal return_commodity_all_amount = new BigDecimal("0");//退货退还费用

            List<OrderFeeExtend> list = OrderFeeComponents.afterSalesOrderFeeList(settlement.getSupplier_id(), after_sales_id);
            for (int i = 0; i < list.size(); i++) {
                OrderFeeExtend orderFeeExtend = list.get(i);
                if (orderFeeExtend.getOrder_fee_enable() == 1) {
                    after_sales_amount = after_sales_amount.add(orderFeeExtend.getSupplier_fee());
                }
            }

            settlement.setSettlement_return_commodity_amount(return_commodity_all_amount);
            settlement.setSettlement_purchase_amount(purchase_amount);
            settlement.setSettlement_after_sales_amount(after_sales_amount);
            settlementAmount(settlement);
        }
    }

    /**
     * 场地维修结算
     * @param settlement
     */
    private void sampleFixSettlementAmount(Settlement settlement) {
        Integer obj_id_type = settlement.getObj_id_type();
        if (obj_id_type != null && obj_id_type == SettlementConstant.OBJ_ID_TYPE_SAMPLE_FIX) {
            BigDecimal after_sales_amount = new BigDecimal("0");//售后费用
            BigDecimal purchase_amount = new BigDecimal("0");//商品采购费用
            BigDecimal return_commodity_all_amount = new BigDecimal("0");//退货退还费用

            Integer sample_fix_id = settlement.getObj_id();
            SampleFixExtend sampleFix = sampleFixService.getSampleFixById(sample_fix_id);
            List<OrderFeeExtend> list = OrderFeeComponents.orderFeeList(sampleFix.getOrder_fee_ids());

            for (int i = 0; i < list.size(); i++) {
                OrderFeeExtend orderFeeExtend = list.get(i);
                if (orderFeeExtend.getOrder_fee_enable() == 1) {
                    after_sales_amount = after_sales_amount.add(orderFeeExtend.getSupplier_fee());
                }
            }

            settlement.setSettlement_return_commodity_amount(return_commodity_all_amount);
            settlement.setSettlement_purchase_amount(purchase_amount);
            settlement.setSettlement_after_sales_amount(after_sales_amount);
            settlementAmount(settlement);
        }
    }

    /**
     * 退货商品结算
     * @param settlement
     */
    private void returnCommodityAmount(Settlement settlement, List<SettlementCommodityPurchase> settlementCommodityPurchases) {
        Integer obj_id_type = settlement.getObj_id_type();
        if (obj_id_type != null && obj_id_type == SettlementConstant.OBJ_ID_TYPE_RETURN_COMMODITY) {
            BigDecimal after_sales_amount = new BigDecimal("0");//售后费用
            BigDecimal purchase_amount = new BigDecimal("0");//商品采购费用
            BigDecimal return_commodity_all_amount = new BigDecimal("0");//退货退还费用

            Integer order_return_id = settlement.getObj_id();
            OrderReturnExtend orderReturn = orderReturnService.getOrderReturnById(order_return_id);
            List<OrderFeeExtend> list = OrderFeeComponents.orderFeeList(orderReturn.getOrder_fee_ids());
            if (list != null && list.size() >0) {
                for (int i = 0; i < list.size(); i++) {
                    OrderFeeExtend orderFeeExtend = list.get(i);
                    System.out.println(settlement.getSupplier_id() + "\t" + orderFeeExtend.getSupplier_id() + "\t" + after_sales_amount);
                    if (orderFeeExtend.getOrder_fee_enable() == 1 && orderFeeExtend.getSupplier_id().equals(settlement.getSupplier_id())) {
                        after_sales_amount = after_sales_amount.add(orderFeeExtend.getSupplier_fee());
                    }
                }
            }

            //退货商品金额
            List<OrderReturnCommodityExtend> orderReturnCommodities = orderReturn.getOrderReturnCommodities();
            for (int i = 0; i < orderReturnCommodities.size(); i++) {
                OrderReturnCommodityExtend orderReturnCommodity = orderReturnCommodities.get(i);
                if (orderReturnCommodity.getOrder_return_commodity_type() != OrderConstants.ORDER_RETURN_COMMODITY_TYPE_ADD) {
                    Commodity commodity = commodityService.getCommodityById(orderReturnCommodity.getCommodity_id());
                    if (commodity.getSupplier_id().equals(settlement.getSupplier_id())) {
                        //退货商品
                        BigDecimal purchase_unit_price = null;

                        Integer settlement_id = settlement.getSettlement_id();
                        Integer order_return_commodity_id = orderReturnCommodity.getOrder_return_commodity_id();
                        if (settlement_id != null) {//编辑结算价
                            SettlementCommodityPurchase settlementCommodityPurchase = settlementExtendMapper.getSettlementCommodityPurchase(settlement_id, order_return_commodity_id);
                            if (settlementCommodityPurchase != null) {
                                purchase_unit_price = settlementCommodityPurchase.getPurchase_price();
                            }
                        }

                        if (purchase_unit_price == null) {
                            PurchaseCommodity purchaseCommodity = orderReturnCommodity.getPurchaseCommodity();
                            if (purchaseCommodity != null) {
                                purchase_unit_price = purchaseCommodity.getPurchase_unit_price();
                            }else {
                                purchase_unit_price = commodity.getPurchase_price();
                                SettlementCommodityPurchase settlementCommodityPurchase = new SettlementCommodityPurchase();
                                settlementCommodityPurchase.setPurchase_price(commodity.getPurchase_price());
                                settlementCommodityPurchase.setObj_id(order_return_commodity_id);
                                settlementCommodityPurchases.add(settlementCommodityPurchase);
                            }
                        }
                        Integer order_return_commodity_num = orderReturnCommodity.getOrder_return_commodity_num();
                        BigDecimal return_commodity_amount = purchase_unit_price.multiply(new BigDecimal(order_return_commodity_num));
                        return_commodity_all_amount = return_commodity_all_amount.add(return_commodity_amount);
                    }
                }
            }

            settlement.setSettlement_return_commodity_amount(return_commodity_all_amount);
            settlement.setSettlement_purchase_amount(purchase_amount);
            settlement.setSettlement_after_sales_amount(after_sales_amount);
            settlementAmount(settlement);
        }
    }

    /**
     * 赠品
     * @param settlement
     */
    private void giftCommodityAmount(Settlement settlement, List<SettlementCommodityPurchase> settlementCommodityPurchases) {
        Integer obj_id_type = settlement.getObj_id_type();
        Integer obj_id = settlement.getObj_id();
        if (obj_id_type != null && obj_id_type == SettlementConstant.OBJ_ID_TYPE_GIFT) {
            List<GiftCommodityExtend> giftCommodities = giftService.getGiftCommodityByGiftId(obj_id);
            BigDecimal after_sales_amount = new BigDecimal("0");//售后费用
            BigDecimal purchase_amount_total = new BigDecimal("0");//商品采购费用
            BigDecimal return_commodity_all_amount = new BigDecimal("0");//退货退还费用

            for (int i = 0; i < giftCommodities.size(); i++) {
                GiftCommodityExtend giftCommodityExtend = giftCommodities.get(i);
                Integer commodity_id = giftCommodityExtend.getCommodity_id();
                Commodity commodity = commodityService.getCommodityById(commodity_id);
                BigDecimal purchase_amount = null;
                Integer gift_commodity_id = giftCommodityExtend.getGift_commodity_id();
                if (settlement.getSettlement_id() != null) {
                    SettlementCommodityPurchase settlementCommodityPurchase = settlementExtendMapper.
                            getSettlementCommodityPurchase(settlement.getSettlement_id(), gift_commodity_id);
                    if (settlementCommodityPurchase != null) {
                        purchase_amount = settlementCommodityPurchase.getPurchase_price();
                    }
                }
                if (purchase_amount == null) {
                    PurchaseCommodity purchaseCommodity = purchaseService.getPurchaseCommodityByGiftCommodityId(gift_commodity_id);
                    if (purchaseCommodity != null && purchaseCommodity.getPurchase_unit_cost_price() != null) {
                        purchase_amount = purchaseCommodity.getPurchase_unit_cost_price();
                    }else {
                        purchase_amount = commodity.getPurchase_price();
                        SettlementCommodityPurchase settlementCommodityPurchase = new SettlementCommodityPurchase();
                        settlementCommodityPurchase.setObj_id(gift_commodity_id);
                        settlementCommodityPurchase.setPurchase_price(commodity.getPurchase_price());
                        settlementCommodityPurchases.add(settlementCommodityPurchase);
                    }
                }
                purchase_amount = purchase_amount.multiply(new BigDecimal(giftCommodityExtend.getGift_commodity_num()));
                purchase_amount_total = purchase_amount_total.add(purchase_amount);
            }
            //赠品结算，由供应商承担
            purchase_amount_total = new BigDecimal("0").subtract(purchase_amount_total);
            settlement.setSettlement_return_commodity_amount(return_commodity_all_amount);
            settlement.setSettlement_purchase_amount(purchase_amount_total);
            settlement.setSettlement_after_sales_amount(after_sales_amount);
            settlementAmount(settlement);
        }
    }

    private void giftReturnAmount(Settlement settlement, List<SettlementCommodityPurchase> settlementCommodityPurchases) {
        Integer obj_id_type = settlement.getObj_id_type();
        Integer obj_id = settlement.getObj_id();
        if (obj_id_type != null && obj_id_type == SettlementConstant.OBJ_ID_TYPE_GIFT_RETURN) {
            List<GiftReturnCommodityExtend> giftReturnCommodities = giftReturnService.giftReturnCommodities(obj_id);
            GiftReturnExtend giftReturnExtend = giftReturnService.giftReturnExtend(obj_id);
            Integer expense_type = giftReturnExtend.getGift().getExpense_type();
            for (int i = 0; i < giftReturnCommodities.size(); i++) {
                GiftReturnCommodityExtend giftReturnCommodityExtend = giftReturnCommodities.get(i);
                GiftCommodity giftCommodity = giftReturnCommodityExtend.getGiftCommodity();
                Integer commodity_id = giftCommodity.getCommodity_id();
                Commodity commodity = commodityService.getCommodityById(commodity_id);

                BigDecimal after_sales_amount = new BigDecimal("0");//售后费用
                BigDecimal purchase_amount = new BigDecimal("0");//商品采购费用
                BigDecimal return_commodity_all_amount = null;//退货退还费用
                Integer gift_return_commodity_id = giftReturnCommodityExtend.getGift_return_commodity_id();
                if (settlement.getSettlement_id() != null) {
                    SettlementCommodityPurchase settlementCommodityPurchase = settlementExtendMapper.
                            getSettlementCommodityPurchase(settlement.getSettlement_id(), gift_return_commodity_id);
                    if (settlementCommodityPurchase != null) {
                        return_commodity_all_amount = settlementCommodityPurchase.getPurchase_price();
                    }
                }
                if (return_commodity_all_amount == null) {
                    return_commodity_all_amount = commodity.getPurchase_price();
                    SettlementCommodityPurchase settlementCommodityPurchase = new SettlementCommodityPurchase();
                    settlementCommodityPurchase.setObj_id(gift_return_commodity_id);
                    settlementCommodityPurchase.setPurchase_price(commodity.getPurchase_price());
                    settlementCommodityPurchases.add(settlementCommodityPurchase);
                }

                return_commodity_all_amount = return_commodity_all_amount.multiply(new BigDecimal(giftReturnCommodityExtend.getReturn_num()));
                return_commodity_all_amount = new BigDecimal("0").subtract(return_commodity_all_amount);

                settlement.setSettlement_return_commodity_amount(return_commodity_all_amount);
                settlement.setSettlement_purchase_amount(purchase_amount);
                settlement.setSettlement_after_sales_amount(after_sales_amount);
                /*if (expense_type == 1) {
                    settlement.setSettlement_return_commodity_amount(return_commodity_all_amount);
                    settlement.setSettlement_purchase_amount(purchase_amount);
                    settlement.setSettlement_after_sales_amount(after_sales_amount);
                }else if (expense_type == 2) {
                    settlement.setSettlement_return_commodity_amount(return_commodity_all_amount);
                    settlement.setSettlement_purchase_amount(purchase_amount);
                    settlement.setSettlement_after_sales_amount(after_sales_amount);
                }*/
                settlementAmount(settlement);
            }
        }
    }

    public void giftReturnCommodityAmount(Settlement settlement, List<SettlementCommodityPurchase> settlementCommodityPurchases) {
        Integer obj_id = settlement.getObj_id();
        Integer obj_id_type = settlement.getObj_id_type();
        if (obj_id_type != null && obj_id_type == SettlementConstant.OBJ_ID_TYPE_GIFT_RETURN_COMMODITY) {
            GiftReturnCommodityExtend giftReturnCommodity = giftReturnService.getGiftReturnCommodityById(obj_id);
            Integer gift_return_commodity_id = giftReturnCommodity.getGift_return_commodity_id();
            Commodity commodity = giftReturnCommodity.getCommodity();
            BigDecimal after_sales_amount = new BigDecimal("0");//售后费用
            BigDecimal purchase_amount = new BigDecimal("0");//商品采购费用
            BigDecimal return_commodity_all_amount = null;//退货退还费用
            if (settlement.getSettlement_id() != null) {
                SettlementCommodityPurchase settlementCommodityPurchase = settlementExtendMapper.
                        getSettlementCommodityPurchase(settlement.getSettlement_id(), gift_return_commodity_id);
                if (settlementCommodityPurchase != null) {
                    return_commodity_all_amount = settlementCommodityPurchase.getPurchase_price();
                }
            }
            if (return_commodity_all_amount == null) {
                return_commodity_all_amount = commodity.getPurchase_price();
                SettlementCommodityPurchase settlementCommodityPurchase = new SettlementCommodityPurchase();
                settlementCommodityPurchase.setObj_id(gift_return_commodity_id);
                settlementCommodityPurchase.setPurchase_price(commodity.getPurchase_price());
                settlementCommodityPurchases.add(settlementCommodityPurchase);
            }

            return_commodity_all_amount = return_commodity_all_amount.multiply(new BigDecimal(giftReturnCommodity.getReturn_num()));

            settlement.setSettlement_return_commodity_amount(return_commodity_all_amount);
            settlement.setSettlement_purchase_amount(purchase_amount);
            settlement.setSettlement_after_sales_amount(after_sales_amount);
            settlementAmount(settlement);
        }
    }

    /**
     * 结算费用计算
     * 结算费用 = 采购费用 - 供应商承担运费、安装费、维修费等费用 - 售后退货商品退货费用
     * @param settlement
     */
    private void settlementAmount(Settlement settlement) {
        BigDecimal settlement_amount = settlement.getSettlement_purchase_amount().
                subtract(settlement.getSettlement_after_sales_amount()).
                subtract(settlement.getSettlement_return_commodity_amount());
        settlement.setSettlement_amount(settlement_amount);
        //已结算费用
        if (settlement.getSettlement_paid_amount() == null) {
            settlement.setSettlement_paid_amount(new BigDecimal("0"));
        }
        //未结算费用
        settlement.setSettlement_need_amount(settlement.getSettlement_amount().subtract(settlement.getSettlement_paid_amount()));
    }

    private SettlementExtend getByInventoryHistoryId(int supplier_id, int inventory_history_id) {
        SettlementSearchOption settlementSearchOption = new SettlementSearchOption();
        settlementSearchOption.setInventory_history_id(inventory_history_id);
        settlementSearchOption.setSupplier_id(supplier_id);
        List<SettlementExtend> list = settlementList(settlementSearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    private SettlementExtend getByAfterSalesId(int supplier_id , int after_sales_id) {
        SettlementSearchOption settlementSearchOption = new SettlementSearchOption();
        settlementSearchOption.setAfter_sales_id(after_sales_id);
        settlementSearchOption.setSupplier_id(supplier_id);
        List<SettlementExtend> list = settlementList(settlementSearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 售后费用结算
     * @param after_sales_id
     */
    public void createSettlementAfterSales(int after_sales_id) {
        AfterSalesExtend afterSalesExtend = afterSalesService.getAfterSalesById(after_sales_id);
        if (afterSalesExtend.getAfter_sales_status() == 1) {
            return;
        }
        List<AfterSalesCommodityExtend> list = afterSalesExtend.getAfterSalesCommodities();
        createSettlementAfterSales(afterSalesExtend, list);
    }

    @Transactional
    public void createSettlementSampleFix(int sample_fix_id, Employee employee) {
        SampleFixExtend sampleFixExtend = sampleFixService.getSampleFixById(sample_fix_id);
        if (sampleFixExtend.getSample_fix_settlement_status() == 1) {
            return;
        }
        Settlement settlement = new Settlement();
        settlement.setSupplier_id(sampleFixExtend.getSupplier_id());
        settlement.setSettlement_status(0);
        settlement.setChecking_status(0);
        settlement.setSettlement_type(SettlementConstant.SETTLEMENT_TYPE_SAMPLE_FIX);
        settlement.setVerify_status(0);
        settlement.setObj_id(sampleFixExtend.getSample_fix_id());
        settlement.setObj_id_type(SettlementConstant.OBJ_ID_TYPE_SAMPLE_FIX);
        List<SettlementCommodityPurchase> settlementCommodityPurchases = new ArrayList<>();
        setSettlementAmount1(settlement, settlementCommodityPurchases);
        settlementExtendMapper.createSettlement(settlement);
        sampleFixExtend.setSample_fix_settlement_status(1);
        sampleFixService.updateSampleFix(sampleFixExtend);
        createSettlementCommodityPurchase(settlementCommodityPurchases, employee, settlement.getSettlement_id());
    }

    /**
     * 售后商品维修结算，结算维修结束添加费用
     * @param afterSales
     * @param list
     */
    private void createSettlementAfterSales(AfterSales afterSales, List<AfterSalesCommodityExtend> list) {
        Set<Integer> supplierIds = new HashSet<>();
        for (int i = 0; i < list.size(); i++) {
            AfterSalesCommodityExtend afterSalesCommodity = list.get(i);
            int commodity_id = afterSalesCommodity.getCommodity_id();
            Commodity commodity = commodityService.getCommodityById(commodity_id);
            supplierIds.add(commodity.getSupplier_id());
        }
        Iterator<Integer> iterator = supplierIds.iterator();
        while (iterator.hasNext()) {
            int supplier_id = iterator.next();
            if (getByAfterSalesId(supplier_id, afterSales.getAfter_sales_id()) == null) {
                Settlement settlement = new Settlement();
                settlement.setSupplier_id(supplier_id);
                settlement.setAfter_sales_id(afterSales.getAfter_sales_id());
                settlement.setOrder_id(afterSales.getOrder_id());
                settlement.setSettlement_status(0);
                settlement.setChecking_status(0);
                settlement.setSettlement_type(SettlementConstant.SETTLEMENT_TYPE_AFTER_SALES);
                settlement.setVerify_status(0);
                settlement.setObj_id_type(SettlementConstant.OBJ_ID_TYPE_AFTER_SALES);
                settlement.setObj_id(afterSales.getAfter_sales_id());
                setSettlementAmount1(settlement, null);
                settlementExtendMapper.createSettlement(settlement);
            }
        }
    }

    /**
     * 退货商品结算
     * @param order_return_id
     */
    @Transactional
    public void createSettlementOrderReturn(int order_return_id, Employee employee){
        OrderReturnExtend orderReturnExtend = orderReturnService.getOrderReturnById(order_return_id);
        if (orderReturnExtend.getOrder_return_settlement_status() == 1) {
            //已经结算
            return;
        }
        /*if (orderReturnExtend.getOrder_return_type() == 0) {
            //售前退货，不结算
            return;
        }*/
        Integer order_return_type = orderReturnExtend.getOrder_return_type();
        switch (order_return_type) {
            case 0: preSaleOrderReturnSettlement(orderReturnExtend, employee);    break;//售前退货
            case 1: afterSalesOrderReturnSettlement(orderReturnExtend, employee);     break;//售后退货
            default:
                break;
        }
    }

    private void preSaleOrderReturnSettlement(OrderReturnExtend orderReturnExtend, Employee employee) {
        List<OrderFeeExtend> orderFees = orderReturnExtend.getOrderFees();
        Set<Integer> supplierIds = new HashSet<>();
        for (int i = 0; i < orderFees.size(); i++) {
            OrderFeeExtend orderFeeExtend = orderFees.get(i);
            Integer supplier_id = orderFeeExtend.getSupplier_id();
            supplierIds.add(supplier_id);
        }

        Iterator<Integer> iterator = supplierIds.iterator();
        while (iterator.hasNext()) {
            int supplier_id = iterator.next();
            Settlement settlement = new Settlement();
            settlement.setSupplier_id(supplier_id);
            settlement.setOrder_id(orderReturnExtend.getOrder_id());
            settlement.setSettlement_status(0);
            settlement.setChecking_status(0);
            settlement.setSettlement_type(SettlementConstant.SETTLEMENT_TYPE_PRE_SALE_RETURN);
            settlement.setVerify_status(0);
            settlement.setObj_id_type(SettlementConstant.OBJ_ID_TYPE_PRE_SALE_RETURN);
            settlement.setObj_id(orderReturnExtend.getOrder_return_id());
            List<SettlementCommodityPurchase> settlementCommodityPurchases = new ArrayList<>();
            setSettlementAmount1(settlement, settlementCommodityPurchases);
            settlementExtendMapper.createSettlement(settlement);
            createSettlementCommodityPurchase(settlementCommodityPurchases, employee, settlement.getSettlement_id());
        }
        orderReturnExtend.setOrder_return_settlement_status(1);
        orderReturnService.updateOrderReturn(orderReturnExtend);
    }
    private void afterSalesOrderReturnSettlement(OrderReturnExtend orderReturnExtend, Employee employee) {
        List<OrderReturnCommodityExtend> list = orderReturnExtend.getOrderReturnCommodities();
        Set<Integer> supplierIds = new HashSet<>();
        for (int i = 0; i < list.size(); i++) {
            OrderReturnCommodityExtend orderReturnCommodityExtend = list.get(i);
            if (orderReturnCommodityExtend.getOrder_return_commodity_type() == OrderConstants.ORDER_RETURN_COMMODITY_TYPE_ADD) {
                //退换货新增商品不在此结算，出库时添加费用时结算
                continue;
            }
            int commodity_id = orderReturnCommodityExtend.getCommodity_id();
            Commodity commodity = commodityService.getCommodityById(commodity_id);
            supplierIds.add(commodity.getSupplier_id());
        }
        Iterator<Integer> iterator = supplierIds.iterator();
        while (iterator.hasNext()) {
            int supplier_id = iterator.next();
            Settlement settlement = new Settlement();
            settlement.setSupplier_id(supplier_id);
            settlement.setOrder_id(orderReturnExtend.getOrder_id());
            settlement.setSettlement_status(0);
            settlement.setChecking_status(0);
            settlement.setSettlement_type(SettlementConstant.SETTLEMENT_TYPE_RETURN_COMMODITY);
            settlement.setVerify_status(0);
            settlement.setObj_id_type(SettlementConstant.OBJ_ID_TYPE_RETURN_COMMODITY);
            settlement.setObj_id(orderReturnExtend.getOrder_return_id());
            List<SettlementCommodityPurchase> settlementCommodityPurchases = new ArrayList<>();
            setSettlementAmount1(settlement, settlementCommodityPurchases);
            settlementExtendMapper.createSettlement(settlement);
            createSettlementCommodityPurchase(settlementCommodityPurchases, employee, settlement.getSettlement_id());
        }
        orderReturnExtend.setOrder_return_settlement_status(1);
        orderReturnService.updateOrderReturn(orderReturnExtend);
    }

    /**
     *
     * @param gift_id
     */
    @Transactional
    public void createGiftSettlement(int gift_id, Employee employee) {
        GiftExtend gift = giftService.getGiftById(gift_id);
        if (gift.getGift_settlement_status() == 1) {
            return;
        }
        InventoryHistorySearchOption inventoryHistorySearchOption = new InventoryHistorySearchOption();
        inventoryHistorySearchOption.setGift_id(gift_id);
        List<InventoryHistoryExtend> inventoryHistoryExtends = inventoryHistoryService.inventoryHistories(inventoryHistorySearchOption);
        InventoryHistoryExtend inventoryHistoryExtend = inventoryHistoryExtends.get(0);
        Logistics logistics = inventoryHistoryExtend.getLogistics();
        if (logistics.getLogistics_status() !=  LogisticsConstants.LOGISTICS_STATUS_DONE) {
            return;
        }
        if (gift.getExpense_type().equals(1)) {//供应商承担费用
            Settlement settlement = new Settlement();
            settlement.setSupplier_id(gift.getSupplier_id());
            settlement.setSettlement_status(0);
            settlement.setChecking_status(0);
            settlement.setSettlement_type(SettlementConstant.SETTLEMENT_TYPE_GIFT);
            settlement.setVerify_status(0);
            settlement.setOrder_id(gift.getOrder_id());
            settlement.setObj_id_type(SettlementConstant.OBJ_ID_TYPE_GIFT);
            settlement.setObj_id(gift_id);
            List<SettlementCommodityPurchase> settlementCommodityPurchases = new ArrayList<>();
            setSettlementAmount1(settlement, settlementCommodityPurchases);
            settlementExtendMapper.createSettlement(settlement);
            createSettlementCommodityPurchase(settlementCommodityPurchases, employee, settlement.getSettlement_id());
        }
        gift.setGift_settlement_status(1);
        giftService.updateGift(gift);
    }

    /**
     * 广东馆商品入库结算
     * @param inventory_history_id
     */
    @Transactional
    public void createSettlementInventoryIn(int inventory_history_id, Employee employee) {
        InventoryHistoryExtend inventoryHistory = inventoryHistoryService.getInventoryHistoryById(inventory_history_id);
        if (inventoryHistory.getInventory_history_settlement_status() == 1) {
            return;
        }
        List<InventoryHistoryDetailExtend> list = inventoryHistory.getInventoryHistoryDetails();
        Set<Integer> supplierIds = new HashSet<>();
        for (int i = 0; i < list.size(); i++) {
            InventoryHistoryDetailExtend inventoryHistoryDetail = list.get(i);
            int commodity_id = inventoryHistoryDetail.getCommodity_id();
            Commodity commodity = commodityService.getCommodityById(commodity_id);
            if (commodity != null && commodity.getSupplier_id() != null && commodity.getGuangdong() != null
                    && commodity.getGuangdong() == 1&& inventoryHistoryDetail.getSample() == 0) {
                supplierIds.add(commodity.getSupplier_id());
            }
        }

        Iterator<Integer> iterator = supplierIds.iterator();
        while (iterator.hasNext()) {
            int supplier_id = iterator.next();
            if (getByInventoryHistoryId(supplier_id, inventoryHistory.getInventory_history_id()) == null) {
                Settlement settlement = new Settlement();
                settlement.setSupplier_id(supplier_id);
                settlement.setInventory_history_id(inventoryHistory.getInventory_history_id());
                settlement.setOrder_id(inventoryHistory.getOrder_id());
                settlement.setSettlement_status(0);
                settlement.setChecking_status(0);
                settlement.setVerify_status(0);
                settlement.setSettlement_type(SettlementConstant.SETTLEMENT_TYPE_INVENTORY_IN);
                settlement.setObj_id(inventoryHistory.getInventory_history_id());
                settlement.setObj_id_type(SettlementConstant.OBJ_ID_TYPE_INVENTORY_IN);
                List<SettlementCommodityPurchase> settlementCommodityPurchases = new ArrayList<>();
                //结算金额
                setSettlementAmount1(settlement, settlementCommodityPurchases);
                settlementExtendMapper.createSettlement(settlement);
                createSettlementCommodityPurchase(settlementCommodityPurchases, employee, settlement.getSettlement_id());
            }
        }
        inventoryHistory.setInventory_history_settlement_status(1);
        inventoryHistoryService.updateInventoryHistory(inventoryHistory);
    }

    /**
     * 广东馆商品全部入库结算
     */
    @Transactional
    public void createSettlementPurchaseArrival(int purchase_commodity_id, Employee employee) {
        PurchaseCommodity purchaseCommodity = purchaseService.getPurchaseCommodityById(purchase_commodity_id);
        Commodity commodity = CommodityComponent.getCommodityById(purchaseCommodity.getCommodity_id());
        if (commodity.getGuangdong() == 1
                && (purchaseCommodity.getPurchase_commodity_settlement_status() == null || purchaseCommodity.getPurchase_commodity_settlement_status() != 1)
                && purchaseCommodity.getPurchase_commodity_status() == 4) {
            Purchase purchase = purchaseService.getPurchaseById1(purchaseCommodity.getPurchase_id());
            Integer supplier_id = commodity.getSupplier_id();
            Settlement settlement = new Settlement();
            settlement.setSupplier_id(supplier_id);
            settlement.setOrder_id(purchase.getOrder_id());
            settlement.setSettlement_status(0);
            settlement.setChecking_status(0);
            settlement.setVerify_status(0);
            settlement.setSettlement_type(SettlementConstant.SETTLEMENT_TYPE_PURCHASE_ARRIVAL);
            settlement.setObj_id(purchaseCommodity.getPurchase_commodity_id());
            settlement.setObj_id_type(SettlementConstant.OBJ_ID_TYPE_PURCHASE_ARRIVAL);
            List<SettlementCommodityPurchase> settlementCommodityPurchases = new ArrayList<>();
            //结算金额
            setSettlementAmount1(settlement, settlementCommodityPurchases);
            settlementExtendMapper.createSettlement(settlement);
            createSettlementCommodityPurchase(settlementCommodityPurchases, employee, settlement.getSettlement_id());
            purchaseCommodity.setPurchase_commodity_settlement_status(1);
            purchaseService.updatePurchaseCommodity(purchaseCommodity);
        }
    }

    public void giftReturnSettlement(int gift_return_id, Employee employee) {
        GiftReturnExtend giftReturnExtend = giftReturnService.giftReturnExtend(gift_return_id);
        if (giftReturnExtend.getGift_return_settlement_status() == 1) {
            return;
        }
        Gift gift = giftReturnExtend.getGift();

        Settlement settlement = new Settlement();
        Integer supplier_id = null;
        if (gift.getExpense_type() == 1) {//凤凰城承担礼品退还不结算
            supplier_id = gift.getSupplier_id();
            settlement.setSupplier_id(supplier_id);
            settlement.setSettlement_status(0);
            settlement.setChecking_status(0);
            settlement.setSettlement_type(SettlementConstant.SETTLEMENT_TYPE_GIFT_RETURN);

            settlement.setVerify_status(0);
            settlement.setObj_id_type(SettlementConstant.OBJ_ID_TYPE_GIFT_RETURN);
            settlement.setObj_id(gift_return_id);
            settlement.setOrder_id(giftReturnExtend.getGift().getOrder_id());
            List<SettlementCommodityPurchase> settlementCommodityPurchases = new ArrayList<>();
            setSettlementAmount1(settlement, settlementCommodityPurchases);
            settlementExtendMapper.createSettlement(settlement);
            createSettlementCommodityPurchase(settlementCommodityPurchases, employee, settlement.getSettlement_id());
        }

        List<GiftReturnCommodityExtend> giftReturnCommodityExtends = giftReturnExtend.getGiftReturnCommodityExtends();
        for (int i = 0; i < giftReturnCommodityExtends.size(); i++) {
            GiftReturnCommodityExtend giftReturnCommodityExtend = giftReturnCommodityExtends.get(i);
            giftReturnCommoditySettlement(giftReturnCommodityExtend, employee);
        }

        giftReturnExtend.setGift_return_settlement_status(1);
        giftReturnService.updateGiftReturn(giftReturnExtend);
    }

    public void giftReturnCommoditySettlement(GiftReturnCommodityExtend giftReturnCommodity, Employee employee) {
        Commodity commodity = giftReturnCommodity.getCommodity();
        CommodityExtend commodityById = CommodityComponent.getCommodityById(commodity.getCommodity_id());
        Settlement settlement = new Settlement();
        settlement.setSupplier_id(commodityById.getSupplier_id());
        settlement.setSettlement_status(0);
        settlement.setChecking_status(0);
        settlement.setSettlement_type(SettlementConstant.SETTLEMENT_TYPE_GIFT_RETURN_COMMODITY);
        settlement.setVerify_status(0);
        settlement.setObj_id_type(SettlementConstant.OBJ_ID_TYPE_GIFT_RETURN_COMMODITY);
        settlement.setObj_id(giftReturnCommodity.getGift_return_commodity_id());
        settlement.setOrder_id(giftReturnCommodity.getGift().getOrder_id());
        List<SettlementCommodityPurchase> settlementCommodityPurchases = new ArrayList<>();
        setSettlementAmount1(settlement, settlementCommodityPurchases);
        settlementExtendMapper.createSettlement(settlement);
        createSettlementCommodityPurchase(settlementCommodityPurchases, employee, settlement.getSettlement_id());
    }

    /**
     * 售前退货结算
     * @param settlement
     */
    private void preSaleOrderReturn(Settlement settlement) {
        Integer obj_id = settlement.getObj_id();
        Integer supplier_id = settlement.getSupplier_id();
        OrderReturnExtend orderReturnExtend = orderReturnService.getOrderReturnById(obj_id);
        List<OrderFeeExtend> orderFees = orderReturnExtend.getOrderFees();

        BigDecimal after_sales_amount = new BigDecimal("0");//售后费用
        BigDecimal purchase_amount = new BigDecimal("0");//商品采购费用
        BigDecimal return_commodity_all_amount = new BigDecimal("0");//退货退还费用

        for (int i = 0; i < orderFees.size(); i++) {
            OrderFeeExtend orderFeeExtend = orderFees.get(i);
            if (orderFeeExtend.getOrder_fee_enable() == 1 && orderFeeExtend.getSupplier_id().equals(supplier_id)) {
                after_sales_amount = after_sales_amount.add(orderFeeExtend.getSupplier_fee());
            }
        }

        settlement.setSettlement_return_commodity_amount(return_commodity_all_amount);
        settlement.setSettlement_purchase_amount(purchase_amount);
        settlement.setSettlement_after_sales_amount(after_sales_amount);
        settlementAmount(settlement);
    }

    private void purchaseArrival(Settlement settlement, List<SettlementCommodityPurchase> settlementCommodityPurchases) {
        Integer purchase_commodity_id = settlement.getObj_id();
        PurchaseCommodity purchaseCommodity = purchaseService.getPurchaseCommodityById(purchase_commodity_id);
        Integer sample = purchaseCommodity.getSample();
        Commodity commodity = commodityService.getCommodityById(purchaseCommodity.getCommodity_id());
        BigDecimal after_sales_amount = new BigDecimal("0");//售后费用
        BigDecimal purchase_amount = new BigDecimal("0");//商品采购费用
        BigDecimal return_commodity_all_amount = new BigDecimal("0");//退货退还费用

        BigDecimal purchaseUnitPrice = null;
        if (settlement.getSettlement_id() != null) {
            SettlementCommodityPurchase settlementCommodityPurchase =
                    settlementExtendMapper.getSettlementCommodityPurchase(settlement.getSettlement_id(), purchase_commodity_id);
            if (settlementCommodityPurchase != null) {
                purchaseUnitPrice = settlementCommodityPurchase.getPurchase_price();
            }
        }
        if (commodity.getGuangdong() != null && commodity.getGuangdong() == 1 && sample == 0) {
            //广东馆商品结算
            Integer purchase_num = purchaseCommodity.getPurchase_num();
            if (purchaseUnitPrice == null) {
                purchaseUnitPrice = purchaseCommodity.getPurchase_unit_cost_price();

                SettlementCommodityPurchase settlementCommodityPurchase = new SettlementCommodityPurchase();
                settlementCommodityPurchase.setObj_id(settlement.getObj_id());
                settlementCommodityPurchase.setPurchase_price(purchaseUnitPrice);
                settlementCommodityPurchases.add(settlementCommodityPurchase);
            }
            purchase_amount = purchaseUnitPrice.multiply(new BigDecimal(purchase_num));
        }

        settlement.setSettlement_return_commodity_amount(return_commodity_all_amount);
        settlement.setSettlement_purchase_amount(purchase_amount);
        settlement.setSettlement_after_sales_amount(after_sales_amount);
        settlementAmount(settlement);
    }

    /**
     * 重新计算结束
     * @param settlement_id
     */
    @Transactional
    public void settlementReset(int settlement_id, Employee employee) {
        SettlementExtend settlement = getSettlementById(settlement_id);
        //计算结算金额
        List<SettlementCommodityPurchase> settlementCommodityPurchases = new ArrayList<>();
        setSettlementAmount1(settlement, settlementCommodityPurchases);

        List<SettlementHistory> settlementHistories = settlement.getSettlementHistories();
        //已结算金额
        BigDecimal paidAmount = new BigDecimal("0");
        for (int i = 0; i < settlementHistories.size(); i++) {
            SettlementHistory settlementHistory = settlementHistories.get(i);
            paidAmount = paidAmount.add(settlementHistory.getSettlement_history_amount());
        }
        settlement.setSettlement_paid_amount(paidAmount);
        settlementMapper.updateByPrimaryKey(settlement);
    }


}
