package com.xinshan.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.order.OrderComponents;
import com.xinshan.dao.InventoryOutCommodityMapper;
import com.xinshan.dao.InventoryOutMapper;
import com.xinshan.dao.LogisticsMapper;
import com.xinshan.dao.OrderCommodityMapper;
import com.xinshan.dao.extend.inventory.InventoryHistoryExtendMapper;
import com.xinshan.dao.extend.inventory.InventoryInExtendMapper;
import com.xinshan.dao.extend.inventory.InventoryOutExtendMapper;
import com.xinshan.model.*;
import com.xinshan.model.extend.commodity.SampleOutDetailExtend;
import com.xinshan.model.extend.commodity.SampleOutExtend;
import com.xinshan.model.extend.gift.GiftCommodityExtend;
import com.xinshan.model.extend.gift.GiftExtend;
import com.xinshan.model.extend.inventory.InventoryHistoryExtend;
import com.xinshan.model.extend.inventory.InventoryOutCommodityExtend;
import com.xinshan.model.extend.inventory.InventoryOutExtend;
import com.xinshan.model.extend.order.OrderCommodityExtend;
import com.xinshan.model.extend.order.OrderExtend;
import com.xinshan.model.extend.order.OrderReturnCommodityExtend;
import com.xinshan.model.extend.order.OrderReturnExtend;
import com.xinshan.pojo.inventory.InventorySearchOption;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.SplitUtils;
import com.xinshan.utils.constant.inventory.InventoryConstant;
import com.xinshan.utils.constant.inventory.InventoryHistoryConstant;
import com.xinshan.utils.constant.inventory.LogisticsConstants;
import com.xinshan.utils.constant.order.OrderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by mxt on 16-11-11.
 */
@Service
public class InventoryOutService {
    @Autowired
    private InventoryOutExtendMapper inventoryOutExtendMapper;
    @Autowired
    private InventoryOutCommodityMapper inventoryOutCommodityMapper;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private InventoryOutMapper inventoryOutMapper;
    @Autowired
    private OrderCommodityMapper orderCommodityMapper;
    @Autowired
    private InventoryInExtendMapper inventoryInExtendMapper;
    @Autowired
    private LogisticsMapper logisticsMapper;
    @Autowired
    private SampleOutService sampleOutService;
    @Autowired
    private CommodityService commodityService;
    @Autowired
    private InventoryHistoryService inventoryHistoryService;
    @Autowired
    private InventoryHistoryExtendMapper inventoryHistoryExtendMapper;
    @Autowired
    private GiftService giftService;


    public List<InventoryOutExtend> inventoryOutList(InventorySearchOption inventorySearchOption) {
        long a = System.currentTimeMillis();
        List<InventoryOutExtend> list = inventoryOutExtendMapper.inventoryOutList(inventorySearchOption);
        long b = System.currentTimeMillis();
        for (int i = 0; i < list.size(); i++) {
            InventoryOutExtend inventoryOutExtend = list.get(i);
            List<InventoryOutCommodityExtend> inventoryOutCommodities = inventoryOutCommodities(inventoryOutExtend.getInventory_out_id());
            inventoryOutExtend.setInventoryOutCommodities(inventoryOutCommodities);
        }
        long c = System.currentTimeMillis();
        System.out.println("inventoryOutList\t" + (b-a) + "ms");
        System.out.println("list\t" + (c-b) + "ms");
        return list;
    }

    public List<InventoryOutCommodityExtend> inventoryOutCommodities(int inventory_out_id) {
        InventorySearchOption inventorySearchOption = new InventorySearchOption();
        inventorySearchOption.setInventory_out_id(inventory_out_id);
        List<InventoryOutCommodityExtend> list = inventoryOutExtendMapper.inventoryOutCommodities(inventorySearchOption);
        return list;
    }

    public List<Integer> inventoryOutIds(InventorySearchOption inventorySearchOption) {
        return inventoryOutExtendMapper.inventoryOutIds(inventorySearchOption);
    }

    public Integer countInventoryOut(InventorySearchOption inventorySearchOption) {
        return inventoryOutExtendMapper.countInventoryOut(inventorySearchOption);
    }

    public synchronized void createInventoryOut(Object o, Employee employee) {
        if (o instanceof OrderExtend) {
            orderInventoryOut((OrderExtend) o, employee);
        }else if (o instanceof SampleOutExtend) {
            sampleOutInventoryOut((SampleOutExtend) o, employee);
        }else if (o instanceof OrderReturnExtend) {
            createOrderReturnInventoryOut(employee, (OrderReturnExtend) o);
        }else if (o instanceof GiftExtend) {
            giftInventoryOut((GiftExtend) o, employee);
        }
    }

