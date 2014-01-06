package org.bahmni.module.openerpatomfeedclient.api.exception;

public class OpenERPFeedException extends RuntimeException {
    public OpenERPFeedException(String message, Exception e) {
        super(message, e);
    }
}
