package com.reactivespring.moviereviewservice.handler;

import com.reactivespring.moviereviewservice.domain.Review;
import com.reactivespring.moviereviewservice.execptions.ReviewNotFoundException;
import com.reactivespring.moviereviewservice.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

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
                .flatMap(review -> ServerResponse
                       .created(request
                               .uriBuilder()
                               .path("/{id}").build(review.getId()))
                       .bodyValue(review)
                );
    }

    public Mono<ServerResponse> getAllReviews(ServerRequest request) {
        Optional<String> movieInfoId = request.queryParam("movieInfoId");
        Flux<Review> reviews;
        if (movieInfoId.isPresent()){
            reviews = reviewRepository.findByMovieInfoId(movieInfoId.get());
        }else {
            reviews = reviewRepository.findAll();
        }
        return ServerResponse.ok().body(reviews, Review.class);

    }

    public Mono<ServerResponse> updateReview(ServerRequest serverRequest) {
        String reviewId = serverRequest.pathVariable("id");
        return reviewRepository.findById(reviewId)
                .flatMap(reviewRequest -> serverRequest.bodyToMono(Review.class))
                .flatMap(reviewRepository::save)
                .flatMap(ServerResponse.ok()::bodyValue);

    }

    public Mono<ServerResponse> deleteReview(ServerRequest serverRequest) {
        String reviewId = serverRequest.pathVariable("id");
        return reviewRepository.findById(reviewId)
                .flatMap(reviewRepository::delete)
                .then(ServerResponse.noContent().build());
    }
}
