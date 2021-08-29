package com.example;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.util.MimeTypeUtils;

import static io.rsocket.metadata.WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION;

@SpringBootTest("spring.rsocket.server.port=0")
class HelloControllerTest {
	@LocalRSocketServerPort
	int port;

	@Autowired
	RSocketRequester.Builder builder;

	@Test
	void hello() {
		final RSocketRequester requester = builder
				.rsocketStrategies(b -> b.encoder(new SimpleAuthenticationEncoder()))
				.tcp("localhost", port);
		final Mono<String> response = requester.route("hello")
				.metadata(new UsernamePasswordMetadata("jdoe", "rsocket"),
						MimeTypeUtils.parseMimeType(MESSAGE_RSOCKET_AUTHENTICATION.getString()))
				.data("World")
				.retrieveMono(String.class);
		StepVerifier.create(response)
				.expectNext("Hello World, jdoe!")
				.expectComplete()
				.verify();
	}
}