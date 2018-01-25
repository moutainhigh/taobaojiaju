package com.xinshan.utils;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.model.PurchaseCommodity;
import com.xinshan.model.extend.purchase.PurchaseCommodityExtend;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by jonson.xu on 10/30/14.
 */
public class Request2ModelUtils {
    public static <K> K covert(Class<K> T, HttpServletRequest request) {
        try {
            K obj = T.newInstance();
            //获取类的方法集合
            Set<Method> methodSet = get_methods(T);
            Iterator<Method> methodIterator = methodSet.iterator();
            while (methodIterator.hasNext()) {
                Method method = methodIterator.next();
                String key = method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);
                String value = request.getParameter(key);
                Class<?>[] type = method.getParameterTypes();
                Object[] param_value = convert_param_type(type, value);
                method.invoke(obj, param_value);
            }
            return obj;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * 取全部Set方法
     *
     * @param T
     * @return
     */
    public static Set<Method> get_methods(Class T) {
        Method[] methods = T.getMethods();
        Set<Method> methodSet = new HashSet<Method>();
        for (Method method : methods) {
            if (method.getName().startsWith("set")) {
                methodSet.add(method);
            }
        }
        return methodSet;
    }

    public static Set<Method> get_get_methods(Class T) {
        Method[] methods = T.getMethods();
        Set<Method> methodSet = new HashSet<Method>();
        for (Method method : methods) {
            if (method.getName().startsWith("get")) {
                methodSet.add(method);
            }
        }
        return methodSet;
    }

    /**
     * 取自定义Set方法
     *
     * @param T
     * @return
     */
    public static Set<Method> get_declared_methods(Class T) {
        Method[] methods = T.getMethods();
        Set<Method> methodSet = new HashSet<Method>();
        for (Method method : methods) {
            if (method.getName().startsWith("set")) {
                methodSet.add(method);
            }
        }
        return methodSet;
    }

    /**
     * 取自定义get方法
     * @param T
     * @return
     */
    public static Set<Method> get_getDeclared_methods(Class T) {
        Method[] methods = T.getDeclaredMethods();
        Set<Method> methodSet = new HashSet<Method>();
        for (Method method : methods) {
            if (method.getName().startsWith("get")) {
                methodSet.add(method);
            }
        }
        return methodSet;
    }

    /**
     * @param type
     * @param value
     * @return 转换参数类型
     */
    private static Object[] convert_param_type(Class<?>[] type, Object value) {
        Object[] objects = new Object[type.length];
        int index = 0;
        for (Class c : type) {
            if (value == null || value.toString().equals("")) {
                objects[index] = null;
                continue;
            }
            if (c.getName().equals("int") || c.getName().equals(Integer.class.getName())) {
                objects[index] = Integer.parseInt(value.toString().trim());
            } else if (c.getName().equals("byte") || c.getName().equals(Byte.class.getName())) {
                objects[index] = Byte.parseByte(value.toString().trim());
            } else if (c.getName().equals(Float.class.getName()) || c.getName().equals("float")) {
                objects[index] = Float.parseFloat(value.toString().trim());
            } else if (c.getName().equals("short") || c.getName().equals(Short.class.getName())) {
                objects[index] = Short.parseShort(value.toString().trim());
            } else if (c.getName().equals("double") || c.getName().equals(Double.class.getName())) {
                objects[index] = Double.parseDouble(value.toString().trim());
            } else if (c.getName().equals(String.class.getName())) {
                objects[index] = value.toString().trim();
            } else if (c.getName().equals(Date.class.getName())) {
                String[] date_format = date_format_string();
                for (String date_format_str : date_format) {
                    DateFormat format1 = new SimpleDateFormat(date_format_str);
                    try {
                        objects[index] = format1.parse(value.toString().trim());
                        break;
                    } catch (Exception ex) {
                        //ex.printStackTrace();
                    }
                }
            } else if (c.getName().equals(BigDecimal.class.getCanonicalName())) {
                objects[index] = new BigDecimal(value.toString().trim());
            }
            else {
//                new Throwable("发现未定义的类型！类型名：" + c.getName()).printStackTrace();
            }
            index++;
        }
        return objects;
    }

    /*
        字符串日期格式集合
     */
    private static String[] date_format_string() {
        String[] date_format = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd"};
        return date_format;
    }

    /**
     * 根据传递的参数修改数据
     * @param o
     * @param parameterMap
     */
    public static void covertObj(Object o, Map<String, String[]> parameterMap) {
        Class clazz = o.getClass();
        Iterator<Map.Entry<String, String[]>> iterator = parameterMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String[]> entry = iterator.next();
            String key = entry.getKey().trim();
            String value = entry.getValue()[0].trim();
            try {
                Method method = setMethod(key, clazz);
                if (method!=null) {
                    Class[] parameterTypes = method.getParameterTypes();
                    if (method != null) {
                        Object[] param_value = convert_param_type(parameterTypes, value);
                        method.invoke(o, param_value);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 根据传递的参数修改数据
     * @param o
     * @param parameterMap map参数
     */
    public static void covertObjWithMap(Object o, Map<String, String> parameterMap) {
        Class clazz = o.getClass();
        Iterator<Map.Entry<String, String>> iterator = parameterMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String key = entry.getKey().trim();
            String value = entry.getValue().trim();
            try {
                Method method = setMethod(key, clazz);
                if (method!=null) {
                    Class[] parameterTypes = method.getParameterTypes();
                    if (method != null) {
                        Object[] param_value = convert_param_type(parameterTypes, value);
                        method.invoke(o, param_value);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据传递的参数修改数据
     * @param o
     * @param paramObj model参数
     */
    public static void covertObj(Object o, Object paramObj) {
        Field[] fields = o.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                Field field = fields[i];
                Method getMethod = getMethod(field.getName(), paramObj.getClass());
                if (getMethod != null) {
                    Object value = getMethod.invoke(paramObj, null);
                    Method setMethod = setMethod(field.getName(), o.getClass());
                    if (setMethod != null) {
                        if (value!=null && !value.toString().equals("")) {
                            setMethod.invoke(o, value);
                        }
                    }
                }
            }catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }

    /**
     * 根据传递的参数修改数据，参数值为null时不修改数据
     * @param o
     * @param paramObj model参数
     * @param map 注释
     */
    public static void covertObj(Object o, Object paramObj, Map<String, String> map) {
        Field[] fields = o.getClass().getDeclaredFields();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < fields.length; i++) {
            try {
                Field field = fields[i];
                Method getMethod = getMethod(field.getName(), paramObj.getClass());
                if (getMethod != null) {
                    Object value = getMethod.invoke(paramObj, null);
                    Object valueOld = getMethod.invoke(o, null);
                    Method setMethod = setMethod(field.getName(), o.getClass());
                    if (setMethod != null) {
                        if (value!=null && !value.toString().equals("")) {
                            setMethod.invoke(o, value);
                            if (map.containsKey(field.getName())){
                                String comment = map.get(field.getName());
                                field.getDeclaringClass();
                                if (!equalsValue(value, valueOld, field.getType())) {
                                    stringBuffer.append("原").append(comment).append("：").append(valueOld);
                                    stringBuffer.append("变更后").append(comment).append("：").append(value).append(",");
                                }
                            }
                        }
                    }
                }
            }catch (Exception e) {
                //e.printStackTrace();
            }
        }
        map.put("1", stringBuffer.toString());
    }

    private static boolean equalsValue(Object o1, Object o2, Class c) {
        if (c.getName().equals("int") || c.getName().equals(Integer.class.getName())) {
            Integer i1 = new Integer(o1.toString());
            Integer i2 = new Integer(o2.toString());
            if (i1.intValue() == i2.intValue()) {
                return true;
            }
        } else if (c.getName().equals("byte") || c.getName().equals(Byte.class.getName())) {
            Byte b1 = new Byte(o1.toString());
            Byte b2 = new Byte(o1.toString());
            if (b1.byteValue() == b2.byteValue()) {
                return true;
            }
        } else if (c.getName().equals(Float.class.getName()) || c.getName().equals("float")) {
            Float f1 = new Float(o1.toString());
            Float b2 = new Float(o1.toString());
            if (f1.floatValue() == b2.floatValue()) {
                return true;
            }
        } else if (c.getName().equals("short") || c.getName().equals(Short.class.getName())) {
            Short f1 = new Short(o1.toString());
            Short b2 = new Short(o1.toString());
            if (f1.shortValue() == b2.shortValue()) {
                return true;
            }
        } else if (c.getName().equals("double") || c.getName().equals(Double.class.getName())) {
            Double f1 = new Double(o1.toString());
            Double b2 = new Double(o1.toString());
            if (f1.doubleValue() == b2.doubleValue()) {
                return true;
            }
        } else if (c.getName().equals(String.class.getName())) {
            if (o1.toString().equals(o2.toString())) {
                return true;
            }
        } else if (c.getName().equals(Date.class.getName())) {
            Date date1 = DateUtil.stringToDate(o1.toString());
            Date date2 = DateUtil.stringToDate(o2.toString());
            if (date1.getTime() == date2.getTime()) {
                return true;
            }
        } else if (c.getName().equals(BigDecimal.class.getCanonicalName())) {
            BigDecimal bigDecimal1 = new BigDecimal(o1.toString());
            BigDecimal bigDecimal2 = new BigDecimal(o2.toString());
            if (bigDecimal1.doubleValue() == bigDecimal2.doubleValue()) {
                return true;
            }
        }
        else {
        }
        return false;
    }

    /**
     *
     * @param obj
     * @param obiExtend
     * @return
     */
    public static Object init(Object obj, Object obiExtend) {
        Class clazz = obj.getClass();
        Set<Method> getMethods = Request2ModelUtils.get_getDeclared_methods(clazz);
        Iterator<Method> ite = getMethods.iterator();
        while (ite.hasNext()) {
            try {
                Method method = ite.next();
                String name = method.getName();//getPosition
                String fileName = name.substring(3,4).toLowerCase()+name.substring(4,name.length());
                Object o = method.invoke(obj, null);//get
                Method setMethod = Request2ModelUtils.setMethod(fileName, clazz);
                setMethod.invoke(obiExtend, o);//set
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return obiExtend;
    }

    public static Method setMethod(String fieldName, Class clazz){
        try {
            Class[] parameterTypes = new Class[1];
            Field field = clazz.getDeclaredField(fieldName);
            parameterTypes[0] = field.getType();
            StringBuffer sb = new StringBuffer();
            sb.append("set");
            sb.append(fieldName.substring(0, 1).toUpperCase());
            sb.append(fieldName.substring(1));
            Method method = clazz.getMethod(sb.toString(), parameterTypes);
            return method;
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return null;
    }

    public static Method getMethod(String fieldName, Class clazz){
        StringBuffer sb = new StringBuffer();
        sb.append("get");
        sb.append(fieldName.substring(0, 1).toUpperCase());
        sb.append(fieldName.substring(1));
        try {
            return clazz.getMethod(sb.toString());
        } catch (Exception e) {

        }
        return null;
    }

    public static void main(String[] args) {
        PurchaseCommodity purchaseCommodity = new PurchaseCommodity();
        purchaseCommodity.setPurchase_arrival_num(100);

        PurchaseCommodityExtend purchaseCommodityExtend = new PurchaseCommodityExtend();
        covertParent(purchaseCommodityExtend, purchaseCommodity);
        System.out.println(JSONObject.toJSONString(purchaseCommodityExtend));
    }

    public static void covertParent(Object o, Object parent) {
        Set<Method> set = get_get_methods(parent.getClass());
        Iterator<Method> iterator = set.iterator();
        while (iterator.hasNext()) {
            Method method = iterator.next();
            try {
                Object value = method.invoke(parent);//getA
                if (value != null) {
                    String methodName = "set"+method.getName().substring(3);
                    Method setMethod = o.getClass().getMethod(methodName, value.getClass());
                    setMethod.invoke(o, value);
                }
            } catch (Exception e) {

            }
        }
    }


}
