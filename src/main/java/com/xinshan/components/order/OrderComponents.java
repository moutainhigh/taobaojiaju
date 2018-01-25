package com.xinshan.components.order;

import com.xinshan.components.inventory.InventoryHistoryComponent;
import com.xinshan.components.inventory.InventoryOutComponent;
import com.xinshan.components.purchase.PurchaseComponent;
import com.xinshan.model.OrderCarryFee;
import com.xinshan.model.OrderCommodityValueAddedCard;
import com.xinshan.model.PurchaseCommodity;
import com.xinshan.model.UserShopping;
import com.xinshan.model.extend.order.OrderCommodityExtend;
import com.xinshan.model.extend.order.OrderExtend;
import com.xinshan.model.extend.user.UserShoppingExtend;
import com.xinshan.service.OrderService;
import com.xinshan.utils.constant.order.OrderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mxt on 17-2-28.
 */
@Component
public class OrderComponents {
    private static OrderService orderService;
    @Autowired
    public void setOrderService(OrderService orderService) {
        OrderComponents.orderService = orderService;
    }


    public static void orderStepInit() {
        List<Integer> list = orderService.orderIds(null);
        for (int i = 0; i < list.size(); i++) {
            orderStep(list.get(i));
        }
    }

    /**
     * 订单步骤计算
     * @param order_id
     */
    public static int orderStep(int order_id) {
        OrderExtend order = orderService.getOrderById(order_id);
        int orderStep = orderStep(order);
        order.setOrder_step(orderStep);
        orderService.updateOrder(order);
        return orderStep;
    }

    private static int orderStep(OrderExtend orderExtend) {
        int purchase = orderExtend.getTrans_purchase();
        if (purchase == 0) {
            return OrderConstants.ORDER_STEP_CREATE_ORDER;
        }
        int purchaseStatus = 0;
        if (!allSample(orderExtend)) {//不是全部样品
            purchaseStatus = PurchaseComponent.purchaseStatus(orderExtend.getOrder_id());
        }else {
            purchaseStatus = 3;
        }

        if (purchaseStatus == 0) {//        * 0未确认采购
            return OrderConstants.ORDER_STEP_CONFIRM_ORDER;
        }else if (purchaseStatus == 1) {//        * 1已确认采购
            return OrderConstants.ORDER_STEP_CONFIRM_PURCHASE;
        }else if (purchaseStatus == 2) {//        * 2部分到货
            Integer inventoryOutStatus = InventoryOutComponent.inventoryOutStatus(orderExtend.getOrder_id());
            if (inventoryOutStatus > 0) {
                return OrderConstants.ORDER_STEP_PART_INVENTORY_OUT;
            }else {
                return OrderConstants.ORDER_STEP_PART_ARRIVAL;
            }
        }else if (purchaseStatus == 3) {//
            Integer inventoryOutStatus = InventoryOutComponent.inventoryOutStatus(orderExtend.getOrder_id());
            if (inventoryOutStatus == 0) {//全部到货,未出库
                return OrderConstants.ORDER_STEP_ALL_ARRIVAL;
            }else if (inventoryOutStatus == 1) {//订单部分出库
                return OrderConstants.ORDER_STEP_PART_INVENTORY_OUT;
            }else if (inventoryOutStatus == 2) {//全部出库
                int outStatus = InventoryHistoryComponent.orderInventoryOutStatus(orderExtend.getOrder_id());
                if (outStatus == 0) {
                    return OrderConstants.ORDER_STEP_ALL_INVENTORY_OUT;
                }else if (outStatus == 1) {
                    return OrderConstants.ORDER_STEP_PART_DONE;
                }else if (outStatus == 2) {
                    return OrderConstants.ORDER_STEP_DONE;
                }
            }
        }
        return 0;
    }

