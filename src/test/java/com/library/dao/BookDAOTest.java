package com.library.dao;

import com.library.constants.BookConstants;
import com.library.dao.BookDAO;
import com.library.model.Book;
import com.library.model.BookIssued;
import com.library.model.Status;
import com.library.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BookDAOTest {

	@Mock
	private Connection mockConnection;

	@Mock
	private PreparedStatement mockPreparedStatement;
	
	@Mock
	private UserDAO userDAO;
	
	@Mock
	private ResultSet mockResultSet;

	@InjectMocks
	private BookDAO bookDAO;

	@BeforeEach
	void setUp() throws SQLException {
		MockitoAnnotations.openMocks(this);
		when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
	}

	@Test
	void testAddBookSuccess() throws SQLException {
		// Arrange
		Book book = new Book();
		book.setBookId("1");
		book.setName("Test Book");
		book.setAuthor("Author");
		book.setPublication("Publication");
		book.setEdition("1st Edition");
		book.setPrice(100.0);
		book.setShelfLocation("Shelf A");
		book.setStatus(Status.Available);

		when(mockPreparedStatement.executeUpdate()).thenReturn(1);

		boolean result = bookDAO.addBook(book);

		assertTrue(result);
		verify(mockPreparedStatement, times(1)).executeUpdate();
	}

	@Test
	void testAddBookFailure() throws SQLException {
		// Arrange
		Book book = new Book();
		book.setBookId("1");
		book.setName("Test Book");
		book.setStatus(Status.Available);

		// Simulate the update query returning a failure
		when(mockPreparedStatement.executeUpdate()).thenReturn(0);

		// Act
		boolean result = bookDAO.addBook(book);

		// Assert
		assertFalse(result);
		verify(mockPreparedStatement, times(1)).executeUpdate();
	}

	@Test
	void testIssueBookSuccess() throws SQLException {
		// Arrange
		BookIssued bookIssued = new BookIssued();
		bookIssued.setIssueId("1");
		bookIssued.setUserId("user123");
		bookIssued.setBookId("book123");
		bookIssued.setIssueDate(LocalDate.now());
		bookIssued.setDeadlineDate(LocalDate.now().plusDays(7));
		bookIssued.setStatus(Status.Issued);

		// Simulate the update query returning a success
		when(mockPreparedStatement.executeUpdate()).thenReturn(1);

		// Act
		boolean result = bookDAO.issueBook(bookIssued);

		// Assert
		assertTrue(result);
		verify(mockPreparedStatement, times(1)).executeUpdate();
	}

	@Test
	void testIssueBookFailure() throws SQLException {
		// Arrange
		BookIssued bookIssued = new BookIssued();
		bookIssued.setIssueId("1");
		bookIssued.setStatus(Status.Issued);

		when(mockPreparedStatement.executeUpdate()).thenReturn(0);

		// Act
		boolean result = bookDAO.issueBook(bookIssued);

		// Assert
		assertFalse(result);
		verify(mockPreparedStatement, times(1)).executeUpdate();
	}

	@Test
	void getAllBooksTest() throws SQLException {
		Book book = new Book();
		book.setBookId("book123");
		book.setStatus(Status.Available);

		String sqlQuery = "select * from book where status = 'Available'";
		when(mockConnection.prepareStatement(sqlQuery)).thenReturn(mockPreparedStatement);
		when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

		when(mockResultSet.next()).thenReturn(true).thenReturn(false); 
		mockResultSetFunction();

		// Call the method under test
		List<Book> books = bookDAO.getAllBooks();

		assertEquals(1, books.size());

		verify(mockConnection).prepareStatement(sqlQuery);
		verify(mockPreparedStatement).executeQuery();
		verify(mockResultSet, times(2)).next();

	}
	@Test
    public void testUpdateBookStatus_Success() throws SQLException, ClassNotFoundException {
        // Arrange
        String bookId = "book123";
        String status = "Available";
              when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Act
        boolean result = bookDAO.updateBookStatus(bookId, status);

        // Assert
        assertTrue(result);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testUpdateBookStatus_Failure() throws SQLException, ClassNotFoundException {
        // Arrange
        String bookId = "book123";
        String status = "Unavailable";
        
        // Mock the failure case
        when(mockPreparedStatement.executeUpdate(anyString())).thenThrow(new SQLException());

        // Act
        boolean result = bookDAO.updateBookStatus(bookId, status);

        // Assert
        assertFalse(result);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    // Test for returnBook method
    @Test
    public void testReturnBook_Success() throws SQLException, ClassNotFoundException {
        // Arrange
        String bookId = "book123";
        String userId = "user456";
        LocalDate returnDate = LocalDate.now();
        
        // Mock the database update behavior
        when(mockPreparedStatement.executeUpdate(anyString())).thenReturn(1);

        // Act
        bookDAO.returnBook(bookId, userId, returnDate);

        
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testReturnBook_Failure() throws SQLException, ClassNotFoundException {
        // Arrange
        String bookId = "book123";
        String userId = "user456";
        LocalDate returnDate = LocalDate.now();
        
        // Mock the failure case
        doThrow(new SQLException()).when(mockPreparedStatement).executeUpdate(anyString());

        // Act and Assert
        assertDoesNotThrow(() -> bookDAO.returnBook(bookId, userId, returnDate));
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }
	private void mockResultSetFunction() throws SQLException {
		// Mock two calls, first returns true, then false
		when(mockResultSet.getString(BookConstants.BOOK_ID_COLUMN)).thenReturn("book123");
		when(mockResultSet.getString(BookConstants.NAME_COLUMN)).thenReturn("test book");
		when(mockResultSet.getString(BookConstants.AUTHOR_COLUMN)).thenReturn("test author");
		when(mockResultSet.getString(BookConstants.PUBLICATION_COLUMN)).thenReturn("test publication");
		when(mockResultSet.getString(BookConstants.EDITION_COLUMN)).thenReturn("1");
		when(mockResultSet.getDouble(BookConstants.PRICE_COLUMN)).thenReturn((double) 100);
		when(mockResultSet.getString(BookConstants.SHELF_COLUMN)).thenReturn("S");
		when(mockResultSet.getString(BookConstants.STATUS_COLUMN)).thenReturn("Available");
	}

}
