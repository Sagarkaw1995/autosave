package com.cctns.autosave.producer.service.core.exception;

/**
 * Incorrect Draft Number
 */
public class InvalidDraftNumberFormat extends RuntimeException {
    public InvalidDraftNumberFormat(String message) {
        super(message);
    }
}
