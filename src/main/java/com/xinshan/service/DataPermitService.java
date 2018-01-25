package com.xinshan.service;

import com.xinshan.dao.DataPermitMapper;
import com.xinshan.dao.extend.role.DataPermitExtendMapper;
import com.xinshan.model.DataPermit;
import com.xinshan.pojo.role.DataPermitSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by mxt on 17-5-5.
 */
@Service
public class DataPermitService {
    @Autowired
    private DataPermitExtendMapper dataPermitExtendMapper;
    @Autowired
    private DataPermitMapper dataPermitMapper;

    /**
     * rul + role_id 唯一
     * @param dataPermit
     */
    @Transactional
    public void createDataPermit(DataPermit dataPermit) {
        if (dataPermit.getUrl() == null || dataPermit.getRole_id() == null) {
            throw new RuntimeException("url和role_id缺失");
        }
        DataPermit dataPermit1 = getDataPermitByUrlAndRoleId(dataPermit.getRole_id(), dataPermit.getUrl());
        if (dataPermit1 != null) {
            dataPermit1.setData_cols(dataPermit.getData_cols());
            dataPermitMapper.updateByPrimaryKey(dataPermit1);
        }else {
            dataPermitExtendMapper.createDataPermit(dataPermit);
        }
    }

    @Transactional
    public void updateDataPermit(DataPermit dataPermit) {
        dataPermitMapper.updateByPrimaryKey(dataPermit);
    }

    public DataPermit getDataPermitByUrlAndRoleId(int role_id, String url) {
        DataPermitSearchOption dataPermitSearchOption = new DataPermitSearchOption();
        dataPermitSearchOption.setRole_id(role_id);
        dataPermitSearchOption.setUrl(url);
        List<DataPermit> list = dataPermitList(dataPermitSearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public DataPermit getDataPermitById(int data_permit_id) {
        return dataPermitMapper.selectByPrimaryKey(data_permit_id);
    }

    public List<DataPermit> dataPermitList(DataPermitSearchOption dataPermitSearchOption) {
        return dataPermitExtendMapper.dataPermitList(dataPermitSearchOption);
    }

    public Integer countDataPermit(DataPermitSearchOption dataPermitSearchOption) {
        return dataPermitExtendMapper.countDataPermit(dataPermitSearchOption);
    }
}
