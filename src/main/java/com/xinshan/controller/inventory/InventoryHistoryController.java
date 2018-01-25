package com.xinshan.controller.inventory;

import com.xinshan.components.order.OrderComponents;
import com.xinshan.model.*;
import com.xinshan.model.extend.inventory.InventoryHistoryDetailExtend;
import com.xinshan.model.extend.inventory.InventoryHistoryExtend;
import com.xinshan.pojo.inventory.InventoryHistorySearchOption;
import com.xinshan.pojo.inventory.InventorySearchOption;
import com.xinshan.service.InventoryHistoryService;
import com.xinshan.utils.*;
import com.xinshan.utils.constant.inventory.InventoryHistoryConstant;
import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mxt on 17-4-26.
 */
@Controller
public class InventoryHistoryController {
    @Autowired
    private InventoryHistoryService inventoryHistoryService;

    /**
     * 出入库列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/inventoryIn/inventoryHistoryList")
    public void inventoryHistoryList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        InventoryHistorySearchOption inventoryHistorySearchOption = Request2ModelUtils.covert(InventoryHistorySearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(inventoryHistorySearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(inventoryHistorySearchOption);
        List<InventoryHistoryExtend> list = inventoryHistoryService.inventoryHistories(inventoryHistorySearchOption);
        Integer count = inventoryHistoryService.countInventoryHistory(inventoryHistorySearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, inventoryHistorySearchOption);
    }

    /**
     * 初始化出入库单号
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/inventoryHistory/inventoryHistoryCodeInit")
    public void inventoryHistoryCodeInit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        inventoryHistoryService.inventoryHistoryCodeInit();
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/order/inventory/inventoryHistoryDetails", "/order/inventory/inventoryHistoryDetails1"})
    public void inventoryHistory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        InventorySearchOption inventorySearchOption = Request2ModelUtils.covert(InventorySearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(inventorySearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(inventorySearchOption);
        List<InventoryHistoryDetailExtend> list = inventoryHistoryService.inventoryHistoryDetailExtends(inventorySearchOption);
        Integer count = inventoryHistoryService.countInventoryHistoryDetailExtends(inventorySearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, inventorySearchOption);
    }

    /**
     * 报表导出
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/inventory/outHistoryExport")
    public void inventoryHistoryExport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        InventorySearchOption inventorySearchOption = Request2ModelUtils.covert(InventorySearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(inventorySearchOption);
        List<InventoryHistoryDetailExtend> list = inventoryHistoryService.inventoryHistoryDetailExtends(inventorySearchOption);
        try {
            OutputStream outputStream = response.getOutputStream();
            response.reset();
            response.setHeader("Content-Type", "application/msexcel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("出库报表导出").getBytes("utf-8"),"iso8859-1") + ".xls");
            response.setHeader("Cache-Control", "max-age=0");
            WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
            WritableSheet sheet = workbook.createSheet("sheet1", 0);

            //1正常；0逾期；-1冻结
            int row = 0;
            Counter col = new Counter();
            sheet.addCell(new Label(col.getN(), row, "出库单号"));//
            sheet.addCell(new Label(col.getN(), row, "出库类型"));//
            sheet.addCell(new Label(col.getN(), row, "货号"));//
            sheet.addCell(new Label(col.getN(), row, "名称"));//
            sheet.addCell(new Label(col.getN(), row, "供应商"));//
            sheet.addCell(new Label(col.getN(), row, "数量"));//
            sheet.addCell(new Label(col.getN(), row, "订单号"));//
            sheet.addCell(new Label(col.getN(), row, "客户姓名"));//
            sheet.addCell(new Label(col.getN(), row, "客户电话"));//
            sheet.addCell(new Label(col.getN(), row, "销售单价"));//
            sheet.addCell(new Label(col.getN(), row, "改版费"));//
            sheet.addCell(new Label(col.getN(), row, "销售总价"));//
            sheet.addCell(new Label(col.getN(), row, "进货价"));//
            sheet.addCell(new Label(col.getN(), row, "进货总价"));//
            sheet.addCell(new Label(col.getN(), row, "存放场馆"));//
            sheet.addCell(new Label(col.getN(), row, "出库日期"));//
            row++;
            for (int i = 0; i < list.size(); i++) {
                col.reset();
                InventoryHistoryDetailExtend inventoryHistoryDetailExtend = list.get(i);
                sheet.addCell(new Label(col.getN(), row, inventoryHistoryDetailExtend.getInventoryHistory().getInventory_history_code()));//
                Commodity commodity = inventoryHistoryDetailExtend.getCommodity();
                Integer inventory_type = inventoryHistoryDetailExtend.getInventoryHistory().getInventory_type();
                sheet.addCell(new Label(col.getN(), row, inventoryType(inventory_type)));//
                sheet.addCell(new Label(col.getN(), row, commodity.getCommodity_code()));//
                sheet.addCell(new Label(col.getN(), row, commodity.getCommodity_name()));//
                Supplier supplier = inventoryHistoryDetailExtend.getSupplier();
                if (supplier != null && supplier.getSupplier_name()!= null) {
                    sheet.addCell(new Label(col.getN(), row, supplier.getSupplier_name()));//
                }else {
                    sheet.addCell(new Label(col.getN(), row, ""));//
                }
                sheet.addCell(new Number(col.getN(), row, inventoryHistoryDetailExtend.getInventory_history_num()));//
                Order order = inventoryHistoryDetailExtend.getOrder();
                if (order != null && order.getOrder_code() != null) {
                    sheet.addCell(new Label(col.getN(), row, order.getOrder_code()));//
                    sheet.addCell(new Label(col.getN(), row, order.getCustomer_name()));//
                    sheet.addCell(new Label(col.getN(), row, order.getCustomer_phone_number()));//
                }else {
                    //order = OrderComponents.getOrderById(inventoryHistoryDetailExtend.getInventoryHistory().getOrder_id());
                    //if (order != null && order.getOrder_code() != null) {
                    //    sheet.addCell(new Label(col.getN(), row, order.getOrder_code(), header_format));//
                    //}else {
                        sheet.addCell(new Label(col.getN(), row, ""));//
                        sheet.addCell(new Label(col.getN(), row, ""));//
                        sheet.addCell(new Label(col.getN(), row, ""));//
                    //}
                }

                OrderCommodity orderCommodity = inventoryHistoryDetailExtend.getOrderCommodity();
                BigDecimal bargain_price = commodity.getSell_price();
                BigDecimal revision_fee = new BigDecimal("0");
                if (orderCommodity != null) {
                    bargain_price = orderCommodity.getBargain_price();
                    if (orderCommodity.getRevision_fee() != null) {
                        revision_fee = orderCommodity.getRevision_fee();
                    }
                }

                BigDecimal commodity_total_price = bargain_price.add(revision_fee).multiply(new BigDecimal(inventoryHistoryDetailExtend.getInventory_history_num()));
                sheet.addCell(new Number(col.getN(), row, bargain_price.doubleValue()));//
                sheet.addCell(new Number(col.getN(), row, revision_fee.doubleValue()));//
                sheet.addCell(new Number(col.getN(), row, commodity_total_price.doubleValue()));//

                PurchaseCommodity purchaseCommodity = inventoryHistoryDetailExtend.getPurchaseCommodity();
                BigDecimal purchase_unit_price = new BigDecimal("0");
                if (purchaseCommodity != null && purchaseCommodity.getPurchase_unit_price() != null) {
                    purchase_unit_price = purchaseCommodity.getPurchase_unit_price();
                }else {
                    purchase_unit_price = commodity.getPurchase_price();
                }
                sheet.addCell(new Number(col.getN(), row, purchase_unit_price.doubleValue()));//
                sheet.addCell(new Number(col.getN(), row, purchase_unit_price.multiply(new BigDecimal(inventoryHistoryDetailExtend.getInventory_history_num())).doubleValue()));//

                CommodityStore commodityStore = inventoryHistoryDetailExtend.getCommodityStore();
                if (commodityStore != null) {
                    sheet.addCell(new Label(col.getN(), row, commodityStore.getStore_name()));//
                }else {
                    sheet.addCell(new Label(col.getN(), row, ""));//
                }
                sheet.addCell(new Label(col.getN(), row, DateUtil.format(inventoryHistoryDetailExtend.getInventoryHistory().getInventory_date(), "yyyy-MM-dd")));//
                row++;
            }
            ResponseUtil.exportResponse(request, response, workbook);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String inventoryType(int inventory_type) {
        String s = "";
        switch (inventory_type) {
            case InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_IN:
                s = "订单入库";
                break;
            case InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_OUT:
                s = "订单出库";
                break;
            case InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_RETURN_IN:
                s = "退货入库";
                break;
            case InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_SAMPLE_OUT:
                s = "下样出库";
                break;
            case InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_SAMPLE_IN:
                s = "上样入库";
                break;
            case InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_GIFT_OUT:
                s = "赠品出库";
                break;
            case InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_GIFT_RETURN:
                s = "退还赠品入库";
                break;
            case InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_MOVE_IN:
                s = "调拨入库";
                break;
            case InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_MOVE_OUT:
                s = "调拨出库";
                break;
            default:
                break;
        }
        return s;
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/inventory/inventoryHistoryStatics")
    public void inventoryHistoryStatics(HttpServletRequest request, HttpServletResponse response) throws IOException {
        InventorySearchOption inventorySearchOption = Request2ModelUtils.covert(InventorySearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(inventorySearchOption);
        List<InventoryHistoryDetailExtend> list = inventoryHistoryService.inventoryHistoryDetailExtends(inventorySearchOption);
        int commodityInNum = 0;
        int commodityOutNum = 0;
        BigDecimal purchaseAmount = new BigDecimal("0");
        BigDecimal sellAmount = new BigDecimal("0");

        for (int i = 0; i < list.size(); i++) {
            InventoryHistoryDetailExtend inventoryHistoryDetailExtend = list.get(i);
            Integer inventory_history_num = inventoryHistoryDetailExtend.getInventory_history_num();
            InventoryHistory inventoryHistory = inventoryHistoryDetailExtend.getInventoryHistory();
            Integer inventory_history_in_out = inventoryHistory.getInventory_history_in_out();
            if (inventory_history_in_out.equals(InventoryHistoryConstant.INVENTORY_HISTORY_IN)) {
                commodityInNum += inventory_history_num;
            }else if (inventory_history_in_out.equals(InventoryHistoryConstant.INVENTORY_HISTORY_OUT)) {
                commodityOutNum += inventory_history_num;
            }
            Commodity commodity = inventoryHistoryDetailExtend.getCommodity();
            OrderCommodity orderCommodity = inventoryHistoryDetailExtend.getOrderCommodity();
            BigDecimal bargain_price = commodity.getSell_price();
            BigDecimal revision_fee = new BigDecimal("0");
            if (orderCommodity != null) {
                bargain_price = orderCommodity.getBargain_price();
                if (orderCommodity.getRevision_fee() != null) {
                    revision_fee = orderCommodity.getRevision_fee();
                }
            }
            BigDecimal commodity_total_price = bargain_price.add(revision_fee).multiply(new BigDecimal(inventory_history_num));
            sellAmount = sellAmount.add(commodity_total_price);

            PurchaseCommodity purchaseCommodity = inventoryHistoryDetailExtend.getPurchaseCommodity();
            BigDecimal purchase_unit_price = new BigDecimal("0");
            if (purchaseCommodity != null && purchaseCommodity.getPurchase_unit_price() != null) {
                purchase_unit_price = purchaseCommodity.getPurchase_unit_price();
            }else {
                purchase_unit_price = commodity.getPurchase_price();
            }
            purchase_unit_price = purchase_unit_price.multiply(new BigDecimal(inventory_history_num));

            purchaseAmount = purchaseAmount.add(purchase_unit_price);
        }

        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("inventoryInNum", commodityInNum);
        hashMap.put("commodityOutNum", commodityOutNum);
        hashMap.put("purchaseAmount", purchaseAmount);
        hashMap.put("sellAmount", sellAmount);
        ResponseUtil.sendSuccessResponse(request, response, hashMap);
    }

    /**
     * 库存流水导出
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/inventory/detailExport")
    public void inventoryHistoryReportExport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        InventorySearchOption inventorySearchOption = Request2ModelUtils.covert(InventorySearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(inventorySearchOption);
        long a = System.currentTimeMillis();
        List<Map> list = inventoryHistoryService.historyReportExport(inventorySearchOption);
        long b = System.currentTimeMillis();
        System.out.println("----------------------------" + (b-a) +"ms");
        try {
            OutputStream outputStream = response.getOutputStream();
            response.reset();
            response.setHeader("Content-Type", "application/msexcel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("库存流水导出").getBytes("utf-8"),"iso8859-1") + ".xls");
            response.setHeader("Cache-Control", "max-age=0");
            WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
            WritableSheet sheet = workbook.createSheet("sheet1", 0);

            //1正常；0逾期；-1冻结
            int row = 0;
            Counter col = new Counter();
            sheet.addCell(new Label(col.getN(), row, "出库\\入库"));//
            sheet.addCell(new Label(col.getN(), row, "入出库类型"));//
            sheet.addCell(new Label(col.getN(), row, "货号"));//
            sheet.addCell(new Label(col.getN(), row, "名称"));//
            sheet.addCell(new Label(col.getN(), row, "售价"));//
            sheet.addCell(new Label(col.getN(), row, "供应商"));//
            sheet.addCell(new Label(col.getN(), row, "数量"));//
            sheet.addCell(new Label(col.getN(), row, "剩余库存"));//
            sheet.addCell(new Label(col.getN(), row, "出入库场馆"));//
            sheet.addCell(new Label(col.getN(), row, "操作日期"));//
            sheet.addCell(new Label(col.getN(), row, "操作人"));//
            for (int i = 0; i < list.size(); i++) {
                col.reset();
                row++;
                Map map = list.get(i);
                String inOut = "";
                if (map.get("inventory_history_in_out").toString().equals("1")) {
                    inOut = "入库";
                }else {
                    inOut = "出库";
                }
                sheet.addCell(new Label(col.getN(), row, inOut));//
                sheet.addCell(new Label(col.getN(), row, inventoryType(Integer.parseInt(map.get("inventory_type").toString()))));//
                sheet.addCell(new Label(col.getN(), row, map.get("commodity_code").toString()));//
                sheet.addCell(new Label(col.getN(), row, map.get("commodity_name").toString()));//
                BigDecimal sellPrice = new BigDecimal("0");
                if (map.get("bargain_price") != null) {
                    sellPrice = new BigDecimal(map.get("bargain_price").toString());
                    if (map.get("revision_fee") != null) {
                        sellPrice = sellPrice.add(new BigDecimal(map.get("revision_fee").toString()));
                    }
                }else {
                    sellPrice = new BigDecimal(map.get("sell_price").toString());
                }

                sheet.addCell(new Number(col.getN(), row, sellPrice.doubleValue()));//
                sheet.addCell(new Label(col.getN(), row, map.get("supplier_name").toString()));//
                sheet.addCell(new Number(col.getN(), row, Integer.parseInt(map.get("inventory_history_num").toString())));//
                if (map.get("commodity_num") == null) {
                    sheet.addCell(new Label(col.getN(), row, ""));//
                }else {
                    sheet.addCell(new Number(col.getN(), row, Integer.parseInt(map.get("commodity_num").toString())));//
                }
                sheet.addCell(new Label(col.getN(), row, map.get("store_name").toString()));//
                sheet.addCell(new Label(col.getN(), row, map.get("inventory_date").toString()));//
                sheet.addCell(new Label(col.getN(), row, map.get("inventory_employee_name").toString()));//
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
    @RequestMapping("/order/inventory/detailExport1")
    public void inventoryHistoryInExport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        InventorySearchOption inventorySearchOption = Request2ModelUtils.covert(InventorySearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(inventorySearchOption);
        List<Map> list = inventoryHistoryService.historyReportExport(inventorySearchOption);
        try {
            OutputStream outputStream = response.getOutputStream();
            response.reset();
            response.setHeader("Content-Type", "application/msexcel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("入库报表导出").getBytes("utf-8"),"iso8859-1") + ".xls");
            response.setHeader("Cache-Control", "max-age=0");
            WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
            WritableSheet sheet = workbook.createSheet("sheet1", 0);

            //1正常；0逾期；-1冻结
            int row = 0;
            Counter col = new Counter();
            sheet.addCell(new Label(col.getN(), row, "入库类型"));//
            sheet.addCell(new Label(col.getN(), row, "货号"));//
            sheet.addCell(new Label(col.getN(), row, "名称"));//
            sheet.addCell(new Label(col.getN(), row, "客户姓名"));//
            sheet.addCell(new Label(col.getN(), row, "客户电话"));//
            sheet.addCell(new Label(col.getN(), row, "供应商"));//
            sheet.addCell(new Label(col.getN(), row, "数量"));//
            sheet.addCell(new Label(col.getN(), row, "运费"));//
            sheet.addCell(new Label(col.getN(), row, "采购单号"));//
            sheet.addCell(new Label(col.getN(), row, "采购单价"));//
            sheet.addCell(new Label(col.getN(), row, "采购总价"));//
            sheet.addCell(new Label(col.getN(), row, "存放场馆"));//
            sheet.addCell(new Label(col.getN(), row, "入库日期"));//
            sheet.addCell(new Label(col.getN(), row, "操作人"));//
            for (int i = 0; i < list.size(); i++) {
                col.reset();
                row++;
                Map map = list.get(i);
                sheet.addCell(new Label(col.getN(), row, inventoryType(Integer.parseInt(map.get("inventory_type").toString()))));//
                sheet.addCell(new Label(col.getN(), row, map.get("commodity_code").toString()));//
                sheet.addCell(new Label(col.getN(), row, map.get("commodity_name").toString()));//
                sheet.addCell(new Label(col.getN(), row, map.get("customer_name") == null ? "" : map.get("customer_name").toString()));//
                sheet.addCell(new Label(col.getN(), row, map.get("customer_phone_number") == null ? "" : map.get("customer_phone_number").toString()));//
                sheet.addCell(new Label(col.getN(), row, map.get("supplier_name") == null ? "" : map.get("supplier_name").toString()));//
                int num = Integer.parseInt(map.get("inventory_history_num").toString());
                sheet.addCell(new Number(col.getN(), row, num));//
                if (map.get("inventory_in_commodity_freight") != null) {
                    sheet.addCell(new Number(col.getN(), row, new BigDecimal(map.get("inventory_in_commodity_freight").toString()).doubleValue()));//
                }else {
                    sheet.addCell(new Number(col.getN(), row, 0));//
                }
                sheet.addCell(new Label(col.getN(), row, map.get("purchase_code") == null ? "" : map.get("purchase_code").toString()));//
                BigDecimal purchase_price = new BigDecimal(map.get("sell_price").toString());
                if (map.get("purchase_unit_cost_price") != null) {
                    purchase_price = new BigDecimal(map.get("purchase_unit_cost_price").toString());
                }
                sheet.addCell(new Number(col.getN(), row, purchase_price.doubleValue()));//
                sheet.addCell(new Number(col.getN(), row, purchase_price.multiply(new BigDecimal(num)).doubleValue()));//
                sheet.addCell(new Label(col.getN(), row, map.get("store_name").toString()));//
                sheet.addCell(new Label(col.getN(), row, map.get("inventory_date").toString()));//
                sheet.addCell(new Label(col.getN(), row, map.get("inventory_employee_name").toString()));//
            }
            ResponseUtil.exportResponse(request, response, workbook);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
