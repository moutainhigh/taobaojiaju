package com.xinshan.service;

import com.xinshan.components.inventory.InventoryHistoryComponent;
import com.xinshan.dao.CommodityNumMapper;
import com.xinshan.dao.InventoryMoveMapper;
import com.xinshan.dao.extend.inventory.InventoryExtendMapper;
import com.xinshan.dao.extend.inventory.InventoryInExtendMapper;
import com.xinshan.dao.extend.inventory.InventoryMoveExtendMapper;
import com.xinshan.model.*;
import com.xinshan.model.extend.inventory.InventoryMoveCommodityExtend;
import com.xinshan.model.extend.inventory.InventoryMoveExtend;
import com.xinshan.utils.DateUtil;
import com.xinshan.pojo.inventory.InventorySearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mxt on 17-2-7.
 */
@Service
public class InventoryMoveService {
    @Autowired
    private InventoryMoveExtendMapper inventoryMoveExtendMapper;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private CommodityNumMapper commodityNumMapper;
    @Autowired
    private InventoryInExtendMapper inventoryInExtendMapper;
    @Autowired
    private InventoryMoveMapper inventoryMoveMapper;
    @Autowired
    private InventoryExtendMapper inventoryExtendMapper;
    @Autowired
    private CommodityService commodityService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private InventoryHistoryService inventoryHistoryService;

    /**
     * 添加库存调拨
     * @param inventoryMoveExtend
     * @param employee
     */
    @Transactional
    public void createInventoryMove(InventoryMoveExtend inventoryMoveExtend, Employee employee) {
        inventoryMoveExtend.setCreate_date(DateUtil.currentDate());
        inventoryMoveExtend.setCreate_employee_code(employee.getEmployee_code());
        inventoryMoveExtend.setCreate_employee_name(employee.getEmployee_name());
        inventoryMoveExtend.setInventory_move_status(0);
        inventoryMoveExtendMapper.createInventoryMove(inventoryMoveExtend);
        List<InventoryMoveCommodityExtend> list = inventoryMoveExtend.getInventoryMoveCommodityExtends();
        for (int i = 0; i < list.size(); i++) {
            InventoryMoveCommodityExtend inventoryMoveCommodity = list.get(i);
            int num = inventoryMoveCommodity.getInventory_move_num();
            int commodity_num_id1 = inventoryMoveCommodity.getCommodity_num_id1();
            CommodityNum commodityNum1 = commodityNumMapper.selectByPrimaryKey(commodity_num_id1);
            if (commodityNum1 == null || commodityNum1.getNum() < num) {
                throw new RuntimeException("库存id错误或数量不足");
            }
            int commodity_id = commodityNum1.getCommodity_id();
            int sample = inventoryMoveCommodity.getSample();
            int commodity_store_id = inventoryMoveCommodity.getCommodity_store_id();

            CommodityNum commodityNum2 = inventoryService.getNumByCommodityIdAndStoreId(commodity_id, commodity_store_id, sample, 0);
            if (commodityNum2 == null) {
                commodityNum2 = inventoryService.createCommodityNum(commodity_id, commodity_store_id, sample, 0, 0);
            }
            inventoryMoveCommodity.setCommodity_num_id2(commodityNum2.getCommodity_num_id());
            inventoryMoveCommodity.setInventory_move_id(inventoryMoveExtend.getInventory_move_id());
            inventoryMoveCommodity.setCommodity_id(commodity_id);
            inventoryMoveExtendMapper.createInventoryMoveCommodity(inventoryMoveCommodity);
        }
    }

