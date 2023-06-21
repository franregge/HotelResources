package com.ontimize.hr.api.core.service.exception;

public class InvalidEmailException extends UserDataException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
