package org.bahmni.module.referencedata.labconcepts.validator;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.APIException;

public class Validator {
    public void throwExceptionIfExists(StringBuilder errors) {
        String message = errors.toString();
        if (!StringUtils.isBlank(message)) {
            throw new APIException(message);
        }
    }
}
