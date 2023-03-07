package com.example.tutornite.utils;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeFormatter {

    public static String convertTimestampToFormat(String desiredFormat, Timestamp sessionDateTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(desiredFormat, Locale.getDefault());
        Date date = sessionDateTime.toDate();
        return simpleDateFormat.format(date);
    }

}
