package com.xinshan.controller.requstHistory;

import com.xinshan.model.RequestHistory;
import com.xinshan.pojo.requestHistory.RequestHistorySearchOption;
import com.xinshan.service.SystemService;
import com.xinshan.utils.Request2ModelUtils;
import com.xinshan.utils.ResponseUtil;
import com.xinshan.utils.SearchOptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by mxt on 17-3-30.
 */
@Controller
public class RequestHistoryController {
    @Autowired
    private SystemService systemService;

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sys/requestHistory")
    public void requestHistory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RequestHistorySearchOption requestHistorySearchOption = Request2ModelUtils.covert(RequestHistorySearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(requestHistorySearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(requestHistorySearchOption);
        List<RequestHistory> list = systemService.requestHistories(requestHistorySearchOption);
        Integer count = systemService.count(requestHistorySearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, requestHistorySearchOption);
    }
}
