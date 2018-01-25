package com.xinshan.dao.extend.afterSales;

import com.xinshan.model.CommoditySampleFix;
import com.xinshan.model.CommoditySampleFixDetail;
import com.xinshan.model.extend.afterSales.SampleFixDetailExtend;
import com.xinshan.model.extend.afterSales.SampleFixExtend;
import com.xinshan.pojo.afterSales.SampleFixSearchOption;

import java.util.List;

/**
 * Created by mxt on 17-4-21.
 */
public interface SampleFixExtendMapper {

    List<SampleFixExtend> sampleFixList(SampleFixSearchOption sampleFixSearchOption);

    Integer countSampleFix(SampleFixSearchOption sampleFixSearchOption);

    void createSampleFix(CommoditySampleFix sampleFix);
    void createSampleFixDetail(CommoditySampleFixDetail sampleFixDetail);

    List<SampleFixDetailExtend> sampleFixDetails(int sample_fix_id);

    String sampleFixCode(String sampleFixCode);
}
