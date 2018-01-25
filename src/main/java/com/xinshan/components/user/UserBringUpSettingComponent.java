package com.xinshan.components.user;

import com.xinshan.model.UserBringUpSetting;
import com.xinshan.service.UserBringUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by mxt on 17-7-20.
 */
@Component
public class UserBringUpSettingComponent {
    private static UserBringUpService userBringUpService;

    @Autowired
    public void setUserBringUpService(UserBringUpService userBringUpService) {
        this.userBringUpService = userBringUpService;
    }

    private static UserBringUpSetting userBringUpSetting;

    public static UserBringUpSetting getUserBringUpSetting() {
        if (userBringUpSetting == null) {
            userBringUpSetting = userBringUpService.bringUpSetting();
        }
        return userBringUpSetting;
    }

    public static void setUserBringUpSetting(UserBringUpSetting userBringUpSetting) {
        UserBringUpSettingComponent.userBringUpSetting = userBringUpSetting;
    }
}
