package com.reactivespring.movieservice.exceptions;

import org.springframework.http.HttpStatus;

public class ClientException extends RuntimeException{
    private final String message;
    private final HttpStatus httpStatus;


    public ClientException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
