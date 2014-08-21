package org.bahmni.module.bahmnicore.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDateSerializer {
    public static String serializeDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(date);
    }
}
