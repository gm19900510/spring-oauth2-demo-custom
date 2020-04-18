package com.gm.client.sso.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test")
public class TestController {

	@GetMapping("/index")
	public String index(HttpServletRequest request) {
		return "I am " + request.getServerPort();
	}

}