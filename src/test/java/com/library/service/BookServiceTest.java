//package com.library.service;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import com.library.constants.UserConstants;
//import com.library.dao.BookDAO;
//import com.library.dao.BookIssueDAO;
//import com.library.dao.UserDAO;
//import com.library.model.Book;
//import com.library.model.BookIssued;
//import com.library.model.IssuedBookDetails;
//import com.library.model.Status;
//import com.library.model.User;
//import com.library.service.BookService;
//
//class BookServiceTest {
//
//	@Mock
//	private BookDAO bookDAO;
//
//	@Mock
//	private UserDAO userDAO;
//
//	@Mock
//	private BookIssueDAO bookIssueDAO;
//	@InjectMocks
//	private BookService bookService;
//
//	@BeforeEach
//	void setUp() {
//		MockitoAnnotations.openMocks(this);
//		// bookService.setUserDAO(userDAO);
//	}
//
//	@Test
//	void testAddBook() throws Exception {
//		Book book = new Book();
//		when(bookDAO.addBook(book)).thenReturn(true);
//
//		boolean result = bookService.addBook(book);
//
//		assertTrue(result);
//		verify(bookDAO, times(1)).addBook(book);
//	}
//
//	@Test
//	void testIssueBook_Success() {
//		Book book = new Book();
//		User user = new User();
//		user.setUserId("123");
//		user.setBookIssueLimit(1);
//		book.setBookId("b123");
//		book.setStatus(Status.valueOf("Available"));
//		user.setBookIssueLimit(2);
//
//		when(bookDAO.issueBook(any(BookIssued.class))).thenReturn(true);
//		when(bookDAO.updateBookStatus(anyString(), anyString())).thenReturn(true);
//		when(userDAO.updateUser(user, user.getUserId(), UserConstants.BOOK_ISSUE_LIMIT_COLUMN)).thenReturn(true);
//		boolean result = bookService.issueBook(book, user);
//
//		assertTrue(result);
//		verify(bookDAO, times(1)).issueBook(any(BookIssued.class));
//		verify(bookDAO, times(1)).updateBookStatus(eq("b123"), eq("Issued"));
//		verify(userDAO, times(1)).updateUser(user, user.getUserId(), "bookIssueLimit");
//	}
//
//	@Test
//	void testIssueBook_BookNotAvailable() {
//		Book book = mock(Book.class);
//		User user = mock(User.class);
//
//		when(book.getStatus()).thenReturn(Status.Issued);
//
//		boolean result = bookService.issueBook(book, user);
//
//		assertFalse(result);
//		verify(bookDAO, never()).issueBook(any(BookIssued.class));
//	}
//
//	@Test
//	void testReturnBook_NoFine() {
//		User user = new User();
//		IssuedBookDetails issuedBook = new IssuedBookDetails();
//
//		user.setUserId("user123");
//		user.setBookIssueLimit(2);
//		user.setFine(new BigDecimal(100));
//
//		issuedBook.setBookId("book123");
//		issuedBook.setDeadline(LocalDate.now().plusDays(10));
//		issuedBook.setPrice(100);
//		issuedBook.setUserId("user123");
//
//		doNothing().when(bookDAO).returnBook(anyString(), anyString(), any(LocalDate.class));
//		when(bookDAO.updateBookStatus("book123", "Available")).thenReturn(true);
//		when(userDAO.updateUser(user, user.getUserId(), "fine")).thenReturn(true);
//		when(userDAO.updateUser(user, user.getUserId(), "bookIssueLimit")).thenReturn(true);
//
//		boolean result = bookService.returnBook(issuedBook, user, false);
//
//		assertTrue(result);
//		assertEquals(new BigDecimal(100), user.getFine());
//		assertEquals(3, user.getBookIssueLimit());
//		verify(bookDAO, times(1)).returnBook("book123", "user123", LocalDate.now());
//		verify(bookDAO, times(1)).updateBookStatus("book123", Status.Available.toString());
//		verify(userDAO, times(1)).updateUser(user, user.getUserId(), UserConstants.FINE_COLUMN);
//		verify(userDAO, times(1)).updateUser(user, user.getUserId(), UserConstants.BOOK_ISSUE_LIMIT_COLUMN);
//	}
//
//	@Test
//	void testReturnBook_WithFine() {
//
//		User user = new User();
//		IssuedBookDetails issuedBook = new IssuedBookDetails();
//
//		String userId = "user123";
//		String bookId = "book123";
//		user.setUserId(userId);
//		user.setUserName("username");
//		user.setFine(new BigDecimal(0)); // Initial fine is 0
//		user.setBookIssueLimit(2);
//		issuedBook.setBookId(bookId);
//		issuedBook.setPrice(100.0); // Book price is 100
//		issuedBook.setDeadline(LocalDate.now().minusDays(10)); // Deadline 10 days ago (overdue)
//
//		// Mock the interactions with DAOs
//		doNothing().when(bookDAO).returnBook(bookId, userId, LocalDate.now());
//		when(bookDAO.updateBookStatus(bookId, Status.Available.toString())).thenReturn(true);
//		when(userDAO.updateUser(user, user.getUserId(), "fine")).thenReturn(true);
//		when(userDAO.updateUser(any(User.class), eq(userId), eq(UserConstants.BOOK_ISSUE_LIMIT_COLUMN)))
//				.thenReturn(true);
//
//		// Execute the method under test
//		boolean result = bookService.returnBook(issuedBook, user, false);
//
//		// Assertions
//		assertTrue(result); // Ensure the returnBook method returned true
//		assertEquals(new BigDecimal(20), user.getFine());
//		assertEquals(3, user.getBookIssueLimit());
//		// Verify the interactions with DAO and userDAO
//		verify(bookDAO, times(1)).returnBook(bookId, userId, LocalDate.now());
//		verify(bookDAO, times(1)).updateBookStatus(bookId, Status.Available.toString());
//		// verify(userDAO, times(1)).updateUser(user,userId,"fine");
//		verify(userDAO, times(1)).updateUser(user, user.getUserId(), "fine");
//		verify(userDAO, times(1)).updateUser(eq(user), eq(userId), eq(UserConstants.BOOK_ISSUE_LIMIT_COLUMN));
//
//	}
//
//	@Test
//	void testIssueBook_NotIssuedWhenBookIssueLimitIsZero() {
//		// Create a real User object with bookIssueLimit = 0
//		User user = new User();
//		user.setUserId("user123");
//		user.setUserName("Test User");
//		user.setBookIssueLimit(0); // Set issue limit to 0
//
//		// Create a real Book object
//		Book book = new Book();
//		book.setBookId("book123");
//		book.setName("Test Book");
//		book.setStatus(Status.Available); // Book is available
//
//		// No need to mock bookDAO or userDAO interactions since the user should not be
//		// allowed to issue a book
//
//		// Call the method under test
//		boolean result = bookService.issueBook(book, user);
//
//		// Verify the expected behavior
//		assertFalse(result); // Book should not be issued
//
//		// Verify that no interactions happened with bookDAO or userDAO for issuing the
//		// book
//		verify(bookDAO, never()).updateBookStatus(anyString(), anyString());
//		verify(bookDAO, never()).issueBook(any(BookIssued.class));
//		verify(userDAO, never()).updateUser(any(User.class), anyString(), anyString());
//	}
//
//	@Test
//	void testReturnBook_WithBookLost() {
//		User user = new User();
//		IssuedBookDetails issuedBook = new IssuedBookDetails();
//
//		user.setUserId("user123");
//		user.setBookIssueLimit(2);
//		user.setFine(new BigDecimal(50));
//
//		issuedBook.setBookId("book123");
//		issuedBook.setDeadline(LocalDate.now().plusDays(10));
//		issuedBook.setPrice(100);
//		issuedBook.setUserId("user123");
//
//		doNothing().when(bookDAO).returnBook(anyString(), anyString(), any(LocalDate.class));
//		when(bookDAO.updateBookStatus("book123", "Lost")).thenReturn(true);
//		when(userDAO.updateUser(user, user.getUserId(), "fine")).thenReturn(true);
//		when(userDAO.updateUser(user, user.getUserId(), "bookIssueLimit")).thenReturn(true);
//
//		boolean result = bookService.returnBook(issuedBook, user, true);
//
//		assertTrue(result);
//		assertEquals(new BigDecimal(150), user.getFine());
//		assertEquals(3, user.getBookIssueLimit());
//		verify(bookDAO, times(1)).returnBook("book123", "user123", LocalDate.now());
//		verify(bookDAO, times(1)).updateBookStatus("book123", Status.Lost.toString());
//		verify(userDAO, times(1)).updateUser(user, user.getUserId(), UserConstants.FINE_COLUMN);
//		verify(userDAO, times(1)).updateUser(user, user.getUserId(), UserConstants.BOOK_ISSUE_LIMIT_COLUMN);
//	}
//
//	@Test
//	void testGetIssuedBook() {
//		User user = mock(User.class);
//		List<IssuedBookDetails> issuedBooks = new ArrayList<>();
//
//		when(bookIssueDAO.getIssuedBook(user.getUserId())).thenReturn(issuedBooks);
//
//		List<IssuedBookDetails> result = bookService.getIssuedBook(user);
//
//		assertEquals(issuedBooks, result);
//		verify(bookIssueDAO, times(1)).getIssuedBook(user.getUserId());
//	}
//
//	@Test
//	void testGetAllIssuedBooks() {
//		List<IssuedBookDetails> issuedBooks = new ArrayList<>();
//		IssuedBookDetails issuedBook1 = new IssuedBookDetails();
//		issuedBook1.setUserId("u123");
//		issuedBook1.setBookId("b123");
//		issuedBook1.setIssueDate(LocalDate.now());
//		issuedBook1.setDeadline(LocalDate.now().plusDays(15));
//
//		issuedBooks.add(issuedBook1);
//
//		when(bookIssueDAO.getAllIssuedBooks()).thenReturn(issuedBooks);
//
//		List<IssuedBookDetails> result = bookService.getAllIssuedBooks(false);
//
//		assertEquals(issuedBooks, result);
//		verify(bookIssueDAO, times(1)).getAllIssuedBooks();
//	}
//
//	@Test
//	void testGetAllIssuedBooksSeeDefaulterList() {
//		List<IssuedBookDetails> issuedBooks = new ArrayList<>();
//		IssuedBookDetails issuedBook1 = new IssuedBookDetails();
//		issuedBook1.setUserId("test123");
//		issuedBook1.setUserName("testuser");
//		issuedBook1.setIssueDate(LocalDate.now().minusDays(25));
//		issuedBook1.setDeadline(LocalDate.now().minusDays(10));
//		issuedBook1.setBookId("book123");
//		User user = new User();
//		user.setUserId("test123");
//		user.setUserName("testuser");
//		issuedBooks.add(issuedBook1);
//
//		when(bookIssueDAO.getAllIssuedBooks()).thenReturn(issuedBooks);
//
//		List<IssuedBookDetails> result = bookService.getAllIssuedBooks(true);
//
//		assertEquals(issuedBooks, result);
//		assertEquals(issuedBooks.get(0).getFine(), 20);
//		verify(bookIssueDAO, times(1)).getAllIssuedBooks();
//	}
//
//	@Test
//	void testGetAllIssuedBookByUserName() {
//		List<IssuedBookDetails> issuedBooks = new ArrayList<>();
//		IssuedBookDetails issuedBook1 = new IssuedBookDetails();
//		issuedBook1.setUserId("u123");
//		issuedBook1.setUserName("username");
//		issuedBook1.setBookId("b123");
//		issuedBook1.setIssueDate(LocalDate.now());
//		issuedBook1.setDeadline(LocalDate.now().plusDays(15));
//		issuedBooks.add(issuedBook1);
//
//		when(bookIssueDAO.getAllIssuedBooks()).thenReturn(issuedBooks);
//
//		List<IssuedBookDetails> result = bookService.getAllIssuedBookByUserName("username");
//
//		assertFalse(result.isEmpty());
//		verify(bookIssueDAO, times(1)).getAllIssuedBooks();
//	}
//
//	@Test
//	void getAllAvailableBooksTest() {
//		List<Book> bookList = new ArrayList<>();
//		Book book1 = new Book();
//		book1.setBookId("book1");
//		book1.setStatus(Status.Available);
//		bookList.add(book1);
//
//		when(bookDAO.getAllBooks()).thenReturn(bookList);
//
//		List<Book> result = bookService.getAllBooks();
//
//		assertFalse(result.isEmpty());
//
//		verify(bookDAO, times(1)).getAllBooks();
//
//	}
//
//	@Test
//	void getAllAvailableBooksEmptyResult() {
//		List<Book> bookList = new ArrayList<>();
//		Book book1 = new Book();
//		book1.setBookId("book1");
//		book1.setStatus(Status.Issued);
//
//		when(bookDAO.getAllBooks()).thenReturn(bookList);
//
//		List<Book> result = bookService.getAllBooks();
//
//		assertTrue(result.isEmpty());
//
//		verify(bookDAO, times(1)).getAllBooks();
//
//	}
//}
