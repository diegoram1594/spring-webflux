package com.reactivespring.moviereviewservice.domain;

import java.util.List;

public class ExceptionDTO {
    public String message;
    public List<String> errors;

    public ExceptionDTO() {
    }

    public ExceptionDTO(String message, List<String> errors) {
        this.message = message;
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
