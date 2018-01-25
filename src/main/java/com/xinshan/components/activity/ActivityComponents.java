package com.xinshan.components.activity;

import com.xinshan.model.*;
import com.xinshan.model.extend.activity.ActivityCommodityExtend;
import com.xinshan.model.extend.activity.ActivityExtend;
import com.xinshan.model.extend.activity.SellLimitExtend;
import com.xinshan.model.extend.commodity.CommodityActivity;
import com.xinshan.model.extend.order.OrderCommodityExtend;
import com.xinshan.model.extend.order.OrderExtend;
import com.xinshan.model.extend.order.OrderReturnCommodityExtend;
import com.xinshan.model.extend.order.OrderReturnExtend;
import com.xinshan.service.ActivityService;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.Request2ModelUtils;
import com.xinshan.utils.SplitUtils;
import com.xinshan.utils.constant.ConstantUtils;
import com.xinshan.utils.constant.activity.ActivityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by mxt on 17-2-10.
 */
@Component
public class ActivityComponents {
    private static ActivityService activityService;

    @Autowired
    public void setActivityService(ActivityService activityService) {
        ActivityComponents.activityService = activityService;
    }

    private static List<ActivityCommodityExtend> list = new ArrayList<>();
    private static List<ActivityExtend> activityExtends = new ArrayList<>();
    private static Set<Integer> sellLimitCommodities = new HashSet<>();

    private static void init() {
        list = activityService.activityCommodities(null);
        activityExtends = activityService.activityList(null);

        sellLimitCommodities.clear();
        for (int i = 0; i < activityExtends.size(); i++) {
            ActivityExtend activityExtend = activityExtends.get(i);
            if (activityExtend.getActivity_type() == ActivityConstant.ACTIVITY_TYPE_SELL_LIMIT) {
                if (available(activityExtend)) {
                    List<SellLimitExtend> sellLimits = activityExtend.getSellLimits();
                    for (int j = 0; j < sellLimits.size(); j++) {
                        SellLimitExtend sellLimitExtend = sellLimits.get(j);
                        sellLimitCommodities.add(sellLimitExtend.getCommodity_id());
                    }
                }
            }
        }
    }

    private static List<ActivityCommodityExtend> getList() {
        if (list == null || list.size() == 0) {
            init();
        }
        return list;
    }

    public static List<ActivityExtend> getActivityExtends() {
        if (activityExtends == null || activityExtends.size() == 0) {
            init();
        }
        return activityExtends;
    }

    public static Set<Integer> getSellLimitCommodities() {
        if (sellLimitCommodities == null || sellLimitCommodities.size() == 0) {
            init();
        }
        return sellLimitCommodities;
    }

    public static void clear() {
        activityExtends.clear();
        list.clear();
        sellLimitCommodities.clear();
    }

    /**
     * 商品适用的当前活动
     * 优先级
     * 1、全部商品
     * 2、所属供应商活动
     * 3、所属系列活动
     * 4、商品活动
     * @param commodity
     *
     * @return
     */
    public static CommodityActivity getByCommodity(Commodity commodity) {
        Date date = DateUtil.currentDate();
        // 1、全部商品
        List<ActivityExtend> activityExtends = getActivityExtends();
        for (int i = 0; i < activityExtends.size(); i++) {
            ActivityExtend activityExtend = activityExtends.get(i);
            int activity_status = activityExtend.getActivity_status();
            if (activity_status == ConstantUtils.ENABLE_STATUS
                    && activityExtend.getActivity_type() == ActivityConstant.ACTIVITY_TYPE_SPECIAL_OFFER
                    && activityExtend.getActivityDetail() != null) {
                Date startDate = activityExtend.getActivity_start_date();
                Date endDate = DateUtil.endOfTheDay(activityExtend.getActivity_end_date());
                Integer all = activityExtend.getActivityDetail().getAll();
                if (startDate.before(date) && date.before(endDate) && all != null && all == 1) {
                    CommodityActivity activity = new CommodityActivity(activityExtend);
                    activity.setActivityDetail(activityExtend.getActivityDetail());
                    activity.setActivityCommodity(null);
                    return activity;
                }
            }
        }

        // 2、所属供应商活动 暂时没有
        // 3、所属系列活动 暂时没有

        // 4、商品活动
        List<ActivityCommodityExtend> list = getList();
        for (int i = 0; i < list.size(); i++) {
            ActivityCommodityExtend activityCommodityExtend = list.get(i);
            int activity_status = activityCommodityExtend.getActivity().getActivity_status();
            if (activity_status == ConstantUtils.ENABLE_STATUS
                    && activityCommodityExtend.getActivity_commodity_status() == ConstantUtils.ENABLE_STATUS
                    && activityCommodityExtend.getActivity().getActivity_type() == ActivityConstant.ACTIVITY_TYPE_SPECIAL_OFFER) {
                Date startDate = activityCommodityExtend.getActivity().getActivity_start_date();
                Date endDate = activityCommodityExtend.getActivity().getActivity_end_date();
                endDate = DateUtil.endOfTheDay(endDate);
                if (startDate.before(date) && date.before(endDate)) {
                    int commodity_id = activityCommodityExtend.getCommodity_id();
                    if (commodity.getCommodity_id() == commodity_id) {
                        CommodityActivity activity = new CommodityActivity(activityCommodityExtend.getActivity());
                        ActivityCommodity activityCommodity = new ActivityCommodity();
                        Request2ModelUtils.covertObj(activityCommodity, activityCommodityExtend);
                        activity.setActivityCommodity(activityCommodity);
                        return activity;
                    }
                }
            }
        }

        for (int i = 0; i < activityExtends.size(); i++) {
            ActivityExtend activityExtend = activityExtends.get(i);
            CommodityActivity commodityActivity = sellLimitCommodityActivity(commodity, activityExtend);
            if (commodityActivity != null) {
                return commodityActivity;
            }
        }
        return null;
    }

