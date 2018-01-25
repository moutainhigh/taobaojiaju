package com.xinshan.controller.supplier;

import com.xinshan.model.*;
import com.xinshan.model.extend.supplier.SupplierExtend;
import com.xinshan.model.extend.supplier.SupplierSeriesExtend;
import com.xinshan.service.EmployeeService;
import com.xinshan.service.SupplierService;
import com.xinshan.utils.*;
import com.xinshan.pojo.supplier.SupplierSearchOption;
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
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by mxt on 16-10-14.
 */
@Controller
public class SupplierController {
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private EmployeeService employeeService;

    @RequestMapping("/supplier/supplier/createSellType")
    public void createSellType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sell_type_name = request.getParameter("sell_type_name");
        SellType sellType = new SellType();
        sellType.setSell_type_name(sell_type_name);
        supplierService.createSellType(sellType);
        ResponseUtil.sendSuccessResponse(request, response, sellType);
    }

    @RequestMapping("/supplier/supplier/deleteSellType")
    public void deleteSellType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int sell_type_id = Integer.parseInt(request.getParameter("sell_type_id"));
        supplierService.deleteSellType(sell_type_id);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    @RequestMapping("/supplier/supplier/sellTypeList")
    public void sellTypeList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseUtil.sendSuccessResponse(request, response, supplierService.sellTypeList());
    }
    /**
     * 添加供应商
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/supplier/supplier/createSupplier","/supplier/supplier/updateSupplier"})
    public void supplierCreate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Supplier supplier = Request2ModelUtils.covert(Supplier.class, request);
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        supplier.setRecord_employee_name(employee.getEmployee_name());
        supplier.setRecord_employee_code(employee.getEmployee_code());
        if (supplier.getSupplier_id() == null) {
            Supplier supplier1 = supplierService.getSupplierByName(supplier.getSupplier_name());
            if (supplier1 != null) {
                ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "供应商重复"));
                return;
            }
            supplierService.createSupplier(supplier);
        }else {
            supplier.setEdit_employee_code(employee.getEmployee_code());
            supplier.setEdit_employee_name(employee.getEmployee_name());
            supplier.setEdit_date(DateUtil.currentDate());
            supplierService.updateSupplier(supplier);
        }
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 详情
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/supplier/supplierDetail")
    public void supplierDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int supplier_id = Integer.parseInt(request.getParameter("supplier_id"));
        SupplierExtend supplierExtend = supplierService.getSupplierById(supplier_id);
        ResponseUtil.sendSuccessResponse(request, response, supplierExtend);
    }

    /**
     * 删除
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/supplier/deleteSupplier")
    public void supplierDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int supplier_id = Integer.parseInt(request.getParameter("supplier_id"));
        supplierService.deleteSupplier(supplier_id);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 供应商列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/supplier/supplier/supplierList", "/supplier/commodity/supplierList", "/supplier/series/supplierList",
            "/order/purchase/supplierList", "/order/order/supplierList", "/order/inventory/supplierList",
            "/order/settlement/supplierList", "/order/inventoryIn/supplierList"})
    public void supplierList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        SupplierSearchOption supplierSearchOption = Request2ModelUtils.covert(SupplierSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(supplierSearchOption);
        if (employee.getPosition_id() == 0) {
            supplierSearchOption.setContacts_code(employee.getEmployee_code());
        }
        List<SupplierExtend> list = supplierService.supplierList(supplierSearchOption);
        Integer count = supplierService.countSupplier(supplierSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, supplierSearchOption);
    }

    /**
     * 添加系列
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/supplier/series/createSeries", "/supplier/series/updateSeries"})
    public void supplierSeries(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SupplierSeries supplierSeries = Request2ModelUtils.covert(SupplierSeries.class, request);
        if (supplierSeries.getSeries_status() == null) {
            supplierSeries.setSeries_status(1);
        }
        if (supplierSeries.getSupplier_series_id() != null) {
            supplierService.updateSeries(supplierSeries);
        }else {
            supplierService.createSeries(supplierSeries);
        }
        ResponseUtil.sendSuccessResponse(request, response, supplierSeries);
    }

    /**
     * 系列列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/supplier/series/seriesList","/supplier/commodity/seriesList"})
    public void seriesList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SupplierSearchOption supplierSearchOption = Request2ModelUtils.covert(SupplierSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(supplierSearchOption);
        List<SupplierSeriesExtend> list = supplierService.seriesList(supplierSearchOption);
        Integer count = supplierService.countSeries(supplierSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, supplierSearchOption);
    }

    /**
     * 供应商导出
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/supplier/supplierExport")
    public void supplierExport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            SupplierSearchOption supplierSearchOption = Request2ModelUtils.covert(SupplierSearchOption.class, request);
            OutputStream outputStream = response.getOutputStream();
            response.reset();
            response.setHeader("Content-Type", "application/msexcel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("供应商导出").getBytes("utf-8"),"iso8859-1") + ".xls");
            response.setHeader("Cache-Control", "max-age=0");
            WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
            WritableSheet sheet = workbook.createSheet("sheet1", 0);
            WritableFont normalFont = new WritableFont(WritableFont.ARIAL, 10);
            WritableFont boldFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);

            WritableCellFormat wcf_left = new WritableCellFormat(normalFont);
            wcf_left.setBorder(Border.NONE, BorderLineStyle.THIN); // 线条
            wcf_left.setVerticalAlignment(VerticalAlignment.CENTRE); // 文字垂直对齐
            wcf_left.setAlignment(Alignment.LEFT); // 文字水平对齐
            wcf_left.setWrap(false); // 文字是否换行
            WritableCellFormat header_format = new WritableCellFormat(boldFont);

            List<SupplierExtend> list = supplierService.supplierList(supplierSearchOption);
            int row = 0;
            sheet.addCell(new Label(0, row, "供应商名称", header_format));//
            sheet.addCell(new Label(1, row, "供应商地址", header_format));//
            sheet.addCell(new Label(2, row, "省", header_format));//
            sheet.addCell(new Label(3, row, "市", header_format));//
            sheet.addCell(new Label(4, row, "区", header_format));//
            sheet.addCell(new Label(5, row, "联系人", header_format));//
            sheet.addCell(new Label(6, row, "联系人电话", header_format));//
            sheet.addCell(new Label(7, row, "联系人地址", header_format));//
            sheet.addCell(new Label(8, row, "进销比", header_format));//
            sheet.addCell(new Label(9, row, "供应商状态（1启用，0禁用）", header_format));//
            sheet.addCell(new Label(10, row, "经销方式", header_format));//
            for (int i = 0; i < list.size(); i++) {
                SupplierExtend supplier = list.get(i);
                row++;
                sheet.addCell(new Label(0, row, supplier.getSupplier_name(), header_format));//
                if (supplier.getAddress() != null) {
                    sheet.addCell(new Label(1, row, supplier.getAddress(), header_format));//
                }
                if (supplier.getProvince() != null && supplier.getProvince().getProvince_name() != null) {
                    sheet.addCell(new Label(2, row, supplier.getProvince().getProvince_name(), header_format));//
                }
                if (supplier.getCity() != null && supplier.getCity().getCity_name() != null) {
                    sheet.addCell(new Label(3, row, supplier.getCity().getCity_name(), header_format));//
                }
                if (supplier.getDistrict() != null && supplier.getDistrict().getDistrict_name() != null) {
                    sheet.addCell(new Label(4, row, supplier.getDistrict().getDistrict_name(), header_format));//
                }
                if (supplier.getContacts() != null) {
                    sheet.addCell(new Label(5, row, supplier.getContacts(), header_format));//
                }
                if (supplier.getContacts_phone_number() != null) {
                    sheet.addCell(new Label(6, row, supplier.getContacts_phone_number(), header_format));//
                }
                if (supplier.getContacts_address() != null) {
                    sheet.addCell(new Label(7, row, supplier.getContacts_address(), header_format));//
                }
                if (supplier.getSupplier_rate() != null) {
                    sheet.addCell(new Number(8, row, supplier.getSupplier_rate(), header_format));//
                }
                if (supplier.getSupplier_status() != null) {
                    sheet.addCell(new Number(9, row, supplier.getSupplier_status(), header_format));//
                }
                if (supplier.getSellType() != null && supplier.getSellType().getSell_type_name() != null) {
                    sheet.addCell(new Label(10, row, supplier.getSellType().getSell_type_name(), header_format));//
                }
            }

            ResponseUtil.exportResponse(request, response, workbook);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 供应商导入
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/supplier/supplierImport")
    public void supplierImport(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
                        if (supplierImport(file, employee)) {
                            ResponseUtil.sendSuccessResponse(request, response);
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

    private boolean supplierImport(MultipartFile file, Employee employee) {
        try {
            Workbook workbook = Workbook.getWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheet(0);
            int rows = sheet.getRows();
            List<Object[]> list = new ArrayList<>();
            for (int i = 1; i < rows; i++) {
                Cell[] cell = sheet.getRow(i);
                String supplier_name = null;
                String address = null;
                String province = null;
                String city = null;
                String district = null;
                String contacts = null;
                String contacts_phone_number = null;
                String contacts_address = null;
                String supplier_rate = null;
                Integer supplier_status = 1;
                String sell_type_name = null;
                if (cell.length > 0) {
                    supplier_name = cell[0].getContents().trim();
                }
                if (cell.length > 1) {
                    address = cell[1].getContents().trim();
                }
                if (cell.length > 2) {
                    province = cell[2].getContents().trim();
                }
                if (cell.length > 3) {
                    city = cell[3].getContents().trim();
                }
                if (cell.length > 4) {
                    district = cell[4].getContents().trim();
                }
                if (cell.length > 5) {
                    contacts = cell[5].getContents().trim();
                }
                if (cell.length > 6) {
                    contacts_phone_number = cell[6].getContents().trim();
                }
                if (cell.length > 7) {
                    contacts_address = cell[7].getContents().trim();
                }
                if (cell.length > 8){
                    supplier_rate = cell[8].getContents().trim();
                }
                if (cell.length > 9) {
                    try {
                        supplier_status = Integer.parseInt(cell[9].getContents().trim());
                    }catch (Exception e) {

                    }
                }
                if (cell.length > 10) {
                    sell_type_name = cell[10].getContents().trim();
                }

                SupplierExtend supplierExtend = new SupplierExtend();
                supplierExtend.setSupplier_name(supplier_name);
                supplierExtend.setAddress(address);
                supplierExtend.setContacts(contacts);
                supplierExtend.setContacts_phone_number(contacts_phone_number);
                supplierExtend.setContacts_address(contacts_address);
                if (supplier_rate != null && !"".equals(supplier_rate)) {
                    supplierExtend.setSupplier_rate(Integer.parseInt(supplier_rate));
                }
                supplierExtend.setSupplier_status(supplier_status);
                supplierExtend.setRecord_employee_code(employee.getEmployee_code());
                supplierExtend.setRecord_employee_name(employee.getEmployee_name());
                Object[] o = {supplierExtend, province, city, district, sell_type_name};
                list.add(o);
                supplierService.supplierImport(list);
            }
            return true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 委托人列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/supplier/supplierContacts")
    public void supplierContacts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SupplierSearchOption supplierSearchOption = Request2ModelUtils.covert(SupplierSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(supplierSearchOption);
        List<String> list = supplierService.supplierContacts(supplierSearchOption);
        Integer count = supplierService.countSupplierContracts(supplierSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, supplierSearchOption);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/supplier/supplier/contactsInit")
    public void contacts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<SupplierExtend> list = supplierService.supplierList(null);
        for (int i = 0; i < list.size(); i++) {
            try {
                SupplierExtend supplierExtend = list.get(i);
                String contacts = supplierExtend.getContacts();
                Employee supplierContacts = supplierService.createSupplierContacts(contacts);
                if (supplierContacts != null) {
                    supplierExtend.setContacts_code(supplierContacts.getEmployee_code());
                }
                supplierService.updateSupplier(supplierExtend);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ResponseUtil.sendSuccessResponse(request, response);
    }
}
