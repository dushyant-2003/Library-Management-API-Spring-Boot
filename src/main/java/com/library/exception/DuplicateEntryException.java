package com.library.exception;

public class DuplicateEntryException extends RuntimeException {
    
	private static final long serialVersionUID = 1L;

	public DuplicateEntryException(String fieldName, String fieldValue) {
        super("Duplicate entry for " + fieldName + ": " + fieldValue);
    }
}
