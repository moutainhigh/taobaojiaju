package com.xinshan.controller.activity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.xinshan.components.activity.ActivityComponents;
import com.xinshan.model.ActivitySellLimit;
import com.xinshan.service.ActivityService;
import com.xinshan.utils.RequestUtils;
import com.xinshan.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mxt on 17-9-18.
 */
@Controller
public class SellLimitController {

    @Autowired
    private ActivityService activityService;

    /**
     * 编辑
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/sellLimit/updateSellLimit")
    public void sellLimitCommodity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String s = RequestUtils.getRequestUtils().postData(request);
        JSONArray jsonArray = JSON.parseArray(s);
        List<ActivitySellLimit> limits = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            Object o = jsonArray.get(i);
            ActivitySellLimit activitySellLimit = JSON.parseObject(o.toString(), ActivitySellLimit.class);
            limits.add(activitySellLimit);
        }
        activityService.createActivitySellLimit(limits);
        ResponseUtil.sendSuccessResponse(request, response, s);
        ActivityComponents.clear();
    }

    @RequestMapping("/activity/sellLimit/deleteSellLimit")
    public void deleteSellLimitCommodity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int activity_sell_limit_id = Integer.parseInt(request.getParameter("activity_sell_limit_id"));
        activityService.deleteSellLimit(activity_sell_limit_id);
        ResponseUtil.sendSuccessResponse(request, response);
        ActivityComponents.clear();
    }
}
