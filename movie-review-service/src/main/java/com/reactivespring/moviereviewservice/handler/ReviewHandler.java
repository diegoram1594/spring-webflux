package com.reactivespring.moviereviewservice.handler;

import com.reactivespring.moviereviewservice.domain.Review;
import com.reactivespring.moviereviewservice.execptions.ReviewDataException;
import com.reactivespring.moviereviewservice.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ReviewHandler {

    private final ReviewRepository reviewRepository;
    private final Validator validator;

    @Autowired
    public ReviewHandler(ReviewRepository reviewRepository, Validator validator) {
        this.reviewRepository = reviewRepository;
        this.validator = validator;
    }

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(reviewRepository::save)
                .flatMap(review -> ServerResponse
                       .created(request
                               .uriBuilder()
                               .path("/{id}").build(review.getId()))
                       .bodyValue(review)
                );
    }

    private void validate(Review review) {
        Set<ConstraintViolation<Review>> errors = validator.validate(review);
        if (errors.size() > 0) {
            List<String> errorsMessage = errors.stream()
                    .map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.toList());
            log.error(String.join(", ", errorsMessage));
            throw new ReviewDataException(errorsMessage);
        }
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
