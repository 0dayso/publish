/**
 * 
 */
package com.shinemo.publish.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author david
 *
 */
public class ThreadLocalDateUtil {
    /**
     * feed流专用时间格式，需要别的时间格式需要新加
     */
    private static final String date_format = "yyyy-MM-dd HH:mm:ss";
    /**
     * h5页面的修改时间
     */
    private static final String getDate_format = "yyyy-MM-dd";

    private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>();

    private static ThreadLocal<DateFormat> threadLocalD = new ThreadLocal<DateFormat>();


    public static DateFormat getDateFormat()
    {
        DateFormat df = threadLocal.get();
        if(df==null){
            df = new SimpleDateFormat(date_format);
            threadLocal.set(df);
        }
        return df;
    }

    public static DateFormat getDateFormatForYMD()
    {
        DateFormat df = threadLocalD.get();
        if(df==null){
            df = new SimpleDateFormat(getDate_format);
            threadLocalD.set(df);
        }
        return df;
    }

    public static String formatDate(Date date) throws ParseException {
        return getDateFormat().format(date);
    }

    public static Date parse(String strDate) throws ParseException {
        return getDateFormat().parse(strDate);
    }

    public static String getCurrentTime(){
        return getDateFormat().format(new Date());
    }

    public static boolean lessThanNow(Date date){
        String now = getCurrentTime();
        String ds = getDateFormat().format(date);
        return (now.compareTo(ds) > 0);
    }

    public static int hourOfDay(Date date){
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        int hour = ca.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    public static boolean canReferalNow(){
        Date now = new Date();
        /**
         * 每天晚上10点-凌晨8点
         */
        if(hourOfDay(now)<8||hourOfDay(now)>=17){
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws ParseException {
        Date d = getDateFormat().parse("2015-08-09 19:04:00");
        System.out.println(lessThanNow(d));
    }
}
