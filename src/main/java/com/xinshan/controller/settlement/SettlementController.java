package com.xinshan.controller.settlement;

import com.xinshan.model.*;
import com.xinshan.model.extend.afterSales.AfterSalesCommodityExtend;
import com.xinshan.model.extend.orderFee.OrderFeeExtend;
import com.xinshan.model.extend.settlement.SettlementExtend;
import com.xinshan.model.extend.settlement.SettlementInventoryOutCommodity;
import com.xinshan.service.SettlementService;
import com.xinshan.utils.*;
import com.xinshan.pojo.settlement.SettlementSearchOption;
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
import java.util.List;
import java.util.Map;

/**
 * Created by mxt on 16-11-23.
 * 结算
 */
@Controller
public class SettlementController {
    @Autowired
    private SettlementService settlementService;

    /**
     * 编辑结算采购价
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/settlement/settlementPurchase")
    public void createSettlementCommodityPurchase(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee  = RequestUtils.getRequestUtils().getEmployeeCode(request);
        SettlementCommodityPurchase settlementCommodityPurchase = Request2ModelUtils.covert(SettlementCommodityPurchase.class, request);
        settlementService.createSettlementCommodityPurchase(settlementCommodityPurchase, employee);
        ResponseUtil.sendSuccessResponse(request, response);
        settlementService.settlementReset(settlementCommodityPurchase.getSettlement_id(), employee);
    }

    @RequestMapping("/order/settlement/settlementList")
    public void settlementList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SettlementSearchOption settlementSearchOption = Request2ModelUtils.covert(SettlementSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(settlementSearchOption);
        List<SettlementExtend> list = settlementService.settlementList(settlementSearchOption);
        Integer count = settlementService.countSettlement(settlementSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, settlementSearchOption);
    }

    @RequestMapping("/order/settlement/settlementStatistics")
    public void settlementStatistics(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SettlementSearchOption settlementSearchOption = Request2ModelUtils.covert(SettlementSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(settlementSearchOption);
        Map map = settlementService.settlementStatistics(settlementSearchOption);
        ResponseUtil.sendSuccessResponse(request, response, map);
    }

    /**
     * 结算
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/settlement/confirmSettlement")
    public void confirmSettlement(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int settlement_id = Integer.parseInt(request.getParameter("settlement_id"));
        String remark = request.getParameter("remark");
        BigDecimal settlement_amount = new BigDecimal(request.getParameter("settlement_amount"));
        Settlement settlement = settlementService.getSettlementById(settlement_id);
        settlement.setSettlement_status(1);
        settlementService.createSettlementHistory(employee, settlement, settlement_amount, remark);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 结算信息初始化
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/settlement/settlementInit")
    public void purchaseSettlement(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //settlementService.settlementInit();
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 重新计算结算信息
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/settlement/settlementReset")
    public void settlementReset(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int settlement_id = Integer.parseInt(request.getParameter("settlement_id"));
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        settlementService.settlementReset(settlement_id, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    @RequestMapping("/order/settlement/verifyStatus")
    public void settlementVerify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int settlement_id = Integer.parseInt(request.getParameter("settlement_id"));
        int verify_status = Integer.parseInt(request.getParameter("verify_status"));
        Settlement settlement = settlementService.getSettlementById(settlement_id);
        settlement.setVerify_status(verify_status);
        settlement.setVerify_date(DateUtil.currentDate());
        settlement.setVerify_employee_code(employee.getEmployee_code());
        settlement.setVerify_employee_name(employee.getEmployee_name());
        settlementService.updateSettlementVerifyStatus(settlement);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    @RequestMapping("/order/settlement/settlementExport")
    public void settlementExport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SettlementSearchOption settlementSearchOption = Request2ModelUtils.covert(SettlementSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(settlementSearchOption);
        List<SettlementExtend> list = settlementService.settlementList(settlementSearchOption);
        try {
            OutputStream outputStream = response.getOutputStream();
            response.reset();
            response.setHeader("Content-Type", "application/msexcel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("结算导出").getBytes("utf-8"),"iso8859-1") + ".xls");
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
            sheet.addCell(new Label(0, row, "供应商", header_format));//
            sheet.addCell(new Label(1, row, "销售日期", header_format));//
            sheet.addCell(new Label(2, row, "产品名称", header_format));//
            sheet.addCell(new Label(3, row, "编号", header_format));//
            sheet.addCell(new Label(4, row, "数量", header_format));//
            sheet.addCell(new Label(5, row, "单价", header_format));//
            sheet.addCell(new Label(6, row, "金额", header_format));//
            sheet.addCell(new Label(7, row, "拆卸安装费等", header_format));//
            sheet.addCell(new Label(8, row, "销售订单号", header_format));//
            sheet.addCell(new Label(9, row, "手工单号", header_format));//
            sheet.addCell(new Label(10, row, "客户姓名", header_format));//
            sheet.addCell(new Label(11, row, "来源", header_format));//
            row++;
            for (int i = 0; i < list.size(); i++) {
                SettlementExtend settlement = list.get(i);
                Supplier supplier = settlement.getSupplier();
                Order order = settlement.getOrder();
                List<OrderFeeExtend> orderFees = settlement.getOrderFees();
                List<SettlementInventoryOutCommodity> settlementInventoryOutCommodities = settlement.getSettlementCommodities();
                List<AfterSalesCommodityExtend> afterSalesCommodities = settlement.getAfterSalesCommodities();
                BigDecimal fee = new BigDecimal("0");
                for (int j = 0; j < orderFees.size(); j++) {
                    OrderFee orderFee = orderFees.get(j);
                    fee = fee.add(orderFee.getSupplier_fee());
                }
                if (settlementInventoryOutCommodities != null) {
                    for (int j = 0; j < settlementInventoryOutCommodities.size(); j++) {
                        SettlementInventoryOutCommodity settlementInventoryOutCommodity = settlementInventoryOutCommodities.get(j);
                        Commodity commodity = settlementInventoryOutCommodity.getCommodity();

                        sheet.addCell(new Label(0, row, supplier.getSupplier_name(), header_format));//
                        sheet.addCell(new Label(1, row, DateUtil.format(order.getRecord_date(), "yyyy-MM-dd"), header_format));//
                        sheet.addCell(new Label(2, row, commodity.getCommodity_name(), header_format));//
                        sheet.addCell(new Label(3, row, commodity.getCommodity_code(), header_format));//

                        int num = settlementInventoryOutCommodity.getInventory_history_num();//商品数量
                        BigDecimal unit_price = new BigDecimal("0");
                        if (settlementInventoryOutCommodity.getPurchaseCommodity().getPurchase_id() == null) {//四川馆商品或者样品
                            OrderCommodity orderCommodity = settlementInventoryOutCommodity.getOrderCommodity();
                            num = orderCommodity.getCommodity_num();
                            unit_price = commodity.getPurchase_price();
                        }else {
                            unit_price = settlementInventoryOutCommodity.getPurchaseCommodity().getPurchase_unit_price();
                        }
                        if (unit_price == null) {
                            unit_price = new BigDecimal("0");
                        }

                        BigDecimal total_price = unit_price.multiply(new BigDecimal(num));

                        sheet.addCell(new Number(4, row, num, header_format));//
                        sheet.addCell(new Number(5, row, unit_price.doubleValue(), header_format));//
                        sheet.addCell(new Number(6, row, total_price.doubleValue(), header_format));//
                        if (j == 0) {
                            sheet.addCell(new Number(7, row, fee.doubleValue(), header_format));//
                        }else {
                            sheet.addCell(new Number(7, row, 0, header_format));//
                        }
                        sheet.addCell(new Label(8, row, order.getOrder_code(), header_format));//
                        sheet.addCell(new Label(9, row, order.getOrder_paper_code(), header_format));//
                        sheet.addCell(new Label(10, row, order.getCustomer_name(), header_format));//
                        sheet.addCell(new Label(11, row, "出库", header_format));//
                        row++;
                    }
                }

                if (afterSalesCommodities != null) {
                    for (int j = 0; j < afterSalesCommodities.size(); j++) {
                        AfterSalesCommodityExtend afterSalesCommodity = afterSalesCommodities.get(j);
                        Commodity commodity = afterSalesCommodity.getCommodity();
                        sheet.addCell(new Label(0, row, supplier.getSupplier_name(), header_format));//
                        sheet.addCell(new Label(1, row, DateUtil.format(order.getRecord_date(), "yyyy-MM-dd"), header_format));//
                        sheet.addCell(new Label(2, row, commodity.getCommodity_name(), header_format));//
                        sheet.addCell(new Label(3, row, commodity.getCommodity_code(), header_format));//

                        sheet.addCell(new Number(4, row, 0, header_format));//
                        sheet.addCell(new Number(5, row, 0, header_format));//
                        sheet.addCell(new Number(6, row, 0, header_format));//
                        if (j == 0) {
                            sheet.addCell(new Number(7, row, fee.doubleValue(), header_format));//
                        }else {
                            sheet.addCell(new Number(7, row, 0, header_format));//
                        }
                        sheet.addCell(new Label(8, row, order.getOrder_code(), header_format));//
                        sheet.addCell(new Label(9, row, order.getOrder_paper_code(), header_format));//
                        sheet.addCell(new Label(10, row, order.getCustomer_name(), header_format));//
                        sheet.addCell(new Label(11, row, "售后", header_format));//
                        row++;
                    }
                }
            }
            ResponseUtil.exportResponse(request, response, workbook);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
