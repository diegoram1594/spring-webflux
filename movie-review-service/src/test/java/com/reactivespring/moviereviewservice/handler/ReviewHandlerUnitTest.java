package com.reactivespring.moviereviewservice.handler;

import com.reactivespring.moviereviewservice.repository.ReviewRepository;
import com.reactivespring.moviereviewservice.router.ReviewRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest
@ContextConfiguration(classes = {ReviewHandler.class, ReviewRouter.class})
@AutoConfigureWebTestClient
public class ReviewHandlerUnitTest {

    @MockBean
    private ReviewRepository reviewRepository;
    @Autowired
    private WebTestClient webTestClient;

}
