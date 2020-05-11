package com.chen.fy.mymail.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    /**
     * 时间格式转化1( date 转为 年-月-日 )
     */
    public static String dateToDateString(Date date) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }

    /**
     * 时间格式转化1( date 转为 小时:分钟 )
     */
    public static String dateToTimeString(Date date) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
        return simpleDateFormat.format(date);
    }

    public static Date stringToDate(String s){
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            return ft.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