    private InventoryMoveExtend getInventoryMoveById(int inventory_move_id) {
        InventorySearchOption inventorySearchOption = new InventorySearchOption();
        inventorySearchOption.setInventory_move_id(inventory_move_id);
        InventoryMoveExtend inventoryMove = inventoryMoveExtendMapper.inventoryMove(inventorySearchOption);
        List<InventoryMoveCommodityExtend> inventoryMoveCommodities = inventoryMove.getInventoryMoveCommodityExtends();
        for (int j = 0; j < inventoryMoveCommodities.size(); j++) {
            InventoryMoveCommodityExtend inventoryMoveCommodity = inventoryMoveCommodities.get(j);
            inventoryMoveCommodity.setCommodityNum1(commodityNumMapper.selectByPrimaryKey(inventoryMoveCommodity.getCommodity_num_id1()));
            inventoryMoveCommodity.setCommodityNum2(commodityNumMapper.selectByPrimaryKey(inventoryMoveCommodity.getCommodity_num_id2()));
            int commodity_id = inventoryMoveCommodity.getCommodityNum1().getCommodity_id();
            CommodityNum commodityNum1 = inventoryMoveCommodity.getCommodityNum1();
            CommodityNum commodityNum2 = inventoryMoveCommodity.getCommodityNum2();
            inventoryMoveCommodity.setCommodity(commodityService.getCommodityById(commodity_id));
            inventoryMoveCommodity.setCommodityStore1(commodityService.getCommodityStoreById(commodityNum1.getCommodity_store_id()));
            inventoryMoveCommodity.setCommodityStore2(commodityService.getCommodityStoreById(commodityNum2.getCommodity_store_id()));
            Commodity commodity = inventoryMoveCommodity.getCommodity();
            if (commodity != null) {
                Integer supplier_id = commodity.getSupplier_id();
                if (supplier_id != null) {
                    inventoryMoveCommodity.setSupplier(supplierService.getSupplierById(supplier_id));
                }
            }
        }
        return inventoryMove;
    }

    public List<InventoryMoveExtend> inventoryMoveList(InventorySearchOption inventorySearchOption) {
        List<InventoryMoveExtend> list = new ArrayList<>();
        List<Integer> inventoryIds = inventoryMoveExtendMapper.inventoryMoveIds(inventorySearchOption);
        for (int i = 0; i < inventoryIds.size(); i++) {
            int inventoryMoveId = inventoryIds.get(i);
            InventoryMoveExtend inventoryMove = getInventoryMoveById(inventoryMoveId);
            list.add(inventoryMove);
        }
        return list;
    }

    public List<InventoryMoveExtend> inventoryMoveList1(InventorySearchOption inventorySearchOption) {
        List<Integer> inventoryIds = inventoryMoveExtendMapper.inventoryMoveIds(inventorySearchOption);
        if (inventoryIds == null || inventoryIds.size() <= 0) {
            return new ArrayList<>();
        }
        inventorySearchOption.setInventoryMoveIds(inventoryIds);
        List<InventoryMoveExtend> inventoryMoveExtends = inventoryMoveExtendMapper.inventoryMove1(inventorySearchOption);
        return inventoryMoveExtends;
    }

    public Integer countInventoryMove(InventorySearchOption inventorySearchOption) {
        return inventoryMoveExtendMapper.countInventoryMove(inventorySearchOption);
    }

    @Transactional
    public int confirmInventoryMove(int inventory_move_id, Employee employee) {
        InventoryMoveExtend inventoryMoveExtend = getInventoryMoveById(inventory_move_id);
        if (inventoryMoveExtend.getConfirm_date() != null || inventoryMoveExtend.getInventory_move_status() == 1) {//已经确认
            return 1;
        }
        inventoryMoveExtend.setConfirm_employee_name(employee.getEmployee_name());
        inventoryMoveExtend.setConfirm_employee_code(employee.getEmployee_code());
        inventoryMoveExtend.setConfirm_date(DateUtil.currentDate());

        List<InventoryMoveCommodityExtend> list = inventoryMoveExtend.getInventoryMoveCommodityExtends();
        for (int i = 0; i < list.size(); i++) {
            InventoryMoveCommodityExtend inventoryMoveCommodityExtend = list.get(i);
            int num = inventoryMoveCommodityExtend.getInventory_move_num();
            CommodityNum commodityNum1 = inventoryMoveCommodityExtend.getCommodityNum1();
            CommodityNum commodityNum2 = inventoryMoveCommodityExtend.getCommodityNum2();
            if (commodityNum1.getNum() < num) {
                return 2;
            }
            commodityNum1.setNum(commodityNum1.getNum() - num);
            inventoryHistoryService.inventoryMoveOut(inventoryMoveExtend, employee, inventoryMoveCommodityExtend, commodityNum1);
            commodityNum2.setNum(commodityNum2.getNum() + num);
            inventoryHistoryService.inventoryMoveIn(inventoryMoveExtend, employee, inventoryMoveCommodityExtend, commodityNum2);
            commodityNumMapper.updateByPrimaryKey(commodityNum1);
            commodityNumMapper.updateByPrimaryKey(commodityNum2);
        }
        inventoryMoveExtend.setInventory_move_status(1);
        inventoryMoveMapper.updateByPrimaryKey(inventoryMoveExtend);

        return 0;//成功
    }



}
