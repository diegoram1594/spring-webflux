package com.reactivespring.movieservice.exceptions;

import com.reactivespring.movieservice.domain.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
@Slf4j
public class GlobalErrorHandler {

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<ExceptionResponse> handleClientException(ClientException exception) {
        log.error(exception.getMessage());
        return ResponseEntity.
                status(exception.getHttpStatus())
                .body(new ExceptionResponse(new Date(),exception.getHttpStatus().getReasonPhrase() ,exception.getMessage()));
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<ExceptionResponse> handleRuntimeException(ServerException exception) {
        log.error(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse(new Date(),HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        exception.getMessage()));
    }


}
