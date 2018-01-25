package com.xinshan.controller.worker;

import com.xinshan.model.Worker;
import com.xinshan.service.WorkerService;
import com.xinshan.utils.Request2ModelUtils;
import com.xinshan.utils.ResponseUtil;
import com.xinshan.utils.ResultData;
import com.xinshan.utils.SearchOptionUtil;
import com.xinshan.pojo.worker.WorkerSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by mxt on 16-12-13.
 */
@Controller
public class WorkerController {
    @Autowired
    private WorkerService workerService;

    /**
     * 添加工人师傅
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/worker/createWorker")
    public void createWorker(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Worker worker = Request2ModelUtils.covert(Worker.class, request);
        try {
            workerService.createWorker(worker);
            ResponseUtil.sendSuccessResponse(request, response, worker);
        }catch (DuplicateKeyException e) {
            e.printStackTrace();
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", ""));
        }
    }

    @RequestMapping("/order/worker/updateWorker")
    public void updateWorker(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Worker worker = Request2ModelUtils.covert(Worker.class, request);
        try {
            workerService.updateWorker(worker);
            ResponseUtil.sendSuccessResponse(request, response, worker);
        }catch (DuplicateKeyException e) {
            e.printStackTrace();
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", ""));
        }
    }

    /**
     * 工人列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/order/worker/workerList", "/order/inventoryOut/workerList","/order/afterSales/workerList"})
    public void workerList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        WorkerSearchOption workerSearchOption = Request2ModelUtils.covert(WorkerSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(workerSearchOption);
        List<Worker> list = workerService.workers(workerSearchOption);
        Integer count = workerService.countWorkers(workerSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, workerSearchOption);
    }

}
