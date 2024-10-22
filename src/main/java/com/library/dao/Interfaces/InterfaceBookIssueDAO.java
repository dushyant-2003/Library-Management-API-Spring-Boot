package com.library.dao.Interfaces;

import java.util.List;

import com.library.model.IssuedBookDetails;

public interface InterfaceBookIssueDAO {
	public List<IssuedBookDetails> getAllIssuedBooks();
	List<IssuedBookDetails> getIssuedBook(String userId);
	IssuedBookDetails getIssuedBook(String bookId, String userId);
}
