package com.reactivespring.movieinfoservice.controller;

import com.reactivespring.movieinfoservice.domain.MovieInfo;
import com.reactivespring.movieinfoservice.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
@AutoConfigureWebTestClient
@DirtiesContext()
class MovieControllerTest {

    @Autowired
    private MovieInfoRepository movieInfoRepository;
    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    public void init(){
        List<MovieInfo> movieList = List.of(
                new MovieInfo("1","testMovie",1994, List.of("testActor"), LocalDate.now()),
                new MovieInfo("2","testMovie2",2022, List.of("testActor2"), LocalDate.now())
        );
        movieInfoRepository.saveAll(movieList).blockLast();
    }

    @AfterEach
    public void tearDown(){
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void saveMovie() {
        webTestClient.post()
                .uri("/v1/movieinfos")
                .bodyValue(
                        new MovieInfo("3","testMovie3",1994, List.of("testActor"), LocalDate.now()))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    MovieInfo movie = movieInfoEntityExchangeResult.getResponseBody();
                    assert movie != null;
                    assert movie.getId() != null;
                });

    }
}