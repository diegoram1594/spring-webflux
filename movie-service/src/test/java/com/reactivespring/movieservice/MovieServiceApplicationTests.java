package com.reactivespring.movieservice;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.reactivespring.movieservice.domain.ExceptionResponse;
import com.reactivespring.movieservice.domain.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 8084)
@TestPropertySource(properties = {
		"restClient.movieInfoUrl: http://localhost:8084/v1/movieinfo",
		"restClient.movieReviewUrl: http://localhost:8084/v1/reviews"
})
class MovieServiceApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	@BeforeEach()
	void resetWiremock(){
		WireMock.resetAllRequests();
	}

	@Test
	void retrieveMovieById() {
		String movieId = "1";
		stubFor(get(urlEqualTo("/v1/movieinfo/" + movieId))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBodyFile("MovieInfoSuccess.json")
				));

		stubFor(get(urlPathEqualTo("/v1/reviews"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBodyFile("ReviewsSuccess.json")
				));

		webTestClient
				.get()
				.uri("/v1/movies/" + movieId)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody(Movie.class)
				.consumeWith(movieRequest ->{
					Movie movie = movieRequest.getResponseBody();
					assert movie != null;
					assertEquals("Hello", movie.getMovieInfo().getName());
					assert movie.getReviews() != null;
					assertEquals(4, movie.getReviews().size());
				});
	}

	@Test
	void shouldSRetrieveError404WhenMovieInfoNotFound() {
		String movieId = "1";
		stubFor(get(urlEqualTo("/v1/movieinfo/" + movieId))
				.willReturn(aResponse()
						.withStatus(HttpStatus.NOT_FOUND.value())
				));

		webTestClient
				.get()
				.uri("/v1/movies/"+ movieId)
				.exchange()
				.expectStatus()
				.isNotFound()
				.expectBody(ExceptionResponse.class)
				.consumeWith(response -> {
					ExceptionResponse responseBody = response.getResponseBody();
					assertNotNull(responseBody);
					assertEquals("Movie not found for id " + movieId, responseBody.getDetails());
				});
	}

	@Test
	void shouldRetrieveMovieWhenMovieInfoIsWorkingButReviewsAreNotFound() {
		String movieId = "1";
		stubFor(get(urlEqualTo("/v1/movieinfo/" + movieId))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBodyFile("MovieInfoSuccess.json")
				));

		stubFor(get(urlPathEqualTo("/v1/reviews"))
				.willReturn(aResponse()
						.withStatus(HttpStatus.NOT_FOUND.value())
				));

		webTestClient
				.get()
				.uri("/v1/movies/" + movieId)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody(Movie.class)
				.consumeWith(movieRequest ->{
					Movie movie = movieRequest.getResponseBody();
					assert movie != null;
					assertEquals("Hello", movie.getMovieInfo().getName());
					assert movie.getReviews() != null;
					assertEquals(0, movie.getReviews().size());
				});
	}

	@Test
	void shouldSRetrieveError500WhenMovieInfoReturnsError500() {
		String movieId = "1";
		stubFor(get(urlEqualTo("/v1/movieinfo/" + movieId))
				.willReturn(aResponse()
						.withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
				));

		webTestClient
				.get()
				.uri("/v1/movies/"+ movieId)
				.exchange()
				.expectStatus()
				.is5xxServerError()
				.expectBody(ExceptionResponse.class)
				.consumeWith(response -> {
					ExceptionResponse responseBody = response.getResponseBody();
					assertNotNull(responseBody);
					assertEquals("Error server MovieInfo", responseBody.getDetails());
				});

		WireMock.verify(4, getRequestedFor(urlEqualTo("/v1/movieinfo/" + movieId)));
	}
	@Test
	void shouldSNotRetryOn404() {
		String movieId = "1";
		stubFor(get(urlEqualTo("/v1/movieinfo/" + movieId))
				.willReturn(aResponse()
						.withStatus(HttpStatus.NOT_FOUND.value())
				));

		webTestClient
				.get()
				.uri("/v1/movies/"+ movieId)
				.exchange()
				.expectStatus()
				.is4xxClientError()
				.expectBody(ExceptionResponse.class)
				.consumeWith(response -> {
					ExceptionResponse responseBody = response.getResponseBody();
					assertNotNull(responseBody);
				});

		WireMock.verify(1, getRequestedFor(urlEqualTo("/v1/movieinfo/" + movieId)));
	}

	@Test
	void shouldRetieReviewsOn500() {
		String movieId = "1";
		stubFor(get(urlEqualTo("/v1/movieinfo/" + movieId))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBodyFile("MovieInfoSuccess.json")
				));

		stubFor(get(urlPathEqualTo("/v1/reviews"))
				.willReturn(aResponse()
						.withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
				));

		webTestClient
				.get()
				.uri("/v1/movies/" + movieId)
				.exchange()
				.expectStatus()
				.is5xxServerError()
				.expectBody(ExceptionResponse.class)
				.consumeWith(movieRequest ->{
					ExceptionResponse exception = movieRequest.getResponseBody();
					assertNotNull(exception);
				});

		WireMock.verify(4, getRequestedFor(urlEqualTo("/v1/reviews?movieInfoId=" + movieId)));
	}

}
