package org.bahmni.module.bahmnicore.forms2.util;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class FormUtil {

    public static String getFormNameFromFieldPath(String formFieldPath) {
        return isNotBlank(formFieldPath) && formFieldPath.contains(".") ?
                formFieldPath.substring(0, formFieldPath.indexOf(".")) : "";
    }

    public static int getFormVersionFromFieldPath(String formFieldPath) {
        String formVersion = "";
        if (isNotBlank(formFieldPath) && formFieldPath.contains(".") && formFieldPath.contains("/")) {
            formVersion = formFieldPath.substring(formFieldPath.indexOf(".") + 1, formFieldPath.indexOf("/"));
        }
        return isNotBlank(formVersion) ? Integer.parseInt(formVersion) : 0;
    }
}

