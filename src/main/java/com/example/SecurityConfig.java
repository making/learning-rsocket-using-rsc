package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;

@Configuration
@EnableRSocketSecurity
public class SecurityConfig {
	@Bean
	public RSocketMessageHandler messageHandler(RSocketStrategies strategies) {
		final RSocketMessageHandler mh = new RSocketMessageHandler();
		mh.getArgumentResolverConfigurer().addCustomResolver(new AuthenticationPrincipalArgumentResolver());
		mh.setRSocketStrategies(strategies);
		return mh;
	}

	@Bean
	public ReactiveUserDetailsService userDetailsService() {
		final UserDetails user = User
				.withUsername("jdoe")
				.password("{noop}rsocket")
				.roles("USER")
				.build();
		return new MapReactiveUserDetailsService(user);
	}

	@Bean
	public PayloadSocketAcceptorInterceptor authorization(RSocketSecurity rsocket) {
		return rsocket
				.authorizePayload(authorize -> authorize
						.route("hello").authenticated()
						.anyExchange().permitAll())
				.simpleAuthentication(Customizer.withDefaults())
				.build();
	}
}
