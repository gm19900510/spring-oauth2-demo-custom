package com.gm.authorization.server.custom.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Component;
import com.gm.authorization.server.custom.exception.CustomOauthException;

/**
 * 自定义错误实现类（如自定义异常需继承OAuth2Exception，才经此类处理）
 * 
 * @author Administrator
 *
 */
@Component("customWebResponseExceptionTranslator")
public class CustomWebResponseExceptionTranslator implements WebResponseExceptionTranslator {
	@Override
	public ResponseEntity<OAuth2Exception> translate(Exception exception) throws Exception {
		if (exception instanceof OAuth2Exception) {
			OAuth2Exception oAuth2Exception = (OAuth2Exception) exception;
			return ResponseEntity.status(oAuth2Exception.getHttpErrorCode())
					.body(new CustomOauthException(oAuth2Exception.getMessage()));
		} else if (exception instanceof AuthenticationException) {
			AuthenticationException authenticationException = (AuthenticationException) exception;
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new CustomOauthException(authenticationException.getMessage()));
		}
		return ResponseEntity.status(HttpStatus.OK).body(new CustomOauthException(exception.getMessage()));
	}

}
