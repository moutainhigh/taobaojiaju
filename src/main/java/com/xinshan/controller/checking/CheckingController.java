package com.xinshan.controller.checking;

import com.xinshan.components.orderFee.OrderFeeComponents;
import com.xinshan.model.*;
import com.xinshan.model.extend.checking.CheckingDetailExtend;
import com.xinshan.model.extend.checking.CheckingExtend;
import com.xinshan.model.extend.settlement.SettlementExtend;
import com.xinshan.service.CheckingService;
import com.xinshan.service.SettlementService;
import com.xinshan.utils.*;
import com.xinshan.utils.constant.checking.ConstantCheckingType;
import com.xinshan.pojo.checking.CheckingSearchOptions;
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
import java.util.List;

/**
 * Created by mxt on 17-2-20.
 */
@Controller
public class CheckingController {
    @Autowired
    private CheckingService checkingService;
    @Autowired
    private SettlementService settlementService;

    /**
     * 初始化对账单信息
     * 根据结算生成对账单
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/settlement/checking/createChecking")
    public void init(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        SettlementSearchOption settlementSearchOption = Request2ModelUtils.covert(SettlementSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(settlementSearchOption);
        String check_remark = request.getParameter("check_remark");
        settlementSearchOption.setLimit(null);
        settlementSearchOption.setStart(null);
        settlementSearchOption.setChecking_status(0);
        String settlement_ids = settlementSearchOption.getSettlement_ids();
        if (settlement_ids != null) {
            settlementSearchOption.setSettlementIdList(SplitUtils.splitToList(settlement_ids, ","));
        }
        List<SettlementExtend> list = settlementService.settlementList(settlementSearchOption);
        checkingService.createChecking(list, employee, check_remark);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 对账单核对
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/settlement/checking/check")
    public void check(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int checking_id = Integer.parseInt(request.getParameter("checking_id"));
        CheckingExtend checking = checkingService.getCheckingById(checking_id);
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        checking.setCheck_date(DateUtil.currentDate());
        checking.setCheck_status(1);
        checking.setCheck_employee_code(employee.getEmployee_code());
        checking.setCheck_employee_name(employee.getEmployee_name());
        checkingService.updateChecking(checking);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 对账单列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/settlement/checking/checkingList")
    public void checkingList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CheckingSearchOptions checkingSearchOptions = Request2ModelUtils.covert(CheckingSearchOptions.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(checkingSearchOptions);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(checkingSearchOptions);
        List<CheckingExtend> list = checkingService.checkingList(checkingSearchOptions);
        Integer count = checkingService.countChecking(checkingSearchOptions);
        ResponseUtil.sendListResponse(request, response, list, count, checkingSearchOptions);
    }

    @RequestMapping("/settlement/checking/checkingDetailList")
    public void checkingDetails(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CheckingSearchOptions checkingSearchOptions = Request2ModelUtils.covert(CheckingSearchOptions.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(checkingSearchOptions);
        List<CheckingDetailExtend> list = checkingService.checkingDetails(checkingSearchOptions);
        Integer count = checkingService.countCheckingDetails(checkingSearchOptions);
        ResponseUtil.sendListResponse(request, response, list, count, checkingSearchOptions);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/settlement/checking/checkingPrint")
    public void checkingPrint(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CheckingSearchOptions checkingSearchOptions = Request2ModelUtils.covert(CheckingSearchOptions.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(checkingSearchOptions);
        List<CheckingExtend> list = checkingService.checkingList(checkingSearchOptions);
        Integer count = checkingService.countChecking(checkingSearchOptions);
        ResponseUtil.sendListResponse(request, response, list, count, checkingSearchOptions);
    }

    /**
     * 导出
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/settlement/checking/checkingExport")
    public void checkingExport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            OutputStream outputStream = response.getOutputStream();
            response.reset();
            response.setHeader("Content-Type", "application/msexcel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("对账单导出").getBytes("utf-8"),"iso8859-1") + ".xls");
            response.setHeader("Cache-Control", "max-age=0");
            WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
            WritableSheet sheet = workbook.createSheet("sheet1", 0);

            int checking_id = Integer.parseInt(request.getParameter("checking_id"));
            CheckingExtend checking = checkingService.getCheckingById(checking_id);
            switch (checking.getChecking_type()) {
                case ConstantCheckingType.CHECKING_TYPE_COMMODITY:
                    checkingCommodityExport(sheet, checking);
                    break;
                case ConstantCheckingType.CHECKING_TYPE_AFTER_SALES:
                    checkingAfterSalesExport(sheet, checking);
                    break;
                case ConstantCheckingType.CHECKING_TYPE_SAMPLE_FIX:

                    break;
                case ConstantCheckingType.CHECKING_TYPE_RETURN_COMMODITY:
                    checkingReturnCommodityExport(sheet, checking);
                    break;
                case ConstantCheckingType.CHECKING_TYPE_GIFT:
                    giftCommodityExport(sheet, checking);
                    break;
                case ConstantCheckingType.CHECKING_TYPE_GIFT_RETURN:
                    giftReturnCommodityExport(sheet, checking);
                    break;
                case ConstantCheckingType.CHECKING_TYPE_INVENTORY_IN:
                    checkingCommodityExport(sheet, checking);
                    break;
                case ConstantCheckingType.CHECKING_TYPE_GIFT_OUT:
                    checkingCommodityExport(sheet, checking);
                    break;
                case ConstantCheckingType.CHECKING_TYPE_PRE_SALE_ORDER_RETURN:
                    checkingAfterSalesExport(sheet, checking);
                    break;
                default:
                    break;
            }

            ResponseUtil.exportResponse(request, response, workbook);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 赠品
     * @param sheet
     * @param checking
     * @throws WriteException
     */
    private void giftCommodityExport(WritableSheet sheet, CheckingExtend checking) throws WriteException {
        List<CheckingDetailExtend> details = checkingService.checkingDetails(checking.getChecking_id());
        Counter row = new Counter();
        Counter col = new Counter();
        checkCommodityExport(sheet, details, row, col, checking);
    }

