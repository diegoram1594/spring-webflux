package com.reactivespring.movieinfoservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.reactivespring.movieinfoservice.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

@DataMongoTest
@DirtiesContext()
class MovieInfoRepositoryTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

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
    public void findAll(){
        Flux<MovieInfo> movies = movieInfoRepository.findAll().log();

        StepVerifier.create(movies)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    public void findById(){
        Mono<MovieInfo> movie = movieInfoRepository.findById("1").log();

        StepVerifier.create(movie)
                .assertNext(movieInfo -> assertEquals(movieInfo.getName(),"testMovie" ))
                .verifyComplete();
    }

    @Test
    public void save(){

        MovieInfo movieInfo = new MovieInfo(null, "testMovie3", 1994, List.of("testActor"), LocalDate.now());
        Mono<MovieInfo> movie = movieInfoRepository.save(movieInfo).log();

        StepVerifier.create(movie)
                .assertNext(m -> {
                    assertNotNull(m);
                    assertEquals(m.getName(),"testMovie3");
                })
                .verifyComplete();
    }

    @Test
    public void update(){

        MovieInfo movie = movieInfoRepository.findById("1").block();
        movie.setYear(2023);
        Mono<MovieInfo> movieSaved = movieInfoRepository.save(movie).log();
        StepVerifier.create(movieSaved)
                .assertNext(m -> assertEquals(m.getYear(),2023))
                .verifyComplete();
    }

    @Test
    public void delete(){
        movieInfoRepository.deleteById("1").block();
        Flux<MovieInfo> movies = movieInfoRepository.findAll();
        StepVerifier.create(movies)
                .expectNextCount(1)
                .verifyComplete();
    }

}