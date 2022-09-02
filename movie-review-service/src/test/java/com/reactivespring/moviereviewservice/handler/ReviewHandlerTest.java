package com.reactivespring.moviereviewservice.handler;

import com.reactivespring.moviereviewservice.domain.ExceptionDTO;
import com.reactivespring.moviereviewservice.domain.Review;
import com.reactivespring.moviereviewservice.repository.ReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest()
@AutoConfigureWebTestClient
@DirtiesContext()
class ReviewHandlerTest {

    public static final String REVIEW_URL = "/v1/reviews";
    @Autowired
    WebTestClient webTestClient;
    @Autowired
    ReviewRepository reviewRepository;

    @BeforeEach
    public void init(){
        List<Review> reviewsList = List.of(
                new Review("1", "1", "Awesome Movie", 9.0),
                new Review("2", "1", "Awesome Movie1", 9.0),
                new Review("3", "2", "Excellent Movie", 8.0));
        reviewRepository.saveAll(reviewsList).blockLast();
    }

    @AfterEach
    public void tearDown(){
        reviewRepository.deleteAll().block();
    }

    @Test
    void saveReview() {
        webTestClient.post()
                .uri(REVIEW_URL)
                .bodyValue(
                        new Review(null, "1", "Awesome Movie", 9.0))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewResult -> {
                    Review review = reviewResult.getResponseBody();
                    assert review != null;
                    assert review.getId() != null;
                });
    }

    @Test
    void getAllReviews() {
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
    void updateReviewNotFound() {
        String id = "notFound";
        String commentTest = "updateTest";
        webTestClient.put()
                .uri(REVIEW_URL + "/{id}",id)
                .bodyValue(
                        new Review(id, "1", commentTest, 9.0)
                )
                .exchange()
                .expectStatus()
                .isNotFound();

    }

    @Test
    void updateReviewBadRequest() {
        String id = "1";
        String commentTest = "comment test";
        webTestClient.put()
                .uri(REVIEW_URL + "/{id}",id)
                .bodyValue(
                        new Review(id, "1", commentTest, -9.0)
                )
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(ExceptionDTO.class)
                .consumeWith(response ->{
                    ExceptionDTO responseDTO = response.getResponseBody();
                    assert responseDTO != null;
                    assertEquals(1, responseDTO.getErrors().size());
                });

    }

    @Test
    void deleteReview() {
        String id = "1";
        String commentTest = "updateTest";
        webTestClient.delete()
                .uri(REVIEW_URL + "/{id}",id)
                .exchange()
                .expectStatus()
                .isNoContent();

    }

}