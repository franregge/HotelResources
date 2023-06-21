package com.ontimize.hr.api.core.service.exception;

public class UserDoesNotExistException extends UserDataException {
    public UserDoesNotExistException(String message) {
        super(message);
    }
}
