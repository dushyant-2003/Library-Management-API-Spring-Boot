package com.library.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.library.constants.StringConstants;
import com.library.constants.UserConstants;
import com.library.dao.InterfaceBookDAO;
import com.library.dao.InterfaceBookIssueDAO;
import com.library.dao.InterfaceUserDAO;
import com.library.exception.BookNotFoundException;
import com.library.exception.BookUnavailableException;
import com.library.exception.IssueLimitExceededException;
import com.library.exception.UserNotFoundException;
import com.library.model.Book;
import com.library.model.BookIssued;
import com.library.model.IssuedBookDetails;
import com.library.model.Status;
import com.library.model.User;
import com.library.responseDTO.BookResponseDTO;
import com.library.responseDTO.IssueBookResponseDTO;
import com.library.responseDTO.ReturnBookResponseDTO;
import com.library.util.IdGenerator;
import com.library.util.LoggingUtil;

@Service
public class BookService {

	private static final Logger logger = LoggingUtil.getLogger(BookService.class);

	@Value("${book.issue.duration}")
	private int issueDuration;

	@Value("${fine.per.day}")
	private double finePerDay;
	
	private InterfaceBookDAO bookDAO;
	private InterfaceUserDAO userDAO;
	private InterfaceBookIssueDAO bookIssueDAO;

	@Autowired 
	public BookService(InterfaceBookDAO bookDAO, InterfaceUserDAO userDAO, InterfaceBookIssueDAO bookIssueDAO) {
		this.bookDAO = bookDAO;
		this.userDAO = userDAO;
		this.bookIssueDAO = bookIssueDAO;
	}

	public BookResponseDTO addBook(Book book) {
		
		String bookId = IdGenerator.generateBookId();
		book.setBookId(bookId);
		book.setStatus(Status.Available);
		boolean addBookStatus = bookDAO.addBook(book);
		
		if(!addBookStatus) {
			return null;
		}
		
		BookResponseDTO responseDTO = new BookResponseDTO(book.getBookId(),book.getName(),book.getPrice());
		
		logger.log(Level.INFO, "Book added status: {0}", addBookStatus);
		return responseDTO;
	}

	public Book getBookById(String bookId) {
		return bookDAO.getBook(bookId);
	}

	public IssueBookResponseDTO issueBook(String bookId, String userId) {

		Book book = getBookById(bookId);
		User issuer = userDAO.getUserByUserId(userId);

		if (book == null) {
			throw new BookNotFoundException(bookId);
		}
		if (issuer == null) {
			throw new UserNotFoundException(userId);
		}
		String issueId = IdGenerator.generateIssueId();
		Status bookStatus = book.getStatus();

		IssueBookResponseDTO responseDTO = new IssueBookResponseDTO();
		if (!Status.Available.toString().equalsIgnoreCase(bookStatus.toString())) {
			logger.log(Level.WARNING, StringConstants.BOOK_NOT_AVAILABLE);
			throw new BookUnavailableException(StringConstants.BOOK_NOT_AVAILABLE);
		}

		int bookIssueLimit = issuer.getBookIssueLimit();
		if (bookIssueLimit == 0) {
			logger.log(Level.WARNING, StringConstants.MAX_BOOKS_ISSUED_MSG);
			throw new IssueLimitExceededException(StringConstants.MAX_BOOKS_ISSUED_MSG);
		}

		LocalDate issueDate = LocalDate.now();
		LocalDate deadLineDate = issueDate.plusDays(issueDuration);
		BookIssued bookIssued = new BookIssued(issueId, userId, bookId, issueDate, deadLineDate, Status.Issued);

		bookDAO.updateBookStatus(bookId, Status.Issued.toString());
		issuer.setBookIssueLimit(bookIssueLimit - 1);
		userDAO.updateUser(issuer, userId, UserConstants.BOOK_ISSUE_LIMIT_COLUMN);

		boolean issueStatus = bookDAO.issueBook(bookIssued);

		if (!issueStatus) {
			responseDTO.setMessage("Cannot issue book");
		}

		responseDTO.setUserId(userId);
		responseDTO.setBookId(bookId);
		responseDTO.setIssueDate(issueDate);
		responseDTO.setDeadline(deadLineDate);
		responseDTO.setMessage("Book issued successfully");
		logger.log(Level.INFO, "Book issued: {0}, Status: {1}", new Object[] { book.getName(), issueStatus });
		return responseDTO;
	}

