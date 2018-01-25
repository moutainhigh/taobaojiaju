package com.xinshan.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jonson.xu on 15-4-24.
 */
public class ThreadPool {
    private ExecutorService executorService = null;
    private Integer threadLength = 1;

    public ThreadPool(Integer threadLength) {
        this.threadLength = threadLength;
    }

    public ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(threadLength);
        }
        return executorService;
    }
}
