package com.xinshan.service;

import com.xinshan.components.commodity.CommodityComponent;
import com.xinshan.dao.*;
import com.xinshan.dao.extend.activity.ActivityExtendMapper;
import com.xinshan.model.*;
import com.xinshan.model.extend.activity.ActivityCommodityExtend;
import com.xinshan.model.extend.activity.ActivityExtend;
import com.xinshan.model.extend.activity.SellLimitExtend;
import com.xinshan.model.extend.activity.SpecialRightCommodityExtend;
import com.xinshan.model.extend.supplier.SupplierExtend;
import com.xinshan.pojo.supplier.SupplierSearchOption;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.SplitUtils;
import com.xinshan.utils.constant.ConstantUtils;
import com.xinshan.pojo.activity.ActivitySearchOption;
import com.xinshan.utils.constant.activity.ActivityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by mxt on 17-2-10.
 */
@Service
public class ActivityService {
    @Autowired
    private ActivityExtendMapper activityExtendMapper;
    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private ActivityCommodityMapper activityCommodityMapper;
    @Autowired
    private CommodityService commodityService;
    @Autowired
    private ActivityCashBackMapper activityCashBackMapper;
    @Autowired
    private ActivityGoldEggMapper activityGoldEggMapper;
    @Autowired
    private ActivityBrandMapper activityBrandMapper;
    @Autowired
    private ActivitySpecialRightMapper activitySpecialRightMapper;
    @Autowired
    private ActivitySpecialRightCommodityMapper activitySpecialRightCommodityMapper;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private ActivitySellLimitMapper activitySellLimitMapper;

    public Activity getActivityById(int activity_id) {
        return activityMapper.selectByPrimaryKey(activity_id);
    }

    @Transactional
    public void updateActivity(Activity activity) {
        activityMapper.updateByPrimaryKey(activity);
    }

    @Transactional
    public void updateActivityCommodity(ActivityCommodity activityCommodity) {
        activityCommodityMapper.updateByPrimaryKey(activityCommodity);
    }

    @Transactional
    public void createActivity(ActivityExtend activityExtend, Employee employee) {
        Date date = DateUtil.currentDate();
        activityExtend.setActivity_create_employee_code(employee.getEmployee_code());
        activityExtend.setActivity_create_employee_name(employee.getEmployee_name());
        activityExtend.setActivity_create_date(date);
        activityExtend.setActivity_status(ConstantUtils.ENABLE_STATUS);
        activityExtend.setActivity_end_date(DateUtil.endOfTheDay(activityExtend.getActivity_end_date()));
        if (activityExtend.getActivity_type() == null) {
            activityExtend.setActivity_type(ActivityConstant.ACTIVITY_TYPE_SPECIAL_OFFER);//默认特价活动
        }
        activityExtendMapper.createActivity(activityExtend);
        int activity_type = activityExtend.getActivity_type();
        switch (activity_type) {
            case ActivityConstant.ACTIVITY_TYPE_SPECIAL_OFFER:
                specialOfferActivity(activityExtend, date);
                break;
            case ActivityConstant.ACTIVITY_TYPE_VALUE_ADDED:
                valueAddedActivity(activityExtend, date);
                break;
            case ActivityConstant.ACTIVITY_TYPE_CASH_BACK:
                cashBackActivity(activityExtend);
                break;
            case ActivityConstant.ACTIVITY_TYPE_GOLD_EGG:
                goldEggActivity(activityExtend);
                break;
            case ActivityConstant.ACTIVITY_TYPE_BRAND:
                brandActivity(activityExtend);
                break;
            case ActivityConstant.ACTIVITY_TYPE_SPECIAL_RIGHT:
                specialRightActivity(activityExtend);
                break;
            case ActivityConstant.ACTIVITY_TYPE_GIFT:

                break;
            case ActivityConstant.ACTIVITY_TYPE_SELL_LIMIT:
                sellLimit(activityExtend);
            default:
                break;
        }
    }