	public ReturnBookResponseDTO returnBook(String bookId, String userId, boolean isBookLost) {
	
		User user = userDAO.getUserByUserId(userId);
		
		if(user == null) {
			throw new UserNotFoundException(userId);
		}
		LocalDate currentDate = LocalDate.now();
		
		IssuedBookDetails book = bookIssueDAO.getIssuedBook(bookId,userId);
		
		if(book == null) {
			throw new BookNotFoundException(bookId);
		}
		
		double price = book.getPrice();
		LocalDate deadLineDate = book.getDeadline();

		long daysLate = ChronoUnit.DAYS.between(deadLineDate, currentDate);
		double fine = 0;
		String updatedStatus = "";

		if (isBookLost) {
			fine = price;
			updatedStatus = Status.Lost.toString();
		} else {
			fine = daysLate > 0 ? daysLate * finePerDay : 0;
			updatedStatus = Status.Available.toString();
		}

		// if fine greater than price of book then make fine = price
		fine = (fine>price) ? price : fine;
		
		bookDAO.returnBook(bookId, userId, currentDate);
		bookDAO.updateBookStatus(bookId, updatedStatus);

		// Calculate fine
		BigDecimal previousDues = user.getFine();
		BigDecimal newDues = new BigDecimal(fine);
		BigDecimal totalDues = previousDues.add(newDues);
		user.setFine(totalDues);
		userDAO.updateUser(user, userId, UserConstants.FINE_COLUMN);
		
		// Update book issue limit
		user.setBookIssueLimit(user.getBookIssueLimit() + 1);
		userDAO.updateUser(user, userId, UserConstants.BOOK_ISSUE_LIMIT_COLUMN);

		// set the response DTO
		ReturnBookResponseDTO responseDTO = new ReturnBookResponseDTO();
		responseDTO.setBookId(bookId);
		responseDTO.setUserId(userId);
		responseDTO.setIssueDate(book.getIssueDate());
		responseDTO.setDeadline(deadLineDate);
		responseDTO.setReturnDate(currentDate);
		responseDTO.setFine(fine);
		responseDTO.setMessage("Book returned successfully");
		if (fine > 0) {
			
			logger.log(Level.INFO, "Book returned with a fine of Rs. {0}", fine);
		} else {
			logger.log(Level.INFO, "Book returned successfully. No fine incurred.");
		}

		return responseDTO;
	}

	public List<IssuedBookDetails> getIssuedBook(User user) {
		logger.log(Level.INFO, "Fetching issued books for user: {0}", user.getUserName());
		return bookIssueDAO.getIssuedBook(user.getUserId());
	}

	public List<IssuedBookDetails> getAllIssuedBooks(boolean seeDefaulterList) {
		logger.log(Level.INFO, "Fetching all issued books");
		LocalDate currentDate = LocalDate.now();
		List<IssuedBookDetails> issuedBookDetails = bookIssueDAO.getAllIssuedBooks();

		issuedBookDetails.forEach(issuedBook -> {
			if (issuedBook.getDeadline().isBefore(currentDate)) {
				long daysOverdue = ChronoUnit.DAYS.between(issuedBook.getDeadline(), currentDate);
				issuedBook.setFine(finePerDay * daysOverdue);
			}
		});

		if (!seeDefaulterList) {
			return issuedBookDetails;
		}

		return issuedBookDetails.stream().filter(issuedBook -> issuedBook.getDeadline().isBefore(currentDate))
				.collect(Collectors.toList());
	}

	public List<Book> getAllBooks() {
		logger.log(Level.INFO, "Fetching all books");
		return bookDAO.getAllBooks();
	}

	public List<IssuedBookDetails> getAllIssuedBookByUserName(String userName) {
		
		List<IssuedBookDetails> issuedBookDetails = getAllIssuedBooks(false);
		if (issuedBookDetails.isEmpty()) {
			return issuedBookDetails;
		}
		return issuedBookDetails.stream().filter(issuedBook -> issuedBook.getUserName().equals(userName))
				.collect(Collectors.toList());
	}

	
	
}
