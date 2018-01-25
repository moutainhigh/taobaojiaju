package com.xinshan.controller.gift;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.supplier.SupplierComponents;
import com.xinshan.model.*;
import com.xinshan.model.extend.gift.GiftCommodityExtend;
import com.xinshan.model.extend.gift.GiftExtend;
import com.xinshan.pojo.gift.GiftSearchOption;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mxt on 17-4-15.
 */
@Controller
public class GiftController {
    @Autowired
    private GiftService giftService;
    @Autowired
    private InventoryOutService inventoryOutService;
    @Autowired
    private SettlementService settlementService;
    @Autowired
    private PurchaseService purchaseService;
    /**
     * 赠品列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/gift/giftList")
    public void giftList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GiftSearchOption giftSearchOption = Request2ModelUtils.covert(GiftSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(giftSearchOption);
        List<Integer> giftIds = giftService.giftIds(giftSearchOption);
        Integer count = giftService.countGift(giftSearchOption);
        if (giftIds == null || giftIds.size() == 0) {
            ResponseUtil.sendListResponse(request, response, new ArrayList(), count, giftSearchOption);
            return;
        }
        giftSearchOption.setGiftIds(giftIds);
        List<GiftExtend> list = giftService.giftList(giftSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, giftSearchOption);
    }

    /**
     * 礼品删除
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/gift/giftEnable")
    public void giftEnable(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int gift_id = Integer.parseInt(request.getParameter("gift_id"));
        int gift_enable = Integer.parseInt(request.getParameter("gift_enable"));
        GiftExtend gift = giftService.getGiftById(gift_id);
        gift.setGift_enable(gift_enable);
        giftService.updateGift(gift);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    @RequestMapping("/activity/gift/createGift")
    public void createGift(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String postData = RequestUtils.getRequestUtils().postData(request);
        GiftExtend giftExtend = JSONObject.parseObject(postData, GiftExtend.class);
        giftService.createGift(giftExtend, employee);
        ResponseUtil.sendSuccessResponse(request, response, postData);
    }

    /**
     * 礼品出库
     * @param request
     * @param response
     * @throws IOException
     */
    /*@RequestMapping("/activity/gift/giftOut")
    public void giftInventoryOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int gift_id = Integer.parseInt(request.getParameter("gift_id"));
        GiftExtend giftExtend = giftService.getGiftById(gift_id);
        InventoryHistory inventoryHistory = inventoryOutService.giftInventoryHistory(giftExtend, employee);
        inventoryOutService.giftConfirm(inventoryHistory.getInventory_history_id());
        ResponseUtil.sendSuccessResponse(request, response, inventoryHistory);
    }*/

