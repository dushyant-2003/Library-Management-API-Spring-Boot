package com.library.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.model.Book;
import com.library.model.IssuedBookDetails;
import com.library.model.User;
import com.library.requestDTO.IssueBookRequestDTO;
import com.library.requestDTO.ReturnBookRequestDTO;
import com.library.responseDTO.ApiResponse;
import com.library.responseDTO.BookResponseDTO;
import com.library.responseDTO.IssueBookResponseDTO;
import com.library.responseDTO.ReturnBookResponseDTO;
import com.library.responseDTO.Status;
import com.library.service.BookService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class BookController {

	private BookService bookService;

	public BookController(BookService bookService) {
		this.bookService = bookService;
	}

	@GetMapping("/books")
	public ResponseEntity<ApiResponse<List<Book>>> getAllBooks() {
		List<Book> books = bookService.getAllBooks();

		ApiResponse<List<Book>> response = ApiResponse.<List<Book>>builder().status(Status.SUCCESS)
				.message("Books retrieved successfully").data(books).build();

		return ResponseEntity.ok(response);
	}

	@PostMapping("/books")
	public ResponseEntity<ApiResponse<BookResponseDTO>> addBook(@Valid @RequestBody Book book) {

		BookResponseDTO responseDTO = bookService.addBook(book);
		if (responseDTO != null) {
			ApiResponse<BookResponseDTO> response = ApiResponse.<BookResponseDTO>builder().status(Status.SUCCESS)
					.message("Book added successfully").data(responseDTO).build();
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		}
		ApiResponse<BookResponseDTO> errorResponse = ApiResponse.<BookResponseDTO>builder().status(Status.ERROR)
				.message("Failed to add book").build();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}

	@DeleteMapping("/books/{bookId}")
	public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable String bookId) {

		boolean bookDeletionStatus = bookService.deleteBook(bookId);

		if (bookDeletionStatus) {
			ApiResponse<String> response = ApiResponse.<String>builder().status(Status.SUCCESS)
					.message("Book deleted successfully").build();
			return ResponseEntity.ok(response);
		}

		ApiResponse<String> response = ApiResponse.<String>builder().status(Status.FAILURE).message("Book not found")
				.build();
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}
	
	@PostMapping("/books/issue")
	public ResponseEntity<ApiResponse<IssueBookResponseDTO>> issueBook(
			@Valid @RequestBody IssueBookRequestDTO issueBookRequestDTO) {

		String bookId = issueBookRequestDTO.getBookId();
		String userId = issueBookRequestDTO.getUserId();

		IssueBookResponseDTO responseData = bookService.issueBook(bookId, userId);

		ApiResponse<IssueBookResponseDTO> response = ApiResponse.<IssueBookResponseDTO>builder().status(Status.SUCCESS)
				.message("Book issued successfully").data(responseData).build();

		return ResponseEntity.ok(response);

	}

	@PostMapping("books/return")
	public ResponseEntity<ApiResponse<ReturnBookResponseDTO>> returnBook(
			@Valid @RequestBody ReturnBookRequestDTO returnBookRequestDTO) {

		String bookId = returnBookRequestDTO.getBookId();
		String userId = returnBookRequestDTO.getUserId();
		boolean isBookLost = returnBookRequestDTO.isBookLostStatus();

		ReturnBookResponseDTO responseData = bookService.returnBook(bookId, userId, isBookLost);

		ApiResponse<ReturnBookResponseDTO> response = ApiResponse.<ReturnBookResponseDTO>builder()
				.status(Status.SUCCESS).message("Book returned successfully").data(responseData).build();

		return ResponseEntity.ok(response);
	}

	public List<IssuedBookDetails> getIssuedBook(User user) {
		return bookService.getIssuedBook(user);
	}

	@GetMapping("books/issued-books")
	public List<IssuedBookDetails> getAllIssuedBook(boolean seeDefaulterList) {

		return bookService.getAllIssuedBooks(false);
	}

	@GetMapping("/users/{userName}/issued-books")
	public ResponseEntity<ApiResponse<List<IssuedBookDetails>>> getAllIssuedBookByUserName(
			@PathVariable String userName) {

		List<IssuedBookDetails> issuedBooksList = bookService.getAllIssuedBookByUserName(userName);
		ApiResponse<List<IssuedBookDetails>> response = ApiResponse.<List<IssuedBookDetails>>builder()
				.status(Status.SUCCESS).message("Books fetched succesfully").data(issuedBooksList).build();
		return ResponseEntity.ok(response);
	}

}
