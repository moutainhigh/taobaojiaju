package com.xinshan.service;

import com.xinshan.components.activity.ActivityComponents;
import com.xinshan.dao.*;
import com.xinshan.dao.extend.inventory.InventoryExtendMapper;
import com.xinshan.dao.extend.order.OrderExtendMapper;
import com.xinshan.model.*;
import com.xinshan.model.extend.commodity.CommodityActivity;
import com.xinshan.model.extend.commodity.CommodityNumExtend;
import com.xinshan.model.extend.inventory.InventoryHistoryDetailExtend;
import com.xinshan.pojo.commodity.CommoditySearchOption;
import com.xinshan.pojo.inventory.InventorySearchOption;
import com.xinshan.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mxt on 16-11-9.
 */
@Service
public class InventoryService {
    @Autowired
    private CommodityStoreMapper commodityStoreMapper;
    @Autowired
    private InventoryExtendMapper inventoryExtendMapper;
    @Autowired
    private CommodityNumMapper commodityNumMapper;
    @Autowired
    private OrderCommodityMapper orderCommodityMapper;
    @Autowired
    private CommodityService commodityService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private PurchaseCommodityMapper purchaseCommodityMapper;
    @Autowired
    private InventoryInCommodityMapper inventoryInCommodityMapper;
    @Autowired
    private InventoryOutCommodityMapper inventoryOutCommodityMapper;
    @Autowired
    private PurchaseMapper purchaseMapper;
    @Autowired
    private OrderExtendMapper orderExtendMapper;

    public CommodityNum getNumByCommodityIdAndStoreId(int commodity_id, int commodity_store_id,
                                                      int sample, int return_commodity) {
        CommoditySearchOption commoditySearchOption = new CommoditySearchOption();
        commoditySearchOption.setCommodity_id(commodity_id);
        commoditySearchOption.setCommodity_store_id(commodity_store_id);
        commoditySearchOption.setSample(sample);
        commoditySearchOption.setReturn_commodity(return_commodity);
        return inventoryExtendMapper.getNumByCommodityIdAndStoreId(commoditySearchOption);
    }

