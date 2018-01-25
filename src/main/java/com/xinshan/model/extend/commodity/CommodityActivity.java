package com.xinshan.model.extend.commodity;

import com.xinshan.model.Activity;
import com.xinshan.model.ActivityCommodity;
import com.xinshan.model.ActivityDetail;
import com.xinshan.model.ActivitySellLimit;
import com.xinshan.utils.DateUtil;

/**
 * Created by mxt on 17-3-21.
 */
public class CommodityActivity extends Activity {
    public CommodityActivity() {

    }

    public CommodityActivity(Activity activity) {
        setActivity_status(activity.getActivity_status());
        setActivity_create_date(activity.getActivity_create_date());
        setActivity_create_employee_code(activity.getActivity_create_employee_code());
        setActivity_create_employee_name(activity.getActivity_create_employee_name());
        setActivity_desc(activity.getActivity_desc());
        setActivity_end_date(DateUtil.endOfTheDay(activity.getActivity_end_date()));
        setActivity_id(activity.getActivity_id());
        setActivity_name(activity.getActivity_name());
    }

    private ActivityDetail activityDetail;
    private ActivityCommodity activityCommodity;
    private ActivitySellLimit activitySellLimit;

    public ActivitySellLimit getActivitySellLimit() {
        return activitySellLimit;
    }

    public void setActivitySellLimit(ActivitySellLimit activitySellLimit) {
        this.activitySellLimit = activitySellLimit;
    }

    public ActivityDetail getActivityDetail() {
        return activityDetail;
    }

    public void setActivityDetail(ActivityDetail activityDetail) {
        this.activityDetail = activityDetail;
    }

    public ActivityCommodity getActivityCommodity() {
        return activityCommodity;
    }

    public void setActivityCommodity(ActivityCommodity activityCommodity) {
        this.activityCommodity = activityCommodity;
    }
}
