package com.xinshan.utils.thread;

import com.xinshan.components.statistics.SalesSupplierComponent;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by mxt on 17-9-8.
 */
public class SalesSupplierInit implements Runnable {
    public static AtomicBoolean status = new AtomicBoolean(false);
    private String month;

    public SalesSupplierInit(String month) {
        this.month = month;
    }

    @Override
    public void run() {
        status.set(true);
        try {
            SalesSupplierComponent.init(month);
        }catch (Exception e) {
            e.printStackTrace();
        }
        status.set(false);
    }
}