    /**
     * 退还新增商品出库
     * @param employee
     * @param orderReturnExtend
     */
    public void createOrderReturnInventoryOut(Employee employee, OrderReturnExtend orderReturnExtend) {
        List<OrderReturnCommodityExtend> list = orderReturnExtend.getOrderReturnCommodities();
        InventoryOutExtend inventoryOutExtend = new InventoryOutExtend();
        inventoryOutExtend.setInventory_out_code(inventoryOutCode());
        inventoryOutExtend.setOrder_id(orderReturnExtend.getOrder_id());
        inventoryOutExtend.setRecord_employee_code(employee.getEmployee_code());
        inventoryOutExtend.setRecord_employee_name(employee.getEmployee_name());
        inventoryOutExtend.setInventory_out_create_date(DateUtil.currentDate());
        inventoryOutExtend.setInventory_out_type(InventoryConstant.INVENTORY_OUT_TYPE_ORDER_RETURN);
        inventoryOutExtend.setInventory_out_obj_id(orderReturnExtend.getOrder_return_id());
        inventoryOutExtendMapper.createInventoryOut(inventoryOutExtend);
        for (int i = 0; i < list.size(); i++) {
            OrderReturnCommodityExtend orderReturnCommodityExtend = list.get(i);
            int order_return_commodity_type = orderReturnCommodityExtend.getOrder_return_commodity_type();
            if (order_return_commodity_type == OrderConstants.ORDER_RETURN_COMMODITY_TYPE_ADD) {
                int sample = orderReturnCommodityExtend.getSample();
                InventoryOutCommodity inventoryOutCommodity = new InventoryOutCommodity();
                inventoryOutCommodity.setInventory_out_id(inventoryOutExtend.getInventory_out_id());
                inventoryOutCommodity.setCommodity_id(orderReturnCommodityExtend.getCommodity_id());
                Commodity commodity = commodityService.getCommodityById(orderReturnCommodityExtend.getCommodity_id());
                inventoryOutCommodity.setSupplier_id(commodity.getSupplier_id());
                inventoryOutCommodity.setOrder_commodity_num(orderReturnCommodityExtend.getOrder_return_commodity_num());//订单需求商品数量
                inventoryOutCommodity.setInventory_out_total_num(0);//出库总数量
                inventoryOutCommodity.setInventory_out_commodity_status(0);//未出库
                inventoryOutCommodity.setSample(sample);
                inventoryOutCommodity.setRetrun_commodity(0);
                inventoryOutCommodity.setOrder_commodity_id(orderReturnCommodityExtend.getOrder_commodity_id());
                inventoryOutExtendMapper.createInventoryOutCommodity(inventoryOutCommodity);
            }
        }
    }
    @Transactional
    public void createOrderReturnInventoryOut1(Employee employee, OrderReturnExtend orderReturnExtend) {
        List<OrderReturnCommodityExtend> list = orderReturnExtend.getOrderReturnCommodities();
        InventoryOutExtend inventoryOutExtend = new InventoryOutExtend();
        inventoryOutExtend.setInventory_out_code(inventoryOutCode());
        inventoryOutExtend.setOrder_id(orderReturnExtend.getOrder_id());
        inventoryOutExtend.setRecord_employee_code(employee.getEmployee_code());
        inventoryOutExtend.setRecord_employee_name(employee.getEmployee_name());
        inventoryOutExtend.setInventory_out_create_date(DateUtil.currentDate());
        inventoryOutExtend.setInventory_out_type(InventoryConstant.INVENTORY_OUT_TYPE_ORDER_RETURN);
        inventoryOutExtend.setInventory_out_obj_id(orderReturnExtend.getOrder_return_id());
        inventoryOutExtendMapper.createInventoryOut(inventoryOutExtend);
        for (int i = 0; i < list.size(); i++) {
            OrderReturnCommodityExtend orderReturnCommodityExtend = list.get(i);
            int order_return_commodity_type = orderReturnCommodityExtend.getOrder_return_commodity_type();
            if (order_return_commodity_type == OrderConstants.ORDER_RETURN_COMMODITY_TYPE_ADD) {
                int sample = orderReturnCommodityExtend.getSample();
                InventoryOutCommodity inventoryOutCommodity = new InventoryOutCommodity();
                inventoryOutCommodity.setInventory_out_id(inventoryOutExtend.getInventory_out_id());
                inventoryOutCommodity.setCommodity_id(orderReturnCommodityExtend.getCommodity_id());
                Commodity commodity = commodityService.getCommodityById(orderReturnCommodityExtend.getCommodity_id());
                inventoryOutCommodity.setSupplier_id(commodity.getSupplier_id());
                inventoryOutCommodity.setOrder_commodity_num(orderReturnCommodityExtend.getOrder_return_commodity_num());//订单需求商品数量
                inventoryOutCommodity.setInventory_out_total_num(0);//出库总数量
                inventoryOutCommodity.setInventory_out_commodity_status(0);//未出库
                inventoryOutCommodity.setSample(sample);
                inventoryOutCommodity.setRetrun_commodity(0);
                inventoryOutCommodity.setOrder_commodity_id(orderReturnCommodityExtend.getOrder_commodity_id());
                inventoryOutExtendMapper.createInventoryOutCommodity(inventoryOutCommodity);
            }
        }
    }

    /**
     * 确认采购入库转化出库单
     * @param orderExtend
     * @param employee
     */
    private void orderInventoryOut(OrderExtend orderExtend, Employee employee) {
        InventoryOutExtend inventoryOutExtend = new InventoryOutExtend();
        inventoryOutExtend.setInventory_out_code(inventoryOutCode());
        inventoryOutExtend.setOrder_id(orderExtend.getOrder_id());
        inventoryOutExtend.setRecord_employee_code(employee.getEmployee_code());
        inventoryOutExtend.setRecord_employee_name(employee.getEmployee_name());
        inventoryOutExtend.setInventory_out_create_date(DateUtil.currentDate());
        inventoryOutExtend.setInventory_out_type(InventoryConstant.INVENTORY_OUT_TYPE_ORDER);
        inventoryOutExtend.setInventory_out_obj_id(orderExtend.getOrder_id());
        inventoryOutExtendMapper.createInventoryOut(inventoryOutExtend);
        List<OrderCommodityExtend> list = orderExtend.getOrderCommodities();
        for (int i = 0; i < list.size(); i++) {
            OrderCommodityExtend orderCommodityExtend = list.get(i);
            InventoryOutCommodity inventoryOutCommodity = new InventoryOutCommodity();
            inventoryOutCommodity.setInventory_out_id(inventoryOutExtend.getInventory_out_id());
            inventoryOutCommodity.setCommodity_id(orderCommodityExtend.getCommodity_id());
            Commodity commodity = commodityService.getCommodityById(orderCommodityExtend.getCommodity_id());
            inventoryOutCommodity.setSupplier_id(commodity.getSupplier_id());
            inventoryOutCommodity.setOrder_commodity_num(orderCommodityExtend.getCommodity_num());//订单需求商品数量
            inventoryOutCommodity.setInventory_out_total_num(0);//出库总数量
            inventoryOutCommodity.setInventory_out_commodity_status(0);//未出库
            inventoryOutCommodity.setSample(orderCommodityExtend.getSample());
            inventoryOutCommodity.setRetrun_commodity(orderCommodityExtend.getReturn_commodity());
            inventoryOutCommodity.setOrder_commodity_id(orderCommodityExtend.getOrder_commodity_id());
            inventoryOutExtendMapper.createInventoryOutCommodity(inventoryOutCommodity);
        }
    }

    /**
     * 下样出库
     * @param sampleOutExtend
     * @param employee
     */
    private void sampleOutInventoryOut(SampleOutExtend sampleOutExtend, Employee employee) {
        InventoryOutExtend inventoryOutExtend = new InventoryOutExtend();
        inventoryOutExtend.setInventory_out_code(inventoryOutCode());
        inventoryOutExtend.setRecord_employee_code(employee.getEmployee_code());
        inventoryOutExtend.setRecord_employee_name(employee.getEmployee_name());
        inventoryOutExtend.setInventory_out_create_date(DateUtil.currentDate());
        inventoryOutExtend.setInventory_out_type(InventoryConstant.INVENTORY_OUT_TYPE_SAMPLE_OUT);
        inventoryOutExtend.setInventory_out_obj_id(sampleOutExtend.getCommodity_sample_out_id());
        inventoryOutExtendMapper.createInventoryOut(inventoryOutExtend);
        sampleOutExtend.setInventory_out_id(inventoryOutExtend.getInventory_out_id());
        List<SampleOutDetailExtend> list = sampleOutExtend.getSampleOutDetails();
        for (int i = 0; i < list.size(); i++) {
            SampleOutDetailExtend sampleOutDetailExtend = list.get(i);
            CommodityNum commodityNum = inventoryService.getCommodityNumById(sampleOutDetailExtend.getCommodity_num_id());
            InventoryOutCommodity inventoryOutCommodity = new InventoryOutCommodity();
            inventoryOutCommodity.setInventory_out_id(inventoryOutExtend.getInventory_out_id());
            inventoryOutCommodity.setCommodity_id(sampleOutDetailExtend.getCommodity_id());
            inventoryOutCommodity.setSupplier_id(inventoryOutCommodity.getSupplier_id());
            inventoryOutCommodity.setOrder_commodity_num(sampleOutDetailExtend.getCommodity_sample_out_num());//订单需求商品数量
            inventoryOutCommodity.setInventory_out_total_num(0);//出库总数量
            inventoryOutCommodity.setInventory_out_commodity_status(0);//未出库
            inventoryOutCommodity.setSample(commodityNum.getSample());
            inventoryOutCommodity.setRetrun_commodity(commodityNum.getReturn_commodity());
            inventoryOutExtendMapper.createInventoryOutCommodity(inventoryOutCommodity);
            sampleOutDetailExtend.setInventory_out_commodity_id(inventoryOutCommodity.getInventory_out_commodity_id());
        }
    }

