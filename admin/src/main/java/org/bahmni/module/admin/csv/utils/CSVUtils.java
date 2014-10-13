package org.bahmni.module.admin.csv.utils;

import org.bahmni.csv.KeyValue;
import org.openmrs.ConceptName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CSVUtils {

    public static String[] getStringArray(List<KeyValue> keyValueList) {
        List<String> stringList = new ArrayList<>();
        for (KeyValue keyValue : keyValueList) {
            stringList.add(keyValue.getValue());
        }
        return stringList.toArray(new String[]{});
    }

}
