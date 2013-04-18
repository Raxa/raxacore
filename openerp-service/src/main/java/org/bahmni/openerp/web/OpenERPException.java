package org.bahmni.openerp.web;

public class OpenERPException extends RuntimeException {
    public OpenERPException(String message, Throwable cause) {
        super(message, cause);
    }

    public OpenERPException(Throwable cause) {
        super(cause);
    }

    public OpenERPException(String message) {
        super(message);
    }
}