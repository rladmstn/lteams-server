package com.lck.server.user.exception;

import lombok.Getter;

@Getter
public class UserPermissionException extends RuntimeException{
	private final String error;
	public UserPermissionException(String error){
		this.error = error;
	}
}
