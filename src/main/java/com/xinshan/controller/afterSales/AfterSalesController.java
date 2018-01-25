package com.xinshan.controller.afterSales;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.orderFee.OrderFeeComponents;
import com.xinshan.model.AfterSalesCommodity;
import com.xinshan.model.Employee;
import com.xinshan.model.OrderFeeType;
import com.xinshan.model.extend.afterSales.AfterSalesCommodityExtend;
import com.xinshan.model.extend.afterSales.AfterSalesExtend;
import com.xinshan.model.extend.orderFee.OrderFeeExtend;
import com.xinshan.pojo.afterSales.AfterSalesSearchOption;
import com.xinshan.pojo.orderFee.OrderFeeSearchOption;
import com.xinshan.service.*;
import com.xinshan.utils.*;
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
import java.util.List;

/**
 * Created by mxt on 16-11-23.
 * 售后
 */
@Controller
public class AfterSalesController {
    @Autowired
    private SettlementService settlementService;
    @Autowired
    private OrderFeeService orderFeeService;
    @Autowired
    private AfterSalesService afterSalesService;
    @Autowired
    private OrderReturnService orderReturnService;
    @Autowired
    private InventoryInService inventoryInService;
    /**
     * 添加费用类型
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/inventoryOut/createOrderFeeType")
    public void createOrderFeeType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        OrderFeeType orderFeeType = Request2ModelUtils.covert(OrderFeeType.class, request);
        orderFeeService.createOrderFeeType(orderFeeType);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 费用类型列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/inventoryOut/orderFeeTypeList")
    public void orderFeeTypeList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseUtil.sendSuccessResponse(request, response, orderFeeService.orderFeeTypes());
    }

    /**
     * 送货结束，添加费用
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/inventoryOut/createOrderFee")
    public void createOrderFee(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String postData = RequestUtils.getRequestUtils().postData(request);
        JSONArray jsonArray = JSON.parseArray(postData);
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = JSON.parseObject(jsonArray.get(i).toString());
            JSONArray array = JSON.parseArray(jsonObject.get("fee").toString());
            for (int j = 0; j < array.size(); j++) {
                JSONObject json = JSON.parseObject(array.get(j).toString());
                BigDecimal supplier_fee = new BigDecimal("0");
                BigDecimal fhc_fee = new BigDecimal("0");
                BigDecimal customer_fee = new BigDecimal("0");
                if (json.get("supplier_fee") != null && !json.get("supplier_fee").toString().equals("")) {
                    supplier_fee = new BigDecimal(json.get("supplier_fee").toString());
                }
                if (json.get("fhc_fee") != null && !json.get("fhc_fee").toString().equals("")) {
                    fhc_fee = new BigDecimal(json.get("fhc_fee").toString());
                }
                if (json.get("customer_fee") != null && !"".equals(json.get("customer_fee").toString())) {
                    customer_fee = new BigDecimal(json.get("customer_fee").toString());
                }
                BigDecimal fee = supplier_fee.add(fhc_fee).add(customer_fee);
                if (fee.doubleValue() > 0 && (json.get("worker_id") == null || json.get("worker_id").toString().equals(""))) {
                    ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "未选择工人师傅"));
                    return;
                }
            }
        }
        orderFeeService.createOrderFee(jsonArray, employee);
        ResponseUtil.sendSuccessResponse(request, response, postData);
    }

    /**
     * 出库费用审核
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/inventoryOut/checkOrderFee")
    public void checkOrderFee(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int inventory_history_id = Integer.parseInt(request.getParameter("inventory_history_id"));
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        orderFeeService.checkOrderFee(inventory_history_id, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 出库结算
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/inventoryOut/inventoryOutSettlement")
    public void inventoryOutSettlement(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int inventory_history_id = Integer.parseInt(request.getParameter("inventory_history_id"));
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        settlementService.createSettlementInventoryOut(inventory_history_id, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 费用列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/order/inventoryOut/orderFeeList", "/order/afterSales/orderFeeList"})
    public void orderFeeList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AfterSalesSearchOption afterSalesSearchOption = Request2ModelUtils.covert(AfterSalesSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(afterSalesSearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(afterSalesSearchOption);
        List<OrderFeeExtend> orderFeeExtends = orderFeeService.orderFees(afterSalesSearchOption);
        Integer count = orderFeeService.countOrderFee(afterSalesSearchOption);
        ResponseUtil.sendListResponse(request, response, orderFeeExtends, count, afterSalesSearchOption);
    }

    /**
     * 售后商品详情
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/afterSales/afterSalesCommodityDetail")
    public void afterSalesCommodityDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int after_sales_commodity_id = Integer.parseInt(request.getParameter("after_sales_commodity_id"));
        AfterSalesCommodityExtend afterSalesCommodityExtend = afterSalesService.getAfterSalesCommodityById(after_sales_commodity_id);
        ResponseUtil.sendSuccessResponse(request, response, afterSalesCommodityExtend);
    }

    /**
     * 添加售后问题
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/afterSales/createAfterSales")
    public void createAfterSalesProblem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = (Employee) request.getSession().getAttribute("employee");
        String postData = RequestUtils.getRequestUtils().postData(request);
        AfterSalesExtend afterSalesExtend = JSON.parseObject(postData, AfterSalesExtend.class);
        afterSalesService.createAfterSales(afterSalesExtend, employee);
        ResponseUtil.sendSuccessResponse(request, response, afterSalesExtend, postData);
    }

    /**
     * 售后列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/afterSales/afterSalesList")
    public void afterSalesProblemList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AfterSalesSearchOption afterSalesSearchOption = Request2ModelUtils.covert(AfterSalesSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(afterSalesSearchOption);
        List<Integer> afterSalesIds = afterSalesService.afterSalesIds(afterSalesSearchOption);
        if (afterSalesIds == null || afterSalesIds.size() == 0) {
            ResponseUtil.sendListResponse(request, response, new ArrayList(), 0, afterSalesSearchOption);
        }else {
            Integer count = afterSalesService.countAfterSales(afterSalesSearchOption);
            afterSalesSearchOption.setAfterSalesIds(afterSalesIds);
            List<AfterSalesExtend> list = afterSalesService.afterSalesList(afterSalesSearchOption);
            ResponseUtil.sendListResponse(request, response, list, count, afterSalesSearchOption);
        }
    }

    /**
     * 维修商品确认
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/afterSales/afterSalesCommodityConfirm")
    public void afterSalesCommodityConfirm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int after_sales_commodity_id = Integer.parseInt(request.getParameter("after_sales_commodity_id"));
        AfterSalesCommodity afterSalesCommodity = afterSalesService.getAfterSalesCommodityById(after_sales_commodity_id);
        if (afterSalesCommodity == null
                || afterSalesCommodity.getAfter_sales_type() != 0
                || afterSalesCommodity.getCommodity_problem_fix_status() != 0) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x00014"));
            return;
        }
        afterSalesCommodity.setCommodity_problem_fix_status(1);
        afterSalesService.updateAfterSalesCommodity(afterSalesCommodity);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 处理结果
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/afterSales/afterSalesResolve")
    public void afterSalesResolve(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String postData = RequestUtils.getRequestUtils().postData(request);
        JSONArray jsonArray = JSON.parseArray(postData);
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = JSON.parseObject(jsonArray.get(i).toString());
            JSONArray array = JSON.parseArray(jsonObject.get("fee").toString());
            for (int j = 0; j < array.size(); j++) {
                JSONObject json = JSON.parseObject(array.get(j).toString());
                BigDecimal supplier_fee = new BigDecimal("0");
                BigDecimal fhc_fee = new BigDecimal("0");
                BigDecimal customer_fee = new BigDecimal("0");
                if (json.get("supplier_fee") != null && !json.get("supplier_fee").toString().equals("")) {
                    supplier_fee = new BigDecimal(json.get("supplier_fee").toString());
                }
                if (json.get("fhc_fee") != null && !json.get("fhc_fee").toString().equals("")) {
                    fhc_fee = new BigDecimal(json.get("fhc_fee").toString());
                }
                if (json.get("customer_fee") != null && !"".equals(json.get("customer_fee").toString())) {
                    customer_fee = new BigDecimal(json.get("customer_fee").toString());
                }
                BigDecimal fee = supplier_fee.add(fhc_fee).add(customer_fee);
                if (fee.doubleValue() > 0 && (json.get("worker_id") == null || json.get("worker_id").toString().equals(""))) {
                    ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "未选择工人师傅"));
                    return;
                }
            }
        }
        orderFeeService.createAfterSalesOrderFee(jsonArray, employee);
        ResponseUtil.sendSuccessResponse(request, response, postData);
    }

    /**
     * 售后结算
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/afterSales/afterSalesSettlement")
    public void afterSalesSettlement(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int after_sales_id = Integer.parseInt(request.getParameter("after_sales_id"));
        AfterSalesExtend afterSales = afterSalesService.getAfterSalesById(after_sales_id);
        boolean done = afterSalesDone(afterSales);
        if (!done) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "未完成"));
            return;
        }
        if (afterSales.getAfter_sales_status() == 1) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0015", "已结算"));
            return;
        }
        afterSalesService.afterSalesSettlement(afterSales);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    private boolean afterSalesDone(AfterSalesExtend afterSales) {
        List<AfterSalesCommodityExtend> list = afterSales.getAfterSalesCommodities();
        boolean done = true;
        //暂不判断售后维修是否完成
        /*for (int i = 0; i < list.size(); i++) {
            AfterSalesCommodityExtend afterSalesCommodityExtend = list.get(i);
            int type = afterSalesCommodityExtend.getAfter_sales_type();
            if (type == AfterSalesConstant.after_sales_type_fix) {
                if (afterSalesCommodityExtend.getCommodity_problem_fix_status() != 1) {
                    done = false;
                    break;
                }
            }else if (type == AfterSalesConstant.after_sales_type_return) {
                if (afterSalesCommodityExtend.getCommodity_problem_return_status() != 1) {
                    done = false;
                    break;
                }
                if (afterSalesCommodityExtend.getReturn_pay_status() != 1) {
                    done = false;
                    break;
                }
            }
        }*/
        return done;
    }

    /**
     * 售后退货
     * @param request
     * @param response
     * @throws IOException
     */
    /*@RequestMapping("/order/afterSales/returnCommodity")
    public void afterSalesReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String postData = RequestUtils.getRequestUtils().postData(request);
        OrderReturnExtend orderReturnExtend = JSONObject.parseObject(postData, OrderReturnExtend.class);
        try {
            orderReturnService.createOrderReturn(orderReturnExtend, employee);
            ResponseUtil.sendSuccessResponse(request, response, postData);
        }catch (Exception e) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0015"));
            e.printStackTrace();
        }
    }*/

    /**
     * 退货商品入库
     * @param request
     * @param response
     * @throws IOException
     */
    /*@RequestMapping("/order/afterSales/returnCommodityIn")
    public void afterSalesReturnPay(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int after_sales_commodity_id = Integer.parseInt(request.getParameter("after_sales_commodity_id"));
        int commodity_store_id = Integer.parseInt(request.getParameter("commodity_store_id"));
        int sample = Integer.parseInt(request.getParameter("sample"));
        AfterSalesCommodityExtend afterSalesCommodityExtend = afterSalesService.getAfterSalesCommodityById(after_sales_commodity_id);
        AfterSales afterSales = afterSalesService.getAfterSalesById(afterSalesCommodityExtend.getAfter_sales_id());
        OrderSearchOption orderSearchOption = new OrderSearchOption();
        orderSearchOption.setAfter_sales_commodity_id(afterSalesCommodityExtend.getAfter_sales_commodity_id());
        List<OrderReturnCommodityExtend> list = orderReturnService.orderReturnCommodities(orderSearchOption);
        if (list != null && list.size() == 1) {
            OrderReturnCommodityExtend orderReturnCommodityExtend = list.get(0);
            inventoryInService.returnCommodityInventoryIn(afterSales, afterSalesCommodityExtend, orderReturnCommodityExtend, commodity_store_id, sample, employee);
            ResponseUtil.sendSuccessResponse(request, response);
        }else {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014"));
        }
    }*/

    /**
     * 售后退款
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/afterSales/afterSalesReturnPay")
    public void returnPayCommodity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String s = RequestUtils.getRequestUtils().postData(request);
        JSONObject jsonObject = JSON.parseObject(s);
        afterSalesService.afterSalesReturnPay(jsonObject, employee);
        ResponseUtil.sendSuccessResponse(request, response, s);
    }

    /**
     * 导出
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/inventoryOut/orderFeeExport")
    public void orderFeeExport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            OutputStream outputStream = response.getOutputStream();
            response.reset();
            response.setHeader("Content-Type", "application/msexcel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("费用导出").getBytes("utf-8"),"iso8859-1") + ".xls");
            response.setHeader("Cache-Control", "max-age=0");
            WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
            WritableSheet sheet = workbook.createSheet("sheet1", 0);

            AfterSalesSearchOption afterSalesSearchOption = Request2ModelUtils.covert(AfterSalesSearchOption.class, request);
            SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(afterSalesSearchOption);
            List<OrderFeeExtend> list = orderFeeService.orderFees(afterSalesSearchOption);

            int row = 0;
            Counter counter = new Counter();
            sheet.addCell(new Label(counter.getN(), row, "订单号"));//
            sheet.addCell(new Label(counter.getN(), row, "客户姓名"));//
            sheet.addCell(new Label(counter.getN(), row, "客户电话"));//
            sheet.addCell(new Label(counter.getN(), row, "供应商"));//
            sheet.addCell(new Label(counter.getN(), row, "费用类型"));//
            sheet.addCell(new Label(counter.getN(), row, "工人师傅"));//
            sheet.addCell(new Label(counter.getN(), row, "供应商费用"));//
            sheet.addCell(new Label(counter.getN(), row, "凤凰城费用"));//
            sheet.addCell(new Label(counter.getN(), row, "客户费用"));//
            sheet.addCell(new Label(counter.getN(), row, "备注"));//
            sheet.addCell(new Label(counter.getN(), row, "时间"));//
            sheet.addCell(new Label(counter.getN(), row, "类型"));//

            for (int i = 0; i < list.size(); i++) {
                OrderFeeExtend orderFeeExtend = list.get(i);
                row++;
                counter.reset();
                sheet.addCell(new Label(counter.getN(), row, orderFeeExtend.getOrder().getOrder_code()));//
                sheet.addCell(new Label(counter.getN(), row, orderFeeExtend.getOrder().getCustomer_name()));//
                sheet.addCell(new Label(counter.getN(), row, orderFeeExtend.getOrder().getCustomer_phone_number()));//
                sheet.addCell(new Label(counter.getN(), row, orderFeeExtend.getSupplier().getSupplier_name()));//
                sheet.addCell(new Label(counter.getN(), row, orderFeeExtend.getOrderFeeType().getOrder_fee_type_name()));//
                String worker_name = "";
                if (orderFeeExtend.getWorker() != null) {
                    worker_name = orderFeeExtend.getWorker().getWorker_name();
                }
                sheet.addCell(new Label(counter.getN(), row, worker_name));//
                sheet.addCell(new Number(counter.getN(), row, orderFeeExtend.getSupplier_fee().doubleValue()));//
                sheet.addCell(new Number(counter.getN(), row, orderFeeExtend.getFhc_fee().doubleValue()));//
                sheet.addCell(new Number(counter.getN(), row, orderFeeExtend.getCustomer_fee()==null?0:orderFeeExtend.getCustomer_fee().doubleValue()));//
                if (orderFeeExtend.getOrder_fee_source() == OrderFeeComponents.FEE_SOURCE_SAMPLE_FIX) {
                    sheet.addCell(new Label(counter.getN(), row, orderFeeExtend.getSampleFixRemark()));//
                }else {
                    sheet.addCell(new Label(counter.getN(), row, orderFeeExtend.getOrder_fee_remark()));//
                }
                sheet.addCell(new Label(counter.getN(), row, DateUtil.format(orderFeeExtend.getRecord_date(), "yyyy-MM-dd")));//
                Integer order_fee_source = orderFeeExtend.getOrder_fee_source();
                String feeSource = feeSource(order_fee_source);
                sheet.addCell(new Label(counter.getN(), row, feeSource));//
            }
            ResponseUtil.exportResponse(request, response, workbook);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String feeSource(int order_fee_source) {
        String feeSource = "";
        switch (order_fee_source) {
            case OrderFeeComponents.FEE_SOURCE_INVENTORY_OUT:feeSource = "送回结束费用";break;//
            case OrderFeeComponents.FEE_SOURCE_AFTER_SALES:feeSource = "售后维修费用";break;//
            case OrderFeeComponents.FEE_SOURCE_SAMPLE_FIX:feeSource = "场地维修费用";break;//
            case OrderFeeComponents.FEE_SOURCE_ORDER_RETURN_DEDUCTION:feeSource = "退换货费用";break;//
            default: break;
        }
        return feeSource;
    }

    /**
     * 状态修复
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/afterSales/afterSalesFeeStatus")
    public void afterSalesFeeStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int after_sales_id = Integer.parseInt(request.getParameter("after_sales_id"));
        AfterSalesExtend afterSales = afterSalesService.getAfterSalesById(after_sales_id);
        if (afterSales.getResolve_date() == null) {//未添加费用
            afterSales.setAfter_sales_fee_status(-1);
        }else {
            OrderFeeSearchOption orderFeeSearchOption = new OrderFeeSearchOption();
            orderFeeSearchOption.setAfter_sales_id(after_sales_id);
            List<OrderFeeExtend> list = OrderFeeComponents.orderFeeList(orderFeeSearchOption);
            if (list != null && list.size() > 0) {
                String orderFeeIds = OrderFeeComponents.orderFeeIds1(list);
                afterSales.setOrder_fee_ids(orderFeeIds);
            }
            afterSales.setAfter_sales_fee_status(1);
        }
        afterSalesService.updateAfterSales(afterSales);
        ResponseUtil.sendSuccessResponse(request, response);
    }
}
