package com.xinshan.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xinshan.dao.InventoryHistoryDetailMapper;
import com.xinshan.dao.InventoryHistoryMapper;
import com.xinshan.dao.OrderCommodityMapper;
import com.xinshan.dao.extend.inventory.InventoryHistoryExtendMapper;
import com.xinshan.model.*;
import com.xinshan.model.extend.commodity.SampleInDetailExtend;
import com.xinshan.model.extend.commodity.SampleInExtend;
import com.xinshan.model.extend.gift.GiftCommodityExtend;
import com.xinshan.model.extend.inventory.*;
import com.xinshan.model.extend.order.OrderReturnCommodityExtend;
import com.xinshan.model.extend.order.OrderReturnExtend;
import com.xinshan.pojo.inventory.InventoryHistorySearchOption;
import com.xinshan.pojo.inventory.InventorySearchOption;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.constant.ConstantUtils;
import com.xinshan.utils.constant.inventory.InventoryHistoryConstant;
import com.xinshan.utils.constant.order.OrderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by mxt on 17-4-24.
 */
@Service
public class InventoryHistoryService {
    @Autowired
    private InventoryHistoryExtendMapper inventoryHistoryExtendMapper;
    @Autowired
    private InventoryHistoryMapper inventoryHistoryMapper;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private InventoryInService inventoryInService;
    @Autowired
    private OrderReturnService orderReturnService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private SampleInService sampleInService;
    @Autowired
    private InventoryHistoryDetailMapper inventoryHistoryDetailMapper;

    @Transactional
    public void updateInventoryHistory(InventoryHistory inventoryHistory) {
        inventoryHistoryMapper.updateByPrimaryKey(inventoryHistory);
    }

    /**
     * 出库历史记录
     * @param inventoryHistorySearchOption
     * @return
     */
    public List<InventoryHistoryExtend> inventoryHistories(InventoryHistorySearchOption inventoryHistorySearchOption) {
        List<Integer> historyIds = inventoryHistoryExtendMapper.inventoryHistoryIds(inventoryHistorySearchOption);
        if (historyIds != null && historyIds.size() > 0) {
            inventoryHistorySearchOption.setInventoryHistoryIds(historyIds);
            List<InventoryHistoryExtend> inventoryHistoryExtends = inventoryHistoryExtendMapper.inventoryHistories(inventoryHistorySearchOption);
            for (int i = 0; i < inventoryHistoryExtends.size(); i++) {
                InventoryHistoryExtend inventoryHistoryExtend = inventoryHistoryExtends.get(i);
                inventoryHistoryExtend.setInventoryHistoryDetails(inventoryHistoryExtendMapper.inventoryHistoryDetails(inventoryHistoryExtend.getInventory_history_id()));
                if (inventoryHistoryExtend.getOrder_id() != null) {
                    inventoryHistoryExtend.setOrderCarryFees(orderService.orderCarryFees(inventoryHistoryExtend.getOrder_id()));
                }
            }
            return inventoryHistoryExtends;
        }
        return null;
    }

    /**
     * 出库记录
     * @param inventory_history_id
     * @return
     */
    public InventoryHistoryExtend getInventoryHistoryById(int inventory_history_id) {
        InventoryHistorySearchOption inventoryHistorySearchOption = new InventoryHistorySearchOption();
        List<Integer> historyIds = new ArrayList<>();
        historyIds.add(inventory_history_id);
        inventoryHistorySearchOption.setInventoryHistoryIds(historyIds);
        List<InventoryHistoryExtend> inventoryHistoryExtends = inventoryHistoryExtendMapper.inventoryHistories(inventoryHistorySearchOption);
        if (inventoryHistoryExtends != null && inventoryHistoryExtends.size() == 1) {
            InventoryHistoryExtend inventoryHistoryExtend = inventoryHistoryExtends.get(0);
            inventoryHistoryExtend.setInventoryHistoryDetails(inventoryHistoryExtendMapper.inventoryHistoryDetails(inventoryHistoryExtend.getInventory_history_id()));
            return inventoryHistoryExtend;
        }
        return null;
    }

    public Integer countInventoryHistory(InventoryHistorySearchOption inventoryHistorySearchOption) {
        return inventoryHistoryExtendMapper.countInventoryHistory(inventoryHistorySearchOption);
    }

