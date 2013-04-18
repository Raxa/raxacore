package org.bahmni.module.bahmnicore;

public class BahmniCoreException extends RuntimeException {
    public BahmniCoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public BahmniCoreException(Throwable cause) {
        super(cause);
    }

    public BahmniCoreException(String message) {
        super(message);
    }
}