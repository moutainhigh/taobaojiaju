package com.xinshan.dao.extend.worker;

import com.xinshan.model.Worker;
import com.xinshan.pojo.worker.WorkerSearchOption;

import java.util.List;

/**
 * Created by mxt on 16-12-13.
 */
public interface WorkerExtendMapper {

    List<Worker> workers(WorkerSearchOption workerSearchOption);

    Integer countWorkers(WorkerSearchOption workerSearchOption);

    void createWorker(Worker worker);
}
