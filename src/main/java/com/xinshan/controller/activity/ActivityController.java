package com.xinshan.controller.activity;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.activity.ActivityComponents;
import com.xinshan.components.commodity.CommodityComponent;
import com.xinshan.model.*;
import com.xinshan.model.extend.activity.ActivityCommodityExtend;
import com.xinshan.model.extend.activity.ActivityExtend;
import com.xinshan.model.extend.commodity.CommodityExtend;
import com.xinshan.model.extend.supplier.SupplierExtend;
import com.xinshan.pojo.supplier.SupplierSearchOption;
import com.xinshan.service.ActivityService;
import com.xinshan.service.CommodityService;
import com.xinshan.service.SupplierService;
import com.xinshan.utils.*;
import com.xinshan.utils.constant.ConstantUtils;
import com.xinshan.pojo.activity.ActivitySearchOption;
import com.xinshan.pojo.commodity.CommoditySearchOption;
import com.xinshan.utils.constant.activity.ActivityConstant;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mxt on 17-2-10.
 */
@Controller
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    @Autowired
    private CommodityService commodityService;
    @Autowired
    private SupplierService supplierService;

    /**
     * 添加活动
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/activity/createActivity")
    public void createActivity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String postData = RequestUtils.getRequestUtils().postData(request);
        ActivityExtend activityExtend = JSONObject.parseObject(postData, ActivityExtend.class);
        activityService.createActivity(activityExtend, employee);
        ActivityComponents.clear();
        ResponseUtil.sendSuccessResponse(request, response, postData);
    }

    /**
     * 活动详情
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/activity/activityDetail")
    public void activityDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int activity_id = Integer.parseInt(request.getParameter("activity_id"));
        ActivityExtend activityExtend = activityService.activityDetail(activity_id);
        ResponseUtil.sendSuccessResponse(request, response, activityExtend);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/activity/specialOfferActivityCommodity")
    public void specialOfferActivityCommodity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivitySearchOption activitySearchOption = Request2ModelUtils.covert(ActivitySearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(activitySearchOption);
        List<ActivityCommodityExtend> list = activityService.activityCommodityExtends(activitySearchOption);
        Integer count = activityService.countActivityCommodityExtends(activitySearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, activitySearchOption);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/activity/cashBackCommodity")
    public void activityCashBackCommodity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int activity_id = Integer.parseInt(request.getParameter("activity_id"));
        ActivityExtend activityExtend = activityService.activityDetail(activity_id);
        ActivityCashBack activityCashBack = activityExtend.getActivityCashBack();
        if (activityCashBack != null && activityCashBack.getCash_back_commodity_ids() != null) {
            CommoditySearchOption commoditySearchOption = new CommoditySearchOption();
            commoditySearchOption.setCommodity_ids(activityExtend.getActivityCashBack().getCash_back_commodity_ids());
            List<CommodityExtend> list = commodityService.commodityEasyList(commoditySearchOption);
            ResponseUtil.sendSuccessResponse(request, response, list);
        }else {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014"));
        }
    }

    /**
     * 编辑活动
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/activity/updateActivity")
    public void updateActivity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int activity_id = Integer.parseInt(request.getParameter("activity_id"));
        String activity_name = request.getParameter("activity_name");
        Date activity_start_date = DateUtil.stringToDate(request.getParameter("activity_start_date"));
        Date activity_end_date = DateUtil.stringToDate(request.getParameter("activity_end_date"));
        String activity_desc = request.getParameter("activity_desc");
        Activity activity = activityService.getActivityById(activity_id);
        if (activity == null) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "活动id不正确"));
            return;
        }
        activity.setActivity_end_date(DateUtil.endOfTheDay(activity_end_date));
        activity.setActivity_start_date(activity_start_date);
        activity.setActivity_name(activity_name);
        activity.setActivity_desc(activity_desc);
        if (request.getParameter("activity_status") != null && !"".equals(request.getParameter("activity_status"))) {
            activity.setActivity_status(Integer.parseInt(request.getParameter("activity_status")));
        }
        activityService.updateActivity(activity);
        ResponseUtil.sendSuccessResponse(request, response);
        ActivityComponents.clear();
    }

    /**
     * 编辑活动商品
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/activity/editCommodity")
    public void activityCommodity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String postData = RequestUtils.getRequestUtils().postData(request);
        ActivityExtend activityExtend = JSONObject.parseObject(postData, ActivityExtend.class);
        activityService.activityCommodityAdd(activityExtend);
        ResponseUtil.sendSuccessResponse(request, response, postData);
        ActivityComponents.clear();
    }



    /**
     * 活动列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/activity/activityList")
    public void activityList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivitySearchOption activitySearchOption = Request2ModelUtils.covert(ActivitySearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(activitySearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(activitySearchOption);
        List<ActivityExtend> list = activityService.activityList(activitySearchOption);
        Integer count = activityService.countActivity(activitySearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, activitySearchOption);
    }

    @RequestMapping("/activity/activity/activityEasyList")
    public void activityEasyList(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }

    /**
     * 当前参与活动商品列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/activity/activityCommodity")
    public void activityCommodityList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CommoditySearchOption commoditySearchOption = Request2ModelUtils.covert(CommoditySearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(commoditySearchOption);
        List<ActivityCommodityExtend> list = ActivityComponents.currentEnableActivity();
        int all = 0;
        Set<Integer> suppliers = new HashSet<>();
        Set<Integer> commodityIds = new HashSet<>();
        Set<Integer> supplierSeries = new HashSet<>();
        String param = commoditySearchOption.getParam();
        Pattern pattern = null;
        Matcher matcher = null;
        if (param != null) {
            pattern = Pattern.compile(param);
        }

        for (int i = 0; i < list.size(); i++) {
            ActivityCommodityExtend activityCommodityExtend = list.get(i);
            ActivityDetail activityDetail = activityCommodityExtend.getActivityDetail();
            String activity_name = activityCommodityExtend.getActivity().getActivity_name();
            if (pattern != null) {
                matcher = pattern.matcher(activity_name);
                if (!matcher.find()) {
                    continue;
                }
            }
            if (activityDetail.getAll() == 1) {
                all = 1;
                break;
            }
            String supplier_ids = activityDetail.getSupplier_ids();
            if (supplier_ids != null && !"".equals(supplier_ids)) {
                suppliers.addAll(SplitUtils.splitToSet(supplier_ids, ","));
            }
            String commodity_ids = activityDetail.getCommodity_ids();
            if (commodity_ids != null && !"".equals(commodity_ids)) {
                commodityIds.addAll(SplitUtils.splitToSet(commodity_ids, ","));
            }
            String supplier_serieses = activityDetail.getSupplier_serieses();
            if (supplier_serieses != null && !"".equals(supplier_serieses)) {
                supplierSeries.addAll(SplitUtils.splitToSet(supplier_serieses, ","));
            }
        }
        if (all == 0) {
            all = all();
        }
        if (all != 1) {//不是全部商品参加活动
            List<Integer> list1 = new ArrayList<>();
            List<Integer> list2 = new ArrayList<>();
            List<Integer> list3 = new ArrayList<>();
            list1.addAll(commodityIds);
            if (list1.size() == 0) {
                list1.add(0);
            }
            list2.addAll(suppliers);
            if (list2.size() == 0) {
                list2.add(0);
            }
            list3.addAll(supplierSeries);
            if (list3.size() == 0) {
                list3.add(0);
            }
            commoditySearchOption.setCommodityIdList(list1);
            commoditySearchOption.setSupplierIdList(list2);
            commoditySearchOption.setSupplierSeriesList(list3);
        }
        commoditySearchOption.setParam(null);
        List<CommodityExtend> commodities = commodityService.commodityList(commoditySearchOption);
        Integer count = commodityService.countCommodity(commoditySearchOption);
        commoditySearchOption.setCommodityIdList(null);
        commoditySearchOption.setSupplierIdList(null);
        commoditySearchOption.setSupplierSeriesList(null);
        commoditySearchOption.setParam(param);
        ResponseUtil.sendListResponse(request, response, commodities, count, commoditySearchOption);
    }

    private int all() {
        Date date = DateUtil.currentDate();
        List<ActivityExtend> list = activityService.activityList(null);
        for (int i = 0; i < list.size(); i++) {
            ActivityExtend activityExtend = list.get(i);
            int activity_status = activityExtend.getActivity_status();
            if (activity_status == ConstantUtils.ENABLE_STATUS) {
                Date startDate = activityExtend.getActivity_start_date();
                Date endDate = activityExtend.getActivity_end_date();
                endDate = DateUtil.endOfTheDay(endDate);
                if (startDate.before(date) && date.before(endDate) && activityExtend.getActivityDetail().getAll() == 1) {
                    return 1;
                }
            }
        }
        return 0;
    }

    @RequestMapping("/activity/activity/goldEggSuppliers")
    public void activityGoldEggSuppliers(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int activity_id = Integer.parseInt(request.getParameter("activity_id"));
        SupplierSearchOption supplierSearchOption = Request2ModelUtils.covert(SupplierSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(supplierSearchOption);
        ActivityExtend activityExtend = activityService.activityDetail(activity_id);
        ActivityGoldEgg activityGoldEgg = activityExtend.getActivityGoldEgg();
        String supplier_ids = activityGoldEgg.getSupplier_ids();
        if (supplier_ids == null || "".equals(supplier_ids)) {
            ResponseUtil.sendSuccessResponse(request, response, new ArrayList<>());
            return;
        }
        List<Integer> list1 = SplitUtils.splitToList(supplier_ids, ",");
        Collections.sort(list1);
        supplierSearchOption.setSupplier_ids(list1);
        List<SupplierExtend> list = supplierService.supplierList(supplierSearchOption);
        int count = list1.size();
        ResponseUtil.sendListResponse(request, response, list, count, supplierSearchOption);
    }

    @RequestMapping("/activity/activity/updateActivityGoldEgg")
    public void updateActivityGoldEgg(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int activity_id = Integer.parseInt(request.getParameter("activity_id"));
        int gold_egg_type = Integer.parseInt(request.getParameter("gold_egg_type"));
        BigDecimal gold_egg_amount = new BigDecimal(request.getParameter("gold_egg_amount"));
        BigDecimal gold_egg_settlement_amount = new BigDecimal(request.getParameter("gold_egg_settlement_amount"));
        String supplier_ids = request.getParameter("supplier_ids");
        String gold_egg_remark = request.getParameter("gold_egg_remark");
        String activity_name = request.getParameter("activity_name");
        Date startDate = DateUtil.stringToDate(request.getParameter("activity_start_date"));
        Date endDate = DateUtil.stringToDate(request.getParameter("activity_end_date"));
        ActivityExtend activityExtend = activityService.activityDetail(activity_id);
        ActivityGoldEgg activityGoldEgg = activityExtend.getActivityGoldEgg();
        activityGoldEgg.setGold_egg_amount(gold_egg_amount);
        activityGoldEgg.setGold_egg_remark(gold_egg_remark);
        activityGoldEgg.setGold_egg_settlement_amount(gold_egg_settlement_amount);
        activityGoldEgg.setGold_egg_type(gold_egg_type);
        activityGoldEgg.setSupplier_ids(supplier_ids);
        activityExtend.setActivity_name(activity_name);
        activityExtend.setActivity_start_date(startDate);
        activityExtend.setActivity_end_date(DateUtil.endOfTheDay(endDate));
        if (request.getParameter("activity_status") != null && !request.getParameter("activity_status").equals("")) {
            activityExtend.setActivity_status(Integer.parseInt(request.getParameter("activity_status")));
        }
        activityService.updateActivityGoldEgg(activityExtend, activityGoldEgg);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    @RequestMapping("/activity/activity/deleteGoldEggSupplier")
    public void deleteActivitySupplier(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int activity_id = Integer.parseInt(request.getParameter("activity_id"));
        int supplier_id = Integer.parseInt(request.getParameter("supplier_id"));
        ActivityExtend activityExtend = activityService.activityDetail(activity_id);
        ActivityGoldEgg activityGoldEgg = activityExtend.getActivityGoldEgg();
        String supplier_ids = activityGoldEgg.getSupplier_ids();
        Set<Integer> set = SplitUtils.splitToSet(supplier_ids, ",");
        set.remove(supplier_id);
        activityGoldEgg.setSupplier_ids(SplitUtils.setToString(set));
        activityService.updateActivityGoldEgg(activityGoldEgg);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    @RequestMapping("/activity/activity/addGoldEggSupplier")
    public void addGoldEggSupplier(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int activity_id = Integer.parseInt(request.getParameter("activity_id"));
        String supplierIds = request.getParameter("supplier_ids");
        ActivityExtend activityExtend = activityService.activityDetail(activity_id);
        ActivityGoldEgg activityGoldEgg = activityExtend.getActivityGoldEgg();
        String supplier_ids = activityGoldEgg.getSupplier_ids();
        Set<Integer> set = SplitUtils.splitToSet(supplier_ids, ",");
        set.addAll(SplitUtils.splitToList(supplierIds, ","));
        activityGoldEgg.setSupplier_ids(SplitUtils.setToString(set));
        activityService.updateActivityGoldEgg(activityGoldEgg);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    @RequestMapping("/activity/activity/updateActivityBrand")
    public void updateActivityBrand(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int activity_id = Integer.parseInt(request.getParameter("activity_id"));
        BigDecimal activity_brand_amount = new BigDecimal(request.getParameter("activity_brand_amount"));
        int brand_gift_num = Integer.parseInt(request.getParameter("brand_gift_num"));
        String activity_name = request.getParameter("activity_name");
        String activity_brand_names = request.getParameter("activity_brand_names");
        String activity_brand_remark = request.getParameter("activity_brand_remark");
        Date activity_start_date = DateUtil.stringToDate(request.getParameter("activity_start_date"));
        Date activity_end_date = DateUtil.stringToDate(request.getParameter("activity_end_date"));
        ActivityExtend activityExtend = activityService.activityDetail(activity_id);
        activityExtend.setActivity_name(activity_name);
        activityExtend.setActivity_start_date(activity_start_date);
        activityExtend.setActivity_end_date(DateUtil.endOfTheDay(activity_end_date));
        ActivityBrand activityBrand = activityExtend.getActivityBrand();
        activityBrand.setActivity_brand_amount(activity_brand_amount);
        activityBrand.setActivity_brand_names(activity_brand_names);
        activityBrand.setActivity_brand_remark(activity_brand_remark);
        activityBrand.setBrand_gift_num(brand_gift_num);
        activityService.updateActivityBrand(activityExtend, activityBrand);
        ActivityComponents.clear();
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 编辑特价权活动
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/activity/updateSpecialRight")
    public void updateActivitySpecialRight(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String activity_name = request.getParameter("activity_name");
        int activity_id = Integer.parseInt(request.getParameter("activity_id"));
        Date activity_start_date = DateUtil.stringToDate(request.getParameter("activity_start_date"));
        Date activity_end_date = DateUtil.stringToDate(request.getParameter("activity_end_date"));
        BigDecimal order_amount = new BigDecimal(request.getParameter("order_amount"));
        String special_right_remark = request.getParameter("special_right_remark");
        ActivityExtend activityExtend = activityService.activityDetail(activity_id);
        activityExtend.setActivity_name(activity_name);
        activityExtend.setActivity_start_date(activity_start_date);
        activityExtend.setActivity_end_date(DateUtil.endOfTheDay(activity_end_date));
        ActivitySpecialRight activitySpecialRight = activityExtend.getActivitySpecialRight();
        activitySpecialRight.setSpecial_right_remark(special_right_remark);
        activitySpecialRight.setOrder_amount(order_amount);
        activityService.updateActivitySpecialRight(activityExtend, activitySpecialRight);
        ActivityComponents.clear();
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 新增特价权活动商品
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/activity/createSpecialRightCommodity")
    public void addActivitySpecialRightCommodity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivitySpecialRightCommodity activitySpecialRightCommodity = Request2ModelUtils.covert(ActivitySpecialRightCommodity.class, request);
        activitySpecialRightCommodity.setSpecial_right_remaining_num(activitySpecialRightCommodity.getSpecial_right_num());
        activitySpecialRightCommodity.setSpecial_right_sell_num(0);
        activitySpecialRightCommodity.setSpecial_right_commodity_status(1);
        activityService.createActivitySpecialCommodity(activitySpecialRightCommodity);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 编辑特价权活动商品
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/activity/updateActivitySpecialRightCommodity")
    public void updateActivitySpecialRightCommodity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivitySpecialRightCommodity activitySpecialRightCommodity = Request2ModelUtils.covert(ActivitySpecialRightCommodity.class, request);
        ActivitySpecialRightCommodity activitySpecialRightCommodity1 = activityService.getSpecialRightCommodityById(activitySpecialRightCommodity.getActivity_special_right_commodity_id());
        activitySpecialRightCommodity.setSpecial_right_sell_num(activitySpecialRightCommodity1.getSpecial_right_sell_num());
        activitySpecialRightCommodity.setSpecial_right_remaining_num(activitySpecialRightCommodity.getSpecial_right_num()
                - activitySpecialRightCommodity.getSpecial_right_sell_num());
        activityService.updateActivitySpecialCommodity(activitySpecialRightCommodity);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 特价活动商品报表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/activity/activityCommodityReports")
    public void activityCommodityReports(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivitySearchOption activitySearchOption = Request2ModelUtils.covert(ActivitySearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(activitySearchOption);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(activitySearchOption);
        List<ActivityCommodityExtend> list = activityService.activityCommodityReports(activitySearchOption);
        Integer count = activityService.countActivityCommodityReports(activitySearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, activitySearchOption);
    }

    @RequestMapping("/activity/activity/activityCommodityReportsExport")
    public void activityCommodityReportsExport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivitySearchOption activitySearchOption = Request2ModelUtils.covert(ActivitySearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionDateInit(activitySearchOption);
        if (activitySearchOption.getParam() != null && !activitySearchOption.getParam().equals("")) {
            activitySearchOption.setParam(URLDecoder.decode(activitySearchOption.getParam(), "utf-8"));
        }
        System.out.println(JSONObject.toJSONString(activitySearchOption));
        List<ActivityCommodityExtend> list = activityService.activityCommodityReports(activitySearchOption);
        try {
            OutputStream outputStream = response.getOutputStream();
            response.reset();
            response.setHeader("Content-Type", "application/msexcel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("特价活动报表").getBytes("utf-8"),"iso8859-1") + ".xls");
            response.setHeader("Cache-Control", "max-age=0");
            WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
            WritableSheet sheet = workbook.createSheet("sheet1", 0);

            //WritableCellFormat wcf_left_format = new WritableCellFormat(new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD));
            //1正常；0逾期；-1冻结
            int row = 0;
            Counter col = new Counter();
            sheet.addCell(new Label(col.getN(), row, "活动名称"));//
            sheet.addCell(new Label(col.getN(), row, "开始时间"));//
            sheet.addCell(new Label(col.getN(), row, "结束时间"));//
            sheet.addCell(new Label(col.getN(), row, "类型"));//
            sheet.addCell(new Label(col.getN(), row, "货号"));//
            sheet.addCell(new Label(col.getN(), row, "名称"));//
            sheet.addCell(new Label(col.getN(), row, "采购价"));//
            sheet.addCell(new Label(col.getN(), row, "活动采购价"));//
            sheet.addCell(new Label(col.getN(), row, "销货价"));//
            sheet.addCell(new Label(col.getN(), row, "活动价"));//
            sheet.addCell(new Label(col.getN(), row, "供应商"));//
            sheet.addCell(new Label(col.getN(), row, "活动备注"));//
            row++;
            for (int i = 0; i < list.size(); i++) {
                col.reset();
                ActivityCommodityExtend activityCommodityExtend = list.get(i);
                sheet.addCell(new Label(col.getN(), row, activityCommodityExtend.getActivity().getActivity_name()));//
                sheet.addCell(new Label(col.getN(), row, DateUtil.format(activityCommodityExtend.getActivity().getActivity_start_date(), "yyyy-MM-dd")));//
                sheet.addCell(new Label(col.getN(), row, DateUtil.format(activityCommodityExtend.getActivity().getActivity_end_date(), "yyyy-MM-dd")));//
                ActivityDetail activityDetail = activityCommodityExtend.getActivityDetail();
                Integer activity_type = activityDetail.getActivity_type();
                if (activity_type == 1) {
                    sheet.addCell(new Label(col.getN(), row, "部分折扣"));//
                }else if (activity_type == 2) {
                    sheet.addCell(new Label(col.getN(), row, "单个调额"));//
                }

                sheet.addCell(new Label(col.getN(), row, activityCommodityExtend.getCommodity().getCommodity_code()));//
                sheet.addCell(new Label(col.getN(), row, activityCommodityExtend.getCommodity().getCommodity_name()));//
                sheet.addCell(new Number(col.getN(), row, activityCommodityExtend.getCommodity().getPurchase_price().doubleValue()));//
                sheet.addCell(new Number(col.getN(), row, activityCommodityExtend.getActivity_purchase_price().doubleValue()));//
                sheet.addCell(new Number(col.getN(), row, activityCommodityExtend.getCommodity().getSell_price().doubleValue()));//
                sheet.addCell(new Number(col.getN(), row, activityCommodityExtend.getActivity_sell_price().doubleValue()));//
                sheet.addCell(new Label(col.getN(), row, activityCommodityExtend.getSupplier().getSupplier_name()));//
                sheet.addCell(new Label(col.getN(), row, activityCommodityExtend.getActivity().getActivity_desc()));//
                row++;
            }
            ResponseUtil.exportResponse(request, response, workbook);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 特价商品导入
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity/activity/commodityImport/{activity_id}")
    public void activityCommodityImport(@PathVariable("activity_id") int activity_id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivityExtend activity = activityService.activityDetail(activity_id);
        Integer activity_type = activity.getActivity_type();
        if (activity_type != ActivityConstant.ACTIVITY_TYPE_SPECIAL_OFFER) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014"));
            return;
        }
        ActivityDetail activityDetail = activity.getActivityDetail();
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        if(multipartResolver.isMultipart(request)) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            Iterator iterator = multiRequest.getFileNames();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                MultipartFile file = multiRequest.getFile(key);
                if (file != null) {
                    String filename = file.getOriginalFilename();
                    String[] s = filename.split("\\.");
                    String ext = s[s.length - 1];
                    if (ext.equals("xls")) {//
                        //开始处理文件
                        CheckFile checkFile = checkFile(file, activityDetail.getActivity_type());
                        if (!checkFile.getResult().equals("")) {
                            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", checkFile.getResult()));
                            return;
                        }
                        activityService.importSpecialOfferActivityCommodity(activity, checkFile.getMap());
                        ResponseUtil.sendSuccessResponse(request, response);
                        UploadUtil.saveFile(file, filename, employee, request.getRequestURI());
                    }else {
                        ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "需要导入xls文件"));
                    }
                }
            }
        }
    }

    private CheckFile checkFile(MultipartFile file, int SpecialOfferType) {
        Map<Integer, Object[]> map = new HashMap<>();
        try {
            Workbook workbook = Workbook.getWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheet(0);
            int rows = sheet.getRows();
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 1; i < rows; i++) {
                Cell[] cell = sheet.getRow(i);
                String commodity_code = cell[1].getContents().trim();
                CommodityExtend commodityByCode = CommodityComponent.getCommodityByCode(commodity_code);

                BigDecimal activity_price = null;
                if (cell.length > 10 && cell[10].getContents() != null) {
                    try {
                        activity_price = new BigDecimal(cell[10].getContents().trim());
                    }catch (Exception e) {

                    }
                }
                if (SpecialOfferType == 2 && activity_price == null) {//降价活动
                    stringBuffer.append("第").append(i).append("行,货号为").append(commodity_code).append("的商品未填写活动价。");
                    break;
                }
                BigDecimal activity_purchase_price = null;
                if (cell.length > 11 && cell[11].getContents() != null) {
                    try {
                        activity_purchase_price = new BigDecimal(cell[11].getContents().trim());
                    }catch (Exception e) {

                    }
                }
                if (activity_purchase_price == null || activity_purchase_price.doubleValue() <= 0) {
                    activity_purchase_price = commodityByCode.getPurchase_price();
                }
                int sample = 0;
                if (cell.length > 12 && cell[12].getContents() != null) {
                    sample = new BigDecimal(cell[12].getContents().trim()).intValue();
                }else {
                    stringBuffer.append("第").append(i).append("行,货号为").append(commodity_code).append("的商品未填写是否样品。");
                    break;
                }

                String remark = "";
                if (cell.length > 13 && cell[13].getContents() != null) {
                    remark = cell[13].getContents().trim();
                }
                map.put(commodityByCode.getCommodity_id(), new Object[]{activity_price, commodityByCode.getSell_price(), activity_purchase_price, sample, remark});
            }
            return new CheckFile(map, stringBuffer.toString());
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new CheckFile(null, "无法读取文件");
    }

    private class CheckFile {
        public CheckFile(Map<Integer, Object[]> map, String result) {
            this.map = map;
            this.result = result;
        }

        private Map<Integer, Object[]> map;
        private String result;

        public Map<Integer, Object[]> getMap() {
            return map;
        }

        public void setMap(Map<Integer, Object[]> map) {
            this.map = map;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }

}
