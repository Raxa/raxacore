package org.bahmni.jss.registration;

import org.apache.commons.lang.WordUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.StringTokenizer;

public class RegistrationFields {
    private static String patternWhenYearSpecifiedAs4Digits = "dd/MM/yyyy";
    private static String patternWhenYearSpecifiedAs2Digits = "dd/MM/yy";

    public static String getDate(String s) {
        StringTokenizer stringTokenizer = new StringTokenizer(s.trim(), " ");
        String datePart = stringTokenizer.nextToken();
        String pattern = datePart.length() == 8 ? patternWhenYearSpecifiedAs2Digits : patternWhenYearSpecifiedAs4Digits;
        LocalDateTime localDateTime = LocalDateTime.parse(datePart, DateTimeFormat.forPattern(pattern));
        return localDateTime.toString("dd-MM-yyyy");
    }

    public static String sentenceCase(String s) {
        return WordUtils.capitalizeFully(s);
    }

    public static RegistrationNumber parseRegistrationNumber(String registrationNumber) {
        StringTokenizer stringTokenizer = new StringTokenizer(registrationNumber, "/");
        String id = stringTokenizer.nextToken();
        String centerCode = stringTokenizer.nextToken();
        return new RegistrationNumber(centerCode, id);
    }
}