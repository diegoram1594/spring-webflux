package com.reactivespring.movieservice.domain;

import java.util.List;

public class Movie {
    private MovieInfo movieInfo;
    private List<Review> reviews;

    public Movie() {
    }

    public Movie(MovieInfo movieInfo, List<Review> reviews) {
        this.movieInfo = movieInfo;
        this.reviews = reviews;
    }

    public MovieInfo getMovieInfo() {
        return movieInfo;
    }

    public void setMovieInfo(MovieInfo movieInfo) {
        this.movieInfo = movieInfo;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
