package org.bahmni.module.bahmnicore.model;

import org.apache.commons.lang.StringEscapeUtils;

public class WildCardParameter {
    private final String[] parts;

    public WildCardParameter(String[] parts) {
        this.parts = parts;
    }

    public static WildCardParameter create(String value) {
        if(value == null || "".equals(value)){
            return new WildCardParameter(new String[0]);
        }
        String[] splitName = value.split(" ");
        for(int i=0;i<splitName.length ; i++){
            splitName[i] = "%" + StringEscapeUtils.escapeSql(splitName[i]) + "%";
        }
        return new WildCardParameter(splitName);
    }

    public String[] getParts() {
        return parts;
    }

    public boolean isEmpty() {
        return parts.length == 0;
    }
}
