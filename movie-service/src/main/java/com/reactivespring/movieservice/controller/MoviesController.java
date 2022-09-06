package com.reactivespring.movieservice.controller;

import com.reactivespring.movieservice.client.MovieInfoRestClient;
import com.reactivespring.movieservice.client.ReviewRestClient;
import com.reactivespring.movieservice.domain.Movie;
import com.reactivespring.movieservice.domain.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/v1/movies")
public class MoviesController {

    private final MovieInfoRestClient movieInfoRestClient;
    private final ReviewRestClient reviewRestClient;
    @Autowired
    public MoviesController(MovieInfoRestClient movieInfoRestClient, ReviewRestClient reviewRestClient) {
        this.movieInfoRestClient = movieInfoRestClient;
        this.reviewRestClient = reviewRestClient;
    }

    @GetMapping("/{movieId}")
    public Mono<Movie> retrieveMovieById(@PathVariable String movieId){
        return movieInfoRestClient.retrieveMovieInfo(movieId)
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewList = reviewRestClient.retrieveMovieInfo(movieId).collectList();
                    return reviewList.flatMap(reviews -> Mono.just(new Movie(movieInfo, reviews)));
                });
    }
}
