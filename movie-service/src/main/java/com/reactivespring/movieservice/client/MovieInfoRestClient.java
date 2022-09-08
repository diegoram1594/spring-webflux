package com.reactivespring.movieservice.client;

import com.reactivespring.movieservice.domain.MovieInfo;
import com.reactivespring.movieservice.exceptions.ClientException;
import com.reactivespring.movieservice.exceptions.ServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(response -> Mono.error(new ServerException(response, httpStatus)));
                })
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(response -> Mono.error(new ServerException(response,
                                    clientResponse.statusCode())));
                })
                .bodyToMono(MovieInfo.class);
    }

}
