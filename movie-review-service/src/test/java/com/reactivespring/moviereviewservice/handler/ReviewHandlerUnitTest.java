package com.reactivespring.moviereviewservice.handler;

import com.reactivespring.moviereviewservice.domain.Review;
import com.reactivespring.moviereviewservice.repository.ReviewRepository;
import com.reactivespring.moviereviewservice.router.ReviewRouter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {ReviewHandler.class, ReviewRouter.class})
@AutoConfigureWebTestClient
public class ReviewHandlerUnitTest {

    public static final String REVIEW_URL = "/v1/reviews";

    @MockBean
    private ReviewRepository reviewRepository;
    @Autowired
    private WebTestClient webTestClient;


    @Test
    void saveReview() {
        Review review = new Review(null, "1", "Awesome Movie", 9.0);
        when(reviewRepository.save(isA(Review.class)))
                .thenReturn(
                        Mono.just(
                                new Review("abc", "1", "Awesome Movie", 9.0)));
        webTestClient.post()
                .uri(REVIEW_URL)
                .bodyValue(
                        review)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewResult -> {
                    Review reviewSaved = reviewResult.getResponseBody();
                    assert reviewSaved != null;
                    assert reviewSaved.getId().equals("abc");
                });
    }

    @Test
    @Disabled
    void saveReviewValidation() {
        Review review = new Review(null, "", "Awesome Movie", -9.0);
        webTestClient.post()
                .uri(REVIEW_URL)
                .bodyValue(
                        review)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void getAllReviews() {

        List<Review> reviewsList = List.of(
                new Review("1", "1", "Awesome Movie", 9.0),
                new Review("2", "1", "Awesome Movie1", 9.0),
                new Review("3", "2", "Excellent Movie", 8.0));
        when(reviewRepository.findAll()).thenReturn(Flux.fromIterable(reviewsList));
        webTestClient.get()
                .uri(REVIEW_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .consumeWith( listEntityExchangeResult -> {
                    List<Review> reviews = listEntityExchangeResult.getResponseBody();
                    assert reviews != null;
                    assert reviews.size() == 3;
                });
    }

    @Test
    void getAllReviewsByMovieInfoId() {
        List<Review> reviewsList = List.of(
                new Review("1", "1", "Awesome Movie", 9.0),
                new Review("2", "1", "Awesome Movie1", 9.0));
        when(reviewRepository.findByMovieInfoId(eq("1")))
                .thenReturn(Flux.fromIterable(reviewsList));
        URI uri = UriComponentsBuilder
                .fromUriString(REVIEW_URL)
                .queryParam("movieInfoId", 1)
                .buildAndExpand()
                .toUri();

        webTestClient.get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .consumeWith( listEntityExchangeResult -> {
                    List<Review> reviews = listEntityExchangeResult.getResponseBody();
                    assert reviews != null;
                    assert reviews.size() == 2;
                });
    }

    @Test
    void updateReview() {

        String id = "1";
        String commentTest = "updateTest";
        when(reviewRepository.findById(id))
                .thenReturn(
                        Mono.just(
                                new Review(id, "1", "Awesome Movie", 9.0)));
        when(reviewRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(
                        new Review(id, "1", commentTest, 9.0)));
        webTestClient.put()
                .uri(REVIEW_URL + "/{id}",id)
                .bodyValue(
                        new Review(id, "1", commentTest, 9.0)
                )
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Review.class)
                .consumeWith( listEntityExchangeResult -> {
                    Review review = listEntityExchangeResult.getResponseBody();
                    assert review != null;
                    assert review.getComment().equals(commentTest);
                    assert review.getId().equals(id);
                });

    }

    @Test
    void deleteReview() {
        String id = "1";
        Review review = new Review(id, "1", "Awesome Movie", 9.0);
        when(reviewRepository.findById(id))
                .thenReturn(
                        Mono.just(review));
        when(reviewRepository.delete(eq(review)))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(REVIEW_URL + "/{id}",id)
                .exchange()
                .expectStatus()
                .isNoContent();

    }

}
