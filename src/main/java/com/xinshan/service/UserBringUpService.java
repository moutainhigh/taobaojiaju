package com.xinshan.service;

import com.xinshan.components.order.OrderComponents;
import com.xinshan.components.user.UserBringUpSettingComponent;
import com.xinshan.dao.UserBringUpMapper;
import com.xinshan.dao.extend.user.UserBringUpExtendMapper;
import com.xinshan.model.*;
import com.xinshan.model.extend.order.OrderExtend;
import com.xinshan.model.extend.user.UserBringUpExtend;
import com.xinshan.model.extend.user.UserExtend;
import com.xinshan.pojo.user.UserSearchOption;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.constant.order.OrderConstants;
import com.xinshan.utils.constant.user.UserConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mxt on 17-6-9.
 */
@Service
public class UserBringUpService {
    @Autowired
    private UserBringUpMapper userBringUpMapper;
    @Autowired
    private UserBringUpExtendMapper userBringUpExtendMapper;
    @Autowired
    private UserService userService;

    @Transactional
    public void createBringUp(UserBringUp userBringUp) {
        userBringUpExtendMapper.createBringUp(userBringUp);
    }

    /**
     * 下单后添加老带新
     */
    public void createBringUp(OrderExtend orderExtend, Employee employee){
        String customer_phone_number = orderExtend.getCustomer_phone_number();
        UserExtend userExtend = userService.getUserByPhone(customer_phone_number);
        if (userExtend.getUser_type() == 1) {
            UserBringUp userBringUp = new UserBringUp();
            userBringUp.setUser_bring_up_create_employee_name(employee.getEmployee_name());
            userBringUp.setUser_bring_up_create_employee_code(employee.getEmployee_code());
            userBringUp.setNew_user_id(userExtend.getUser_id());
            userBringUp.setOld_user_id(userExtend.getUser_referrals());
            userBringUp.setOrder_id(orderExtend.getOrder_id());
            userBringUp.setUser_bring_up_create_date(DateUtil.currentDate());
            userBringUp.setUser_bring_up_date(DateUtil.currentDate());
            userBringUp.setUser_bring_up_status(UserConstant.USER_BRING_UP_STATUS_INITIAL);
            userBringUpExtendMapper.createBringUp(userBringUp);
        }
        userExtend.setUser_type(2);
        userService.updateUserType(userExtend);
    }

    public void orderComplete(int order_id) {
        OrderExtend orderExtend = OrderComponents.getOrderById(order_id);
        if (orderExtend.getOrder_step() >= OrderConstants.ORDER_STEP_DONE) {
            UserBringUp userBringUp = userBringUpExtendMapper.getBringUpByOrderId(orderExtend.getOrder_id());
            if (userBringUp != null) {
                userBringUp.setOrder_complete_date(DateUtil.currentDate());
                userBringUp.setUser_bring_up_status(UserConstant.USER_BRING_UP_STATUS_ORDER_COMPLETE);
                userBringUpMapper.updateByPrimaryKey(userBringUp);
            }
        }
    }

    public List<UserBringUpExtend> bringUpList(UserSearchOption userSearchOption) {
        List<Integer> bringUpIds = userBringUpExtendMapper.bringUpIds(userSearchOption);
        return list(bringUpIds, userSearchOption);
    }

    public List<Integer> bringUpIds(UserSearchOption userSearchOption) {
        return userBringUpExtendMapper.bringUpIds(userSearchOption);
    }

    /**
     * 被带用户
     * @param user_phone
     * @return
     */
    public List<UserBringUpExtend> newUserBringUpIds(String user_phone) {
        UserSearchOption userSearchOption = new UserSearchOption();
        userSearchOption.setParam(user_phone);
        List<Integer> list = userBringUpExtendMapper.newUserBringUpIds(userSearchOption);
        return list(list, userSearchOption);
    }

    private List<UserBringUpExtend> list(List<Integer> bringUpIds, UserSearchOption userSearchOption) {
        List<UserBringUpExtend> list = new ArrayList<>();
        if (bringUpIds == null || bringUpIds.size() == 0) {
            return list;
        }
        userSearchOption.setBringUpIds(bringUpIds);
        list = userBringUpExtendMapper.bringUpList(userSearchOption);
        for (int i = 0; i < list.size(); i++) {
            UserBringUpExtend userBringUpExtend = list.get(i);
            Integer new_user_id = userBringUpExtend.getNew_user_id();
            UserExtend user = userService.getUserById(new_user_id);
            userBringUpExtend.setNewUser(user);
        }
        return list;
    }

    @Transactional
    public void createBringUpSetting(UserBringUpSetting bringUpSetting, Employee employee) {
        userBringUpExtendMapper.invalidBringUpSetting();
        bringUpSetting.setSetting_create_date(DateUtil.currentDate());
        bringUpSetting.setSetting_enable(1);
        bringUpSetting.setSetting_create_employee_code(employee.getEmployee_code());
        bringUpSetting.setSetting_create_employee_name(employee.getEmployee_name());
        userBringUpExtendMapper.createBringUpSetting(bringUpSetting);
        UserBringUpSettingComponent.setUserBringUpSetting(bringUpSetting);
    }

    public UserBringUpSetting bringUpSetting() {
        return userBringUpExtendMapper.bringUpSetting();
    }

    @Transactional
    public void createBringUpCashBack(UserBringUpCashBack bringUpCashBack) {
        UserBringUp userBringUp = userBringUpMapper.selectByPrimaryKey(bringUpCashBack.getUser_bring_up_id());
        if (userBringUp.getUser_bring_up_status().equals(UserConstant.USER_BRING_UP_STATUS_CASH_BACK)) {
            return;
        }
        userBringUp.setUser_bring_up_status(UserConstant.USER_BRING_UP_STATUS_CASH_BACK);
        userBringUpMapper.updateByPrimaryKey(userBringUp);
        if (bringUpCashBack.getUser_bring_up_cash_back_date() == null) {
            bringUpCashBack.setUser_bring_up_cash_back_date(DateUtil.currentDate());
        }
        userBringUpExtendMapper.createBringUpCashBack(bringUpCashBack);
    }
}
