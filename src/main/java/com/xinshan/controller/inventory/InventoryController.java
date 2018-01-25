package com.xinshan.controller.inventory;

import com.xinshan.model.*;
import com.xinshan.model.extend.commodity.CommodityNumExtend;
import com.xinshan.model.extend.inventory.InventoryHistoryDetailExtend;
import com.xinshan.service.InventoryService;
import com.xinshan.utils.*;
import com.xinshan.pojo.commodity.CommoditySearchOption;
import com.xinshan.pojo.inventory.InventorySearchOption;
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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * 商品库存
 * Created by mxt on 16-11-9.
 */
@Controller
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    /**
     * 库存列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/order/inventory/commodityNumList", "/order/order/commodityNumList", "/order/inventoryOut/commodityNumList"})
    public void commodityNumList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CommoditySearchOption commoditySearchOption = Request2ModelUtils.covert(CommoditySearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(commoditySearchOption);
        List<CommodityNumExtend> list = inventoryService.commodityNumList(commoditySearchOption);
        Integer count = inventoryService.countCommodityNum(commoditySearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, commoditySearchOption);
    }

    /**
     * 移库操作
     * @param request
     * @param response
     * @throws IOException
     */
    /*@RequestMapping("/order/inventory/commodityNumMove")
    public void commodityMove(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int commodity_num_id = Integer.parseInt(request.getParameter("commodity_num_id"));
        int move_num = Integer.parseInt(request.getParameter("move_num"));
        int move_store_id = Integer.parseInt(request.getParameter("move_store_id"));
        int sample = Integer.parseInt(request.getParameter("sample"));
        inventoryService.commodityNumMove(commodity_num_id, move_num, move_store_id, sample);
        ResponseUtil.sendSuccessResponse(request, response);
    }*/

    /**
     * 库存导入
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/commodity/commodity/inventoryInit")
    public void inventoryInit(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
                        if (inventoryInit(file, employee)) {
                            ResponseUtil.sendSuccessResponse(request, response);
                            UploadUtil.saveFile(file, filename, employee, request.getRequestURI());
                        }else {
                            ResponseUtil.sendResponse(request, response, new ResultData("0x0015", "导入失败"));
                        }
                    }else {
                        ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "需要导入xls文件"));
                    }
                }
            }
        }
    }

    private boolean inventoryInit(MultipartFile file, Employee employee) {
        try {
            Workbook workbook = Workbook.getWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheet(0);
            int rows = sheet.getRows();
            List<Object[]> list = new ArrayList<>();
            for (int i = 1; i < rows; i++) {
                Cell[] cell = sheet.getRow(i);
                String supplier_commodity_code = null;//原货号
                String commodity_code = null;//新货号
                String commodity_name = null;//商品名称
                String commodity_size = null;//商品规格
                String store_name = null;//存放位置
                int num = 0;
                String supplier_name = null;//供应商名称
                String contacts = null;//委托人
                int sample = 0;
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
                    commodity_size = cell[3].getContents().trim();
                }

                if (cell.length > 4) {
                    store_name = cell[4].getContents().trim();
                }

                if (cell.length > 5) {
                    if (!cell[5].getContents().trim().equals("")) {
                        num = new BigDecimal(cell[5].getContents().trim()).intValue();
                    }
                }

                if (cell.length > 6) {
                    supplier_name = cell[6].getContents().trim();
                }
                if (cell.length > 7) {
                    contacts = cell[7].getContents().trim();
                }
                if (cell.length > 8) {
                    if (!cell[8].getContents().trim().equals("")) {
                        sample = new BigDecimal(cell[8].getContents().trim()).intValue();
                    }
                }

                Commodity commodity = new Commodity();
                commodity.setSupplier_commodity_code(supplier_commodity_code);//原货号
                commodity.setCommodity_code(commodity_code); //新货号
                commodity.setCommodity_name(commodity_name);//商品名称
                commodity.setCommodity_size(commodity_size);//商品规格
                commodity.setRecord_employee_code(employee.getEmployee_code());
                commodity.setRecord_employee_name(employee.getEmployee_name());

                Object[] o = new Object[]{commodity, supplier_name, contacts, sample, store_name, num};
                list.add(o);
            }
            inventoryService.inventoryInit(list, employee);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     *
     * 库存导出
     * @param request
     * @param response
     * @throws IOException
     */
    /*@RequestMapping("/inventory/inventory/inventoryExport")
    public void inventoryExport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CommoditySearchOption commoditySearchOption = Request2ModelUtils.covert(CommoditySearchOption.class, request);
        boolean noParam = CommodityUtil.getCommodityUtil().noParam(commoditySearchOption);
        if (noParam) {
            String filename = new String(("库存导出").getBytes("utf-8"), CommonUtils.CHARSET) + ".xls";
            response.setHeader("Content-Type", "application/msexcel");
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);
            response.setHeader("Cache-Control", "max-age=0");
            File file = new File(CommonUtils.FILE_UPLOAD_DIR + "/" + filename);
            OutputStream outputStream = response.getOutputStream();
            byte[] b = new byte[4096];
            int size = 0;
            if(file.exists()){
                FileInputStream fileInputStream=new FileInputStream(file);
                while ((size=fileInputStream.read(b))!=-1) {
                    outputStream.write(b, 0, size);
                }
            }
            outputStream.flush();
        }else {
            List<CommodityNumExtend> list = inventoryService.commodityNumList1(commoditySearchOption);
            try {
                String filename = new String(("库存导出").getBytes("utf-8"),"iso8859-1") + ".xls";
                File file = new File(CommonUtils.FILE_UPLOAD_DIR + "/" + filename);
                if (!file.exists()) {
                    file.createNewFile();
                }
                WritableWorkbook workbook = Workbook.createWorkbook(file);
                WritableSheet sheet = workbook.createSheet("sheet1", 0);
                WritableFont normalFont = new WritableFont(WritableFont.ARIAL, 10);
                WritableFont boldFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);

                WritableCellFormat wcf_left = new WritableCellFormat(normalFont);
                wcf_left.setBorder(Border.NONE, BorderLineStyle.THIN); // 线条
                wcf_left.setVerticalAlignment(VerticalAlignment.CENTRE); // 文字垂直对齐
                wcf_left.setAlignment(Alignment.LEFT); // 文字水平对齐
                wcf_left.setWrap(false); // 文字是否换行
                WritableCellFormat header_format = new WritableCellFormat(boldFont);

                //WritableCellFormat wcf_left_format = new WritableCellFormat(new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD));
                //1正常；0逾期；-1冻结
                int row = 0;
                Counter col = new Counter();
                sheet.addCell(new Label(col.getN(), row, "原货号", header_format));//
                sheet.addCell(new Label(col.getN(), row, "新货号", header_format));//
                sheet.addCell(new Label(col.getN(), row, "样品", header_format));//
                sheet.addCell(new Label(col.getN(), row, "是否退货商品", header_format));//
                sheet.addCell(new Label(col.getN(), row, "商品名称", header_format));//
                sheet.addCell(new Label(col.getN(), row, "销货价", header_format));//
                sheet.addCell(new Label(col.getN(), row, "尺寸", header_format));//
                sheet.addCell(new Label(col.getN(), row, "供应商", header_format));//
                sheet.addCell(new Label(col.getN(), row, "委托人", header_format));//
                sheet.addCell(new Label(col.getN(), row, "存放场馆", header_format));//
                sheet.addCell(new Label(col.getN(), row, "库存数量", header_format));//
                row++;
                for (int i = 0; i < list.size(); i++) {
                    col.reset();
                    CommodityNumExtend commodityNumExtend = list.get(i);
                    Commodity commodity = commodityNumExtend.getCommodity();
                    Supplier supplier = commodityNumExtend.getSupplier();
                    CommodityStore commodityStore = commodityNumExtend.getCommodityStore();
                    sheet.addCell(new Label(col.getN(), row, commodity.getSupplier_commodity_code(), header_format));//
                    sheet.addCell(new Label(col.getN(), row, commodity.getCommodity_code(), header_format));//
                    if (commodityNumExtend.getSample() == 1) {
                        sheet.addCell(new Label(col.getN(), row, "是", header_format));//
                    }else {
                        sheet.addCell(new Label(col.getN(), row, "否", header_format));//
                    }if (commodityNumExtend.getReturn_commodity() == 1) {
                        sheet.addCell(new Label(col.getN(), row, "是", header_format));//
                    }else {
                        sheet.addCell(new Label(col.getN(), row, "否", header_format));//
                    }
                    sheet.addCell(new Label(col.getN(), row, commodity.getCommodity_name(), header_format));//
                    if (commodity.getPurchase_price() != null) {
                        sheet.addCell(new Number(col.getN(), row, commodity.getSell_price().doubleValue(), header_format));//
                    }else {
                        sheet.addCell(new Number(col.getN(), row, 0, header_format));//
                    }
                    sheet.addCell(new Label(col.getN(), row, commodity.getCommodity_size(), header_format));//
                    if (supplier != null) {
                        sheet.addCell(new Label(col.getN(), row, supplier.getSupplier_name(), header_format));//
                        sheet.addCell(new Label(col.getN(), row, supplier.getContacts(), header_format));//
                    }else {
                        sheet.addCell(new Label(col.getN(), row, "", header_format));//
                        sheet.addCell(new Label(col.getN(), row, "", header_format));//
                    }
                    sheet.addCell(new Label(col.getN(), row, commodityStore.getStore_name(), header_format));//
                    sheet.addCell(new Number(col.getN(), row, commodityNumExtend.getNum(), header_format));//

                    row++;
                }
                workbook.write();
                workbook.close();

                response.setHeader("Content-Type", "application/msexcel");
                response.setHeader("Content-Disposition", "attachment;filename=" + filename);
                response.setHeader("Cache-Control", "max-age=0");
                ServletOutputStream outputStream = response.getOutputStream();
                byte[] b = new byte[4096];
                int size = 0;
                if(file.exists()){
                    FileInputStream fileInputStream = new FileInputStream(file);
                    while ((size=fileInputStream.read(b))!=-1) {
                        outputStream.write(b, 0, size);
                    }
                }
                outputStream.flush();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

    /**
     *
     * 库存导出
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/inventory/inventory/inventoryExport")
    public void inventoryExport1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CommoditySearchOption commoditySearchOption = Request2ModelUtils.covert(CommoditySearchOption.class, request);
        List<CommodityNumExtend> list = inventoryService.commodityNumList1(commoditySearchOption);
        try {
            String filename = new String(("库存导出").getBytes("utf-8"),"iso8859-1") + ".xls";
            response.setHeader("Content-Type", "application/msexcel");
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);
            response.setHeader("Cache-Control", "max-age=0");

            WritableWorkbook workbook = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet sheet = workbook.createSheet("sheet1", 0);
            int row = 0;
            Counter col = new Counter();
            sheet.addCell(new Label(col.getN(), row, "原货号"));//
            sheet.addCell(new Label(col.getN(), row, "新货号"));//
            sheet.addCell(new Label(col.getN(), row, "样品"));//
            sheet.addCell(new Label(col.getN(), row, "是否退货商品"));//
            sheet.addCell(new Label(col.getN(), row, "商品名称"));//
            sheet.addCell(new Label(col.getN(), row, "进货价"));//
            sheet.addCell(new Label(col.getN(), row, "销货价"));//
            sheet.addCell(new Label(col.getN(), row, "尺寸"));//
            sheet.addCell(new Label(col.getN(), row, "供应商"));//
            sheet.addCell(new Label(col.getN(), row, "委托人"));//
            sheet.addCell(new Label(col.getN(), row, "存放场馆"));//
            sheet.addCell(new Label(col.getN(), row, "库存数量"));//
            row++;
            for (int i = 0; i < list.size(); i++) {
                col.reset();
                CommodityNumExtend commodityNumExtend = list.get(i);
                Commodity commodity = commodityNumExtend.getCommodity();
                Supplier supplier = commodityNumExtend.getSupplier();
                CommodityStore commodityStore = commodityNumExtend.getCommodityStore();
                sheet.addCell(new Label(col.getN(), row, commodity.getSupplier_commodity_code()));//
                sheet.addCell(new Label(col.getN(), row, commodity.getCommodity_code()));//
                if (commodityNumExtend.getSample() == 1) {
                    sheet.addCell(new Label(col.getN(), row, "是"));//
                }else {
                    sheet.addCell(new Label(col.getN(), row, "否"));//
                }if (commodityNumExtend.getReturn_commodity() == 1) {
                    sheet.addCell(new Label(col.getN(), row, "是"));//
                }else {
                    sheet.addCell(new Label(col.getN(), row, "否"));//
                }
                sheet.addCell(new Label(col.getN(), row, commodity.getCommodity_name()));//
                if (commodity.getSell_price() != null) {
                    sheet.addCell(new Number(col.getN(), row, commodity.getSell_price().doubleValue()));//
                }else {
                    sheet.addCell(new Number(col.getN(), row, 0));//
                }
                if (commodity.getPurchase_price() != null) {
                    sheet.addCell(new Number(col.getN(), row, commodity.getSell_price().doubleValue()));//
                }else {
                    sheet.addCell(new Number(col.getN(), row, 0));//
                }
                sheet.addCell(new Label(col.getN(), row, commodity.getCommodity_size()));//
                if (supplier != null) {
                    sheet.addCell(new Label(col.getN(), row, supplier.getSupplier_name()));//
                    sheet.addCell(new Label(col.getN(), row, supplier.getContacts()));//
                }else {
                    sheet.addCell(new Label(col.getN(), row, ""));//
                    sheet.addCell(new Label(col.getN(), row, ""));//
                }
                sheet.addCell(new Label(col.getN(), row, commodityStore.getStore_name()));//
                sheet.addCell(new Number(col.getN(), row, commodityNumExtend.getNum()));//

                row++;
            }
            workbook.write();
            workbook.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
