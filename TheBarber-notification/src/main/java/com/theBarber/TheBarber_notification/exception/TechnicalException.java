package com.theBarber.TheBarber_notification.exception;

public class TechnicalException extends  RuntimeException{

    public TechnicalException(String message, Exception e){
        super(message);
    }
}
