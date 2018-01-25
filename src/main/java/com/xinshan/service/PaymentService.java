package com.xinshan.service;

import com.xinshan.dao.extend.payment.PaymentExtendMapper;
import com.xinshan.model.Checking;
import com.xinshan.model.Employee;
import com.xinshan.model.Payment;
import com.xinshan.model.extend.payment.PaymentExtend;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.constant.ConstantUtils;
import com.xinshan.pojo.payment.PaymentSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by mxt on 17-3-8.
 */
@Service
public class PaymentService {
    @Autowired
    private CheckingService checkingService;
    @Autowired
    private PaymentExtendMapper paymentExtendMapper;

    @Transactional
    public synchronized void createPayment(Payment payment, Employee employee) {
        payment.setCreate_employee_name(employee.getEmployee_name());
        payment.setCreate_employee_code(employee.getEmployee_code());
        payment.setPayment_create_date(DateUtil.currentDate());
        if (payment.getPayment_date() == null) {
            payment.setPayment_date(DateUtil.currentDate());
        }
        payment.setPayment_code(paymentCode(null));
        paymentExtendMapper.createPayment(payment);
        int checking_id = payment.getChecking_id();
        Checking checking = checkingService.getCheckingById(checking_id);
        if (checking.getPayment_status() != 1) {
            checking.setPayment_status(1);
            checking.setPay_date(DateUtil.currentDate());
            checking.setPay_employee_code(employee.getEmployee_code());
            checking.setPay_employee_name(employee.getEmployee_name());
            checkingService.updateChecking(checking);
        }
    }

    public List<PaymentExtend> paymentList(PaymentSearchOption paymentSearchOption) {
        return paymentExtendMapper.paymentList(paymentSearchOption);
    }

    public Integer countPayment(PaymentSearchOption paymentSearchOption) {
        return paymentExtendMapper.countPayment(paymentSearchOption);
    }
    /**
     *
     * @return
     */
    private String paymentCode(String payment_code) {
        String n = null;
        if (payment_code == null) {
            String s = ConstantUtils.PAYMENT_CODE_HEADER + DateUtil.format(DateUtil.currentDate(), "yyMMdd") + "[0-9]{3}";
            n = paymentExtendMapper.maxPaymentCode(s);//DZD161031001
        }else {
            n = payment_code;
        }
        if (n == null) {
            return ConstantUtils.PAYMENT_CODE_HEADER + Long.parseLong(DateUtil.format(DateUtil.currentDate(), "yyMMdd"))*100+1l;
        }
        n = n.substring(3, n.length());
        int m = Integer.parseInt(n);
        m++;
        return ConstantUtils.PAYMENT_CODE_HEADER + m;
    }

}
