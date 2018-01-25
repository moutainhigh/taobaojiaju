package com.xinshan.dao.extend.salesTarget;

import com.xinshan.model.SalesTarget;
import com.xinshan.model.SalesTargetAmount;
import com.xinshan.model.SalesTargetAnalysis;
import com.xinshan.model.extend.salesTarget.SalesTargetAnalysisExtend;
import com.xinshan.model.extend.salesTarget.SalesTargetExtend;
import com.xinshan.pojo.salesTarget.SalesTargetSearchOption;

import java.util.List;

/**
 * Created by mxt on 17-8-22.
 */
public interface SalesTargetExtendMapper {

    void createSalesTargetAnalysis(SalesTargetAnalysis salesTargetAnalysis);

    void createSalesTarget(SalesTarget salesTarget);

    void createSalesTargetAmount(SalesTargetAmount salesTargetAmount);

    List<Integer> salesTargetIds(SalesTargetSearchOption salesTargetSearchOption);

    Integer countSalesTarget(SalesTargetSearchOption salesTargetSearchOption);

    List<SalesTargetExtend> salesTargetList(SalesTargetSearchOption salesTargetSearchOption);

    List<SalesTargetAnalysisExtend> targetAnalysisList(SalesTargetSearchOption salesTargetSearchOption);

    Integer countAnalysis(SalesTargetSearchOption salesTargetSearchOption);
}
