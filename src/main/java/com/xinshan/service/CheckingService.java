package com.xinshan.service;

import com.xinshan.components.commodity.CommodityComponent;
import com.xinshan.dao.CheckingDetailMapper;
import com.xinshan.dao.CheckingMapper;
import com.xinshan.dao.extend.checking.CheckingExtendMapper;
import com.xinshan.model.*;
import com.xinshan.model.extend.afterSales.AfterSalesCommodityExtend;
import com.xinshan.model.extend.checking.CheckingDetailExtend;
import com.xinshan.model.extend.checking.CheckingExtend;
import com.xinshan.model.extend.commodity.CommodityExtend;
import com.xinshan.model.extend.gift.GiftCommodityExtend;
import com.xinshan.model.extend.gift.GiftReturnCommodityExtend;
import com.xinshan.model.extend.order.OrderReturnCommodityExtend;
import com.xinshan.model.extend.orderFee.OrderFeeExtend;
import com.xinshan.model.extend.purchase.PurchaseCommodityExtend;
import com.xinshan.model.extend.settlement.SettlementExtend;
import com.xinshan.model.extend.settlement.SettlementInventoryOutCommodity;
import com.xinshan.model.extend.supplier.SupplierExtend;
import com.xinshan.pojo.settlement.SettlementSearchOption;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.checking.CheckingUtil;
import com.xinshan.utils.constant.ConstantUtils;
import com.xinshan.utils.constant.checking.ConstantCheckingType;
import com.xinshan.pojo.checking.CheckingSearchOptions;
import com.xinshan.utils.constant.order.OrderConstants;
import com.xinshan.utils.constant.settlement.SettlementConstant;
import org.apache.http.annotation.ThreadSafe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by mxt on 17-2-20.
 */
@Service
@ThreadSafe
public class CheckingService {
    @Autowired
    private CheckingExtendMapper checkingExtendMapper;
    @Autowired
    private SettlementService settlementService;
    @Autowired
    private CheckingMapper checkingMapper;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private CheckingDetailMapper checkingDetailMapper;
    @Autowired
    private OrderService orderService;


    @Transactional
    public void createChecking(List<SettlementExtend> list, Employee employee, String check_remark) {
        create(list, employee, check_remark, false);
    }

