package com.example;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

@Controller
public class HelloController {
	@MessageMapping("hello")
	public String hello(@AuthenticationPrincipal UserDetails user, @Payload String s) {
		return String.format("Hello %s, %s!", s, user.getUsername());
	}
}
