package com.reactivespring.moviereviewservice.handler;

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

import java.time.LocalDate;
import java.util.List;

@SpringBootTest()
@AutoConfigureWebTestClient
@DirtiesContext()
class ReviewHandlerTest {

    public static final String REVIEWI_URL = "/v1/reviews";
    @Autowired
    WebTestClient webTestClient;
    @Autowired
    ReviewRepository reviewRepository;

    @BeforeEach
    public void init(){
        List<Review> reviewsList = List.of(
                new Review(null, "1", "Awesome Movie", 9.0),
                new Review(null, "1", "Awesome Movie1", 9.0),
                new Review(null, "2", "Excellent Movie", 8.0));
        reviewRepository.saveAll(reviewsList).blockLast();
    }

    @AfterEach
    public void tearDown(){
        reviewRepository.deleteAll().block();
    }

    @Test
    void saveReview() {
        webTestClient.post()
                .uri(REVIEWI_URL)
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

}