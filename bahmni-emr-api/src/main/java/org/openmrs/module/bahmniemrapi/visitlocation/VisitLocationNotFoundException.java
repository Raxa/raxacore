package org.openmrs.module.bahmniemrapi.visitlocation;

import org.openmrs.api.APIException;

public class VisitLocationNotFoundException extends APIException {
    public VisitLocationNotFoundException(String message) {
        super(message);
    }
}