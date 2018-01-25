package com.xinshan.controller.order;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.activity.ActivityComponents;
import com.xinshan.components.order.OrderComponents;
import com.xinshan.components.orderFee.OrderFeeComponents;
import com.xinshan.components.pay.PayComponents;
import com.xinshan.model.*;
import com.xinshan.model.extend.inventory.InventoryInExtend;
import com.xinshan.model.extend.order.OrderExtend;
import com.xinshan.model.extend.order.OrderReturnCommodityExtend;
import com.xinshan.model.extend.order.OrderReturnExtend;
import com.xinshan.model.extend.orderFee.OrderFeeExtend;
import com.xinshan.pojo.inventory.InventorySearchOption;
import com.xinshan.service.*;
import com.xinshan.utils.*;
import com.xinshan.pojo.order.OrderSearchOption;
import com.xinshan.utils.constant.order.OrderConstants;
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
import java.util.Date;
import java.util.List;

/**
 * Created by mxt on 17-3-24.
 */
@Controller
public class OrderReturnController {
    @Autowired
    private OrderReturnService orderReturnService;
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private InventoryInService inventoryInService;
    @Autowired
    private InventoryHistoryService inventoryHistoryService;
    @Autowired
    private SettlementService settlementService;
    @Autowired
    private OrderFeeService orderFeeService;

