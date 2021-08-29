package com.example;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;

@SpringBootTest("spring.rsocket.server.port=0")
class BasicControllerTest {
	@LocalRSocketServerPort
	int port;

	@Autowired
	RSocketRequester.Builder builder;

	@Test
	void requestResponse() {
		final RSocketRequester requester = builder.tcp("localhost", port);
		final Mono<String> response = requester.route("request-response")
				.data("Hello")
				.retrieveMono(String.class);

		StepVerifier.create(response)
				.expectNext("HELLO")
				.expectComplete()
				.verify();
	}

	@Test
	void requestStream() {
		final RSocketRequester requester = builder.tcp("localhost", port);
		final Flux<String> response = requester.route("request-stream")
				.data("Hello")
				.retrieveFlux(String.class)
				.limitRate(1)
				.take(5);

		StepVerifier.create(response)
				.expectNext("0\tHELLO")
				.expectNext("1\tHELLO")
				.expectNext("2\tHELLO")
				.expectNext("3\tHELLO")
				.expectNext("4\tHELLO")
				.expectComplete()
				.verify();
	}

	@Test
	void requestChannel() {
		final RSocketRequester requester = builder.tcp("localhost", port);
		final Flux<String> response = requester.route("request-channel")
				.data(Flux.just("a", "b", "c"), String.class)
				.retrieveFlux(String.class);

		StepVerifier.create(response)
				.expectNext("0\tA")
				.expectNext("1\tB")
				.expectNext("2\tC")
				.expectComplete()
				.verify();
	}

	@Test
	void fireAndForget() {
		final RSocketRequester requester = builder.tcp("localhost", port);
		final Mono<Void> response = requester.route("fire-and-forget")
				.data("Hello")
				.retrieveMono(Void.class);

		StepVerifier.create(response)
				.expectComplete()
				.verify();
	}
}