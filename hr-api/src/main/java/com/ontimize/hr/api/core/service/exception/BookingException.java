package com.ontimize.hr.api.core.service.exception;

public abstract class BookingException extends Exception {
    protected BookingException(String message) {
        super(message);
    }
}
