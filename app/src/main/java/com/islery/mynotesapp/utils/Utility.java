package com.islery.mynotesapp.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utility {
    public static String getFormatedDate(long timestamp, String pattern){
        Date date = new Date(timestamp);
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        return format.format(date);
    }
}
