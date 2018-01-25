package com.xinshan.utils.thread;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.commodity.CommodityComponent;
import com.xinshan.components.inventory.InventoryComponent;
import com.xinshan.model.Commodity;
import com.xinshan.model.CommodityStore;
import com.xinshan.model.Supplier;
import com.xinshan.model.extend.commodity.CommodityExtend;
import com.xinshan.model.extend.commodity.CommodityNumExtend;
import com.xinshan.utils.CommonUtils;
import com.xinshan.utils.Counter;
import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mxt on 17-6-2.
 */
public class InventoryExport implements Runnable{
    @Override
    public void run() {
        export();
    }

    private void export() {
        /*inventoryExport();
        commodityExport();*/
    }

    private void inventoryExport() {
        try {
            String filename = new String(("库存导出").getBytes("utf-8"), CommonUtils.CHARSET) + ".xls";
            File file = new File(CommonUtils.FILE_UPLOAD_DIR + "/" + filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            System.out.println(file.getAbsolutePath());
            List<CommodityNumExtend> list = InventoryComponent.commodityExport(null);
            WritableWorkbook workbook = Workbook.createWorkbook(file);
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
            sheet.addCell(new Label(col.getN(), row, "原货号", header_format));//
            sheet.addCell(new Label(col.getN(), row, "新货号", header_format));//
            sheet.addCell(new Label(col.getN(), row, "样品", header_format));//
            sheet.addCell(new Label(col.getN(), row, "是否退货商品", header_format));//
            sheet.addCell(new Label(col.getN(), row, "商品名称", header_format));//
            sheet.addCell(new Label(col.getN(), row, "销货价", header_format));//
            sheet.addCell(new Label(col.getN(), row, "尺寸", header_format));//
            sheet.addCell(new Label(col.getN(), row, "供应商", header_format));//
            sheet.addCell(new Label(col.getN(), row, "委托人", header_format));//
            sheet.addCell(new Label(col.getN(), row, "存放场馆", header_format));//
            sheet.addCell(new Label(col.getN(), row, "库存数量", header_format));//
            row++;
            for (int i = 0; i < list.size(); i++) {
                col.reset();
                CommodityNumExtend commodityNumExtend = list.get(i);
                Commodity commodity = commodityNumExtend.getCommodity();
                Supplier supplier = commodityNumExtend.getSupplier();
                CommodityStore commodityStore = commodityNumExtend.getCommodityStore();
                sheet.addCell(new Label(col.getN(), row, commodity.getSupplier_commodity_code(), header_format));//
                sheet.addCell(new Label(col.getN(), row, commodity.getCommodity_code(), header_format));//
                if (commodityNumExtend.getSample() == 1) {
                    sheet.addCell(new Label(col.getN(), row, "是", header_format));//
                }else {
                    sheet.addCell(new Label(col.getN(), row, "否", header_format));//
                }if (commodityNumExtend.getReturn_commodity() == 1) {
                    sheet.addCell(new Label(col.getN(), row, "是", header_format));//
                }else {
                    sheet.addCell(new Label(col.getN(), row, "否", header_format));//
                }
                sheet.addCell(new Label(col.getN(), row, commodity.getCommodity_name(), header_format));//
                if (commodity.getPurchase_price() != null) {
                    sheet.addCell(new Number(col.getN(), row, commodity.getSell_price().doubleValue(), header_format));//
                }else {
                    sheet.addCell(new Number(col.getN(), row, 0, header_format));//
                }
                sheet.addCell(new Label(col.getN(), row, commodity.getCommodity_size(), header_format));//
                if (supplier != null) {
                    sheet.addCell(new Label(col.getN(), row, supplier.getSupplier_name(), header_format));//
                    sheet.addCell(new Label(col.getN(), row, supplier.getContacts(), header_format));//
                }else {
                    sheet.addCell(new Label(col.getN(), row, "", header_format));//
                    sheet.addCell(new Label(col.getN(), row, "", header_format));//
                }
                sheet.addCell(new Label(col.getN(), row, commodityStore.getStore_name(), header_format));//
                sheet.addCell(new Number(col.getN(), row, commodityNumExtend.getNum(), header_format));//

                row++;
            }
            workbook.write();
            workbook.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("--------------------------------");
        try {
            String filename = new String(("库存导出").getBytes("utf-8"), CommonUtils.CHARSET) + ".xls";
            File file = new File(CommonUtils.FILE_UPLOAD_DIR + "/" + filename);
            System.out.println("文件大小："+file.length());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void commodityExport() {
        try {
            String filename = new String(("commodityExport").getBytes("utf-8"), CommonUtils.CHARSET) + ".xls";
            File file = new File(CommonUtils.FILE_UPLOAD_DIR + "/" + filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            System.out.println(file.getAbsolutePath());
            List<CommodityExtend> list = CommodityComponent.commodityExtends(null);
            WritableWorkbook workbook = Workbook.createWorkbook(file);
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
            sheet.addCell(new Label(col.getN(), row, "原货号", header_format));//
            sheet.addCell(new Label(col.getN(), row, "新货号", header_format));//
            sheet.addCell(new Label(col.getN(), row, "名称", header_format));//
            sheet.addCell(new Label(col.getN(), row, "供应商", header_format));//
            sheet.addCell(new Label(col.getN(), row, "委托人", header_format));//
            sheet.addCell(new Label(col.getN(), row, "颜色尺寸", header_format));//
            sheet.addCell(new Label(col.getN(), row, "进货价", header_format));//
            sheet.addCell(new Label(col.getN(), row, "标准售价", header_format));//
            sheet.addCell(new Label(col.getN(), row, "含运费", header_format));//
            sheet.addCell(new Label(col.getN(), row, "不含运费售价", header_format));//
            row++;
            for (int i = 0; i < list.size(); i++) {
                col.reset();
                CommodityExtend commodityExtend = list.get(i);
                sheet.addCell(new Label(col.getN(), row, commodityExtend.getSupplier_commodity_code(), header_format));//
                sheet.addCell(new Label(col.getN(), row, commodityExtend.getCommodity_code(), header_format));//
                sheet.addCell(new Label(col.getN(), row, commodityExtend.getCommodity_name(), header_format));//
                String supplier_name = "";
                String contacts = "";
                if (commodityExtend.getSupplier() != null) {
                    supplier_name = commodityExtend.getSupplier().getSupplier_name();
                    contacts = commodityExtend.getSupplier().getContacts();
                }
                sheet.addCell(new Label(col.getN(), row, supplier_name, header_format));//
                sheet.addCell(new Label(col.getN(), row, contacts, header_format));//
                sheet.addCell(new Label(col.getN(), row, commodityExtend.getCommodity_size(), header_format));//
                sheet.addCell(new Number(col.getN(), row, commodityExtend.getPurchase_price() == null ? 0 : commodityExtend.getPurchase_price().doubleValue(), header_format));//
                BigDecimal sell_price = commodityExtend.getSell_price();
                if (sell_price == null) {
                    sell_price = new BigDecimal("0");
                }
                BigDecimal commodity_freight = commodityExtend.getCommodity_freight();
                if (commodity_freight == null ) {
                    commodity_freight = new BigDecimal("0");
                }
                sheet.addCell(new Number(col.getN(), row, sell_price.doubleValue(), header_format));//
                sheet.addCell(new Number(col.getN(), row, commodity_freight.doubleValue(), header_format));//
                sheet.addCell(new Number(col.getN(), row, sell_price.subtract(commodity_freight).doubleValue(), header_format));//
                row++;
            }
            workbook.write();
            workbook.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("--------------------------------");
        try {
            String filename = new String(("commodityExport").getBytes("utf-8"), CommonUtils.CHARSET) + ".xls";
            File file = new File(CommonUtils.FILE_UPLOAD_DIR + "/" + filename);
            System.out.println("文件大小："+file.length());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
