package com.xinshan.utils.thread;

import com.xinshan.components.statistics.PerformanceComponent;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by mxt on 17-8-25.
 */
public class PerformanceInit implements Runnable {

    public static AtomicBoolean status = new AtomicBoolean(false);

    public PerformanceInit(List<String> employeeCodes, int month, int year) {
        this.employeeCodes = employeeCodes;
        this.month = month;
        this.year = year;
    }
    private int year;
    private int month;
    private List<String> employeeCodes;

    @Override
    public void run() {
        status.set(true);
        try {
            performanceInit();
        }catch (Exception e) {
            e.printStackTrace();
        }
        status.set(false);
    }

    private void performanceInit() {
        for (int i = 0; i < employeeCodes.size(); i++) {
            String employeeCode = employeeCodes.get(i);
            PerformanceComponent.performance(year, month, employeeCode);
        }
        System.out.println("performance done!!");
    }
}
