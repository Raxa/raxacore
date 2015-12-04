package org.openmrs.module.bahmniemrapi.encountertransaction.utils;

import org.joda.time.DateTime;

import java.util.Date;

public class DateUtil {
    public static Boolean isBefore(Date date1, Date date2) {
        return new DateTime(date1).toDateMidnight().isBefore(new DateTime(date2).toDateMidnight());
    }

}
