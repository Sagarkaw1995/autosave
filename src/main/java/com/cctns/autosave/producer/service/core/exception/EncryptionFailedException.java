package com.cctns.autosave.producer.service.core.exception;

public class EncryptionFailedException extends RuntimeException {
    public EncryptionFailedException(String message) {
        super(message);
    }
}
