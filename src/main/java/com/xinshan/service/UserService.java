package com.xinshan.service;

import com.xinshan.dao.UserDeliveryAddressMapper;
import com.xinshan.dao.UserMapper;
import com.xinshan.dao.UserOpenidMapper;
import com.xinshan.dao.extend.user.UserExtendMapper;
import com.xinshan.model.User;
import com.xinshan.model.UserDeliveryAddress;
import com.xinshan.model.UserOpenid;
import com.xinshan.model.extend.user.UserExtend;
import com.xinshan.pojo.user.UserSearchOption;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.SplitUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Created by mxt on 16-10-21.
 */
@Service
public class UserService {
    @Autowired
    private UserExtendMapper userExtendMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserOpenidMapper userOpenidMapper;
    @Autowired
    private UserDeliveryAddressMapper userDeliveryAddressMapper;

    @Transactional
    public void createUser(User user) {
        userExtendMapper.createUser(user);
        createDeliveryAddress(user);
    }

    @Transactional
    public boolean updateUser(User user) {
        try {
            setHistoryDaogou(user);
            userMapper.updateByPrimaryKey(user);
            updateUserDeliveryAddresss(user);
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void updateUserType(User user) {
        userExtendMapper.updateUserType(user);
    }
    private void setHistoryDaogou(User user) {
        String record_employee_code = user.getRecord_employee_code();
        if (record_employee_code != null) {
            String history_daogou = user.getHistory_daogou();
            if (history_daogou != null) {
                Set<String> set = SplitUtils.splitToStrSet(history_daogou, ",");
                set.add(record_employee_code);
                user.setHistory_daogou(SplitUtils.setToString(set));
            }else {
                user.setHistory_daogou(record_employee_code);
            }
        }
    }

    public User createUser(String userName, String userPhone, String userAddress,
                           String employee_code, String employee_name, Integer position_id,
                           String province_zip, String city_zip, String district_zip, String user_second_phone) {
        User user = getUserByPhone(userPhone);
        if (user == null) {
            user = new User();
            user.setUser_type(3);
            user.setUser_name(userName);
            user.setUser_phone(userPhone);
            user.setUser_address(userAddress);
            user.setRecord_employee_code(employee_code);
            user.setRecord_employee_name(employee_name);
            user.setUser_create_date(DateUtil.currentDate());
            user.setPosition_id(position_id);
            user.setProvince_zip(province_zip);
            user.setDistrict_zip(district_zip);
            user.setCity_zip(city_zip);
            user.setUser_second_phone(user_second_phone);
            userExtendMapper.createUser(user);
            createDeliveryAddress(user);
        }else {
            user.setUser_address(userAddress);
            user.setUser_name(userName);
            user.setPosition_id(position_id);
            user.setProvince_zip(province_zip);
            user.setDistrict_zip(district_zip);
            user.setCity_zip(city_zip);
            user.setUser_second_phone(user_second_phone);
            userMapper.updateByPrimaryKey(user);
            updateUserDeliveryAddresss(user);
        }
        return user;
    }

    public void createUserOpenid(String openid, int user_id) {
        UserOpenid userOpenid = new UserOpenid();
        userOpenid.setUser_id(user_id);
        userOpenid.setOpenid(openid);
        userOpenidMapper.insert(userOpenid);
    }

    public List<UserExtend> userList(UserSearchOption userSearchOption) {
        return userExtendMapper.userList(userSearchOption);
    }

    public Integer countUser(UserSearchOption userSearchOption) {
        return userExtendMapper.countUser(userSearchOption);
    }

    public UserExtend getUserByPhone(String user_phone) {
        UserSearchOption userSearchOption = new UserSearchOption();
        userSearchOption.setUser_phone(user_phone);
        List<UserExtend> list = userList(userSearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public UserExtend getUserByOpenid(String openid) {
        UserSearchOption userSearchOption = new UserSearchOption();
        userSearchOption.setUser_weixin_openid(openid);
        List<UserExtend> list = userList(userSearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public UserExtend getUserById(Integer user_id) {
        UserSearchOption userSearchOption = new UserSearchOption();
        userSearchOption.setUser_id(user_id);
        List<UserExtend> list = userList(userSearchOption);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public void createDeliveryAddress(User user) {
        UserDeliveryAddress userDeliveryAddress = new UserDeliveryAddress();
        userDeliveryAddress.setCity_zip(user.getCity_zip());
        userDeliveryAddress.setDistrict_zip(user.getDistrict_zip());
        userDeliveryAddress.setProvince_zip(user.getProvince_zip());
        userDeliveryAddress.setDelivery_address(user.getUser_address());
        userDeliveryAddress.setUser_id(user.getUser_id());
        userDeliveryAddress.setUser_delivery_address_enable(1);
        userExtendMapper.createDeliveryAddress(userDeliveryAddress);
        user.setUser_delivery_address_id(userDeliveryAddress.getUser_delivery_address_id());
        userMapper.updateByPrimaryKey(user);
    }

    public void updateUserDeliveryAddresss(User user) {
        UserDeliveryAddress userDeliveryAddress = userDeliveryAddressMapper.selectByPrimaryKey(user.getUser_delivery_address_id());
        if (userDeliveryAddress == null) {
            createDeliveryAddress(user);
        }else {
            userDeliveryAddress.setCity_zip(user.getCity_zip());
            userDeliveryAddress.setDistrict_zip(user.getDistrict_zip());
            userDeliveryAddress.setProvince_zip(user.getProvince_zip());
            userDeliveryAddress.setDelivery_address(user.getUser_address());
            userDeliveryAddress.setUser_id(user.getUser_id());
            userDeliveryAddress.setUser_delivery_address_enable(1);
            userDeliveryAddressMapper.updateByPrimaryKey(userDeliveryAddress);
        }
    }
}
