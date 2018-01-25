package com.xinshan.components.supplier;

import com.xinshan.model.extend.supplier.SupplierExtend;
import com.xinshan.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by mxt on 17-7-26.
 */
@Component
public class SupplierComponents {

    private static Lock lock = new ReentrantLock();

    private static SupplierService supplierService;

    @Autowired
    public void setSupplierService(SupplierService supplierService) {
        SupplierComponents.supplierService = supplierService;
    }

    private static Map<Integer, SupplierExtend> supplierMap = new HashMap<>();

    public static Map<Integer, SupplierExtend> getSupplierMap() {
        if (supplierMap == null || supplierMap.isEmpty() || supplierMap.size() == 0) {
            initSupplierMap();
        }
        return supplierMap;
    }

    private static void initSupplierMap(){
        lock.lock();
        try {
            supplierMap.clear();
            List<SupplierExtend> list = supplierService.supplierList(null);
            for (int i = 0; i < list.size(); i++) {
                SupplierExtend supplierExtend = list.get(i);
                Integer supplier_id = supplierExtend.getSupplier_id();
                supplierMap.put(supplier_id, supplierExtend);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public static void clear(){
        lock.lock();
        try {
            supplierMap.clear();
        }catch (Exception e) {

        }finally {
            lock.unlock();
        }
    }

    public static List<Integer> concractsSupplierIds(String employee_code) {
        List<Integer> list = new ArrayList<>();
        if (employee_code == null || employee_code.equals("")) {
            list.add(-1);
            return list;
        }
        Iterator<Map.Entry<Integer, SupplierExtend>> iterator = SupplierComponents.getSupplierMap().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, SupplierExtend> next = iterator.next();
            SupplierExtend value = next.getValue();
            if (employee_code.equals(value.getContacts_code())) {
                list.add(value.getSupplier_id());
            }
        }
        return list;
    }
}