    public CommodityNumExtend getCommodityNumById(int commodity_num_id) {
        CommoditySearchOption commoditySearchOption = new CommoditySearchOption();
        commoditySearchOption.setCommodity_num_id(commodity_num_id);
        List<CommodityNumExtend> list = inventoryExtendMapper.commodityNumList(commoditySearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public CommodityNum getCommodityNumById1(int commodity_num_id) {
        return commodityNumMapper.selectByPrimaryKey(commodity_num_id);
    }

    public List<CommodityNumExtend> commodityNumList(CommoditySearchOption commoditySearchOption) {
        List<CommodityNumExtend> commodityNumExtends = inventoryExtendMapper.commodityNumList(commoditySearchOption);
        for (int i = 0; i < commodityNumExtends.size(); i++) {
            CommodityNumExtend commodityNumExtend = commodityNumExtends.get(i);
            Commodity commodity = commodityNumExtend.getCommodity();
            CommodityActivity commodityActivity = ActivityComponents.getByCommodity(commodity);
            commodityNumExtend.setCommodityActivity(commodityActivity);
            commodityNumExtend.setActivities(ActivityComponents.commodityActivities(commodity));
        }
        return commodityNumExtends;
    }

    public List<CommodityNumExtend> commodityNumList1(CommoditySearchOption commoditySearchOption) {
        List<CommodityNumExtend> commodityNumExtends = inventoryExtendMapper.commodityNumList(commoditySearchOption);
        return commodityNumExtends;
    }

    public Integer countCommodityNum(CommoditySearchOption commoditySearchOption) {
        return inventoryExtendMapper.countCommodityNum(commoditySearchOption);
    }
    public void updateCommodityNum(CommodityNum commodityNum) {
        commodityNumMapper.updateByPrimaryKey(commodityNum);
    }

    /**
     * 商品库存
     * @param commodity_id
     * @return
     */
    public Integer InventoryNum(int commodity_id, int sample) {
        CommoditySearchOption commoditySearchOption = new CommoditySearchOption();
        commoditySearchOption.setCommodity_id(commodity_id);
        commoditySearchOption.setSample(sample);
        commoditySearchOption.setCommodity_num(0);
        return inventoryExtendMapper.InventoryNum(commoditySearchOption);
    }

    @Transactional
    public void commodityNumMove(int commodity_num_id, int move_num, int move_store_id, int sample) {
        CommodityNum commodityNum = commodityNumMapper.selectByPrimaryKey(commodity_num_id);
        if (commodityNum.getNum() < move_num) {
            throw new RuntimeException("数量不足");
        }
        CommodityNum moveCommodityNum = getNumByCommodityIdAndStoreId(commodityNum.getCommodity_id(), move_store_id, sample, 0);
        if (moveCommodityNum == null) {
            createCommodityNum(commodityNum.getCommodity_id(), move_store_id, sample, move_num, 0);
        }else {
            moveCommodityNum.setNum(moveCommodityNum.getNum() + move_num);
            commodityNumMapper.updateByPrimaryKey(moveCommodityNum);
        }
        commodityNum.setNum(commodityNum.getNum() - move_num);
        commodityNumMapper.updateByPrimaryKey(commodityNum);
    }

    public Map inventoryHistoryStatics(InventorySearchOption inventorySearchOption) {
        Map<String, Object> hashMap = new HashMap<>();
        int commodityInNum = 0;
        int commodityOutNum = 0;
        BigDecimal purchaseAmount = new BigDecimal("0");
        BigDecimal sellAmount = new BigDecimal("0");
        List<Map> list = inventoryExtendMapper.inventoryHistoryStatics(inventorySearchOption);
        for (int i = 0; i < list.size(); i++) {
            Map map = list.get(i);
            int num = Integer.parseInt(map.get("inventory_history_num").toString());
            if (map.get("inventory_history_in_out") != null) {
                int inventory_history_in_out = Integer.parseInt(map.get("inventory_history_in_out").toString());
                if (map.get("inventory_history_num") != null) {//出入库数量
                    if (inventory_history_in_out == 1) {
                        commodityInNum += num;
                    }else if (inventory_history_in_out == 2) {
                        commodityOutNum += num;
                    }
                }
            }else {
                int inventory_type = Integer.parseInt(map.get("inventory_type").toString());
                if (inventory_type == 0) {
                    commodityInNum += num;
                }else if (inventory_type == 1) {
                    commodityOutNum += num;
                }
            }
            if (map.get("inventory_in_commodity_id") != null) {//采购总价
                InventoryInCommodity inventoryInCommodity = inventoryInCommodityMapper.selectByPrimaryKey(Integer.parseInt(map.get("inventory_in_commodity_id").toString()));
                if (inventoryInCommodity != null) {
                    PurchaseCommodity purchaseCommodity = purchaseCommodityMapper.selectByPrimaryKey(inventoryInCommodity.getPurchase_commodity_id());
                    if (purchaseCommodity != null && purchaseCommodity.getPurchase_unit_cost_price() != null && purchaseCommodity.getPurchase_unit_cost_price().doubleValue() > 0) {
                        purchaseAmount = purchaseAmount.add(purchaseCommodity.getPurchase_unit_cost_price().multiply(new BigDecimal(num)));
                    }else {
                        Commodity commodity = commodityService.getCommodityById(inventoryInCommodity.getCommodity_id());
                        purchaseAmount = purchaseAmount.add(commodity.getPurchase_price().multiply(new BigDecimal(num)));
                    }
                }
            }
            if (map.get("inventory_out_commodity_id") != null) {
                InventoryOutCommodity inventoryOutCommodity = inventoryOutCommodityMapper.selectByPrimaryKey(Integer.parseInt(map.get("inventory_out_commodity_id").toString()));
                if (inventoryOutCommodity != null) {
                    OrderCommodity orderCommodity = orderCommodityMapper.selectByPrimaryKey(inventoryOutCommodity.getOrder_commodity_id());
                    if (orderCommodity != null) {
                        BigDecimal bargain_price = orderCommodity.getBargain_price();
                        if (orderCommodity.getRevision_fee() != null && orderCommodity.getRevision_fee().doubleValue() > 0) {
                            bargain_price = bargain_price.add(orderCommodity.getRevision_fee());
                        }
                        BigDecimal aa = bargain_price.multiply(new BigDecimal(num));
                        sellAmount = sellAmount.add(aa);
                    }
                }
            }
        }
        hashMap.put("inventoryInNum", commodityInNum);
        hashMap.put("commodityOutNum", commodityOutNum);
        hashMap.put("purchaseAmount", purchaseAmount);
        hashMap.put("sellAmount", sellAmount);
        return hashMap;
    }

    public void inventoryInit(List<Object[]> list, Employee employee) {
        for (int i = 0; i < list.size(); i++) {
            Object[] o = list.get(i);
            Commodity commodity = (Commodity) o[0];
            String supplier_name = (String) o[1];
            String contacts = (String) o[2];
            int sample = (int) o[3];
            String store_name = (String) o[4];
            int num = (int) o[5];
            Supplier supplier = supplierService.getSupplierByName(supplier_name);
            if (supplier == null) {
                supplier = new Supplier();
                supplier.setContacts(contacts);
                supplierService.createSupplier(supplier);
            }else {
                supplier.setContacts(contacts);
                supplier.setEdit_employee_code(employee.getEmployee_code());
                supplier.setEdit_employee_name(employee.getEmployee_name());
                supplier.setEdit_date(DateUtil.currentDate());
                supplierService.updateSupplier(supplier);
            }
            Commodity commodity1 = commodityService.getCommodityByCode(commodity.getCommodity_code());
            if (commodity1 != null) {
                commodity1.setSupplier_commodity_code(commodity.getSupplier_commodity_code());//原货号
                commodity1.setCommodity_code(commodity.getCommodity_code()); //新货号
                commodity1.setCommodity_name(commodity.getCommodity_name());//商品名称
                commodity1.setCommodity_size(commodity.getCommodity_size());//商品规格
                commodity1.setRecord_employee_code(employee.getEmployee_code());
                commodity1.setRecord_employee_name(employee.getEmployee_name());
                commodityService.updateCommodity(commodity1);
                commodity = commodity1;
            }else {
                commodity.setRecord_employee_code(employee.getEmployee_code());
                commodity.setRecord_employee_name(employee.getEmployee_name());
                commodity.setSupplier_id(supplier.getSupplier_id());
                commodity.setCommodity_status(1);
                commodityService.createCommodity(commodity);
            }
            CommodityStore commodityStore = getStoreByName(store_name);
            if (commodityStore == null) {
                commodityStore = new CommodityStore();
                commodityStore.setStore_name(store_name);
                commodityStore.setStore_enable(1);
                inventoryExtendMapper.createStore(commodityStore);
            }

            CommodityNum commodityNum = getNumByCommodityIdAndStoreId(commodity.getCommodity_id(), commodityStore.getCommodity_store_id(), sample, 0);
            if (commodityNum == null) {
                createCommodityNum(commodity.getCommodity_id(), commodityStore.getCommodity_store_id(), sample, num, 0);
            }else {
                commodityNum.setNum(num);
                commodityNumMapper.updateByPrimaryKey(commodityNum);
            }
        }
    }

    public CommodityNum createCommodityNum(int commodity_id, int commodity_store_id, int sample, int num, int return_commodity) {
        CommodityNum commodityNum = getNumByCommodityIdAndStoreId(commodity_id, commodity_store_id, sample, return_commodity);
        if (commodityNum == null) {
            commodityNum = new CommodityNum();
            commodityNum.setCommodity_store_id(commodity_store_id);
            commodityNum.setCommodity_id(commodity_id);
            commodityNum.setSample(sample);
            commodityNum.setReturn_commodity(return_commodity);
            commodityNum.setNum(num);
            inventoryExtendMapper.createCommodityNum(commodityNum);
        }else {
            commodityNum.setNum(commodityNum.getNum() + num);
        }
        return commodityNum;
    }

    public CommodityStore getStoreByName(String store_name) {
        CommoditySearchOption commoditySearchOption = new CommoditySearchOption();
        commoditySearchOption.setStore_name(store_name);
        List<CommodityStore> list = inventoryExtendMapper.stores(commoditySearchOption);
        if (list != null && list.size() >0){
            return list.get(0);
        }
        return null;
    }

    public CommodityStore getStoreById(int commodity_store_id) {
        CommoditySearchOption commoditySearchOption = new CommoditySearchOption();
        commoditySearchOption.setCommodity_store_id(commodity_store_id);
        List<CommodityStore> list = inventoryExtendMapper.stores(commoditySearchOption);
        if (list != null && list.size() >0){
            return list.get(0);
        }
        return null;
    }



    public List<CommodityStore> stores(CommoditySearchOption commoditySearchOption) {
        return inventoryExtendMapper.stores(commoditySearchOption);
    }
    public void createStore(CommodityStore commodityStore) {
        inventoryExtendMapper.createStore(commodityStore);
    }
    @Transactional
    public void updateStore(CommodityStore commodityStore) {
        commodityStoreMapper.updateByPrimaryKey(commodityStore);
    }
    @Transactional
    public void deleteStore(int store_id) {
        commodityStoreMapper.deleteByPrimaryKey(store_id);
    }

    /**
     * 库存数量增加
     * @param commodityNumId
     * @param addNum
     */
    public void commodityNumAdd(int commodityNumId, int addNum) {
        CommodityNum commodityNum = commodityNumMapper.selectByPrimaryKey(commodityNumId);
        commodityNum.setNum(commodityNum.getNum() + addNum);
        commodityNumMapper.updateByPrimaryKey(commodityNum);
    }

    /**
     *
     * @param commodityNumId
     * @param num
     */
    public void commodityNum(int commodityNumId, int num) {
        CommodityNum commodityNum = commodityNumMapper.selectByPrimaryKey(commodityNumId);
        commodityNum.setNum(num);
        commodityNumMapper.updateByPrimaryKey(commodityNum);
    }
}
