package com.gm.authorization.server.custom.config;

import com.gm.authorization.server.custom.exception.VerificationCodeException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

	@Autowired
	UserDetailsService userDetailsService;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		if (authentication.getCredentials() == null) {
			this.logger.debug("Authentication failed: no credentials provided");
			throw new BadCredentialsException(this.messages
					.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		} else {
			String presentedPassword = authentication.getCredentials().toString();
			if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
				this.logger.debug("Authentication failed: password does not match stored value");
				throw new BadCredentialsException(this.messages
						.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
			}
		}
	}

	@Override
	protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {

		// 开启未发现登录用户异常
		super.setHideUserNotFoundExceptions(false);
		
		// 添加额外处理，如验证码等
		Object details = authentication.getDetails();
		if (details instanceof CustomWebAuthenticationDetails) {
			CustomWebAuthenticationDetails customWebAuthenticationDetails = (CustomWebAuthenticationDetails) details;
			if (!StringUtils.equalsIgnoreCase(customWebAuthenticationDetails.getInputVerificationCode(),
					customWebAuthenticationDetails.getSessionVerificationCode())) {
				throw new VerificationCodeException("验证码错误.");
			}
		}

		try {
			UserDetails loadedUser = userDetailsService.loadUserByUsername(username);
			if (loadedUser == null) {
				throw new InternalAuthenticationServiceException(
						"UserDetailsService returned null, which is an interface contract violation");
			}
			return loadedUser;
		} catch (UsernameNotFoundException ex) {
			throw new UsernameNotFoundException("用户 " + ex.getMessage() + " 不存在.");
		} catch (InternalAuthenticationServiceException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
		}
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return true;
	}

	/**
	 * 授权持久化.
	 */
	@Override
	protected Authentication createSuccessAuthentication(Object principal, Authentication authentication,
			UserDetails user) {
		return super.createSuccessAuthentication(principal, authentication, user);
	}
}
