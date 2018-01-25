package com.xinshan.dao.extend.sys;

import com.xinshan.model.RequestHistory;
import com.xinshan.pojo.requestHistory.RequestHistorySearchOption;

import java.util.List;

/**
 * Created by mxt on 16-11-24.
 */
public interface SystemExtendMapper {

    List<RequestHistory> requestHistories(RequestHistorySearchOption requestHistorySearchOption);

    Integer count(RequestHistorySearchOption requestHistorySearchOption);
}
