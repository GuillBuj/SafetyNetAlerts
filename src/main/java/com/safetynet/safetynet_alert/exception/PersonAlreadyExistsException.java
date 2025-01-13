package com.safetynet.safetynet_alert.exception;

public class PersonAlreadyExistsException extends RuntimeException{

    public PersonAlreadyExistsException(String message){
        super(message);
    }
}
