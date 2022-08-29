package com.reactivespring.moviereviewservice.repository;

import com.reactivespring.moviereviewservice.domain.Review;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ReviewRepository extends ReactiveMongoRepository<Review, String> {
}