    /**
     * 礼品出库
     * @param giftExtend
     * @param employee
     */
    public void giftInventoryOut(GiftExtend giftExtend, Employee employee) {
        InventoryOutExtend inventoryOutExtend = new InventoryOutExtend();
        inventoryOutExtend.setInventory_out_code(inventoryOutCode());
        inventoryOutExtend.setRecord_employee_code(employee.getEmployee_code());
        inventoryOutExtend.setRecord_employee_name(employee.getEmployee_name());
        inventoryOutExtend.setInventory_out_create_date(DateUtil.currentDate());
        inventoryOutExtend.setInventory_out_type(InventoryConstant.INVENTORY_OUT_TYPE_GIFT);
        inventoryOutExtend.setInventory_out_obj_id(giftExtend.getGift_id());
        inventoryOutExtend.setOrder_id(giftExtend.getOrder_id());
        inventoryOutExtendMapper.createInventoryOut(inventoryOutExtend);
        List<GiftCommodityExtend> list = giftExtend.getGiftCommodities();
        for (int i = 0; i < list.size(); i++) {
            GiftCommodityExtend giftCommodityExtend = list.get(i);
            InventoryOutCommodity inventoryOutCommodity = new InventoryOutCommodity();
            inventoryOutCommodity.setInventory_out_id(inventoryOutExtend.getInventory_out_id());
            inventoryOutCommodity.setCommodity_id(giftCommodityExtend.getCommodity_id());
            inventoryOutCommodity.setSupplier_id(inventoryOutCommodity.getSupplier_id());
            inventoryOutCommodity.setOrder_commodity_num(giftCommodityExtend.getGift_commodity_num());//订单需求商品数量
            inventoryOutCommodity.setInventory_out_total_num(0);//出库总数量
            inventoryOutCommodity.setInventory_out_commodity_status(0);//未出库
            inventoryOutCommodity.setSample(giftCommodityExtend.getGift_commodity_sample());
            inventoryOutCommodity.setRetrun_commodity(0);
            inventoryOutCommodity.setGift_commodity_id(giftCommodityExtend.getGift_commodity_id());
            inventoryOutExtendMapper.createInventoryOutCommodity(inventoryOutCommodity);
        }
    }

    private String inventoryOutCode() {
        String inventoryOutCode = inventoryOutExtendMapper.inventoryOutCode(DateUtil.format(DateUtil.currentDate(), "yyMMdd"));
        if (inventoryOutCode == null) {
            return "PO" + DateUtil.format(DateUtil.currentDate(), "yyMMdd") + "001";
        }
        inventoryOutCode = inventoryOutCode.substring(2, inventoryOutCode.length());
        int n = Integer.parseInt(inventoryOutCode);
        n++;
        return "PO"+n;
    }

    public InventoryOutCommodity getInventoryOutCommodityById(int inventory_out_commodity_id) {
        return inventoryOutCommodityMapper.selectByPrimaryKey(inventory_out_commodity_id);
    }

    public InventoryOutCommodity getInventoryOutCommodityByOrderCommodityId(int order_commodity_id) {
        return inventoryOutExtendMapper.getInventoryOutCommodityByOrderCommodityId(order_commodity_id);
    }

    @Transactional
    public void commodityOut(JSONArray jsonArray, Employee employee, String inventory_out_remark, Date inventoryDate) {
        InventoryOutCommodity inven = inventoryOutId(jsonArray);
        InventoryOutExtend inventoryOutExtend = getInventoryOutById(inven.getInventory_out_id());
        if (inventoryOutExtend.getInventory_out_type() == InventoryConstant.INVENTORY_OUT_TYPE_ORDER
                || inventoryOutExtend.getInventory_out_type() == InventoryConstant.INVENTORY_OUT_TYPE_ORDER_RETURN) {
            createOrderCommodityOut(jsonArray, employee, inventory_out_remark, inventoryDate, inven, inventoryOutExtend);
        }else if (inventoryOutExtend.getInventory_out_type() == InventoryConstant.INVENTORY_OUT_TYPE_GIFT) {
            createGiftCommodityOut(jsonArray, employee, inventory_out_remark, inventoryDate, inven, inventoryOutExtend);
        }
    }