    /**
     * 特价活动详情
     * @param activityExtend
     * @param date
     */
    private void specialOfferActivity(ActivityExtend activityExtend, Date date) {
        ActivityDetail activityDetail = activityExtend.getActivityDetail();
        activityDetail.setActivity_id(activityExtend.getActivity_id());
        Set<Integer> set = new HashSet<>();
        List<ActivityCommodityExtend> list = activityExtend.getActivityCommodities();
        for (int i = 0; i < list.size(); i++) {
            ActivityCommodity activityCommodity = list.get(i);
            int commodity_id = activityCommodity.getCommodity_id();
            if (!set.contains(commodity_id)) {
                Commodity commodity = commodityService.getCommodityById(commodity_id);
                activityCommodity.setActivity_id(activityExtend.getActivity_id());
                activityCommodity.setActivity_commodity_status(ConstantUtils.ENABLE_STATUS);
                activityCommodity.setActivity_commodity_create_date(date);
                if (activityDetail.getActivity_discount() != null && activityDetail.getActivity_type() == 1) {
                    activityCommodity.setActivity_sell_price(commodity.getSell_price().multiply(activityDetail.getActivity_discount()));
                }
                activityCommodity.setSell_price(commodity.getSell_price());
                activityCommodity.setPurchase_price(commodity.getPurchase_price());
                activityExtendMapper.createActivityCommodity(activityCommodity);
                commodity.setActivity_commodity_id(activityCommodity.getActivity_commodity_id());
                commodityService.updateCommodity(commodity);
            }
            set.add(commodity_id);
        }
        activityDetail.setCommodity_ids(SplitUtils.setToString(set));
        activityExtendMapper.createActivityDetail(activityDetail);
    }

    /**
     * 导入特价商品
     * @param activityExtend
     * @param map
     */
    @Transactional
    public void importSpecialOfferActivityCommodity(ActivityExtend activityExtend, Map<Integer, Object[]> map) {
        ActivityDetail activityDetail = activityExtend.getActivityDetail();
        activityDetail.setActivity_id(activityExtend.getActivity_id());
        List<ActivityCommodityExtend> list = activityExtend.getActivityCommodities();
        for (int i = 0; i < list.size(); i++) {
            ActivityCommodityExtend activityCommodityExtend = list.get(i);
            Integer commodity_id = activityCommodityExtend.getCommodity_id();
            if (map.containsKey(commodity_id)) {
                Object[] value = map.get(commodity_id);
                BigDecimal activityPrice = (BigDecimal) value[0];
                BigDecimal sellPrice = (BigDecimal) value[1];
                BigDecimal purchasePrice = (BigDecimal) value[2];
                int sample = (int) value[3];
                String remark = (String) value[4];
                if (activityDetail.getActivity_discount() != null && activityDetail.getActivity_type() == 1) {
                    activityCommodityExtend.setActivity_sell_price(sellPrice.multiply(activityDetail.getActivity_discount()));
                }else {
                    activityCommodityExtend.setActivity_sell_price(activityPrice);
                }
                activityCommodityExtend.setSample(sample);
                activityCommodityExtend.setActivity_purchase_price(purchasePrice);
                activityCommodityExtend.setActivity_commodity_remark(remark);
                activityCommodityExtend.setActivity_commodity_status(ConstantUtils.ENABLE_STATUS);
                map.remove(commodity_id);
            }else {
                activityCommodityExtend.setActivity_commodity_status(ConstantUtils.DISABLE_STATUS);
            }
            activityCommodityMapper.updateByPrimaryKey(activityCommodityExtend);
        }
        Iterator<Map.Entry<Integer, Object[]>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Object[]> next = iterator.next();
            Integer key = next.getKey();
            Object[] value = next.getValue();
            BigDecimal activityPrice = (BigDecimal) value[0];
            BigDecimal sellPrice = (BigDecimal) value[1];
            BigDecimal purchasePrice = (BigDecimal) value[2];
            int sample = (int) value[3];
            String remark = (String) value[4];
            Commodity commodity = CommodityComponent.getCommodityById(key);
            ActivityCommodity activityCommodity = new ActivityCommodity();
            activityCommodity.setActivity_id(activityExtend.getActivity_id());
            activityCommodity.setActivity_commodity_status(1);
            activityCommodity.setCommodity_id(key);
            if (activityDetail.getActivity_discount() != null && activityDetail.getActivity_type() == 1) {
                activityCommodity.setActivity_sell_price(sellPrice.multiply(activityDetail.getActivity_discount()));
            }else {
                activityCommodity.setActivity_sell_price(activityPrice);
            }
            activityCommodity.setSample(sample);
            activityCommodity.setActivity_purchase_price(purchasePrice);
            activityCommodity.setActivity_commodity_remark(remark);
            activityCommodity.setSell_price(commodity.getSell_price());
            activityCommodity.setPurchase_price(commodity.getPurchase_price());
            activityExtendMapper.createActivityCommodity(activityCommodity);
            commodity.setActivity_commodity_id(activityCommodity.getActivity_commodity_id());
            commodityService.updateCommodity(commodity);
        }

