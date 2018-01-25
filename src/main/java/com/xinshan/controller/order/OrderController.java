package com.xinshan.controller.order;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.activity.ActivityComponents;
import com.xinshan.components.employee.EmployeeComponent;
import com.xinshan.components.order.OrderComponents;
import com.xinshan.components.pay.PayComponents;
import com.xinshan.components.position.PositionComponent;
import com.xinshan.components.salesTarget.SalesTargetComponent;
import com.xinshan.components.supplier.SupplierComponents;
import com.xinshan.model.*;
import com.xinshan.model.extend.order.OrderCommodityExtend;
import com.xinshan.model.extend.order.OrderCommodityReturnExtend;
import com.xinshan.model.extend.order.OrderExtend;
import com.xinshan.model.extend.position.PositionExtend;
import com.xinshan.pojo.order.OrderSearchOption;
import com.xinshan.pojo.position.PositionSearchOption;
import com.xinshan.service.*;
import com.xinshan.utils.*;
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
import java.util.*;

/**
 * Created by mxt on 16-10-24.
 * 订单
 */
@Controller
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private OrderClauseService orderClauseService;
    @Autowired
    private CommodityService commodityService;
    /**
     * 添加订单
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/order/createOrder")
    public void createOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String postData = RequestUtils.getRequestUtils().postData(request);
        OrderExtend orderExtend = JSONObject.parseObject(postData, OrderExtend.class);
        boolean chinaPhoneLegal = PhoneNumberUtil.isChinaPhoneLegal(orderExtend.getCustomer_phone_number());
        if (!chinaPhoneLegal) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0015", "手机号不正确"));
            return;
        }
        try {
            orderService.createOrder(orderExtend, employee);
        }catch (Exception e) {
            e.printStackTrace();
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "订单信息不正确"));
            return;
        }
        ResponseUtil.sendSuccessResponse(request, response, postData);
        OrderComponents.totalPrice(orderExtend.getOrder_id());
        ActivityComponents.sellLimit(orderExtend);
    }

    /**
     * 审核订单生成采购单
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/order/createPurchase")
    public void createPurchase(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int order_id = Integer.parseInt(request.getParameter("order_id"));
        String purchase_remark = request.getParameter("purchase_remark");
        Date estimate_arrival_date = DateUtil.stringToDate(request.getParameter("estimate_arrival_date"));
        OrderExtend orderExtend = orderService.getOrderById(order_id);
        if (orderExtend == null) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "订单号不正确"));
            return;
        }
        if (orderExtend.getTrans_purchase() == 1) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0015", "已经审核"));
            return;
        }
        purchaseService.createPurchase(orderExtend, purchase_remark, estimate_arrival_date, employee);
        ResponseUtil.sendSuccessResponse(request, response);
        OrderComponents.orderStep(order_id);
        SalesTargetComponent.salesTargetAnalysis(orderExtend);
        ActivityComponents.sellLimit(orderExtend);
    }

    /**
     * 编辑订单
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/order/updateOrder")
    public void updateOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = (Employee) request.getSession().getAttribute("employee");
        String postData = RequestUtils.getRequestUtils().postData(request);
        OrderExtend orderExtend = JSONObject.parseObject(postData, OrderExtend.class);
        orderExtend.setRecord_employee_code(employee.getEmployee_code());
        orderExtend.setRecord_employee_name(employee.getEmployee_name());
        orderExtend.setRecord_date(DateUtil.currentDate());
        orderService.updateOrder(orderExtend, employee);
        OrderComponents.totalPrice(orderExtend.getOrder_id());
        ResponseUtil.sendSuccessResponse(request, response, postData);
        ActivityComponents.sellLimit(orderExtend);
    }

    /**
     * 删除
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/order/deleteOrder")
    public void deleteOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int order_id = Integer.parseInt(request.getParameter("order_id"));
        orderService.deleteOrder(order_id);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 订单列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/order/order/orderList", "/order/afterSales/orderList"})
    public void orderList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        OrderSearchOption orderSearchOption = Request2ModelUtils.covert(OrderSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(orderSearchOption);
        if (orderSearchOption.getOrder_status() == null && orderSearchOption.getOrderStatuses() == null) {
            orderSearchOption.setOrder_status(1);
        }

        if (orderSearchOption.getEmployee_position() != null && orderSearchOption.getEmployee_position().equals(1)) {
            SearchOptionUtil.getSearchOptionUtil().positionLimit(orderSearchOption, EmployeeComponent.getEmployeeByCode(employee.getEmployee_code()));
        }

        List<Integer> orderIds = orderService.orderIds(orderSearchOption);
        int count = orderService.countOrder(orderSearchOption);
        if (orderIds == null || orderIds.size() == 0) {
            ResponseUtil.sendListResponse(request, response, new ArrayList(), orderIds.size(), orderSearchOption);
            return;
        }
        List<OrderExtend> list = orderService.orderList(orderIds);

        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("count", count);
        int allPage = count/orderSearchOption.getLimit();
        if (count % orderSearchOption.getLimit() > 0) {
            allPage++;
        }
        map.put("allPage", allPage);
        map.put("query", orderSearchOption);

        orderSearchOption.setLimit(null);
        orderSearchOption.setStart(null);

        ResponseUtil.sendSuccessResponse(request, response, map);
    }

    @RequestMapping({"/order/order/supplierOrderList"})
    public void supplierOrderList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        OrderSearchOption orderSearchOption = Request2ModelUtils.covert(OrderSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(orderSearchOption);
        if (orderSearchOption.getOrder_status() == null && orderSearchOption.getOrderStatuses() == null) {
            orderSearchOption.setOrder_status(1);
        }

        List<Integer> supplierIds = SupplierComponents.concractsSupplierIds(employee.getEmployee_code());
        orderSearchOption.setSupplierIds(supplierIds);

        List<Integer> orderIds = orderService.orderIds(orderSearchOption);
        int count = orderService.countOrder(orderSearchOption);
        if (orderIds == null || orderIds.size() == 0) {
            ResponseUtil.sendListResponse(request, response, new ArrayList(), orderIds.size(), orderSearchOption);
            return;
        }
        List<OrderExtend> list = orderService.orderList(orderIds, supplierIds);

        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("count", count);
        int allPage = count/orderSearchOption.getLimit();
        if (count % orderSearchOption.getLimit() > 0) {
            allPage++;
        }
        map.put("allPage", allPage);
        map.put("query", orderSearchOption);

        orderSearchOption.setLimit(null);
        orderSearchOption.setStart(null);

        ResponseUtil.sendSuccessResponse(request, response, map);
    }


    @RequestMapping("/order/order/orderFeeStatics")
    public void orderStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        OrderSearchOption orderSearchOption = Request2ModelUtils.covert(OrderSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(orderSearchOption);
        if (orderSearchOption.getOrder_status() == null && orderSearchOption.getOrderStatuses() == null) {
            orderSearchOption.setOrder_status(1);
        }

        if (orderSearchOption.getEmployee_position() != null && orderSearchOption.getEmployee_position().equals(1)) {
            SearchOptionUtil.getSearchOptionUtil().positionLimit(orderSearchOption, EmployeeComponent.getEmployeeByCode(employee.getEmployee_code()));
        }
        long a = System.currentTimeMillis();
        List<HashMap> orderFeeStatics = orderService.orderFeeStatics(orderSearchOption);
        long b = System.currentTimeMillis();
        System.out.println("--------------------------"+(b-a));
        BigDecimal orderCarryFeeStatics = orderService.orderCarryFeeStatics(orderSearchOption);
        BigDecimal countPreferential = orderService.countPreferential(orderSearchOption);
        Map<String, Object> map = new HashMap<>();
        map.put("orderFeeStatics", orderFeeStatics);
        map.put("orderCarryFeeStatics", orderCarryFeeStatics);
        map.put("countPreferential", countPreferential);
        ResponseUtil.sendSuccessResponse(request, response, map);
    }

    /**
     * 金额统计
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/order/feeStatics")
    public void orderStatics(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        OrderSearchOption orderSearchOption = Request2ModelUtils.covert(OrderSearchOption.class, request);
        orderSearchOption.setLimit(null);
        orderSearchOption.setCurrentPage(null);
        orderSearchOption.setStart(null);
        if (orderSearchOption.getOrder_status() == null && orderSearchOption.getOrderStatuses() == null) {
            orderSearchOption.setOrder_status(1);
        }
        SearchOptionUtil.getSearchOptionUtil().positionLimit(orderSearchOption, EmployeeComponent.getEmployeeByCode(employee.getEmployee_code()));
        List<HashMap> orderFeeStatics = orderService.orderFeeStatics(orderSearchOption);
        BigDecimal orderCarryFeeStatics = orderService.orderCarryFeeStatics(orderSearchOption);
        BigDecimal countPreferential = orderService.countPreferential(orderSearchOption);
        Map<String, Object> map = new HashMap<>();
        map.put("orderFeeStatics", orderFeeStatics);
        map.put("orderCarryFeeStatics", orderCarryFeeStatics);
        map.put("countPreferential", countPreferential);
        ResponseUtil.sendSuccessResponse(request, response, map);
    }

    /**
     * 订单导出
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/order/orderExport")
    public void orderExport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        OrderSearchOption orderSearchOption = Request2ModelUtils.covert(OrderSearchOption.class, request);
        if (orderSearchOption.getOrder_status() == null && orderSearchOption.getOrderStatuses() == null) {
            orderSearchOption.setOrder_status(1);
        }
        SearchOptionUtil.getSearchOptionUtil().positionLimit(orderSearchOption, EmployeeComponent.getEmployeeByCode(employee.getEmployee_code()));

        List<Integer> orderIds = orderService.orderIds(orderSearchOption);
        if (orderIds == null || orderIds.size() == 0) {
            return;
        }
        try {
            OutputStream outputStream = response.getOutputStream();
            response.reset();
            response.setHeader("Content-Type", "application/msexcel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("销售订单").getBytes("utf-8"),"iso8859-1") + ".xls");
            response.setHeader("Cache-Control", "max-age=0");
            WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
            WritableSheet sheet = workbook.createSheet("sheet1", 0);

            //1正常；0逾期；-1冻结
            Counter col = new Counter();
            sheet.addCell(new Label(col.getN(), 0, "订单号"));//
            sheet.addCell(new Label(col.getN(), 0, "手工单号"));//
            sheet.addCell(new Label(col.getN(), 0, "下单时间"));//
            sheet.addCell(new Label(col.getN(), 0, "用户姓名"));//
            sheet.addCell(new Label(col.getN(), 0, "用户电话"));//
            sheet.addCell(new Label(col.getN(), 0, "地址"));//
            sheet.addCell(new Label(col.getN(), 0, "送货时间"));//
            sheet.addCell(new Label(col.getN(), 0, "馆长"));//
            sheet.addCell(new Label(col.getN(), 0, "销售员"));//
            sheet.addCell(new Label(col.getN(), 0, "商品总价"));//
            sheet.addCell(new Label(col.getN(), 0, "搬运费"));//
            sheet.addCell(new Label(col.getN(), 0, "优惠"));//
            sheet.addCell(new Label(col.getN(), 0, "总价"));//
            sheet.addCell(new Label(col.getN(), 0, "收银员"));//
            sheet.addCell(new Label(col.getN(), 0, "状态"));//
            sheet.addCell(new Label(col.getN(), 0, "现金"));//
            sheet.addCell(new Label(col.getN(), 0, "中原银行"));//
            sheet.addCell(new Label(col.getN(), 0, "兴业银行"));//
            sheet.addCell(new Label(col.getN(), 0, "应收提货券"));//
            sheet.addCell(new Label(col.getN(), 0, "实收提货券"));//

            orderExport1(sheet, col, orderIds);

            ResponseUtil.exportResponse(request, response, workbook);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void orderExport1(WritableSheet sheet, Counter col, List<Integer> orderIds) throws Exception {
        long a = System.currentTimeMillis();
        List<Map> list = orderService.orderExport(orderIds);
        long b = System.currentTimeMillis();
        System.out.println("----------------------------------" + (b-a) + "ms");
        int row = 1;
        for (int i = 0; i < list.size(); i++) {
            Map map = list.get(i);
            col.reset();
            sheet.addCell(new Label(col.getN(), row, map.get("order_code") == null ? "":map.get("order_code").toString()));//
            sheet.addCell(new Label(col.getN(), row, map.get("order_paper_code") == null ? "":map.get("order_paper_code").toString()));//
            sheet.addCell(new Label(col.getN(), row, map.get("order_date") == null ? "":map.get("order_date").toString().substring(0, 10)));//
            sheet.addCell(new Label(col.getN(), row, map.get("customer_name") == null ? "":map.get("customer_name").toString()));//
            sheet.addCell(new Label(col.getN(), row, map.get("customer_phone_number") == null ? "":map.get("customer_phone_number").toString()));//
            sheet.addCell(new Label(col.getN(), row, map.get("delivery_address") == null ? "":map.get("delivery_address").toString()));//
            sheet.addCell(new Label(col.getN(), row, map.get("delivery_date") == null ? "":map.get("delivery_date").toString().substring(0, 10)));//
            sheet.addCell(new Label(col.getN(), row, map.get("position_name") == null ? "":map.get("position_name").toString()));//
            sheet.addCell(new Label(col.getN(), row, map.get("employee_name") == null ? "":map.get("employee_name").toString()));//
            sheet.addCell(new Number(col.getN(), row, map.get("order_total_price") == null ? 0:new BigDecimal(map.get("order_total_price").toString()).doubleValue()));//
            sheet.addCell(new Number(col.getN(), row, map.get("order_carry_fee") == null ? 0:new BigDecimal(map.get("order_carry_fee").toString()).doubleValue()));//
            sheet.addCell(new Number(col.getN(), row, map.get("preferential_amount") == null ? 0:new BigDecimal(map.get("preferential_amount").toString()).doubleValue()));//
            sheet.addCell(new Number(col.getN(), row, map.get("total_price") == null ? 0:new BigDecimal(map.get("total_price").toString()).doubleValue()));//
            sheet.addCell(new Label(col.getN(), row, map.get("record_employee_name") == null ? "":map.get("record_employee_name").toString()));//
            if (map.get("order_step") != null) {
                sheet.addCell(new Label(col.getN(), row, map.get("order_step") == null ? "":OrderComponents.orderStepStr(Integer.parseInt(map.get("order_step").toString()))));//
            }else {
                sheet.addCell(new Label(col.getN(), row, ""));//
            }
            String order_pay_ids = "";
            if (map.get("order_pay_ids") != null) {
                order_pay_ids = map.get("order_pay_ids").toString();
            }
            List<OrderPay> orderPays = PayComponents.orderPays(order_pay_ids);
            writePay(sheet, col, row, orderPays);
            row ++;
        }
    }
    private void orderExport(WritableSheet sheet, Counter col, List<Integer> orderIds) throws Exception {
        List<OrderExtend> list = orderService.orderList(orderIds);
        int row = 1;
        for (int i = 0; i < list.size(); i++) {
            col.reset();
            OrderExtend orderExtend = list.get(i);
            sheet.addCell(new Label(col.getN(), row, orderExtend.getOrder_code()));//
            sheet.addCell(new Label(col.getN(), row, orderExtend.getOrder_paper_code()));//
            sheet.addCell(new Label(col.getN(), row, DateUtil.format(orderExtend.getOrder_date(), "yyyy-MM-dd")));//
            sheet.addCell(new Label(col.getN(), row, orderExtend.getCustomer_name()));//
            sheet.addCell(new Label(col.getN(), row, orderExtend.getCustomer_phone_number()));//
            sheet.addCell(new Label(col.getN(), row, orderExtend.getDelivery_address()));//
            sheet.addCell(new Label(col.getN(), row, DateUtil.format(orderExtend.getDelivery_date(), "yyyy-MM-dd")));//
            Position position = orderExtend.getPosition();
            if (position != null && position.getPosition_name() != null) {
                sheet.addCell(new Label(col.getN(), row, position.getPosition_name()));//
            }else {
                sheet.addCell(new Label(col.getN(), row, ""));//
            }
            sheet.addCell(new Label(col.getN(), row, orderExtend.getEmployee_name()));//
            sheet.addCell(new Number(col.getN(), row, orderExtend.getOrder_total_price().doubleValue()));//
            List<OrderCarryFee> orderCarryFees = orderExtend.getOrderCarryFees();
            BigDecimal orderCarryFee = new BigDecimal("0");
            if (orderCarryFees != null && orderCarryFees.size() > 0) {
                for (int j = 0; j < orderCarryFees.size(); j++) {
                    OrderCarryFee orderCarryFee1 = orderCarryFees.get(j);
                    if (orderCarryFee1.getOrder_carry_fee() != null) {
                        orderCarryFee = orderCarryFee.add(orderCarryFee1.getOrder_carry_fee());
                    }
                }
            }
            sheet.addCell(new Number(col.getN(), row, orderCarryFee.doubleValue()));//
            sheet.addCell(new Number(col.getN(), row, orderExtend.getPreferential_amount().doubleValue()));//
            sheet.addCell(new Number(col.getN(), row, orderExtend.getTotal_price().doubleValue()));//
            sheet.addCell(new Label(col.getN(), row, orderExtend.getRecord_employee_name()));//
            Integer order_step = orderExtend.getOrder_step();
            if (order_step == null) {
                order_step = OrderComponents.orderStep(orderExtend.getOrder_id());
            }
            System.out.println(orderExtend.getOrder_code() + "\t" +order_step);
            sheet.addCell(new Label(col.getN(), row, OrderComponents.orderStepStr(order_step)));//

            List<OrderPay> orderPays = orderExtend.getOrderPays();
            writePay(sheet, col, row, orderPays);
            row++;
        }
    }

    private void writePay(WritableSheet sheet, Counter col, int row, List<OrderPay> orderPays) throws WriteException {
        BigDecimal cash = new BigDecimal("0");//现金
        BigDecimal zyb = new BigDecimal("0");//中原银行
        BigDecimal xyb = new BigDecimal("0");//兴业银行
        BigDecimal tihuoquan1 = new BigDecimal("0");//应收提货券
        BigDecimal tihuoquan2 = new BigDecimal("0");//实收提货券
        if (orderPays != null) {
            for (int j = 0; j < orderPays.size(); j++) {
                OrderPay orderPay = orderPays.get(j);
                BigDecimal pay_amount = orderPay.getPay_amount();
                BigDecimal need_amount = orderPay.getNeed_amount();
                if (pay_amount == null) {
                    pay_amount = new BigDecimal("0");
                }
                if (need_amount == null) {
                    need_amount = new BigDecimal("0");
                }
                Integer pay_source = orderPay.getPay_source();
                switch (pay_source) {
                    case PayComponents.pay_source_cash:
                        cash = cash.add(pay_amount);
                        break;
                    case PayComponents.pay_source_zhongyuan_bank:
                        zyb = zyb.add(pay_amount);
                        break;
                    case PayComponents.pay_source_xingye_bank:
                        xyb = xyb.add(pay_amount);
                        break;
                    case PayComponents.pay_source_tihuoquan:
                        tihuoquan1 = tihuoquan1.add(need_amount);
                        tihuoquan2 = tihuoquan2.add(pay_amount);
                        break;
                }
            }
        }

        sheet.addCell(new Number(col.getN(), row, cash.doubleValue()));//
        sheet.addCell(new Number(col.getN(), row, zyb.doubleValue()));//
        sheet.addCell(new Number(col.getN(), row, xyb.doubleValue()));//
        sheet.addCell(new Number(col.getN(), row, tihuoquan1.doubleValue()));//
        sheet.addCell(new Number(col.getN(), row, tihuoquan2.doubleValue()));//
    }

    /**
     * 导购列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/order/daogouList")
    public void daogouList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        OrderSearchOption orderSearchOption = Request2ModelUtils.covert(OrderSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(orderSearchOption);
        List<Map> list = orderService.daogouList(orderSearchOption);
        ResponseUtil.sendSuccessResponse(request, response, list);
    }

    @RequestMapping("/order/order/orderDetail")
    public void orderDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        List<Integer> orderIds = SplitUtils.splitToList(request.getParameter("order_id"), ",");
        List<OrderExtend> list = orderService.orderList(orderIds);
        if(list != null){
            for (int i = 0; i < list.size(); i++) {
                OrderExtend orderExtend = list.get(i);
                List<OrderCommodityExtend> orderCommodityExtends = orderExtend.getOrderCommodities();
                for (int j = 0; j < orderCommodityExtends.size(); j++) {
                    OrderCommodityExtend orderCommodity = orderCommodityExtends.get(j);
                    Commodity commodity = orderCommodity.getCommodity();
                    if (commodity != null && commodity.getSupplier_id() != null) {
                        orderCommodity.setSupplier(supplierService.getSupplierById(commodity.getSupplier_id()));
                    }
                }
                orderExtend.setPrintEmployee(employee);
                orderExtend.setOrderCommodityReturns(orderService.orderCommodityReturns(orderExtend.getOrder_id()));
            }
        }
        ResponseUtil.sendSuccessResponse(request, response, list);
    }

    /**
     * 退货
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/order/orderCommodityReturn")
    public void orderCommodityReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = (Employee) request.getSession().getAttribute("employee");
        OrderCommodityReturn orderCommodityReturn = Request2ModelUtils.covert(OrderCommodityReturn.class, request);
        orderService.createOrderCommodityReturn(orderCommodityReturn, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 退货详情
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/order/orderCommodityReturnDetail")
    public void orderCommodityReturnDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int order_commodity_return_id = Integer.parseInt(request.getParameter("order_commodity_return_id"));
        OrderCommodityReturn orderCommodityReturn = orderService.getReturnById(order_commodity_return_id);
        OrderCommodityReturnExtend orderCommodityReturnExtend = new OrderCommodityReturnExtend();
        orderCommodityReturnExtend.setReturn_employee_name(orderCommodityReturn.getReturn_employee_name());
        orderCommodityReturnExtend.setReturn_employee_code(orderCommodityReturn.getReturn_employee_code());
        orderCommodityReturnExtend.setOrder_commodity_return_date(orderCommodityReturn.getOrder_commodity_return_date());
        orderCommodityReturnExtend.setOrder_commodity_ids(orderCommodityReturn.getOrder_commodity_ids());
        orderCommodityReturnExtend.setOrder_commodity_return_id(orderCommodityReturn.getOrder_commodity_return_id());
        orderCommodityReturnExtend.setOrder_id(orderCommodityReturn.getOrder_id());
        orderCommodityReturnExtend.setReturn_reason(orderCommodityReturn.getReturn_reason());
        List<Integer> orderCommodityIds = SplitUtils.splitToList(orderCommodityReturn.getOrder_commodity_ids(), ",");
        int order_id = orderCommodityReturn.getOrder_id();
        OrderExtend orderExtend = orderService.getOrderById(order_id);
        List<OrderCommodityExtend> orderCommodityExtends = orderExtend.getOrderCommodities();
        List<OrderCommodityExtend> list = new ArrayList<>();
        for (int i = 0; i < orderCommodityExtends.size(); i++) {
            if (orderCommodityIds.contains(orderCommodityExtends.get(i).getOrder_commodity_id())) {
                list.add(orderCommodityExtends.get(i));
            }
        }
        orderCommodityReturnExtend.setOrderCommodities(list);
        orderCommodityReturnExtend.setOrder(orderExtend);
        ResponseUtil.sendSuccessResponse(request, response, orderCommodityReturnExtend);
    }

    /**
     * 检查订单步骤
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/order/orderStep")
    public void orderStep(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int order_id = Integer.parseInt(request.getParameter("order_id"));
        OrderComponents.orderStep(order_id);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 订单步骤初始化
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/order/orderStepInit")
    public void orderStepInit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long a = System.currentTimeMillis();
        OrderComponents.orderStepInit();
        long b = System.currentTimeMillis();
        ResponseUtil.sendSuccessResponse(request, response, b-a);
    }

    /**
     * 订单商品状态检查
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/order/orderCommodityStatus")
    public void orderCommodityStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int order_commodity_id = Integer.parseInt(request.getParameter("order_commodity_id"));
        OrderComponents.orderCommodityStatus(order_commodity_id);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    @RequestMapping("/order/order/orderDaogou")
    public void orderDaogou(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String position_ids = request.getParameter("position_ids");
        List<Integer> list = SplitUtils.splitToList(position_ids, ",");
        List<PositionExtend> positionExtend = new ArrayList<>();
        if (list != null && list.size() >0){
            PositionSearchOption positionSearchOption = new PositionSearchOption();
            positionSearchOption.setPositionList(list);
            positionExtend = positionService.positionList(positionSearchOption);
        }
        positionService.updatePositionDaogou(positionExtend);
        PositionComponent.clear();
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/order/orderPositionList")
    public void positionList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PositionSearchOption positionSearchOption = new PositionSearchOption();
        positionSearchOption.setDaogou(1);
        List<PositionExtend> list = positionService.positionList(positionSearchOption);
        ResponseUtil.sendSuccessResponse(request, response, list);
    }

    /**
     * 条款详情
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/order/clauseDetail")
    public void orderClauseDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseUtil.sendSuccessResponse(request, response, orderClauseService.getClauseByType(1));
    }

    /**
     * 编辑条款
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/clause/updateClause")
    public void updateClause(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String s = request.getParameter("order_clause");
        OrderClause orderClause = orderClauseService.getClauseByType(1);
        orderClause.setOrder_clause(s);
        orderClauseService.updateOrderClause(orderClause);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 销售报表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/order/orderReports")
    public void orderReports(HttpServletRequest request, HttpServletResponse response) throws IOException {
        OrderSearchOption orderSearchOption = Request2ModelUtils.covert(OrderSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(orderSearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(orderSearchOption);
        List<OrderCommodityExtend> list = orderService.orderCommodityExtends(orderSearchOption);
        Integer count = orderService.countOrderCommodity(orderSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, orderSearchOption);
    }


    /**
     * 供应商销售报表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/order/orderSupplierReports")
    public void orderSupplierReports(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        OrderSearchOption orderSearchOption = Request2ModelUtils.covert(OrderSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(orderSearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(orderSearchOption);
        if (employee.getPosition_id() == 0) {
            List<Integer> list = SupplierComponents.concractsSupplierIds(employee.getEmployee_code());
            orderSearchOption.setSupplierIds(list);
        }
        orderSearchOption.setOrder_commodity_supplier_status(1);
        orderSearchOption.setCommodity_num(1);
        List<OrderCommodityExtend> list = orderService.orderCommodityExtends(orderSearchOption);
        Integer count = orderService.countOrderCommodity(orderSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, orderSearchOption);
    }

    /**
     * 供应商确认订单
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/order/supplierConfirm")
    public void orderSupplierConfirm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int order_commodity_id = Integer.parseInt(request.getParameter("order_commodity_id"));
        OrderCommodityExtend orderCommodityExtend = orderService.getOrderCommodityExtend(order_commodity_id);
        OrderCommoditySupplier orderCommoditySupplier = orderCommodityExtend.getOrderCommoditySupplier();
        if (orderCommoditySupplier == null || orderCommoditySupplier.getOrder_commodity_supplier_id() == null) {
            orderCommoditySupplier = new OrderCommoditySupplier();
            orderCommoditySupplier.setOrder_commodity_supplier_status(0);
            orderCommoditySupplier.setOrder_commodity_id(order_commodity_id);
            orderService.createOrderCommoditySupplier(orderCommoditySupplier);
        }

        orderCommoditySupplier.setOrder_commodity_supplier_status(1000);
        orderCommoditySupplier.setSupplier_confirm_date(DateUtil.currentDate());
        orderCommoditySupplier.setSupplier_confirm_employee_code(employee.getEmployee_code());
        orderCommoditySupplier.setSupplier_confirm_employee_name(employee.getEmployee_name());
        orderService.updateOrderCommoditySupplier(orderCommoditySupplier);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 供应商确认到货
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/order/supplierArrival")
    public void supplierArrival(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int order_commodity_id = Integer.parseInt(request.getParameter("order_commodity_id"));
        OrderCommodityExtend orderCommodityExtend = orderService.getOrderCommodityExtend(order_commodity_id);
        OrderCommoditySupplier orderCommoditySupplier = orderCommodityExtend.getOrderCommoditySupplier();
        orderCommoditySupplier.setOrder_commodity_supplier_status(2000);
        orderCommoditySupplier.setSupplier_arrival_date(DateUtil.currentDate());
        orderCommoditySupplier.setSupplier_arrival_employee_code(employee.getEmployee_code());
        orderCommoditySupplier.setSupplier_arrival_employee_name(employee.getEmployee_name());
        orderService.updateOrderCommoditySupplier(orderCommoditySupplier);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    @RequestMapping("/order/order/orderCommoditySupplierInit")
    public void orderCommoditySupplierInit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getParameter("order_commodity_id") != null && !request.getParameter("order_commodity_id").equals("")) {
            int order_commodity_id = Integer.parseInt(request.getParameter("order_commodity_id"));
            OrderCommodityExtend orderCommodityExtend = orderService.getOrderCommodityExtend(order_commodity_id);
            if (orderCommodityExtend.getOrderCommoditySupplier() == null || orderCommodityExtend.getOrderCommoditySupplier().getOrder_commodity_supplier_id() == null) {
                OrderCommoditySupplier orderCommoditySupplier = new OrderCommoditySupplier();
                orderCommoditySupplier.setOrder_commodity_id(order_commodity_id);
                if (orderCommodityExtend.getOrder_commodity_status() > OrderConstants.ORDER_COMMODITY_STATUS_ALL_ARRIVAL) {
                    orderCommoditySupplier.setOrder_commodity_supplier_status(2000);
                }else {
                    orderCommoditySupplier.setOrder_commodity_supplier_status(0);
                }
                orderService.createOrderCommoditySupplier(orderCommoditySupplier);
            }
        }else {
            List<OrderCommodityExtend> list = orderService.orderCommodityExtends(null);
            for (int i = 0; i < list.size(); i++) {
                OrderCommodityExtend orderCommodityExtend = list.get(i);
                if (orderCommodityExtend.getOrderCommoditySupplier() == null || orderCommodityExtend.getOrderCommoditySupplier().getOrder_commodity_supplier_id() == null) {
                    OrderCommoditySupplier orderCommoditySupplier = new OrderCommoditySupplier();
                    orderCommoditySupplier.setOrder_commodity_id(orderCommodityExtend.getOrder_commodity_id());
                    if (orderCommodityExtend.getOrder_commodity_status() > OrderConstants.ORDER_COMMODITY_STATUS_ALL_ARRIVAL) {
                        orderCommoditySupplier.setOrder_commodity_supplier_status(2000);
                    }else {
                        orderCommoditySupplier.setOrder_commodity_supplier_status(0);
                    }
                    orderService.createOrderCommoditySupplier(orderCommoditySupplier);
                }
            }
        }

        ResponseUtil.sendSuccessResponse(request, response);
    }

    @RequestMapping("/order/order/orderCommodityStatics")
    public void orderCommodityStatics(HttpServletRequest request, HttpServletResponse response) throws IOException{
        OrderSearchOption orderSearchOption = Request2ModelUtils.covert(OrderSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(orderSearchOption);
        Map map = orderService.orderCommodityStatics(orderSearchOption);
        if (map == null) {
            map = new HashMap();
        }
        /*List<Map> list = orderService.simpleOrderCommodities(orderSearchOption);
        BigDecimal purchase_total_price = new BigDecimal("0");
        for (int i = 0; i < list.size(); i++) {
            Map map1 = list.get(i);
            int commodity_id = Integer.parseInt(map1.get("commodity_id").toString());
            if (map1.get("purchase_commodity_id") != null && !map1.get("purchase_commodity_id").equals("")) {
                int purchase_commodity_id = Integer.parseInt(map1.get("purchase_commodity_id").toString());
                PurchaseCommodity purchaseCommodity = purchaseService.getPurchaseCommodityById(purchase_commodity_id);
                if (purchaseCommodity != null && purchaseCommodity.getPurchase_total_price() != null) {
                    purchase_total_price = purchase_total_price.add(purchaseCommodity.getPurchase_total_price());
                }
            }else {
                Commodity commodity = commodityService.getCommodityById(commodity_id);
                purchase_total_price = purchase_total_price.add(commodity.getPurchase_price());
            }
        }
        map.put("purchase_total_price1", purchase_total_price.doubleValue());*/
        BigDecimal bigDecimal = orderService.purchaseTotalPrice(orderSearchOption);
        if (bigDecimal != null) {
            map.put("purchase_total_price", bigDecimal.doubleValue());
        }
        ResponseUtil.sendSuccessResponse(request, response, map);
    }

    /**
     * 销售报表导出
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/order/orderReportsExport")
    public void orderReportsExport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        OrderSearchOption orderSearchOption = Request2ModelUtils.covert(OrderSearchOption.class, request);
        List<OrderCommodityExtend> list = orderService.orderCommodityExtends(orderSearchOption);
        try {
            OutputStream outputStream = response.getOutputStream();
            response.reset();
            response.setHeader("Content-Type", "application/msexcel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("销售报表").getBytes("utf-8"),"iso8859-1") + ".xls");
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

            //WritableCellFormat wcf_left_format = new WritableCellFormat(new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD));
            //1正常；0逾期；-1冻结
            int row = 0;
            Counter col = new Counter();
            sheet.addCell(new Label(col.getN(), row, "下单时间", header_format));//
            sheet.addCell(new Label(col.getN(), row, "订单号", header_format));//
            sheet.addCell(new Label(col.getN(), row, "送货时间", header_format));//
            sheet.addCell(new Label(col.getN(), row, "备注", header_format));//
            sheet.addCell(new Label(col.getN(), row, "货号", header_format));//
            sheet.addCell(new Label(col.getN(), row, "名称", header_format));//
            sheet.addCell(new Label(col.getN(), row, "是否样品", header_format));//
            sheet.addCell(new Label(col.getN(), row, "数量", header_format));//
            sheet.addCell(new Label(col.getN(), row, "采购单价", header_format));//
            sheet.addCell(new Label(col.getN(), row, "采购总价", header_format));//
            sheet.addCell(new Label(col.getN(), row, "改版尺寸", header_format));//
            sheet.addCell(new Label(col.getN(), row, "改版费", header_format));//
            sheet.addCell(new Label(col.getN(), row, "增值卡", header_format));//
            sheet.addCell(new Label(col.getN(), row, "销售单价", header_format));//
            sheet.addCell(new Label(col.getN(), row, "销售总价", header_format));//
            sheet.addCell(new Label(col.getN(), row, "客户名", header_format));//
            sheet.addCell(new Label(col.getN(), row, "客户电话", header_format));//
            sheet.addCell(new Label(col.getN(), row, "送货地址", header_format));//
            sheet.addCell(new Label(col.getN(), row, "供应商", header_format));//
            sheet.addCell(new Label(col.getN(), row, "委托人", header_format));//
            sheet.addCell(new Label(col.getN(), row, "收银", header_format));//
            sheet.addCell(new Label(col.getN(), row, "销售", header_format));//
            sheet.addCell(new Label(col.getN(), row, "馆长", header_format));//
            sheet.addCell(new Label(col.getN(), row, "状态", header_format));//
            row++;
            for (int i = 0; i < list.size(); i++) {
                col.reset();
                OrderCommodityExtend orderCommodity = list.get(i);
                Order order = orderCommodity.getOrder();
                Commodity commodity = orderCommodity.getCommodity();
                OrderCommodityValueAddedCard orderCommodityValueAddedCard = orderCommodity.getOrderCommodityValueAddedCard();
                PurchaseCommodity purchaseCommodity = orderCommodity.getPurchaseCommodity();
                if (order.getOrder_date() == null) {
                    sheet.addCell(new Label(col.getN(), row, DateUtil.format(order.getRecord_date(), "yyyy-MM-dd"), header_format));//
                }else {
                    sheet.addCell(new Label(col.getN(), row, DateUtil.format(order.getOrder_date(), "yyyy-MM-dd"), header_format));//
                }
                sheet.addCell(new Label(col.getN(), row, order.getOrder_code(), header_format));//
                if (order.getDelivery_date() != null) {
                    sheet.addCell(new Label(col.getN(), row, DateUtil.format(order.getDelivery_date(), "yyyy-MM-dd"), header_format));//
                }else {
                    sheet.addCell(new Label(col.getN(), row, "", header_format));//
                }
                sheet.addCell(new Label(col.getN(), row, orderCommodity.getOrder_commodity_remark(), header_format));//
                sheet.addCell(new Label(col.getN(), row, commodity.getCommodity_code(), header_format));//
                sheet.addCell(new Label(col.getN(), row, commodity.getCommodity_name(), header_format));//
                String sample = "否";
                if (orderCommodity.getSample() != null && orderCommodity.getSample() == 1) {
                    sample = "是";
                }
                sheet.addCell(new Label(col.getN(), row, sample, header_format));//
                sheet.addCell(new Number(col.getN(), row, orderCommodity.getCommodity_num(), header_format));//
                if (purchaseCommodity.getPurchase_unit_cost_price() != null && purchaseCommodity.getPurchase_unit_cost_price().doubleValue() > 0) {
                    sheet.addCell(new Number(col.getN(), row, purchaseCommodity.getPurchase_unit_cost_price().doubleValue(), header_format));//
                    sheet.addCell(new Number(col.getN(), row, purchaseCommodity.getPurchase_cost_price().doubleValue(), header_format));//
                }else {
                    sheet.addCell(new Number(col.getN(), row, commodity.getPurchase_price().doubleValue(), header_format));//
                    sheet.addCell(new Number(col.getN(), row, commodity.getPurchase_price().multiply(new BigDecimal(orderCommodity.getCommodity_num())).doubleValue(), header_format));//
                }
                sheet.addCell(new Label(col.getN(), row, orderCommodity.getRevision_size(), header_format));//
                BigDecimal revision_fee = orderCommodity.getRevision_fee();
                if (revision_fee == null) {
                    revision_fee = new BigDecimal("0");
                }
                sheet.addCell(new Number(col.getN(), row, revision_fee.doubleValue(), header_format));//
                if (orderCommodityValueAddedCard != null && orderCommodityValueAddedCard.getCard_code() != null) {
                    sheet.addCell(new Number(col.getN(), row, orderCommodityValueAddedCard.getCard_amount().doubleValue(), header_format));//
                }else {
                    sheet.addCell(new Label(col.getN(), row, "", header_format));//
                }
                BigDecimal bargain_price = orderCommodity.getBargain_price();
                sheet.addCell(new Number(col.getN(), row, bargain_price.doubleValue(), header_format));//
                bargain_price = bargain_price.add(revision_fee);
                bargain_price = bargain_price.multiply(new BigDecimal(orderCommodity.getCommodity_num()));
                sheet.addCell(new Number(col.getN(), row, bargain_price.doubleValue(), header_format));//
                sheet.addCell(new Label(col.getN(), row, order.getCustomer_name(), header_format));//
                sheet.addCell(new Label(col.getN(), row, order.getCustomer_phone_number(), header_format));//
                sheet.addCell(new Label(col.getN(), row, order.getDelivery_address(), header_format));//
                sheet.addCell(new Label(col.getN(), row, orderCommodity.getSupplier().getSupplier_name(), header_format));//
                sheet.addCell(new Label(col.getN(), row, orderCommodity.getSupplier().getContacts(), header_format));//

                sheet.addCell(new Label(col.getN(), row, order.getRecord_employee_name(), header_format));//
                sheet.addCell(new Label(col.getN(), row, order.getEmployee_name(), header_format));//
                Position position = orderCommodity.getPosition();
                if (position != null && position.getPosition_name() != null) {
                    sheet.addCell(new Label(col.getN(), row, position.getPosition_name(), header_format));//
                }else {
                    sheet.addCell(new Label(col.getN(), row, "", header_format));//
                }
                sheet.addCell(new Label(col.getN(), row, orderCommodityStatus(orderCommodity.getOrder_commodity_status()), header_format));//
                row++;
            }
            ResponseUtil.exportResponse(request, response, workbook);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //采购中  不采购   供应商发货 已经部分送到 已经全部送到 退货
    private String orderCommodityStatus(int order_commodity_status) {
        String s = "";
        switch (order_commodity_status) {//0,100,200,300,350,
            case OrderConstants.ORDER_COMMODITY_STATUS: //初始状态，等待生成采购单
                s = "采购中";
                break;
            case OrderConstants.ORDER_COMMODITY_STATUS_PURCHASE: //已生成采购单，等待确认采购
                s = "采购中";
                break;
            case OrderConstants.ORDER_COMMODITY_STATUS_PURCHASE_CONFIRM: //采购已确认
                s = "采购中";
                break;
            case OrderConstants.ORDER_COMMODITY_STATUS_PURCHASE_CHECK: //采购已审核
                s = "采购中";
                break;
            case OrderConstants.ORDER_COMMODITY_STATUS_PART_ARRIVAL: //bufendaohuo
                s = "采购中";
                break;
            case OrderConstants.ORDER_COMMODITY_STATUS_ALL_ARRIVAL: //全部到货
                s = "已到货";
                break;
            case OrderConstants.ORDER_COMMODITY_STATUS_NO_PURCHASE: //不采购
                s = "不采购";
                break;
            case OrderConstants.ORDER_COMMODITY_STATUS_PART_OUT: //部分出库
                s = "已部分出库";
                break;
            case OrderConstants.ORDER_COMMODITY_STATUS_COMPLETE_OUT: //全部出库
                s = "已全部出库";
                break;
            case OrderConstants.ORDER_COMMODITY_STATUS_SUPPLIER_DELIVERY: //供应商发货
                s = "供应商发货";
                break;
            case OrderConstants.ORDER_COMMODITY_STATUS_PART_DONE: //600部分到货
                s = "部分送到";
                break;
            case OrderConstants.ORDER_COMMODITY_STATUS_ALL_DONE: //650全部到货
                s = "全部送到";
                break;
            case -1:
                s = "退货";
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
    @RequestMapping("/order/order/orderPayIds")
    public void orderPayIds(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Integer> orderIds = orderService.orderIds(null);
        for (int i = 0; i < orderIds.size(); i++) {
            int order_id = orderIds.get(i);
            OrderExtend order = orderService.getOrderById(order_id);
            List<OrderPay> orderPays = order.getOrderPays();
            List<Integer> orderPayIds = new ArrayList<>();
            for (int j = 0; j < orderPays.size(); j++) {
                OrderPay orderPay = orderPays.get(j);
                if (orderPay.getOrder_pay_type() == OrderConstants.order_pay_type_add_order) {
                    orderPayIds.add(orderPay.getOrder_pay_id());
                }
            }
            order.setOrder_pay_ids(SplitUtils.listToString(orderPayIds));
            orderService.updateOrderPayIds(order);
        }
        ResponseUtil.sendSuccessResponse(request, response);
    }




}
