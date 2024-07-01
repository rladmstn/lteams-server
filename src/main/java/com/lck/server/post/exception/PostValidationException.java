package com.lck.server.post.exception;

import lombok.Getter;

@Getter
public class PostValidationException extends RuntimeException{
	private final String error;
	public PostValidationException(String error){
		this.error = error;
	}
}
