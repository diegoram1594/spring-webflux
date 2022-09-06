package com.reactivespring.movieservice.client;

import com.reactivespring.movieservice.domain.Review;
import com.reactivespring.movieservice.exceptions.ServerException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
public class ReviewRestClient {

    private final WebClient webClient;
    //@Value("${restClient.movieReviewUrl}")
    private String reviewUrl = "http://localhost:8081/v1/reviews";

    public ReviewRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Review> retrieveMovieInfo(String movieInfoId){
        URI uri = UriComponentsBuilder.fromHttpUrl(reviewUrl)
                .queryParam("movieInfoId", movieInfoId)
                .buildAndExpand().toUri();
        return webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                    HttpStatus httpStatus = clientResponse.statusCode();
                    if (httpStatus.equals(HttpStatus.NOT_FOUND)) {
                        return Mono.empty();
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(response -> Mono.error(new ServerException(response, httpStatus)));
                })
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(response -> Mono.error(new ServerException(response,
                                clientResponse.statusCode()))))
                .bodyToFlux(Review.class);
    }



}
