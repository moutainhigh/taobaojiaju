package com.xinshan.controller.purchase;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.commodity.CommodityComponent;
import com.xinshan.model.*;
import com.xinshan.model.extend.activity.ActivityCommodityExtend;
import com.xinshan.model.extend.order.OrderCommodityExtend;
import com.xinshan.model.extend.order.OrderExtend;
import com.xinshan.model.extend.purchase.PurchaseCommodityExtend;
import com.xinshan.model.extend.purchase.PurchaseExtend;
import com.xinshan.model.extend.purchase.PurchaseInCommodity;
import com.xinshan.model.extend.purchase.PurchaseReports;
import com.xinshan.service.*;
import com.xinshan.utils.*;
import com.xinshan.pojo.commodity.CommoditySearchOption;
import com.xinshan.pojo.purchase.PurchaseSearchOption;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 采购记录
 * Created by mxt on 16-11-2.
 */
@Controller
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private InventoryInService inventoryInService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CommodityService commodityService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private SettlementService settlementService;
    @Autowired
    private ActivityService activityService;
    /**
     * 采购单列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/order/purchase/purchaseList","/order/inventoryIn/purchaseList"})
    public void purchaseList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PurchaseSearchOption purchaseSearchOption = Request2ModelUtils.covert(PurchaseSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(purchaseSearchOption);
        List<Integer> purchaseIds = purchaseService.purchaseIds(purchaseSearchOption);
        Integer count = purchaseService.countPurchase(purchaseSearchOption);
        purchaseSearchOption.setPurchaseIds(purchaseIds);
        List<PurchaseExtend> list = new ArrayList<>();
        if (purchaseIds.size() > 0) {
            list = purchaseService.purchaseList(purchaseSearchOption);
        }
        ResponseUtil.sendListResponse(request, response, list, count, purchaseSearchOption);
    }

    /**
     * 添加采购单
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/purchase/createPurchase")
    public void createPurchase(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String postData = RequestUtils.getRequestUtils().postData(request);
        JSONObject jsonObject = JSONObject.parseObject(postData);
        purchaseService.createPurchase(jsonObject, null, null, employee);
        ResponseUtil.sendSuccessResponse(request, response, postData);
    }

    /**
     * 确认采购
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/purchase/confirmPurchase")
    public void confirmPurchase(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = (Employee) request.getSession().getAttribute("employee");
        int purchase_commodity_id = Integer.parseInt(request.getParameter("purchase_commodity_id"));
        int purchase_num = Integer.parseInt(request.getParameter("purchase_num"));
        BigDecimal purchase_unit_price = null;
        if (request.getParameter("purchase_unit_price") != null && !request.getParameter("purchase_unit_price").equals("")) {
            purchase_unit_price = new BigDecimal(request.getParameter("purchase_unit_price"));
        }
        String purchase_commodity_remark = request.getParameter("purchase_commodity_remark");
        BigDecimal purchase_unit_cost_price = new BigDecimal("0");
        if (request.getParameter("purchase_unit_cost_price") != null && !"".equals(request.getParameter("purchase_unit_cost_price"))) {
            purchase_unit_cost_price = new BigDecimal(request.getParameter("purchase_unit_cost_price"));
        }

        PurchaseCommodity purchaseCommodity = purchaseService.getPurchaseCommodityById(purchase_commodity_id);
        purchaseCommodity.setPurchase_num(purchase_num);
        purchaseCommodity.setPurchase_unit_price(purchase_unit_price);
        purchaseCommodity.setPurchase_total_price(purchaseCommodity.getPurchase_unit_price().multiply(new BigDecimal(purchase_num)));
        purchaseCommodity.setPurchase_commodity_remark(purchase_commodity_remark);
        purchaseCommodity.setPurchase_commodity_status(1);
        purchaseCommodity.setConfirm_purchase_employee_code(employee.getEmployee_code());
        purchaseCommodity.setConfirm_purchase_employee_name(employee.getEmployee_name());
        purchaseCommodity.setConfirm_purchase_employee_date(DateUtil.currentDate());
        purchaseCommodity.setPurchase_unit_cost_price(purchase_unit_cost_price);
        if (purchaseCommodity.getPurchase_unit_cost_price() != null) {
            purchaseCommodity.setPurchase_cost_price(purchaseCommodity.getPurchase_unit_cost_price().multiply(new BigDecimal(purchaseCommodity.getPurchase_num())));
        }else {
            purchaseCommodity.setPurchase_cost_price(new BigDecimal("0"));
        }
        purchaseService.updatePurchaseCommodity(purchaseCommodity);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 采购单审核，开始采购
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/purchase/checkPurchase")
    public void checkPurchase(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int purchase_commodity_id = Integer.parseInt(request.getParameter("purchase_commodity_id"));
        PurchaseCommodity purchaseCommodity = purchaseService.getPurchaseCommodityById(purchase_commodity_id);
        purchaseCommodity.setCheck_purchase_employee_code(employee.getEmployee_code());
        purchaseCommodity.setCheck_purchase_employee_date(DateUtil.currentDate());
        purchaseCommodity.setCheck_purchase_employee_name(employee.getEmployee_name());

        if (request.getParameter("purchase_num") != null) {
            purchaseCommodity.setPurchase_num(Integer.parseInt(request.getParameter("purchase_num")));
        }

        if (purchaseCommodity.getPurchase_num() == 0) {
            purchaseCommodity.setPurchase_commodity_status(5);//不许要采购
        }else {
            purchaseCommodity.setPurchase_commodity_status(2);//审核
        }

        if (request.getParameter("purchase_commodity_price") != null && !request.getParameter("purchase_commodity_price").equals("")) {
            purchaseCommodity.setPurchase_unit_price(new BigDecimal(request.getParameter("purchase_commodity_price")));
            purchaseCommodity.setPurchase_total_price(purchaseCommodity.getPurchase_unit_price().multiply(new BigDecimal(purchaseCommodity.getPurchase_num())));
        }
        if (request.getParameter("purchase_commodity_remark") != null) {
            purchaseCommodity.setPurchase_commodity_remark(request.getParameter("purchase_commodity_remark"));
        }

        if (request.getParameter("purchase_unit_cost_price") != null && !"".equals(request.getParameter("purchase_unit_cost_price"))) {
            BigDecimal purchase_unit_cost_price = new BigDecimal(request.getParameter("purchase_unit_cost_price"));
            purchaseCommodity.setPurchase_unit_cost_price(purchase_unit_cost_price);
            purchaseCommodity.setPurchase_cost_price(purchase_unit_cost_price.multiply(new BigDecimal(purchaseCommodity.getPurchase_num())));
        }else {
            purchaseCommodity.setPurchase_unit_cost_price(new BigDecimal("0"));
            purchaseCommodity.setPurchase_cost_price(new BigDecimal("0"));
        }

        purchaseService.updatePurchaseCommodity(purchaseCommodity);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 确认到货
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/purchase/confirmArrival")
    public void confirmArrival(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = (Employee) request.getSession().getAttribute("employee");
        int purchase_commodity_id = Integer.parseInt(request.getParameter("purchase_commodity_id"));
        int purchase_arrival_num = Integer.parseInt(request.getParameter("purchase_arrival_num"));
        PurchaseCommodity purchaseCommodity = purchaseService.getPurchaseCommodityById(purchase_commodity_id);
        if (purchase_arrival_num <= 0) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0015", "到货数量不能小于1"));
            return;
        }
        if (purchaseCommodity.getPurchase_num() == 0) {
            ResponseUtil.sendSuccessResponse(request, response, new ResultData("0x0014", "采购数量0"));
            return;
        }
        if (purchaseCommodity.getPurchase_arrival_num() == null) {
            purchaseCommodity.setPurchase_arrival_num(purchase_arrival_num);
        }else {
            purchaseCommodity.setPurchase_arrival_num(purchaseCommodity.getPurchase_arrival_num() + purchase_arrival_num);
        }
        if (request.getParameter("purchase_commodity_status") != null && !"".equals(request.getParameter("purchase_commodity_status"))) {
            purchaseCommodity.setPurchase_commodity_status(4);
        }else {
            //到货状态，0新添加，1确认采购，2审核，3部分到货，4全部到货
            if (purchaseCommodity.getPurchase_arrival_num() > 0 && purchaseCommodity.getPurchase_arrival_num() < purchaseCommodity.getPurchase_num()) {
                purchaseCommodity.setPurchase_commodity_status(3);
            }else if(purchaseCommodity.getPurchase_arrival_num() >= purchaseCommodity.getPurchase_num()) {
                purchaseCommodity.setPurchase_commodity_status(4);
            }
        }

        InventoryInCommodity inventoryInCommodity = inventoryInService.getInventoryInCommodityByPid(purchaseCommodity.getPurchase_commodity_id());
        if (inventoryInCommodity == null) {//采购单转化入库单
            purchaseService.confirmArrival(purchaseCommodity, employee, purchase_arrival_num);
            PurchaseExtend purchase = purchaseService.getPurchaseById(purchaseCommodity.getPurchase_id());
            inventoryInService.createInventoryIn(purchase, employee);
        }else {
            purchaseService.confirmArrival(purchaseCommodity, employee, purchase_arrival_num);
        }
        ResponseUtil.sendSuccessResponse(request, response);

        //是否需要结算广东馆商品
        PurchaseExtend purchaseExtend = purchaseService.getPurchaseById(purchaseCommodity.getPurchase_id());
        List<PurchaseCommodityExtend> purchaseCommodities = purchaseExtend.getPurchaseCommodities();
        for (int i = 0; i < purchaseCommodities.size(); i++) {
            PurchaseCommodityExtend purchaseCommodityExtend = purchaseCommodities.get(i);
            settlementService.createSettlementPurchaseArrival(purchaseCommodityExtend.getPurchase_commodity_id(), employee);
        }
    }

    /**
     * 采购单打印
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/purchase/detail")
    public void purchasePrint(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int purchase_id = Integer.parseInt(request.getParameter("purchase_id"));
        PurchaseSearchOption purchaseSearchOption = new PurchaseSearchOption();
        List<Integer> purchaseIds = new ArrayList<>();
        purchaseIds.add(purchase_id);
        purchaseSearchOption.setPurchaseIds(purchaseIds);
        List<PurchaseExtend> list = new ArrayList<>();
        if (purchaseIds.size() > 0) {
            list = purchaseService.purchaseList(purchaseSearchOption);
        }
        if (list!= null && list.size() >0) {
            PurchaseExtend purchaseExtend = list.get(0);
            purchaseExtend.setPrintEmployee(employee);
            if (purchaseExtend.getOrder_id() != null) {
                OrderExtend order = orderService.getOrderById(purchaseExtend.getOrder_id());
                purchaseExtend.setOrder(order);
                Map<Integer, BigDecimal> map = new HashMap<>();
                List<OrderCommodityExtend> orderCommodityExtends = order.getOrderCommodities();
                for (int i = 0; i < orderCommodityExtends.size(); i++) {
                    OrderCommodityExtend orderCommodity = orderCommodityExtends.get(i);
                    int commodity_id = orderCommodity.getCommodity_id();
                    BigDecimal price = orderCommodity.getCommodity_total_price();
                    map.put(commodity_id, price);
                }
                List<PurchaseCommodityExtend> purchaseCommodities = purchaseExtend.getPurchaseCommodities();
                for (int i = 0; i < purchaseCommodities.size(); i++) {
                    PurchaseCommodityExtend purchaseCommodityExtend = purchaseCommodities.get(i);
                    int commodity_id = purchaseCommodityExtend.getCommodity_id();
                    if (map.containsKey(commodity_id)) {
                        purchaseCommodityExtend.setOrder_commodity_price(map.get(commodity_id));
                    }
                }
            }

            ResponseUtil.sendSuccessResponse(request, response, purchaseExtend);
        }else {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "id不正确"));
        }
    }

    /**
     * 采购商品详情
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/purchase/purchaseCommodityDetail")
    public void purchaseCommodity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int purchase_commodity_id = Integer.parseInt(request.getParameter("purchase_commodity_id"));
        PurchaseCommodityExtend purchaseCommodityExtend = new PurchaseCommodityExtend();
        PurchaseCommodity purchaseCommodity = purchaseService.getPurchaseCommodityById(purchase_commodity_id);

        Request2ModelUtils.covertParent(purchaseCommodityExtend, purchaseCommodity);
        //response.setHeader("Content-Disposition", "attachment;filename=" + new String(("采购报表").getBytes("utf-8"), "iso8859-1") + ".xls");

        purchaseCommodityExtend.setCommodity(commodityService.getCommodityById(purchaseCommodity.getCommodity_id()));
        CommoditySearchOption commoditySearchOption = new CommoditySearchOption();
        commoditySearchOption.setCommodity_id(purchaseCommodity.getCommodity_id());
        commoditySearchOption.setCommodity_num(0);
        purchaseCommodityExtend.setCommodityNumList(inventoryService.commodityNumList(commoditySearchOption));
        purchaseCommodityExtend.setSupplier(supplierService.getSupplierById(purchaseCommodityExtend.getCommodity().getSupplier_id()));

        if (purchaseCommodity.getOrder_commodity_id() != null) {
            OrderCommodity orderCommodity = orderService.getOrderCommodity(purchaseCommodity.getOrder_commodity_id());
            purchaseCommodityExtend.setOrderCommodity(orderCommodity);
            if (orderCommodity != null && orderCommodity.getActivity_commodity_id() != null) {
                purchaseCommodityExtend.setActivityCommodity(activityService.getActivityCommodityById(orderCommodity.getActivity_commodity_id()));
            }
        }

        ResponseUtil.sendSuccessResponse(request, response, purchaseCommodityExtend);
    }

    /**
     * 不采购
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/purchase/purchaseStatus")
    public void purchaseStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int purchase_commodity_id = Integer.parseInt(request.getParameter("purchase_commodity_id"));
        String no_purchase_remark = request.getParameter("no_purchase_remark");
        PurchaseCommodity purchaseCommodity = purchaseService.getPurchaseCommodityById(purchase_commodity_id);
        if (purchaseCommodity.getPurchase_commodity_status() <= 2) {
            purchaseCommodity.setPurchase_commodity_status(5);
            purchaseCommodity.setNo_purchase_remark(no_purchase_remark);
            purchaseService.updatePurchaseCommodity(purchaseCommodity);
        }
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 采购报表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/purchase/reports")
    public void purchaseReports(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PurchaseSearchOption purchaseSearchOption = Request2ModelUtils.covert(PurchaseSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(purchaseSearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(purchaseSearchOption);
        List<PurchaseReports> list = purchaseService.purchaseReportses(purchaseSearchOption);
        Integer count = purchaseService.countPurchaseReports(purchaseSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, purchaseSearchOption);
    }

    @RequestMapping("/order/purchase/reportsStatistics")
    public void purchaseReportsStatistics(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PurchaseSearchOption purchaseSearchOption = Request2ModelUtils.covert(PurchaseSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(purchaseSearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(purchaseSearchOption);
        Map map = purchaseService.purchaseReportsStatistics(purchaseSearchOption);
        ResponseUtil.sendSuccessResponse(request, response, map);
    }

    @RequestMapping("/order/purchase/reportsExport")
    public void purchaseReportsExport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PurchaseSearchOption purchaseSearchOption = Request2ModelUtils.covert(PurchaseSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(purchaseSearchOption);
        List<PurchaseReports> list = purchaseService.purchaseReportses(purchaseSearchOption);
        OutputStream outputStream = response.getOutputStream();
        response.reset();
        response.setHeader("Content-Type", "application/msexcel");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(("采购报表").getBytes("utf-8"), "iso8859-1") + ".xls");
        response.setHeader("Cache-Control", "max-age=0");
        try {
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
            int row = 0;
            sheet.addCell(new Label(0, row, "货号", header_format));//
            sheet.addCell(new Label(1, row, "名称", header_format));//
            sheet.addCell(new Label(2, row, "供应商", header_format));//
            sheet.addCell(new Label(3, row, "采购单号", header_format));//
            sheet.addCell(new Label(4, row, "采购时间", header_format));//
            sheet.addCell(new Label(5, row, "入库单号", header_format));//
            sheet.addCell(new Label(6, row, "入库时间", header_format));//
            sheet.addCell(new Label(7, row, "订货周期", header_format));//
            sheet.addCell(new Label(8, row, "数量", header_format));//
            sheet.addCell(new Label(9, row, "采购单价", header_format));//
            sheet.addCell(new Label(10, row, "采购总价", header_format));//
            sheet.addCell(new Label(11, row, "操作员", header_format));//
            sheet.addCell(new Label(12, row, "审核人", header_format));//
            sheet.addCell(new Label(13, row, "订单号", header_format));//
            sheet.addCell(new Label(14, row, "客户", header_format));//
            sheet.addCell(new Label(15, row, "客户电话", header_format));//
            sheet.addCell(new Label(16, row, "状态", header_format));//
            for (int i = 0; i < list.size(); i++) {
                PurchaseReports purchaseReports = list.get(i);
                row++;
                sheet.addCell(new Label(0, row, purchaseReports.getCommodity().getCommodity_code(), header_format));//
                sheet.addCell(new Label(1, row, purchaseReports.getCommodity().getCommodity_name(), header_format));//
                sheet.addCell(new Label(2, row, purchaseReports.getSupplier().getSupplier_name(), header_format));//
                sheet.addCell(new Label(3, row, purchaseReports.getPurchase().getPurchase_code(), header_format));//
                sheet.addCell(new Label(4, row, purchaseReports.getConfirm_purchase_employee_date() == null? "":DateUtil.format(purchaseReports.getConfirm_purchase_employee_date(), "yyyy-MM-dd"), header_format));//
                StringBuilder inventory_in_code = new StringBuilder();
                StringBuilder confirm_in_date = new StringBuilder();
                StringBuilder purchase_period = new StringBuilder();
                List<PurchaseInCommodity> purchaseInCommodities = purchaseReports.getPurchaseInCommodities();
                for (int j = 0; j < purchaseInCommodities.size(); j++) {
                    PurchaseInCommodity purchaseInCommodity = purchaseInCommodities.get(j);
                    inventory_in_code.append(purchaseInCommodity.getInventory_in_code());
                    if (purchaseInCommodity.getConfirm_in_date() != null) {
                        confirm_in_date.append(DateUtil.format(purchaseInCommodity.getConfirm_in_date(), "yyyy-MM-dd"));
                    }else {
                        confirm_in_date.append("");
                    }
                    if (purchaseInCommodity.getPurchase_period() != null) {
                        purchase_period.append(purchaseInCommodity.getPurchase_period());
                    }else {
                        purchase_period.append("");
                    }
                    if (j < purchaseInCommodities.size() - 1) {
                        inventory_in_code.append("\t");
                        confirm_in_date.append("\t");
                        purchase_period.append("\t");
                    }
                }
                sheet.addCell(new Label(5, row, inventory_in_code.toString(), header_format));//
                sheet.addCell(new Label(6, row, confirm_in_date.toString(), header_format));//
                sheet.addCell(new Label(7, row, purchase_period.toString(), header_format));//
                Integer purchase_num = purchaseReports.getPurchase_num();
                if (purchase_num == null) {
                    purchase_num = 0;
                }
                sheet.addCell(new Number(8, row, purchase_num, header_format));//
                BigDecimal purchase_unit_cost_price = purchaseReports.getPurchase_unit_cost_price();
                if (purchase_unit_cost_price == null) {
                    purchase_unit_cost_price = new BigDecimal("0");
                }
                sheet.addCell(new Number(9, row, purchase_unit_cost_price.doubleValue(), header_format));//
                sheet.addCell(new Number(10, row, purchase_unit_cost_price.multiply(new BigDecimal(purchase_num)).doubleValue(), header_format));//
                sheet.addCell(new Label(11, row, purchaseReports.getConfirm_purchase_employee_name(), header_format));//
                sheet.addCell(new Label(12, row, purchaseReports.getCheck_purchase_employee_name(), header_format));//
                sheet.addCell(new Label(13, row, purchaseReports.getOrder().getOrder_code(), header_format));//
                sheet.addCell(new Label(14, row, purchaseReports.getOrder().getCustomer_name(), header_format));//
                sheet.addCell(new Label(15, row, purchaseReports.getOrder().getCustomer_phone_number(), header_format));//
                //到货状态，0新添加，1确认采购，2审核，3部分到货，4全部到货,5不许要采购
                String arrival_status = "等待确认采购";
                switch (purchaseReports.getPurchase_commodity_status()) {
                    case 1: arrival_status = "确认采购";    break;
                    case 2: arrival_status = "审核";    break;
                    case 3: arrival_status = "部分到货";    break;
                    case 4: arrival_status = "全部到货";    break;
                    case 5: arrival_status = "不需要采购";    break;
                    default:
                        break;
                }
                sheet.addCell(new Label(16, row, arrival_status, header_format));//
            }
            ResponseUtil.exportResponse(request, response, workbook);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
