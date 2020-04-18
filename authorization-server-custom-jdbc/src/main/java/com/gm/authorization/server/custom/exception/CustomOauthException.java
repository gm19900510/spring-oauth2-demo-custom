package com.gm.authorization.server.custom.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * 自定义OAuth2异常
 * 
 * @author Administrator
 *
 */
@JsonSerialize(using = CustomOauthExceptionSerializer.class)
public class CustomOauthException extends OAuth2Exception {

	private static final long serialVersionUID = 1L;

	public CustomOauthException(String msg) {
        super(msg);
    }
}
