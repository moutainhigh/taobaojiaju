package com.xinshan.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xinshan.components.sys.SysComponents;
import com.xinshan.pojo.SearchOption;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mxt on 16-10-9.
 */
public class ResponseUtil {

    public static void sendResponse(HttpServletRequest request, HttpServletResponse response, ResultData resultData) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String postData = resultData.getPostData();
        resultData.setPostData(null);
        byte[] bytes = JSON.toJSONBytes(resultData, SerializerFeature.DisableCircularReferenceDetect);
        response.getOutputStream().write(bytes);
        SysComponents.requestHistory(request, resultData, postData);
    }

    public static void sendSuccessResponse(HttpServletRequest request, HttpServletResponse response, String postData) throws IOException {
        sendSuccessResponse(request, response, null, postData);
    }

    public static void sendSuccessResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        sendSuccessResponse(request, response, null, null);
    }

    public static void sendSuccessResponse(HttpServletRequest request, HttpServletResponse response, Object data) throws IOException {
        sendSuccessResponse(request, response, data, null);
    }

    public static void sendSuccessResponse(HttpServletRequest request, HttpServletResponse response, Object data, String postData) throws IOException {
        sendResponse(request, response, new ResultData("0x0007", data, postData));//执行成功
    }

    public static void sendListResponse(HttpServletRequest request, HttpServletResponse response, List list, int count, SearchOption searchOption) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("count", count);
        int allPage = count/searchOption.getLimit();
        if (count % searchOption.getLimit() > 0) {
            allPage++;
        }
        map.put("allPage", allPage);
        map.put("query", searchOption);
        sendResponse(request, response, new ResultData("0x0007", map));//执行成功
    }

    /**
     * 导出
     * @param request
     * @param response
     * @param workbook
     * @throws IOException
     * @throws WriteException
     */
    public static void exportResponse(HttpServletRequest request, HttpServletResponse response, WritableWorkbook workbook) throws IOException, WriteException {
        workbook.write();
        workbook.close();
        SysComponents.requestHistory(request, null, null);
    }
}