    /**
     * 退货入库
     * @param orderReturnExtend
     * @param list
     * @param returnCommodityStore
     * @param employee
     */
    @Transactional
    public void createInventoryHistory(OrderReturnExtend orderReturnExtend, List<InventoryInExtend> list, int returnCommodityStore, Employee employee) {
        for (int i = 0; i < list.size(); i++) {
            InventoryInExtend inventoryInExtend = list.get(i);
            InventoryHistory inventoryHistory = new InventoryHistory();
            inventoryHistory.setInventory_date(DateUtil.currentDate());
            inventoryHistory.setInventory_employee_code(employee.getEmployee_code());
            inventoryHistory.setInventory_employee_name(employee.getEmployee_name());
            inventoryHistory.setInventory_in_id(inventoryInExtend.getInventory_in_id());
            inventoryHistory.setOrder_id(inventoryInExtend.getOrder_id());
            inventoryHistory.setInventory_type(InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_RETURN_IN);//0入库， 1出库
            inventoryHistory.setInventory_history_code(inventoryHistoryCode(null));
            inventoryHistory.setInventory_history_settlement_status(0);
            inventoryHistory.setInventory_history_in_out(InventoryHistoryConstant.INVENTORY_HISTORY_IN);
            inventoryHistoryExtendMapper.createInventoryHistory(inventoryHistory);
            List<InventoryInCommodityExtend> inventoryInCommodities = inventoryInExtend.getInventoryInCommodities();
            createInventoryHistoryDetail(employee, inventoryHistory, returnCommodityStore, inventoryInCommodities);
        }
        List<OrderReturnCommodityExtend> orderReturnCommodities = orderReturnExtend.getOrderReturnCommodities();
        for (int i = 0; i < orderReturnCommodities.size(); i++) {
            OrderReturnCommodityExtend orderReturnCommodityExtend = orderReturnCommodities.get(i);
            if (orderReturnCommodityExtend.getOrder_return_commodity_type() == OrderConstants.ORDER_RETURN_COMMODITY_TYPE_ADD) {
                continue;
            }
            if (orderReturnCommodityExtend.getInventory_in_commodity_status() != null
                    && orderReturnCommodityExtend.getInventory_in_commodity_status() == 0) {
                orderReturnCommodityExtend.setInventory_in_commodity_status(2);
                orderReturnService.updateOrderReturnCommodity(orderReturnCommodityExtend);
            }
        }
    }

    /**
     * 赠品退货入库
     * @param list
     * @param returnCommodityStore
     * @param employee
     */
    public void createGiftReturnInventoryHistory(GiftReturn giftReturn, List<InventoryInExtend> list, int returnCommodityStore, Employee employee) {
        for (int i = 0; i < list.size(); i++) {
            InventoryInExtend inventoryInExtend = list.get(i);
            InventoryHistory inventoryHistory = new InventoryHistory();
            inventoryHistory.setInventory_date(DateUtil.currentDate());
            inventoryHistory.setInventory_employee_code(employee.getEmployee_code());
            inventoryHistory.setInventory_employee_name(employee.getEmployee_name());
            inventoryHistory.setInventory_in_id(inventoryInExtend.getInventory_in_id());
            inventoryHistory.setOrder_id(inventoryInExtend.getOrder_id());
            inventoryHistory.setInventory_type(InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_GIFT_RETURN);//0入库， 1出库
            inventoryHistory.setInventory_history_code(inventoryHistoryCode(null));
            inventoryHistory.setInventory_history_settlement_status(0);
            inventoryHistory.setInventory_history_in_out(InventoryHistoryConstant.INVENTORY_HISTORY_IN);
            inventoryHistory.setGift_return_id(giftReturn.getGift_return_id());
            inventoryHistoryExtendMapper.createInventoryHistory(inventoryHistory);
            List<InventoryInCommodityExtend> inventoryInCommodities = inventoryInExtend.getInventoryInCommodities();
            createInventoryHistoryDetail(employee, inventoryHistory, returnCommodityStore, inventoryInCommodities);
        }
    }

