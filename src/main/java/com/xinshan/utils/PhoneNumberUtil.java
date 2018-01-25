package com.xinshan.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by mxt on 17-6-5.
 */
public class PhoneNumberUtil {

    /**
     * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数
     * 此方法中前三位格式有：
     * 13+任意数
     * 15+除4的任意数
     * 18+除1和4的任意数
     * 17+除9的任意数
     * 147
     */
    public static boolean isChinaPhoneLegal(String str) {
        try {
            //String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147)|(198)|(166)|(199)|(181)|(184))\\d{8}$";
            Pattern p = Pattern.compile(CommonUtils.phone_number_pattern);
            Matcher m = p.matcher(str);
            return m.matches();
        }catch (PatternSyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }
}