    private static CommodityActivity sellLimitCommodityActivity(Commodity commodity, ActivityExtend activityExtend) {
        Date date = DateUtil.currentDate();
        if (activityExtend.getActivity_status() == ConstantUtils.ENABLE_STATUS && activityExtend.getActivity_type() == ActivityConstant.ACTIVITY_TYPE_SELL_LIMIT) {
            Date startDate = activityExtend.getActivity_start_date();
            Date endDate = DateUtil.endOfTheDay(activityExtend.getActivity_end_date());
            if (startDate.before(date) && date.before(endDate)) {
                List<SellLimitExtend> sellLimits = activityExtend.getSellLimits();
                for (int j = 0; j < sellLimits.size(); j++) {
                    ActivitySellLimit activitySellLimit = sellLimits.get(j);
                    if (activitySellLimit.getCommodity_id().equals(commodity.getCommodity_id())) {
                        CommodityActivity activity = new CommodityActivity(activityExtend);
                        activity.setActivitySellLimit(activitySellLimit);
                        return activity;
                    }
                }
            }
        }
        return null;
    }

    public static CommodityActivity sellLimitCommodityActivity(Commodity commodity) {
        List<ActivityExtend> activityExtends = getActivityExtends();
        for (int i = 0; i < activityExtends.size(); i++) {
            ActivityExtend activityExtend = activityExtends.get(i);
            CommodityActivity commodityActivity = sellLimitCommodityActivity(commodity, activityExtend);
            if (commodityActivity != null) {
                return commodityActivity;
            }
        }
        return null;
    }

    /**
     * 当前活动
     */
    public static List<ActivityCommodityExtend> currentEnableActivity() {
        Date date = DateUtil.currentDate();
        List<ActivityCommodityExtend> activityCommodityExtends = new ArrayList<>();
        List<ActivityCommodityExtend> list = getList();
        for (int i = 0; i < list.size(); i++) {
            ActivityCommodityExtend activityCommodityExtend = list.get(i);
            int activity_status = activityCommodityExtend.getActivity().getActivity_status();
            if (activity_status == ConstantUtils.ENABLE_STATUS
                    && activityCommodityExtend.getActivity_commodity_status() == ConstantUtils.ENABLE_STATUS
                    && activityCommodityExtend.getActivity().getActivity_type() == ActivityConstant.ACTIVITY_TYPE_SPECIAL_OFFER) {
                Date startDate = activityCommodityExtend.getActivity().getActivity_start_date();
                Date endDate = activityCommodityExtend.getActivity().getActivity_end_date();
                endDate = DateUtil.endOfTheDay(endDate);
                if (startDate.before(date) && date.before(endDate)) {
                    activityCommodityExtends.add(activityCommodityExtend);
                }
            }
        }
        return activityCommodityExtends;
    }
    public static List<ActivityExtend> commodityActivities(int activity_type, Commodity commodity) {
        List<ActivityExtend> activities = new ArrayList<>();
        Date date = DateUtil.currentDate();
        List<ActivityExtend> list = getActivityExtends();
        for (int i = 0; i < list.size(); i++) {
            ActivityExtend activityExtend = activityExtends.get(i);
            if (activityExtend.getActivity_type() == activity_type) {
                if (activityExtend.getActivity_status() == ConstantUtils.ENABLE_STATUS) {//活动状态正确
                    Date startDate = activityExtend.getActivity_start_date();
                    Date endDate = DateUtil.endOfTheDay(activityExtend.getActivity_end_date());
                    if (startDate.before(date) && date.before(endDate)) {//活动时间正确
                        boolean b = activityCommodity(activityExtend, commodity);
                        if (b) {
                            activities.add(activityExtend);
                        }
                    }
                }
            }
        }
        return activities;
    }
    public static List<Activity> commodityActivities(Commodity commodity) {
        List<Activity> activities = new ArrayList<>();
        Date date = DateUtil.currentDate();
        List<ActivityExtend> list = getActivityExtends();
        for (int i = 0; i < list.size(); i++) {
            ActivityExtend activityExtend = activityExtends.get(i);
            int activity_status = activityExtend.getActivity_status();
            if (activity_status == ConstantUtils.ENABLE_STATUS) {//活动状态正确
                Date startDate = activityExtend.getActivity_start_date();
                Date endDate = DateUtil.endOfTheDay(activityExtend.getActivity_end_date());
                if (startDate.before(date) && date.before(endDate)) {//活动时间正确
                    boolean b = activityCommodity(activityExtend, commodity);
                    if (b) {
                        activities.add(easyActivity(activityExtend));
                    }
                }
            }
        }
        return activities;
    }

