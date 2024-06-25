package com.lck.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.lck.server.user.exception.UserPermissionException;
import com.lck.server.user.exception.UserValidationException;

@ControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler(RequestException.class)
	protected ResponseEntity<Object> handler(RequestException e){
		return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getError(), e.getMessages()));
	}
	@ExceptionHandler(UserValidationException.class)
	protected ResponseEntity<Object> handler(UserValidationException e){
		return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getError(), null));
	}
	@ExceptionHandler(UserPermissionException.class)
	protected ResponseEntity<Object> handler(UserPermissionException e){
		return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getError(), null));
	}
}
