package com.xinshan.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.activity.ActivityComponents;
import com.xinshan.components.commodity.CommodityComponent;
import com.xinshan.dao.*;
import com.xinshan.dao.extend.inventory.InventoryHistoryExtendMapper;
import com.xinshan.dao.extend.inventory.InventoryInExtendMapper;
import com.xinshan.model.*;
import com.xinshan.model.extend.activity.ActivityCommodityExtend;
import com.xinshan.model.extend.afterSales.AfterSalesCommodityExtend;
import com.xinshan.model.extend.commodity.*;
import com.xinshan.model.extend.gift.GiftCommodityExtend;
import com.xinshan.model.extend.gift.GiftExtend;
import com.xinshan.model.extend.gift.GiftReturnCommodityExtend;
import com.xinshan.model.extend.gift.GiftReturnExtend;
import com.xinshan.model.extend.inventory.InventoryHistoryDetailExtend;
import com.xinshan.model.extend.inventory.InventoryHistoryExtend;
import com.xinshan.model.extend.inventory.InventoryInCommodityExtend;
import com.xinshan.model.extend.inventory.InventoryInExtend;
import com.xinshan.model.extend.order.OrderReturnCommodityExtend;
import com.xinshan.model.extend.order.OrderReturnExtend;
import com.xinshan.model.extend.purchase.PurchaseCommodityExtend;
import com.xinshan.model.extend.purchase.PurchaseExtend;
import com.xinshan.utils.DateUtil;
import com.xinshan.pojo.inventory.InventorySearchOption;
import com.xinshan.utils.constant.ConstantUtils;
import com.xinshan.utils.constant.inventory.InventoryConstant;
import com.xinshan.utils.constant.inventory.InventoryHistoryConstant;
import com.xinshan.utils.constant.order.OrderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by mxt on 16-11-9.
 */
@Service
public class InventoryInService {
    @Autowired
    private InventoryInExtendMapper inventoryInExtendMapper;
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private PurchaseMapper purchaseMapper;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private CommodityNumMapper commodityNumMapper;
    @Autowired
    private InventoryInCommodityMapper inventoryInCommodityMapper;
    @Autowired
    private PurchaseCommodityMapper purchaseCommodityMapper;
    @Autowired
    private OrderCommodityMapper orderCommodityMapper;
    @Autowired
    private InventoryHistoryMapper inventoryHistoryMapper;
    @Autowired
    private CommodityService commodityService;
    @Autowired
    private OrderReturnCommodityMapper orderReturnCommodityMapper;
    @Autowired
    private InventoryHistoryService inventoryHistoryService;
    @Autowired
    private SampleInService sampleInService;
    @Autowired
    private SettlementService settlementService;
    @Autowired
    private GiftService giftService;
    @Autowired
    private InventoryHistoryExtendMapper inventoryHistoryExtendMapper;
    @Autowired
    private ActivityService activityService;

    /**
     * 确认到货添加入库数据
     * @param
     * @param employee
     */
    @Transactional
    public synchronized void createInventoryIn(Object o, Employee employee) {
        if (o instanceof PurchaseExtend) {
            purchaseInventoryIn((PurchaseExtend) o, employee);
        }else if (o instanceof OrderReturnExtend) {
            orderReturnInventoryIn((OrderReturnExtend) o, employee);
        }else if (o instanceof SampleInExtend) {
            sampleInInventoryIn((SampleInExtend) o, employee);
        }else if (o instanceof GiftReturnExtend) {
            giftReturnInventoryIn((GiftReturnExtend) o, employee);
        }
    }

