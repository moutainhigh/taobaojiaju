package com.xinshan.dao.extend.user;

import com.xinshan.model.UserRecord;
import com.xinshan.model.UserSource;
import com.xinshan.model.UserStage;
import com.xinshan.model.UserStatus;
import com.xinshan.model.extend.user.UserRecordExtend;
import com.xinshan.pojo.user.UserSearchOption;

import java.util.List;

/**
 * Created by mxt on 16-11-3.
 */
public interface UserRecordExtendMapper {
    void createStatus(UserStatus userStatus);
    void createStage(UserStage userStage);
    void createSource(UserSource userSource);
    void createRecord(UserRecord userRecord);

    List<UserRecordExtend> recordList(UserSearchOption userSearchOption);
    Integer countRecord(UserSearchOption userSearchOption);
}
