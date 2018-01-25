package com.xinshan.model.extend.activity;

import com.xinshan.model.*;

/**
 * Created by mxt on 17-2-10.
 */
public class ActivityCommodityExtend extends ActivityCommodity {
    private Commodity commodity;
    private Supplier supplier;
    private Activity activity;
    private ActivityDetail activityDetail;

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public ActivityDetail getActivityDetail() {
        return activityDetail;
    }

    public void setActivityDetail(ActivityDetail activityDetail) {
        this.activityDetail = activityDetail;
    }
}