    public void purchaseInventoryIn(PurchaseExtend purchaseExtend, Employee employee) {
        InventoryIn inventoryIn = new InventoryIn();
        Date date = DateUtil.currentDate();
        inventoryIn.setInventory_in_date(date);
        inventoryIn.setSupplier_id(purchaseExtend.getSupplier_id());
        inventoryIn.setPurchase_id(purchaseExtend.getPurchase_id());
        inventoryIn.setRecord_employee_code(employee.getEmployee_code());
        inventoryIn.setRecord_employee_name(employee.getEmployee_name());
        inventoryIn.setInventory_in_code(inventoryInCode(null));
        inventoryIn.setOrder_id(purchaseExtend.getOrder_id());
        inventoryInExtendMapper.createInventoryIn(inventoryIn);
        List<PurchaseCommodityExtend> list = purchaseExtend.getPurchaseCommodities();
        for (int i = 0; i < list.size(); i++) {
            PurchaseCommodityExtend purchaseCommodityExtend = list.get(i);
            InventoryInCommodity inventoryInCommodity = new InventoryInCommodity();
            inventoryInCommodity.setCommodity_id(purchaseCommodityExtend.getCommodity_id());
            inventoryInCommodity.setPurchase_commodity_id(purchaseCommodityExtend.getPurchase_commodity_id());
            inventoryInCommodity.setInventory_in_id(inventoryIn.getInventory_in_id());
            if (purchaseCommodityExtend.getPurchase_commodity_status() != null &&
                    purchaseCommodityExtend.getPurchase_commodity_status() == 4) {//4全部到货
                inventoryInCommodity.setInventory_in_status(1);//到货状态，0新添加，1确认采购，2审核，3部分到货，4全部到货
            }else {
                inventoryInCommodity.setInventory_in_status(0);
            }
            inventoryInCommodity.setSample(purchaseCommodityExtend.getSample());
            inventoryInCommodity.setReturn_commodity(0);
            inventoryInCommodity.setConfirm_in_date(date);
            inventoryInCommodity.setInventory_in_commodity_type(InventoryConstant.INVENTORY_IN_COMMODITY_TYPE_ORDER);
            inventoryInExtendMapper.createInventoryInCommodity(inventoryInCommodity);
        }
        if (purchaseExtend.getPurchase_id() != null) {
            purchaseExtend.setPurchase_status(1);
            purchaseMapper.updateByPrimaryKey(purchaseExtend);
        }
    }

