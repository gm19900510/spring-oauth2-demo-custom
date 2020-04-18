package com.gm.client.authorization.code.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test")
public class TestController {

	@Value("${security.oauth2.resource.test-uri}")
	private String test_uri;

	@Autowired
	OAuth2RestTemplate oAuth2RestTemplate;

	@GetMapping("/query")
	public String getDemoAuthResource() {
		ResponseEntity<String> responseEntity = oAuth2RestTemplate.getForEntity(test_uri, String.class);
		return responseEntity.getBody();
	}

	@GetMapping("/index")
	public String index(HttpServletRequest request) {
		return "I am " + request.getServerPort();
	}

}