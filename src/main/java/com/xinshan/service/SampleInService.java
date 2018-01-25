package com.xinshan.service;

import com.xinshan.dao.CommoditySampleInDetailMapper;
import com.xinshan.dao.CommoditySampleInMapper;
import com.xinshan.dao.extend.commodity.SampleInExtendMapper;
import com.xinshan.model.CommoditySampleIn;
import com.xinshan.model.CommoditySampleInDetail;
import com.xinshan.model.Employee;
import com.xinshan.model.extend.commodity.SampleInDetailExtend;
import com.xinshan.model.extend.commodity.SampleInExtend;
import com.xinshan.pojo.commodity.SampleInSearchOption;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.constant.ConstantUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mxt on 17-5-15.
 */
@Service
public class SampleInService {
    @Autowired
    private InventoryHistoryService inventoryHistoryService;
    @Autowired
    private InventoryInService inventoryInService;
    @Autowired
    private SampleInExtendMapper sampleInExtendMapper;
    @Autowired
    private CommoditySampleInMapper commoditySampleInMapper;
    @Autowired
    private CommoditySampleInDetailMapper commoditySampleInDetailMapper;

    /**
     * 添加上样单
     * @param sampleInExtend
     * @param employee
     */
    @Transactional
    public void createSampleIn(SampleInExtend sampleInExtend, Employee employee) {
        sampleInExtend.setSample_in_create_date(DateUtil.currentDate());
        sampleInExtend.setSample_in_employee_code(employee.getEmployee_code());
        sampleInExtend.setSample_in_employee_name(employee.getEmployee_name());
        sampleInExtend.setSample_in_code(sampleInCode(null));
        sampleInExtend.setSample_in_confirm_status(0);
        sampleInExtendMapper.createSampleIn(sampleInExtend);
        createSampleInDetails(sampleInExtend.getSampleInDetailExtends(), sampleInExtend.getCommodity_sample_in_id());
    }

    private void createSampleInDetails(List<SampleInDetailExtend> sampleInDetailExtends, int commodity_sample_in_id) {
        for (int i = 0; i < sampleInDetailExtends.size(); i++) {
            SampleInDetailExtend sampleInDetailExtend = sampleInDetailExtends.get(i);
            sampleInDetailExtend.setCommodity_sample_in_id(commodity_sample_in_id);
            sampleInDetailExtend.setSample_in_detail_confirm_status(0);
            sampleInExtendMapper.createSampleInDetail(sampleInDetailExtend);
        }
    }

    private String sampleInCode(String sampleOutCode) {
        String n = null;
        if (sampleOutCode == null) {
            String s = ConstantUtils.SAMPLE_IN_HEADER + DateUtil.format(DateUtil.currentDate(), "yyMMdd") + "[0-9]{4}";
            n = sampleInExtendMapper.sampleInCode(s);//
        }else {
            n = sampleOutCode;
        }
        if (n == null) {
            return ConstantUtils.SAMPLE_IN_HEADER + Long.parseLong(DateUtil.format(DateUtil.currentDate(), "yyMMdd"))*1000+1l;
        }
        n = n.substring(ConstantUtils.SAMPLE_IN_HEADER.length(), n.length());
        int m = Integer.parseInt(n);
        m++;
        return ConstantUtils.SAMPLE_IN_HEADER + m;
    }

    /**
     * 编辑修改上样单
     * @param sampleInExtend
     * @param employee
     */
    @Transactional
    public void updateSampleIn(SampleInExtend sampleInExtend, Employee employee) {
        CommoditySampleIn commoditySampleIn = commoditySampleInMapper.selectByPrimaryKey(sampleInExtend.getCommodity_sample_in_id());
        sampleInExtend.setSample_in_create_date(DateUtil.currentDate());
        sampleInExtend.setSample_in_code(commoditySampleIn.getSample_in_code());
        sampleInExtend.setSample_in_employee_code(employee.getEmployee_code());
        sampleInExtend.setSample_in_employee_name(employee.getEmployee_name());
        sampleInExtend.setSample_in_confirm_status(0);
        commoditySampleInMapper.updateByPrimaryKey(sampleInExtend);
        List<SampleInDetailExtend> old = sampleInDetailExtends(sampleInExtend.getCommodity_sample_in_id());
        for (int i = 0; i < old.size(); i++) {
            SampleInDetailExtend sampleInDetailExtend = old.get(i);
            commoditySampleInDetailMapper.deleteByPrimaryKey(sampleInDetailExtend.getCommodity_sample_in_detail_id());
        }
        createSampleInDetails(sampleInExtend.getSampleInDetailExtends(), sampleInExtend.getCommodity_sample_in_id());
    }

