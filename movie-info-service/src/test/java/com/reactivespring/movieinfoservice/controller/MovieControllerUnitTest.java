package com.reactivespring.movieinfoservice.controller;

import com.reactivespring.movieinfoservice.domain.MovieInfo;
import com.reactivespring.movieinfoservice.services.MovieService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebFluxTest(controllers = MovieController.class)
@AutoConfigureWebTestClient
public class MovieControllerUnitTest {
    public static final String MOVIEINFOS = "/v1/movieinfo";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MovieService movieService;

    @Test
    public void getAllMovies(){
        List<MovieInfo> movieList = List.of(
                new MovieInfo("1","testMovie",1994, List.of("testActor"), LocalDate.now()),
                new MovieInfo("2","testMovie2",2022, List.of("testActor2"), LocalDate.now())
        );
        Mockito.when(movieService.getAllMovies()).thenReturn(Flux.fromIterable(movieList));

        webTestClient.get()
                .uri(MOVIEINFOS)
                .exchange()
                .expectBodyList(MovieInfo.class)
                .hasSize(2);
    }

    @Test
    public void getMovieInfoById(){
        String id = "1";
        String movieName = "testMovie";
        MovieInfo movie =
                new MovieInfo(id,movieName,1994, List.of("testActor"), LocalDate.now());
        Mockito.when(movieService.getMovieById(Mockito.eq("1")))
                .thenReturn(Mono.just(movie));

        webTestClient.get()
                .uri(MOVIEINFOS + "/{id}", id)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    MovieInfo movieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(movieInfo);
                    assert movieInfo.getName().equals(movieName);
                });
    }

    @Test
    public void saveMovie(){
        String id = "testId";
        MovieInfo movieReturn =
                new MovieInfo(id,"testName",1994, List.of("testActor"), LocalDate.now());
        Mockito.when(movieService.saveMovie(Mockito.any(MovieInfo.class)))
                .thenReturn(Mono.just(movieReturn));
        webTestClient.post()
                .uri(MOVIEINFOS)
                .bodyValue(
                        new MovieInfo(null,"testName",1994, List.of("testActor"), LocalDate.now()))
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoResponse ->{
                    MovieInfo movieInfo = movieInfoResponse.getResponseBody();
                    assertNotNull(movieInfo);
                    assert movieInfo.getId().equals(id);
                } );

    }
}
