package com.reactivespring.moviereviewservice.domain;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Review {
    @Id
    private String id;
    private String movieInfoId;
    private String comment;
    private Double rating;

    public Review() {
    }

    public Review(String id, String movieInfoId, String comment, Double rating) {
        this.id = id;
        this.movieInfoId = movieInfoId;
        this.comment = comment;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieInfoId() {
        return movieInfoId;
    }

    public void setMovieInfoId(String movieInfoId) {
        this.movieInfoId = movieInfoId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewId='" + id + '\'' +
                ", movieInfoId='" + movieInfoId + '\'' +
                ", comment='" + comment + '\'' +
                ", rating=" + rating +
                '}';
    }
}
