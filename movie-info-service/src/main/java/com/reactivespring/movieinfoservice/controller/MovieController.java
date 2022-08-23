package com.reactivespring.movieinfoservice.controller;

import com.reactivespring.movieinfoservice.domain.MovieInfo;
import com.reactivespring.movieinfoservice.services.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
@Validated
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping("/movieinfo")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> saveMovie(@RequestBody @Valid MovieInfo movieInfo){
        return movieService.saveMovie(movieInfo);
    }

    @GetMapping("/movieinfo")
    public Flux<MovieInfo> getAllMovies(){
        return movieService.getAllMovies();
    }

    @GetMapping("/movieinfo/{id}")
    public Mono<MovieInfo> getMovieById(@PathVariable String id){
        return movieService.getMovieById(id);
    }

    @PutMapping("/movieinfo/{id}")
    public Mono<MovieInfo> updateMovieById(@RequestBody MovieInfo movieInfo,
                                           @PathVariable String id){
        return movieService.updateMovie(movieInfo, id);
    }

    @DeleteMapping("/movieinfo/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieById(@PathVariable String id){
        return movieService.deleteMovie(id);
    }
}
