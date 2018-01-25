package com.xinshan.dao.extend.role;

import com.xinshan.model.DataPermit;
import com.xinshan.pojo.role.DataPermitSearchOption;

import java.util.List;

/**
 * Created by mxt on 17-5-5.
 */
public interface DataPermitExtendMapper {

    void createDataPermit(DataPermit dataPermit);

    List<DataPermit> dataPermitList(DataPermitSearchOption dataPermitSearchOption);

    Integer countDataPermit(DataPermitSearchOption dataPermitSearchOption);
}