    /**
     * 赠品退还
     * @param sheet
     * @param checking
     * @throws WriteException
     */
    private void giftReturnCommodityExport(WritableSheet sheet, CheckingExtend checking) throws WriteException {
        List<CheckingDetailExtend> details = checkingService.checkingDetails(checking.getChecking_id());
        Counter row = new Counter();
        Counter col = new Counter();
        checkCommodityExport(sheet, details, row, col, checking);
    }
    /**
     * 退货
     * @param sheet
     * @param checking
     * @throws WriteException
     */
    private void checkingReturnCommodityExport(WritableSheet sheet, CheckingExtend checking) throws WriteException {
        List<CheckingDetailExtend> details = checkingService.checkingDetails(checking.getChecking_id());
        Counter row = new Counter();
        Counter col = new Counter();
        checkCommodityExport(sheet,details, row, col, checking);
    }

    private void checkCommodityExport(WritableSheet sheet, List<CheckingDetailExtend> details, Counter row , Counter col, CheckingExtend checking) throws WriteException {
        int n = row.getN();
        sheet.addCell(new Label(col.getN(), n, "订单日期"));//
        sheet.addCell(new Label(col.getN(), n, "商品名称"));//
        sheet.addCell(new Label(col.getN(), n, "型号"));//
        sheet.addCell(new Label(col.getN(), n, "数量"));//
        sheet.addCell(new Label(col.getN(), n, "单价"));//
        sheet.addCell(new Label(col.getN(), n, "金额"));//
        sheet.addCell(new Label(col.getN(), n, "客户姓名"));//
        sheet.addCell(new Label(col.getN(), n, "订单号"));//
        for (int j = 0; j < details.size(); j++) {
            CheckingDetailExtend checkingDetail = details.get(j);
            if (!checkingDetail.getChecking_detail_type().equals(ConstantCheckingType.CHECKING_DETAIL_TYPE_COMMODITY)) {//商品
                continue;
            }
            n = row.getN();
            col.reset();
            if (checkingDetail.getOrder() != null && checkingDetail.getOrder().getOrder_date() != null) {
                sheet.addCell(new Label(col.getN(), n, DateUtil.format(checkingDetail.getOrder().getOrder_date(), "yyyy-MM-dd")));//
            }else {
                sheet.addCell(new Label(col.getN(), n, DateUtil.format(checking.getCreate_date(), "yyyy-MM-dd")));//
            }
            sheet.addCell(new Label(col.getN(), n, checkingDetail.getCommodity().getCommodity_name()));//
            sheet.addCell(new Label(col.getN(), n, checkingDetail.getCommodity().getCommodity_code()));//
            sheet.addCell(new Number(col.getN(), n, checkingDetail.getCommodity_num()));//
            sheet.addCell(new Number(col.getN(), n, checkingDetail.getPurchase_unit_price().doubleValue()));//
            sheet.addCell(new Number(col.getN(), n, checkingDetail.getPurchase_total_price().doubleValue()));//
            if (checkingDetail.getOrder() != null) {
                sheet.addCell(new Label(col.getN(), n, checkingDetail.getOrder().getCustomer_name()));//
                sheet.addCell(new Label(col.getN(), n, checkingDetail.getOrder().getOrder_code()));//
            }else {
                sheet.addCell(new Label(col.getN(), n, ""));//
                sheet.addCell(new Label(col.getN(), n, ""));//
            }
        }
    }

