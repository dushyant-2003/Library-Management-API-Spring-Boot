package com.library.dao;



import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.library.constants.IssuedBookConstants;
import com.library.model.IssuedBookDetails;

public class BookIssueDAOTest {

	@Mock
	private Connection mockConnection;

	@Mock
	private PreparedStatement mockPreparedStatement;

	@Mock
	private ResultSet mockResultSet;

	@InjectMocks
	private BookIssueDAO bookIssueDAO;

	@BeforeEach
	void setUp() throws SQLException {
		MockitoAnnotations.openMocks(this);
	    when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);
	}

	@Test
	public void testGetAllIssuedBooks_Success() throws SQLException, ClassNotFoundException {
		// Arrange

		
		//when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

		when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
		when(mockResultSet.next()).thenReturn(true).thenReturn(false);
		when(mockResultSet.getString(IssuedBookConstants.ISSUER_ID_COLUMN)).thenReturn("user123");
		when(mockResultSet.getString(IssuedBookConstants.ISSUER_NAME_COLUMN)).thenReturn("Test User");
		when(mockResultSet.getString(IssuedBookConstants.ISSUER_USERNAME_COLUMN)).thenReturn("testUser");
		when(mockResultSet.getString(IssuedBookConstants.BOOK_ID_COLUMN)).thenReturn("book123");
		when(mockResultSet.getString(IssuedBookConstants.BOOK_NAME_COLUMN)).thenReturn("test book");
		when(mockResultSet.getString(IssuedBookConstants.AUTHOR_COLUMN)).thenReturn("author");
		when(mockResultSet.getString(IssuedBookConstants.EMAIL_COLUMN)).thenReturn("test@gmail.com");
		when(mockResultSet.getDate(IssuedBookConstants.ISSUE_DATE_COLUMN))
				.thenReturn(Date.valueOf(LocalDate.of(2024, 9, 11)));
		when(mockResultSet.getDate(IssuedBookConstants.DEADLINE_DATE_COLUMN))
				.thenReturn(Date.valueOf(LocalDate.of(2024, 9, 26)));
		// Act
		List<IssuedBookDetails> result = bookIssueDAO.getAllIssuedBooks();

		// Assert
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("book123", result.get(0).getBookId());

		verify(mockConnection).prepareStatement(anyString());
		verify(mockPreparedStatement).executeQuery();
		verify(mockResultSet, times(2)).next();
	}
	
	 @Test
	    public void testGetAllIssuedBooks_EmptyList() throws SQLException, ClassNotFoundException {
	        // Arrange
	        List<IssuedBookDetails> emptyList = new ArrayList<>();

	        // Mock the execution of the query
	        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
	        
	        List<IssuedBookDetails> result = bookIssueDAO.getAllIssuedBooks();

	        // Assert
	        assertNotNull(result);
	        assertTrue(result.isEmpty());
	        verify(mockPreparedStatement, times(1)).executeQuery();
	    }
	
	  /*
	 * @Test public void testGetIssuedBookByUserId_Success() throws SQLException,
	 * ClassNotFoundException { // Arrange String userId = "user123";
	 * List<IssuedBookDetails> issuedBookList = new ArrayList<>(); IssuedBookDetails
	 * book = new IssuedBookDetails(); book.setBookId("book456");
	 * book.setUserId(userId); book.setBookName("Book Two");
	 * issuedBookList.add(book);
	 * 
	 * // Mock the execution of the query
	 * when(mockBookIssueDAO.executeGetAllQuery(anyString())).thenReturn(
	 * issuedBookList);
	 * 
	 * // Act List<IssuedBookDetails> result = bookIssueDAO.getIssuedBook(userId);
	 * 
	 * // Assert assertNotNull(result); assertEquals(1, result.size());
	 * assertEquals("book456", result.get(0).getBookId()); verify(mockBookIssueDAO,
	 * times(1)).executeGetAllQuery(anyString()); }
	 * 
	 * @Test public void testGetIssuedBookByUserId_EmptyList() throws SQLException,
	 * ClassNotFoundException { // Arrange String userId = "user123";
	 * List<IssuedBookDetails> emptyList = new ArrayList<>();
	 * 
	 * // Mock the execution of the query
	 * when(mockBookIssueDAO.executeGetAllQuery(anyString())).thenReturn(emptyList);
	 * 
	 * // Act List<IssuedBookDetails> result = bookIssueDAO.getIssuedBook(userId);
	 * 
	 * // Assert assertNotNull(result); assertTrue(result.isEmpty());
	 * verify(mockBookIssueDAO, times(1)).executeGetAllQuery(anyString()); }
	 * 
	 * @Test public void testGetIssuedBookByUserId_Exception() throws SQLException,
	 * ClassNotFoundException { // Arrange String userId = "user123";
	 * 
	 * // Mock the exception case
	 * when(mockBookIssueDAO.executeGetAllQuery(anyString())).thenThrow(new
	 * SQLException());
	 * 
	 * // Act List<IssuedBookDetails> result = bookIssueDAO.getIssuedBook(userId);
	 * 
	 * // Assert assertNotNull(result); assertTrue(result.isEmpty());
	 * verify(mockBookIssueDAO, times(1)).executeGetAllQuery(anyString()); }
	 */
}
