package com.xinshan.service;

import com.xinshan.utils.DateUtil;
import com.xinshan.utils.ThreadPool;
import com.xinshan.utils.thread.InventoryExport;
import org.springframework.stereotype.Service;

/**
 * Created by mxt on 17-6-2.
 */
@Service
public class InventoryExportService {
    public void run() {
        System.out.println("run---------" + DateUtil.format(DateUtil.currentDate(), "yyyy-MM-dd HH:mm:ss"));
        ThreadPool threadPool = new ThreadPool(1);
        threadPool.getExecutorService().submit(new InventoryExport());
        threadPool.getExecutorService().shutdown();
    }
}