        activityDetail.setCommodity_ids(SplitUtils.setToString(map.keySet()));
        activityExtendMapper.updateActivityDetail(activityDetail);
    }

    /**
     * 增值卡活动
     * @param activityExtend
     * @param date
     */
    public void valueAddedActivity(ActivityExtend activityExtend, Date date) {
        ActivityValueAdded activityValueAdded = activityExtend.getActivityValueAdd();
        if (activityValueAdded == null) {
            activityValueAdded = new ActivityValueAdded();
        }
        activityValueAdded.setActivity_id(activityExtend.getActivity_id());
        activityExtendMapper.createActivityValueAdded(activityValueAdded);
    }

    /**
     * 返现活动
     * @param activityExtend
     */
    public void cashBackActivity(ActivityExtend activityExtend) {
        ActivityCashBack activityCashBack = activityExtend.getActivityCashBack();
        activityCashBack.setActivity_id(activityExtend.getActivity_id());
        activityExtendMapper.createCashBack(activityCashBack);
    }

    public void goldEggActivity(ActivityExtend activityExtend) {
        ActivityGoldEgg activityGoldEgg = activityExtend.getActivityGoldEgg();
        activityGoldEgg.setActivity_id(activityExtend.getActivity_id());
        activityExtendMapper.createActivityGoldEgg(activityGoldEgg);
    }

    public void brandActivity(ActivityExtend activityExtend) {
        ActivityBrand activityBrand = activityExtend.getActivityBrand();
        activityBrand.setActivity_id(activityExtend.getActivity_id());
        activityExtendMapper.createActivityBrand(activityBrand);
    }

    public void specialRightActivity(ActivityExtend activityExtend) {
        ActivitySpecialRight activitySpecialRight = activityExtend.getActivitySpecialRight();
        activitySpecialRight.setActivity_id(activityExtend.getActivity_id());
        List<Integer> commodityIds = new ArrayList<>();
        List<SpecialRightCommodityExtend> list = activityExtend.getSpecialRightCommodities();
        for (int i = 0; i < list.size(); i++) {
            SpecialRightCommodityExtend commodity = list.get(i);
            commodity.setSpecial_right_remaining_num(commodity.getSpecial_right_num());
            commodity.setSpecial_right_sell_num(0);
            commodity.setSpecial_right_commodity_status(1);
            commodity.setActivity_id(activityExtend.getActivity_id());
            commodityIds.add(commodity.getCommodity_id());
            activityExtendMapper.createActivitySpecialCommodity(commodity);
        }
        activitySpecialRight.setSpecial_right_commodity_ids(SplitUtils.listToString(commodityIds));
        activityExtendMapper.createActivitySpecial(activitySpecialRight);
    }

    public void sellLimit(ActivityExtend activityExtend) {
        List<SellLimitExtend> sellLimits = activityExtend.getSellLimits();
        for (int i = 0; i < sellLimits.size(); i++) {
            SellLimitExtend sellLimitExtend = sellLimits.get(i);
            sellLimitExtend.setActivity_id(activityExtend.getActivity_id());
            activityExtendMapper.createActivitySellLimit(sellLimitExtend);
        }
    }

    @Transactional
    public void createActivitySpecialCommodity(ActivitySpecialRightCommodity activitySpecialRightCommodity) {
        activityExtendMapper.createActivitySpecialCommodity(activitySpecialRightCommodity);
    }

    public List<ActivityCommodityExtend> activityCommodityExtends(ActivitySearchOption activitySearchOption) {
        return activityExtendMapper.activityCommodityList(activitySearchOption);
    }

    public Integer countActivityCommodityExtends(ActivitySearchOption activitySearchOption) {
        return activityExtendMapper.countActivityCommodityExtends(activitySearchOption);
    }
    private List<ActivityCommodityExtend> activityCommodityExtends(int activity_id) {
        ActivitySearchOption activitySearchOption = new ActivitySearchOption();
        activitySearchOption.setActivity_id(activity_id);
        return activityCommodityExtends(activitySearchOption);
    }
    public ActivityExtend activityDetail(int activity_id) {
        ActivityExtend activityExtend = activityExtendMapper.activityDetail(activity_id);
        ActivitySearchOption activitySearchOption = new ActivitySearchOption();
        int activity_type = activityExtend.getActivity_type();
        switch (activity_type) {
            case ActivityConstant.ACTIVITY_TYPE_SPECIAL_OFFER:
                activityExtend.setActivityCommodities(activityCommodityExtends(activity_id));
                activityExtend.setActivityDetail(activityExtendMapper.getActivityDetail(activity_id));
                break;
            case ActivityConstant.ACTIVITY_TYPE_VALUE_ADDED:
                activityExtend.setActivityValueAdd(activityExtendMapper.getValueAddedByActivityId(activityExtend.getActivity_id()));
                break;
            case ActivityConstant.ACTIVITY_TYPE_CASH_BACK:
                cashBackActivityDetail(activityExtend);
                break;
            case ActivityConstant.ACTIVITY_TYPE_GOLD_EGG:
                activityExtend.setActivityGoldEgg(activityExtendMapper.getActivityGoldEgg(activity_id));
                break;
            case ActivityConstant.ACTIVITY_TYPE_BRAND:
                activityExtend.setActivityBrand(activityExtendMapper.getActivityBrand(activity_id));
                break;
            case ActivityConstant.ACTIVITY_TYPE_SPECIAL_RIGHT:
                activityExtend.setActivitySpecialRight(activityExtendMapper.getActivitySpecial(activity_id));
                activitySearchOption.setActivity_id(activity_id);
                activityExtend.setSpecialRightCommodities(activityExtendMapper.specialRightCommodities(activitySearchOption));
                break;
            case ActivityConstant.ACTIVITY_TYPE_GIFT:
                break;
            case ActivityConstant.ACTIVITY_TYPE_SELL_LIMIT:
                activityExtend.setSellLimits(activityExtendMapper.sellLimits(activity_id));
            default:
                break;
        }
        return activityExtend;
    }

    private void cashBackActivityDetail(ActivityExtend activity) {
        ActivityCashBack activityCashBack = activityExtendMapper.getActivityCashBack(activity.getActivity_id());
        activity.setActivityCashBack(activityCashBack);
        String cash_back_supplier_ids = activityCashBack.getCash_back_supplier_ids();
        if (cash_back_supplier_ids != null && !cash_back_supplier_ids.equals("")) {
            SupplierSearchOption supplierSearchOption = new SupplierSearchOption();
            supplierSearchOption.setSupplier_ids(SplitUtils.splitToList(cash_back_supplier_ids, ","));
            List<SupplierExtend> list = supplierService.supplierList(supplierSearchOption);
            activity.setCashBackSuppliers(list);
        }
    }

    public List<ActivityExtend> activityList(ActivitySearchOption activitySearchOption) {
        List<ActivityExtend> activityExtends = new ArrayList<>();
        List<Integer> list = activityExtendMapper.activityIds(activitySearchOption);
        for (int i = 0; i < list.size(); i++) {
            int activity_id = list.get(i);
            activityExtends.add(activityDetail(activity_id));
        }
        return activityExtends;
    }

    public Integer countActivity(ActivitySearchOption activitySearchOption) {
        return activityExtendMapper.countActivity(activitySearchOption);
    }

    public List<ActivityCommodityExtend> activityCommodities(ActivitySearchOption activitySearchOption) {
        return activityExtendMapper.activityCommodities(activitySearchOption);
    }

    public ActivityCommodityExtend getActivityCommodityById(int activity_commodity_id) {
        ActivitySearchOption activitySearchOption = new ActivitySearchOption();
        activitySearchOption.setActivity_commodity_id(activity_commodity_id);
        List<ActivityCommodityExtend> list = activityCommodities(activitySearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    @Transactional
    public void activityCommodityAdd(ActivityExtend activityExtend) {
        try {
            Date date = DateUtil.currentDate();
            ActivityExtend activityExtendOld = activityDetail(activityExtend.getActivity_id());
            List<ActivityCommodityExtend> list = activityExtendOld.getActivityCommodities();
            List<ActivityCommodityExtend> list1 = activityExtend.getActivityCommodities();
            ActivityDetail activityDetail = activityExtendOld.getActivityDetail();
            Set<Integer> set = SplitUtils.splitToSet(activityDetail.getCommodity_ids(), ",");
            for (int i = 0; i < list1.size(); i++) {
                ActivityCommodity activityCommodityNew = list1.get(i);
                ActivityCommodity activityCommodityOld = null;
                for (int j = 0; j < list.size(); j++) {
                    ActivityCommodity activityCommodity1 = list.get(j);
                    if (activityCommodityNew.getCommodity_id().equals(activityCommodity1.getCommodity_id())) {
                        activityCommodityOld = activityCommodity1;
                        break;
                    }
                }
                if (activityCommodityOld != null) {
                    activityCommodityNew.setActivity_commodity_id(activityCommodityOld.getActivity_commodity_id());
                    activityCommodityNew.setActivity_commodity_create_date(date);
                    Commodity commodity = commodityService.getCommodityById(activityCommodityNew.getCommodity_id());
                    if (activityDetail.getActivity_discount() != null && activityDetail.getActivity_type() == 1) {
                        activityCommodityNew.setActivity_sell_price(commodity.getSell_price().multiply(activityDetail.getActivity_discount()));
                    }
                    activityCommodityMapper.updateByPrimaryKey(activityCommodityNew);
                }else {
                    activityCommodityNew.setActivity_commodity_create_date(date);
                    Commodity commodity = commodityService.getCommodityById(activityCommodityNew.getCommodity_id());
                    if (activityDetail.getActivity_discount() != null && activityDetail.getActivity_type() == 1) {
                        activityCommodityNew.setActivity_sell_price(commodity.getSell_price().multiply(activityDetail.getActivity_discount()));
                    }
                    activityCommodityNew.setSell_price(commodity.getSell_price());
                    activityCommodityNew.setPurchase_price(commodity.getPurchase_price());
                    activityExtendMapper.createActivityCommodity(activityCommodityNew);
                    commodity.setActivity_commodity_id(activityCommodityNew.getActivity_commodity_id());
                    commodityService.updateCommodity(commodity);
                }
                if (activityCommodityNew.getActivity_commodity_status() == 1) {
                    set.add(activityCommodityNew.getCommodity_id());
                }
            }
            activityDetail.setCommodity_ids(SplitUtils.setToString(set));
            activityExtendMapper.updateActivityDetail(activityDetail);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void updateActivityCashBack(ActivityCashBack activityCashBack) {
        activityCashBackMapper.updateByPrimaryKey(activityCashBack);
    }

    public ActivityCashBack getActivityCashBackById(int activity_cash_back_id) {
        return activityCashBackMapper.selectByPrimaryKey(activity_cash_back_id);
    }
    @Transactional
    public void updateActivityGoldEgg(Activity activity, ActivityGoldEgg activityGoldEgg) {
        activityGoldEggMapper.updateByPrimaryKey(activityGoldEgg);
        activityMapper.updateByPrimaryKey(activity);
    }
    @Transactional
    public void updateActivityGoldEgg(ActivityGoldEgg activityGoldEgg) {
        activityGoldEggMapper.updateByPrimaryKey(activityGoldEgg);
    }

    @Transactional
    public void updateActivityBrand(Activity activity, ActivityBrand activityBrand) {
        activityMapper.updateByPrimaryKey(activity);
        activityBrandMapper.updateByPrimaryKey(activityBrand);
    }
    @Transactional
    public void updateActivitySpecialRight(Activity activity, ActivitySpecialRight activitySpecialRight) {
        activityMapper.updateByPrimaryKey(activity);
        activitySpecialRightMapper.updateByPrimaryKey(activitySpecialRight);
    }

    @Transactional
    public void updateActivitySpecialCommodity(ActivitySpecialRightCommodity activitySpecialRightCommodity) {
        activitySpecialRightCommodityMapper.updateByPrimaryKey(activitySpecialRightCommodity);
    }

    public ActivitySpecialRightCommodity getSpecialRightCommodityById(int activity_special_right_commodity_id) {
        return activitySpecialRightCommodityMapper.selectByPrimaryKey(activity_special_right_commodity_id);
    }

    @Transactional
    public void createActivitySellLimit(List<ActivitySellLimit> activitySellLimits) {
        for (int i = 0; i < activitySellLimits.size(); i++) {
            ActivitySellLimit activitySellLimit = activitySellLimits.get(i);
            ActivitySellLimit activitySellLimit1 = activityExtendMapper.getActivitySellLimit(activitySellLimit.getActivity_id(), activitySellLimit.getCommodity_id());
            if (activitySellLimit1 == null) {
                activityExtendMapper.createActivitySellLimit(activitySellLimit);
            }else {
                activitySellLimit.setActivity_sell_limit_id(activitySellLimit1.getActivity_sell_limit_id());
                activitySellLimitMapper.updateByPrimaryKey(activitySellLimit);
            }
        }
    }
    @Transactional
    public void deleteSellLimit(int activity_sell_limit_id) {
        activitySellLimitMapper.deleteByPrimaryKey(activity_sell_limit_id);
    }

    public List<ActivityCommodityExtend> activityCommodityReports(ActivitySearchOption activitySearchOption) {
        return activityExtendMapper.activityCommodityReports(activitySearchOption);
    }
    public Integer countActivityCommodityReports(ActivitySearchOption activitySearchOption) {
        return activityExtendMapper.countActivityCommodityReports(activitySearchOption);
    }
}