    /**
     * 礼品结算
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/gift/giftSettlement")
    public void giftSettlement(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int gift_id = Integer.parseInt(request.getParameter("gift_id"));
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        settlementService.createGiftSettlement(gift_id, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 礼品采购
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/gift/giftPurchase")
    public void giftPurchase(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int gift_id = Integer.parseInt(request.getParameter("gift_id"));
        String purchase_remark = request.getParameter("purchase_remark");
        Date estimate_arrival_date = DateUtil.stringToDate(request.getParameter("estimate_arrival_date"));
        GiftExtend gift = giftService.getGiftById(gift_id);
        if (gift.getGift_purchase_status() == 1) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "已经采购"));
            return;
        }
        purchaseService.createPurchase(gift, purchase_remark, estimate_arrival_date, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 礼品报表
     * @param response
     * @param request
     * @throws IOException
     */
    @RequestMapping("/activity/gift/giftReport")
    public void giftReport(HttpServletResponse response, HttpServletRequest request) throws IOException {
        GiftSearchOption giftSearchOption = Request2ModelUtils.covert(GiftSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(giftSearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(giftSearchOption);
        List<GiftCommodityExtend> giftCommodityExtends = giftService.giftCommodities(giftSearchOption);
        Integer count = giftService.countGiftCommodities(giftSearchOption);
        ResponseUtil.sendListResponse(request, response, giftCommodityExtends, count, giftSearchOption);
    }

    /**
     * 赠品报表导出
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/gift/giftReportExport")
    public void giftReportExport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GiftSearchOption giftSearchOption = Request2ModelUtils.covert(GiftSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(giftSearchOption);
        List<GiftCommodityExtend> list = giftService.giftCommodities(giftSearchOption);
        try {
            OutputStream outputStream = response.getOutputStream();
            response.reset();
            response.setHeader("Content-Type", "application/msexcel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("赠品报表导出").getBytes("utf-8"),"iso8859-1") + ".xls");
            response.setHeader("Cache-Control", "max-age=0");
            WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
            WritableSheet sheet = workbook.createSheet("sheet1", 0);
            int row = 0;
            Counter col = new Counter();
            sheet.addCell(new Label(col.getN(), row, "礼品单号"));//
            sheet.addCell(new Label(col.getN(), row, "订单号"));//
            sheet.addCell(new Label(col.getN(), row, "活动"));//
            sheet.addCell(new Label(col.getN(), row, "赠送时间"));//
            sheet.addCell(new Label(col.getN(), row, "客户姓名"));//
            sheet.addCell(new Label(col.getN(), row, "客户电话"));//
            sheet.addCell(new Label(col.getN(), row, "操作人"));//
            sheet.addCell(new Label(col.getN(), row, "货号"));//
            sheet.addCell(new Label(col.getN(), row, "名称"));//
            sheet.addCell(new Label(col.getN(), row, "供应商"));//
            sheet.addCell(new Label(col.getN(), row, "存放位置"));//
            sheet.addCell(new Label(col.getN(), row, "赠送数量"));//
            sheet.addCell(new Label(col.getN(), row, "备注"));//
            sheet.addCell(new Label(col.getN(), row, "类型"));//
            sheet.addCell(new Label(col.getN(), row, "承担费用"));//
            sheet.addCell(new Label(col.getN(), row, "备注"));//
            for (int i = 0; i < list.size(); i++) {
                col.reset();
                row++;
                GiftCommodityExtend giftCommodity = list.get(i);
                sheet.addCell(new Label(col.getN(), row, giftCommodity.getGift().getGift_code()));//
                String order_code = "";
                if (giftCommodity.getOrder() != null && giftCommodity.getOrder().getOrder_code() != null) {
                    order_code = giftCommodity.getOrder().getOrder_code();
                }
                sheet.addCell(new Label(col.getN(), row, order_code));//
                String activity_name = "";
                if (giftCommodity.getActivity() != null && giftCommodity.getActivity().getActivity_name() != null) {
                    activity_name = giftCommodity.getActivity().getActivity_name();
                }
                sheet.addCell(new Label(col.getN(), row, activity_name));//
                sheet.addCell(new Label(col.getN(), row, DateUtil.format(giftCommodity.getGift().getGift_create_date(), "yyyy-MM-dd")));//
                if (giftCommodity.getOrder() != null) {
                    sheet.addCell(new Label(col.getN(), row, giftCommodity.getOrder().getCustomer_name()));//
                    sheet.addCell(new Label(col.getN(), row, giftCommodity.getOrder().getCustomer_phone_number()));//
                }else {
                    User user = giftCommodity.getUser();
                    sheet.addCell(new Label(col.getN(), row, user.getUser_name()));//
                    sheet.addCell(new Label(col.getN(), row, user.getUser_phone()));//
                }
                sheet.addCell(new Label(col.getN(), row, giftCommodity.getGift().getGift_create_employee_name()));//
                sheet.addCell(new Label(col.getN(), row, giftCommodity.getCommodity().getCommodity_code()));//
                sheet.addCell(new Label(col.getN(), row, giftCommodity.getCommodity().getCommodity_name()));//
                sheet.addCell(new Label(col.getN(), row, giftCommodity.getSupplier().getSupplier_name()));//
                String store_name = "";
                if (giftCommodity.getCommodityStore() != null && giftCommodity.getCommodityStore().getStore_name() != null) {
                    store_name = giftCommodity.getCommodityStore().getStore_name();
                }
                sheet.addCell(new Label(col.getN(), row, store_name));//
                sheet.addCell(new Number(col.getN(), row, giftCommodity.getGift_commodity_num()));//
                sheet.addCell(new Label(col.getN(), row, giftCommodity.getGift_commodity_remark()));//
                //赠品类型，1订单赠品，2活动赠品
                String gift_type = "";
                if (giftCommodity.getGift().getGift_type() == 1) {
                    gift_type = "订单赠送（" + order_code + ")";
                }else if (giftCommodity.getGift().getGift_type() == 2) {
                    gift_type = "活动赠送（" + activity_name + ")";
                }
                sheet.addCell(new Label(col.getN(), row, gift_type));//
                //费用承担，1供应商承担，2凤凰城承担
                String expense_type = "凤凰城";
                if (giftCommodity.getGift().getExpense_type() == 1) {
                    Integer supplier_id = giftCommodity.getGift().getSupplier_id();
                    if (supplier_id != null) {
                        Supplier supplier = SupplierComponents.getSupplierMap().get(supplier_id);
                        if (supplier != null) {
                            expense_type = supplier.getSupplier_name();
                        }
                    }
                }
                sheet.addCell(new Label(col.getN(), row, expense_type));//
                sheet.addCell(new Label(col.getN(), row, giftCommodity.getGift().getGift_remark()));
            }
            ResponseUtil.exportResponse(request, response, workbook);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
