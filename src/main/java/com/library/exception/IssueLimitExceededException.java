package com.library.exception;

public class IssueLimitExceededException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IssueLimitExceededException(String message) {
        super(message);
    }
}