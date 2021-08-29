package com.example;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;

@SpringBootTest("spring.rsocket.server.port=0")
class UpperCaseControllerTest {
	@LocalRSocketServerPort
	int port;

	@Autowired
	RSocketRequester.Builder builder;

	@Test
	void requestResponse() {
		final RSocketRequester requester = builder.tcp("localhost", port);
		final Mono<String> response = requester.route("uc")
				.data("Hello")
				.retrieveMono(String.class);

		StepVerifier.create(response)
				.expectNext("HELLO")
				.expectComplete()
				.verify();
	}
}