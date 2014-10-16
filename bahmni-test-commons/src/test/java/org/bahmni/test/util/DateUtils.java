package org.bahmni.test.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static final String DATE_PATTERN = "yyyy-M-d";

    public static Date getDate(String date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);
        simpleDateFormat.setLenient(false);
        return simpleDateFormat.parse(date);
    }

}
