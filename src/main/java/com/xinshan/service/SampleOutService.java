package com.xinshan.service;

import com.xinshan.dao.CommoditySampleOutDetailMapper;
import com.xinshan.dao.CommoditySampleOutMapper;
import com.xinshan.dao.extend.commodity.SampleOutExtendMapper;
import com.xinshan.model.Commodity;
import com.xinshan.model.CommodityNum;
import com.xinshan.model.CommoditySampleOutDetail;
import com.xinshan.model.Employee;
import com.xinshan.model.extend.commodity.SampleOutDetailExtend;
import com.xinshan.model.extend.commodity.SampleOutExtend;
import com.xinshan.pojo.commodity.SampleOutSearchOption;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.constant.ConstantUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mxt on 17-4-22.
 */
@Service
public class SampleOutService {
    @Autowired
    private SampleOutExtendMapper sampleOutExtendMapper;
    @Autowired
    private CommodityService commodityService;
    @Autowired
    private InventoryOutService inventoryOutService;
    @Autowired
    private CommoditySampleOutMapper commoditySampleOutMapper;
    @Autowired
    private CommoditySampleOutDetailMapper commoditySampleOutDetailMapper;
    @Autowired
    private InventoryService inventoryService;

    @Transactional
    public synchronized void createSampleOut(SampleOutExtend sampleOut, Employee employee) {
        sampleOut.setSample_out_create_date(DateUtil.currentDate());
        sampleOut.setSample_out_create_employee_name(employee.getEmployee_name());
        sampleOut.setSample_out_create_employee_code(employee.getEmployee_code());
        sampleOut.setSample_out_code(sampleOutCode(null));
        sampleOut.setSample_out_check_status(0);
        sampleOut.setCommodity_sample_out_status(0);
        sampleOutExtendMapper.createSampleOut(sampleOut);
        List<SampleOutDetailExtend> list = sampleOut.getSampleOutDetails();
        createSampleOutDetail(list, sampleOut.getCommodity_sample_out_id());
    }

    private void createSampleOutDetail(List<SampleOutDetailExtend> list, int commodity_sample_out_id) {
        for (int i = 0; i < list.size(); i++) {
            SampleOutDetailExtend sampleOutDetail = list.get(i);
            sampleOutDetail.setCommodity_sample_out_id(commodity_sample_out_id);
            sampleOutDetail.setInventory_out_status(0);
            sampleOutExtendMapper.createSampleOutDetail(sampleOutDetail);
        }
    }
    @Transactional
    public void updateSampleOut(SampleOutExtend sampleOutExtendNew) {
        int sample_out_id = sampleOutExtendNew.getCommodity_sample_out_id();
        SampleOutExtend sampleOutExtendOld = sampleOutExtendMapper.sampleOut(sample_out_id);
        sampleOutExtendOld.setSupplier_id(sampleOutExtendNew.getSupplier_id());
        sampleOutExtendOld.setSample_out_remark(sampleOutExtendNew.getSample_out_remark());
        commoditySampleOutMapper.updateByPrimaryKey(sampleOutExtendOld);

        sampleOutExtendMapper.deleteSampleOutDetail(sampleOutExtendOld.getCommodity_sample_out_id());

        List<SampleOutDetailExtend> list = sampleOutExtendNew.getSampleOutDetails();
        createSampleOutDetail(list, sampleOutExtendOld.getCommodity_sample_out_id());
    }

    private String sampleOutCode(String sampleOutCode) {
        String n = null;
        if (sampleOutCode == null) {
            String s = ConstantUtils.SAMPLE_OUT_HEADER + DateUtil.format(DateUtil.currentDate(), "yyMMdd") + "[0-9]{4}";
            n = sampleOutExtendMapper.sampleOutCode(s);//
        }else {
            n = sampleOutCode;
        }
        if (n == null) {
            return ConstantUtils.SAMPLE_OUT_HEADER + Long.parseLong(DateUtil.format(DateUtil.currentDate(), "yyMMdd"))*1000+1l;
        }
        n = n.substring(ConstantUtils.SAMPLE_OUT_HEADER.length(), n.length());
        int m = Integer.parseInt(n);
        m++;
        return ConstantUtils.SAMPLE_OUT_HEADER + m;
    }

