package com.library.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.library.constants.BookConstants;
import com.library.constants.UserConstants;
import com.library.model.Book;
import com.library.model.BookIssued;
import com.library.model.Gender;
import com.library.model.IssuedBookDetails;
import com.library.model.Role;
import com.library.model.Status;
import com.library.model.User;

@Repository
public class BookDAO extends GenericDAO<Book> implements InterfaceBookDAO {

	public BookDAO() {
	}

	protected Book mapResultSetToEntity(ResultSet resultSet) throws SQLException {
		Book book = null;
		book = new Book();
		book.setBookId(resultSet.getString(BookConstants.BOOK_ID_COLUMN));
		book.setName(resultSet.getString(BookConstants.NAME_COLUMN));
		book.setAuthor(resultSet.getString(BookConstants.AUTHOR_COLUMN));
		book.setPublication(resultSet.getString(BookConstants.PUBLICATION_COLUMN));
		book.setEdition(resultSet.getString(BookConstants.EDITION_COLUMN));
		book.setShelfLocation(resultSet.getString(BookConstants.SHELF_COLUMN));
		book.setPrice(resultSet.getDouble(BookConstants.PRICE_COLUMN));
		book.setStatus(Status.valueOf(resultSet.getString(BookConstants.STATUS_COLUMN)));
		return book;
	}

	public boolean addBook(Book book) {
		String sqlQueryAddBook = String.format(
				"INSERT INTO book (bookId, name, author, publication, edition, price, shelfLocation, status) VALUES ('%s', '%s', '%s', '%s', '%s', '%.2f', '%s', '%s')",
				book.getBookId(), book.getName(), book.getAuthor(), book.getPublication(), book.getEdition(),
				book.getPrice(), book.getShelfLocation(), book.getStatus().toString());

		try {
			return executeUpdateQuery(sqlQueryAddBook);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public Book getBook(String bookId) {
		String sqlQueryGetBook = String.format("SELECT * from book where bookId = '%s'",bookId);
		try {
			return executeGetQuery(sqlQueryGetBook);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

		
	}
	public boolean issueBook(BookIssued bookIssued) {
		String insertBookIssueQuery = String.format(
				"INSERT INTO BookIssued (issueId, userId, bookId, issueDate, deadline, status) VALUES ('%s','%s','%s','%s','%s','%s')",
				bookIssued.getIssueId(), bookIssued.getUserId(), bookIssued.getBookId(), bookIssued.getIssueDate(),
				bookIssued.getDeadlineDate(), bookIssued.getStatus().toString());
		try {
			return executeUpdateQuery(insertBookIssueQuery);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	public boolean updateBookStatus(String bookId, String status) {
		String sqlQueryUpdateBookStatus = String.format("update book set status = '%s' where bookId = '%s'", status,
				bookId);
		try {
			return executeUpdateQuery(sqlQueryUpdateBookStatus);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	public void returnBook(String bookId, String userId, LocalDate returnDate) {

		try {

			String returnBookSQlQuery = String.format(
					"UPDATE BookIssued SET status = 'Returned', returnDate = '%s' WHERE bookId = '%s' AND userId = '%s' AND status = 'Issued'",
					returnDate, bookId, userId);
			executeUpdateQuery(returnBookSQlQuery);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

	}

	public List<Book> getAllBooks() {
		List<Book> books = new ArrayList<>();
		String selectAllBooksSqlQuery = "select * from book where status = 'Available'";
		try {
			return executeGetAllQuery(selectAllBooksSqlQuery);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return books;
	}

}
