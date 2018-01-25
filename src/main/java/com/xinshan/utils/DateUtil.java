package com.xinshan.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jonson.xu on 5/25/15.
 */
public class DateUtil {
    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.YEAR, 1);
        calendar1.add(Calendar.DAY_OF_YEAR, 1);
        System.out.println(halfYear(calendar.getTime(), calendar1.getTime()));
    }

    public static Date startOfTheDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date endOfTheDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 日期加减
     * @param date
     * @param i
     * @return
     */
    public static Calendar dateAdd(Date date, int i) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, i);
            return calendar;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 组合日期与时间
     *
     * @param date
     * @param hour
     * @return
     */
    public static Calendar combDate(Date date, String hour) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int y = calendar.get(Calendar.YEAR);
            int m = calendar.get(Calendar.MONTH);
            int d = calendar.get(Calendar.DATE);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Date dateTime = simpleDateFormat.parse(hour);
            calendar.setTime(dateTime);
            calendar.set(y, m, d);
            return calendar;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 设定月份的第几天
     *
     * @param i
     * @param date
     * @return
     */
    public static Date setMonthDay(int i, Date date) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DATE, i);
            return calendar.getTime();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Calendar parse(String dateStr, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            Date parse = simpleDateFormat.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parse);
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字符串转日期
     *
     * @param period
     * @return
     */
    public static Date stringToDate(String period) {
        String[] date_format = date_format_string();
        Date date = null;
        for (String date_format_str : date_format) {
            DateFormat format1 = new SimpleDateFormat(date_format_str);
            try {
                date = format1.parse(period);
                break;
            } catch (Exception ex) {
                //ex.printStackTrace();
            }
        }
        return date;
    }

    private static String[] date_format_string() {
        String[] date_format = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd", "yyyy.MM.dd", "yyyy.M.dd", "yyyy-MM", "yyyy-M"};
        return date_format;
    }

    /**
     * 日期格式化
     * @param date
     * @param format
     * @return
     */
    public static String format(Date date, String format) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            return simpleDateFormat.format(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Period 返回每月最大天
     * type:
     * Calender.DAY_OF_MONTH //每月多少天
     *
     * @param period
     * @return
     */
    public static Integer getMaxDays(String period) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        Date periodDate = null;
        try {
            periodDate = simpleDateFormat.parse(period);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(periodDate);
            return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 间隔天数
     * @param startDate
     * @param endDate
     * @return
     */
    public static Integer getDayBetween(Date startDate, Date endDate) {
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        end.set(Calendar.HOUR_OF_DAY, 0);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);

        long n = end.getTimeInMillis() - start.getTimeInMillis();
        return (int) (n/(60 * 60 * 24 * 1000l));
    }

    /**
     * 间隔月
     * @param startDate
     * @param endDate
     * @return
     */
    public static Integer getMonthBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null || !startDate.before(endDate)) {
            return null;
        }
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        int year1 = start.get(Calendar.YEAR);
        int year2 = end.get(Calendar.YEAR);
        int month1 = start.get(Calendar.MONTH);
        int month2 = end.get(Calendar.MONTH);
        int n = (year2 - year1) * 12;
        n = n + month2 - month1;
        return n;
    }
    /**
     * 间隔月，算天，多一天就多算一个月
     * @param startDate
     * @param endDate
     * @return
     */
    public static Integer getMonthBetweenWithDay(Date startDate, Date endDate) {
        if (startDate == null || endDate == null || !startDate.before(endDate)) {
            return null;
        }
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        int year1 = start.get(Calendar.YEAR);
        int year2 = end.get(Calendar.YEAR);
        int month1 = start.get(Calendar.MONTH);
        int month2 = end.get(Calendar.MONTH);
        int n = (year2 - year1) * 12;
        n = n + month2 - month1;
        int day1 = start.get(Calendar.DAY_OF_MONTH);
        int day2 = end.get(Calendar.DAY_OF_MONTH);
        if (day1 <= day2) {
            n++;
        }
        return n;
    }

    public static Integer halfYear(Date startDate, Date endDate) {
        if (startDate == null || endDate == null || !startDate.before(endDate)) {
            return null;
        }

        int month = getMonthBetweenWithDay(startDate, endDate);
        int n = month/6;
        if (month%6>0){
            n++;
        }
        return n;
    }

    public static Date currentDate() {
        return currentCalendar().getTime();
    }

    public static int currentTime() {
        return (int) (currentCalendar().getTimeInMillis()/1000);
    }

    private static Calendar currentCalendar() {
        return Calendar.getInstance();
    }

    /**
     * 时间补全时分秒
     * @param date
     * @return
     */
    public static Date date(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        if (hour == 0 && minute == 0 && second == 0) {
            String format = format(Calendar.getInstance().getTime(), "HH:mm:ss");
            String format1 = format(date, "yyyy-MM-dd");
            date = stringToDate(format1 + " " + format);
        }
        return date;
    }
}