    public List<SampleOutExtend> sampleOutList(SampleOutSearchOption sampleOutSearchOption) {
        List<Integer> sampleOutIds = sampleOutExtendMapper.sampleOutIds(sampleOutSearchOption);
        if (sampleOutIds== null || sampleOutIds.size() == 0) {
            return new ArrayList<>();
        }
        List<SampleOutExtend> sampleOuts = new ArrayList<>();
        for (int i = 0; i < sampleOutIds.size(); i++) {
            SampleOutExtend sampleOut = sampleOutExtendMapper.sampleOut(sampleOutIds.get(i));
            List<SampleOutDetailExtend> sampleOutDetails = sampleOut.getSampleOutDetails();
            for (int j = 0; j < sampleOutDetails.size(); j++) {
                SampleOutDetailExtend sampleOutDetail = sampleOutDetails.get(j);
                sampleOutDetail.setCommodity(commodityService.getCommodityById(sampleOutDetail.getCommodity_id()));
                CommodityNum commodityNum = inventoryService.getCommodityNumById(sampleOutDetail.getCommodity_num_id());
                sampleOutDetail.setCommodityNum(commodityNum);
            }
            sampleOuts.add(sampleOut);
        }
        return sampleOuts;
    }

    public Integer countSampleOut(SampleOutSearchOption sampleOutSearchOption) {
        return sampleOutExtendMapper.countSampleOut(sampleOutSearchOption);
    }

    @Transactional
    public void confirmSampleOut(int sampleOutId, Employee employee) {
        SampleOutExtend sampleOut = sampleOutExtendMapper.sampleOut(sampleOutId);
        inventoryOutService.createInventoryOut(sampleOut, employee);
        List<SampleOutDetailExtend> list = sampleOut.getSampleOutDetails();
        for (int i = 0; i < list.size(); i++) {
            SampleOutDetailExtend sampleOutDetail = list.get(i);
            commoditySampleOutDetailMapper.updateByPrimaryKey(sampleOutDetail);
        }
        sampleOut.setSample_out_check_status(1);
        commoditySampleOutMapper.updateByPrimaryKey(sampleOut);
    }

    public CommoditySampleOutDetail getCommoditySampleOutDetailById(int commodity_sample_out_detail_id) {
        return commoditySampleOutDetailMapper.selectByPrimaryKey(commodity_sample_out_detail_id);
    }

    public void sampleOutStatus(CommoditySampleOutDetail sampleOutDetail) {
        sampleOutDetail.setInventory_out_status(2);
        int sample_out_id = sampleOutDetail.getCommodity_sample_out_id();
        SampleOutExtend sampleOutExtend = sampleOutExtendMapper.sampleOut(sample_out_id);
        List<SampleOutDetailExtend> list = sampleOutExtend.getSampleOutDetails();
        int out = 1;
        for (int i = 0; i < list.size(); i++) {
            SampleOutDetailExtend sampleOutDetailExtend = list.get(i);
            if (sampleOutDetailExtend.getCommodity_sample_out_detail_id() == sampleOutDetail.getCommodity_sample_out_detail_id()) {
                continue;
            }
            if (sampleOutDetailExtend.getInventory_out_status() == 2) {
                out ++;
            }
        }
        if (out == list.size()) {
            sampleOutExtend.setCommodity_sample_out_status(2);
        }else {
            sampleOutExtend.setCommodity_sample_out_status(1);
        }
        commoditySampleOutDetailMapper.updateByPrimaryKey(sampleOutDetail);
        commoditySampleOutMapper.updateByPrimaryKey(sampleOutExtend);
    }

    @Transactional
    public void sampleOutInventoryOut(int sample_out_id, Employee employee) {
        SampleOutExtend sampleOut = sampleOutExtendMapper.sampleOut(sample_out_id);
        List<SampleOutDetailExtend> sampleOutDetails = sampleOut.getSampleOutDetails();
        for (int j = 0; j < sampleOutDetails.size(); j++) {
            SampleOutDetailExtend sampleOutDetail = sampleOutDetails.get(j);
            if (sampleOutDetail.getInventory_out_status() == 2) {
                continue;
            }
            inventoryOutService.sampleOutInventoryOut(sampleOutDetail, employee);
        }
        sampleOut.setCommodity_sample_out_status(2);
        commoditySampleOutMapper.updateByPrimaryKey(sampleOut);
    }
}
