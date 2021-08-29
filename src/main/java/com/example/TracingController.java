package com.example;

import java.time.Duration;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;

@Controller
public class TracingController {
	private final RSocketRequester requester;

	public TracingController(RSocketRequester.Builder builder, @Value("${svc2.port:7001}") int port) {
		this.requester = builder.tcp("localhost", port);
	}

	@MessageMapping("tracing")
	public Flux<String> tracing() {
		final Mono<String> hello = this.requester.route("delay/100")
				.data("hello").retrieveMono(String.class);
		final Mono<String> world = this.requester.route("delay/300")
				.data("world").retrieveMono(String.class);
		final Mono<String> tracing = this.requester.route("delay/200")
				.data("tracing").retrieveMono(String.class);
		return hello
				.mergeWith(world)
				.mergeWith(tracing);
	}

	@MessageMapping("delay/{n}")
	public Mono<String> delay(@Payload String s, @DestinationVariable int n) {
		return Mono.delay(Duration.ofMillis(n)).map(__ -> s.toUpperCase());
	}
}
