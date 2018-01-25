package com.xinshan.controller.orderFee;

import com.xinshan.components.order.OrderComponents;
import com.xinshan.components.orderFee.OrderFeeComponents;
import com.xinshan.model.Employee;
import com.xinshan.model.OrderFee;
import com.xinshan.model.extend.afterSales.SampleFixExtend;
import com.xinshan.model.extend.orderFee.OrderFeeExtend;
import com.xinshan.service.*;
import com.xinshan.utils.RequestUtils;
import com.xinshan.utils.ResponseUtil;
import com.xinshan.utils.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mxt on 17-5-17.
 */
@Controller
public class OrderFeeController {
    @Autowired
    private OrderFeeService orderFeeService;
    @Autowired
    private InventoryHistoryService inventoryHistoryService;
    @Autowired
    private OrderReturnService orderReturnService;
    @Autowired
    private SampleFixService sampleFixService;
    @Autowired
    private AfterSalesService afterSalesService;

    /**
     * 费用删除
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/orderFee/orderFeeEnable")
    public void orderFeeEnable(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int order_fee_id = Integer.parseInt(request.getParameter("order_fee_id"));
        int enable = Integer.parseInt(request.getParameter("order_fee_enable"));
        OrderFee orderFee = orderFeeService.getOrderFeeById(order_fee_id);
        orderFee.setOrder_fee_enable(enable);
        orderFeeService.updateOrderFeeWithTran(orderFee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 编辑费用
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/orderFee/updateOrderFee")
    public void updateOrderFee(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int order_fee_id = Integer.parseInt(request.getParameter("order_fee_id"));
        BigDecimal supplier_fee = new BigDecimal(request.getParameter("supplier_fee"));
        BigDecimal fhc_fee = new BigDecimal(request.getParameter("fhc_fee"));
        BigDecimal customer_fee = new BigDecimal(request.getParameter("customer_fee"));
        int worker_id = Integer.parseInt(request.getParameter("worker_id"));
        String order_fee_remark = request.getParameter("order_fee_remark");
        OrderFee orderFee = orderFeeService.getOrderFeeById(order_fee_id);
        if (orderFee.getOrder_fee_check_status() == 1) {
            //已审核，不能编辑
            ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "已审核，不能编辑"));
            return;
        }
        orderFee.setSupplier_fee(supplier_fee);
        orderFee.setFhc_fee(fhc_fee);
        orderFee.setCustomer_fee(customer_fee);
        orderFee.setWorker_id(worker_id);
        orderFee.setOrder_fee_remark(order_fee_remark);
        orderFeeService.updateOrderFeeWithTran(orderFee);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     * 费用审核
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/order/orderFee/checkOrderFee")
    public void orderFeeCheck(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        int obj_id = 0;
        int obj_id_type = -1;
        if (request.getParameter("inventory_history_id") != null) {
            obj_id = Integer.parseInt(request.getParameter("inventory_history_id"));
            obj_id_type = 0;
        }else if (request.getParameter("order_return_id") != null) {
            obj_id = Integer.parseInt(request.getParameter("order_return_id"));
            obj_id_type = 1;
        }else if (request.getParameter("sample_fix_id") != null) {
            obj_id = Integer.parseInt(request.getParameter("sample_fix_id"));
            obj_id_type = 2;
        }else if (request.getParameter("after_sales_id") != null) {
            obj_id = Integer.parseInt(request.getParameter("after_sales_id"));
            obj_id_type = 3;
        }

        switch (obj_id_type) {
            case 0://出库费用审核
                break;
            case 1://退换货费用审核
                orderReturnService.orderReturnFeeCheck(obj_id, employee);
                break;
            case 2://场地维修费用审核
                sampleFixService.sampleFixFeeCheck(obj_id, employee);
                break;
            case 3://售后维修费用审核
                afterSalesService.afterSalesFeeCheck(obj_id, employee);
                break;
            default:
                break;
        }
        ResponseUtil.sendSuccessResponse(request, response);
    }
}