    public void orderReturnInventoryIn(OrderReturnExtend orderReturnExtend, Employee employee) {
        int order_id = orderReturnExtend.getOrder_id();
        List<OrderReturnCommodityExtend> list = orderReturnExtend.getOrderReturnCommodities();
        Map<Integer, List<OrderReturnCommodityExtend>> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            OrderReturnCommodityExtend orderReturnCommodityExtend = list.get(i);
            if (orderReturnCommodityExtend.getOrder_return_commodity_type() == OrderConstants.ORDER_RETURN_COMMODITY_TYPE_ADD) {//新增退货商品
                continue;
            }
            Integer commodity_id = orderReturnCommodityExtend.getCommodity_id();
            Commodity commodity = commodityService.getCommodityById(commodity_id);
            Integer supplier_id = commodity.getSupplier_id();
            if (map.containsKey(supplier_id)) {
                List<OrderReturnCommodityExtend> list1 = map.get(supplier_id);
                list1.add(orderReturnCommodityExtend);
            }else {
                List<OrderReturnCommodityExtend> list1 = new ArrayList<>();
                list1.add(orderReturnCommodityExtend);
                map.put(supplier_id, list1);
            }
        }
        String inventoryInCode = null;
        Iterator<Map.Entry<Integer, List<OrderReturnCommodityExtend>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            inventoryInCode = inventoryInCode(inventoryInCode);
            Map.Entry<Integer, List<OrderReturnCommodityExtend>> next = iterator.next();
            List<OrderReturnCommodityExtend> value = next.getValue();
            Integer supplier_id = next.getKey();
            InventoryIn inventoryIn = new InventoryIn();
            Date date = DateUtil.currentDate();
            inventoryIn.setInventory_in_date(date);
            inventoryIn.setSupplier_id(supplier_id);
            inventoryIn.setRecord_employee_code(employee.getEmployee_code());
            inventoryIn.setRecord_employee_name(employee.getEmployee_name());
            inventoryIn.setInventory_in_code(inventoryInCode);
            inventoryIn.setOrder_id(order_id);
            inventoryInExtendMapper.createInventoryIn(inventoryIn);
            for (int i = 0; i < value.size(); i++) {
                OrderReturnCommodityExtend orderReturnCommodityExtend = value.get(i);
                InventoryInCommodity inventoryInCommodity = new InventoryInCommodity();
                inventoryInCommodity.setCommodity_id(orderReturnCommodityExtend.getCommodity_id());
                inventoryInCommodity.setInventory_in_id(inventoryIn.getInventory_in_id());
                inventoryInCommodity.setInventory_in_status(1);//到货状态，0新添加，1确认采购，2审核，3部分到货，4全部到货
                inventoryInCommodity.setSample(1);
                inventoryInCommodity.setReturn_commodity(1);
                inventoryInCommodity.setConfirm_in_date(date);
                inventoryInCommodity.setInventory_in_employee_code(employee.getEmployee_code());
                inventoryInCommodity.setInventory_in_employee_name(employee.getEmployee_name());
                inventoryInCommodity.setIn_num(Math.abs(orderReturnCommodityExtend.getOrder_return_commodity_num()));
                inventoryInCommodity.setInventory_in_commodity_type(InventoryConstant.INVENTORY_IN_COMMODITY_TYPE_RETURN_ADD);
                inventoryInExtendMapper.createInventoryInCommodity(inventoryInCommodity);

                orderReturnCommodityExtend.setInventory_in_commodity_id(inventoryInCommodity.getInventory_in_commodity_id());
                orderReturnCommodityExtend.setInventory_in_commodity_status(0);
                orderReturnCommodityMapper.updateByPrimaryKey(orderReturnCommodityExtend);
            }
        }
    }

    /**
     * 退还礼品入库
     * @param
     * @param employee
     */
    public void giftReturnInventoryIn(GiftReturnExtend giftReturnExtend, Employee employee) {
        Gift gift = giftReturnExtend.getGift();
        String inventoryInCode = inventoryInCode(null);
        Integer supplier_id = gift.getSupplier_id();
        InventoryIn inventoryIn = new InventoryIn();
        Date date = DateUtil.currentDate();
        inventoryIn.setInventory_in_date(date);
        inventoryIn.setSupplier_id(supplier_id);
        inventoryIn.setRecord_employee_code(employee.getEmployee_code());
        inventoryIn.setRecord_employee_name(employee.getEmployee_name());
        inventoryIn.setInventory_in_code(inventoryInCode);
        inventoryIn.setGift_return_id(giftReturnExtend.getGift_return_id());
        inventoryInExtendMapper.createInventoryIn(inventoryIn);

        List<GiftReturnCommodityExtend> giftReturnCommodityExtends = giftReturnExtend.getGiftReturnCommodityExtends();
        for (int i = 0; i < giftReturnCommodityExtends.size(); i++) {
            GiftReturnCommodityExtend giftReturnCommodityExtend = giftReturnCommodityExtends.get(i);
            GiftCommodity giftCommodity = giftReturnCommodityExtend.getGiftCommodity();
            int sample = 0;
            Integer commodity_num_id = giftCommodity.getCommodity_num_id();
            if (commodity_num_id != null) {
                CommodityNumExtend commodityNumById = inventoryService.getCommodityNumById(commodity_num_id);
                if (commodityNumById != null) {
                    sample = commodityNumById.getSample();
                }
            }
            InventoryInCommodity inventoryInCommodity = new InventoryInCommodity();
            inventoryInCommodity.setCommodity_id(giftCommodity.getCommodity_id());
            inventoryInCommodity.setInventory_in_id(inventoryIn.getInventory_in_id());
            inventoryInCommodity.setInventory_in_status(1);//到货状态，0新添加，1确认采购，2审核，3部分到货，4全部到货
            inventoryInCommodity.setSample(sample);
            inventoryInCommodity.setReturn_commodity(1);
            inventoryInCommodity.setConfirm_in_date(date);
            inventoryInCommodity.setInventory_in_employee_code(employee.getEmployee_code());
            inventoryInCommodity.setInventory_in_employee_name(employee.getEmployee_name());
            inventoryInCommodity.setIn_num(Math.abs(giftReturnCommodityExtend.getReturn_num()));
            inventoryInCommodity.setInventory_in_commodity_type(InventoryConstant.INVENTORY_IN_COMMODITY_TYPE_GIFT_RETURN);
            inventoryInExtendMapper.createInventoryInCommodity(inventoryInCommodity);
        }
    }

    public void createInventoryIn(JSONArray jsonArray, Employee employee) {
        int size = jsonArray.size();
        for (int i = 0; i < size; i++) {
            PurchaseExtend purchaseExtend = JSONObject.parseObject(jsonArray.get(i).toString(), PurchaseExtend.class);
            createInventoryIn(purchaseExtend , employee);
        }
    }

    private String inventoryInCode(String inventoryInCode){
        String n = null;
        if (inventoryInCode == null) {
            String s = ConstantUtils.INVENTORY_IN_CODE_HEADER + DateUtil.format(DateUtil.currentDate(), "yyMMdd") + "[0-9]{3}";
            n = inventoryInExtendMapper.maxInventoryInCode(s);
        }else {
            n = inventoryInCode;
        }
        if (n == null) {
            return ConstantUtils.INVENTORY_IN_CODE_HEADER + Long.parseLong(DateUtil.format(DateUtil.currentDate(), "yyMMdd"))*100+1l;
        }
        n = n.substring(ConstantUtils.INVENTORY_IN_CODE_HEADER.length(), n.length());
        int m = Integer.parseInt(n);
        m++;
        return ConstantUtils.INVENTORY_IN_CODE_HEADER + m;
    }

    public List<Integer> inventoryInIds(InventorySearchOption inventorySearchOption) {
        return inventoryInExtendMapper.inventoryInIds(inventorySearchOption);
    }
    public List<InventoryInExtend> inventoryInList(InventorySearchOption inventorySearchOption) {
        List<InventoryInExtend> list = inventoryInExtendMapper.inventoryInList(inventorySearchOption);
        for (int i = 0; i < list.size(); i++) {
            InventoryInExtend inventoryInExtend = list.get(i);
            List<InventoryInCommodityExtend> inventoryInCommodityExtends = inventoryInCommodities(inventoryInExtend.getInventory_in_id());
            inventoryInExtend.setInventoryInCommodities(inventoryInCommodityExtends);
        }
        return list;
    }

    private List<InventoryInCommodityExtend> inventoryInCommodities(InventorySearchOption inventorySearchOption) {
        List<InventoryInCommodityExtend> inventoryInCommodityExtends = inventoryInExtendMapper.inventoryInCommodities(inventorySearchOption);
        return inventoryInCommodityExtends;
    }

    public List<InventoryInCommodityExtend> inventoryInCommodities(int inventory_in_id) {
        InventorySearchOption inventorySearchOption = new InventorySearchOption();
        inventorySearchOption.setInventory_in_id(inventory_in_id);
        return inventoryInCommodities(inventorySearchOption);
    }

    public List<InventoryInCommodityExtend> inventoryInCommoditiesByPid(int purchase_commodity_id) {
        InventorySearchOption inventorySearchOption = new InventorySearchOption();
        inventorySearchOption.setPurchase_commodity_id(purchase_commodity_id);
        return inventoryInCommodities(inventorySearchOption);
    }

    public Integer countInventoryIn(InventorySearchOption inventorySearchOption) {
        return inventoryInExtendMapper.countInventoryIn(inventorySearchOption);
    }

    public InventoryInExtend getInventoryInById(int inventory_in_id) {
        InventorySearchOption inventorySearchOption = new InventorySearchOption();
        List<Integer> ids = new ArrayList<>();
        ids.add(inventory_in_id);
        inventorySearchOption.setInventoryInIds(ids);
        List<InventoryInExtend> list = inventoryInExtendMapper.inventoryInList(inventorySearchOption);
        if (list != null && list.size() == 1) {
            InventoryInExtend inventoryInExtend = list.get(0);
            inventoryInExtend.setInventoryInCommodities(inventoryInCommodities(inventoryInExtend.getInventory_in_id()));
            return inventoryInExtend;
        }
        return null;
    }

    public InventoryInCommodity getInventoryInCommodityByPid(int purchase_commodity_id) {
        return inventoryInExtendMapper.getInventoryInCommodityByPid(purchase_commodity_id);
    }

    public InventoryInCommodity getInventoryInCommodityById(int inventory_in_commodity_id) {
        return inventoryInCommodityMapper.selectByPrimaryKey(inventory_in_commodity_id);
    }

    public void updateInventoryInCommodity(InventoryInCommodity inventoryInCommodity) {
        inventoryInCommodityMapper.updateByPrimaryKey(inventoryInCommodity);
    }

    /**
     * 确认入库
     * @param jsonArray
     * @param employee
     */
    @Transactional
    public void confirmInventoryIn(JSONArray jsonArray, Employee employee){
        int size = jsonArray.size();
        InventoryHistory inventoryHistory = new InventoryHistory();
        inventoryHistory.setInventory_date(DateUtil.currentDate());
        inventoryHistory.setInventory_employee_code(employee.getEmployee_code());
        inventoryHistory.setInventory_employee_name(employee.getEmployee_name());
        Integer[] inId = getInIdAndOrderId(jsonArray);
        inventoryHistory.setInventory_in_id(inId[0]);
        inventoryHistory.setOrder_id(inId[1]);
        inventoryHistory.setInventory_type(InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_IN);//0入库， 1出库
        inventoryHistory.setInventory_history_settlement_status(0);
        inventoryHistory.setInventory_history_in_out(InventoryHistoryConstant.INVENTORY_HISTORY_IN);
        String inventoryHistoryCode = inventoryHistoryService.inventoryHistoryCode(null);
        inventoryHistory.setInventory_history_code(inventoryHistoryCode);
        inventoryHistoryExtendMapper.createInventoryHistory(inventoryHistory);
        boolean guangdongCommodity = false;
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = JSON.parseObject(jsonArray.get(i).toString());
            int inventory_in_commodity_id = jsonObject.getInteger("inventory_in_commodity_id");
            int inventory_in_num = jsonObject.getInteger("inventory_in_num");
            int commodity_store_id = jsonObject.getInteger("commodity_store_id");
            int sample = jsonObject.getInteger("sample");
            BigDecimal inventory_in_commodity_freight = new BigDecimal("0");
            if (jsonObject.get("inventory_in_commodity_freight") != null && !jsonObject.get("inventory_in_commodity_freight").toString().equals("")) {
                inventory_in_commodity_freight = new BigDecimal(jsonObject.get("inventory_in_commodity_freight").toString());
            }
            InventoryInCommodity inventoryInCommodity = inventoryInCommodityMapper.selectByPrimaryKey(inventory_in_commodity_id);
            PurchaseCommodity purchaseCommodity = null;
            OrderCommodity orderCommodity = null;
            if(inventoryInCommodity.getPurchase_commodity_id() != null) {
                 purchaseCommodity = purchaseService.getPurchaseCommodityById(inventoryInCommodity.getPurchase_commodity_id());
                if (purchaseCommodity != null) {
                    orderCommodity = orderCommodityMapper.selectByPrimaryKey(purchaseCommodity.getOrder_commodity_id());
                }
            }
            int commodity_id = inventoryInCommodity.getCommodity_id();
            if (inventoryInCommodity.getIn_num() == null){
                inventoryInCommodity.setIn_num(inventory_in_num);
            }else {
                inventoryInCommodity.setIn_num(inventory_in_num + inventoryInCommodity.getIn_num());
            }

            if (purchaseCommodity != null) {
                if (inventoryInCommodity.getIn_num() < purchaseCommodity.getPurchase_num()) {//入库数量小于采购数量
                    inventoryInCommodity.setInventory_in_status(2);//部分入库
                }else if(inventoryInCommodity.getIn_num() >= purchaseCommodity.getPurchase_num()){
                    inventoryInCommodity.setInventory_in_status(3);//全部入库
                }
                if (purchaseCommodity.getInventory_in_num() == null) {
                    purchaseCommodity.setInventory_in_num(inventory_in_num);
                }else {
                    purchaseCommodity.setInventory_in_num(inventory_in_num + purchaseCommodity.getInventory_in_num());
                }

                if (purchaseCommodity.getPurchase_arrival_num() < purchaseCommodity.getInventory_in_num()) {
                    throw new RuntimeException("入库数量不能大于到货数量");
                }
            }else {
                inventoryInCommodity.setInventory_in_status(3);//全部入库
            }

            inventoryInCommodity.setInventory_in_employee_name(employee.getEmployee_name());
            inventoryInCommodity.setInventory_in_employee_code(employee.getEmployee_code());

            //退货入库
            int return_commodity = 0;
            if (orderCommodity != null && orderCommodity.getOrder_commodity_return_status() < 0) {
                return_commodity = 1;
            }
            CommodityNum commodityNum = inventoryService.getNumByCommodityIdAndStoreId(commodity_id, commodity_store_id, sample, return_commodity);
            if (commodityNum == null) {
                commodityNum = inventoryService.createCommodityNum(commodity_id, commodity_store_id, sample, inventory_in_num, return_commodity);
            }else {
                commodityNum.setNum(inventory_in_num + commodityNum.getNum());
                commodityNumMapper.updateByPrimaryKey(commodityNum);
            }
            InventoryHistoryDetail inventoryHistoryDetail = new InventoryHistoryDetail();
            inventoryHistoryDetail.setCommodity_num_id(commodityNum.getCommodity_num_id());
            inventoryHistoryDetail.setInventory_in_commodity_id(inventoryInCommodity.getInventory_in_commodity_id());
            inventoryHistoryDetail.setInventory_history_id(inventoryHistory.getInventory_history_id());
            inventoryHistoryDetail.setInventory_history_num(inventory_in_num);
            inventoryHistoryDetail.setInventory_in_out(0);
            inventoryHistoryDetail.setSample(sample);
            inventoryHistoryDetail.setCommodity_id(inventoryInCommodity.getCommodity_id());
            inventoryHistoryDetail.setInventory_in_commodity_freight(inventory_in_commodity_freight);
            inventoryHistoryDetail.setInventory_history_return_num(0);
            inventoryHistoryDetail.setCommodity_num(commodityNum.getNum());
            inventoryHistoryExtendMapper.createInventoryHistoryDetail(inventoryHistoryDetail);
            inventoryInCommodityMapper.updateByPrimaryKey(inventoryInCommodity);
            if (purchaseCommodity != null) {
                purchaseCommodityMapper.updateByPrimaryKey(purchaseCommodity);
                purchaseService.orderCommodityStatus(purchaseCommodity);
            }
            CommodityExtend commodity = CommodityComponent.getCommodityById(commodity_id);
            if (commodity.getGuangdong() != null && commodity.getGuangdong().equals(1)) {
                guangdongCommodity = true;
            }
        }
        if (guangdongCommodity) {
            //settlementService.createSettlementInventoryIn(inventoryHistory.getInventory_history_id(), employee);
        }
    }

    private Integer[] getInIdAndOrderId(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = JSON.parseObject(jsonArray.get(i).toString());
            int inventory_in_commodity_id = jsonObject.getInteger("inventory_in_commodity_id");
            InventoryInCommodity inventoryInCommodity = inventoryInCommodityMapper.selectByPrimaryKey(inventory_in_commodity_id);
            Integer purchase_commodity_id = inventoryInCommodity.getPurchase_commodity_id();
            OrderCommodity orderCommodity = null;
            if (purchase_commodity_id != null) {
                PurchaseCommodity purchaseCommodity = purchaseService.getPurchaseCommodityById(inventoryInCommodity.getPurchase_commodity_id());
                if (purchaseCommodity != null) {
                    orderCommodity = orderCommodityMapper.selectByPrimaryKey(purchaseCommodity.getOrder_commodity_id());
                }
            }
            return new Integer[]{inventoryInCommodity.getInventory_in_id(), orderCommodity == null ? null : orderCommodity.getOrder_id()};
        }
        return null;
    }

    public List<InventoryHistoryExtend> inventoryHistories(InventorySearchOption inventorySearchOption) {
        List<InventoryHistoryExtend> list = inventoryInExtendMapper.inventoryHistories(inventorySearchOption);
        for (int i = 0; i < list.size(); i++) {
            InventoryHistoryExtend inventoryHistoryExtend = list.get(i);
            List<InventoryHistoryDetailExtend> list1 = inventoryInExtendMapper.inventoryHistoryDetails(inventoryHistoryExtend.getInventory_history_id());
            for (int j = 0; j < list1.size(); j++) {
                InventoryHistoryDetailExtend inventoryHistoryDetailExtend = list1.get(j);
                InventoryOutCommodity inventoryOutCommodity = inventoryHistoryDetailExtend.getInventoryOutCommodity();
                if (inventoryOutCommodity != null && inventoryOutCommodity.getOrder_commodity_id() != null) {
                    OrderCommodity orderCommodity = orderCommodityMapper.selectByPrimaryKey(inventoryOutCommodity.getOrder_commodity_id());
                    inventoryHistoryDetailExtend.setOrderCommodity(orderCommodity);
                    if (orderCommodity.getActivity_commodity_id() != null) {
                        ActivityCommodityExtend activityCommodityExtend = activityService.getActivityCommodityById(orderCommodity.getActivity_commodity_id());
                        inventoryHistoryDetailExtend.setActivityCommodityExtend(activityCommodityExtend);
                    }
                }
            }
            inventoryHistoryExtend.setInventoryHistoryDetails(list1);
            if (inventoryHistoryExtend.getInventory_type() == InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_GIFT_OUT) {
                inventoryHistoryExtend.setGiftExtend(giftService.getGiftById(inventoryHistoryExtend.getGift_id()));
            }
        }
        return list;
    }

    public InventoryHistoryExtend getInventoryHistoryById(int inventory_history_id) {
        InventorySearchOption inventorySearchOption = new InventorySearchOption();
        inventorySearchOption.setInventory_history_id(inventory_history_id);
        List<InventoryHistoryExtend> list = inventoryHistories(inventorySearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public Integer countInventoryHistory(InventorySearchOption inventorySearchOption) {
        return inventoryInExtendMapper.countInventoryHistory(inventorySearchOption);
    }

    public List<InventoryHistoryExtend> inventoryHistoryExtends(InventorySearchOption inventorySearchOption) {
        long a = System.currentTimeMillis();
        List<Integer> list = inventoryInExtendMapper.inventoryHistoryList(inventorySearchOption);
        long b = System.currentTimeMillis();
        System.out.println("inventoryHistoryList:"+(b-a));
        List<InventoryHistoryExtend> inventoryHistoryExtends = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            long aa = System.currentTimeMillis();
            inventoryHistoryExtends.add(getInventoryHistoryById(list.get(i)));
            long bb = System.currentTimeMillis();
            System.out.println("getInventoryHistoryById:"+(bb-aa));
        }
        return inventoryHistoryExtends;
    }

    public void updateInventoryHistory(InventoryHistory inventoryHistory) {
        inventoryHistoryMapper.updateByPrimaryKey(inventoryHistory);
    }

    /**
     * 添加上样入库
     * @param sampleInExtend
     */
    public void sampleInInventoryIn(SampleInExtend sampleInExtend, Employee employee) {
        Date date = DateUtil.currentDate();
        InventoryIn inventoryIn = new InventoryIn();
        inventoryIn.setSupplier_id(sampleInExtend.getSample_in_supplier_id());
        inventoryIn.setInventory_in_code(inventoryInCode(null));
        inventoryIn.setInventory_in_date(date);
        inventoryIn.setRecord_employee_code(employee.getEmployee_code());
        inventoryIn.setRecord_employee_name(employee.getEmployee_name());
        inventoryInExtendMapper.createInventoryIn(inventoryIn);
        //sampleInService.updateSampleIn(sampleInExtend);
        sampleInExtend.setInventory_in_id(inventoryIn.getInventory_in_id());
        List<SampleInDetailExtend> sampleInDetailExtends = sampleInExtend.getSampleInDetailExtends();
        for (int i = 0; i < sampleInDetailExtends.size(); i++) {
            SampleInDetailExtend sampleInDetailExtend = sampleInDetailExtends.get(i);
            Integer commodity_id = sampleInDetailExtend.getCommodity_id();
            InventoryInCommodity inventoryInCommodity = new InventoryInCommodity();
            inventoryInCommodity.setCommodity_id(commodity_id);
            inventoryInCommodity.setInventory_in_id(inventoryIn.getInventory_in_id());
            inventoryInCommodity.setInventory_in_status(1);//到货状态，0新添加，1确认采购，2审核，3部分到货，4全部到货
            inventoryInCommodity.setSample(1);
            inventoryInCommodity.setReturn_commodity(0);
            inventoryInCommodity.setConfirm_in_date(date);
            inventoryInCommodity.setInventory_in_employee_code(employee.getEmployee_code());
            inventoryInCommodity.setInventory_in_employee_name(employee.getEmployee_name());
            inventoryInCommodity.setIn_num(sampleInDetailExtend.getCommodity_sample_in_num());
            inventoryInCommodity.setInventory_in_commodity_type(InventoryConstant.INVENTORY_IN_COMMODITY_TYPE_SAMPLE_IN);
            inventoryInExtendMapper.createInventoryInCommodity(inventoryInCommodity);
            sampleInDetailExtend.setInventory_in_commodity_id(inventoryInCommodity.getInventory_in_commodity_id());
            sampleInService.updateSampleInDetail(sampleInDetailExtend);
        }
        sampleInService.updateSampleIn(sampleInExtend);
    }


}
