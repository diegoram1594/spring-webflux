package com.reactivespring.movieinfoservice.exceptions;

import com.reactivespring.movieinfoservice.domain.ExceptionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ExceptionGlobalHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ExceptionDTO> handleRequestException(WebExchangeBindException ex) {
        String message = "Please verify the request and try again";
        log.error("Exception caught in handleRequestException : {}", ex.getMessage());
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionDTO(message, errors));

    }
}