    /**
     * 库存调拨出库
     * @param inventoryMoveExtend
     * @param employee
     */
    public void inventoryMoveOut(InventoryMoveExtend inventoryMoveExtend, Employee employee,
                                  InventoryMoveCommodityExtend inventoryMoveCommodity,CommodityNum commodityNum) {
        InventoryHistory inventoryHistory = new InventoryHistory();
        inventoryHistory.setInventory_date(DateUtil.currentDate());
        inventoryHistory.setInventory_employee_code(employee.getEmployee_code());
        inventoryHistory.setInventory_employee_name(employee.getEmployee_name());
        inventoryHistory.setInventory_type(InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_MOVE_OUT);//0入库， 1出库
        inventoryHistory.setInventory_history_code(inventoryHistoryCode(null));
        inventoryHistory.setInventory_history_settlement_status(0);
        inventoryHistory.setInventory_history_in_out(InventoryHistoryConstant.INVENTORY_HISTORY_OUT);
        inventoryHistory.setInventory_move_id(inventoryMoveExtend.getInventory_move_id());
        inventoryHistoryExtendMapper.createInventoryHistory(inventoryHistory);
        InventoryHistoryDetail inventoryHistoryDetail = new InventoryHistoryDetail();
        inventoryHistoryDetail.setCommodity_num_id(inventoryMoveCommodity.getCommodity_num_id1());
        inventoryHistoryDetail.setInventory_history_id(inventoryHistory.getInventory_history_id());
        inventoryHistoryDetail.setInventory_history_num(inventoryMoveCommodity.getInventory_move_num());
        inventoryHistoryDetail.setInventory_in_out(0);
        inventoryHistoryDetail.setSample(commodityNum.getSample());
        inventoryHistoryDetail.setCommodity_id(inventoryMoveCommodity.getCommodity_id());
        inventoryHistoryDetail.setInventory_history_return_num(0);
        inventoryHistoryDetail.setCommodity_num(commodityNum.getNum());
        inventoryHistoryExtendMapper.createInventoryHistoryDetail(inventoryHistoryDetail);
    }

    /**
     * 库存调拨入库
     * @param inventoryMoveExtend
     * @param employee
     */
    public void inventoryMoveIn(InventoryMoveExtend inventoryMoveExtend, Employee employee,
                                InventoryMoveCommodityExtend inventoryMoveCommodity,CommodityNum commodityNum) {
        InventoryHistory inventoryHistory = new InventoryHistory();
        inventoryHistory.setInventory_date(DateUtil.currentDate());
        inventoryHistory.setInventory_employee_code(employee.getEmployee_code());
        inventoryHistory.setInventory_employee_name(employee.getEmployee_name());
        inventoryHistory.setInventory_type(InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_MOVE_IN);//0入库， 1出库
        inventoryHistory.setInventory_history_code(inventoryHistoryCode(null));
        inventoryHistory.setInventory_history_settlement_status(0);
        inventoryHistory.setInventory_history_in_out(InventoryHistoryConstant.INVENTORY_HISTORY_IN);
        inventoryHistory.setInventory_move_id(inventoryMoveExtend.getInventory_move_id());
        inventoryHistoryExtendMapper.createInventoryHistory(inventoryHistory);
        InventoryHistoryDetail inventoryHistoryDetail = new InventoryHistoryDetail();
        inventoryHistoryDetail.setCommodity_num_id(inventoryMoveCommodity.getCommodity_num_id2());
        inventoryHistoryDetail.setInventory_history_id(inventoryHistory.getInventory_history_id());
        inventoryHistoryDetail.setInventory_history_num(inventoryMoveCommodity.getInventory_move_num());
        inventoryHistoryDetail.setInventory_in_out(0);
        inventoryHistoryDetail.setSample(commodityNum.getSample());
        inventoryHistoryDetail.setCommodity_id(inventoryMoveCommodity.getCommodity_id());
        inventoryHistoryDetail.setInventory_history_return_num(0);
        inventoryHistoryDetail.setCommodity_num(commodityNum.getNum());
        inventoryHistoryExtendMapper.createInventoryHistoryDetail(inventoryHistoryDetail);
    }



    public String inventoryHistoryCode(String inventoryHistoryCode) {
        String n = null;
        if (inventoryHistoryCode == null) {
            String s = ConstantUtils.INVENTORY_IN_OUT_CODE_HEADER + DateUtil.format(DateUtil.currentDate(), "yyMMdd") + "[0-9]{3}";
            n = inventoryHistoryExtendMapper.todayInventoryHistoryCode(s);//
        }else {
            n = inventoryHistoryCode;
        }
        if (n == null) {
            return ConstantUtils.INVENTORY_IN_OUT_CODE_HEADER + Long.parseLong(DateUtil.format(DateUtil.currentDate(), "yyMMdd"))*100+1l;
        }
        n = n.substring(ConstantUtils.INVENTORY_IN_OUT_CODE_HEADER.length(), n.length());
        int m = Integer.parseInt(n);
        m++;
        return ConstantUtils.INVENTORY_IN_OUT_CODE_HEADER + m;
    }

