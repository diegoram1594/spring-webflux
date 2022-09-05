package com.reactivespring.movieservice.client;

import com.reactivespring.movieservice.domain.Review;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

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
                .bodyToFlux(Review.class);
    }



}
