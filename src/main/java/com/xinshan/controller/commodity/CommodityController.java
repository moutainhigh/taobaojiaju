package com.xinshan.controller.commodity;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.supplier.SupplierComponents;
import com.xinshan.components.websocket.CommoditySyncComponent;
import com.xinshan.model.*;
import com.xinshan.model.extend.commodity.CommodityExtend;
import com.xinshan.model.extend.commodity.CommodityPrint;
import com.xinshan.service.CommodityService;
import com.xinshan.service.InventoryService;
import com.xinshan.service.SupplierService;
import com.xinshan.utils.*;
import com.xinshan.pojo.commodity.CommoditySearchOption;
import com.xinshan.utils.commodity.CommodityUtil;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * 商品基本信息
 * Created by mxt on 16-10-17.
 */
@Controller
public class CommodityController {
    @Autowired
    private CommodityService commodityService;

    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private SupplierService supplierService;

    /**
     * 添加单位
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/commodity/addUnit")
    public void createUnit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CommodityUnit commodityUnit = Request2ModelUtils.covert(CommodityUnit.class, request);
        if (commodityUnit.getCommodity_unit_id() == null) {
            commodityService.createUnit(commodityUnit);
        }else {
            commodityService.updateUnit(commodityUnit);
        }
        ResponseUtil.sendSuccessResponse(request, response, commodityUnit);
    }

    /**
     * 单位列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/commodity/unitList")
    public void unitList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CommoditySearchOption commoditySearchOption = Request2ModelUtils.covert(CommoditySearchOption.class, request);
        List<CommodityUnit> list = commodityService.units(commoditySearchOption);
        ResponseUtil.sendSuccessResponse(request, response, list);
    }

    /**
     * 删除单位
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/commodity/deleteUnit")
    public void deleteUnit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int unit_id = Integer.parseInt(request.getParameter("unit_id"));
        commodityService.deleteUnit(unit_id);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 添加场馆
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/supplier/commodity/addStore", "/order/inventoryIn/addStore"})
    public void createStore(HttpServletRequest request, HttpServletResponse response) throws IOException{
        CommodityStore commodityStore = Request2ModelUtils.covert(CommodityStore.class, request);
        if (commodityStore.getCommodity_store_id() == null) {
            commodityStore.setStore_enable(1);
            inventoryService.createStore(commodityStore);
        }else {
            inventoryService.updateStore(commodityStore);
        }
        ResponseUtil.sendSuccessResponse(request, response, commodityStore);
    }

    /**
     * 修改场馆名
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/commodity/updateStore")
    public void updateStore(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CommodityStore commodityStore = Request2ModelUtils.covert(CommodityStore.class, request);
        inventoryService.updateStore(commodityStore);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 删除场馆
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/commodity/deleteStore")
    public void deleteStore(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int store_id = Integer.parseInt(request.getParameter("store_id"));
        inventoryService.deleteStore(store_id);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 场馆列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/supplier/commodity/storeList","/order/inventoryIn/storeList", "/order/inventory/storeList"})
    public void storeList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CommoditySearchOption commoditySearchOption = Request2ModelUtils.covert(CommoditySearchOption.class, request);
        if (commoditySearchOption == null) {
            commoditySearchOption = new CommoditySearchOption();
        }
        if (commoditySearchOption.getStore_enable() == null) {
            commoditySearchOption.setStore_enable(1);
        }
        List<CommodityStore> list = inventoryService.stores(commoditySearchOption);
        ResponseUtil.sendSuccessResponse(request, response, list);
    }

    /**
     * 添加颜色
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/commodity/addColor")
    public void createColor(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CommodityColor commodityColor = Request2ModelUtils.covert(CommodityColor.class, request);
        if (commodityColor.getCommodity_color_id() == null) {
            commodityService.createColor(commodityColor);
        }else {
            commodityService.updateColor(commodityColor);
        }
        ResponseUtil.sendSuccessResponse(request, response, commodityColor);
    }

    /**
     * 删除颜色
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/commodity/deleteColor")
    public void deleteColor(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int color_id = Integer.parseInt(request.getParameter("color_id"));
        commodityService.deleteColor(color_id);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 颜色列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/commodity/colorList")
    public void colorList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CommoditySearchOption commoditySearchOption = Request2ModelUtils.covert(CommoditySearchOption.class, request);
        List<CommodityColor> list = commodityService.colors(commoditySearchOption);
        ResponseUtil.sendSuccessResponse(request, response, list);
    }

    /**
     * 添加商品
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/commodity/addCommodity")
    public void createCommodity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathDir = request.getSession().getServletContext().getRealPath("/WEB-INF/resources/tmp");
        String namespace = "commodity";
        String object = "supplier";
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String postData = RequestUtils.getRequestUtils().postData(request);
        CommodityExtend commodityExtend = JSONObject.parseObject(postData, CommodityExtend.class);
        commodityExtend.setRecord_employee_name(employee.getEmployee_name());
        commodityExtend.setRecord_employee_code(employee.getEmployee_code());

        Commodity commodity = commodityService.getCommodityByCode(commodityExtend.getCommodity_code());
        if (commodity != null) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "货号重复"));
            return;
        }
        if (commodityExtend.getCommodity_id() == null) {
            String barCodeImg = commodityExtend.getBar_code_img();
            List<String> list = SplitUtils.splitToStrList(barCodeImg, ",");
            if (list != null && list.size() >0) {
                String bar_code_img = null;
                        //AttachmentUtil.getAttachmentUtil().fileAttachments(list, pathDir, namespace, object);
                commodityExtend.setBar_code_img(bar_code_img);
            }
            list = SplitUtils.splitToStrList(commodityExtend.getCommodity_img(), ",");
            if (list != null && list.size() >0) {
                String commodity_img = null;
                        //AttachmentUtil.getAttachmentUtil().fileAttachments(list, pathDir, namespace, object);
                commodityExtend.setCommodity_img(commodity_img);
            }
            commodityExtend.setRecord_date(DateUtil.currentDate());
            if (commodityExtend.getSichuan() == null) {
                commodityExtend.setSichuan(0);
            }
            commodityService.createCommodity(commodityExtend);
        }
        ResponseUtil.sendSuccessResponse(request, response, postData);
        CommoditySyncComponent.commoditySync(commodityExtend.getCommodity_id());
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/commodity/updateCommodity")
    public void commodityEdit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String postData = RequestUtils.getRequestUtils().postData(request);
        CommodityExtend commodityExtend = JSONObject.parseObject(postData, CommodityExtend.class);

        commodityService.updateCommodity(commodityExtend, employee);
        ResponseUtil.sendSuccessResponse(request, response, postData);
        CommoditySyncComponent.commoditySync(commodityExtend.getCommodity_id());
    }

    /**
     * 商品列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/supplier/commodity/commodityList","/order/order/commodityList", "/order/purchase/commodityList", "/order/inventoryIn/commodityList"})
    public void commodityList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CommoditySearchOption commoditySearchOption = Request2ModelUtils.covert(CommoditySearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(commoditySearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(commoditySearchOption);
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        if (employee.getPosition_id() == 0) {//供应商委托人
            List<Integer> list = SupplierComponents.concractsSupplierIds(employee.getEmployee_code());
            commoditySearchOption.setSupplierIds(SplitUtils.listToString(list));
        }
        List<CommodityExtend> list = commodityService.commodityList(commoditySearchOption);
        Integer count = commodityService.countCommodity(commoditySearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, commoditySearchOption);
    }

    /**
     * 打印标签
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/commodity/print")
    public void commodityPrint(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CommoditySearchOption commoditySearchOption = Request2ModelUtils.covert(CommoditySearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(commoditySearchOption);
        List<CommodityExtend> list = commodityService.commodityList(commoditySearchOption);
        List<CommodityPrint> commodityPrints = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CommodityExtend commodityExtend = list.get(i);
            CommodityPrint commodityPrint = new CommodityPrint(commodityExtend);
            commodityPrints.add(commodityPrint);
        }
        ResponseUtil.sendSuccessResponse(request, response, commodityPrints);
    }


    @RequestMapping("/supplier/commodity/import")
    public void commodityImport(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
                        String s1 = commodityImport(file, employee);
                        if (s1.equals("")) {
                            ResponseUtil.sendSuccessResponse(request, response);
                            UploadUtil.saveFile(file, filename, employee, request.getRequestURI());
                        }else {
                            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", s1));
                        }
                    }else {
                        ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "需要导入xls文件"));
                    }
                }
            }
        }
    }

    public String commodityImport(MultipartFile file, Employee employee) {
        try {
            Workbook workbook = Workbook.getWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheet(0);
            int rows = sheet.getRows();
            List<Object[]> list = new ArrayList<>();
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 1; i < rows; i++) {
                Cell[] cell = sheet.getRow(i);
                String supplier_commodity_code = null;
                String commodity_code = null;
                String commodity_name = null;
                String supplier_name = null;
                String series_name = null;
                String size = null;
                String store_name = null;
                BigDecimal purchase_price = null;
                BigDecimal sell_price = null;
                int num = 0, sichuan = 0, sample = 0, guangdong = 0;
                String contacts = null;
                String contacts_phone_number = null;
                String contacts_address = null;
                if (cell.length > 0) {
                    supplier_commodity_code = cell[0].getContents().trim();
                }
                if (cell.length > 1) {
                    commodity_code = cell[1].getContents().trim();
                }
                if (cell.length > 2) {
                    commodity_name = cell[2].getContents().trim();
                }
                if (cell.length > 3) {
                    supplier_name = cell[3].getContents().trim();
                }
                if (cell.length > 4) {
                    series_name = cell[4].getContents().trim();
                }
                if (cell.length > 5) {
                    if (!cell[5].getContents().trim().equals("")) {
                        purchase_price = new BigDecimal(cell[5].getContents().trim());
                    }
                }
                if (cell.length > 6) {
                    if (!cell[6].getContents().trim().equals("")) {
                        sell_price = new BigDecimal(cell[6].getContents().trim());
                    }
                }
                if (cell.length > 7) {
                    size = cell[7].getContents().trim();
                }
                if (cell.length > 8){
                    store_name = cell[8].getContents().trim();
                }
                if (cell.length > 9) {
                    num = Integer.parseInt(cell[9].getContents().trim());
                }
                if (cell.length > 10) {
                    sichuan = Integer.parseInt(cell[10].getContents().trim());
                }
                if (cell.length > 11) {
                    sample = Integer.parseInt(cell[11].getContents().trim());
                }
                if (cell.length > 12) {
                    contacts = cell[12].getContents().trim();
                }
                if (cell.length > 13) {
                    contacts_phone_number = cell[13].getContents().trim();
                }
                if (cell.length > 14) {
                    contacts_address = cell[14].getContents().trim();
                }
                if (cell.length > 15) {
                    guangdong = Integer.parseInt(cell[15].getContents().trim());
                }
                Commodity commodity = new Commodity();
                commodity.setCommodity_code(commodity_code);
                if (sell_price != null) {
                    //commodity.setSell_price(sell_price);
                }
                if (size != null) {
                    commodity.setCommodity_size(size);
                }
                if (commodity_name != null) {
                    commodity.setCommodity_name(commodity_name);
                }
                commodity.setRecord_employee_code(employee.getEmployee_code());
                commodity.setRecord_employee_name(employee.getEmployee_name());
                if (supplier_commodity_code != null) {
                    commodity.setSupplier_commodity_code(supplier_commodity_code);
                }
                if (purchase_price != null) {
                    //commodity.setPurchase_price(purchase_price);
                }
                commodity.setRecord_date(new Date());
                Object[] o = {commodity, supplier_name, series_name, store_name, num, sichuan,
                        sample, contacts, contacts_phone_number, contacts_address, guangdong};
                list.add(o);
                Supplier supplier = supplierService.getSupplierByName(supplier_name);
                if (supplier == null) {
                    stringBuffer.append("供应商不存在，第" + (i) +"行，" + supplier_name);
                    break;
                }else {
                    if (!supplier.getContacts().equals(contacts)) {
                        stringBuffer.append("供应商已存在, 供应商委托人不正确，第" + (i) +"行，" + supplier_name);
                        break;
                    }
                }
            }
            if (stringBuffer.toString().equals("")) {
                commodityService.commodityImport(list, employee);
                return "";
            }
            return stringBuffer.toString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }



    /**
     * 微信端商品信息查看
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/commodity/commodity/detail/wx")
    public void commodityMsg(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int commodity_id = Integer.parseInt(request.getParameter("commodity_id"));
        CommoditySearchOption commoditySearchOption = new CommoditySearchOption();
        commoditySearchOption.setCommodity_id(commodity_id);
        List<CommodityExtend> list = commodityService.commodityList(commoditySearchOption);
        if (list != null && list.size() == 1) {
            ResponseUtil.sendSuccessResponse(request, response, list.get(0));
        } else {
            ResponseUtil.sendSuccessResponse(request, response);
        }
    }

    /**
     * 商品价格变动
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/commodity/commodity/modifyPrice")
    public void commodityPrice(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int commodity_id = Integer.parseInt(request.getParameter("commodity_id"));
        BigDecimal price = new BigDecimal(request.getParameter("price"));
        Commodity commodity = commodityService.getCommodityById(commodity_id);
        if (commodity == null) {
            return;
        }
        if (commodity.getSell_price().longValue() == price.longValue()) {
            //价格没有变动
            return;
        }
        commodity.setEdit_employee_name(employee.getEmployee_name());
        commodity.setEdit_employee_code(employee.getEmployee_code());
        commodity.setEdit_date(DateUtil.currentDate());
        commodityService.commodityPrice(commodity, employee);
        ResponseUtil.sendSuccessResponse(request, response);
        CommoditySyncComponent.commoditySync(commodity.getCommodity_id());
    }

    /**
     * 商品下架
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/commodity/commodity/commodityStatus")
    public void commodityStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int commodity_id = Integer.parseInt(request.getParameter("commodity_id"));
        int commodity_status = Integer.parseInt(request.getParameter("commodity_status"));
        Commodity commodity = commodityService.getCommodityById(commodity_id);
        commodity.setCommodity_status(commodity_status);
        commodity.setEdit_employee_code(employee.getEmployee_code());
        commodity.setEdit_employee_name(employee.getEmployee_name());
        commodity.setEdit_date(DateUtil.currentDate());
        commodityService.updateCommodity(commodity);
        ResponseUtil.sendSuccessResponse(request, response);
        CommoditySyncComponent.commoditySync(commodity.getCommodity_id());
    }

    /**
     * 商品信息导入
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/commodity/commodity/commodityInit")
    public void commodityInit(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
                        String s1 = commodityInit(file, employee);
                        if (s1.equals("")) {
                            ResponseUtil.sendSuccessResponse(request, response);
                            UploadUtil.saveFile(file, filename, employee, request.getRequestURI());
                        }else {
                            ResponseUtil.sendResponse(request, response, new ResultData("0x0015", s1));
                        }
                    }else {
                        ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "需要导入xls文件"));
                    }
                }
            }
        }
    }

    private String commodityInit(MultipartFile file, Employee employee) {
        try {
            Workbook workbook = Workbook.getWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheet(0);
            int rows = sheet.getRows();
            List<Object[]> list = new ArrayList<>();
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 1; i < rows; i++) {
                Cell[] cell = sheet.getRow(i);
                String supplier_commodity_code = null;//原货号
                String commodity_code = null;//新货号
                String commodity_name = null;//商品名称
                String commodity_size = null;//商品规格
                String supplier_name = null;//供应商名称
                String contacts = null;//委托人
                BigDecimal sell_price = null;//售价
                BigDecimal purchase_price = null;//进价
                BigDecimal commodity_freight = null;
                int sample = 0;
                if (cell.length > 0) {
                    supplier_commodity_code = cell[0].getContents().trim();
                }
                if (cell.length > 1) {
                    commodity_code = cell[1].getContents().trim();
                }
                if (commodity_code == null) {
                    continue;
                }
                if (cell.length > 2) {
                    commodity_name = cell[2].getContents().trim();
                }
                if (cell.length > 3) {
                    commodity_size = cell[3].getContents().trim();
                }
                if (cell.length > 4) {
                    supplier_name = cell[4].getContents().trim();
                }
                if (cell.length > 5) {
                    contacts = cell[5].getContents().trim();
                }
                if (cell.length > 6) {
                    if (!cell[6].getContents().trim().equals("")) {
                        sell_price = new BigDecimal(cell[6].getContents().trim());
                    }
                }
                if (cell.length > 7) {
                    if (!cell[7].getContents().trim().equals("")) {
                        purchase_price = new BigDecimal(cell[7].getContents().trim());
                    }
                }
                if (cell.length > 9) {
                    if (!cell[9].getContents().trim().equals("")) {
                        sample = new BigDecimal(cell[9].getContents().trim()).intValue();
                    }
                }

                if (cell.length > 10) {
                    if (!cell[10].getContents().trim().equals("")) {
                        commodity_freight = new BigDecimal(cell[10].getContents().trim());
                    }
                }

                Commodity commodity = new Commodity();
                commodity.setSupplier_commodity_code(supplier_commodity_code);//原货号
                commodity.setCommodity_code(commodity_code); //新货号
                commodity.setCommodity_name(commodity_name);//商品名称
                commodity.setCommodity_size(commodity_size);//商品规格
                commodity.setSell_price(sell_price);//售价
                commodity.setPurchase_price(purchase_price);//进价
                commodity.setRecord_employee_code(employee.getEmployee_code());
                commodity.setRecord_employee_name(employee.getEmployee_name());
                commodity.setCommodity_freight(commodity_freight);
                Object[] o = new Object[]{commodity, supplier_name, contacts, sample};
                list.add(o);
                Supplier supplier = supplierService.getSupplierByName(supplier_name);
                if (supplier == null) {
                    stringBuffer.append("供应商不存在，第" + (i) +"行，" + supplier_name);
                    break;
                }else {
                    if (!supplier.getContacts().equals(contacts)) {
                        stringBuffer.append("供应商已存在, 供应商委托人不正确，第" + (i) +"行，" + supplier_name);
                        break;
                    }
                }
            }
            if (stringBuffer.toString().equals("")) {
                commodityService.commodityInit(list, employee);
                return "";
            }
            return stringBuffer.toString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 商品档案导出
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/commodity/commodity/commodityExport")
    public void commodityExport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CommoditySearchOption commoditySearchOption = Request2ModelUtils.covert(CommoditySearchOption.class, request);
        List<HashMap> list = commodityService.commodityExport(commoditySearchOption);
        try {
            OutputStream outputStream = response.getOutputStream();
            response.reset();
            response.setHeader("Content-Type", "application/msexcel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("商品档案导出").getBytes("utf-8"),"iso8859-1") + ".xls");
            response.setHeader("Cache-Control", "max-age=0");
            WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
            WritableSheet sheet = workbook.createSheet("sheet1", 0);

            //WritableCellFormat wcf_left_format = new WritableCellFormat(new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD));
            //1正常；0逾期；-1冻结
            int row = 0;
            Counter col = new Counter();
            sheet.addCell(new Label(col.getN(), row, "原货号"));//
            sheet.addCell(new Label(col.getN(), row, "新货号"));//
            sheet.addCell(new Label(col.getN(), row, "名称"));//
            sheet.addCell(new Label(col.getN(), row, "供应商"));//
            sheet.addCell(new Label(col.getN(), row, "委托人"));//
            sheet.addCell(new Label(col.getN(), row, "颜色尺寸"));//
            sheet.addCell(new Label(col.getN(), row, "进货价"));//
            sheet.addCell(new Label(col.getN(), row, "标准售价"));//
            sheet.addCell(new Label(col.getN(), row, "含运费"));//
            sheet.addCell(new Label(col.getN(), row, "不含运费售价"));//
            row++;
            for (int i = 0; i < list.size(); i++) {
                col.reset();
                HashMap hashMap = list.get(i);
                sheet.addCell(new Label(col.getN(), row, hashMap.get("supplier_commodity_code")==null ? "":hashMap.get("supplier_commodity_code").toString()));//
                sheet.addCell(new Label(col.getN(), row, hashMap.get("commodity_code")==null ? "":hashMap.get("commodity_code").toString()));//
                sheet.addCell(new Label(col.getN(), row, hashMap.get("commodity_name")==null ? "":hashMap.get("commodity_name").toString()));//
                String supplier_name = hashMap.get("supplier_name")==null ? "":hashMap.get("supplier_name").toString();
                String contacts = hashMap.get("contacts")==null ? "":hashMap.get("contacts").toString();
                String commodity_size = hashMap.get("commodity_size")==null ? "":hashMap.get("commodity_size").toString();
                sheet.addCell(new Label(col.getN(), row, supplier_name));//
                sheet.addCell(new Label(col.getN(), row, contacts));//
                sheet.addCell(new Label(col.getN(), row, commodity_size));//
                sheet.addCell(new Number(col.getN(), row, hashMap.get("purchase_price")==null ? 0:new BigDecimal(hashMap.get("purchase_price").toString()).doubleValue()));//
                BigDecimal sell_price = hashMap.get("sell_price")==null ? new BigDecimal("0"):new BigDecimal(hashMap.get("sell_price").toString());
                BigDecimal commodity_freight = hashMap.get("commodity_freight")==null ? new BigDecimal("0"):new BigDecimal(hashMap.get("commodity_freight").toString());
                sheet.addCell(new Number(col.getN(), row, sell_price.doubleValue()));//
                sheet.addCell(new Number(col.getN(), row, commodity_freight.doubleValue()));//
                sheet.addCell(new Number(col.getN(), row, sell_price.subtract(commodity_freight).doubleValue()));//
                row++;
            }
            ResponseUtil.exportResponse(request, response, workbook);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/commodity/commodity/commoditySample")
    public void commoditySample(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int commodity_sample = Integer.parseInt(request.getParameter("commodity_sample"));
        String commodity_ids = request.getParameter("commodity_ids");
        commodityService.commoditySample(commodity_ids, commodity_sample, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 设置商品是否广东馆
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/commodity/commodity/guangdong")
    public void commodityGuangdong(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int supplier_id = Integer.parseInt(request.getParameter("supplier_id"));
        int guangdong = Integer.parseInt(request.getParameter("guangdong"));
        commodityService.commodityGuangdong(guangdong, supplier_id);
        ResponseUtil.sendSuccessResponse(request, response);
    }

}
