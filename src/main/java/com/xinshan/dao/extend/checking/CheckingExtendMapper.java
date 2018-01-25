package com.xinshan.dao.extend.checking;

import com.xinshan.model.Checking;
import com.xinshan.model.CheckingDetail;
import com.xinshan.model.extend.checking.CheckingDetailExtend;
import com.xinshan.model.extend.checking.CheckingExtend;
import com.xinshan.pojo.checking.CheckingSearchOptions;

import java.util.List;

/**
 * Created by mxt on 17-2-20.
 */
public interface CheckingExtendMapper {

    void createChecking(Checking checking);

    void createCheckingDetail(CheckingDetail checkingDetail);

    String todayCheckingNum(String dateStr);

    List<CheckingDetailExtend> checkingDetailList(CheckingSearchOptions checkingSearchOptions);

    Integer countCheckingDetails(CheckingSearchOptions checkingSearchOptions);

    Integer countChecking(CheckingSearchOptions checkingSearchOptions);

    List<Integer> checkingIds(CheckingSearchOptions checkingSearchOptions);

    CheckingExtend getCheckingById(int checking_id);

    List<CheckingExtend> checkingList(List<Integer> list);
}
