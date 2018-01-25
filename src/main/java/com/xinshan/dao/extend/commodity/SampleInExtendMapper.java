package com.xinshan.dao.extend.commodity;

import com.xinshan.model.CommoditySampleIn;
import com.xinshan.model.CommoditySampleInDetail;
import com.xinshan.model.extend.commodity.SampleInDetailExtend;
import com.xinshan.model.extend.commodity.SampleInExtend;
import com.xinshan.pojo.commodity.SampleInSearchOption;

import java.util.List;

/**
 * Created by mxt on 17-5-15.
 */
public interface SampleInExtendMapper {

    void createSampleIn(CommoditySampleIn commoditySampleIn);

    void createSampleInDetail(CommoditySampleInDetail commoditySampleInDetail);

    String sampleInCode(String s);

    List<SampleInExtend> sampleInList(SampleInSearchOption sampleInSearchOption);

    Integer countSampleIn(SampleInSearchOption sampleInSearchOption);

    List<Integer> sampleInIds(SampleInSearchOption sampleInSearchOption);

    List<SampleInDetailExtend> sampleInDetails(SampleInSearchOption sampleInSearchOption);

}
