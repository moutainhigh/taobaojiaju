package com.xinshan.utils.constant.order;

/**
 * Created by mxt on 17-4-1.
 */
public class OrderConstants {

    //送货费用添加状态
    public static final int inventory_out_fee_check_status_not_create = -1;//费用未添加
    public static final int inventory_out_fee_check_status_not_check = 0;//费用已添加未审核
    public static final int inventory_out_fee_check_status_check = 1;//费用已审核


    //订单费用审核状态
    public static final int order_fee_no_check_status = 0;//费用未审核
    public static final int order_fee_check_status = 1;//费用已审核

    //订单商品状态
//    状态0添加订单，100生成采购单，200确认采购，300采购审核,350部分到货,400全部到货（等待出库）,
    //            450不采购（等待出库）,500部分出库,550完全出库，551供应商发货, 600部分到货,650全部到货,
    public static final int ORDER_COMMODITY_STATUS = 0;//初始状态，等待生成采购单
    public static final int ORDER_COMMODITY_STATUS_PURCHASE = 100;//已生成采购单，等待确认采购
    public static final int ORDER_COMMODITY_STATUS_PURCHASE_CONFIRM = 200;//采购已确认
    public static final int ORDER_COMMODITY_STATUS_PURCHASE_CHECK = 300;//采购已审核
    public static final int ORDER_COMMODITY_STATUS_PART_ARRIVAL = 350;//部分到货
    public static final int ORDER_COMMODITY_STATUS_PART_IN = 360;//部分入库
    public static final int ORDER_COMMODITY_STATUS_ALL_ARRIVAL = 400;//全部到货
    public static final int ORDER_COMMODITY_STATUS_ALL_IN = 410;//全部入库
    public static final int ORDER_COMMODITY_STATUS_NO_PURCHASE = 450;//不采购
    public static final int ORDER_COMMODITY_STATUS_PART_OUT = 500;//部分出库
    public static final int ORDER_COMMODITY_STATUS_COMPLETE_OUT = 550;//全部出库
    public static final int ORDER_COMMODITY_STATUS_SUPPLIER_DELIVERY = 551;//供应商发货
    public static final int ORDER_COMMODITY_STATUS_PART_DONE = 600;//600部分到货
    public static final int ORDER_COMMODITY_STATUS_ALL_DONE = 650;//650全部到货

    //付款类型
    public static final int order_pay_type_add_order = 1;//1下单付款
    public static final int order_pay_type_return_add_order = 2;//退货新增商品付款
    public static final int order_pay_type_return_order = 3;//退货退款（金额为负）
    public static final int order_pay_type_after_sales_return = 4;//售后退货退款

    //退货类型
    public static final int order_return_type_return = 0;//订单退货
    public static final int order_return_type_after_sales = 1;//售后退货

    public static final int ORDER_STEP_CREATE_ORDER = 0;//新添加订单
    public static final int ORDER_STEP_CONFIRM_ORDER = 1000;//订单已确认
    public static final int ORDER_STEP_CONFIRM_PURCHASE = 2000;//订单已确认采购
    public static final int ORDER_STEP_PART_ARRIVAL = 3000;//采购部分到货
    public static final int ORDER_STEP_ALL_ARRIVAL = 3999;//采购全部到货
    public static final int ORDER_STEP_PART_INVENTORY_OUT = 4000;//订单部分出库
    public static final int ORDER_STEP_ALL_INVENTORY_OUT = 4999;//订单全部出库
    public static final int ORDER_STEP_PART_DONE = 5000;//部分送达
    public static final int ORDER_STEP_DONE = 5999;//全部送达

    //订单商品类型
    public static final int ORDER_COMMODITY_TYPE_ORDER = 1;//订单
    public static final int ORDER_COMMODITY_TYPE_RETURN = 2;//退还新增
//    退换货类型，1退货，2新添,3售后退货
    public static final int ORDER_RETURN_COMMODITY_TYPE_RETURN = 1;//1退货
    public static final int ORDER_RETURN_COMMODITY_TYPE_ADD = 2;//换货，新增商品

}
