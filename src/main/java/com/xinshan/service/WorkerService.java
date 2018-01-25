package com.xinshan.service;

import com.xinshan.dao.WorkerMapper;
import com.xinshan.dao.extend.worker.WorkerExtendMapper;
import com.xinshan.model.Worker;
import com.xinshan.pojo.worker.WorkerSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by mxt on 16-12-13.
 */
@Service
public class WorkerService {
    @Autowired
    private WorkerExtendMapper workerExtendMapper;
    @Autowired
    private WorkerMapper workerMapper;

    public List<Worker> workers(WorkerSearchOption workerSearchOption) {
        return workerExtendMapper.workers(workerSearchOption);
    }

    public Integer countWorkers(WorkerSearchOption workerSearchOption) {
        return workerExtendMapper.countWorkers(workerSearchOption);
    }

    @Transactional
    public void createWorker(Worker worker) {
        workerExtendMapper.createWorker(worker);
    }

    @Transactional
    public void updateWorker(Worker worker) {
        workerMapper.updateByPrimaryKey(worker);
    }
}