    private void checkingCommodityExport(WritableSheet sheet, CheckingExtend checking) throws WriteException {
        List<CheckingDetailExtend> details = checkingService.checkingDetails(checking.getChecking_id());
        Counter row = new Counter();
        Counter col = new Counter();

        checkCommodityExport(sheet, details, row, col, checking);

        row.getN();
        row.getN();
        int n = row.getN();
        sheet.addCell(new Label(col.getN(), n, "费用类型"));//
        sheet.addCell(new Label(col.getN(), n, "供应商费用"));//
        sheet.addCell(new Label(col.getN(), n, "凤凰城费用"));//
        sheet.addCell(new Label(col.getN(), n, "客户姓名"));//
        sheet.addCell(new Label(col.getN(), n, "订单号"));//

        writeFee(sheet, row, col, details);
    }

    /**
     * 售后费用
     * @param sheet
     * @param checking
     * @throws WriteException
     */
    private void checkingAfterSalesExport(WritableSheet sheet,CheckingExtend checking) throws WriteException {
        List<CheckingDetailExtend> details = checkingService.checkingDetails(checking.getChecking_id());
        Counter row = new Counter();
        Counter col = new Counter();
        int n = row.getN();
        sheet.addCell(new Label(col.getN(), n, "费用类型"));//
        sheet.addCell(new Label(col.getN(), n, "供应商费用"));//
        sheet.addCell(new Label(col.getN(), n, "凤凰城费用"));//
        sheet.addCell(new Label(col.getN(), n, "客户姓名"));//
        sheet.addCell(new Label(col.getN(), n, "订单号"));//
        writeFee(sheet, row, col, details);
    }
    private void writeFee(WritableSheet sheet, Counter row, Counter col, List<CheckingDetailExtend> details) throws WriteException {
        for (int j = 0; j < details.size(); j++) {
            CheckingDetailExtend checkingDetail = details.get(j);
            if (checkingDetail.getChecking_detail_type().equals(ConstantCheckingType.CHECKING_DETAIL_TYPE_COMMODITY)) {//商品
                continue;
            }
            int n = row.getN();
            int fee_type = checkingDetail.getFee_type();
            col.reset();
            OrderFeeType orderFeeType = OrderFeeComponents.getOrderFeeTypeMap().get(fee_type);
            sheet.addCell(new Label(col.getN(), n, orderFeeType == null ? "":orderFeeType.getOrder_fee_type_name()));//
            sheet.addCell(new Number(col.getN(), n, checkingDetail.getSupplier_fee().doubleValue()));//
            sheet.addCell(new Number(col.getN(), n, checkingDetail.getFhc_fee().doubleValue()));//
            sheet.addCell(new Label(col.getN(), n, checkingDetail.getOrder().getCustomer_name()));//
            sheet.addCell(new Label(col.getN(), n, checkingDetail.getOrder().getOrder_code()));//
        }
    }

