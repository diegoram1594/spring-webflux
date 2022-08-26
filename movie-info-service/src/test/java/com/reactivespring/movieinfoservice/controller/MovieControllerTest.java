package com.reactivespring.movieinfoservice.controller;

import com.reactivespring.movieinfoservice.domain.MovieInfo;
import com.reactivespring.movieinfoservice.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;

@SpringBootTest()
@AutoConfigureWebTestClient
@DirtiesContext()
class MovieControllerTest {

    public static final String MOVIEINFOS = "/v1/movieinfo";
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
                .uri(MOVIEINFOS)
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

    @Test
    public void getAllMovies(){
        webTestClient.get()
                .uri(MOVIEINFOS)
                .exchange()
                .expectBodyList(MovieInfo.class)
                .hasSize(2);
    }

    @Test
    public void getAllMoviesByYear(){
        URI uri = UriComponentsBuilder
                .fromUriString(MOVIEINFOS)
                .queryParam("year", 1994)
                .buildAndExpand()
                .toUri();


        webTestClient.get()
                .uri(uri)
                .exchange()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }

    @Test
    public void getAllMoviesByName(){
        URI uri = UriComponentsBuilder
                .fromUriString(MOVIEINFOS)
                .queryParam("name", "testMovie")
                .buildAndExpand()
                .toUri();


        webTestClient.get()
                .uri(uri)
                .exchange()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }

    @Test
    public void getMovieById(){
        String id = "1";
        webTestClient.get()
                .uri(MOVIEINFOS + "/{id}", id)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    MovieInfo movie = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(movie);
                    assert movie.getName().equals("testMovie");
                });

    }

    @Test
    public void getMovieByIdNotFound(){
        String id = "NotFound";
        webTestClient.get()
                .uri(MOVIEINFOS + "/{id}", id)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void updateMovieById(){
        String id = "1";
        webTestClient.put()
                .uri(MOVIEINFOS + "/{id}", id)
                .bodyValue( new MovieInfo(
                        "1",
                        "updateName",
                        1994,
                        List.of("testActor"),
                        LocalDate.now()))
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    MovieInfo movie = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(movie);
                    assert movie.getName().equals("updateName");
                });

    }

    @Test
    public void updateMovieByIdNotFound(){
        String id = "notFound";
        webTestClient.put()
                .uri(MOVIEINFOS + "/{id}", id)
                .bodyValue( new MovieInfo(
                        "1",
                        "updateName",
                        1994,
                        List.of("testActor"),
                        LocalDate.now()))
                .exchange()
                .expectStatus()
                .isNotFound();

    }

    @Test
    public void deleteMovieById(){
        String id = "1";
        webTestClient.delete()
                .uri(MOVIEINFOS + "/{id}", id)
                .exchange()
                .expectStatus()
                .isNoContent();

        webTestClient.get()
                .uri(MOVIEINFOS)
                .exchange()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }

    @Test
    public void findByYear(){
        Flux<MovieInfo> movie = movieInfoRepository.findByYear(1994);

        StepVerifier.create(movie)
                .expectNextCount(1)
                .verifyComplete();
    }
}