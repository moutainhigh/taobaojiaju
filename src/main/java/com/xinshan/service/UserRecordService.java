package com.xinshan.service;

import com.xinshan.dao.UserRecordMapper;
import com.xinshan.dao.UserSourceMapper;
import com.xinshan.dao.UserStageMapper;
import com.xinshan.dao.UserStatusMapper;
import com.xinshan.dao.extend.user.UserRecordExtendMapper;
import com.xinshan.model.UserRecord;
import com.xinshan.model.UserSource;
import com.xinshan.model.UserStage;
import com.xinshan.model.UserStatus;
import com.xinshan.model.extend.user.UserRecordExtend;
import com.xinshan.pojo.user.UserSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by mxt on 16-11-3.
 */
@Service
public class UserRecordService {
    @Autowired
    private UserRecordExtendMapper userRecordExtendMapper;
    @Autowired
    private UserStatusMapper userStatusMapper;
    @Autowired
    private UserStageMapper userStageMapper;
    @Autowired
    private UserSourceMapper userSourceMapper;
    @Autowired
    private UserRecordMapper userRecordMapper;

    @Transactional
    public void createStatus(UserStatus userStatus) {
        userRecordExtendMapper.createStatus(userStatus);
    }
    @Transactional
    public void updateStatus(UserStatus userStatus) {
        userStatusMapper.updateByPrimaryKey(userStatus);
    }
    @Transactional
    public void createStage(UserStage userStage) {
        userRecordExtendMapper.createStage(userStage);
    }
    @Transactional
    public void updateStage(UserStage userStage) {
        userStageMapper.updateByPrimaryKey(userStage);
    }
    @Transactional
    public void createSource(UserSource userSource) {
        userRecordExtendMapper.createSource(userSource);
    }
    @Transactional
    public void updateSource(UserSource userSource) {
        userSourceMapper.updateByPrimaryKey(userSource);
    }

    @Transactional
    public void createRecord(UserRecord userRecord) {
        userRecordExtendMapper.createRecord(userRecord);
    }
    @Transactional
    public void updateRecord(UserRecord userRecord) {
        userRecordMapper.updateByPrimaryKey(userRecord);
    }

    public List<UserRecordExtend> recordList(UserSearchOption userSearchOption) {
        return userRecordExtendMapper.recordList(userSearchOption);
    }
    public Integer countRecord(UserSearchOption userSearchOption) {
        return userRecordExtendMapper.countRecord(userSearchOption);
    }

    public List<UserStatus> userStatuses(UserSearchOption userSearchOption) {
        return userStatusMapper.selectAll();
    }
    public List<UserStage> userStages(UserSearchOption userSearchOption) {
        return userStageMapper.selectAll();
    }
    public List<UserSource> userSources(UserSearchOption userSearchOption) {
        return userSourceMapper.selectAll();
    }
}
