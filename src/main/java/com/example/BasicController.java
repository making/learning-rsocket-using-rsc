package com.example;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class BasicController {
	private final Logger log = LoggerFactory.getLogger(BasicController.class);

	@MessageMapping("request-response")
	public Mono<String> requestResponse(@Payload String text) {
		log.info("Request Response ({})", text);
		return Mono.just(text.toUpperCase()).log("requestResponse");
	}

	@MessageMapping("request-stream")
	public Flux<String> requestStream(@Payload String text) {
		log.info("Request Stream ({})", text);
		final AtomicLong counter = new AtomicLong(0);
		return Mono.fromCallable(() -> counter.getAndIncrement() + "\t" + text.toUpperCase())
				.repeat()
				.log("requestStream");
	}

	@MessageMapping("request-channel")
	public Flux<String> requestChannel(@Payload Flux<String> stream) {
		log.info("Request Channel");
		final AtomicLong counter = new AtomicLong(0);
		return stream.map(text -> counter.getAndIncrement() + "\t" + text.toUpperCase())
				.log("requestChannel");
	}

	@MessageMapping("fire-and-forget")
	public Mono<Void> fireAndForget() {
		log.info("Fire and Forget");
		return Mono.<Void>empty().log("fireAndForget");
	}
}
