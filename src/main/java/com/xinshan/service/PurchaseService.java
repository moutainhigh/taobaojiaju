package com.xinshan.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.order.OrderComponents;
import com.xinshan.components.purchase.PurchaseComponent;
import com.xinshan.dao.OrderCommodityMapper;
import com.xinshan.dao.OrderReturnMapper;
import com.xinshan.dao.PurchaseCommodityMapper;
import com.xinshan.dao.PurchaseMapper;
import com.xinshan.dao.extend.order.OrderExtendMapper;
import com.xinshan.dao.extend.purchase.PurchaseExtendMapper;
import com.xinshan.model.*;
import com.xinshan.model.extend.gift.GiftCommodityExtend;
import com.xinshan.model.extend.gift.GiftExtend;
import com.xinshan.model.extend.inventory.InventoryInCommodityExtend;
import com.xinshan.model.extend.order.OrderCommodityExtend;
import com.xinshan.model.extend.order.OrderExtend;
import com.xinshan.model.extend.order.OrderReturnCommodityExtend;
import com.xinshan.model.extend.order.OrderReturnExtend;
import com.xinshan.model.extend.purchase.PurchaseCommodityExtend;
import com.xinshan.model.extend.purchase.PurchaseExtend;
import com.xinshan.model.extend.purchase.PurchaseInCommodity;
import com.xinshan.model.extend.purchase.PurchaseReports;
import com.xinshan.utils.DateUtil;
import com.xinshan.pojo.inventory.InventorySearchOption;
import com.xinshan.pojo.purchase.PurchaseSearchOption;
import com.xinshan.utils.constant.order.OrderConstants;
import com.xinshan.utils.constant.purchase.PurchaseConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by mxt on 16-11-2.
 */
