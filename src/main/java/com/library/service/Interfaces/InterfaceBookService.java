package com.library.service.Interfaces;

import java.util.List;

import com.library.model.Book;
import com.library.model.IssuedBookDetails;
import com.library.model.User;
import com.library.responseDTO.BookResponseDTO;
import com.library.responseDTO.IssueBookResponseDTO;
import com.library.responseDTO.ReturnBookResponseDTO;

public interface InterfaceBookService {
	public BookResponseDTO addBook(Book book);
	public boolean deleteBook(String bookId);
	public Book getBookById(String bookId);
	public IssueBookResponseDTO issueBook(String bookId, String userId);
	public ReturnBookResponseDTO returnBook(String bookId, String userId, boolean isBookLost);
	public List<IssuedBookDetails> getIssuedBook(User user);
	public List<IssuedBookDetails> getAllIssuedBooks(boolean seeDefaulterList);
	public List<Book> getAllBooks();
	public List<IssuedBookDetails> getAllIssuedBookByUserName(String userName);
	
}
