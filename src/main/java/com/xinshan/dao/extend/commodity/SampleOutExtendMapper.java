package com.xinshan.dao.extend.commodity;

import com.xinshan.model.CommoditySampleOut;
import com.xinshan.model.CommoditySampleOutDetail;
import com.xinshan.model.extend.commodity.SampleOutExtend;
import com.xinshan.pojo.commodity.SampleOutSearchOption;

import java.util.List;

/**
 * Created by mxt on 17-4-22.
 */
public interface SampleOutExtendMapper {
    void createSampleOut(CommoditySampleOut sampleOut);

    void createSampleOutDetail(CommoditySampleOutDetail commoditySampleOutDetail);

    String sampleOutCode(String s);

    List<Integer> sampleOutIds(SampleOutSearchOption sampleOutSearchOption);

    SampleOutExtend sampleOut(int commodity_sample_out_id);

    Integer countSampleOut(SampleOutSearchOption sampleOutSearchOption);

    void deleteSampleOutDetail(int commodity_sample_out_id);
}
