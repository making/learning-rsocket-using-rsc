package com.example;

import java.util.function.Function;

import io.rsocket.core.Resume;

import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RSocketConfig {

	@Bean
	public RSocketServerCustomizer rSocketServerCustomizer() {
		return customizer -> customizer
				.resume(new Resume());
	}

	@Bean
	public Function<String, String> uppercase() {
		return s -> s.toUpperCase();
	}
}
