package com.gm.client.credentials.config;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@Configuration
@EnableConfigurationProperties
@EnableOAuth2Client
public class Oauth2ClientConfig {

	@Value("${security.oauth2.authorization.check-token-uri}")
	private String checkTokenUrl;

	@Bean
	public OAuth2RestTemplate oauth2RestTemplate(OAuth2ClientContext context,
			ClientCredentialsResourceDetails details) {
		OAuth2RestTemplate template = new OAuth2RestTemplate(details, context);

		ClientCredentialsAccessTokenProvider authCodeProvider = new ClientCredentialsAccessTokenProvider();
		AccessTokenProviderChain provider = new AccessTokenProviderChain(Arrays.asList(authCodeProvider));
		template.setAccessTokenProvider(provider);
		return template;
	}

	/**
	 * 注册处理redirect uri的filter
	 * 
	 * @param oauth2RestTemplate
	 * @param tokenService
	 * @return
	 */
	@Bean
	public OAuth2ClientAuthenticationProcessingFilter oauth2ClientAuthenticationProcessingFilter(
			OAuth2RestTemplate oauth2RestTemplate, RemoteTokenServices tokenService) {
		OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(
				"http://localhost:8082/callback");
		filter.setRestTemplate(oauth2RestTemplate);
		filter.setTokenServices(tokenService);

		/**
		 * 在client-credentials模式下回调成功不会进行处理
		 */
		// 设置回调成功的页面
		filter.setAuthenticationSuccessHandler(new SimpleUrlAuthenticationSuccessHandler() {
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				this.setDefaultTargetUrl("/login");
				super.onAuthenticationSuccess(request, response, authentication);
			}
		});
		// 设置回调失败异常处理
		filter.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {

			@Override
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException exception) throws IOException, ServletException {
				exception.printStackTrace();
			}
		});
		return filter;
	}

	/**
	 * 注册check token服务
	 * 
	 * @param details
	 * @return
	 */
	@Bean
	public RemoteTokenServices tokenService(ClientCredentialsResourceDetails details) {
		RemoteTokenServices tokenService = new RemoteTokenServices();
		tokenService.setCheckTokenEndpointUrl(checkTokenUrl);
		tokenService.setClientId(details.getClientId());
		tokenService.setClientSecret(details.getClientSecret());
		return tokenService;
	}

	@Bean
	@ConfigurationProperties(prefix = "security.oauth2.client-one")
	public ClientCredentialsResourceDetails clientCredentialsResourceDetails() {
		ClientCredentialsResourceDetails details = new ClientCredentialsResourceDetails();
		return details;
	}

}