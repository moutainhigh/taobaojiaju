package com.xinshan.controller.activity;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.activity.ActivityComponents;
import com.xinshan.model.*;
import com.xinshan.model.extend.activity.ActivityExtend;
import com.xinshan.model.extend.activity.CashBackExtend;
import com.xinshan.model.extend.commodity.CommodityActivity;
import com.xinshan.model.extend.order.OrderCommodityExtend;
import com.xinshan.model.extend.order.OrderReturnCommodityExtend;
import com.xinshan.model.extend.order.OrderReturnExtend;
import com.xinshan.model.extend.supplier.SupplierExtend;
import com.xinshan.pojo.SearchOption;
import com.xinshan.pojo.activity.CashBackSearchOption;
import com.xinshan.pojo.order.OrderSearchOption;
import com.xinshan.pojo.supplier.SupplierSearchOption;
import com.xinshan.service.*;
import com.xinshan.utils.*;
import com.xinshan.utils.constant.activity.ActivityConstant;
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
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by mxt on 17-4-17.
 */
@Controller
public class CashBackController {
    @Autowired
    private CashBackService cashBackService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private OrderReturnService orderReturnService;

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/cashBack/cashBackList")
    public void cashBackList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CashBackSearchOption cashBackSearchOption = Request2ModelUtils.covert(CashBackSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(cashBackSearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(cashBackSearchOption);
        List<Integer> cashBashIds = cashBackService.cashBackIds(cashBackSearchOption);
        Integer count = cashBackService.countCashBack(cashBackSearchOption);
        List<CashBackExtend> list = cashBackService.cashBackList(cashBashIds);
        ResponseUtil.sendListResponse(request, response, list, count, cashBackSearchOption);
    }

    /**
     * 添加返现
     * @param response
     * @param request
     * @throws IOException
     */
    @RequestMapping("/activity/cashBack/createCashBack")
    public void createCashBack(HttpServletResponse response, HttpServletRequest request) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String postData = RequestUtils.getRequestUtils().postData(request);
        CashBackExtend cashBackExtend = JSONObject.parseObject(postData, CashBackExtend.class);
        cashBackService.createCashBack(cashBackExtend, employee);
        ResponseUtil.sendSuccessResponse(request, response, postData);
    }

