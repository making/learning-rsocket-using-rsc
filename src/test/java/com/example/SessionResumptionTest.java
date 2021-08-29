package com.example;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import io.rsocket.core.Resume;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.ToxiproxyContainer;
import org.testcontainers.containers.ToxiproxyContainer.ContainerProxy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest("spring.rsocket.server.port=0")
@Testcontainers
class SessionResumptionTest {
	@LocalRSocketServerPort
	int port;

	@Autowired
	RSocketRequester.Builder builder;

	Network network = Network.newNetwork();

	@Container
	public ToxiproxyContainer toxiproxy = new ToxiproxyContainer("shopify/toxiproxy:2.1.4").withNetwork(network);

	Logger log = LoggerFactory.getLogger(SessionResumptionTest.class);

	@Test
	void resume() {
		Hooks.onErrorDropped(e -> {
		});
		final ContainerProxy proxy = toxiproxy.getProxy("host.docker.internal", port);
		final RSocketRequester requester = builder
				.rsocketConnector(c -> c.resume(new Resume())
						.keepAlive(Duration.ofSeconds(1), Duration.ofSeconds(3)))
				.tcp(proxy.getContainerIpAddress(), proxy.getProxyPort());
		final Flux<String> response = requester
				.route("request-stream")
				.data("hello")
				.retrieveFlux(String.class)
				.log("response")
				.limitRate(1)
				.take(6);
		StepVerifier.create(response)
				.expectNext("HELLO0")
				.expectNext("HELLO1")
				.expectNext("HELLO2")
				.consumeNextWith(s -> {
					assertThat(s).isEqualTo("HELLO3");
					disruptNetwork(proxy);
				})
				.expectNext("HELLO4")
				.expectNext("HELLO5")
				.expectComplete()
				.verify();
	}

	void disruptNetwork(ContainerProxy proxy) {
		log.warn("⚡️ Start connection cut ⚡️");
		proxy.setConnectionCut(true);
		final Thread thread = new Thread(() -> {
			try {
				TimeUnit.SECONDS.sleep(5);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			try {
				log.warn("⚡️ Stop connection cut ⚡️");
				proxy.setConnectionCut(false);
			}
			catch (RuntimeException ignored) {
			}
		});
		thread.setName("disrupt-network");
		thread.start();
	}
}