    public void updateSampleIn(CommoditySampleIn commoditySampleIn) {
        commoditySampleInMapper.updateByPrimaryKey(commoditySampleIn);
    }
    public void updateSampleInDetail(CommoditySampleInDetail commoditySampleInDetail) {
        commoditySampleInDetailMapper.updateByPrimaryKey(commoditySampleInDetail);
    }

    /**
     * 上样单列表
     * @param sampleInSearchOption
     * @return
     */
    public List<SampleInExtend> sampleInExtends(SampleInSearchOption sampleInSearchOption) {
        Integer commodity_sample_in_id = sampleInSearchOption.getCommodity_sample_in_id();
        if (commodity_sample_in_id == null) {
            List<Integer> sampleInIds = sampleInExtendMapper.sampleInIds(sampleInSearchOption);
            if (sampleInIds != null && sampleInIds.size() > 0){
                sampleInSearchOption.setSampleInIds(sampleInIds);
                List<SampleInExtend> sampleInExtends = sampleInExtendMapper.sampleInList(sampleInSearchOption);
                for (int i = 0; i < sampleInExtends.size(); i++) {
                    SampleInExtend sampleInExtend = sampleInExtends.get(i);
                    List<SampleInDetailExtend> sampleInDetailExtends = sampleInDetailExtends(sampleInExtend.getCommodity_sample_in_id());
                    sampleInExtend.setSampleInDetailExtends(sampleInDetailExtends);
                }
                return sampleInExtends;
            }
            return new ArrayList<>();
        }else {
            SampleInExtend sampleInExtend = sampleInExtend(commodity_sample_in_id);
            if (sampleInExtend == null) {
                return null;
            }
            List<SampleInExtend> list = new ArrayList<>();
            list.add(sampleInExtend);
            return list;
        }
    }

    public SampleInExtend sampleInExtend(int sample_in_id) {
        SampleInSearchOption sampleInSearchOption = new SampleInSearchOption();
        List<Integer> list = new ArrayList<>();
        list.add(sample_in_id);
        sampleInSearchOption.setSampleInIds(list);
        List<SampleInExtend> sampleInExtends = sampleInExtendMapper.sampleInList(sampleInSearchOption);
        if (sampleInExtends != null && sampleInExtends.size() == 1) {
            SampleInExtend sampleInExtend = sampleInExtends.get(0);
            sampleInExtend.setSampleInDetailExtends(sampleInDetailExtends(sample_in_id));
            return sampleInExtend;
        }
        return null;
    }

    public List<SampleInDetailExtend> sampleInDetailExtends(int commodity_sample_in_id) {
        SampleInSearchOption sampleInSearchOption1 = new SampleInSearchOption();
        sampleInSearchOption1.setCommodity_sample_in_id(commodity_sample_in_id);
        List<SampleInDetailExtend> sampleInDetailExtends = sampleInExtendMapper.sampleInDetails(sampleInSearchOption1);
        return sampleInDetailExtends;
    }

    public Integer countSampleIn(SampleInSearchOption sampleInSearchOption) {
        return sampleInExtendMapper.countSampleIn(sampleInSearchOption);
    }

    /**
     *
     * 确认上样
     * @param sampleInExtend
     */
    public void sampleInConfirm(SampleInExtend sampleInExtend, Employee employee) {
        if (sampleInExtend.getInventory_in_id() == null) {
            inventoryInService.createInventoryIn(sampleInExtend, employee);
        }
    }

    public void sampleInInventoryIn(SampleInExtend sampleInExtend, Employee employee) {
        if (sampleInExtend.getSample_in_confirm_status() == 1) {
            return;
        }
        inventoryHistoryService.sampleInInventoryHistory(sampleInExtend, employee);
    }

}
