package com.xinshan.service;

import com.xinshan.dao.UserCheckCodeMapper;
import com.xinshan.dao.extend.checkCode.CheckCodeExtendMapper;
import com.xinshan.model.UserCheckCode;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.shortMessage.AliMessage;
import com.xinshan.utils.shortMessage.ShortMessageUtils;
import com.xinshan.pojo.checkCode.CheckCodeSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by mxt on 16-12-28.
 */
@Service
public class CheckCodeService {
    @Autowired
    private CheckCodeExtendMapper checkCodeExtendMapper;
    @Autowired
    private UserCheckCodeMapper userCheckCodeMapper;
    @Autowired
    private UserService userService;
    @Transactional
    public UserCheckCode createCheckCode(String phone_number) throws Exception {
        //添加新的验证码
        String checkCode = randomCode();
        Date date = DateUtil.currentDate();
        UserCheckCode userCheckCode = new UserCheckCode();
        userCheckCode.setEffective_date(date);
        userCheckCode.setPhone_number(phone_number);
        userCheckCode.setCheck_code(checkCode);
        //发送验证码到手机
        if (AliMessage.sendAliCheckCode(phone_number, checkCode, userCheckCode)) {
            CheckCodeSearchOption checkCodeSearchOption = new CheckCodeSearchOption();
            checkCodeSearchOption.setPhone_number(phone_number);
            checkCodeSearchOption.setInvalid_type(0);
            List<UserCheckCode> list = checkCodes(checkCodeSearchOption);
            if (list != null && list.size() > 0) {//以前发送的未使用验证码失效
                for (int i = 0; i < list.size(); i++) {
                    UserCheckCode userCheckCode1 = list.get(i);
                    userCheckCode1.setInvalid_type(2);
                    userCheckCode1.setInvalid_date(date);
                    userCheckCodeMapper.updateByPrimaryKey(userCheckCode1);
                }
            }
            checkCodeExtendMapper.createCheckCode(userCheckCode);
        }
        return userCheckCode;
    }

    private String randomCode() {
        Random random = new Random(System.currentTimeMillis());
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < 6; i++) {
            stringBuffer.append(random.nextInt(10));
        }
        return stringBuffer.toString();
    }

    public List<UserCheckCode> checkCodes(CheckCodeSearchOption checkCodeSearchOption) {
        return checkCodeExtendMapper.getCheckCode(checkCodeSearchOption);
    }

    @Transactional
    public void updateCheckCode(UserCheckCode userCheckCode) {
        userCheckCodeMapper.updateByPrimaryKey(userCheckCode);
    }

    @Transactional
    public void userCheckCode(UserCheckCode userCheckCode, String openid, int user_id) {
        userCheckCode.setInvalid_type(1);
        //userCheckCode.setInvalid_date(DateUtil.currentDate());
        userCheckCode.setUse_date(DateUtil.currentDate());
        userCheckCodeMapper.updateByPrimaryKey(userCheckCode);
        userService.createUserOpenid(openid, user_id);
    }
}