    /**
     * 出库
     * @param jsonArray
     * @param employee
     */
    public void createOrderCommodityOut(JSONArray jsonArray, Employee employee, String inventory_out_remark, Date inventoryDate,
                                        InventoryOutCommodity inven, InventoryOutExtend inventoryOutExtend) {
        OrderCommodity orderCommodity = orderCommodityMapper.selectByPrimaryKey(inven.getOrder_commodity_id());
        int order_id = orderCommodity.getOrder_id();
        Order order = orderService.getOrderById(order_id);
        String remark = order.getOrder_remark();
        int size = jsonArray.size();
        if (size == 0) {
            throw new RuntimeException("无出库商品");
        }
        InventoryHistory inventoryHistory = new InventoryHistory();
        inventoryHistory.setInventory_date(inventoryDate);
        inventoryHistory.setInventory_employee_code(employee.getEmployee_code());
        inventoryHistory.setInventory_employee_name(employee.getEmployee_name());
        inventoryHistory.setInventory_out_id(inven.getInventory_out_id());
        inventoryHistory.setOrder_id(order_id);
        inventoryHistory.setInventory_type(InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_OUT);//0入库， 1出库
        inventoryHistory.setInventory_out_fee_check_status(OrderConstants.inventory_out_fee_check_status_not_create);
        inventoryHistory.setInventory_history_settlement_status(0);
        if (inventory_out_remark == null || inventory_out_remark.equals("")) {
            inventoryHistory.setInventory_history_remark(remark);
        }else {
            inventoryHistory.setInventory_history_remark(inventory_out_remark);
        }

        boolean noCommodity = true;
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(i).toString());
            JSONArray commodityNa = JSON.parseArray(jsonObject.get("commodityNa").toString());
            int supplier_out = Integer.parseInt(jsonObject.get("supplier_out").toString());//是否供应商发货
            if (commodityNa.size() > 0 || supplier_out == 1) {
                noCommodity = false;
            }
        }
        if (noCommodity) {
            throw new RuntimeException("无出库商品");
        }
        String inventoryHistoryCode = inventoryHistoryService.inventoryHistoryCode(null);
        inventoryHistory.setInventory_history_code(inventoryHistoryCode);
        inventoryHistory.setInventory_history_in_out(InventoryHistoryConstant.INVENTORY_HISTORY_OUT);
        inventoryHistoryExtendMapper.createInventoryHistory(inventoryHistory);
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(i).toString());
            int inventory_out_commodity_id = Integer.parseInt(jsonObject.get("inventory_out_commodity_id").toString());//出库id
            int supplier_out = Integer.parseInt(jsonObject.get("supplier_out").toString());//是否供应商发货
            InventoryOutCommodity inventoryOutCommodity = inventoryOutCommodityMapper.selectByPrimaryKey(inventory_out_commodity_id);
            OrderCommodity orderCommodity1 = orderService.getOrderCommodity(inventoryOutCommodity.getOrder_commodity_id());
            if (supplier_out == 1) {
                InventoryHistoryDetail inventoryHistoryDetail = new InventoryHistoryDetail();
                inventoryHistoryDetail.setCommodity_num_id(0);
                inventoryHistoryDetail.setInventory_out_commodity_id(inventoryOutCommodity.getInventory_out_commodity_id());
                inventoryHistoryDetail.setInventory_history_id(inventoryHistory.getInventory_history_id());
                inventoryHistoryDetail.setInventory_history_num(0);
                inventoryHistoryDetail.setInventory_in_out(1);
                inventoryHistoryDetail.setCommodity_id(inventoryOutCommodity.getCommodity_id());
                inventoryHistoryDetail.setSample(orderCommodity1.getSample());
                inventoryHistoryDetail.setSupplier_out(1);
                inventoryHistoryDetail.setInventory_history_return_num(0);
                inventoryHistoryDetail.setCommodity_num(0);
                inventoryHistoryExtendMapper.createInventoryHistoryDetail(inventoryHistoryDetail);
                inventoryOutCommodity.setInventory_out_commodity_status(3);
                inventoryOutCommodityMapper.updateByPrimaryKey(inventoryOutCommodity);
            }else {
                JSONArray commodityNa = JSON.parseArray(jsonObject.get("commodityNa").toString());
                for (int j = 0; j < commodityNa.size(); j++) {
                    JSONObject commodityNumDetail = JSONObject.parseObject(commodityNa.get(j).toString());
                    int inventory_out_num = Integer.parseInt(commodityNumDetail.get("inventory_out_num").toString());
                    if (commodityNumDetail.get("commodity_num_id").toString().equals("")) {
                        if (orderCommodity1.getSample() == 1) {//库存数量不足并且时样品，直接出样
                            inventoryOutCommodity.setInventory_out_commodity_status(2);
                            InventoryHistoryDetail inventoryHistoryDetail = new InventoryHistoryDetail();
                            inventoryHistoryDetail.setCommodity_num_id(0);
                            inventoryHistoryDetail.setInventory_out_commodity_id(inventoryOutCommodity.getInventory_out_commodity_id());
                            inventoryHistoryDetail.setInventory_history_id(inventoryHistory.getInventory_history_id());
                            inventoryHistoryDetail.setInventory_history_num(inventory_out_num);
                            inventoryHistoryDetail.setInventory_in_out(1);
                            inventoryHistoryDetail.setCommodity_id(inventoryOutCommodity.getCommodity_id());
                            inventoryHistoryDetail.setSample(orderCommodity1.getSample());
                            inventoryHistoryDetail.setSupplier_out(0);
                            inventoryHistoryDetail.setInventory_history_return_num(0);
                            inventoryHistoryDetail.setCommodity_num(0);
                            inventoryHistoryExtendMapper.createInventoryHistoryDetail(inventoryHistoryDetail);
                            inventoryOutCommodityMapper.updateByPrimaryKey(inventoryOutCommodity);
                            continue;
                        } else {
                            throw new RuntimeException("样品数量不足!");
                        }
                    }
                    int commodity_num_id = Integer.parseInt(commodityNumDetail.get("commodity_num_id").toString());
                    CommodityNum commodityNum = inventoryService.getCommodityNumById(commodity_num_id);
                    if (commodityNum == null || commodityNum.getNum() < inventory_out_num) {//库存数量不足
                        throw new RuntimeException("库存数量不足!");
                    }
                    int need_out_num = orderCommodity1.getCommodity_num();//应出库数量
                    if (need_out_num == 0) {
                        throw new RuntimeException("已全部退货，不许要出库");
                    }
                    if (inventoryOutCommodity.getInventory_out_total_num() + inventory_out_num > need_out_num) {
                        throw new RuntimeException("出多了");
                    }
                    commodityNum.setNum(commodityNum.getNum() - inventory_out_num);
                    inventoryService.updateCommodityNum(commodityNum);
                    if (inventoryOutCommodity.getInventory_out_total_num() == null) {
                        inventoryOutCommodity.setInventory_out_total_num(inventory_out_num);
                    }else {
                        inventoryOutCommodity.setInventory_out_total_num(inventory_out_num + inventoryOutCommodity.getInventory_out_total_num());
                    }

                    int inventory_out_total_num = inventoryOutCommodity.getInventory_out_total_num();//已出库数量

                    //1部分出库，2完全出库
                    if (inventory_out_total_num >= need_out_num) {
                        inventoryOutCommodity.setInventory_out_commodity_status(2);
                    }else {
                        inventoryOutCommodity.setInventory_out_commodity_status(1);
                    }

                    InventoryHistoryDetail inventoryHistoryDetail = new InventoryHistoryDetail();
                    inventoryHistoryDetail.setCommodity_num_id(commodityNum.getCommodity_num_id());
                    inventoryHistoryDetail.setInventory_out_commodity_id(inventoryOutCommodity.getInventory_out_commodity_id());
                    inventoryHistoryDetail.setInventory_history_id(inventoryHistory.getInventory_history_id());
                    inventoryHistoryDetail.setInventory_history_num(inventory_out_num);
                    inventoryHistoryDetail.setInventory_in_out(1);
                    inventoryHistoryDetail.setCommodity_id(inventoryOutCommodity.getCommodity_id());
                    inventoryHistoryDetail.setSample(commodityNum.getSample());
                    inventoryHistoryDetail.setSupplier_out(0);
                    inventoryHistoryDetail.setInventory_history_return_num(0);
                    inventoryHistoryDetail.setCommodity_num(commodityNum.getNum());
                    inventoryHistoryExtendMapper.createInventoryHistoryDetail(inventoryHistoryDetail);
                    inventoryOutCommodityMapper.updateByPrimaryKey(inventoryOutCommodity);
                }
            }
        }

        Logistics logistics = new Logistics();
        logistics.setInventory_history_id(inventoryHistory.getInventory_history_id());
        logistics.setLogistics_status(LogisticsConstants.LOGISTICS_STATUS_DISTRIBUTION);
        inventoryOutExtendMapper.createLogistics(logistics);

        inventoryOutStatus(inventoryOutExtend.getInventory_out_id());
        //订单状态同步
        orderStatus(order_id);
    }

    public void createGiftCommodityOut(JSONArray jsonArray, Employee employee, String inventory_out_remark, Date inventoryDate,
                                       InventoryOutCommodity inven, InventoryOutExtend inventoryOutExtend) {
        String remark = null;
        Integer gift_id = inventoryOutExtend.getInventory_out_obj_id();
        GiftExtend giftById = giftService.getGiftById(gift_id);
        remark = giftById.getGift_remark();
        int size = jsonArray.size();
        if (size == 0) {
            throw new RuntimeException("无出库商品");
        }
        InventoryHistory inventoryHistory = new InventoryHistory();
        inventoryHistory.setInventory_date(inventoryDate);
        inventoryHistory.setInventory_employee_code(employee.getEmployee_code());
        inventoryHistory.setInventory_employee_name(employee.getEmployee_name());
        inventoryHistory.setInventory_out_id(inven.getInventory_out_id());
        inventoryHistory.setOrder_id(giftById.getOrder_id());
        inventoryHistory.setInventory_type(InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_GIFT_OUT);//0入库， 1出库
        inventoryHistory.setInventory_out_fee_check_status(OrderConstants.inventory_out_fee_check_status_not_create);
        inventoryHistory.setInventory_history_settlement_status(0);
        if (inventory_out_remark == null || inventory_out_remark.equals("")) {
            inventoryHistory.setInventory_history_remark(remark);
        }else {
            inventoryHistory.setInventory_history_remark(inventory_out_remark);
        }

        boolean noCommodity = true;
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(i).toString());
            JSONArray commodityNa = JSON.parseArray(jsonObject.get("commodityNa").toString());
            int supplier_out = Integer.parseInt(jsonObject.get("supplier_out").toString());//是否供应商发货
            if (commodityNa.size() > 0 || supplier_out == 1) {
                noCommodity = false;
            }
        }
        if (noCommodity) {
            throw new RuntimeException("无出库商品");
        }
        String inventoryHistoryCode = inventoryHistoryService.inventoryHistoryCode(null);
        inventoryHistory.setInventory_history_code(inventoryHistoryCode);
        inventoryHistory.setInventory_history_in_out(InventoryHistoryConstant.INVENTORY_HISTORY_OUT);
        inventoryHistory.setGift_id(gift_id);
        inventoryHistoryExtendMapper.createInventoryHistory(inventoryHistory);
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(i).toString());
            int inventory_out_commodity_id = Integer.parseInt(jsonObject.get("inventory_out_commodity_id").toString());//出库id
            int supplier_out = Integer.parseInt(jsonObject.get("supplier_out").toString());//是否供应商发货
            InventoryOutCommodity inventoryOutCommodity = inventoryOutCommodityMapper.selectByPrimaryKey(inventory_out_commodity_id);
            GiftCommodityExtend giftCommodityExtend = giftService.getGiftCommodityById(inventoryOutCommodity.getGift_commodity_id());
            if (supplier_out == 1) {
                InventoryHistoryDetail inventoryHistoryDetail = new InventoryHistoryDetail();
                inventoryHistoryDetail.setCommodity_num_id(0);
                inventoryHistoryDetail.setInventory_out_commodity_id(inventoryOutCommodity.getInventory_out_commodity_id());
                inventoryHistoryDetail.setInventory_history_id(inventoryHistory.getInventory_history_id());
                inventoryHistoryDetail.setInventory_history_num(0);
                inventoryHistoryDetail.setInventory_in_out(1);
                inventoryHistoryDetail.setCommodity_id(inventoryOutCommodity.getCommodity_id());
                inventoryHistoryDetail.setSample(giftCommodityExtend.getGift_commodity_sample());
                inventoryHistoryDetail.setSupplier_out(1);
                inventoryHistoryDetail.setInventory_history_return_num(0);
                inventoryHistoryDetail.setCommodity_num(0);
                inventoryHistoryExtendMapper.createInventoryHistoryDetail(inventoryHistoryDetail);
                inventoryOutCommodity.setInventory_out_commodity_status(3);
                inventoryOutCommodityMapper.updateByPrimaryKey(inventoryOutCommodity);
            }else {
                JSONArray commodityNa = JSON.parseArray(jsonObject.get("commodityNa").toString());
                for (int j = 0; j < commodityNa.size(); j++) {
                    JSONObject commodityNumDetail = JSONObject.parseObject(commodityNa.get(j).toString());
                    int inventory_out_num = Integer.parseInt(commodityNumDetail.get("inventory_out_num").toString());
                    if (commodityNumDetail.get("commodity_num_id").toString().equals("")) {
                        if (giftCommodityExtend.getGift_commodity_sample() == 1) {//库存数量不足并且时样品，直接出样
                            inventoryOutCommodity.setInventory_out_commodity_status(2);
                            InventoryHistoryDetail inventoryHistoryDetail = new InventoryHistoryDetail();
                            inventoryHistoryDetail.setCommodity_num_id(0);
                            inventoryHistoryDetail.setInventory_out_commodity_id(inventoryOutCommodity.getInventory_out_commodity_id());
                            inventoryHistoryDetail.setInventory_history_id(inventoryHistory.getInventory_history_id());
                            inventoryHistoryDetail.setInventory_history_num(inventory_out_num);
                            inventoryHistoryDetail.setInventory_in_out(1);
                            inventoryHistoryDetail.setCommodity_id(inventoryOutCommodity.getCommodity_id());
                            inventoryHistoryDetail.setSample(giftCommodityExtend.getGift_commodity_sample());
                            inventoryHistoryDetail.setSupplier_out(0);
                            inventoryHistoryDetail.setInventory_history_return_num(0);
                            inventoryHistoryDetail.setCommodity_num(0);
                            inventoryHistoryExtendMapper.createInventoryHistoryDetail(inventoryHistoryDetail);
                            inventoryOutCommodityMapper.updateByPrimaryKey(inventoryOutCommodity);
                            continue;
                        } else {
                            throw new RuntimeException("样品数量不足!");
                        }
                    }
                    int commodity_num_id = Integer.parseInt(commodityNumDetail.get("commodity_num_id").toString());
                    CommodityNum commodityNum = inventoryService.getCommodityNumById(commodity_num_id);
                    if (commodityNum == null || commodityNum.getNum() < inventory_out_num) {//库存数量不足
                        throw new RuntimeException("库存数量不足!");
                    }

                    int need_out_num = giftCommodityExtend.getGift_commodity_num();//应出库数量
                    if (need_out_num == 0) {
                        throw new RuntimeException("已全部退货，不许要出库");
                    }
                    if (inventoryOutCommodity.getInventory_out_total_num() + inventory_out_num > need_out_num) {
                        throw new RuntimeException("出多了");
                    }

                    if (inventoryOutCommodity.getInventory_out_total_num() == null) {
                        inventoryOutCommodity.setInventory_out_total_num(inventory_out_num);
                    }else {
                        inventoryOutCommodity.setInventory_out_total_num(inventory_out_num + inventoryOutCommodity.getInventory_out_total_num());
                    }
                    commodityNum.setNum(commodityNum.getNum() - inventory_out_num);
                    inventoryService.updateCommodityNum(commodityNum);

                    //1部分出库，2完全出库
                    int inventory_out_total_num = inventoryOutCommodity.getInventory_out_total_num();//已出库数量
                    if (inventory_out_total_num >= need_out_num) {
                        inventoryOutCommodity.setInventory_out_commodity_status(2);
                    }else {
                        inventoryOutCommodity.setInventory_out_commodity_status(1);
                    }

                    InventoryHistoryDetail inventoryHistoryDetail = new InventoryHistoryDetail();
                    inventoryHistoryDetail.setCommodity_num_id(commodityNum.getCommodity_num_id());
                    inventoryHistoryDetail.setInventory_out_commodity_id(inventoryOutCommodity.getInventory_out_commodity_id());
                    inventoryHistoryDetail.setInventory_history_id(inventoryHistory.getInventory_history_id());
                    inventoryHistoryDetail.setInventory_history_num(inventory_out_num);
                    inventoryHistoryDetail.setInventory_in_out(1);
                    inventoryHistoryDetail.setCommodity_id(inventoryOutCommodity.getCommodity_id());
                    inventoryHistoryDetail.setSample(commodityNum.getSample());
                    inventoryHistoryDetail.setSupplier_out(0);
                    inventoryHistoryDetail.setInventory_history_return_num(0);
                    inventoryHistoryDetail.setCommodity_num(commodityNum.getNum());
                    inventoryHistoryExtendMapper.createInventoryHistoryDetail(inventoryHistoryDetail);
                    inventoryOutCommodityMapper.updateByPrimaryKey(inventoryOutCommodity);
                }
            }
        }

        Logistics logistics = new Logistics();
        logistics.setInventory_history_id(inventoryHistory.getInventory_history_id());
        logistics.setLogistics_status(LogisticsConstants.LOGISTICS_STATUS_DISTRIBUTION);
        inventoryOutExtendMapper.createLogistics(logistics);

        inventoryOutStatus(inventoryOutExtend.getInventory_out_id());
        giftStatus(gift_id);
    }

    @Transactional
    public InventoryHistory giftInventoryHistory(GiftExtend giftExtend, Employee employee) {
        if (giftExtend.getGift_out_status() == 200) {
            return null;
        }
        List<GiftCommodityExtend> giftCommodities = giftExtend.getGiftCommodities();
        InventorySearchOption inventorySearchOption = new InventorySearchOption();
        inventorySearchOption.setInventory_out_type(InventoryConstant.INVENTORY_OUT_TYPE_GIFT);
        inventorySearchOption.setInventory_out_obj_id(giftExtend.getGift_id());
        List<Integer> list = inventoryOutIds(inventorySearchOption);
        if (list != null && list.size() == 1) {
            inventorySearchOption.setInventoryOutIds(list);
            List<InventoryOutExtend> inventoryOutExtends = inventoryOutList(inventorySearchOption);
            InventoryOutExtend inventoryOutExtend = inventoryOutExtends.get(0);
            List<InventoryOutCommodityExtend> inventoryOutCommodities = inventoryOutExtend.getInventoryOutCommodities();

            InventoryHistory inventoryHistory = new InventoryHistory();
            inventoryHistory.setInventory_date(DateUtil.currentDate());
            inventoryHistory.setInventory_employee_code(employee.getEmployee_code());
            inventoryHistory.setInventory_employee_name(employee.getEmployee_name());
            inventoryHistory.setInventory_out_id(inventoryOutExtend.getInventory_out_id());
            inventoryHistory.setInventory_type(InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_GIFT_OUT);
            inventoryHistory.setInventory_history_code(inventoryHistoryService.inventoryHistoryCode(null));
            inventoryHistory.setInventory_history_settlement_status(0);
            inventoryHistory.setInventory_history_in_out(InventoryHistoryConstant.INVENTORY_HISTORY_OUT);
            inventoryHistory.setGift_id(giftExtend.getGift_id());
            inventoryHistoryExtendMapper.createInventoryHistory(inventoryHistory);

            for (int i = 0; i < inventoryOutCommodities.size(); i++) {
                InventoryOutCommodityExtend inventoryOutCommodity = inventoryOutCommodities.get(i);
                Integer gift_commodity_id = inventoryOutCommodity.getGift_commodity_id();
                GiftCommodityExtend giftCommodityExtend = null;
                for (int j = 0; j < giftCommodities.size(); j++) {
                    giftCommodityExtend = giftCommodities.get(j);
                    if (giftCommodityExtend.getGift_commodity_id().equals(gift_commodity_id)) {
                        break;
                    }
                }
                Integer gift_commodity_num = giftCommodityExtend.getGift_commodity_num();
                Integer commodity_num_id = giftCommodityExtend.getCommodity_num_id();
                CommodityNum commodityNum = inventoryService.getCommodityNumById(commodity_num_id);
                if (commodityNum == null || commodityNum.getNum() < gift_commodity_num) {//库存数量不足
                    throw new RuntimeException("库存数量不足!");
                }
                inventoryService.commodityNumAdd(commodity_num_id, -gift_commodity_num);

                InventoryHistoryDetail inventoryHistoryDetail = new InventoryHistoryDetail();
                inventoryHistoryDetail.setCommodity_num_id(commodityNum.getCommodity_num_id());
                inventoryHistoryDetail.setInventory_out_commodity_id(inventoryOutCommodity.getInventory_out_commodity_id());
                inventoryHistoryDetail.setInventory_history_id(inventoryHistory.getInventory_history_id());
                inventoryHistoryDetail.setInventory_history_num(gift_commodity_num);
                inventoryHistoryDetail.setInventory_in_out(1);
                inventoryHistoryDetail.setCommodity_id(inventoryOutCommodity.getCommodity_id());
                inventoryHistoryDetail.setSample(commodityNum.getSample());
                inventoryHistoryDetail.setSupplier_out(0);
                inventoryHistoryDetail.setInventory_history_return_num(0);
                inventoryHistoryDetail.setCommodity_num(commodityNum.getNum());
                inventoryHistoryExtendMapper.createInventoryHistoryDetail(inventoryHistoryDetail);
                inventoryOutCommodity.setInventory_out_commodity_status(2);
                inventoryOutCommodity.setInventory_out_total_num(inventoryOutCommodity.getOrder_commodity_num());
                inventoryOutCommodityMapper.updateByPrimaryKey(inventoryOutCommodity);
                giftCommodityExtend.setGift_commodity_out_status(OrderConstants.ORDER_COMMODITY_STATUS_COMPLETE_OUT);//全部出库
                giftService.updateGiftCommodity(giftCommodityExtend);
            }

            Logistics logistics = new Logistics();
            logistics.setInventory_history_id(inventoryHistory.getInventory_history_id());
            logistics.setLogistics_status(LogisticsConstants.LOGISTICS_STATUS_DISTRIBUTION);
            inventoryOutExtendMapper.createLogistics(logistics);

            giftExtend.setGift_out_status(200);
            giftService.updateGift(giftExtend);
            return inventoryHistory;
        }
        return null;
    }

    /**
     * 下样出库
     * @param commoditySampleOutDetail
     * @param employee
     */
    @Transactional
    public void sampleOutInventoryOut(CommoditySampleOutDetail commoditySampleOutDetail, Employee employee) {
        if (commoditySampleOutDetail.getInventory_out_status() == 2) {
            return;
        }
        InventoryOutCommodity inventoryOutCommodity = getInventoryOutCommodityById(commoditySampleOutDetail.getInventory_out_commodity_id());
        InventoryOut inventoryOut = inventoryOutMapper.selectByPrimaryKey(inventoryOutCommodity.getInventory_out_id());
        InventoryHistory inventoryHistory = new InventoryHistory();
        inventoryHistory.setInventory_date(DateUtil.currentDate());
        inventoryHistory.setInventory_employee_code(employee.getEmployee_code());
        inventoryHistory.setInventory_employee_name(employee.getEmployee_name());
        inventoryHistory.setInventory_out_id(inventoryOut.getInventory_out_id());
        inventoryHistory.setInventory_type(InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_SAMPLE_OUT);//0入库， 1出库,2,3出样
        inventoryOut.setInventory_out_status(2);
        inventoryOut.setInventory_out_complete_date(DateUtil.currentDate());
        inventoryHistory.setInventory_history_settlement_status(0);
        String inventoryHistoryCode = inventoryHistoryService.inventoryHistoryCode(null);
        inventoryHistory.setInventory_history_code(inventoryHistoryCode);
        inventoryHistory.setInventory_history_in_out(InventoryHistoryConstant.INVENTORY_HISTORY_OUT);
        inventoryHistoryExtendMapper.createInventoryHistory(inventoryHistory);
        int commodity_num_id = commoditySampleOutDetail.getCommodity_num_id();
        CommodityNum commodityNum = inventoryService.getCommodityNumById(commodity_num_id);
        if (commodityNum.getNum() < commoditySampleOutDetail.getCommodity_sample_out_num()) {
            throw new RuntimeException("库存数量不足");
        }
        commodityNum.setNum(commodityNum.getNum() - commoditySampleOutDetail.getCommodity_sample_out_num());
        InventoryHistoryDetail inventoryHistoryDetail = new InventoryHistoryDetail();
        inventoryHistoryDetail.setCommodity_num_id(commodity_num_id);
        inventoryHistoryDetail.setInventory_out_commodity_id(inventoryOutCommodity.getInventory_out_commodity_id());
        inventoryHistoryDetail.setInventory_history_id(inventoryHistory.getInventory_history_id());
        inventoryHistoryDetail.setInventory_history_num(commoditySampleOutDetail.getCommodity_sample_out_num());
        inventoryHistoryDetail.setInventory_in_out(0);
        inventoryHistoryDetail.setSample(inventoryOutCommodity.getSample());
        inventoryHistoryDetail.setCommodity_id(inventoryOutCommodity.getCommodity_id());
        inventoryHistoryDetail.setInventory_history_return_num(0);
        inventoryOutCommodity.setInventory_out_commodity_status(2);
        inventoryService.updateCommodityNum(commodityNum);
        inventoryHistoryDetail.setCommodity_num(commodityNum.getNum());
        inventoryHistoryExtendMapper.createInventoryHistoryDetail(inventoryHistoryDetail);
        commoditySampleOutDetail.setInventory_out_status(2);
        sampleOutService.sampleOutStatus(commoditySampleOutDetail);
        inventoryOutMapper.updateByPrimaryKey(inventoryOut);
        inventoryOutCommodityMapper.updateByPrimaryKey(inventoryOutCommodity);
    }

    /**
     * 全部退货不需要出库
     * @param inventory_out_commodity_ids
     */
    @Transactional
    public void noNeedInventoryOut(String inventory_out_commodity_ids) {
        Set<Integer> ids = SplitUtils.splitToSet(inventory_out_commodity_ids, ",");
        Integer order_id = null;
        Integer inventory_out_id = null;
        for (int inventory_out_commodity_id:ids) {
            InventoryOutCommodity inventoryOutCommodity = inventoryOutCommodityMapper.selectByPrimaryKey(inventory_out_commodity_id);
            OrderCommodity orderCommodity = orderService.getOrderCommodity(inventoryOutCommodity.getOrder_commodity_id());
            order_id = orderCommodity.getOrder_id();
            inventory_out_id = inventoryOutCommodity.getInventory_out_id();
            Integer commodity_num = orderCommodity.getCommodity_num();//应出库数量
            Integer order_commodity_num = orderCommodity.getOrder_commodity_num();
            if (commodity_num > 0 && commodity_num < order_commodity_num) {//部分退货
                inventoryOutCommodity.setInventory_out_commodity_status(-1);
                inventoryOutCommodityMapper.updateByPrimaryKey(inventoryOutCommodity);
            }else if(commodity_num == 0){//全部退货
                inventoryOutCommodity.setInventory_out_commodity_status(-2);
                inventoryOutCommodityMapper.updateByPrimaryKey(inventoryOutCommodity);
            }
        }
        inventoryOutStatus(inventory_out_id);
        InventoryOutExtend inventoryOutExtend = getInventoryOutById(inventory_out_id);
        Integer inventory_out_type = inventoryOutExtend.getInventory_out_type();
        if (inventory_out_type.equals(InventoryConstant.INVENTORY_OUT_TYPE_GIFT)) {
            Integer gift_id = inventoryOutExtend.getInventory_out_obj_id();
            giftStatus(gift_id);
        }else if (inventory_out_type.equals(InventoryConstant.INVENTORY_OUT_TYPE_ORDER)) {
            //订单状态同步
            orderStatus(order_id);
        }
    }

    private InventoryOutCommodity inventoryOutId(JSONArray jsonArray) {
        JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(0).toString());
        int inventory_out_commodity_id = Integer.parseInt(jsonObject.get("inventory_out_commodity_id").toString());//出库id
        InventoryOutCommodity inventoryOutCommodity = inventoryOutCommodityMapper.selectByPrimaryKey(inventory_out_commodity_id);
        return inventoryOutCommodity;
    }

    public void orderStatus(int order_id) {
        OrderExtend orderExtend = orderService.getOrderById(order_id);
        InventorySearchOption inventorySearchOption = new InventorySearchOption();
        inventorySearchOption.setOrder_id(order_id);
        inventorySearchOption.setInventory_out_type(InventoryConstant.INVENTORY_OUT_TYPE_ORDER);
        List<Integer> inventoryOutIds = inventoryOutExtendMapper.inventoryOutIds(inventorySearchOption);
        inventorySearchOption.setInventoryOutIds(inventoryOutIds);
        List<InventoryOutExtend> inventoryOuts = inventoryOutExtendMapper.inventoryOutList(inventorySearchOption);
        List<OrderCommodityExtend> orderCommodities = orderExtend.getOrderCommodities();
        for (int i = 0; i < orderCommodities.size(); i++) {
            OrderCommodity orderCommodity = orderCommodities.get(i);
            if (orderCommodity.getOrder_commodity_status() >= 600) {//已经送货
                continue;
            }
            Integer inventoryOutCommodityStatus = orderCommodityStatus(inventoryOuts, orderCommodity.getOrder_commodity_id());
            if (inventoryOutCommodityStatus != null) {
                if (inventoryOutCommodityStatus == 1) {//1部分出库，
                    orderCommodity.setOrder_commodity_status(OrderConstants.ORDER_COMMODITY_STATUS_PART_OUT);
                }else if (inventoryOutCommodityStatus == 2) {//2完全出库
                    orderCommodity.setOrder_commodity_status(OrderConstants.ORDER_COMMODITY_STATUS_COMPLETE_OUT);
                }else if (inventoryOutCommodityStatus == 3) {//3供应商发货
                    orderCommodity.setOrder_commodity_status(OrderConstants.ORDER_COMMODITY_STATUS_SUPPLIER_DELIVERY);//
                }
                orderCommodityMapper.updateByPrimaryKey(orderCommodity);
            }
        }
        OrderComponents.orderStep(order_id);
    }

    private void giftStatus(int gift_id) {
        GiftExtend giftExtend = giftService.getGiftById(gift_id);
        InventorySearchOption inventorySearchOption = new InventorySearchOption();
        inventorySearchOption.setInventory_out_type(InventoryConstant.INVENTORY_OUT_TYPE_GIFT);
        inventorySearchOption.setInventory_out_obj_id(gift_id);
        List<Integer> inventoryOutIds = inventoryOutExtendMapper.inventoryOutIds(inventorySearchOption);
        inventorySearchOption.setInventoryOutIds(inventoryOutIds);
        List<InventoryOutExtend> inventoryOuts = inventoryOutExtendMapper.inventoryOutList(inventorySearchOption);
        List<GiftCommodityExtend> giftCommodities = giftExtend.getGiftCommodities();
        int out_status = 2;
        for (int i = 0; i < giftCommodities.size(); i++) {
            GiftCommodityExtend giftCommodityExtend = giftCommodities.get(i);
            if (giftCommodityExtend.getGift_commodity_out_status() >= 600) {//已经送货
                continue;
            }
            Integer inventoryOutCommodityStatus = giftCommodityStatus(inventoryOuts, giftCommodityExtend.getGift_commodity_id());
            if (inventoryOutCommodityStatus != null) {
                if (inventoryOutCommodityStatus == 1) {//1部分出库，
                    giftCommodityExtend.setGift_commodity_out_status(OrderConstants.ORDER_COMMODITY_STATUS_PART_OUT);
                    out_status = 1;
                }else if (inventoryOutCommodityStatus == 2) {//2完全出库
                    giftCommodityExtend.setGift_commodity_out_status(OrderConstants.ORDER_COMMODITY_STATUS_COMPLETE_OUT);
                }else if (inventoryOutCommodityStatus == 3) {//3供应商发货
                    giftCommodityExtend.setGift_commodity_out_status(OrderConstants.ORDER_COMMODITY_STATUS_SUPPLIER_DELIVERY);//
                }
                giftService.updateGiftCommodity(giftCommodityExtend);
            }
        }
        giftExtend.setGift_out_status(out_status);
        giftService.updateGift(giftExtend);
    }

    private Integer orderCommodityStatus(List<InventoryOutExtend> inventoryOuts, int order_commodity_id) {
        for (int i = 0; i < inventoryOuts.size(); i++) {
            InventoryOutExtend inventoryOutExtend = inventoryOuts.get(i);
            List<InventoryOutCommodityExtend> list = inventoryOutCommodities(inventoryOutExtend.getInventory_out_id());
            for (int j = 0; j < list.size(); j++) {
                InventoryOutCommodityExtend inventoryOutCommodity = list.get(j);
                if (order_commodity_id == inventoryOutCommodity.getOrder_commodity_id()) {
                    return inventoryOutCommodity.getInventory_out_commodity_status();
                }
            }
        }
        return null;
    }
    private Integer giftCommodityStatus(List<InventoryOutExtend> inventoryOuts, int gift_commodity_id) {
        for (int i = 0; i < inventoryOuts.size(); i++) {
            InventoryOutExtend inventoryOutExtend = inventoryOuts.get(i);
            List<InventoryOutCommodityExtend> list = inventoryOutCommodities(inventoryOutExtend.getInventory_out_id());
            for (int j = 0; j < list.size(); j++) {
                InventoryOutCommodityExtend inventoryOutCommodity = list.get(j);
                if (inventoryOutCommodity.getGift_commodity_id().equals(gift_commodity_id)) {
                    return inventoryOutCommodity.getInventory_out_commodity_status();
                }
            }
        }
        return null;
    }

    /**
     * 检测是否全部出库
     * @param inventory_out_id
     */
    public void inventoryOutStatus(int inventory_out_id){
        InventoryOutExtend inventoryOutExtend = getInventoryOutById(inventory_out_id);
        List<InventoryOutCommodityExtend> inventoryOutCommodities = inventoryOutExtend.getInventoryOutCommodities();
        boolean inventoryOutAll = true;
        for (int i = 0; i < inventoryOutCommodities.size(); i++) {
            InventoryOutCommodityExtend inventoryOutCommodity = inventoryOutCommodities.get(i);
            if (inventoryOutCommodity.getInventory_out_commodity_status() == 2 || inventoryOutCommodity.getInventory_out_commodity_status() == 3) {
                //
            }else {
                inventoryOutAll = false;
            }
        }
        if (inventoryOutAll) {
            inventoryOutExtend.setInventory_out_status(2);
        }else {
            inventoryOutExtend.setInventory_out_status(1);
        }
        inventoryOutMapper.updateByPrimaryKey(inventoryOutExtend);
    }

    public InventoryOutExtend getInventoryOutById(int inventory_out_id) {
        InventorySearchOption inventorySearchOption = new InventorySearchOption();
        List<Integer> inventoryOutIds = new ArrayList<>();
        inventoryOutIds.add(inventory_out_id);
        inventorySearchOption.setInventoryOutIds(inventoryOutIds);
        List<InventoryOutExtend> list = inventoryOutList(inventorySearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public void updateLogistics(Logistics logistics) {
        logisticsMapper.updateByPrimaryKey(logistics);
    }

    /**
     * 礼品送货结束
     * @param inventory_history_id
     */
    @Transactional
    public void giftConfirm(int inventory_history_id) {
        InventoryHistoryExtend inventoryHistory = inventoryHistoryService.getInventoryHistoryById(inventory_history_id);
        inventoryHistory.setInventory_out_fee_check_status(OrderConstants.inventory_out_fee_check_status_not_check);
        inventoryHistoryService.updateInventoryHistory(inventoryHistory);

        //到货
        Logistics logistics = inventoryHistory.getLogistics();
        logistics.setLogistics_status(LogisticsConstants.LOGISTICS_STATUS_DONE);
        updateLogistics(logistics);
    }
}