    /**
     * 添加订单退换货
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/return/createOrderReturn")
    public void createOrderReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String postData = RequestUtils.getRequestUtils().postData(request);
        OrderReturnExtend orderReturnExtend = JSONObject.parseObject(postData, OrderReturnExtend.class);
        Order order = orderService.getOrderById(orderReturnExtend.getOrder_id());
        if (order == null || (order.getReturn_status() != null && order.getReturn_status() == 1)) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "正在退货"));
            return;
        }

        try {
            orderReturnService.createOrderReturn(orderReturnExtend, employee);
            ResponseUtil.sendSuccessResponse(request, response, postData);
            ActivityComponents.sellLimit(orderReturnExtend);
        }catch (Exception e) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0015"));
            e.printStackTrace();
        }
    }

    /**
     * 编辑退换货
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/return/updateOrderReturn")
    public void updateOrderReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String postData = RequestUtils.getRequestUtils().postData(request);
        OrderReturnExtend orderReturnExtend = JSONObject.parseObject(postData, OrderReturnExtend.class);
        OrderReturnExtend orderReturnExtendOld = orderReturnService.getOrderReturnById(orderReturnExtend.getOrder_return_id());
        if (orderReturnExtendOld == null || orderReturnExtendOld.getOrder_return_check_status() == 1) {
            return;
        }
        orderReturnService.updateOrderReturn(orderReturnExtend, orderReturnExtendOld, employee);
        ResponseUtil.sendSuccessResponse(request, response, postData);
        ActivityComponents.sellLimit(orderReturnExtend);
    }

    /**
     * 退换货订单列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/return/orderReturnList")
    public void orderReturnList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        OrderSearchOption orderSearchOption = Request2ModelUtils.covert(OrderSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(orderSearchOption);
        List<Integer> list = orderReturnService.orderReturnIds(orderSearchOption);
        Integer count = orderReturnService.countOrderReturn(orderSearchOption);
        List<OrderReturnExtend> orderReturnExtends = orderReturnService.orderReturnList(list);
        ResponseUtil.sendListResponse(request, response, orderReturnExtends, count, orderSearchOption);
    }

    /**
     * 退换货单审核
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/return/checkOrderReturn")
    public void checkOrderReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int order_return_id = Integer.parseInt(request.getParameter("order_return_id"));
        OrderReturnExtend orderReturnExtend = orderReturnService.getOrderReturnById(order_return_id);
        if (orderReturnExtend == null || orderReturnExtend.getOrder_return_check_status() != 0) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "状态不正确"));
            return;
        }
        try {
            orderReturnService.checkOrderReturn(orderReturnExtend, employee);
            OrderComponents.totalPrice(orderReturnExtend.getOrder_id());
            ResponseUtil.sendSuccessResponse(request, response);
            ActivityComponents.sellLimit(orderReturnExtend);
        } catch (Exception e) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014"));
            e.printStackTrace();
        }
    }

    /**
     * 退换货单采购
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/return/orderReturnPurchase")
    public void orderReturnPurchase(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int order_return_id = Integer.parseInt(request.getParameter("order_return_id"));
        String purchase_remark = request.getParameter("purchase_remark");
        Date estimate_arrival_date = DateUtil.stringToDate(request.getParameter("estimate_arrival_date"));
        OrderReturnExtend orderReturnExtend = orderReturnService.getOrderReturnById(order_return_id);
        if (orderReturnExtend == null) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", ""));
            return;
        }

        if (orderReturnExtend.getOrder_return_check_status() == null || orderReturnExtend.getOrder_return_check_status() != 1) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0015", "未审核通过"));
            return;
        }

        if (orderReturnExtend.getOrder_return_purchase_status() != null && orderReturnExtend.getOrder_return_purchase_status() == 1) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0015", "已经采购"));
            return;
        }

        boolean b = false;
        List<OrderReturnCommodityExtend> list = orderReturnExtend.getOrderReturnCommodities();
        for (int i = 0; i < list.size(); i++) {
            OrderReturnCommodityExtend orderReturnCommodity = list.get(i);
            if (orderReturnCommodity.getOrder_return_commodity_type() == OrderConstants.ORDER_RETURN_COMMODITY_TYPE_ADD) {
                b = true;
                break;
            }
        }
        if (!b) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0017", "没有新增商品，不许要采购"));
            return;
        }
        purchaseService.createPurchase(orderReturnExtend, purchase_remark, estimate_arrival_date, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    @RequestMapping("/order/return/orderReturnPays")
    public void orderReturnPays(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<OrderReturn> list = orderReturnService.orderReturns();
        for (int i = 0; i < list.size(); i++) {
            OrderReturn orderReturn = list.get(i);
            List<OrderPay> orderPays = PayComponents.getOrderPaysByOrderReturnId(orderReturn.getOrder_return_id());
            String orderPayIds = PayComponents.orderPayIds(orderPays);
            orderReturn.setOrder_pay_ids(orderPayIds);
            orderReturnService.updateOrderReturn(orderReturn);
        }
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 退换货扣除费用付款
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/return/payDeduction")
    public void payDeduction(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String postData = RequestUtils.getRequestUtils().postData(request);
        OrderReturnExtend orderReturnExtend = JSONObject.parseObject(postData, OrderReturnExtend.class);
        List<OrderFeeExtend> orderFees = orderReturnExtend.getOrderFees();
        for (int i = 0; i < orderFees.size(); i++) {
            OrderFeeExtend orderFeeExtend = orderFees.get(i);
            BigDecimal supplier_fee = orderFeeExtend.getSupplier_fee();
            if (supplier_fee == null) {
                supplier_fee = new BigDecimal("0");
                orderFeeExtend.setSupplier_fee(supplier_fee);
            }
            BigDecimal fhc_fee = orderFeeExtend.getFhc_fee();
            if (fhc_fee == null) {
                fhc_fee = new BigDecimal("0");
                orderFeeExtend.setFhc_fee(fhc_fee);
            }
            BigDecimal customer_fee = orderFeeExtend.getCustomer_fee();
            if (customer_fee == null) {
                customer_fee = new BigDecimal("0");
                orderFeeExtend.setCustomer_fee(customer_fee);
            }
            BigDecimal fee = supplier_fee.add(fhc_fee).add(customer_fee);
            if (fee.doubleValue() > 0 && (orderFeeExtend.getWorker_id() == null)) {
                ResponseUtil.sendResponse(request, response, new ResultData("0x0014"));
                return;
            }
        }
        orderReturnService.orderReturnFee(orderReturnExtend, employee);
        ResponseUtil.sendSuccessResponse(request, response, postData);
    }

    /**
     * 退货结算
     * @param request
     * @param response
     * @throws IOException
     */
    /*@RequestMapping("/order/return/orderReturnSettlement")
    public void createSettlementOrderReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int order_return_id = Integer.parseInt(request.getParameter("order_return_id"));
        settlementService.createSettlementOrderReturn(order_return_id, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }*/

