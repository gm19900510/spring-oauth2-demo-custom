package com.gm.resource.server.custom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import javax.sql.DataSource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import com.gm.resource.server.custom.exception.AuthExceptionEntryPoint;
import com.gm.resource.server.custom.exception.CustomAccessDeniedHandler;

/**
 * 资源服务器配置
 */

@Configuration
@EnableResourceServer
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourcesServerConfiguration extends ResourceServerConfigurerAdapter {

	private static final String RESOURCEID = "project_api_one";
	
	@Autowired
	private DataSource dataSource;

	@Autowired
	public CustomAccessDeniedHandler customAccessDeniedHandler;

	@Autowired
	public AuthExceptionEntryPoint authExceptionEntryPoint;

	@Autowired
	private LogoutSuccessHandler logoutSuccessHandler;

	@Bean
	public TokenStore tokenStore() {
		return new JdbcTokenStore(dataSource);
	}

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId(RESOURCEID).stateless(false);
		resources.tokenStore(tokenStore());
		resources.authenticationEntryPoint(authExceptionEntryPoint).accessDeniedHandler(customAccessDeniedHandler);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		/*http.authorizeRequests().antMatchers(HttpMethod.GET, "/**").access("#oauth2.hasScope('read')")
				.antMatchers(HttpMethod.POST, "/**").access("#oauth2.hasScope('write')")
				.antMatchers(HttpMethod.PATCH, "/**").access("#oauth2.hasScope('write')")
				.antMatchers(HttpMethod.PUT, "/**").access("#oauth2.hasScope('write')")
				.antMatchers(HttpMethod.DELETE, "/**").access("#oauth2.hasScope('write')");*/
		http.logout().logoutUrl("/logout")// 虚拟的登出地址
				.logoutSuccessHandler(logoutSuccessHandler)// 登出做的操作
				.and().authorizeRequests().antMatchers("/test/hello").permitAll().antMatchers("/test/**")
				.authenticated();
	}
}