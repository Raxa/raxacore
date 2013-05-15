package org.bahmni.address;

public class AddressSanitiserException extends RuntimeException {
    public AddressSanitiserException(Throwable throwable) {
        super(throwable);
    }

    public AddressSanitiserException(String message) {
        super(message);
    }
}