    public void inventoryHistoryCodeInit() {
        InventoryHistorySearchOption inventoryHistorySearchOption = new InventoryHistorySearchOption();
        inventoryHistorySearchOption.setInventory_history_id_order(1);
        List<InventoryHistoryExtend> inventoryHistoryExtends = inventoryHistoryExtendMapper.inventoryHistories(inventoryHistorySearchOption);
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < inventoryHistoryExtends.size(); i++) {
            InventoryHistoryExtend inventoryHistoryExtend = inventoryHistoryExtends.get(i);
            Date inventory_date = inventoryHistoryExtend.getInventory_date();
            String dateStr = DateUtil.format(inventory_date, "yyMMdd");
            int n = 1;
            if (map.containsKey(dateStr)) {
                n = map.get(dateStr);
                n++;
            }
            map.put(dateStr, n);
            String s = ConstantUtils.INVENTORY_IN_OUT_CODE_HEADER + dateStr;
            if (n > 0 && n < 10) {
                s = s + "00"+n;
            }else if (n >= 10 && n < 100){
                s = s + "0"+n;
            }else if (n >= 100 && n < 1000) {
                s = s + n;
            }else {
                s = s + n;
            }
            inventoryHistoryExtend.setInventory_history_code(s);
            inventoryHistoryMapper.updateByPrimaryKey(inventoryHistoryExtend);
        }
    }

    /**
     *
     * @param inventorySearchOption
     * @return
     */
    public List<InventoryHistoryDetailExtend> inventoryHistoryDetailExtends(InventorySearchOption inventorySearchOption) {
        List<InventoryHistoryDetailExtend> list = inventoryHistoryExtendMapper.inventoryHistoryDetailExtends(inventorySearchOption);
        for (int j = 0; j < list.size(); j++) {
            InventoryHistoryDetailExtend inventoryHistoryDetailExtend = list.get(j);
            InventoryOutCommodity inventoryOutCommodity = inventoryHistoryDetailExtend.getInventoryOutCommodity();
            InventoryInCommodity inventoryInCommodity = inventoryHistoryDetailExtend.getInventoryInCommodity();
            if (inventoryOutCommodity != null && inventoryOutCommodity.getOrder_commodity_id() != null) {
                OrderCommodity orderCommodity = orderService.getOrderCommodity(inventoryOutCommodity.getOrder_commodity_id());
                inventoryHistoryDetailExtend.setOrderCommodity(orderCommodity);
                PurchaseCommodity purchaseCommodity = purchaseService.getPurchaseCommodityByOrderCommodityId(orderCommodity.getOrder_commodity_id());
                if (purchaseCommodity != null && purchaseCommodity.getPurchase_id() != null) {
                    Purchase purchase = purchaseService.getPurchaseById1(purchaseCommodity.getPurchase_id());
                    inventoryHistoryDetailExtend.setPurchase(purchase);
                    if (purchase.getOrder_id() != null) {
                        inventoryHistoryDetailExtend.setOrder(orderService.getOrderById1(purchase.getOrder_id()));
                    }
                }
                inventoryHistoryDetailExtend.setPurchaseCommodity(purchaseCommodity);
            }else if (inventoryInCommodity != null && inventoryInCommodity.getInventory_in_commodity_id() != null
                    && inventoryInCommodity.getPurchase_commodity_id() != null) {
                Integer purchase_commodity_id = inventoryInCommodity.getPurchase_commodity_id();
                PurchaseCommodity purchaseCommodity = purchaseService.getPurchaseCommodityById(purchase_commodity_id);
                inventoryHistoryDetailExtend.setPurchaseCommodity(purchaseCommodity);
            }

            CommodityNum commodityNum = inventoryService.getCommodityNumById1(inventoryHistoryDetailExtend.getCommodity_num_id());
            inventoryHistoryDetailExtend.setCommodityNum(commodityNum);
            if (commodityNum != null) {
                inventoryHistoryDetailExtend.setCommodityStore(inventoryService.getStoreById(commodityNum.getCommodity_store_id()));
            }
            /*InventoryHistory inventoryHistory = inventoryHistoryDetailExtend.getInventoryHistory();
            if (inventoryHistory != null && inventoryHistory.getOrder_id() != null) {
                Order order = orderService.getOrderById1(inventoryHistory.getOrder_id());
                inventoryHistoryDetailExtend.setOrder(order);
            }*/
        }
        return list;
    }

    public Integer countInventoryHistoryDetailExtends(InventorySearchOption inventorySearchOption) {
        return inventoryHistoryExtendMapper.countInventoryHistoryDetailExtends(inventorySearchOption);
    }


    @Transactional
    public void sampleInInventoryHistory(SampleInExtend sampleInExtend, Employee employee) {
        Integer inventory_in_id = sampleInExtend.getInventory_in_id();
        InventoryInExtend inventoryInExtend = inventoryInService.getInventoryInById(inventory_in_id);
        InventoryHistory inventoryHistory = new InventoryHistory();
        inventoryHistory.setInventory_date(DateUtil.currentDate());
        inventoryHistory.setInventory_employee_code(employee.getEmployee_code());
        inventoryHistory.setInventory_employee_name(employee.getEmployee_name());
        inventoryHistory.setInventory_in_id(inventoryInExtend.getInventory_in_id());
        inventoryHistory.setOrder_id(inventoryInExtend.getOrder_id());
        inventoryHistory.setInventory_type(InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_SAMPLE_IN);//0入库， 1出库,4上样入库
        inventoryHistory.setInventory_history_code(inventoryHistoryCode(null));
        inventoryHistory.setInventory_history_settlement_status(0);
        inventoryHistory.setInventory_history_in_out(InventoryHistoryConstant.INVENTORY_HISTORY_IN);
        inventoryHistoryExtendMapper.createInventoryHistory(inventoryHistory);
        Integer commodity_store_id = sampleInExtend.getCommodity_store_id();
        List<InventoryInCommodityExtend> inventoryInCommodities = inventoryInExtend.getInventoryInCommodities();
        createInventoryHistoryDetail(employee, inventoryHistory, commodity_store_id, inventoryInCommodities, sampleInExtend.getSampleInDetailExtends());
        sampleInExtend.setSample_in_confirm_status(1);
        sampleInService.updateSampleIn(sampleInExtend);
    }

    private void createInventoryHistoryDetail(Employee employee, InventoryHistory inventoryHistory, Integer commodity_store_id,
                                              List<InventoryInCommodityExtend> inventoryInCommodities, List<SampleInDetailExtend> sampleInDetailExtends) {
        for (int j = 0; j < inventoryInCommodities.size(); j++) {
            InventoryInCommodity inventoryInCommodity = inventoryInCommodities.get(j);
            SampleInDetailExtend sampleInDetailExtend = getSampleInDetailExtend(inventoryInCommodity.getInventory_in_commodity_id(), sampleInDetailExtends);
            int commodity_id = inventoryInCommodity.getCommodity_id();
            inventoryInCommodity.setInventory_in_status(3);
            inventoryInCommodity.setInventory_in_employee_name(employee.getEmployee_name());
            inventoryInCommodity.setInventory_in_employee_code(employee.getEmployee_code());
            //退货入库
            int return_commodity = inventoryInCommodity.getReturn_commodity();
            int sample = inventoryInCommodity.getSample();
            int inventory_in_num = inventoryInCommodity.getIn_num();
            CommodityNum commodityNum = inventoryService.getNumByCommodityIdAndStoreId(commodity_id, commodity_store_id, sample, return_commodity);
            if (commodityNum == null) {
                commodityNum = inventoryService.createCommodityNum(commodity_id, commodity_store_id, sample, inventory_in_num, return_commodity);
            }else {
                commodityNum.setNum(inventory_in_num + commodityNum.getNum());
                inventoryService.updateCommodityNum(commodityNum);
            }
            InventoryHistoryDetail inventoryHistoryDetail = new InventoryHistoryDetail();
            inventoryHistoryDetail.setCommodity_num_id(commodityNum.getCommodity_num_id());
            inventoryHistoryDetail.setInventory_in_commodity_id(inventoryInCommodity.getInventory_in_commodity_id());
            inventoryHistoryDetail.setInventory_history_id(inventoryHistory.getInventory_history_id());
            inventoryHistoryDetail.setInventory_history_num(inventory_in_num);
            inventoryHistoryDetail.setInventory_in_out(0);
            inventoryHistoryDetail.setSample(sample);
            inventoryHistoryDetail.setCommodity_id(inventoryInCommodity.getCommodity_id());
            inventoryHistoryDetail.setInventory_in_commodity_freight(sampleInDetailExtend.getSample_in_freight());
            inventoryHistoryDetail.setInventory_history_return_num(0);
            inventoryHistoryDetail.setCommodity_num(commodityNum.getNum());
            sampleInDetailExtend.setSample_in_detail_confirm_status(1);
            sampleInService.updateSampleInDetail(sampleInDetailExtend);
            inventoryHistoryExtendMapper.createInventoryHistoryDetail(inventoryHistoryDetail);
            inventoryInService.updateInventoryInCommodity(inventoryInCommodity);
        }
    }
    private void createInventoryHistoryDetail(Employee employee, InventoryHistory inventoryHistory, Integer commodity_store_id,
                                              List<InventoryInCommodityExtend> inventoryInCommodities) {
        for (int j = 0; j < inventoryInCommodities.size(); j++) {
            InventoryInCommodity inventoryInCommodity = inventoryInCommodities.get(j);
            int commodity_id = inventoryInCommodity.getCommodity_id();
            inventoryInCommodity.setInventory_in_status(3);
            inventoryInCommodity.setInventory_in_employee_name(employee.getEmployee_name());
            inventoryInCommodity.setInventory_in_employee_code(employee.getEmployee_code());
            //退货入库
            int return_commodity = inventoryInCommodity.getReturn_commodity();
            int sample = inventoryInCommodity.getSample();
            int inventory_in_num = inventoryInCommodity.getIn_num();
            CommodityNum commodityNum = inventoryService.getNumByCommodityIdAndStoreId(commodity_id, commodity_store_id, sample, return_commodity);
            if (commodityNum == null) {
                commodityNum = inventoryService.createCommodityNum(commodity_id, commodity_store_id, sample, inventory_in_num, return_commodity);
            }else {
                commodityNum.setNum(inventory_in_num + commodityNum.getNum());
                inventoryService.updateCommodityNum(commodityNum);
            }
            InventoryHistoryDetail inventoryHistoryDetail = new InventoryHistoryDetail();
            inventoryHistoryDetail.setCommodity_num_id(commodityNum.getCommodity_num_id());
            inventoryHistoryDetail.setInventory_in_commodity_id(inventoryInCommodity.getInventory_in_commodity_id());
            inventoryHistoryDetail.setInventory_history_id(inventoryHistory.getInventory_history_id());
            inventoryHistoryDetail.setInventory_history_num(inventory_in_num);
            inventoryHistoryDetail.setInventory_in_out(0);
            inventoryHistoryDetail.setSample(sample);
            inventoryHistoryDetail.setCommodity_id(inventoryInCommodity.getCommodity_id());
            inventoryHistoryDetail.setInventory_history_return_num(0);
            inventoryHistoryDetail.setCommodity_num(commodityNum.getNum());
            inventoryHistoryExtendMapper.createInventoryHistoryDetail(inventoryHistoryDetail);
            inventoryInService.updateInventoryInCommodity(inventoryInCommodity);
        }
    }

    private SampleInDetailExtend getSampleInDetailExtend(int inventory_in_commodity_id, List<SampleInDetailExtend> sampleInDetailExtends) {
        for (int i = 0; i < sampleInDetailExtends.size(); i++) {
            SampleInDetailExtend sampleInDetailExtend = sampleInDetailExtends.get(i);
            if (sampleInDetailExtend.getInventory_in_commodity_id().equals(inventory_in_commodity_id)) {
                return sampleInDetailExtend;
            }
        }
        return null;
    }

    public InventoryHistoryDetail getInventoryHistoryDetailById(int inventory_history_detail_id) {
        return inventoryHistoryDetailMapper.selectByPrimaryKey(inventory_history_detail_id);
    }

    public void updateInventoryHistoryDetail(InventoryHistoryDetail inventoryHistoryDetail) {
        inventoryHistoryDetailMapper.updateByPrimaryKey(inventoryHistoryDetail);
    }

    public List<Map> historyReportExport(InventorySearchOption inventorySearchOption) {
        return inventoryHistoryExtendMapper.historyReportExport(inventorySearchOption);
    }
}