    private static boolean allSample(OrderExtend orderExtend) {
        List<OrderCommodityExtend> orderCommodities = orderExtend.getOrderCommodities();
        for (int i = 0; i < orderCommodities.size(); i++) {
            OrderCommodityExtend orderCommodityExtend = orderCommodities.get(i);
            Integer sample = orderCommodityExtend.getSample();
            if (sample == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 已订单id查询订单
     * @param order_id
     * @return
     */
    public static OrderExtend getOrderById(int order_id) {
        return orderService.getOrderById(order_id);
    }

    /**
     *
     * @param order_id
     */
    public static void totalPrice(int order_id) {
        OrderExtend orderExtend = getOrderById(order_id);
        List<OrderCommodityExtend> orderCommodities = orderExtend.getOrderCommodities();
        BigDecimal totalPrice = new BigDecimal("0");
        int totalNum = 0;
        for (int i = 0; i < orderCommodities.size(); i++) {
            OrderCommodityExtend orderCommodity = orderCommodities.get(i);
            BigDecimal bargain_price = orderCommodity.getBargain_price();
            BigDecimal revision_fee = orderCommodity.getRevision_fee();
            if (revision_fee == null) {
                revision_fee = new BigDecimal("0");
            }
            //订单商品总金额 (成交价 + 改版费) * 数量 - 增值卡优惠
            BigDecimal commodity_total_price = bargain_price.add(revision_fee).multiply(new BigDecimal(orderCommodity.getCommodity_num()));
            totalNum += orderCommodity.getCommodity_num();

            OrderCommodityValueAddedCard orderCommodityValueAddedCard = orderCommodity.getOrderCommodityValueAddedCard();
            if (orderCommodityValueAddedCard != null && orderCommodityValueAddedCard.getOrder_commodity_value_added_card_id() != null) {
                commodity_total_price = commodity_total_price.subtract(orderCommodityValueAddedCard.getCard_real_amount());
            }
            orderCommodity.setCommodity_total_price(commodity_total_price);
            orderService.updateOrderCommodity(orderCommodity);
            totalPrice = totalPrice.add(commodity_total_price);
        }
        orderExtend.setOrder_total_price(totalPrice);
        orderExtend.setTotal_num(totalNum);

        //加入搬运费
        List<OrderCarryFee> orderCarryFees = orderExtend.getOrderCarryFees();
        BigDecimal orderCarryFee = orderCarryFee(orderCarryFees);
        totalPrice = totalPrice.add(orderCarryFee);

        //减去优惠费用
        if (orderExtend.getPreferential_amount() != null) {//商品总金额 - 优惠金额
            totalPrice = totalPrice.subtract(orderExtend.getPreferential_amount());
        }
        orderExtend.setTotal_price(totalPrice);
        orderService.updateOrder(orderExtend);
    }

    public static BigDecimal orderCarryFee(List<OrderCarryFee> orderCarryFees) {
        BigDecimal fee = new BigDecimal("0");
        if (orderCarryFees != null) {
            for (int i = 0; i < orderCarryFees.size(); i++) {
                OrderCarryFee orderCarryFee = orderCarryFees.get(i);
                BigDecimal order_carry_fee = orderCarryFee.getOrder_carry_fee();
                if (order_carry_fee == null) {
                    order_carry_fee = new BigDecimal("0");
                }
                fee = fee.add(order_carry_fee);
            }
        }
        return fee;
    }

    /**
     *
     * @param order_step
     * @return
     */
    public static String orderStepStr(int order_step) {
        String orderStep = "";
        switch (order_step) {
            case OrderConstants.ORDER_STEP_CREATE_ORDER:        orderStep = "订单未确认";      break;
            case OrderConstants.ORDER_STEP_CONFIRM_ORDER:       orderStep = "订单已确认";      break;
            case OrderConstants.ORDER_STEP_CONFIRM_PURCHASE:    orderStep = "订单已确认采购";   break;
            case OrderConstants.ORDER_STEP_PART_ARRIVAL:        orderStep = "采购部分到货";     break;
            case OrderConstants.ORDER_STEP_ALL_ARRIVAL:         orderStep = "采购全部到货";     break;
            case OrderConstants.ORDER_STEP_PART_INVENTORY_OUT:  orderStep = "订单部分出库";     break;
            case OrderConstants.ORDER_STEP_ALL_INVENTORY_OUT:   orderStep = "订单全部出库";     break;
            case OrderConstants.ORDER_STEP_PART_DONE:           orderStep = "部分送达";         break;
            case OrderConstants.ORDER_STEP_DONE:                orderStep = "全部送达";         break;
            default:
                break;
        }
        return orderStep;
    }

    public static void orderCommodityStatus(int order_commodity_id) {
        OrderCommodityExtend orderCommodity = orderService.getOrderCommodityExtend(order_commodity_id);
        OrderExtend order = getOrderById(orderCommodity.getOrder_id());
        //采购
        PurchaseCommodity purchaseCommodity = orderCommodity.getPurchaseCommodity();
        int orderCommodityStatus = 0;
        if (orderCommodity.getSample() == 1 && order.getTrans_purchase() == 1) {//样品
            orderCommodityStatus = OrderConstants.ORDER_COMMODITY_STATUS_ALL_ARRIVAL;
        }else {
            if (purchaseCommodity != null && purchaseCommodity.getPurchase_commodity_id() != null) {
                orderCommodityStatus = OrderConstants.ORDER_COMMODITY_STATUS_PURCHASE;
                int p = PurchaseComponent.orderCommodityStatus(purchaseCommodity);
                if (p > 0) {
                    orderCommodityStatus = p;
                }
            }
        }

        //出库
        int commodityOutStatus = InventoryOutComponent.orderCommodityOutStatus(order_commodity_id);
        if (commodityOutStatus > 0) {
            orderCommodityStatus = commodityOutStatus;
        }
        System.out.println(orderCommodityStatus + "\t" + orderCommodity.getOrder_commodity_status());
        orderCommodity.setOrder_commodity_status(orderCommodityStatus);
        orderService.updateOrderCommodityWithTran(orderCommodity);
    }

    public static void userShoppingCreateOrder(UserShoppingExtend userShoppingExtend) {
        orderService.createOrder(userShoppingExtend, null);
    }
}
