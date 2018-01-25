package com.xinshan.controller.activity;

import com.xinshan.model.Gift;
import com.xinshan.model.GoldEgg;
import com.xinshan.model.Order;
import com.xinshan.model.extend.activity.GoldEggExtend;
import com.xinshan.pojo.activity.GoldEggSearchOption;
import com.xinshan.service.GoldEggService;
import com.xinshan.service.OrderService;
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
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mxt on 17-4-18.
 */
@Controller
public class GoldEggController {
    @Autowired
    private GoldEggService goldEggService;

    @Autowired
    private OrderService orderService;

    @RequestMapping("/activity/goldEgg/createGoldEgg")
    public void createGoldEgg(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GoldEgg goldEgg = Request2ModelUtils.covert(GoldEgg.class, request);
        int order_id = goldEgg.getOrder_id();
        GoldEggExtend goldEggExtend = goldEggService.getGoldEggByOrderId(order_id);
        if (goldEggExtend != null) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014","已参加此活动"));
            return;
        }
        goldEggService.createGoldEgg(goldEgg);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    @RequestMapping("/activity/goldEgg/goldEggList")
    public void goldEggList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GoldEggSearchOption goldEggSearchOption = Request2ModelUtils.covert(GoldEggSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(goldEggSearchOption);
        List<GoldEggExtend> list = goldEggService.goldEggExtends(goldEggSearchOption);
        Integer count = goldEggService.countGoldEgg(goldEggSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, goldEggSearchOption);
    }

    @RequestMapping("/activity/goldEgg/goldEggExport")
    public void goldEggExport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GoldEggSearchOption goldEggSearchOption = Request2ModelUtils.covert(GoldEggSearchOption.class, request);
        List<GoldEggExtend> list = goldEggService.goldEggExtends(goldEggSearchOption);
        try {
            OutputStream outputStream = response.getOutputStream();
            response.reset();
            response.setHeader("Content-Type", "application/msexcel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("砸金蛋导出").getBytes("utf-8"),"iso8859-1") + ".xls");
            response.setHeader("Cache-Control", "max-age=0");
            WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
            WritableSheet sheet = workbook.createSheet("sheet1", 0);

            //WritableCellFormat wcf_left_format = new WritableCellFormat(new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD));
            //1正常；0逾期；-1冻结
            int row = 0;
            Counter col = new Counter();
            sheet.addCell(new Label(col.getN(), row, "活动名称"));//
            sheet.addCell(new Label(col.getN(), row, "订单号"));//
            sheet.addCell(new Label(col.getN(), row, "客户姓名"));//
            sheet.addCell(new Label(col.getN(), row, "电话"));//
            sheet.addCell(new Label(col.getN(), row, "订单金额"));//
            sheet.addCell(new Label(col.getN(), row, "次数"));//
            sheet.addCell(new Label(col.getN(), row, "结算金额"));//
            sheet.addCell(new Label(col.getN(), row, "提货券金额"));//
            sheet.addCell(new Label(col.getN(), row, "备注"));//
            row++;
            for (int i = 0; i < list.size(); i++) {
                col.reset();
                GoldEggExtend goldEggExtend = list.get(i);
                sheet.addCell(new Label(col.getN(), row, goldEggExtend.getActivity().getActivity_name()));//
                sheet.addCell(new Label(col.getN(), row, goldEggExtend.getOrder().getOrder_code()));//
                sheet.addCell(new Label(col.getN(), row, goldEggExtend.getOrder().getCustomer_name()));//
                sheet.addCell(new Label(col.getN(), row, goldEggExtend.getOrder().getCustomer_phone_number()));//
                sheet.addCell(new Number(col.getN(), row, goldEggExtend.getOrder().getTotal_price().doubleValue()));//
                sheet.addCell(new Number(col.getN(), row, goldEggExtend.getGold_egg_times()));//
                BigDecimal gold_egg_settlement_amount = goldEggExtend.getActivityGoldEgg().getGold_egg_settlement_amount();
                gold_egg_settlement_amount = gold_egg_settlement_amount.multiply(new BigDecimal(goldEggExtend.getGold_egg_times()));
                sheet.addCell(new Number(col.getN(), row, gold_egg_settlement_amount.doubleValue()));//
                sheet.addCell(new Number(col.getN(), row, goldEggExtend.getTotal_amount().doubleValue()));//
                sheet.addCell(new Label(col.getN(), row, goldEggExtend.getRemark()));//
                row++;
            }
            ResponseUtil.exportResponse(request, response, workbook);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