    /**
     * 退货商品入库
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/return/returnCommodityInventoryIn")
    public void returnCommodityInventoryIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int order_return_id = Integer.parseInt(request.getParameter("order_return_id"));
        int commodity_store_id = Integer.parseInt(request.getParameter("commodity_store_id"));
        OrderReturnExtend orderReturnExtend = orderReturnService.getOrderReturnById(order_return_id);
        List<OrderReturnCommodityExtend> orderReturnCommodities = orderReturnExtend.getOrderReturnCommodities();
        List<Integer> inventoryInIds = new ArrayList<>();
        for (int i = 0; i < orderReturnCommodities.size(); i++) {
            OrderReturnCommodityExtend orderReturnCommodityExtend = orderReturnCommodities.get(i);
            if (orderReturnCommodityExtend.getOrder_return_commodity_type() == OrderConstants.ORDER_RETURN_COMMODITY_TYPE_ADD) {
                continue;
            }
            if (orderReturnCommodityExtend.getInventory_in_commodity_status() != null
                    && orderReturnCommodityExtend.getInventory_in_commodity_status() == 0) {
                Integer inventory_in_commodity_id = orderReturnCommodityExtend.getInventory_in_commodity_id();
                InventoryInCommodity inventoryInCommodity = inventoryInService.getInventoryInCommodityById(inventory_in_commodity_id);
                inventoryInIds.add(inventoryInCommodity.getInventory_in_id());
            }
        }
        if (inventoryInIds.size() > 0) {
            InventorySearchOption inventorySearchOption = new InventorySearchOption();
            inventorySearchOption.setInventoryInIds(inventoryInIds);
            List<InventoryInExtend> list = inventoryInService.inventoryInList(inventorySearchOption);
            inventoryHistoryService.createInventoryHistory(orderReturnExtend, list, commodity_store_id, employee);
        }
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 退货商品结算
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/return/orderReturnSettlement")
    public void orderReturnSettlement(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int order_return_id = Integer.parseInt(request.getParameter("order_return_id"));
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        settlementService.createSettlementOrderReturn(order_return_id, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 费用状态设置
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/return/orderReturnFeeStatus")
    public void orderReturnFeeCheckStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int order_return_id = Integer.parseInt(request.getParameter("order_return_id"));
        OrderReturnExtend orderReturn = orderReturnService.getOrderReturnById(order_return_id);
        if (orderReturn.getOrder_return_fee_check_status() != null) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014"));
            return;
        }
        if (orderReturn.getOrder_return_deduction_amount_pay_status() != 1) {
            orderReturn.setOrder_return_fee_check_status(-1);
        }
        if (orderReturn.getOrder_return_deduction_amount_pay_status() == 1) {
            orderReturn.setOrder_return_fee_check_status(1);
        }
        orderReturnService.updateOrderReturn(orderReturn);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/return/orderReturnExport")
    public void orderReturnExport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        OrderSearchOption orderSearchOption = Request2ModelUtils.covert(OrderSearchOption.class, request);
        List<Integer> list = orderReturnService.orderReturnIds(orderSearchOption);
        List<OrderReturnExtend> orderReturnExtends = orderReturnService.orderReturnList(list);
        try {
            OutputStream outputStream = response.getOutputStream();
            response.reset();
            response.setHeader("Content-Type", "application/msexcel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("退换货导出").getBytes("utf-8"),"iso8859-1") + ".xls");
            response.setHeader("Cache-Control", "max-age=0");
            WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
            WritableSheet sheet = workbook.createSheet("sheet1", 0);

            //1正常；0逾期；-1冻结
            int row = 0;
            Counter col = new Counter();
            sheet.addCell(new Label(col.getN(), row, "订单号"));//
            sheet.addCell(new Label(col.getN(), row, "退货日期"));//
            sheet.addCell(new Label(col.getN(), row, "用户名"));//
            sheet.addCell(new Label(col.getN(), row, "电话"));//
            sheet.addCell(new Label(col.getN(), row, "商品货号"));//
            sheet.addCell(new Label(col.getN(), row, "商品名称"));//
            sheet.addCell(new Label(col.getN(), row, "供应商"));//
            sheet.addCell(new Label(col.getN(), row, "标准售价"));//
            sheet.addCell(new Label(col.getN(), row, "数量"));//
            sheet.addCell(new Label(col.getN(), row, "实际售价"));//
            sheet.addCell(new Label(col.getN(), row, "改版费"));//
            sheet.addCell(new Label(col.getN(), row, "类型"));//
            row++;
            for (int i = 0; i < list.size(); i++) {
                OrderReturnExtend orderReturnExtend = orderReturnExtends.get(i);
                List<OrderReturnCommodityExtend> orderReturnCommodities = orderReturnExtend.getOrderReturnCommodities();
                Order order = orderReturnExtend.getOrder();
                for (int j = 0; j < orderReturnCommodities.size(); j++) {
                    col.reset();
                    OrderReturnCommodityExtend orderReturnCommodityExtend = orderReturnCommodities.get(j);
                    Commodity commodity = orderReturnCommodityExtend.getCommodity();
                    Supplier supplier = orderReturnCommodityExtend.getSupplier();
                    sheet.addCell(new Label(col.getN(), row, order.getOrder_code()));//
                    sheet.addCell(new Label(col.getN(), row, DateUtil.format(orderReturnExtend.getOrder_return_date(), "yyyy-MM-dd")));//
                    sheet.addCell(new Label(col.getN(), row, order.getCustomer_name()));//
                    sheet.addCell(new Label(col.getN(), row, order.getCustomer_phone_number()));//
                    sheet.addCell(new Label(col.getN(), row, commodity.getCommodity_code()));//
                    sheet.addCell(new Label(col.getN(), row, commodity.getCommodity_name()));//
                    sheet.addCell(new Label(col.getN(), row, supplier.getSupplier_name()));//
                    BigDecimal sell_price = commodity.getSell_price();
                    BigDecimal bargain_price = orderReturnCommodityExtend.getBargain_price();
                    BigDecimal revision_fee = orderReturnCommodityExtend.getRevision_fee();
                    String type = "新增";
                    if (orderReturnCommodityExtend.getOrder_return_commodity_type() == OrderConstants.ORDER_RETURN_COMMODITY_TYPE_RETURN) {//退货
                        sell_price = new BigDecimal("0").subtract(sell_price);
                        bargain_price = new BigDecimal("0").subtract(bargain_price);
                        revision_fee = new BigDecimal("0").subtract(revision_fee);
                        type = "退货";
                    }
                    sheet.addCell(new Number(col.getN(), row, sell_price.doubleValue()));//
                    sheet.addCell(new Number(col.getN(), row, orderReturnCommodityExtend.getOrder_return_commodity_num()));//
                    sheet.addCell(new Number(col.getN(), row, bargain_price.doubleValue()));//
                    sheet.addCell(new Number(col.getN(), row, revision_fee.doubleValue()));//
                    sheet.addCell(new Label(col.getN(), row, type));//
                    row++;
                }
            }
            ResponseUtil.exportResponse(request, response, workbook);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修复退换货费用没有订单id bug
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/orderReturn/feeOrderId")
    public void orderReturnFeeOrderId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int order_return_id = Integer.parseInt(request.getParameter("order_return_id"));
        OrderReturnExtend orderReturn = orderReturnService.getOrderReturnById(order_return_id);
        String order_fee_ids = orderReturn.getOrder_fee_ids();
        List<OrderFeeExtend> list = OrderFeeComponents.orderFeeList(order_fee_ids);
        for (int i = 0; i < list.size(); i++) {
            OrderFeeExtend orderFeeExtend = list.get(i);
            if (orderFeeExtend.getOrder_id() == null) {
                orderFeeExtend.setOrder_id(orderReturn.getOrder_id());
                orderFeeService.updateOrderFeeWithTran(orderFeeExtend);
            }
        }
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 退换货报表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/orderReturn/report")
    public void orderReturnReport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        OrderSearchOption orderSearchOption = Request2ModelUtils.covert(OrderSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(orderSearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(orderSearchOption);
        List<OrderReturnCommodityExtend> list = orderReturnService.orderReturnReport(orderSearchOption);
        Integer count = orderReturnService.countOrderReturnReport(orderSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, orderSearchOption);
    }

    /**
     * 退换货报表导出
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/orderReturn/reportExport")
    public void orderReturnReportExport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        OrderSearchOption orderSearchOption = Request2ModelUtils.covert(OrderSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(orderSearchOption);
        List<OrderReturnCommodityExtend> list = orderReturnService.orderReturnReport(orderSearchOption);
        try {
            OutputStream outputStream = response.getOutputStream();
            response.reset();
            response.setHeader("Content-Type", "application/msexcel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("退换货报表导出").getBytes("utf-8"),"iso8859-1") + ".xls");
            response.setHeader("Cache-Control", "max-age=0");
            WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
            WritableSheet sheet = workbook.createSheet("sheet1", 0);

            //1正常；0逾期；-1冻结
            int row = 0;
            Counter col = new Counter();
            sheet.addCell(new Label(col.getN(), row, "退换货单号"));//
            sheet.addCell(new Label(col.getN(), row, "订单号"));//
            sheet.addCell(new Label(col.getN(), row, "退换货日期"));//
            sheet.addCell(new Label(col.getN(), row, "销售员"));//
            sheet.addCell(new Label(col.getN(), row, "客户姓名"));//
            sheet.addCell(new Label(col.getN(), row, "客户电话"));//
            sheet.addCell(new Label(col.getN(), row, "商品编号"));//
            sheet.addCell(new Label(col.getN(), row, "商品名称"));//
            sheet.addCell(new Label(col.getN(), row, "供应商"));//
            sheet.addCell(new Label(col.getN(), row, "退换货类型"));//
            sheet.addCell(new Label(col.getN(), row, "数量"));//
            sheet.addCell(new Label(col.getN(), row, "单价"));//
            sheet.addCell(new Label(col.getN(), row, "改版费"));//
            sheet.addCell(new Label(col.getN(), row, "改版尺寸"));//
            sheet.addCell(new Label(col.getN(), row, "金额"));//
            sheet.addCell(new Label(col.getN(), row, "退换货金额"));//
            sheet.addCell(new Label(col.getN(), row, "其他费用"));//
            sheet.addCell(new Label(col.getN(), row, "订单类型"));//
            sheet.addCell(new Label(col.getN(), row, "备注"));//
            row++;
            for (int i = 0; i < list.size(); i++) {
                col.reset();
                OrderReturnCommodityExtend orderReturnCommodityExtend = list.get(i);
                OrderReturn orderReturn = orderReturnCommodityExtend.getOrderReturn();
                sheet.addCell(new Label(col.getN(), row, orderReturn.getOrder_return_code()));//
                sheet.addCell(new Label(col.getN(), row, orderReturnCommodityExtend.getOrder().getOrder_code()));//
                sheet.addCell(new Label(col.getN(), row, DateUtil.format(orderReturn.getOrder_return_date(), "yyyy-MM-dd")));//
                sheet.addCell(new Label(col.getN(), row, orderReturnCommodityExtend.getOrder().getEmployee_name()));//
                sheet.addCell(new Label(col.getN(), row, orderReturnCommodityExtend.getOrder().getCustomer_name()));//
                sheet.addCell(new Label(col.getN(), row, orderReturnCommodityExtend.getOrder().getCustomer_phone_number()));//
                sheet.addCell(new Label(col.getN(), row, orderReturnCommodityExtend.getCommodity().getCommodity_code()));//
                sheet.addCell(new Label(col.getN(), row, orderReturnCommodityExtend.getCommodity().getCommodity_name()));//
                sheet.addCell(new Label(col.getN(), row, orderReturnCommodityExtend.getSupplier().getSupplier_name()));//
                String returnType = "";
                int num = 0;
                BigDecimal bargain_price = orderReturnCommodityExtend.getBargain_price();
                BigDecimal revision_fee = orderReturnCommodityExtend.getRevision_fee();
                if (orderReturnCommodityExtend.getOrder_return_commodity_type() == OrderConstants.ORDER_RETURN_COMMODITY_TYPE_RETURN) {
                    returnType = "退货";
                    num = -orderReturnCommodityExtend.getOrder_return_commodity_num();
                }else if(orderReturnCommodityExtend.getOrder_return_commodity_type() == OrderConstants.ORDER_RETURN_COMMODITY_TYPE_ADD){
                    returnType = "新增";
                    num = orderReturnCommodityExtend.getOrder_return_commodity_num();
                }
                sheet.addCell(new Label(col.getN(), row, returnType));//
                sheet.addCell(new Number(col.getN(), row, num));//
                sheet.addCell(new Number(col.getN(), row, orderReturnCommodityExtend.getBargain_price()==null?0:orderReturnCommodityExtend.getBargain_price().doubleValue()));//
                sheet.addCell(new Number(col.getN(), row, orderReturnCommodityExtend.getRevision_fee()==null?0:orderReturnCommodityExtend.getRevision_fee().doubleValue()));//
                sheet.addCell(new Label(col.getN(), row, orderReturnCommodityExtend.getRevision_size()));//
                sheet.addCell(new Number(col.getN(), row, bargain_price.add(revision_fee).multiply(new BigDecimal(num)).doubleValue()));//
                sheet.addCell(new Number(col.getN(), row, orderReturn.getOrder_return_amount().doubleValue()));//
                sheet.addCell(new Number(col.getN(), row, orderReturn.getOrder_return_deduction_amount() == null?0:orderReturn.getOrder_return_deduction_amount().doubleValue()));//
                if (orderReturn.getOrder_return_type() == OrderConstants.order_return_type_return) {
                    sheet.addCell(new Label(col.getN(), row, "售前退货"));//
                }else if (orderReturn.getOrder_return_type() == OrderConstants.order_return_type_after_sales) {
                    sheet.addCell(new Label(col.getN(), row, "售后退货"));//
                }
                sheet.addCell(new Label(col.getN(), row, orderReturn.getOrder_return_remark()));
                row++;
            }
            ResponseUtil.exportResponse(request, response, workbook);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
