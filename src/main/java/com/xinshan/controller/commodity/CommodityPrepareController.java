package com.xinshan.controller.commodity;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.supplier.SupplierComponents;
import com.xinshan.model.CommodityPrepare;
import com.xinshan.model.Employee;
import com.xinshan.model.extend.commodity.CommodityPrepareExtend;
import com.xinshan.pojo.commodity.CommoditySearchOption;
import com.xinshan.service.CommodityPrepareService;
import com.xinshan.utils.*;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mxt on 17-9-6.
 */
@Controller
public class CommodityPrepareController {
    @Autowired
    private CommodityPrepareService commodityPrepareService;

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/commodity/commodity/createPrepare")
    public void createCommodityPrepare(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String postData = RequestUtils.getRequestUtils().postData(request);
        CommodityPrepareExtend commodityPrepare = JSONObject.parseObject(postData, CommodityPrepareExtend.class);
        commodityPrepare.setPrepare_status(0);
        commodityPrepare.setRecord_date(DateUtil.currentDate());
        commodityPrepare.setRecord_employee_code(employee.getEmployee_code());
        commodityPrepare.setRecord_employee_name(employee.getEmployee_name());
        commodityPrepareService.createCommodityPrepare(commodityPrepare);
        ResponseUtil.sendSuccessResponse(request, response, commodityPrepare);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/commodity/commodity/updatePrepare")
    public void updateCommodityPrepare(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String postData = RequestUtils.getRequestUtils().postData(request);
        CommodityPrepareExtend commodityPrepare = JSONObject.parseObject(postData, CommodityPrepareExtend.class);
        commodityPrepareService.updateCommodityPrepare(commodityPrepare);
        ResponseUtil.sendSuccessResponse(request, response, commodityPrepare);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/commodity/commodity/commodityPrepareList")
    public void commodityPrepareList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        CommoditySearchOption commoditySearchOption = Request2ModelUtils.covert(CommoditySearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(commoditySearchOption);
        if (employee.getPosition_id() == 0) {
            /*List<Integer> list = SupplierComponents.concractsSupplierIds(employee.getEmployee_code());
            commoditySearchOption.setSupplierIdList(list);*/
            commoditySearchOption.setRecord_employee_code(employee.getEmployee_code());
        }
        List<CommodityPrepareExtend> list = commodityPrepareService.commodityPrepareList(commoditySearchOption);
        Integer count = commodityPrepareService.countCommodityPrepare(commoditySearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, commoditySearchOption);
    }

    /**
     * 审核
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/commodity/commodity/check")
    public void commodityPrepareCheck(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String commodity_prepare_ids = request.getParameter("commodity_prepare_ids");
        List<Integer> list = SplitUtils.splitToList(commodity_prepare_ids, ",");
        CommoditySearchOption commoditySearchOption = new CommoditySearchOption();
        commoditySearchOption.setCommodityPrepareIds(list);
        List<CommodityPrepareExtend> commodityPrepareExtends = commodityPrepareService.commodityPrepareList(commoditySearchOption);
        commodityPrepareService.commodityPrepareCheck(commodityPrepareExtends, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 导入模板下载
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/commodity/commodity/commodityPrepareImportModel")
    public void commodityPrepareImportModel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            OutputStream outputStream = response.getOutputStream();
            response.reset();
            response.setHeader("Content-Type", "application/msexcel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("供应商商品档案导入模板").getBytes("utf-8"),"iso8859-1") + ".xls");
            response.setHeader("Cache-Control", "max-age=0");
            WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
            WritableSheet sheet = workbook.createSheet("sheet1", 0);
            Counter counter = new Counter();
            sheet.addCell(new Label(counter.getN(), 0, "原货号"));
            sheet.addCell(new Label(counter.getN(), 0, "新货号(必填)"));
            sheet.addCell(new Label(counter.getN(), 0, "商品名称(必填)"));
            sheet.addCell(new Label(counter.getN(), 0, "供应商名称(必填)"));
            sheet.addCell(new Label(counter.getN(), 0, "进货价"));
            sheet.addCell(new Label(counter.getN(), 0, "销货价"));
            sheet.addCell(new Label(counter.getN(), 0, "销货价包含运费"));
            sheet.addCell(new Label(counter.getN(), 0, "类别"));
            sheet.addCell(new Label(counter.getN(), 0, "颜色"));
            sheet.addCell(new Label(counter.getN(), 0, "单位"));
            sheet.addCell(new Label(counter.getN(), 0, "商品尺寸"));
            sheet.addCell(new Label(counter.getN(), 0, "是否四川馆(1是，0不是)"));
            sheet.addCell(new Label(counter.getN(), 0, "是否广东馆(1是，0不是)"));
            sheet.addCell(new Label(counter.getN(), 0, "商品备注"));
            workbook.write();
            workbook.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 导入
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/commodity/commodity/commodityPrepareImport")
    public void commodityPrepareImport(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
                        if (commodityPrepareImport(file, employee)) {
                            ResponseUtil.sendSuccessResponse(request, response);
                        }else {
                            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "文件内容有错误"));
                        }
                    }else {
                        ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "需要导入xls文件"));
                    }
                }
            }
        }
    }

    private boolean commodityPrepareImport(MultipartFile file, Employee employee) {
        try {
            Workbook workbook = Workbook.getWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheet(0);
            int rows = sheet.getRows();
            List<Object[]> list = new ArrayList<>();
            for (int i = 1; i < rows; i++) {
                Cell[] row = sheet.getRow(i);
                String supplier_commodity_code = null, commodity_code = null, commodity_name = null, supplier_name = null;
                BigDecimal purchase_price = null, sell_price = null, freight = null;
                String size = null, category_name = null, content = null, color = null;
                int sichuan = 0, guangdong = 0;
                if (row.length > 0) {
                    supplier_commodity_code = row[0].getContents();
                }
                if (row.length > 1) {
                    commodity_code = row[1].getContents();
                }
                if (row.length > 2) {
                    commodity_name = row[2].getContents();
                }
                if (row.length > 3) {
                    supplier_name = row[3].getContents();
                }
                if (row.length > 4) {
                    if (row[4].getContents() != null) {
                        try {
                            purchase_price = new BigDecimal(row[4].getContents().trim());
                        }catch (Exception e) {}
                    }
                }
                if (row.length > 5) {
                    if (row[5].getContents() != null) {
                        try {
                            sell_price = new BigDecimal(row[5].getContents().trim());
                        }catch (Exception e) {}
                    }
                }
                if (row.length > 6) {
                    if (row[5].getContents() != null) {
                        try {
                            freight = new BigDecimal(row[6].getContents().trim());
                        }catch (Exception e) {}
                    }
                }
                if (row.length > 7) {
                    category_name = row[7].getContents();
                }
                if (row.length > 8) {
                    color = row[8].getContents();
                }
                if (row.length > 9) {
                    size = row[9].getContents();
                }
                if (row.length > 10) {
                    try {
                        sichuan = Integer.parseInt(row[10].getContents());
                    }catch (Exception e) {}
                }
                if (row.length > 11) {
                    try {
                        guangdong = Integer.parseInt(row[11].getContents());
                    }catch (Exception e) {}
                }
                if (row.length > 12) {
                    content = row[12].getContents();
                }
                CommodityPrepare commodityPrepare = new CommodityPrepare();
                commodityPrepare.setSupplier_commodity_code(supplier_commodity_code);
                commodityPrepare.setCommodity_code(commodity_code);
                commodityPrepare.setCommodity_name(commodity_name);
                commodityPrepare.setPurchase_price(purchase_price);
                commodityPrepare.setSell_price(sell_price);
                commodityPrepare.setCommodity_freight(freight);
                commodityPrepare.setCommodity_size(size);
                commodityPrepare.setSichuan(sichuan);
                commodityPrepare.setGuangdong(guangdong);
                commodityPrepare.setCommodity_remark(content);
                list.add(new Object[]{commodityPrepare, supplier_name, category_name, color});
            }
            commodityPrepareService.commodityPrepareImport(list, employee);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
