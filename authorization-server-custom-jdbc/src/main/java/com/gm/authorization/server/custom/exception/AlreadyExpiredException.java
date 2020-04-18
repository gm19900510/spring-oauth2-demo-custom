package com.gm.authorization.server.custom.exception;

public class AlreadyExpiredException extends CustomOauthException {

	private static final long serialVersionUID = 1L;

	public AlreadyExpiredException(String message) {
		super(message);
	}
}
