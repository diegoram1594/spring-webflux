package com.reactivespring.moviereviewservice.execptions;

import java.util.List;

public class ReviewDataException extends RuntimeException {

    private List<String> errorList;
    private String message;

    public ReviewDataException(List<String> errorList, String message) {
        this.message = message;
        this.errorList = errorList;
    }

    public List<String> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
