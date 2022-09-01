package com.reactivespring.moviereviewservice.execptions;

import java.util.List;

public class ReviewDataException extends RuntimeException {

    private List<String> message;

    public ReviewDataException(List<String> message) {
        this.message = message;
    }
}
