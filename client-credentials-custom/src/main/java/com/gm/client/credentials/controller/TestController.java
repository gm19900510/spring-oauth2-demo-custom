package com.gm.client.credentials.controller;

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

	@Autowired
	OAuth2RestTemplate oAuth2RestTemplate;

	@Value("${security.oauth2.resource.test-uri}")
	private String test_uri;
	
	@GetMapping("/query")
	public String getDemoAuthResource() {
		ResponseEntity<String> responseEntity = oAuth2RestTemplate.getForEntity(test_uri, String.class);
		return responseEntity.getBody();
	}
	
}