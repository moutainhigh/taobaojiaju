package com.xinshan.utils.constant.checking;

/**
 * Created by mxt on 17-2-22.
 * 对账单类型常量
 *
 */
public class ConstantCheckingType {
    //商品采购费用对账单
    public static final int CHECKING_TYPE_COMMODITY = 1;
    //售后费用对账单
    public static final int CHECKING_TYPE_AFTER_SALES = 2;
    //样品维修
    public static final int CHECKING_TYPE_SAMPLE_FIX = 3;
    //售后退货对账单
    public static final int CHECKING_TYPE_RETURN_COMMODITY = 4;
    public static final int CHECKING_TYPE_GIFT = 5;//赠品
    public static final int CHECKING_TYPE_GIFT_RETURN = 6;//赠品退还
    public static final int CHECKING_TYPE_INVENTORY_IN = 7;//广东馆入库
    public static final int CHECKING_TYPE_GIFT_OUT = 8;//赠品出库
    public static final int CHECKING_TYPE_PRE_SALE_ORDER_RETURN = 10;//售前退货
    public static final int CHECKING_TYPE_PURCHASE_ARRIVAL = 11;//广东馆到货结算

    public static final int CHECKING_DETAIL_TYPE_COMMODITY = 1;//商品
    public static final int CHECKING_DETAIL_TYPE_FEE = 2;//费用
}