    /**
     * 对账单导出
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/settlement/checking/checkingExport1")
    public void checkingExport1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            OutputStream outputStream = response.getOutputStream();
            response.reset();
            response.setHeader("Content-Type", "application/msexcel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("对账单导出").getBytes("utf-8"),"iso8859-1") + ".xls");
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

            CheckingSearchOptions checkingSearchOptions = Request2ModelUtils.covert(CheckingSearchOptions.class, request);
            SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(checkingSearchOptions);
            List<CheckingExtend> list = checkingService.checkingEasyList(checkingSearchOptions);
            Counter counter = new Counter();
            int row = 0;
            sheet.addCell(new Label(counter.getN(), row, "对账单号", header_format));//
            sheet.addCell(new Label(counter.getN(), row, "供应商", header_format));//
            sheet.addCell(new Label(counter.getN(), row, "委托人", header_format));//
            sheet.addCell(new Label(counter.getN(), row, "执行人", header_format));//
            sheet.addCell(new Label(counter.getN(), row, "执行时间", header_format));//
            sheet.addCell(new Label(counter.getN(), row, "来源", header_format));//
            sheet.addCell(new Label(counter.getN(), row, "商品货号", header_format));//
            sheet.addCell(new Label(counter.getN(), row, "商品名称", header_format));//
            sheet.addCell(new Label(counter.getN(), row, "数量", header_format));//
            sheet.addCell(new Label(counter.getN(), row, "采购单价", header_format));//
            sheet.addCell(new Label(counter.getN(), row, "采购总价", header_format));//
            sheet.addCell(new Label(counter.getN(), row, "销售单价", header_format));//
            sheet.addCell(new Label(counter.getN(), row, "改版费", header_format));//
            sheet.addCell(new Label(counter.getN(), row, "销售总价", header_format));//
            sheet.addCell(new Label(counter.getN(), row, "客户姓名", header_format));//
            sheet.addCell(new Label(counter.getN(), row, "客户电话", header_format));//
            sheet.addCell(new Label(counter.getN(), row, "订单号", header_format));//
            sheet.addCell(new Label(counter.getN(), row, "状态", header_format));//
            row++;
            for (int i = 0; i < list.size(); i++) {
                CheckingExtend checkingExtend = list.get(i);
                List<CheckingDetailExtend> checkingDetails = checkingService.checkingDetails(checkingExtend.getChecking_id());
                if (checkingDetails == null) {
                    continue;
                }
                for (int j = 0; j < checkingDetails.size(); j++) {
                    CheckingDetailExtend checkingDetailExtend = checkingDetails.get(j);
                    if (checkingDetailExtend.getChecking_detail_type().equals(ConstantCheckingType.CHECKING_DETAIL_TYPE_COMMODITY)) {//商品
                        counter.reset();
                        sheet.addCell(new Label(counter.getN(), row, checkingExtend.getChecking_code(), header_format));//
                        String supplier_name = "";
                        String contacts = checkingExtend.getSupplier_contacts();
                        Supplier supplier = checkingExtend.getSupplier();
                        if (supplier != null && supplier.getSupplier_name() != null) {
                            supplier_name = supplier.getSupplier_name();
                            if (contacts == null || contacts.equals("")) {
                                contacts = supplier.getContacts();
                            }
                        }
                        sheet.addCell(new Label(counter.getN(), row, supplier_name, header_format));//
                        sheet.addCell(new Label(counter.getN(), row, contacts, header_format));//
                        sheet.addCell(new Label(counter.getN(), row, checkingExtend.getCreate_employee_name(), header_format));//
                        sheet.addCell(new Label(counter.getN(), row, DateUtil.format(checkingExtend.getCreate_date(), "yyyy-MM-dd"), header_format));//
                        sheet.addCell(new Label(counter.getN(), row, checkingType(checkingExtend.getChecking_type()), header_format));//
                        sheet.addCell(new Label(counter.getN(), row, checkingDetailExtend.getCommodity().getCommodity_name(), header_format));//
                        sheet.addCell(new Label(counter.getN(), row, checkingDetailExtend.getCommodity().getCommodity_code(), header_format));//
                        sheet.addCell(new Number(counter.getN(), row, checkingDetailExtend.getCommodity_num(), header_format));//
                        sheet.addCell(new Number(counter.getN(), row, checkingDetailExtend.getPurchase_unit_price().doubleValue(), header_format));//
                        sheet.addCell(new Number(counter.getN(), row, checkingDetailExtend.getPurchase_total_price().doubleValue(), header_format));//
                        sheet.addCell(new Number(counter.getN(), row, checkingDetailExtend.getBargain_price().doubleValue(), header_format));//
                        sheet.addCell(new Number(counter.getN(), row, checkingDetailExtend.getRevision_fee().doubleValue(), header_format));//
                        sheet.addCell(new Number(counter.getN(), row, checkingDetailExtend.getCommodity_total_price().doubleValue(), header_format));//
                        Order order = checkingDetailExtend.getOrder();
                        if (order != null) {
                            sheet.addCell(new Label(counter.getN(), row, order.getCustomer_name(), header_format));//
                            sheet.addCell(new Label(counter.getN(), row, order.getCustomer_phone_number(), header_format));//
                            sheet.addCell(new Label(counter.getN(), row, order.getOrder_code(), header_format));//
                        }else {
                            sheet.addCell(new Label(counter.getN(), row, "", header_format));//
                            sheet.addCell(new Label(counter.getN(), row, "", header_format));//
                            sheet.addCell(new Label(counter.getN(), row, "", header_format));//
                        }
                        if (checkingExtend.getPayment_status() == 0) {
                            sheet.addCell(new Label(counter.getN(), row, "未付款", header_format));//
                        }else {
                            sheet.addCell(new Label(counter.getN(), row, "已付款", header_format));//
                        }
                        row++;
                    }
                }
            }
            ResponseUtil.exportResponse(request, response, workbook);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String checkingType(int checking_type) {
        String s = "";
        switch (checking_type) {
            case ConstantCheckingType.CHECKING_TYPE_COMMODITY:
                s = "出库";
                break;
            case ConstantCheckingType.CHECKING_TYPE_AFTER_SALES:
                s = "售后";
                break;
            case ConstantCheckingType.CHECKING_TYPE_SAMPLE_FIX:
                s = "场地维修";
                break;
            case ConstantCheckingType.CHECKING_TYPE_RETURN_COMMODITY:
                s = "退换货";
                break;
            case ConstantCheckingType.CHECKING_TYPE_GIFT:
                s = "赠品";
                break;
            case ConstantCheckingType.CHECKING_TYPE_GIFT_RETURN:
                s = "赠品退还";
                break;
            case ConstantCheckingType.CHECKING_TYPE_INVENTORY_IN:
                s = "广东馆入库";
                break;
            case ConstantCheckingType.CHECKING_TYPE_GIFT_OUT:
                s = "赠品出库";
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
    @RequestMapping("/settlement/checking/reset")
    public void checkingReset(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int checking_id = Integer.parseInt(request.getParameter("checking_id"));
        CheckingExtend checking = checkingService.getCheckingById(checking_id);
        /*if (checking.getPayment_status() == 1) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "已生成付款单"));
            return;
        }*/
        checkingService.checkingReset(checking, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }
}
