package com.reactivespring.movieservice.controller;

import com.reactivespring.movieservice.domain.Movie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/movies")
public class MoviesController {

    @GetMapping("/{movieId}")
    public Mono<Movie> retrieveMovieById(@PathVariable String movieId){
        return null;
    }
}
