package com.xinshan.dao.extend.user;

import com.xinshan.model.User;
import com.xinshan.model.UserDeliveryAddress;
import com.xinshan.model.extend.user.UserExtend;
import com.xinshan.pojo.user.UserSearchOption;

import java.util.List;

/**
 * Created by mxt on 16-10-21.
 */
public interface UserExtendMapper {
    void createUser(User user);

    List<UserExtend> userList(UserSearchOption userSearchOption);

    Integer countUser(UserSearchOption userSearchOption);

    void updateUserType(User user);

    void createDeliveryAddress(UserDeliveryAddress deliveryAddress);

}
