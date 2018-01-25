package com.xinshan.components.role;

import com.xinshan.model.DataPermit;
import com.xinshan.service.DataPermitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by mxt on 17-5-5.
 */
@Component
public class DataPermitComponents {

    private static DataPermitService dataPermitService;
    @Autowired
    public void setDataPermitService(DataPermitService dataPermitService) {
        DataPermitComponents.dataPermitService = dataPermitService;
    }

    public static DataPermit dataPermit(int order_id, String url) {
        return dataPermitService.getDataPermitByUrlAndRoleId(order_id, url);
    }
}
