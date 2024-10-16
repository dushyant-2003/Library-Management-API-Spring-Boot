package com.library.exception;

public class BookNotFoundException extends ResourceNotFoundException {

	private static final long serialVersionUID = 1L;

	public BookNotFoundException(String bookId) {
		super("Book with + " + bookId + " not found");
	}
}
