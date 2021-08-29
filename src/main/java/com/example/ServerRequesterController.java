package com.example;

import java.util.Scanner;

import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;

// Remove dependencies on Sleuth from pom.xml
@Controller
public class ServerRequesterController {
	@MessageMapping("requester")
	public Mono<String> demo(RSocketRequester requester) {
		final Mono<Payload> sendingPayload = console("Enter your name > ").map(DefaultPayload::create);
		requester.rsocketClient()
				.requestResponse(sendingPayload)
				.map(Payload::getDataUtf8)
				.doOnNext(s -> System.out.println("[Response From Client] > " + s))
				.log("requester")
				.subscribe();
		return Mono.just("I'll call you later.");
	}

	Mono<String> console(String prompt) {
		return Mono.fromCallable(() -> {
					System.out.print(prompt);
					return new Scanner(System.in).nextLine();
				})
				.subscribeOn(Schedulers.boundedElastic());
	}
}
