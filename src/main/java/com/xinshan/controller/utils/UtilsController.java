package com.xinshan.controller.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.commodity.CommodityComponent;
import com.xinshan.components.order.OrderComponents;
import com.xinshan.components.orderFee.OrderFeeComponents;
import com.xinshan.components.pay.PayComponents;
import com.xinshan.model.*;
import com.xinshan.model.extend.activity.ActivityCommodityExtend;
import com.xinshan.model.extend.activity.ActivityExtend;
import com.xinshan.model.extend.commodity.CommodityNumExtend;
import com.xinshan.model.extend.inventory.InventoryHistoryDetailExtend;
import com.xinshan.model.extend.inventory.InventoryHistoryExtend;
import com.xinshan.model.extend.inventory.InventoryOutExtend;
import com.xinshan.model.extend.order.OrderExtend;
import com.xinshan.model.extend.order.OrderReturnExtend;
import com.xinshan.model.extend.orderFee.OrderFeeExtend;
import com.xinshan.model.extend.purchase.PurchaseExtend;
import com.xinshan.pojo.activity.ActivitySearchOption;
import com.xinshan.pojo.afterSales.AfterSalesSearchOption;
import com.xinshan.pojo.inventory.InventoryHistorySearchOption;
import com.xinshan.pojo.orderFee.OrderFeeSearchOption;
import com.xinshan.pojo.pay.PaySearchOption;
import com.xinshan.pojo.requestHistory.RequestHistorySearchOption;
import com.xinshan.service.*;
import com.xinshan.utils.*;
import com.xinshan.utils.thread.InventoryExport;
import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * Created by mxt on 17-5-4.
 */
