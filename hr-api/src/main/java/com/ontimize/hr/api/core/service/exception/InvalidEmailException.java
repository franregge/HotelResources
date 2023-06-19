package com.ontimize.hr.api.core.service.exception;

public class InvalidEmailException extends Exception{
    public InvalidEmailException (String message){
        super(message);
    }
}
