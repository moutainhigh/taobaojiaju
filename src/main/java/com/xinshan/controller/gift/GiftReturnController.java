package com.xinshan.controller.gift;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.supplier.SupplierComponents;
import com.xinshan.model.*;
import com.xinshan.model.extend.gift.GiftCommodityExtend;
import com.xinshan.model.extend.gift.GiftReturnCommodityExtend;
import com.xinshan.model.extend.gift.GiftReturnExtend;
import com.xinshan.pojo.gift.GiftSearchOption;
import com.xinshan.service.GiftReturnService;
import com.xinshan.service.SettlementService;
import com.xinshan.utils.*;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by mxt on 17-5-26.
 */
@Controller
public class GiftReturnController {
    @Autowired
    private GiftReturnService giftReturnService;
    @Autowired
    private SettlementService settlementService;

    /**
     * 添加
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/gift/giftReturn/createGiftReturn")
    public void createGiftReturn(HttpServletRequest request, HttpServletResponse response)throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String postData = RequestUtils.getRequestUtils().postData(request);
        GiftReturnExtend giftReturnExtend = JSONObject.parseObject(postData, GiftReturnExtend.class);
        giftReturnService.createGiftReturn(giftReturnExtend, employee);
        ResponseUtil.sendSuccessResponse(request, response, giftReturnExtend, postData);
    }

    /**
     * 列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/gift/giftReturn/giftReturnList")
    public void giftReturnList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GiftSearchOption giftSearchOption = Request2ModelUtils.covert(GiftSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(giftSearchOption);
        List<GiftReturnExtend> giftReturnExtends = giftReturnService.giftReturnExtends(giftSearchOption);
        Integer count = giftReturnService.count(giftSearchOption);
        ResponseUtil.sendListResponse(request, response, giftReturnExtends, count, giftSearchOption);
    }

    /**
     * 确认
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/gift/giftReturn/confirmGiftReturn")
    public void giftReturnConfirmIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int gift_return_id = Integer.parseInt(request.getParameter("gift_return_id"));
        int commodity_store_id = Integer.parseInt(request.getParameter("commodity_store_id"));
        giftReturnService.giftReturnInventoryIn(gift_return_id, commodity_store_id, employee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 退礼品报表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/gift/giftReturn/giftReturnReport")
    public void giftReturnReport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GiftSearchOption giftSearchOption = Request2ModelUtils.covert(GiftSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(giftSearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(giftSearchOption);
        List<GiftReturnCommodityExtend> giftReturnCommodities = giftReturnService.giftReturnCommodities(giftSearchOption);
        Integer count = giftReturnService.countGiftReturnCommodities(giftSearchOption);
        ResponseUtil.sendListResponse(request, response, giftReturnCommodities, count, giftSearchOption);
    }

    /**
     * 退礼品报表导出
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/gift/giftReturn/giftReturnReportExport")
    public void giftReturnReportExport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GiftSearchOption giftSearchOption = Request2ModelUtils.covert(GiftSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(giftSearchOption);
        List<GiftReturnCommodityExtend> list = giftReturnService.giftReturnCommodities(giftSearchOption);
        try {
            OutputStream outputStream = response.getOutputStream();
            response.reset();
            response.setHeader("Content-Type", "application/msexcel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("退赠品报表导出").getBytes("utf-8"),"iso8859-1") + ".xls");
            response.setHeader("Cache-Control", "max-age=0");
            WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
            WritableSheet sheet = workbook.createSheet("sheet1", 0);
            int row = 0;
            Counter col = new Counter();
            sheet.addCell(new Label(col.getN(), row, "礼品单号"));//
            sheet.addCell(new Label(col.getN(), row, "订单号"));//
            sheet.addCell(new Label(col.getN(), row, "活动"));//
            sheet.addCell(new Label(col.getN(), row, "退赠品时间"));//
            sheet.addCell(new Label(col.getN(), row, "客户姓名"));//
            sheet.addCell(new Label(col.getN(), row, "客户电话"));//
            sheet.addCell(new Label(col.getN(), row, "操作人"));//
            sheet.addCell(new Label(col.getN(), row, "货号"));//
            sheet.addCell(new Label(col.getN(), row, "名称"));//
            sheet.addCell(new Label(col.getN(), row, "供应商"));//
            sheet.addCell(new Label(col.getN(), row, "退赠品数量"));//
            sheet.addCell(new Label(col.getN(), row, "类型"));//
            sheet.addCell(new Label(col.getN(), row, "备注"));//
            for (int i = 0; i < list.size(); i++) {
                col.reset();
                row++;
                GiftReturnCommodityExtend giftReturnCommodity = list.get(i);
                sheet.addCell(new Label(col.getN(), row, giftReturnCommodity.getGift().getGift_code()));//
                String order_code = "";
                if (giftReturnCommodity.getOrder() != null && giftReturnCommodity.getOrder().getOrder_code() != null) {
                    order_code = giftReturnCommodity.getOrder().getOrder_code();
                }
                sheet.addCell(new Label(col.getN(), row, order_code));//
                String activity_name = "";
                if (giftReturnCommodity.getActivity() != null && giftReturnCommodity.getActivity().getActivity_name() != null) {
                    activity_name = giftReturnCommodity.getActivity().getActivity_name();
                }
                sheet.addCell(new Label(col.getN(), row, activity_name));//
                sheet.addCell(new Label(col.getN(), row, DateUtil.format(giftReturnCommodity.getGiftReturn().getGift_return_create_date(), "yyyy-MM-dd")));//
                if (giftReturnCommodity.getOrder() != null) {
                    sheet.addCell(new Label(col.getN(), row, giftReturnCommodity.getOrder().getCustomer_name()));//
                    sheet.addCell(new Label(col.getN(), row, giftReturnCommodity.getOrder().getCustomer_phone_number()));//
                }else {
                    User user = giftReturnCommodity.getUser();
                    sheet.addCell(new Label(col.getN(), row, user.getUser_name()));//
                    sheet.addCell(new Label(col.getN(), row, user.getUser_phone()));//
                }
                sheet.addCell(new Label(col.getN(), row, giftReturnCommodity.getGiftReturn().getGift_return_employee_name()));//
                sheet.addCell(new Label(col.getN(), row, giftReturnCommodity.getCommodity().getCommodity_code()));//
                sheet.addCell(new Label(col.getN(), row, giftReturnCommodity.getCommodity().getCommodity_name()));//
                sheet.addCell(new Label(col.getN(), row, giftReturnCommodity.getSupplier().getSupplier_name()));//
                sheet.addCell(new Number(col.getN(), row, giftReturnCommodity.getReturn_num()));//
                //赠品类型，1订单赠品，2活动赠品
                String gift_type = "";
                if (giftReturnCommodity.getGift().getGift_type() == 1) {
                    gift_type = "订单赠送（" + order_code + ")";
                }else if (giftReturnCommodity.getGift().getGift_type() == 2) {
                    gift_type = "活动赠送（" + activity_name + ")";
                }
                sheet.addCell(new Label(col.getN(), row, gift_type));//
                sheet.addCell(new Label(col.getN(), row, giftReturnCommodity.getGift().getGift_remark()));//
            }
            ResponseUtil.exportResponse(request, response, workbook);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
