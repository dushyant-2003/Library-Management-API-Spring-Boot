package com.library.exception;

public class UserNotFoundException extends ResourceNotFoundException {

	
	private static final long serialVersionUID = 1L;
	
	public UserNotFoundException(String userId) {
		super("User with id " + userId + " not found");
	}
}
