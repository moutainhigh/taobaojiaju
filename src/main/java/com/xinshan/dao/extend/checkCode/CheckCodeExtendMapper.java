package com.xinshan.dao.extend.checkCode;

import com.xinshan.model.UserCheckCode;
import com.xinshan.pojo.checkCode.CheckCodeSearchOption;

import java.util.List;

/**
 * Created by mxt on 16-12-28.
 */
public interface CheckCodeExtendMapper {
    void createCheckCode(UserCheckCode userCheckCode);

    List<UserCheckCode> getCheckCode(CheckCodeSearchOption checkCodeSearchOption);
}
