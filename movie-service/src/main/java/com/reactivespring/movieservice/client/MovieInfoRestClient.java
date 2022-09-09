package com.reactivespring.movieservice.client;

import com.reactivespring.movieservice.domain.MovieInfo;
import com.reactivespring.movieservice.exceptions.ClientException;
import com.reactivespring.movieservice.exceptions.ServerException;
import com.reactivespring.movieservice.util.RetryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

@Service
@Slf4j
public class MovieInfoRestClient {

    private final WebClient webClient;
    @Value("${restClient.movieInfoUrl}")
    private String movieInfoUrl;

    public MovieInfoRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<MovieInfo> retrieveMovieInfo(String movieId){
        String url = movieInfoUrl.concat("/{id}");
        return webClient.get()
                .uri(url, movieId)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                    HttpStatus httpStatus = clientResponse.statusCode();
                    if (httpStatus.equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new ClientException("Movie not found for id " +
                                movieId, httpStatus));
                    }
                    return Mono.error(new ClientException("Error", httpStatus));
                })
                .onStatus(HttpStatus::is5xxServerError, clientResponse ->
                         Mono.error(new ServerException("Error server MovieInfo",
                                 clientResponse.statusCode()))
                )
                .bodyToMono(MovieInfo.class)
                .retryWhen(RetryUtil.retrySpec());
    }



}