    public static Activity activity(int activity_id) {
        List<ActivityExtend> activityExtends = getActivityExtends();
        for (int i = 0; i < activityExtends.size(); i++) {
            ActivityExtend activityExtend = activityExtends.get(i);
            if (activityExtend.getActivity_id() == activity_id) {
                return easyActivity(activityExtend);
            }
        }
        return null;
    }

    private static ActivityExtend easyActivity(Activity activity) {
        ActivityExtend activ = new ActivityExtend();
        activ.setActivity_name(activity.getActivity_name());
        activ.setActivity_type(activity.getActivity_type());
        activ.setActivity_id(activity.getActivity_id());
        return activ;
    }

    /**
     * 是否活动商品
     * @param activity
     */
    private static boolean activityCommodity(ActivityExtend activity, Commodity commodity) {
        boolean activityCommodity = false;
        Integer commodity_id = commodity.getCommodity_id();
        int activity_type = activity.getActivity_type();
        switch (activity_type) {
            case ActivityConstant.ACTIVITY_TYPE_SPECIAL_OFFER:
                ActivityDetail activityDetail = activity.getActivityDetail();
                if (activityDetail.getAll() == 1) {
                    activityCommodity = true;
                }else {
                    String commodity_ids = activityDetail.getCommodity_ids();
                    List<Integer> list = SplitUtils.splitToList(commodity_ids, ",");
                    if (list.contains(commodity_id)) {
                        activityCommodity = true;
                    }
                }
                break;
            case ActivityConstant.ACTIVITY_TYPE_VALUE_ADDED:
                activityCommodity = true;
                break;
            case ActivityConstant.ACTIVITY_TYPE_CASH_BACK:
                ActivityCashBack activityCashBack = activity.getActivityCashBack();
                if (activityCashBack.getCash_back_all() == 1) {
                    activityCommodity = true;
                }
                if (!activityCommodity) {
                    String cash_back_supplier_ids = activityCashBack.getCash_back_supplier_ids();
                    if (cash_back_supplier_ids != null && !cash_back_supplier_ids.equals("")) {
                        List<Integer> supplierIds = SplitUtils.splitToList(cash_back_supplier_ids, ",");
                        if (supplierIds.contains(commodity.getSupplier_id())) {
                            activityCommodity = true;
                        }
                    }
                }
                if (!activityCommodity) {
                    String commodity_ids = activityCashBack.getCash_back_commodity_ids();
                    List<Integer> list = SplitUtils.splitToList(commodity_ids, ",");
                    if (list.contains(commodity_id)) {
                        activityCommodity = true;
                    }
                }
                break;
            case ActivityConstant.ACTIVITY_TYPE_SELL_LIMIT:
                List<SellLimitExtend> sellLimits = activity.getSellLimits();
                for (int i = 0; i < sellLimits.size(); i++) {
                    ActivitySellLimit activitySellLimit = sellLimits.get(i);
                    Integer commodity_id1 = activitySellLimit.getCommodity_id();
                    if (commodity_id.equals(commodity_id1)) {
                        activityCommodity = true;
                    }
                }
                break;
            default:
                break;
        }
        return activityCommodity;
    }

    /**
     *
     * @param orderExtend
     */
    public static void sellLimit(OrderExtend orderExtend) {
        List<OrderCommodityExtend> orderCommodities = orderExtend.getOrderCommodities();
        for (int i = 0; i < orderCommodities.size(); i++) {
            OrderCommodityExtend orderCommodityExtend = orderCommodities.get(i);
            if (getSellLimitCommodities().contains(orderCommodityExtend.getCommodity_id())) {
                clear();
                break;
            }
        }
    }

    public static void sellLimit(OrderReturnExtend orderReturnExtend) {
        List<OrderReturnCommodityExtend> orderReturnCommodities = orderReturnExtend.getOrderReturnCommodities();
        for (int i = 0; i < orderReturnCommodities.size(); i++) {
            OrderReturnCommodityExtend orderReturnCommodityExtend = orderReturnCommodities.get(i);
            if (getSellLimitCommodities().contains(orderReturnCommodityExtend.getCommodity_id())) {
                clear();
            }
        }
    }

    public static boolean available(ActivityExtend activityExtend) {
        Date date = DateUtil.currentDate();
        int activity_status = activityExtend.getActivity_status();
        if (activity_status == ConstantUtils.ENABLE_STATUS) {//活动状态正确
            Date startDate = activityExtend.getActivity_start_date();
            Date endDate = DateUtil.endOfTheDay(activityExtend.getActivity_end_date());
            if (startDate.before(date) && date.before(endDate)) {//活动时间正确
                return true;
            }
        }
        return false;
    }
}
