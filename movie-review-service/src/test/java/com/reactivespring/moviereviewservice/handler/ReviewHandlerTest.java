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

import java.util.List;

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

}