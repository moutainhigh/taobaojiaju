package com.xinshan.components.user;

import com.xinshan.model.UserShopping;
import com.xinshan.model.extend.user.UserShoppingExtend;
import com.xinshan.service.UserShoppingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by mxt on 17-8-15.
 */
@Component
public class UserShoppingComponent {

    private static UserShoppingService userShoppingService;
    @Autowired
    public void setUserShoppingService(UserShoppingService userShoppingService) {
        UserShoppingComponent.userShoppingService = userShoppingService;
    }

    public static void createUserShopping(UserShoppingExtend userShoppingExtend) {
        userShoppingService.syncCreateUserShopping(userShoppingExtend);
    }

    public static void updateUserShopping(UserShoppingExtend userShoppingExtend) {
        userShoppingService.syncUpdateUserShopping(userShoppingExtend);
    }
}
