package com.reactivespring.moviereviewservice.handler;

import com.reactivespring.moviereviewservice.domain.Review;
import com.reactivespring.moviereviewservice.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ReviewHandler {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewHandler(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .flatMap(reviewRepository::save)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }
}
