package com.xinshan.controller.commodity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xinshan.model.Commodity;
import com.xinshan.model.CommodityPriceAdjust;
import com.xinshan.model.Employee;
import com.xinshan.model.extend.commodity.CommodityPriceAdjustDetailExtend;
import com.xinshan.model.extend.commodity.CommodityPriceAdjustExtend;
import com.xinshan.pojo.commodity.CommoditySearchOption;
import com.xinshan.service.CommodityService;
import com.xinshan.service.SellPriceAdjustService;
import com.xinshan.utils.*;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mxt on 17-4-21.
 */
@Controller
public class CommodityPriceAdjustController {

    @Autowired
    private SellPriceAdjustService sellPriceAdjustService;
    @Autowired
    private CommodityService commodityService;

    /**
     * 添加调价
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/commodity/priceAdjust/createAdjust")
    public void createSellPriceAdjust(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String postData = RequestUtils.getRequestUtils().postData(request);
        CommodityPriceAdjustExtend sellPriceAdjustExtend = JSONObject.parseObject(postData, CommodityPriceAdjustExtend.class);
        sellPriceAdjustService.createCommodityPriceAdjust(sellPriceAdjustExtend, employee);
        ResponseUtil.sendSuccessResponse(request, response, postData);
    }

    /**
     * 添加备注
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/commodity/priceAdjust/adjustRemark")
    public void adjustRemark(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String adjust_remark = request.getParameter("adjust_remark");
        int commodity_price_adjust_id = Integer.parseInt(request.getParameter("commodity_price_adjust_id"));
        CommodityPriceAdjust priceAdjust = sellPriceAdjustService.getPriceAdjust(commodity_price_adjust_id);
        priceAdjust.setAdjust_remark(adjust_remark);
        sellPriceAdjustService.updateCommodityPriceAdjust(priceAdjust);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 调价列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/commodity/priceAdjust/priceAdjustList")
    public void commodityPriceAdjust(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CommoditySearchOption commoditySearchOption = Request2ModelUtils.covert(CommoditySearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(commoditySearchOption);
        List<CommodityPriceAdjustExtend> list = sellPriceAdjustService.commodityPriceAdjustExtends(commoditySearchOption);
        Integer count = sellPriceAdjustService.count(commoditySearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, commoditySearchOption);
    }

    /**
     * 调价详情
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/commodity/priceAdjust/priceAdjustDetails")
    public void commodityPriceAdjustDetails(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CommoditySearchOption commoditySearchOption = Request2ModelUtils.covert(CommoditySearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(commoditySearchOption);
        List<CommodityPriceAdjustDetailExtend> list = sellPriceAdjustService.detailExtends(commoditySearchOption);
        Integer count = sellPriceAdjustService.countPriceAdjustDetail(commoditySearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, commoditySearchOption);
    }

    /**
     * 添加或修改
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/commodity/priceAdjust/updateAdjustDetail")
    public void updatePriceAdjustDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String postData = RequestUtils.getRequestUtils().postData(request);
        JSONArray jsonArray = JSON.parseArray(postData);
        sellPriceAdjustService.updatePriceAdjustDetail(jsonArray, employee);
        ResponseUtil.sendSuccessResponse(request, response, postData);
    }

    /**
     * 禁用
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/commodity/priceAdjust/adjustDetailDisable")
    public void priceAdjustDisable(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String s = request.getParameter("commodity_price_adjust_detail_ids");
        List<Integer> list = SplitUtils.splitToList(s, ",");
        sellPriceAdjustService.disable(list);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 导入调价
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/commodity/priceAdjust/import")
    public void priceAdjustImport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        if(multipartResolver.isMultipart(request)) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            Iterator iterator = multiRequest.getFileNames();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                MultipartFile file = multiRequest.getFile(key);
                if (file != null) {
                    String filename = file.getOriginalFilename();
                    String[] s = filename.split("\\.");
                    String ext = s[s.length - 1];
                    if (ext.equals("xls")) {//
                        //开始处理文件
                        if (commodityPriceAdjustImport(file, employee)) {
                            ResponseUtil.sendSuccessResponse(request, response);
                            UploadUtil.saveFile(file, filename, employee, request.getRequestURI());
                        }else {
                            ResponseUtil.sendResponse(request, response, new ResultData("0x0015", "文件填写错误"));
                        }
                    }else {
                        ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "需要导入xls文件"));
                    }
                }
            }
        }
    }

    private boolean commodityPriceAdjustImport(MultipartFile file, Employee employee) {
        try {
            Workbook workbook = Workbook.getWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheet(0);
            int rows = sheet.getRows();
            List<CommodityPriceAdjustDetailExtend> list = new ArrayList<>();
            for (int i = 1; i < rows; i++) {
                Cell[] cell = sheet.getRow(i);
                CommodityPriceAdjustDetailExtend commodityPriceAdjustDetailExtend = new CommodityPriceAdjustDetailExtend();
                String commodity_code = cell[1].getContents().trim();
                if (commodity_code == null || commodity_code.equals("")) {
                    continue;
                }
                Commodity commodityByCode = commodityService.getCommodityByCode(commodity_code);
                if (commodityByCode == null) {
                    return false;
                }
                int commodity_id = commodityByCode.getCommodity_id();
                commodityPriceAdjustDetailExtend.setCommodity_id(commodity_id);

                if (cell.length > 5) {//
                    BigDecimal beforeSellPrice = new BigDecimal(cell[5].getContents().trim());
                    if (beforeSellPrice == null) {
                        return false;
                    }
                    commodityPriceAdjustDetailExtend.setBefore_sell_price(beforeSellPrice);
                }
                if (cell.length > 6) {
                    BigDecimal afterSellPrice = new BigDecimal(cell[6].getContents().trim());
                    if (afterSellPrice == null) {
                        return false;
                    }
                    commodityPriceAdjustDetailExtend.setAfter_sell_price(afterSellPrice);
                }
                if (cell.length > 7) {
                    BigDecimal beforePurchasePrice = new BigDecimal(cell[7].getContents().trim());
                    if (beforePurchasePrice == null) {
                        return false;
                    }
                    commodityPriceAdjustDetailExtend.setBefore_purchase_price(beforePurchasePrice);
                }
                if (cell.length > 8) {
                    BigDecimal afterPurchasePrice = new BigDecimal(cell[8].getContents().trim());
                    if (afterPurchasePrice == null) {
                        return false;
                    }
                    commodityPriceAdjustDetailExtend.setAfter_purchase_price(afterPurchasePrice);
                }
                list.add(commodityPriceAdjustDetailExtend);
            }
            sellPriceAdjustService.createCommodityPriceAdjust(list, employee);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

