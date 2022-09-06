package com.reactivespring.movieservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;

@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureWireMock
class MovieServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
