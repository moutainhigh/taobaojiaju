package com.xinshan.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xinshan.dao.InventoryMapper;
import com.xinshan.dao.extend.inventory.InventoryCheckExtendMapper;
import com.xinshan.model.CommodityNum;
import com.xinshan.model.Employee;
import com.xinshan.model.Inventory;
import com.xinshan.model.InventoryAdjust;
import com.xinshan.model.extend.commodity.CommodityNumExtend;
import com.xinshan.model.extend.inventory.InventoryExtend;
import com.xinshan.pojo.inventory.InventoryCheckSearchOption;
import com.xinshan.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mxt on 17-5-10.
 */
@Service
public class InventoryCheckService {
    @Autowired
    private InventoryCheckExtendMapper inventoryCheckExtendMapper;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private InventoryMapper inventoryMapper;

    @Transactional
    public void inventoryImport(List<Object[]> list, Employee employee) {
        Date date = DateUtil.currentDate();
        for (int i = 0; i < list.size(); i++) {
            Object[] objects = list.get(i);
            int commodity_id = (int) objects[0];
            int sample = (int) objects[1];
            int return_commodity = (int) objects[2];
            int commodity_store_id = (int) objects[3];
            int commodity_num = (int) objects[4];
            int inventory_num = (int) objects[5];
            System.out.println(commodity_id + "\t" + commodity_store_id + "\t" + sample + "\t" + return_commodity);
            CommodityNum commodityNum = inventoryService.getNumByCommodityIdAndStoreId(commodity_id, commodity_store_id, sample, return_commodity);
            Inventory inventory = new Inventory();
            inventory.setCreate_date(date);
            inventory.setInventory_employee_code(employee.getEmployee_code());
            inventory.setInventory_employee_name(employee.getEmployee_name());
            inventory.setCommodity_num(commodity_num);
            inventory.setInventory_num(inventory_num);
            inventory.setCommodity_num_id(commodityNum.getCommodity_num_id());
            inventory.setInventory_adjust_status(0);
            setProfitStatus(inventory);
            inventoryCheckExtendMapper.createInventory(inventory);
        }
    }

    public InventoryExtend getInventoryById(int inventory_id) {
        InventoryCheckSearchOption inventoryCheckSearchOption = new InventoryCheckSearchOption();
        inventoryCheckSearchOption.setInventory_id(inventory_id);
        List<InventoryExtend> list = inventoryList(inventoryCheckSearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public List<InventoryExtend> inventoryList(InventoryCheckSearchOption inventoryCheckSearchOption) {
        return inventoryCheckExtendMapper.inventoryList(inventoryCheckSearchOption);
    }

    public Integer countInventory(InventoryCheckSearchOption inventoryCheckSearchOption) {
        return inventoryCheckExtendMapper.countInventory(inventoryCheckSearchOption);
    }

    /**
     * 调整
     * @param inventoryAdjust
     * @param employee
     */
    @Transactional
    public void createInventoryAdjust(InventoryAdjust inventoryAdjust, Employee employee) {
        inventoryAdjust.setInventory_adjust_date(DateUtil.currentDate());
        inventoryAdjust.setInventory_adjust_employee_code(employee.getEmployee_code());
        inventoryAdjust.setInventory_adjust_employee_name(employee.getEmployee_name());
        Integer inventory_id = inventoryAdjust.getInventory_id();
        InventoryExtend inventoryExtend = getInventoryById(inventory_id);
        Integer commodity_num_id = inventoryExtend.getCommodity_num_id();
        Integer inventory_num = inventoryExtend.getInventory_num();
        inventoryAdjust.setInventory_adjust_num(inventory_num);
        CommodityNumExtend commodityNum = inventoryService.getCommodityNumById(commodity_num_id);
        inventoryAdjust.setInventory_adjust_before_num(commodityNum.getNum());
        inventoryService.commodityNum(commodity_num_id, inventory_num);//库存修改
        inventoryExtend.setInventory_adjust_status(1);
        inventoryMapper.updateByPrimaryKey(inventoryExtend);
        inventoryCheckExtendMapper.createInventoryAdjust(inventoryAdjust);
    }

    @Transactional
    public List<Inventory> createInventory(JSONArray jsonArray, Employee employee) {
        List<Inventory> inventories = new ArrayList<>();
        Iterator<Object> iterator = jsonArray.iterator();
        Date date = DateUtil.currentDate();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            JSONObject jsonObject = JSON.parseObject(next.toString());
            int commodity_num_id = Integer.parseInt(jsonObject.get("commodity_num_id").toString());
            int inventory_num = Integer.parseInt(jsonObject.get("inventory_num").toString());
            int commodity_num = Integer.parseInt(jsonObject.get("commodity_num").toString());
            Inventory inventory = new Inventory();
            inventory.setCreate_date(date);
            inventory.setInventory_employee_code(employee.getEmployee_code());
            inventory.setInventory_employee_name(employee.getEmployee_name());
            inventory.setCommodity_num(commodity_num);
            inventory.setInventory_num(inventory_num);
            inventory.setCommodity_num_id(commodity_num_id);
            inventory.setInventory_adjust_status(0);
            setProfitStatus(inventory);
            inventories.add(inventory);
            inventoryCheckExtendMapper.createInventory(inventory);
        }
        return inventories;
    }
    private void setProfitStatus(Inventory inventory) {
        Integer commodity_num = inventory.getCommodity_num();
        Integer inventory_num = inventory.getInventory_num();
        if (commodity_num > inventory_num) {//库存数量 > 盘点数量， 盘亏
            inventory.setProfit_status(-1);
        }else if (commodity_num == inventory_num) {//库存数量 = 盘点数量 正常
            inventory.setProfit_status(0);
        }else if (commodity_num < inventory_num) {//库存数量 < 盘点数量 盘盈
            inventory.setProfit_status(1);
        }
    }
}
