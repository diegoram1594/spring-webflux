package com.reactivespring.moviereviewservice.exceptionhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactivespring.moviereviewservice.domain.ExceptionDTO;
import com.reactivespring.moviereviewservice.execptions.ReviewDataException;
import com.reactivespring.moviereviewservice.execptions.ReviewNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        DataBufferFactory buffer = exchange
                .getResponse()
                .bufferFactory();
        DataBuffer message = buffer.wrap(ex.getMessage().getBytes(StandardCharsets.UTF_8));

        if (ex instanceof ReviewDataException) {
            try {
                ExceptionDTO exception = new ExceptionDTO(ex.getMessage(), ((ReviewDataException) ex).getErrorList());
                message = buffer.wrap(objectMapper.writeValueAsBytes(exception));
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return exchange.getResponse().writeWith(Mono.just(message));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        if (ex instanceof ReviewNotFoundException) {
                exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
                return exchange.getResponse().writeWith(Mono.just(message));
        }


        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return exchange.getResponse().writeWith(Mono.just(message));
    }
}
