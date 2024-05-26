package com.lck.server.user.exception;

import lombok.Getter;

@Getter
public class UserValidationException extends RuntimeException{
	private final String error;
	public UserValidationException(String error){
		this.error = error;
	}
}