@Controller
public class UtilsController {
    @Autowired
    private SystemService systemService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private InventoryOutService inventoryOutService;
    @Autowired
    private OrderReturnService orderReturnService;
    @Autowired
    private InventoryHistoryService inventoryHistoryService;
    @Autowired
    private InventoryInService inventoryInService;
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private SettlementService settlementService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private CommodityService commodityService;
    /**
     * 修复编辑订单造成的付款数据增多问题
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sys/fix/orderPayFix")
    public void orderPayFix(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer order_id = Integer.parseInt(request.getParameter("order_id"));
        orderPayFix(order_id);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    private void orderPayFix(Integer order_id) {
        RequestHistorySearchOption requestHistorySearchOption = new RequestHistorySearchOption();
        requestHistorySearchOption.setRequest_url("/order/order/updateOrder");
        requestHistorySearchOption.setQuery_request_data("1");
        requestHistorySearchOption.setOrder_id(order_id);
        List<RequestHistory> list = systemService.requestHistories(requestHistorySearchOption);
        OrderExtend orderExtend = null;
        if (order_id != null) {
            orderExtend = orderService.getOrderById(order_id);
        }

        Set<Integer> set = new HashSet<>();
        PaySearchOption paySearchOption = new PaySearchOption();
        paySearchOption.setOrder_pay_type(PayComponents.pay_type_order);
        for (int i = 0; i < list.size(); i++) {
            RequestHistory requestHistory = list.get(i);
            String request_data = requestHistory.getRequest_data();
            List<Integer> orderPayIds = null;
            if (orderExtend != null) {
                orderPayIds = SplitUtils.splitToList(orderExtend.getOrder_pay_ids(), ",");
            }else {
                OrderExtend order = JSON.parseObject(request_data, OrderExtend.class);
                order_id = order.getOrder_id();
                order = orderService.getOrderById(order_id);
                orderPayIds = SplitUtils.splitToList(order.getOrder_pay_ids(), ",");
            }
            if (orderPayIds == null) {
                orderPayIds = new ArrayList<>();
            }
            paySearchOption.setOrder_id(orderExtend.getOrder_id());
            List<OrderPay> orderPays = PayComponents.orderPays(paySearchOption);
            for (int j = 0; j < orderPays.size(); j++) {
                OrderPay orderPay = orderPays.get(j);
                if (!orderPayIds.contains(orderPay.getOrder_pay_id())) {
                    //错误数据
                    set.add(orderPay.getOrder_pay_id());
                }
            }
        }

        List<OrderPay> orderPays = PayComponents.orderPays(SplitUtils.setToString(set));
        if (saveToFile(orderPays, order_id)) {
            for (int i = 0; i < orderPays.size(); i++) {
                OrderPay orderPay = orderPays.get(i);
                PayComponents.deleteOrderPay(orderPay);
            }
        }
    }

    private boolean saveToFile(List<OrderPay> orderPays, int order_id) {
        String filename = DateUtil.format(DateUtil.currentDate(), "yyyy-MM-dd HH:mm:ss") + "orderPayFix-"+order_id+".txt";
        File file = new File(CommonUtils.FILE_UPLOAD_DIR+"/"+filename);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            for (int i = 0; i < orderPays.size(); i++) {
                OrderPay orderPay = orderPays.get(i);
                fileOutputStream.write(JSONObject.toJSONString(orderPay).getBytes());
                fileOutputStream.write("\n".getBytes());
            }
            fileOutputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 重新计算订单金额
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sys/fix/totalPrice")
    public void orderTotalPrice(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int order_id = Integer.parseInt(request.getParameter("order_id"));
        OrderComponents.totalPrice(order_id);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sys/fix/orderReturnInventoryOut")
    public void orderReturnOutFix(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int order_return_id = Integer.parseInt(request.getParameter("order_return_id"));
        OrderReturnExtend orderReturnExtend = orderReturnService.getOrderReturnById(order_return_id);
        inventoryOutService.createOrderReturnInventoryOut1(employee, orderReturnExtend);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 修复入库历史记录订单id丢失问题
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sys/fix/inventoryInHistoryFix")
    public void inventoryHistoryFix(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int inventory_history_id = Integer.parseInt(request.getParameter("inventory_history_id"));
        InventoryHistorySearchOption inventoryHistorySearchOption = new InventoryHistorySearchOption();
        inventoryHistorySearchOption.setInventory_type("0");
        inventoryHistorySearchOption.setInventory_history_id(inventory_history_id);
        List<InventoryHistoryExtend> inventoryHistoryExtends = inventoryHistoryService.inventoryHistories(inventoryHistorySearchOption);
        String n = "";
        for (int i = 0; i < inventoryHistoryExtends.size(); i++) {
            InventoryHistoryExtend inventoryHistoryExtend = inventoryHistoryExtends.get(i);
            Integer order_id = inventoryHistoryExtend.getOrder_id();
            if (order_id == null) {
                List<InventoryHistoryDetailExtend> inventoryHistoryDetails = inventoryHistoryExtend.getInventoryHistoryDetails();
                if (inventoryHistoryDetails != null && inventoryHistoryDetails.size() > 0) {
                    InventoryHistoryDetailExtend inventoryHistoryDetailExtend = inventoryHistoryDetails.get(0);
                    Integer inventory_in_commodity_id = inventoryHistoryDetailExtend.getInventory_in_commodity_id();
                    InventoryInCommodity inventoryInCommodityById = inventoryInService.getInventoryInCommodityById(inventory_in_commodity_id);
                    if (inventoryInCommodityById != null && inventoryInCommodityById.getPurchase_commodity_id() != null) {
                        PurchaseCommodity purchaseCommodity = purchaseService.getPurchaseCommodityById(inventoryInCommodityById.getPurchase_commodity_id());
                        if (purchaseCommodity != null) {
                            Integer purchase_id = purchaseCommodity.getPurchase_id();
                            PurchaseExtend purchaseExtend = purchaseService.getPurchaseById(purchase_id);
                            if (purchaseExtend != null && purchaseExtend.getOrder_id() != null) {
                                inventoryHistoryExtend.setOrder_id(purchaseExtend.getOrder_id());
                                inventoryHistoryService.updateInventoryHistory(inventoryHistoryExtend);
                            }else {
                                n = "purchaseExtend = null or purchaseExtend.getOrder_id() = null";
                            }
                        }else {
                            n = "purchaseCommodity = null";
                        }
                    }else {
                        n = "inventoryInCommodityById = null or inventoryInCommodityById.getPurchase_commodity_id() = null";
                    }
                }
            }
        }
        ResponseUtil.sendSuccessResponse(request, response, n);
    }

    /**
     * 广东馆商品入库结算
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sys/fix/guangdongCommoditySettlement")
    public void guangdongCommoditySettlement(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int inventory_history_id = Integer.parseInt(request.getParameter("inventory_history_id"));
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        settlementService.createSettlementInventoryIn(inventory_history_id, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    @RequestMapping("/sys/fix/commodityExportFileSync")
    public void commodityExportFileSync(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ThreadPool threadPool = new ThreadPool(1);
        threadPool.getExecutorService().submit(new InventoryExport());
        threadPool.getExecutorService().shutdown();
        ResponseUtil.sendSuccessResponse(request, response);
    }


    @RequestMapping("/sys/fix/inventoryExport")
    public void inventoryExport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long a = System.currentTimeMillis();
        try {
            List<CommodityNumExtend> list = inventoryService.commodityNumList1(null);
            String filename = new String(("库存导出").getBytes("utf-8"),"iso8859-1") + ".xls";
            response.setHeader("Content-Type", "application/msexcel");
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);
            response.setHeader("Cache-Control", "max-age=0");
            WritableWorkbook workbook = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet sheet = workbook.createSheet("sheet1", 0);
            //1正常；0逾期；-1冻结
            int row = 0;
            Counter col = new Counter();
            sheet.addCell(new Label(col.getN(), row, "原货号"));//
            sheet.addCell(new Label(col.getN(), row, "新货号"));//
            sheet.addCell(new Label(col.getN(), row, "样品"));//
            sheet.addCell(new Label(col.getN(), row, "是否退货商品"));//
            sheet.addCell(new Label(col.getN(), row, "商品名称"));//
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
        long b = System.currentTimeMillis();
        System.out.println("b-a="+(b - a));
    }


    /**
     * 修复出库状态
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sys/fix/fixStatus")
    public void fixInventoryOutStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int inventory_out_id = Integer.parseInt(request.getParameter("inventory_out_id"));
        inventoryOutService.inventoryOutStatus(inventory_out_id);
        InventoryOutExtend inventoryOutExtend = inventoryOutService.getInventoryOutById(inventory_out_id);
        Integer order_id = inventoryOutExtend.getOrder_id();
        if (order_id != null) {
            OrderComponents.orderStep(order_id);
        }
        ResponseUtil.sendSuccessResponse(request, response);
    }

    @RequestMapping("/sys/fix/fixOrderId")
    public void fixOrderFeeOrderId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Integer> orderReturnIds = orderReturnService.orderReturnIds(null);
        List<OrderReturnExtend> list = orderReturnService.orderReturnList(orderReturnIds);
        for (int i = 0; i < list.size(); i++) {
            OrderReturnExtend orderReturnExtend = list.get(i);
            List<OrderFeeExtend> orderFees = orderReturnExtend.getOrderFees();
            if (orderFees != null && orderFees.size() >0 ) {
                for (int j = 0; j < orderFees.size(); j++) {
                    OrderFeeExtend orderFeeExtend = orderFees.get(j);
                    if (orderFeeExtend.getOrder_id() == null) {
                        orderFeeExtend.setOrder_id(orderReturnExtend.getOrder_id());
                        System.out.println("\t"+orderFeeExtend.getOrder_fee_id() + "\tcustomer_fee:"+orderFeeExtend.getCustomer_fee() +
                                "\tfhc_fee:"+orderFeeExtend.getFhc_fee() + "\tsupplier_fee:"+orderFeeExtend.getSupplier_fee());
                        orderFeeExtend.setOrder_id(orderReturnExtend.getOrder_id());
                        OrderFeeComponents.updateOrderFee(orderFeeExtend);
                    }
                }
            }
        }
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 退礼品结算
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sys/fix/giftReturnSettlement")
    public void giftReturnSettlement(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int gift_return_id = Integer.parseInt(request.getParameter("gift_return_id"));
        settlementService.giftReturnSettlement(gift_return_id, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 采购到货结算
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sys/fix/purchaseArrivalSettlement")
    public void purchaseArrivalSettlement(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int purchase_commodity_id = Integer.parseInt(request.getParameter("purchase_commodity_id"));
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        settlementService.createSettlementPurchaseArrival(purchase_commodity_id, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 特价活动商品原采购价和原销售价
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sys/fix/activityCommodity")
    public void activityCommodity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<ActivityCommodityExtend> activityCommodityExtends = activityService.activityCommodities(null);
        for (int i = 0; i < activityCommodityExtends.size(); i++) {
            ActivityCommodityExtend activityCommodityExtend = activityCommodityExtends.get(i);
            Integer commodity_id = activityCommodityExtend.getCommodity_id();
            Commodity commodity = CommodityComponent.getCommodityById(commodity_id);
            activityCommodityExtend.setPurchase_price(commodity.getPurchase_price());
            activityCommodityExtend.setSell_price(commodity.getSell_price());
            activityService.updateActivityCommodity(activityCommodityExtend);
        }
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sys/fix/commodityActivityIds")
    public void commodityActivityIds(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivitySearchOption activitySearchOption = new ActivitySearchOption();
        activitySearchOption.setCurrentDate(new Date());
        activitySearchOption.setActivity_type(1);
        List<ActivityExtend> list = activityService.activityList(activitySearchOption);
        for (int i = 0; i < list.size(); i++) {
            ActivityExtend activityExtend = list.get(i);
            List<ActivityCommodityExtend> activityCommodities = activityExtend.getActivityCommodities();
            for (int j = 0; j < activityCommodities.size(); j++) {
                ActivityCommodityExtend activityCommodityExtend = activityCommodities.get(j);
                Commodity commodity = commodityService.getCommodityById(activityCommodityExtend.getCommodity_id());
                commodity.setActivity_commodity_id(activityCommodityExtend.getActivity_commodity_id());
                commodityService.updateCommodity(commodity);
            }
        }
        ResponseUtil.sendSuccessResponse(request, response);
    }
}