    @RequestMapping("/activity/cashBack/cashBackOrderCommodities")
    public void cashBackOrderCommodities(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int activity_id = Integer.parseInt(request.getParameter("activity_id"));
        int order_id = Integer.parseInt(request.getParameter("order_id"));
        ActivityExtend activity = activityService.activityDetail(activity_id);
        Map<String, Object> map = new HashMap<>();

        CashBackSearchOption cashBackSearchOption = new CashBackSearchOption();
        cashBackSearchOption.setOrder_id(order_id);
        List<Integer> cashBackIds = null;
        if (request.getParameter("order_return_id") != null && !request.getParameter("order_return_id").equals("")) {
            cashBackSearchOption.setCash_back_type(ActivityConstant.CASH_BACK_TYPE_ORDER_RETURN);
            int order_return_id = Integer.parseInt(request.getParameter("order_return_id"));
            cashBackSearchOption.setOrder_return_id(order_return_id);
            cashBackIds = cashBackService.cashBackIds(cashBackSearchOption);
            OrderSearchOption orderSearchOption = new OrderSearchOption();
            orderSearchOption.setOrder_return_id(order_return_id);
            List<OrderReturnCommodityExtend> orderReturnCommodities = orderReturnService.orderReturnReport(orderSearchOption);
            for (int i = 0; i < orderReturnCommodities.size(); i++) {
                OrderReturnCommodityExtend orderReturnCommodityExtend = orderReturnCommodities.get(i);
                Commodity commodity = orderReturnCommodityExtend.getCommodity();
                CommodityActivity commodityActivity = ActivityComponents.getByCommodity(commodity);
                orderReturnCommodityExtend.setCommodityActivity(commodityActivity);
                orderReturnCommodityExtend.setActivities(ActivityComponents.commodityActivities(ActivityConstant.ACTIVITY_TYPE_CASH_BACK, commodity));
            }
            map.put("list", orderReturnCommodities);
        }else {
            cashBackSearchOption.setCash_back_type(ActivityConstant.CASH_BACK_TYPE_ORDER);
            cashBackIds = cashBackService.cashBackIds(cashBackSearchOption);
            List<OrderCommodityExtend> list = orderService.cashBackCommodities(order_id);
            map.put("list", list);
        }
        if (cashBackIds != null && cashBackIds.size() >= 1) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014"));
            return;
        }
        map.put("activity", activity);
        ResponseUtil.sendSuccessResponse(request, response, map);
    }

    @RequestMapping("/activity/cashBack/deleteCashBackCommodity")
    public void deleteCashBackCommodity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int activity_id = Integer.parseInt(request.getParameter("activity_id"));
        ActivityExtend activity = activityService.activityDetail(activity_id);
        ActivityCashBack activityCashBack = activity.getActivityCashBack();
        if (request.getParameter("commodity_id") != null && !request.getParameter("commodity_id").equals("")) {
            int commodity_id = Integer.parseInt(request.getParameter("commodity_id"));
            Set<Integer> commodityIds = SplitUtils.splitToSet(activityCashBack.getCash_back_commodity_ids(), ",");
            commodityIds.remove(commodity_id);
            activityCashBack.setCash_back_commodity_ids(SplitUtils.setToString(commodityIds));
        }
        if (request.getParameter("supplier_id") != null && !request.getParameter("supplier_id").equals("")) {
            int supplier_id = Integer.parseInt(request.getParameter("supplier_id"));
            Set<Integer> supplierIds = SplitUtils.splitToSet(activityCashBack.getCash_back_supplier_ids(), ",");
            supplierIds.remove(supplier_id);
            activityCashBack.setCash_back_supplier_ids(SplitUtils.setToString(supplierIds));
        }
        activityService.updateActivityCashBack(activityCashBack);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 返现活动
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/activity/editActivityCashBack")
    public void editActivityCashBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int activity_cash_back_id = Integer.parseInt(request.getParameter("activity_cash_back_id"));
        BigDecimal cash_back_rate = new BigDecimal(request.getParameter("cash_back_rate"));
        String cash_back_commodity_ids = request.getParameter("cash_back_commodity_ids");
        String cash_back_supplier_ids = request.getParameter("cash_back_supplier_ids");
        int cash_back_all = 0;
        if (request.getParameter("cash_back_all") != null && !request.getParameter("cash_back_all").equals("")) {
            cash_back_all = Integer.parseInt(request.getParameter("cash_back_all"));
        }

        ActivityCashBack activityCashBack = activityService.getActivityCashBackById(activity_cash_back_id);
        activityCashBack.setCash_back_rate(cash_back_rate);
        Set<Integer> commodityIds = SplitUtils.splitToSet(activityCashBack.getCash_back_commodity_ids(), ",");
        commodityIds.addAll(SplitUtils.splitToSet(cash_back_commodity_ids, ","));
        activityCashBack.setCash_back_commodity_ids(SplitUtils.setToString(commodityIds));

        Set<Integer> supplierIds = SplitUtils.splitToSet(activityCashBack.getCash_back_supplier_ids(), ",");
        supplierIds.addAll(SplitUtils.splitToSet(cash_back_supplier_ids, ","));
        activityCashBack.setCash_back_supplier_ids(SplitUtils.setToString(supplierIds));

        activityCashBack.setCash_back_all(cash_back_all);
        activityService.updateActivityCashBack(activityCashBack);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/cashBack/cashBackSuppliers")
    public void cashBackSuppliers(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int activity_id = Integer.parseInt(request.getParameter("activity_id"));
        String param = request.getParameter("param");
        ActivityExtend activityExtend = activityService.activityDetail(activity_id);
        ActivityCashBack activityCashBack = activityExtend.getActivityCashBack();
        String cash_back_supplier_ids = activityCashBack.getCash_back_supplier_ids();
        List<SupplierExtend> list = new ArrayList<>();
        if(cash_back_supplier_ids != null && !cash_back_supplier_ids.equals("")) {
            SupplierSearchOption supplierSearchOption = new SupplierSearchOption();
            supplierSearchOption.setSupplier_ids(SplitUtils.splitToList(cash_back_supplier_ids, ","));
            supplierSearchOption.setParam(param);
            list = supplierService.supplierList(supplierSearchOption);
        }
        ResponseUtil.sendSuccessResponse(request, response, list);
    }

    /**
     * 返现金额统计
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/cashBack/statistics")
    public void cashBackStatistics(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CashBackSearchOption cashBackSearchOption = Request2ModelUtils.covert(CashBackSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(cashBackSearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(cashBackSearchOption);
        Map map = cashBackService.statistics(cashBackSearchOption);
        if (map == null) {
            map = new HashMap<>();
            map.put("cash_back_amount", 0);
            map.put("cash_back_real_amount", 0);
        }
        ResponseUtil.sendSuccessResponse(request, response, map);
    }

    /**
     * 导出返现
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/cashBack/export")
    public void cashBackExport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CashBackSearchOption cashBackSearchOption = Request2ModelUtils.covert(CashBackSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(cashBackSearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(cashBackSearchOption);
        cashBackSearchOption.setStart(null);
        cashBackSearchOption.setLimit(null);
        cashBackSearchOption.setCurrentPage(null);
        List<Integer> cashBashIds = cashBackService.cashBackIds(cashBackSearchOption);
        List<CashBackExtend> list = cashBackService.cashBackList(cashBashIds);
        response.setHeader("Content-Type", "application/msexcel");
        //设置文本头部名称
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(("返现导出").getBytes("utf-8"), "iso8859-1") + ".xls");
        response.setHeader("Cache-Control", "max-age=0");
        WritableWorkbook workbook = Workbook.createWorkbook(response.getOutputStream());
        WritableSheet sheet = workbook.createSheet("返现导出", 0);
        int row = 0;
        Counter counter = new Counter();
        try {
            sheet.addCell(new Label(counter.getN(), row, "活动名称"));
            sheet.addCell(new Label(counter.getN(), row, "订单号"));
            sheet.addCell(new Label(counter.getN(), row, "返现日期"));
            sheet.addCell(new Label(counter.getN(), row, "客户姓名"));
            sheet.addCell(new Label(counter.getN(), row, "客户电话"));
            sheet.addCell(new Label(counter.getN(), row, "返现商品金额"));
            sheet.addCell(new Label(counter.getN(), row, "实际返现金额"));
            sheet.addCell(new Label(counter.getN(), row, "操作人"));
            sheet.addCell(new Label(counter.getN(), row, "类型"));
            sheet.addCell(new Label(counter.getN(), row, "备注"));
            for (int i = 0; i < list.size(); i++) {
                CashBackExtend cashBackExtend = list.get(i);
                row++;
                counter.reset();
                sheet.addCell(new Label(counter.getN(), row, cashBackExtend.getActivity().getActivity_name()));
                String order_code = "", customer_name = "", phoneNumber = "";
                if (cashBackExtend.getOrder() != null) {
                    Order order = cashBackExtend.getOrder();
                    order_code = order.getOrder_code();
                    customer_name = order.getCustomer_name();
                    phoneNumber = order.getCustomer_phone_number();
                }
                sheet.addCell(new Label(counter.getN(), row, order_code));
                sheet.addCell(new Label(counter.getN(), row, DateUtil.format(cashBackExtend.getCash_back_date(), "yyyy-MM-dd")));
                sheet.addCell(new Label(counter.getN(), row, customer_name));
                sheet.addCell(new Label(counter.getN(), row, phoneNumber));
                sheet.addCell(new Number(counter.getN(), row, cashBackExtend.getCash_back_amount().doubleValue()));
                sheet.addCell(new Number(counter.getN(), row, cashBackExtend.getCash_back_real_amount().doubleValue()));
                sheet.addCell(new Label(counter.getN(), row, cashBackExtend.getCash_back_employee_name()));
                Integer cash_back_type = cashBackExtend.getCash_back_type();
                if (cash_back_type == 1) {
                    sheet.addCell(new Label(counter.getN(), row, "订单"));
                }else if (cash_back_type == 2) {
                    sheet.addCell(new Label(counter.getN(), row, "退换货"));
                }
                sheet.addCell(new Label(counter.getN(), row, cashBackExtend.getCash_back_remark()));
            }
            workbook.write();
            workbook.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
