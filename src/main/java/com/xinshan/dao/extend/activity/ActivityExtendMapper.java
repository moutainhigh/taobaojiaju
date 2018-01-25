package com.xinshan.dao.extend.activity;

import com.xinshan.model.*;
import com.xinshan.model.extend.activity.ActivityCommodityExtend;
import com.xinshan.model.extend.activity.ActivityExtend;
import com.xinshan.model.extend.activity.SellLimitExtend;
import com.xinshan.model.extend.activity.SpecialRightCommodityExtend;
import com.xinshan.pojo.activity.ActivitySearchOption;

import java.util.List;

/**
 * Created by mxt on 17-2-10.
 */
public interface ActivityExtendMapper {
    void createActivity(Activity activity);
    void createActivityDetail(ActivityDetail activityDetail);
    void createActivityCommodity(ActivityCommodity activityCommodity);
    void createActivityValueAdded(ActivityValueAdded activityValueAdded);
    void createActivityGoldEgg(ActivityGoldEgg activityGoldEgg);
    void createActivityBrand(ActivityBrand activityBrand);
    void createActivitySpecial(ActivitySpecialRight activitySpecialRight);
    void createActivitySpecialCommodity(ActivitySpecialRightCommodity activitySpecialRightCommodity);
    void createActivitySellLimit(ActivitySellLimit activitySellLimit);

    void createCashBack(ActivityCashBack activityCashBack);

    List<Integer> activityIds(ActivitySearchOption activitySearchOption);

    ActivityExtend activityDetail(int activity_id);

    ActivityDetail getActivityDetail(int activity_id);

    Integer countActivity(ActivitySearchOption activitySearchOption);

    List<ActivityCommodityExtend> activityCommodities(ActivitySearchOption activitySearchOption);

    List<ActivityCommodityExtend> activityCommodityList(ActivitySearchOption activitySearchOption);
    Integer countActivityCommodityExtends(ActivitySearchOption activitySearchOption);

    ActivityValueAdded getValueAddedByActivityId(int activity_id);

    void updateActivityDetail(ActivityDetail activityDetail);

    ActivityCashBack getActivityCashBack(int activity_id);
    ActivityGoldEgg getActivityGoldEgg(int activity_id);
    ActivityBrand getActivityBrand(int activity_id);
    ActivitySpecialRight getActivitySpecial(int activity_id);
    List<SpecialRightCommodityExtend> specialRightCommodities(ActivitySearchOption activitySearchOption);
    List<SellLimitExtend> sellLimits(int activity_id);
    ActivitySellLimit getActivitySellLimit(int activity_id, int commodity_id);

    List<ActivityCommodityExtend> activityCommodityReports(ActivitySearchOption activitySearchOption);
    Integer countActivityCommodityReports(ActivitySearchOption activitySearchOption);
}
