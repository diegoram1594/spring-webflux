package com.reactivespring.moviereviewservice.execptions;

public class ReviewNotFoundException extends RuntimeException{

    private String message;
    private Throwable ex;

    public ReviewNotFoundException(String message, Throwable ex) {
        super(message);
        this.message = message;
        this.ex = ex;
    }

    public ReviewNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
