package com.xinshan.service;

import com.xinshan.dao.RequestHistoryMapper;
import com.xinshan.dao.extend.sys.SystemExtendMapper;
import com.xinshan.model.RequestHistory;
import com.xinshan.pojo.requestHistory.RequestHistorySearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by mxt on 16-11-24.
 */
@Service
public class SystemService {
    @Autowired
    private SystemExtendMapper systemExtendMapper;
    @Autowired
    private RequestHistoryMapper requestHistoryMapper;

    @Transactional
    public void createRequestHistory(RequestHistory requestHistory) {
        requestHistoryMapper.insert(requestHistory);
    }

    public List<RequestHistory> requestHistories(RequestHistorySearchOption requestHistorySearchOption) {
        return systemExtendMapper.requestHistories(requestHistorySearchOption);
    }

    public Integer count(RequestHistorySearchOption requestHistorySearchOption) {
        return systemExtendMapper.count(requestHistorySearchOption);
    }
}