@Service
public class PurchaseService {
    @Autowired
    private CommodityService commodityService;
    @Autowired
    private PurchaseExtendMapper purchaseExtendMapper;
    @Autowired
    private OrderExtendMapper orderExtendMapper;
    @Autowired
    private PurchaseCommodityMapper purchaseCommodityMapper;
    @Autowired
    private PurchaseMapper purchaseMapper;
    @Autowired
    private OrderCommodityMapper orderCommodityMapper;
    @Autowired
    private InventoryOutService inventoryOutService;
    @Autowired
    private InventoryInService inventoryInService;
    @Autowired
    private SettlementService settlementService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private OrderReturnMapper orderReturnMapper;
    @Autowired
    private GiftService giftService;
    @Autowired
    private UserBringUpService userBringUpService;
    /**
     * 添加
     * @param orderExtend
     */
    private void orderPurchase(OrderExtend orderExtend, Employee employee, String purchase_remark, Date estimate_arrival_date) {
        List<OrderCommodityExtend> list = orderExtend.getOrderCommodities();
        Map<Integer, List<Object[]>> map = new HashMap<>();//供应商id，商品id，商品数量
        Map<Integer, OrderCommodity> orderCommodityMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            OrderCommodityExtend orderCommodity = list.get(i);
            if (orderCommodity.getSample() != null && orderCommodity.getSample() == 1) {//样品，不转化采购单
                //直接生成出库单
                orderCommodityMap.put(orderCommodity.getOrder_commodity_id(), orderCommodity);
                continue;
            }

            int num = orderCommodity.getCommodity_num();
            Commodity commodity = commodityService.getCommodityById(orderCommodity.getCommodity_id());
            if (commodity.getSupplier_id() == null) {
                continue;
            }

            if (commodity.getSichuan() != null && commodity.getSichuan() == 1) {//四川馆模式，不生成采购单
                continue;
            }

            if (map.containsKey(commodity.getSupplier_id())) {
                List<Object[]> list1 = map.get(commodity.getSupplier_id());
                list1.add(new Object[]{commodity.getCommodity_id(), num, orderCommodity.getOrder_commodity_remark(), orderCommodity.getOrder_commodity_id(), orderCommodity.getSample()});
                map.put(commodity.getSupplier_id(), list1);
            }else {
                List<Object[]> list1 = new ArrayList<>();
                list1.add(new Object[]{commodity.getCommodity_id(), num, orderCommodity.getOrder_commodity_remark(), orderCommodity.getOrder_commodity_id(), orderCommodity.getSample()});
                map.put(commodity.getSupplier_id(), list1);
            }

            orderCommodity.setOrder_commodity_status(OrderConstants.ORDER_COMMODITY_STATUS_PURCHASE);
            orderCommodityMap.put(orderCommodity.getOrder_commodity_id(), orderCommodity);
        }
        Iterator<Map.Entry<Integer,List<Object[]>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, List<Object[]>> entry = iterator.next();
            int supplier_id = entry.getKey();
            Purchase purchase = new Purchase();
            purchase.setSupplier_id(supplier_id);
            purchase.setPurchase_status(0);
            purchase.setPurchase_start_date(DateUtil.currentDate());
            purchase.setPurchase_code(purchaseCode(null));
            purchase.setOrder_id(orderExtend.getOrder_id());
            purchase.setRecord_employee_code(employee.getEmployee_code());
            purchase.setRecord_employee_name(employee.getEmployee_name());
            purchase.setPurchase_remark(purchase_remark);
            purchase.setEstimate_arrival_date(estimate_arrival_date);
            purchase.setPurchase_type(PurchaseConstant.PURCHASE_TYPE_ORDER);
            purchaseExtendMapper.createPurchase(purchase);
            createPurchaseCommodity(purchase, entry.getValue(), PurchaseConstant.PURCHASE_COMMODITY_TYPE_ORDER);
        }

        Iterator<Map.Entry<Integer, OrderCommodity>> ite = orderCommodityMap.entrySet().iterator();
        while (ite.hasNext()) {
            Map.Entry<Integer, OrderCommodity> entry = ite.next();
            OrderCommodity orderCommodity = entry.getValue();
            if (orderCommodity.getSample() == 1) {//样品初始状态400
                orderCommodity.setOrder_commodity_status(OrderConstants.ORDER_COMMODITY_STATUS_ALL_ARRIVAL);
            }
            orderCommodity.setOrder_commodity_return_num(0);
            orderCommodity.setOrder_commodity_num(orderCommodity.getCommodity_num());
            orderCommodityMapper.updateByPrimaryKey(orderCommodity);
        }

        orderExtend.setTrans_purchase(1);
        orderExtendMapper.updateTransPurchase(orderExtend);
        userBringUpService.createBringUp(orderExtend, employee);
    }

    private void createPurchaseCommodity(Purchase purchase, List<Object[]> list1, int purchase_commodity_type) {
        Iterator<Object[]> ite = list1.iterator();
        while (ite.hasNext()) {
            Object[] o = ite.next();
            Integer commodity_id = (int) o[0];
            Integer num = (int) o[1];
            String remark = (String) o[2];
            Integer order_commodity_id = (int) o[3];
            Integer sample = (int) o[4];
            PurchaseCommodity purchaseCommodity = new PurchaseCommodity();
            purchaseCommodity.setCommodity_id(commodity_id);
            purchaseCommodity.setPurchase_id(purchase.getPurchase_id());
            purchaseCommodity.setPurchase_num(num);
            purchaseCommodity.setPurchase_arrival_num(0);
            if (purchaseCommodity.getPurchase_unit_price() != null) {
                purchaseCommodity.setPurchase_total_price(purchaseCommodity.getPurchase_unit_price().multiply(new BigDecimal(num)));
            }else {
                purchaseCommodity.setPurchase_unit_price(new BigDecimal("0"));
                purchaseCommodity.setPurchase_total_price(new BigDecimal("0"));
            }
            purchaseCommodity.setPurchase_commodity_status(0);
            purchaseCommodity.setInventory_in_num(0);
            purchaseCommodity.setPurchase_commodity_remark(remark);
            purchaseCommodity.setOrder_commodity_id(order_commodity_id);
            purchaseCommodity.setSample(sample);
            purchaseCommodity.setPurchase_cost_price(new BigDecimal("0"));
            purchaseCommodity.setPurchase_unit_cost_price(new BigDecimal("0"));
            purchaseCommodity.setPurchase_commodity_type(purchase_commodity_type);
            purchaseExtendMapper.createPurchaseCommodity(purchaseCommodity);
        }
    }

    @Transactional
    public synchronized void createPurchase(Object o, String purchase_remark, Date estimate_arrival_date,
                                            Employee employee) {
        if (o instanceof OrderExtend) {
            OrderExtend orderExtend = (OrderExtend) o;
            orderPurchase(orderExtend, employee, purchase_remark, estimate_arrival_date);
            inventoryOutService.createInventoryOut(orderExtend, employee);
        }else if (o instanceof JSONObject) {
            createPurchase((JSONObject) o, employee);
        }else if (o instanceof OrderReturnExtend) {
            OrderReturnExtend orderReturnExtend = (OrderReturnExtend) o;
            orderReturnPurchase(orderReturnExtend, employee, purchase_remark, estimate_arrival_date);
            inventoryOutService.createInventoryOut(orderReturnExtend, employee);
        }else if (o instanceof GiftExtend) {
            GiftExtend giftExtend = (GiftExtend) o;
            giftPurchase(giftExtend, employee, purchase_remark, estimate_arrival_date);
            inventoryOutService.createInventoryOut(giftExtend, employee);
        }
    }

    private void createPurchase(JSONObject jsonObject, Employee employee) {
        String purchase_remark = null;
        if (jsonObject.get("purchase_remark") != null) {
            purchase_remark = jsonObject.get("purchase_remark").toString().trim();
        }
        Date estimate_arrival_date = null;
        if (jsonObject.get("estimate_arrival_date") != null) {
            estimate_arrival_date = DateUtil.stringToDate(jsonObject.get("estimate_arrival_date").toString().trim());
        }
        JSONArray jsonArray = JSONArray.parseArray(jsonObject.get("commodities").toString());
        Iterator iterator = jsonArray.iterator();
        Map<Integer, List<Integer[]>> map = new HashMap<>();
        while (iterator.hasNext()) {
            String s = iterator.next().toString();
            JSONObject jsonObject1 = JSONObject.parseObject(s);
            int commodity_id = Integer.parseInt(jsonObject1.get("commodity_id").toString().trim());
            Commodity commodity = commodityService.getCommodityById(commodity_id);
            int sample = Integer.parseInt(jsonObject1.get("sample").toString().trim());
            List<Integer[]> list = map.get(commodity.getSupplier_id());
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(new Integer[]{commodity_id, sample});
            map.put(commodity.getSupplier_id(), list);
        }

        String purchaseCode = purchaseCode(null);
        Iterator<Map.Entry<Integer, List<Integer[]>>> ite = map.entrySet().iterator();
        while (ite.hasNext()) {
            Map.Entry<Integer, List<Integer[]>> entry = ite.next();
            int supplier_id = entry.getKey();
            Purchase purchase = new Purchase();
            purchase.setEstimate_arrival_date(estimate_arrival_date);
            purchase.setPurchase_remark(purchase_remark);
            purchase.setPurchase_code(purchaseCode);
            purchase.setRecord_employee_name(employee.getEmployee_name());
            purchase.setRecord_employee_code(employee.getEmployee_code());
            purchase.setPurchase_status(0);
            purchase.setSupplier_id(supplier_id);
            purchase.setPurchase_start_date(DateUtil.currentDate());
            purchase.setPurchase_type(PurchaseConstant.PURCHASE_TYPE_PURCHASE);
            purchaseExtendMapper.createPurchase(purchase);

            List<Integer[]> list = entry.getValue();
            for (int i = 0; i < list.size(); i++) {
                Integer[] m = list.get(i);
                int commodity_id = m[0];
                int sample = m[1];
                PurchaseCommodity purchaseCommodity = new PurchaseCommodity();
                purchaseCommodity.setPurchase_id(purchase.getPurchase_id());
                purchaseCommodity.setCommodity_id(commodity_id);
                purchaseCommodity.setSample(sample);
                purchaseCommodity.setPurchase_commodity_status(0);
                purchaseCommodity.setPurchase_arrival_num(0);
                purchaseCommodity.setInventory_in_num(0);
                purchaseCommodity.setPurchase_commodity_type(1);
                purchaseCommodity.setPurchase_unit_price(new BigDecimal("0"));
                purchaseCommodity.setPurchase_total_price(new BigDecimal("0"));
                purchaseCommodity.setPurchase_commodity_type(PurchaseConstant.PURCHASE_COMMODITY_TYPE_PURCHASE);
                purchaseExtendMapper.createPurchaseCommodity(purchaseCommodity);
            }
            purchaseCode = purchaseCode(purchaseCode);
        }
    }

    /**
     * 退换货新增商品生成采购单
     * @param orderReturnExtend
     */
    private void orderReturnPurchase(OrderReturnExtend orderReturnExtend, Employee employee, String purchase_remark, Date estimate_arrival_date) {
        List<OrderReturnCommodityExtend> orderReturnCommodityExtends = orderReturnExtend.getOrderReturnCommodities();
        Map<Integer, List<Object[]>> map = new HashMap<>();//供应商id，商品id，商品数量
        Map<Integer, OrderCommodity> orderCommodityMap = new HashMap<>();
        int order_id = orderReturnExtend.getOrder_id();
        for (int i = 0; i < orderReturnCommodityExtends.size(); i++) {
            OrderReturnCommodityExtend orderReturnCommodity = orderReturnCommodityExtends.get(i);
            if (orderReturnCommodity.getOrder_return_commodity_type() != OrderConstants.ORDER_RETURN_COMMODITY_TYPE_ADD) {
                continue;
            }
            int num = orderReturnCommodity.getOrder_return_commodity_num();
            Commodity commodity = commodityService.getCommodityById(orderReturnCommodity.getCommodity_id());
            if (commodity.getSupplier_id() == null) {
                continue;
            }

            if (commodity.getSichuan() != null && commodity.getSichuan() == 1) {//四川馆模式，不生成采购单
                continue;
            }

            int order_commodity_id = orderReturnCommodity.getOrder_commodity_id();
            OrderCommodity orderCommodity = orderCommodityMapper.selectByPrimaryKey(order_commodity_id);
            if (orderCommodity == null) {
                continue;
            }
            if (orderReturnCommodity.getSample() != null && orderReturnCommodity.getSample() == 1) {//样品，不转化采购单
                //直接生成出库单
                orderCommodityMap.put(orderCommodity.getOrder_commodity_id(), orderCommodity);
                continue;
            }
            if (map.containsKey(commodity.getSupplier_id())) {
                List<Object[]> list = map.get(commodity.getSupplier_id());
                list.add(new Object[]{commodity.getCommodity_id(), num, orderCommodity.getOrder_commodity_remark(), orderCommodity.getOrder_commodity_id(), orderCommodity.getSample()});
                map.put(commodity.getSupplier_id(), list);
            }else {
                List<Object[]> list = new ArrayList<>();
                list.add(new Object[]{commodity.getCommodity_id(), num, orderCommodity.getOrder_commodity_remark(), orderCommodity.getOrder_commodity_id(), orderCommodity.getSample()});
                map.put(commodity.getSupplier_id(), list);
            }

            orderCommodity.setOrder_commodity_status(OrderConstants.ORDER_COMMODITY_STATUS_PURCHASE);
            orderCommodityMap.put(orderCommodity.getOrder_commodity_id(), orderCommodity);
        }

        Iterator<Map.Entry<Integer,List<Object[]>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, List<Object[]>> entry = iterator.next();
            int supplier_id = entry.getKey();
            Purchase purchase = new Purchase();
            purchase.setSupplier_id(supplier_id);
            purchase.setPurchase_status(0);
            purchase.setPurchase_start_date(DateUtil.currentDate());
            purchase.setPurchase_code(purchaseCode(null));
            purchase.setOrder_id(order_id);
            purchase.setRecord_employee_code(employee.getEmployee_code());
            purchase.setRecord_employee_name(employee.getEmployee_name());
            purchase.setPurchase_remark(purchase_remark);
            purchase.setEstimate_arrival_date(estimate_arrival_date);
            purchase.setOrder_return_id(orderReturnExtend.getOrder_return_id());
            purchase.setPurchase_type(PurchaseConstant.PURCHASE_TYPE_ORDER_RETURN);
            purchaseExtendMapper.createPurchase(purchase);

            createPurchaseCommodity(purchase, entry.getValue(), PurchaseConstant.PURCHASE_COMMODITY_TYPE_RETURN);
        }

        Iterator<Map.Entry<Integer, OrderCommodity>> ite = orderCommodityMap.entrySet().iterator();
        while (ite.hasNext()) {
            Map.Entry<Integer, OrderCommodity> entry = ite.next();
            OrderCommodity orderCommodity = entry.getValue();
            if (orderCommodity.getSample() == 1) {//样品初始状态400
                orderCommodity.setOrder_commodity_status(OrderConstants.ORDER_COMMODITY_STATUS_ALL_ARRIVAL);
            }
            orderCommodity.setOrder_commodity_return_num(0);
            orderCommodity.setOrder_commodity_num(orderCommodity.getCommodity_num());
            orderCommodityMapper.updateByPrimaryKey(orderCommodity);
        }

        orderReturnExtend.setOrder_return_purchase_status(1);
        orderReturnMapper.updateByPrimaryKey(orderReturnExtend);
    }

    /**
     * 赠品采购
     * @param giftExtend
     * @param employee
     * @param purchase_remark
     * @param estimate_arrival_date
     */
    public void giftPurchase(GiftExtend giftExtend, Employee employee, String purchase_remark, Date estimate_arrival_date) {
        List<GiftCommodityExtend> list = giftExtend.getGiftCommodities();
        Map<Integer, List<Object[]>> map = new HashMap<>();//供应商id，商品id，商品数量
        Map<Integer, GiftCommodityExtend> orderCommodityMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            GiftCommodityExtend giftCommodityExtend = list.get(i);
            int gift_commodity_sample = giftCommodityExtend.getGift_commodity_sample();
            if (gift_commodity_sample == 1) {//样品，不转化采购单
                //直接生成出库单
                orderCommodityMap.put(giftCommodityExtend.getGift_commodity_id(), giftCommodityExtend);
                continue;
            }

            int num = giftCommodityExtend.getGift_commodity_num();
            Commodity commodity = commodityService.getCommodityById(giftCommodityExtend.getCommodity_id());
            if (commodity.getSupplier_id() == null) {
                continue;
            }

            if (commodity.getSichuan() != null && commodity.getSichuan() == 1) {//四川馆模式，不生成采购单
                continue;
            }
            Object[] o = {commodity.getCommodity_id(),
                    num,
                    giftCommodityExtend.getGift_commodity_remark(),
                    giftCommodityExtend.getGift_commodity_id(),
                    gift_commodity_sample};
            if (map.containsKey(commodity.getSupplier_id())) {
                List<Object[]> list1 = map.get(commodity.getSupplier_id());
                list1.add(o);
                map.put(commodity.getSupplier_id(), list1);
            }else {
                List<Object[]> list1 = new ArrayList<>();
                list1.add(o);
                map.put(commodity.getSupplier_id(), list1);
            }

            giftCommodityExtend.setGift_commodity_out_status(OrderConstants.ORDER_COMMODITY_STATUS_PURCHASE);
            orderCommodityMap.put(giftCommodityExtend.getGift_commodity_id(), giftCommodityExtend);
        }
        Iterator<Map.Entry<Integer,List<Object[]>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, List<Object[]>> entry = iterator.next();
            int supplier_id = entry.getKey();
            Purchase purchase = new Purchase();
            purchase.setSupplier_id(supplier_id);
            purchase.setPurchase_status(0);
            purchase.setPurchase_start_date(DateUtil.currentDate());
            purchase.setPurchase_code(purchaseCode(null));
            purchase.setOrder_id(giftExtend.getOrder_id());
            purchase.setRecord_employee_code(employee.getEmployee_code());
            purchase.setRecord_employee_name(employee.getEmployee_name());
            purchase.setPurchase_remark(purchase_remark);
            purchase.setEstimate_arrival_date(estimate_arrival_date);
            purchase.setPurchase_type(PurchaseConstant.PURCHASE_TYPE_GIFT);
            purchase.setGift_id(giftExtend.getGift_id());
            purchaseExtendMapper.createPurchase(purchase);
            Iterator<Object[]> ite = entry.getValue().iterator();
            while (ite.hasNext()) {
                Object[] o = ite.next();
                Integer commodity_id = (int) o[0];
                Integer num = (int) o[1];
                String remark = (String) o[2];
                Integer gift_commodity_id = (int) o[3];
                Integer sample = (int) o[4];
                PurchaseCommodity purchaseCommodity = new PurchaseCommodity();
                purchaseCommodity.setCommodity_id(commodity_id);
                purchaseCommodity.setPurchase_id(purchase.getPurchase_id());
                purchaseCommodity.setPurchase_num(num);
                purchaseCommodity.setPurchase_arrival_num(0);
                if (purchaseCommodity.getPurchase_unit_price() != null) {
                    purchaseCommodity.setPurchase_total_price(purchaseCommodity.getPurchase_unit_price().multiply(new BigDecimal(num)));
                }else {
                    purchaseCommodity.setPurchase_unit_price(new BigDecimal("0"));
                    purchaseCommodity.setPurchase_total_price(new BigDecimal("0"));
                }
                purchaseCommodity.setPurchase_commodity_status(0);
                purchaseCommodity.setInventory_in_num(0);
                purchaseCommodity.setPurchase_commodity_remark(remark);
                purchaseCommodity.setGift_commodity_id(gift_commodity_id);
                purchaseCommodity.setSample(sample);
                purchaseCommodity.setPurchase_cost_price(new BigDecimal("0"));
                purchaseCommodity.setPurchase_unit_cost_price(new BigDecimal("0"));
                purchaseCommodity.setPurchase_commodity_type(PurchaseConstant.PURCHASE_COMMODITY_TYPE_GIFT);
                purchaseExtendMapper.createPurchaseCommodity(purchaseCommodity);
            }
        }

        Iterator<Map.Entry<Integer, GiftCommodityExtend>> ite = orderCommodityMap.entrySet().iterator();
        while (ite.hasNext()) {
            Map.Entry<Integer, GiftCommodityExtend> entry = ite.next();
            GiftCommodityExtend giftCommodityExtend = entry.getValue();
            if (giftCommodityExtend.getGift_commodity_sample() == 1) {//样品初始状态400
                giftCommodityExtend.setGift_commodity_out_status(OrderConstants.ORDER_COMMODITY_STATUS_ALL_ARRIVAL);
            }
            giftService.updateGiftCommodity(giftCommodityExtend);
        }

        giftExtend.setGift_purchase_status(1);
        giftService.updateGift(giftExtend);
    }

    private String purchaseCode(String maxCode) {
        if (maxCode == null) {
            maxCode = purchaseExtendMapper.purchaseCode(DateUtil.format(DateUtil.currentDate(), "yyMMdd"));
        }
        if (maxCode == null) {
            return "P"+Long.parseLong(DateUtil.format(DateUtil.currentDate(), "yyMMdd"))*100+1l;
        }
        maxCode = maxCode.substring(1, maxCode.length());
        int m = Integer.parseInt(maxCode);
        m++;
        return "P"+m;
    }

    public List<Integer> purchaseIds(PurchaseSearchOption purchaseSearchOption) {
        return purchaseExtendMapper.purchaseIds(purchaseSearchOption);
    }

    public Integer countPurchase(PurchaseSearchOption purchaseSearchOption) {
        return purchaseExtendMapper.countPurchase(purchaseSearchOption);
    }

    public List<PurchaseExtend> purchaseList(PurchaseSearchOption purchaseSearchOption) {
        List<PurchaseExtend> list = purchaseExtendMapper.purchaseList(purchaseSearchOption);
        for (int i = 0; i < list.size(); i++) {
            PurchaseExtend purchase = list.get(i);
            List<PurchaseCommodityExtend> purchaseCommodityExtends = purchase.getPurchaseCommodities();
            for (int j = 0; j < purchaseCommodityExtends .size(); j++) {
                PurchaseCommodityExtend purchaseCommodityExtend = purchaseCommodityExtends.get(j);
                purchaseCommodityExtend.setCommodity(commodityService.getCommodityById(purchaseCommodityExtend.getCommodity_id()));
                if (purchaseCommodityExtend.getOrder_commodity_id() != null) {
                    int order_commodity_id = purchaseCommodityExtend.getOrder_commodity_id();
                    OrderCommodity orderCommodity = orderCommodityMapper.selectByPrimaryKey(order_commodity_id);
                    purchaseCommodityExtend.setOrderCommodity(orderCommodity);
                    if (orderCommodity != null && orderCommodity.getActivity_commodity_id() != null) {
                        purchaseCommodityExtend.setActivityCommodity(activityService.getActivityCommodityById(orderCommodity.getActivity_commodity_id()));
                    }
                }
            }
        }
        return list;
    }

    public PurchaseExtend getPurchaseById(int purchase_id){
        List<Integer> purchaseIds = new ArrayList<>();
        purchaseIds.add(purchase_id);
        PurchaseSearchOption purchaseSearchOption = new PurchaseSearchOption();
        purchaseSearchOption.setPurchaseIds(purchaseIds);
        List<PurchaseExtend> list = purchaseExtendMapper.purchaseList(purchaseSearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public Purchase getPurchaseById1(int purchase_id) {
        return purchaseMapper.selectByPrimaryKey(purchase_id);
    }

    public PurchaseCommodity getPurchaseCommodityById(int purchase_commodity_id){
        return purchaseCommodityMapper.selectByPrimaryKey(purchase_commodity_id);
    }
    public PurchaseCommodity getPurchaseCommodityByOrderCommodityId(int order_commodity_id) {
        return purchaseExtendMapper.getPurchaseCommodityByOrderCommodityId(order_commodity_id);
    }
    public PurchaseCommodity getPurchaseCommodityByGiftCommodityId(int gift_commodity_id) {
        return purchaseExtendMapper.getPurchaseCommodityByGiftCommodityId(gift_commodity_id);
    }

    @Transactional
    public void updatePurchaseCommodity(PurchaseCommodity purchaseCommodity) {
        purchaseCommodityMapper.updateByPrimaryKey(purchaseCommodity);
        Integer purchase_commodity_type = purchaseCommodity.getPurchase_commodity_type();
        switch (purchase_commodity_type) {
            case PurchaseConstant.PURCHASE_COMMODITY_TYPE_ORDER:
                orderCommodityStatus(purchaseCommodity);
                break;
            case PurchaseConstant.PURCHASE_COMMODITY_TYPE_RETURN:
                orderCommodityStatus(purchaseCommodity);
                break;
            case PurchaseConstant.PURCHASE_COMMODITY_TYPE_PURCHASE:

                break;
            case PurchaseConstant.PURCHASE_COMMODITY_TYPE_GIFT:
                giftCommodityStatus(purchaseCommodity);
                break;
            default:
                break;
        }
        purchaseStatus(purchaseCommodity.getPurchase_id());
    }

    /**
     * 采购状态
     * @param purchase_id
     */
    public void purchaseStatus(int purchase_id) {
        PurchaseExtend purchaseExtend = getPurchaseById(purchase_id);
        List<PurchaseCommodityExtend> list = purchaseExtend.getPurchaseCommodities();
        int status = 4;
        boolean no_purchase = true;
        //到货状态，0新添加，1确认采购，2审核，3部分到货，4全部到货,5不采购
        for (int i = 0; i < list.size(); i++) {
            PurchaseCommodityExtend purchaseCommodity = list.get(i);
            int purchaseCommodityStatus = purchaseCommodity.getPurchase_commodity_status();
            if (purchaseCommodityStatus < status) {
                status = purchaseCommodityStatus;
            }
            if (purchaseCommodityStatus != 5) {
                no_purchase = false;
            }
        }
        purchaseExtend.setPurchase_status(status);
        if (no_purchase) {//全部不采购
            purchaseExtend.setPurchase_status(5);
        }else {
            if (purchaseExtend.getPurchase_status() == 4) {//全部到货
                purchaseExtend.setPurchase_complete_date(DateUtil.currentDate());
            }
        }
        purchaseExtendMapper.purchaseStatus(purchaseExtend);
        if (purchaseExtend.getOrder_id() != null) {
            OrderComponents.orderStep(purchaseExtend.getOrder_id());
        }
    }

    public void orderCommodityStatus(PurchaseCommodity purchaseCommodity) {
        if (purchaseCommodity.getOrder_commodity_id() == null) {
            return;
        }
        int order_commodity_id = purchaseCommodity.getOrder_commodity_id();
        OrderCommodity orderCommodity = orderCommodityMapper.selectByPrimaryKey(order_commodity_id);
        if (orderCommodity != null) {
            //到货状态，0新添加，1确认采购，2审核，3部分到货，4全部到货,5不许要采购
            int orderCommodityStatus = PurchaseComponent.orderCommodityStatus(purchaseCommodity);
            orderCommodity.setOrder_commodity_status(orderCommodityStatus);
            orderCommodityMapper.updateByPrimaryKey(orderCommodity);
        }
    }

    public void giftCommodityStatus(PurchaseCommodity purchaseCommodity) {
        if (purchaseCommodity.getGift_commodity_id() == null) {
            return;
        }
        if (!purchaseCommodity.getPurchase_commodity_type().equals(PurchaseConstant.PURCHASE_COMMODITY_TYPE_GIFT)) {
            return;
        }
        int gift_commodity_id = purchaseCommodity.getGift_commodity_id();
        GiftCommodity giftCommodity = giftService.getGiftCommodityById(gift_commodity_id);
        if (giftCommodity != null) {
            //到货状态，0新添加，1确认采购，2审核，3部分到货，4全部到货,5不许要采购
            int orderCommodityStatus = PurchaseComponent.orderCommodityStatus(purchaseCommodity);
            giftCommodity.setGift_commodity_out_status(orderCommodityStatus);
            giftService.updateGiftCommodity(giftCommodity);
        }
    }

    @Transactional
    public void confirmArrival(PurchaseCommodity purchaseCommodity, Employee employee, int purchase_arrival_num) {
        purchaseCommodityMapper.updateByPrimaryKey(purchaseCommodity);
        createArrival(purchase_arrival_num, employee, null, purchaseCommodity.getPurchase_commodity_id());
        List<InventoryInCommodityExtend> list = inventoryInService.inventoryInCommoditiesByPid(purchaseCommodity.getPurchase_commodity_id());
        if (list != null && list.size() > 0) {
            InventoryInCommodity inventoryInCommodity = list.get(0);
            if (purchaseCommodity.getPurchase_commodity_status() == 4) {//3全部到货
                inventoryInCommodity.setInventory_in_status(1);//入库状态，0初始状态，1可确认入库，2已确认入库
            }else {
                inventoryInCommodity.setInventory_in_status(0);
            }
            inventoryInService.updateInventoryInCommodity(inventoryInCommodity);
        }
        orderCommodityStatus(purchaseCommodity);
        purchaseStatus(purchaseCommodity.getPurchase_id());
    }

    private void createArrival(int arrival_num, Employee employee, String remark, int purchase_commodity_id) {
        PurchaseCommodityArrival purchaseCommodityArrival = new PurchaseCommodityArrival();
        purchaseCommodityArrival.setArrival_employee_code(employee.getEmployee_code());
        purchaseCommodityArrival.setArrival_employee_name(employee.getEmployee_name());
        purchaseCommodityArrival.setPurchase_commodity_arrival_num(arrival_num);
        purchaseCommodityArrival.setPurchase_commodity_arrival_remark(remark);
        purchaseCommodityArrival.setPurchase_commodity_id(purchase_commodity_id);
        purchaseExtendMapper.createPurchaseCommodityArrival(purchaseCommodityArrival);
    }

    public List<PurchaseReports> purchaseReportses(PurchaseSearchOption purchaseSearchOption) {
        List<PurchaseReports> list = purchaseExtendMapper.purchaseReportses(purchaseSearchOption);
        for (int i = 0; i < list.size(); i++) {
            PurchaseReports purchaseReports = list.get(i);
            int purchase_commodity_id = purchaseReports.getPurchase_commodity_id();
            List<PurchaseInCommodity> purchaseInCommodities = purchaseExtendMapper.purchaseInventoryIn(purchase_commodity_id);
            if (purchaseInCommodities != null) {
                for (int j = 0; j < purchaseInCommodities.size(); j++) {
                    PurchaseInCommodity purchaseInCommodity = purchaseInCommodities.get(j);
                    if (purchaseInCommodity.getConfirm_in_date() != null && purchaseReports.getConfirm_purchase_employee_date() != null) {
                        int n = DateUtil.getDayBetween(purchaseReports.getConfirm_purchase_employee_date(), purchaseInCommodity.getConfirm_in_date());
                        purchaseInCommodity.setPurchase_period(n);
                    }
                }
                purchaseReports.setPurchaseInCommodities(purchaseInCommodities);
            }
        }
        return list;
    }

    public Integer countPurchaseReports(PurchaseSearchOption purchaseSearchOption) {
        return purchaseExtendMapper.countPurchaseReports(purchaseSearchOption);
    }

    public Map purchaseReportsStatistics(PurchaseSearchOption purchaseSearchOption) {
        return purchaseExtendMapper.purchaseReportsStatistics(purchaseSearchOption);
    }
}
