package com.reactivespring.moviereviewservice.handler;

import com.reactivespring.moviereviewservice.domain.Review;
import com.reactivespring.moviereviewservice.execptions.ReviewDataException;
import com.reactivespring.moviereviewservice.execptions.ReviewNotFoundException;
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
                .doOnNext(this::validateFields)
                .flatMap(reviewRepository::save)
                .flatMap(review -> ServerResponse
                       .created(request
                               .uriBuilder()
                               .path("/{id}").build(review.getId()))
                       .bodyValue(review)
                );
    }

    private void validateFields(Review review) {
        String message = "Please verify the request and try again";
        Set<ConstraintViolation<Review>> errors = validator.validate(review);
        if (errors.size() > 0) {
            List<String> errorList = errors.stream()
                    .map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.toList());
            log.error(String.join(", ", errorList));
            throw new ReviewDataException(errorList, message);
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
                .switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found for the given id " + reviewId)))
                .flatMap(review -> serverRequest.bodyToMono(Review.class)
                        .map(reqReview -> {
                            review.setComment(reqReview.getComment());
                            review.setRating(reqReview.getRating());
                            validateFields(review);
                            return review;
                        }))
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
