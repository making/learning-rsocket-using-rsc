package com.example;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class UpperCaseController {
	@MessageMapping("uc")
	public String uppercase(@Payload String s) {
		return s.toUpperCase();
	}
}
