package com.university.university_events.core.utils;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public final class Formatter {
    private Formatter() {
    }

    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    public static String format(Date date) {
        return dateFormatter.format(date);
    }

    public static Date parse(String date) throws ParseException {
        return dateFormatter.parse(date);
    }
}