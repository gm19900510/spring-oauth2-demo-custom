package com.gm.client.authorization.code.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableOAuth2Client
public class Oauth2ClientConfig {

	@Value("${security.oauth2.client-two.id}")
	private String id;

	@Value("${security.oauth2.client-two.access-token-uri}")
	private String accessTokenUri;

	@Value("${security.oauth2.client-two.client-id}")
	private String clientId;

	@Value("${security.oauth2.client-two.client-secret}")
	private String clientSecret;

	@Value("#{'${security.oauth2.client-two.scope}'.split(',')}")
	private List<String> scope;

	@Value("${security.oauth2.client-two.user-authorization-uri}")
	private String userAuthorizationUri;

	@Value("${security.oauth2.client-two.pre-established-redirect-uri}")
	private String preEstablishedRedirectUri;

	@Value("${security.oauth2.authorization.check-token-uri}")
	private String checkTokenUrl;

	@Autowired
	OAuth2ClientContext oAuth2ClientContext;

	@Bean
	public AuthorizationCodeResourceDetails authorizationCodeResourceDetails() {
		AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
		details.setId(id);
		details.setClientId(clientId);
		details.setClientSecret(clientSecret);
		details.setAccessTokenUri(accessTokenUri);
		details.setUserAuthorizationUri(userAuthorizationUri);
		details.setScope(scope);
		return details;
	}

	/**
	 * 注册check token服务
	 * 
	 * @param details
	 * @return
	 */
	@Primary
	@Bean
	public RemoteTokenServices tokenService(AuthorizationCodeResourceDetails details) {
		RemoteTokenServices tokenService = new RemoteTokenServices();
		tokenService.setCheckTokenEndpointUrl(checkTokenUrl);
		tokenService.setClientId(clientId);
		tokenService.setClientSecret(clientSecret);
		return tokenService;
	}

	/**
	 * 注册处理redirect uri的filter
	 *
	 * @return
	 */
	@Bean
	public OAuth2ClientAuthenticationProcessingFilter oauth2ClientAuthenticationProcessingFilter(
			OAuth2RestTemplate oauth2RestTemplate, RemoteTokenServices tokenService) {
		OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(
				preEstablishedRedirectUri);

		/*
		 * RemoteTokenServices tokenService = new RemoteTokenServices();
		 * tokenService.setCheckTokenEndpointUrl(checkTokenUrl);
		 * tokenService.setClientId(clientId);
		 * tokenService.setClientSecret(clientSecret);
		 */
		tokenService.setRestTemplate(oauth2RestTemplate);

		filter.setRestTemplate(oauth2RestTemplate);
		filter.setTokenServices(tokenService);

		// 设置回调成功的页面
		/*filter.setAuthenticationSuccessHandler(new SimpleUrlAuthenticationSuccessHandler() {
			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				this.setDefaultTargetUrl("/user");
				super.onAuthenticationSuccess(request, response, authentication);
			}
		});*/
		// 设置回调失败处理
		filter.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
			@Override
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException exception) throws IOException, ServletException {
				exception.printStackTrace();
			}

		});

		return filter;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(filter);
		registration.setOrder(-100);
		return registration;
	}

	@Bean
	public OAuth2RestTemplate oauth2RestTemplate(OAuth2ClientContext context, OAuth2ProtectedResourceDetails details) {
		OAuth2RestTemplate template = new OAuth2RestTemplate(details, context);

		AuthorizationCodeAccessTokenProvider authCodeProvider = new AuthorizationCodeAccessTokenProvider();
		authCodeProvider.setStateMandatory(false);
		AccessTokenProviderChain provider = new AccessTokenProviderChain(Arrays.asList(authCodeProvider));
		template.setAccessTokenProvider(provider);
		return template;
	}

}
