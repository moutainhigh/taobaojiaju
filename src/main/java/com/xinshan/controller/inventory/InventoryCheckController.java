package com.xinshan.controller.inventory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.xinshan.components.commodity.CommodityComponent;
import com.xinshan.model.Employee;
import com.xinshan.model.Inventory;
import com.xinshan.model.InventoryAdjust;
import com.xinshan.model.extend.inventory.InventoryExtend;
import com.xinshan.pojo.inventory.InventoryCheckSearchOption;
import com.xinshan.service.InventoryCheckService;
import com.xinshan.service.InventoryService;
import com.xinshan.utils.*;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 盘点
 * Created by mxt on 17-5-10.
 */
@Controller
public class InventoryCheckController {
    @Autowired
    private InventoryCheckService inventoryCheckService;
    @Autowired
    private InventoryService inventoryService;

    /**
     * 盘点导入
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/inventory/check/inventoryImport")
    public void inventoryCheckImport(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
                        if (inventoryImport(file, employee)) {
                            ResponseUtil.sendSuccessResponse(request, response);
                            UploadUtil.saveFile(file, filename, employee, request.getRequestURI());
                        }else {
                            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "导入失败"));
                        }
                    }else {
                        ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "需要导入xls文件"));
                    }
                }
            }
        }
    }

    private boolean inventoryImport(MultipartFile file, Employee employee) {
        try {
            Workbook workbook = Workbook.getWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheet(0);
            int rows = sheet.getRows();
            List<Object[]> list = new ArrayList<>();
            for (int i = 1; i < rows; i++) {
                Cell[] cell = sheet.getRow(i);
                String commodity_code = cell[1].getContents().trim();
                int commodity_id = CommodityComponent.getCommodityByCode(commodity_code).getCommodity_id();
                int sample = 0;
                int return_commodity = 0;
                String sampleStr = cell[2].getContents().trim();
                if (sampleStr.equals("是")) {
                    sample = 1;
                }
                String return_commodityStr = cell[3].getContents().trim();
                if (return_commodityStr.equals("是")) {
                    return_commodity = 1;
                }
                String storeName = cell[9].getContents().trim();
                int commodity_store_id = inventoryService.getStoreByName(storeName).getCommodity_store_id();
                int commodity_num = 0, inventory_num = 0;
                if (cell.length > 9) {
                    commodity_num = Integer.parseInt(cell[10].getContents().trim());
                }
                if (cell.length > 10) {
                    inventory_num = Integer.parseInt(cell[11].getContents().trim());
                }
                Object[] o = new Object[]{commodity_id, sample, return_commodity, commodity_store_id, commodity_num, inventory_num};
                list.add(o);
            }
            inventoryCheckService.inventoryImport(list, employee);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/inventory/check/inventoryList")
    public void inventoryList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        InventoryCheckSearchOption inventoryCheckSearchOption = Request2ModelUtils.covert(InventoryCheckSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(inventoryCheckSearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(inventoryCheckSearchOption);
        List<InventoryExtend> list = inventoryCheckService.inventoryList(inventoryCheckSearchOption);
        Integer count = inventoryCheckService.countInventory(inventoryCheckSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, inventoryCheckSearchOption);
    }

    /**
     * 添加盘点
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/inventory/check/createInventory")
    public void createInventory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String s = RequestUtils.getRequestUtils().postData(request);
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        JSONArray jsonArray = JSON.parseArray(s);
        List<Inventory> list = inventoryCheckService.createInventory(jsonArray, employee);
        ResponseUtil.sendSuccessResponse(request, response, list, s);
    }

    /**
     * 库存调整
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/inventory/check/createInventoryAdjust")
    public void createAdjust(HttpServletRequest request, HttpServletResponse response) throws IOException {
        InventoryAdjust inventoryAdjust = Request2ModelUtils.covert(InventoryAdjust.class, request);
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        InventoryExtend inventoryExtend = inventoryCheckService.getInventoryById(inventoryAdjust.getInventory_id());
        if (inventoryExtend.getInventory_adjust_status() == 1) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x00014", "已调整"));
            return;
        }
        inventoryCheckService.createInventoryAdjust(inventoryAdjust, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }
}
