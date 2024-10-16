package com.library.dao;

import java.time.LocalDate;
import java.util.List;

import com.library.model.Book;
import com.library.model.BookIssued;

public interface InterfaceBookDAO {
	public boolean addBook(Book book);
	public boolean issueBook(BookIssued bookIssued);
	public boolean updateBookStatus(String bookId, String status);
	public void returnBook(String bookId, String userId, LocalDate returnDate);
	public List<Book> getAllBooks();
	public Book getBook(String bookId);
}
