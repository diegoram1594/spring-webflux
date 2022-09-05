package com.reactivespring.movieservice.client;

import com.reactivespring.movieservice.domain.MovieInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class MovieInfoRestClient {

    private final WebClient webClient;
    //@Value("${restClient.movieInfoUrl}")
    private String movieInfoUrl = "http://localhost:8080/v1/movieinfo";

    public MovieInfoRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<MovieInfo> retrieveMovieInfo(String movieId){
        String url = movieInfoUrl.concat("/{id}");
        return webClient.get()
                .uri(url, movieId)
                .retrieve()
                .bodyToMono(MovieInfo.class);
    }

}
