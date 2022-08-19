package com.reactivespring.movieinfoservice.services;

import com.reactivespring.movieinfoservice.domain.MovieInfo;
import com.reactivespring.movieinfoservice.repository.MovieInfoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MovieService {
    private final MovieInfoRepository movieInfoRepository;

    public MovieService(MovieInfoRepository movieInfoRepository) {
        this.movieInfoRepository = movieInfoRepository;
    }

    public Mono<MovieInfo> saveMovie(MovieInfo movieInfo){
        return movieInfoRepository.save(movieInfo).log();
    }
}
