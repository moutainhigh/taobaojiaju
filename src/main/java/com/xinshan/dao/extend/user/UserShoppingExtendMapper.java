package com.xinshan.dao.extend.user;

import com.xinshan.model.UserShopping;
import com.xinshan.model.UserShoppingCommodity;
import com.xinshan.model.extend.user.UserShoppingExtend;
import com.xinshan.pojo.user.UserSearchOption;

import java.util.List;

/**
 * Created by mxt on 17-7-31.
 */
public interface UserShoppingExtendMapper {

    void createUserShopping(UserShopping userShopping);

    void createUserShoppingCommodity(UserShoppingCommodity userShoppingCommodity);

    List<UserShoppingExtend> userShoppingList(UserSearchOption userSearchOption);

    List<Integer> userShoppingIds(UserSearchOption userSearchOption);

    Integer countUserShopping(UserSearchOption userSearchOption);

    void deleteUserShoppingCommodity(int user_shopping_id);

    void updateUserShopping(UserShopping userShopping);
}
