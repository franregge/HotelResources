package com.ontimize.hr.api.core.service.exception;

public class UserDoesNotExistException extends Exception{
    public UserDoesNotExistException (String message){
        super(message);
   }
}
