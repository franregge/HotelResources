package com.ontimize.hr.api.core.service.exception;

public abstract class UserDataException extends Exception {
    protected UserDataException(String message) {
        super(message);
    }
}
