package org.bahmni.module.bahmnicore.web.v1_0;

import org.openmrs.api.APIException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Visit is closed.")
public class VisitClosedException extends APIException {
    public VisitClosedException(String message){
        super(message);
    }
}
