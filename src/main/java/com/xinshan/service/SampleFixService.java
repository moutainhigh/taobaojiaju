package com.xinshan.service;

import com.xinshan.components.orderFee.OrderFeeComponents;
import com.xinshan.dao.CommoditySampleFixDetailMapper;
import com.xinshan.dao.CommoditySampleFixMapper;
import com.xinshan.dao.extend.afterSales.SampleFixExtendMapper;
import com.xinshan.model.*;
import com.xinshan.model.extend.afterSales.SampleFixDetailExtend;
import com.xinshan.model.extend.afterSales.SampleFixExtend;
import com.xinshan.model.extend.orderFee.OrderFeeExtend;
import com.xinshan.pojo.afterSales.SampleFixSearchOption;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.constant.ConstantUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by mxt on 17-4-21.
 */
@Service
public class SampleFixService {
    @Autowired
    private SampleFixExtendMapper sampleFixExtendMapper;
    @Autowired
    private CommoditySampleFixMapper commoditySampleFixMapper;

    @Transactional
    public void createSampleFix(SampleFixExtend sampleFix, Employee employee) {
        sampleFix.setSample_fix_code(sampleFixCode(null));
        sampleFix.setFix_create_date(DateUtil.currentDate());
        sampleFix.setFix_create_employee_code(employee.getEmployee_code());
        sampleFix.setFix_create_employee_name(employee.getEmployee_name());
        sampleFix.setOrder_fee_check_status(0);
        sampleFix.setSample_fix_settlement_status(0);
        sampleFixExtendMapper.createSampleFix(sampleFix);

        List<SampleFixDetailExtend> sampleFixDetails = sampleFix.getSampleFixDetails();
        for (int i = 0; i < sampleFixDetails.size(); i++) {
            SampleFixDetailExtend sampleFixDetailExtend = sampleFixDetails.get(i);
            sampleFixDetailExtend.setSample_fix_id(sampleFix.getSample_fix_id());
            sampleFixDetailExtend.setFix_detail_status(0);
            sampleFixExtendMapper.createSampleFixDetail(sampleFixDetailExtend);
        }

        List<OrderFee> list = OrderFeeComponents.createOrderFee(sampleFix.getOrderFees(), sampleFix.getSupplier_id(), employee);
        sampleFix.setOrder_fee_ids(OrderFeeComponents.orderFeeIds(list));
        commoditySampleFixMapper.updateByPrimaryKey(sampleFix);
    }

    public List<SampleFixExtend> sampleFixList(SampleFixSearchOption sampleFixSearchOption) {
        List<SampleFixExtend> list = sampleFixExtendMapper.sampleFixList(sampleFixSearchOption);
        for (int i = 0; i < list.size(); i++) {
            SampleFixExtend sampleFixExtend = list.get(i);

            sampleFixExtend.setOrderFees(OrderFeeComponents.orderFeeList(sampleFixExtend.getOrder_fee_ids()));

            sampleFixExtend.setSampleFixDetails(sampleFixExtendMapper.sampleFixDetails(sampleFixExtend.getSample_fix_id()));
        }
        return list;
    }

    public SampleFixExtend getSampleFixById(int sample_fix_id) {
        SampleFixSearchOption sampleFixSearchOption = new SampleFixSearchOption();
        sampleFixSearchOption.setSample_fix_id(sample_fix_id);
        List<SampleFixExtend> list = sampleFixList(sampleFixSearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public Integer countSampleFix(SampleFixSearchOption sampleFixSearchOption) {
        return sampleFixExtendMapper.countSampleFix(sampleFixSearchOption);
    }

    @Transactional
    public void sampleFixFeeCheck(int sample_fix_id, Employee employee) {
        SampleFixExtend sampleFixById = getSampleFixById(sample_fix_id);
        if (sampleFixById.getOrder_fee_check_status() == 1) {
            return;
        }
        List<OrderFeeExtend> orderFees = OrderFeeComponents.orderFeeList(sampleFixById.getOrder_fee_ids());
        OrderFeeComponents.checkOrderFee(orderFees, employee);
        sampleFixById.setOrder_fee_check_status(1);
        commoditySampleFixMapper.updateByPrimaryKey(sampleFixById);
    }

    public void updateSampleFix(CommoditySampleFix sampleFix) {
        commoditySampleFixMapper.updateByPrimaryKey(sampleFix);
    }


    private String sampleFixCode(String sampleFixCode) {
        String n = null;
        if (sampleFixCode == null) {
            String s = ConstantUtils.SAMPLE_FIX_CODE_HEADER + DateUtil.format(DateUtil.currentDate(), "yyMMdd") + "[0-9]{4}";
            n = sampleFixExtendMapper.sampleFixCode(s);//
        }else {
            n = sampleFixCode;
        }
        if (n == null) {
            return ConstantUtils.SAMPLE_FIX_CODE_HEADER + Long.parseLong(DateUtil.format(DateUtil.currentDate(), "yyMMdd"))*1000+1l;
        }
        n = n.substring(ConstantUtils.SAMPLE_FIX_CODE_HEADER.length(), n.length());
        int m = Integer.parseInt(n);
        m++;
        return ConstantUtils.SAMPLE_FIX_CODE_HEADER + m;
    }
}
