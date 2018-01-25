package com.xinshan.utils.commodity;

import com.xinshan.pojo.commodity.CommoditySearchOption;
import com.xinshan.utils.Request2ModelUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by mxt on 17-6-12.
 */
public class CommodityUtil {
    private CommodityUtil() {
    }
    private static CommodityUtil commodityUtil;
    public static CommodityUtil getCommodityUtil() {
        if (commodityUtil == null) {
            commodityUtil = new CommodityUtil();
        }
        return commodityUtil;
    }

    public boolean noParam(CommoditySearchOption commoditySearchOption) {
        Class<CommoditySearchOption> aClass = CommoditySearchOption.class;
        Field[] fields = aClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String name = field.getName();
            if (name.equals("commodity_num") || name.equals("commodity_status")) {
                continue;
            }
            Method method = Request2ModelUtils.getMethod(name, CommoditySearchOption.class);
            if (method != null) {
                try {
                    Object invoke = method.invoke(commoditySearchOption);
                    if (invoke != null) {
                        return false;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