    public synchronized void create(List<SettlementExtend> list, Employee employee, String check_remark, boolean reset) {
        Date date = DateUtil.currentDate();
        Map<Integer, Map<String, List<SettlementExtend>>> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {//根据供应商生成对账单
            SettlementExtend settlement = list.get(i);
            Integer supplier_id = settlement.getSupplier_id();
            SupplierExtend supplier = supplierService.getSupplierById(supplier_id);
            String contacts = supplier.getContacts();
            int checking_type = 0;
            if (settlement.getSettlement_type() == SettlementConstant.SETTLEMENT_TYPE_INVENTORY_OUT) {
                checking_type = ConstantCheckingType.CHECKING_TYPE_COMMODITY;
            }else if (settlement.getSettlement_type() == SettlementConstant.SETTLEMENT_TYPE_AFTER_SALES) {
                checking_type = ConstantCheckingType.CHECKING_TYPE_AFTER_SALES;
            }else if (settlement.getSettlement_type() == SettlementConstant.SETTLEMENT_TYPE_SAMPLE_FIX) {
                checking_type = ConstantCheckingType.CHECKING_TYPE_SAMPLE_FIX;
            }else if (settlement.getSettlement_type() == SettlementConstant.SETTLEMENT_TYPE_RETURN_COMMODITY) {
                checking_type = ConstantCheckingType.CHECKING_TYPE_RETURN_COMMODITY;
            }else if (settlement.getSettlement_type() == SettlementConstant.SETTLEMENT_TYPE_GIFT) {
                checking_type = ConstantCheckingType.CHECKING_TYPE_GIFT;
            }else if (settlement.getSettlement_type() == SettlementConstant.SETTLEMENT_TYPE_GIFT_RETURN) {
                checking_type = ConstantCheckingType.CHECKING_TYPE_GIFT_RETURN;
            }else if (settlement.getSettlement_type() == SettlementConstant.SETTLEMENT_TYPE_INVENTORY_IN) {
                checking_type = ConstantCheckingType.CHECKING_TYPE_INVENTORY_IN;
            }else if (settlement.getSettlement_type() == SettlementConstant.SETTLEMENT_TYPE_GIFT_OUT) {
                checking_type = ConstantCheckingType.CHECKING_TYPE_GIFT_OUT;
            }else if (settlement.getSettlement_type() == SettlementConstant.SETTLEMENT_TYPE_PRE_SALE_RETURN) {
                checking_type = ConstantCheckingType.CHECKING_TYPE_PRE_SALE_ORDER_RETURN;
            }else if (settlement.getSettlement_type() == SettlementConstant.SETTLEMENT_TYPE_PURCHASE_ARRIVAL) {
                checking_type = ConstantCheckingType.CHECKING_TYPE_PURCHASE_ARRIVAL;
            }
            if (map.containsKey(checking_type)) {
                Map<String, List<SettlementExtend>> hashMap = map.get(checking_type);
                List<SettlementExtend> settlementExtends = hashMap.get(contacts);
                if (settlementExtends == null) {
                    settlementExtends = new ArrayList<>();
                }
                settlementExtends.add(settlement);
                hashMap.put(contacts, settlementExtends);
                map.put(checking_type, hashMap);
            }else {
                Map<String, List<SettlementExtend>> hashMap = new HashMap<>();
                List<SettlementExtend> settlementExtends = new ArrayList<>();
                settlementExtends.add(settlement);
                hashMap.put(contacts, settlementExtends);
                map.put(checking_type, hashMap);
            }
        }
        String checkCode = null;
        Iterator<Map.Entry<Integer, Map<String, List<SettlementExtend>>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Map<String, List<SettlementExtend>>> entry = iterator.next();
            int checking_type = entry.getKey();
            Map<String, List<SettlementExtend>> hashMap = entry.getValue();
            Iterator<Map.Entry<String, List<SettlementExtend>>> ite = hashMap.entrySet().iterator();
            while (ite.hasNext()) {
                Map.Entry<String, List<SettlementExtend>> entry1 = ite.next();
                List<SettlementExtend> settlementExtends = entry1.getValue();
                checkCode = checkingCode(checkCode);//获取对账单号
                Checking checking = new Checking();
                checking.setChecking_code(checkCode);
                checking.setCreate_date(date);
                checking.setCreate_employee_name(employee.getEmployee_name());
                checking.setCreate_employee_code(employee.getEmployee_code());
                checking.setSupplier_id(0);
                checking.setSupplier_contacts(entry1.getKey());
                checking.setChecking_type(checking_type);
                checking.setCheck_remark(check_remark);
                checking.setPayment_status(0);
                checking.setCheck_status(0);
                checkingExtendMapper.createChecking(checking);
                createChecking(settlementExtends, checking, reset);
            }
        }
    }

    private void createChecking(List<SettlementExtend> list, Checking checking, boolean reset) {
        for (int i = 0; i < list.size(); i++) {
            SettlementExtend settlement = list.get(i);
            if (settlement.getChecking_status() == 1 && !reset) {
                continue;
            }
            Integer obj_id_type = settlement.getObj_id_type();
            switch (obj_id_type) {
                case SettlementConstant.OBJ_ID_TYPE_INVENTORY_OUT://出库
                    inventoryOutCheckingDetail(checking, settlement);
                    break;
                case SettlementConstant.OBJ_ID_TYPE_AFTER_SALES://售后
                    afterSalesCheckingDetail(checking, settlement);
                    break;
                case SettlementConstant.OBJ_ID_TYPE_SAMPLE_FIX://场地维修
                    sampleFixCheckingDetail(checking, settlement);
                    break;
                case SettlementConstant.OBJ_ID_TYPE_RETURN_COMMODITY://退货
                    returnCommodityCheckingDetail(checking, settlement);
                    break;
                case SettlementConstant.OBJ_ID_TYPE_GIFT://
                    giftCommodityCheckingDetail(checking, settlement);
                    break;
                case SettlementConstant.OBJ_ID_TYPE_GIFT_RETURN://退货
                    giftReturnCommodityCheckingDetail(checking, settlement);
                    break;
                case SettlementConstant.OBJ_ID_TYPE_INVENTORY_IN://退货
                    inventoryOutCheckingDetail(checking, settlement);
                    break;
                case SettlementConstant.OBJ_ID_TYPE_GIFT_OUT://退货
                    giftOutCheckingDetail(checking, settlement);
                    break;
                case SettlementConstant.OBJ_ID_TYPE_PRE_SALE_RETURN:
                    preSaleOrderReturnCheckingDetail(checking, settlement);
                    break;
                case SettlementConstant.SETTLEMENT_TYPE_PURCHASE_ARRIVAL:
                    purchaseArroval(checking, settlement);
                default:
                    break;
            }
            settlement.setChecking_status(1);
            settlementService.updateSettlementCheckingStatus(settlement);
        }
    }

    /**
     * 各项费用对账详情
     * @param checking
     * @param settlement
     */
    private void orderFeesCheckingDetail(Checking checking, SettlementExtend settlement) {
        //售后费用对账单
        List<OrderFeeExtend> orderFees = settlement.getOrderFees();
        if (orderFees != null && orderFees.size() >0) {
            for (int j = 0; j < orderFees.size(); j++) {
                OrderFeeExtend orderFee = orderFees.get(j);

                CheckingDetail checkingDetail = checkingDetailInit(checking, settlement, ConstantCheckingType.CHECKING_DETAIL_TYPE_FEE);
                checkingDetail.setFee_type(orderFee.getOrder_fee_type_id());
                checkingDetail.setFhc_fee(orderFee.getFhc_fee() == null ? new BigDecimal("0") : orderFee.getFhc_fee());
                checkingDetail.setSupplier_fee(orderFee.getSupplier_fee() == null ? new BigDecimal("0") : orderFee.getSupplier_fee());
                checkingDetail.setFee(checkingDetail.getFhc_fee().add(checkingDetail.getSupplier_fee()));
                checkingExtendMapper.createCheckingDetail(checkingDetail);
            }
        }
    }

    private void preSaleOrderReturnCheckingDetail(Checking checking, SettlementExtend settlement) {
        //售后费用对账单
        List<OrderFeeExtend> orderFees = settlement.getOrderFees();
        if (orderFees != null && orderFees.size() >0) {
            for (int i = 0; i < orderFees.size(); i++) {
                OrderFeeExtend orderFee = orderFees.get(i);
                if (orderFee.getOrder_fee_enable() == 1 && orderFee.getSupplier_id().equals(settlement.getSupplier_id())) {
                    CheckingDetail checkingDetail = checkingDetailInit(checking, settlement, ConstantCheckingType.CHECKING_TYPE_PRE_SALE_ORDER_RETURN);
                    checkingDetail.setFee_type(orderFee.getOrder_fee_type_id());
                    //checkingDetail.setFhc_fee(orderFee.getFhc_fee() == null ? new BigDecimal("0") : orderFee.getFhc_fee());
                    checkingDetail.setFhc_fee(new BigDecimal("0"));
                    checkingDetail.setSupplier_fee(orderFee.getSupplier_fee() == null ? new BigDecimal("0") : orderFee.getSupplier_fee());
                    checkingDetail.setFee(checkingDetail.getFhc_fee().add(checkingDetail.getSupplier_fee()));
                    checkingExtendMapper.createCheckingDetail(checkingDetail);
                }
            }
        }
    }

    /**
     * 出库对账单详情
     * @param checking
     * @param settlement
     */
    private void inventoryOutCheckingDetail(Checking checking, SettlementExtend settlement) {
        List<SettlementInventoryOutCommodity> settlementCommodities = settlement.getSettlementCommodities();
        if (settlementCommodities != null && settlementCommodities.size() > 0) {
            for (int j = 0; j < settlementCommodities.size(); j++) {
                SettlementInventoryOutCommodity settlementCommodity = settlementCommodities.get(j);
                SettlementCommodityPurchase settlementCommodityPurchase = settlementCommodity.getSettlementCommodityPurchase();
                OrderCommodity orderCommodity = settlementCommodity.getOrderCommodity();
                PurchaseCommodity purchaseCommodity = settlementCommodity.getPurchaseCommodity();
                Commodity commodity = settlementCommodity.getCommodity();
                CheckingDetail checkingDetail = checkingDetailInit(checking, settlement, ConstantCheckingType.CHECKING_DETAIL_TYPE_COMMODITY);

                checkingDetail.setOrder_commodity_id(orderCommodity.getOrder_commodity_id());
                checkingDetail.setCommodity_id(commodity.getCommodity_id());
                boolean supplier_out = CheckingUtil.getCheckingUtil().supplierOut(settlementCommodity);
                int num = 0;
                if (supplier_out) {//供应商发货，出货数量未订单商品数量
                    num = settlementCommodity.getOrderCommodity().getCommodity_num();
                }else {//正常出库，出货数量为出库数量
                    num = settlementCommodity.getInventory_history_num();
                }
                checkingDetail.setCommodity_num(num);//出库数量
                checkingDetail.setRevision_fee(orderCommodity.getRevision_fee());//改版费
                checkingDetail.setBargain_price(orderCommodity.getBargain_price());//商品成交价
                //商品成交总价 = （成交单价 + 改版费） * 出库数量
                checkingDetail.setCommodity_total_price((checkingDetail.getBargain_price().add(checkingDetail.getRevision_fee())).multiply(new BigDecimal(checkingDetail.getCommodity_num())));
                //商品采购单价
                checkingDetail.setPurchase_unit_price(purchaseUnitPrice(settlementCommodityPurchase, purchaseCommodity, commodity));
                //商品采购总价 = 采购单价 * 出库数量
                checkingDetail.setPurchase_total_price(checkingDetail.getPurchase_unit_price().multiply(new BigDecimal(checkingDetail.getCommodity_num())));
                checkingExtendMapper.createCheckingDetail(checkingDetail);
            }
        }
        orderFeesCheckingDetail(checking, settlement);
    }

    /**
     * 出库对账单详情
     * @param checking
     * @param settlement
     */
    private void giftOutCheckingDetail(Checking checking, SettlementExtend settlement) {
        List<SettlementInventoryOutCommodity> settlementCommodities = settlement.getSettlementCommodities();
        if (settlementCommodities != null && settlementCommodities.size() > 0) {
            for (int j = 0; j < settlementCommodities.size(); j++) {
                SettlementInventoryOutCommodity settlementCommodity = settlementCommodities.get(j);
                SettlementCommodityPurchase settlementCommodityPurchase = settlementCommodity.getSettlementCommodityPurchase();
                PurchaseCommodity purchaseCommodity = settlementCommodity.getPurchaseCommodity();
                Commodity commodity = settlementCommodity.getCommodity();
                CheckingDetail checkingDetail = checkingDetailInit(checking, settlement, ConstantCheckingType.CHECKING_DETAIL_TYPE_COMMODITY);

                checkingDetail.setCommodity_id(commodity.getCommodity_id());
                Integer num = settlementCommodity.getInventory_history_num();
                checkingDetail.setCommodity_num(num);//出库数量
                checkingDetail.setBargain_price(new BigDecimal("0"));
                checkingDetail.setRevision_fee(new BigDecimal("0"));
                checkingDetail.setCommodity_total_price(new BigDecimal("0"));
                //商品采购单价
                checkingDetail.setPurchase_unit_price(purchaseUnitPrice(settlementCommodityPurchase, purchaseCommodity, commodity));
                //商品采购总价 = 采购单价 * 出库数量
                checkingDetail.setPurchase_total_price(checkingDetail.getPurchase_unit_price().multiply(new BigDecimal(checkingDetail.getCommodity_num())));
                checkingExtendMapper.createCheckingDetail(checkingDetail);
            }
        }
        orderFeesCheckingDetail(checking, settlement);
    }

    /**
     * 商品采购价
     * @param settlementCommodityPurchase
     * @param purchaseCommodity
     * @param commodity
     * @return
     */
    private BigDecimal purchaseUnitPrice(SettlementCommodityPurchase settlementCommodityPurchase, PurchaseCommodity purchaseCommodity, Commodity commodity) {
        if (settlementCommodityPurchase != null && settlementCommodityPurchase.getPurchase_price() != null) {
            return settlementCommodityPurchase.getPurchase_price();
        }else {
            if (purchaseCommodity != null && purchaseCommodity.getPurchase_unit_cost_price() != null && purchaseCommodity.getPurchase_commodity_status() != 5) {
                return purchaseCommodity.getPurchase_unit_cost_price();
            }else {
                return commodity.getPurchase_price();
            }
        }
    }

    /**
     * 售后对账详情
     * @param checking
     * @param settlement
     */
    private void afterSalesCheckingDetail(Checking checking, SettlementExtend settlement) {
        List<AfterSalesCommodityExtend> afterSalesCommodities = settlement.getAfterSalesCommodities();//退货商品
        if (afterSalesCommodities != null && afterSalesCommodities.size() > 0) {
            for (int j = 0; j < afterSalesCommodities.size(); j++) {//退货商品
                AfterSalesCommodityExtend salesCommodityExtend = afterSalesCommodities.get(j);
                OrderCommodity orderCommodity = salesCommodityExtend.getOrderCommodity();
                PurchaseCommodity purchaseCommodity = salesCommodityExtend.getPurchaseCommodity();
                Commodity commodity = salesCommodityExtend.getCommodity();

                CheckingDetail checkingDetail = checkingDetailInit(checking, settlement, ConstantCheckingType.CHECKING_DETAIL_TYPE_COMMODITY);
                OrderReturnCommodity orderReturnCommodity = salesCommodityExtend.getOrderReturnCommodity();
                checkingDetail.setOrder_commodity_id(orderCommodity.getOrder_commodity_id());
                checkingDetail.setCommodity_id(commodity.getCommodity_id());

                int num = Math.abs(orderReturnCommodity.getOrder_return_commodity_num());
                checkingDetail.setCommodity_num(num);
                checkingDetail.setBargain_price(orderReturnCommodity.getBargain_price());//商品成交价
                //商品成交总价 = （成交单价 + 改版费） * 出库数量
                checkingDetail.setCommodity_total_price(checkingDetail.getBargain_price().multiply(new BigDecimal(checkingDetail.getCommodity_num())));

                //商品采购单价
                if (purchaseCommodity != null && purchaseCommodity.getPurchase_unit_price() != null) {
                    checkingDetail.setPurchase_unit_price(purchaseCommodity.getPurchase_unit_price());
                }else {
                    checkingDetail.setPurchase_unit_price(commodity.getPurchase_price());
                }
                //商品采购总价 = 采购单价 * 出库数量
                checkingDetail.setPurchase_total_price(checkingDetail.getPurchase_unit_price().multiply(new BigDecimal(checkingDetail.getCommodity_num())));
                checkingExtendMapper.createCheckingDetail(checkingDetail);
            }
        }
        orderFeesCheckingDetail(checking, settlement);
    }

    /**
     * 场地维修对账详情
     * @param checking
     * @param settlement
     */
    private void sampleFixCheckingDetail(Checking checking, SettlementExtend settlement) {
        //售后费用对账单
        List<OrderFeeExtend> orderFees = settlement.getOrderFees();
        if (orderFees != null && orderFees.size() >0) {
            for (int j = 0; j < orderFees.size(); j++) {
                OrderFeeExtend orderFee = orderFees.get(j);
                CheckingDetail checkingDetail = checkingDetailInit(checking, settlement, ConstantCheckingType.CHECKING_DETAIL_TYPE_FEE);
                checkingDetail.setFee_type(orderFee.getOrder_fee_type_id());
                checkingDetail.setFhc_fee(orderFee.getFhc_fee() == null ? new BigDecimal("0") : orderFee.getFhc_fee());
                checkingDetail.setSupplier_fee(orderFee.getSupplier_fee() == null ? new BigDecimal("0") : orderFee.getSupplier_fee());
                checkingDetail.setFee(checkingDetail.getFhc_fee().add(checkingDetail.getSupplier_fee()));
                checkingDetail.setSample_fix_id(settlement.getObj_id());
                checkingExtendMapper.createCheckingDetail(checkingDetail);
            }
        }
    }

    /**
     * 退换货对账详情
     * @param checking
     * @param settlement
     */
    private void returnCommodityCheckingDetail(Checking checking, SettlementExtend settlement) {
        //TODO 退换货商品
        List<OrderReturnCommodityExtend> orderReturnCommodities = settlement.getOrderReturnCommodities();
        for (int i = 0; i < orderReturnCommodities.size(); i++) {
            OrderReturnCommodityExtend orderReturnCommodity = orderReturnCommodities.get(i);
            if (orderReturnCommodity.getOrder_return_commodity_type() != OrderConstants.ORDER_RETURN_COMMODITY_TYPE_ADD) {
                Commodity commodity = CommodityComponent.getCommodityById(orderReturnCommodity.getCommodity_id());
                if (commodity.getSupplier_id().equals(settlement.getSupplier_id())) {
                    //退货商品
                    /*BigDecimal purchase_unit_price = null;
                    SettlementCommodityPurchase settlementCommodityPurchase = orderReturnCommodity.getSettlementCommodityPurchase();
                    if (settlementCommodityPurchase != null && settlementCommodityPurchase.getPurchase_price() != null) {
                        purchase_unit_price = settlementCommodityPurchase.getPurchase_price();
                    }
                    if (purchase_unit_price == null) {
                        PurchaseCommodity purchaseCommodity = orderReturnCommodity.getPurchaseCommodity();
                        if (purchaseCommodity != null && purchaseCommodity.getPurchase_unit_cost_price() != null) {
                            purchase_unit_price = purchaseCommodity.getPurchase_unit_cost_price();
                        }else {
                            purchase_unit_price = commodity.getPurchase_price();
                        }
                    }*/

                    Integer order_return_commodity_num = orderReturnCommodity.getOrder_return_commodity_num();
                    BigDecimal purchase_unit_price = new BigDecimal("0").subtract(purchaseUnitPrice(orderReturnCommodity.getSettlementCommodityPurchase(),
                            orderReturnCommodity.getPurchaseCommodity(), commodity));
                    BigDecimal return_commodity_amount = purchase_unit_price.multiply(new BigDecimal(order_return_commodity_num));
                    CheckingDetail checkingDetail = checkingDetailInit(checking, settlement, ConstantCheckingType.CHECKING_DETAIL_TYPE_COMMODITY);
                    checkingDetail.setCommodity_id(commodity.getCommodity_id());
                    checkingDetail.setCommodity_num(order_return_commodity_num);//出库数量
                    BigDecimal zero = new BigDecimal("0");
                    checkingDetail.setRevision_fee(zero.subtract(orderReturnCommodity.getRevision_fee()));//改版费
                    checkingDetail.setBargain_price(zero.subtract(orderReturnCommodity.getBargain_price()));//商品成交价
                    //商品成交总价 = （成交单价 + 改版费） * 出库数量
                    checkingDetail.setCommodity_total_price((checkingDetail.getBargain_price().
                            add(checkingDetail.getRevision_fee())).multiply(new BigDecimal(checkingDetail.getCommodity_num())));
                    //商品采购单价
                    checkingDetail.setPurchase_unit_price(purchase_unit_price);
                    //商品采购总价 = 采购单价 * 出库数量
                    checkingDetail.setPurchase_total_price(return_commodity_amount);
                    checkingExtendMapper.createCheckingDetail(checkingDetail);
                }
            }
        }
        orderFeesCheckingDetail(checking, settlement);
    }

    /**
     * 赠品对账单
     * @param checking
     * @param settlement
     */
    private void giftCommodityCheckingDetail(Checking checking, SettlementExtend settlement) {
        List<GiftCommodityExtend> giftCommodityExtends = settlement.getGiftCommodityExtendList();
        for (int i = 0; i < giftCommodityExtends.size(); i++) {
            GiftCommodityExtend giftCommodityExtend = giftCommodityExtends.get(i);
            Commodity commodity = CommodityComponent.getCommodityById(giftCommodityExtend.getCommodity_id());
            //退货商品
            BigDecimal purchase_unit_price = null;
            SettlementCommodityPurchase settlementCommodityPurchase = giftCommodityExtend.getSettlementCommodityPurchase();
            if (settlementCommodityPurchase != null && settlementCommodityPurchase.getPurchase_price() != null) {
                purchase_unit_price = settlementCommodityPurchase.getPurchase_price();
            }
            if (purchase_unit_price == null) {
                purchase_unit_price = commodity.getPurchase_price();
            }

            Integer gift_commodity_num = giftCommodityExtend.getGift_commodity_num();
            BigDecimal return_commodity_amount = purchase_unit_price.multiply(new BigDecimal(gift_commodity_num));
            CheckingDetail checkingDetail = checkingDetailInit(checking, settlement, ConstantCheckingType.CHECKING_DETAIL_TYPE_COMMODITY);
            checkingDetail.setCommodity_id(commodity.getCommodity_id());
            checkingDetail.setCommodity_num(gift_commodity_num);//出库数量
            checkingDetail.setRevision_fee(new BigDecimal("0"));//改版费
            checkingDetail.setBargain_price(new BigDecimal("0"));//商品成交价
            //商品成交总价 = （成交单价 + 改版费） * 出库数量
            checkingDetail.setCommodity_total_price((checkingDetail.getBargain_price().
                    add(checkingDetail.getRevision_fee())).multiply(new BigDecimal(checkingDetail.getCommodity_num())));
            //商品采购单价
            checkingDetail.setPurchase_unit_price(new BigDecimal("0").subtract(purchase_unit_price));
            //商品采购总价 = 采购单价 * 出库数量
            checkingDetail.setPurchase_total_price(new BigDecimal("0").subtract(return_commodity_amount));
            checkingExtendMapper.createCheckingDetail(checkingDetail);
        }
    }

    /**
     * 赠品退还对账
     * @param checking
     * @param settlement
     */
    private void giftReturnCommodityCheckingDetail(Checking checking, SettlementExtend settlement) {
        //TODO 退换货商品
        List<GiftReturnCommodityExtend> giftReturnCommodities = settlement.getGiftReturnCommodities();
        for (int i = 0; i < giftReturnCommodities.size(); i++) {
            GiftReturnCommodityExtend giftReturnCommodityExtend = giftReturnCommodities.get(i);
            GiftCommodity giftCommodity = giftReturnCommodityExtend.getGiftCommodity();
            Commodity commodity = CommodityComponent.getCommodityById(giftCommodity.getCommodity_id());
            //退货商品
            BigDecimal purchase_unit_price = null;
            SettlementCommodityPurchase settlementCommodityPurchase = giftReturnCommodityExtend.getSettlementCommodityPurchase();
            if (settlementCommodityPurchase != null && settlementCommodityPurchase.getPurchase_price() != null) {
                purchase_unit_price = settlementCommodityPurchase.getPurchase_price();
            }
            if (purchase_unit_price == null) {
                purchase_unit_price = commodity.getPurchase_price();
            }

            Integer return_num = giftReturnCommodityExtend.getReturn_num();
            purchase_unit_price = new BigDecimal("0").subtract(purchase_unit_price);
            BigDecimal return_commodity_amount = purchase_unit_price.multiply(new BigDecimal(return_num));

            CheckingDetail checkingDetail = checkingDetailInit(checking, settlement, ConstantCheckingType.CHECKING_DETAIL_TYPE_COMMODITY);
            checkingDetail.setCommodity_id(commodity.getCommodity_id());
            checkingDetail.setCommodity_num(return_num);//出库数量
            checkingDetail.setRevision_fee(new BigDecimal("0"));//改版费
            checkingDetail.setBargain_price(new BigDecimal("0"));//商品成交价
            //商品成交总价 = （成交单价 + 改版费） * 出库数量
            checkingDetail.setCommodity_total_price((checkingDetail.getBargain_price().
                    add(checkingDetail.getRevision_fee())).multiply(new BigDecimal(checkingDetail.getCommodity_num())));
            //商品采购单价
            checkingDetail.setPurchase_unit_price(purchase_unit_price);
            //商品采购总价 = 采购单价 * 出库数量
            checkingDetail.setPurchase_total_price(return_commodity_amount);
            checkingExtendMapper.createCheckingDetail(checkingDetail);
        }
    }

    /**
     * 出库对账单详情
     * @param checking
     * @param settlement
     */
    private void purchaseArroval(Checking checking, SettlementExtend settlement) {
        PurchaseCommodityExtend purchaseCommodityExtend = settlement.getPurchaseCommodity();
        OrderCommodity orderCommodity = orderService.getOrderCommodity(purchaseCommodityExtend.getOrder_commodity_id());
        CommodityExtend commodity = CommodityComponent.getCommodityById(orderCommodity.getCommodity_id());
        SettlementCommodityPurchase settlementCommodityPurchase = purchaseCommodityExtend.getSettlementCommodityPurchase();
        CheckingDetail checkingDetail = checkingDetailInit(checking, settlement, ConstantCheckingType.CHECKING_DETAIL_TYPE_COMMODITY);
        checkingDetail.setOrder_commodity_id(purchaseCommodityExtend.getOrder_commodity_id());
        checkingDetail.setCommodity_id(purchaseCommodityExtend.getCommodity_id());
        checkingDetail.setCommodity_num(purchaseCommodityExtend.getPurchase_num());//出库数量
        checkingDetail.setRevision_fee(orderCommodity.getRevision_fee());//改版费
        checkingDetail.setBargain_price(orderCommodity.getBargain_price());//商品成交价
        //商品成交总价 = （成交单价 + 改版费） * 出库数量
        checkingDetail.setCommodity_total_price((checkingDetail.getBargain_price().add(checkingDetail.getRevision_fee())).multiply(new BigDecimal(checkingDetail.getCommodity_num())));
        //商品采购单价
        checkingDetail.setPurchase_unit_price(purchaseUnitPrice(settlementCommodityPurchase, purchaseCommodityExtend, commodity));
        //商品采购总价 = 采购单价 * 出库数量
        checkingDetail.setPurchase_total_price(checkingDetail.getPurchase_unit_price().multiply(new BigDecimal(checkingDetail.getCommodity_num())));
        checkingExtendMapper.createCheckingDetail(checkingDetail);
        orderFeesCheckingDetail(checking, settlement);
    }

    /**
     *
     * @param settlement
     * @return
     */
    private CheckingDetail checkingDetailInit(Checking checking, Settlement settlement, int checking_detail_type) {
        CheckingDetail checkingDetail = new CheckingDetail();
        checkingDetail.setChecking_id(checking.getChecking_id());
        checkingDetail.setSettlement_id(settlement.getSettlement_id());
        checkingDetail.setChecking_detail_type(checking_detail_type);
        checkingDetail.setOrder_id(settlement.getOrder_id());

        return checkingDetail;
    }

    /**
     *
     * @return
     */
    private String checkingCode(String checking_code) {
        String n = null;
        if (checking_code == null) {
            String s = ConstantUtils.CHECKING_CODE_HEADER + DateUtil.format(DateUtil.currentDate(), "yyMMdd") + "[0-9]{3}";
            n = checkingExtendMapper.todayCheckingNum(s);//DZD161031001
        }else {
            n = checking_code;
        }
        if (n == null) {
            return ConstantUtils.CHECKING_CODE_HEADER + Long.parseLong(DateUtil.format(DateUtil.currentDate(), "yyMMdd"))*100+1l;
        }
        n = n.substring(3, n.length());
        int m = Integer.parseInt(n);
        m++;
        return ConstantUtils.CHECKING_CODE_HEADER + m;
    }

    public Integer countChecking(CheckingSearchOptions checkingSearchOptions) {
        return checkingExtendMapper.countChecking(checkingSearchOptions);
    }

    public CheckingExtend getCheckingById(int checking_id) {
        CheckingExtend checking = checkingExtendMapper.getCheckingById(checking_id);
        checking.setCheckingDetails(checkingDetails(checking_id));
        return checking;
    }

    public List<CheckingExtend> checkingList(CheckingSearchOptions checkingSearchOptions) {
        List<CheckingExtend> list = new ArrayList<>();
        List<Integer> checkingIds = checkingExtendMapper.checkingIds(checkingSearchOptions);
        if (checkingIds == null || checkingIds.size() == 0) {
            return list;
        }
        list = checkingExtendMapper.checkingList(checkingIds);
        for (int i = 0; i < list.size(); i++) {
            CheckingExtend checkingExtend = list.get(i);
            checkingExtend.setCheckingDetails(checkingDetails(checkingExtend.getChecking_id()));
        }
        return list;
    }

    public List<CheckingDetailExtend> checkingDetails(int checking_id) {
        CheckingSearchOptions checkingSearchOptions = new CheckingSearchOptions();
        checkingSearchOptions.setChecking_id(checking_id);
        return checkingDetails(checkingSearchOptions);
    }

    public List<CheckingDetailExtend> checkingDetails(CheckingSearchOptions checkingSearchOptions) {
        return checkingExtendMapper.checkingDetailList(checkingSearchOptions);
    }

    public Integer countCheckingDetails(CheckingSearchOptions checkingSearchOptions) {
        return checkingExtendMapper.countCheckingDetails(checkingSearchOptions);
    }

    public List<CheckingExtend> checkingEasyList(CheckingSearchOptions checkingSearchOptions) {
        List<CheckingExtend> list = new ArrayList<>();
        List<Integer> checkingIds = checkingExtendMapper.checkingIds(checkingSearchOptions);
        if (checkingIds == null || checkingIds.size() == 0) {
            return list;
        }
        for (int i = 0; i < checkingIds.size(); i++) {
            int checking_id = checkingIds.get(i);
            CheckingExtend checking = checkingExtendMapper.getCheckingById(checking_id);
            list.add(checking);
        }
        return list;
    }

    public void updateChecking(Checking checking) {
        checkingMapper.updateByPrimaryKey(checking);
    }

    @Transactional
    public void checkingReset(CheckingExtend checking, Employee employee) {
        String check_remark = checking.getCheck_remark();
        List<Integer> settlementIds = new ArrayList<>();
        List<CheckingDetailExtend> checkingDetails = checking.getCheckingDetails();
        for (int i = 0; i < checkingDetails.size(); i++) {
            CheckingDetailExtend checkingDetailExtend = checkingDetails.get(i);
            Integer settlement_id = checkingDetailExtend.getSettlement_id();
            settlementIds.add(settlement_id);
            checkingDetailMapper.deleteByPrimaryKey(checkingDetailExtend.getChecking_detail_id());
        }

        SettlementSearchOption settlementSearchOption = new SettlementSearchOption();
        settlementSearchOption.setSettlementIdList(settlementIds);
        List<SettlementExtend> settlementExtends = settlementService.settlementList(settlementSearchOption);

        checkingMapper.deleteByPrimaryKey(checking.getChecking_id());

        create(settlementExtends, employee, check_remark, true);
    }

}
