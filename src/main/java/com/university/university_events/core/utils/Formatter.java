package com.university.university_events.core.utils;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public final class Formatter {
    private Formatter() {
    }

    private static SimpleDateFormat dateFormatterWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    public static String formatWithTime(Date date) {
        return dateFormatterWithTime.format(date);
    }

    public static Date parseWithTime(String date) throws ParseException {
        return dateFormatterWithTime.parse(date);
    }

    public static String format(Date date) {
        return dateFormatter.format(date);
    }

    public static Date parse(String date) throws ParseException {
        return dateFormatter.parse(date);
    }
}