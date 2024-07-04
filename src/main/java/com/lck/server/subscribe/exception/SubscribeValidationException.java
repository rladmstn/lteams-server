package com.lck.server.subscribe.exception;

import lombok.Getter;

@Getter
public class SubscribeValidationException extends RuntimeException{
	private final String error;

	public SubscribeValidationException(String error) {
		this.error = error;
	}
}
