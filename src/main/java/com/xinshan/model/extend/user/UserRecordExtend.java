package com.xinshan.model.extend.user;

import com.xinshan.model.*;

/**
 * Created by mxt on 16-11-3.
 */
public class UserRecordExtend extends UserRecord {
    private User user;
    private UserSource userSource;
    private UserStage userStage;
    private UserStatus userStatus;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserSource getUserSource() {
        return userSource;
    }

    public void setUserSource(UserSource userSource) {
        this.userSource = userSource;
    }

    public UserStage getUserStage() {
        return userStage;
    }

    public void setUserStage(UserStage userStage) {
        this.userStage = userStage;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }
}
