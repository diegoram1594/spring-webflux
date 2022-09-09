package com.reactivespring.movieservice.util

import com.reactivespring.movieservice.exceptions.ServerException
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.util.retry.Retry
import reactor.util.retry.RetryBackoffSpec

import java.time.Duration

class RetryUtil {

    public static RetryBackoffSpec retrySpec() {
        return Retry
                .fixedDelay(3, Duration.ofSeconds(1))
                .filter(ex -> ex instanceof ServerException);
    }


}
