package com.gm.authorization.server.custom.config;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import com.gm.authorization.server.custom.domain.GlobalConstant;

@Configuration
@EnableWebSecurity
// 对全部方法进行验证
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource;

	@Autowired
	CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

	@Autowired
	CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

	@Autowired
	DataSource dataSource;
	
	/**
     * 用来做remember me的处理
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        // 如果token表不存在，使用下面语句可以初始化该表；若存在，请注释掉这条语句，否则会报错。
        /*tokenRepository.setCreateTableOnStartup(true);*/
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers(GlobalConstant.LOGIN_PAGE, GlobalConstant.LOGIN_PROCESSING_URL, GlobalConstant.CAPTCHA_URL)
				.permitAll().antMatchers("/**").authenticated()
				// 表单登录
				.and().formLogin()
				// 绑定自定义图片验证码
				.authenticationDetailsSource(authenticationDetailsSource)
				// 登录失败处理
				.failureHandler(customAuthenticationFailureHandler)
				// 登录成功处理
				.successHandler(customAuthenticationSuccessHandler)
				// 登录处理
				.loginProcessingUrl(GlobalConstant.LOGIN_PROCESSING_URL)
				// 用户名、密码
				.usernameParameter("username").passwordParameter("password")
				// 登录页面
				.loginPage(GlobalConstant.LOGIN_PAGE)
				// 登出处理
				.and().logout().logoutRequestMatcher(new AntPathRequestMatcher(GlobalConstant.LOGOUT_URL))
				.and()
                // 开启 记住我功能，意味着 RememberMeAuthenticationFilter 将会 从Cookie 中获取token信息
                .rememberMe()
                // 设置 tokenRepository ，这里默认使用 jdbcTokenRepositoryImpl，意味着我们将从数据库中读取token所代表的用户信息
                .tokenRepository(persistentTokenRepository())
                // 设置  userDetailsService , 和 认证过程的一样，RememberMe 有专门的 RememberMeAuthenticationProvider ,也就意味着需要 使用UserDetailsService 加载 UserDetails 信息
                .userDetailsService(userDetailsService)
                // 设置 rememberMe 的有效时间，这里通过 配置来设置
                .tokenValiditySeconds(60)
		
		/*.and().userDetailsService(userDetailsService)*/
		/***
		 * .and().userDetailsService(userDetailsService)时，ProviderManager类中的
		 * getProviders()获取2个Provider（不使用rememberMe时），明细如下：
		 * 
		 * DaoAuthenticationProvider 从数据库中读取用户信息验证身份 
		 * AnonymousAuthenticationProvider 匿名用户身份认证
		 * 
		 * 此时自定义的Provider在用户信息验证身份成功后不会被执行到.因此类似于像自定义图片验证码等的验证不会执行到.
		 * 
		 * 
		 */
                ;
	}
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	/*
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
	    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
	    provider.setHideUserNotFoundExceptions(false);
	    provider.setUserDetailsService(userDetailsService);
	    provider.setPasswordEncoder(passwordEncoder);
	    return provider;
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
	    auth.authenticationProvider(authenticationProvider());
	}*/

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/webjars/**", "/resources/**", "/assets/**");
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

}
