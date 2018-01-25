package com.xinshan.dao.extend.user;

import com.xinshan.model.UserBringUp;
import com.xinshan.model.UserBringUpCashBack;
import com.xinshan.model.UserBringUpSetting;
import com.xinshan.model.extend.user.UserBringUpExtend;
import com.xinshan.pojo.user.UserSearchOption;

import java.util.List;

/**
 * Created by mxt on 17-6-9.
 */
public interface UserBringUpExtendMapper {

    void createBringUp(UserBringUp userBringUp);

    List<Integer> bringUpIds(UserSearchOption userSearchOption);

    List<UserBringUpExtend> bringUpList(UserSearchOption userSearchOption);

    List<Integer> newUserBringUpIds(UserSearchOption userSearchOption);

    List<Integer> oldUserBringUpIds(UserSearchOption userSearchOption);

    void createBringUpSetting(UserBringUpSetting bringUpSetting);

    UserBringUpSetting bringUpSetting();

    void invalidBringUpSetting();

    UserBringUp getBringUpByOrderId(int order_id);

    void createBringUpCashBack(UserBringUpCashBack bringUpCashBack);
}
