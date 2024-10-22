package com.library.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import com.library.constants.ApiMessages;
import com.library.responseDTO.ApiError;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	 
	 
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ApiError> handleAllExceptions(Exception ex, WebRequest request) throws Exception {
		ApiError errorDetails = new ApiError(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
		
		
//		logger.error("Error occurred: {}, URL: {}, Method: {}, User: {}, Params: {}, Body: {}", ex.getMessage(),
//				request.getRequestURL(), request.getMethod(),
//				request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "Anonymous",
//				request.getParameterMap(),
//				request.getContentLength() > 0
//						? request.getReader().lines().collect(Collectors.joining(System.lineSeparator()))
//						: "N/A",
//				ex);
		return new ResponseEntity<ApiError>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex) {
		ApiError apiError = new ApiError(LocalDateTime.now(), ex.getMessage(), "Not found");
		return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BookUnavailableException.class)
	public ResponseEntity<ApiError> handleBookUnavailableException(BookUnavailableException ex) {
		ApiError apiError = new ApiError(LocalDateTime.now(), ex.getMessage(), ApiMessages.BOOK_UNAVAILABLE_MSG);
		return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(IssueLimitExceededException.class)
	public ResponseEntity<ApiError> handleIssueLimitExceededException(IssueLimitExceededException ex) {
		ApiError apiError = new ApiError(LocalDateTime.now(), ex.getMessage(), ApiMessages.ISSUE_LIMIT_EXCEEDED_MSG);
		return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DuplicateEntryException.class)
	public ResponseEntity<ApiError> handleDuplicateEntryException(DuplicateEntryException ex) {
		ApiError apiError = new ApiError(LocalDateTime.now(), ex.getMessage(), ApiMessages.DUPLICATE_ENTRY);
		return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(GeneralException.class)
	public final ResponseEntity<ApiError> handleGeneralExceptions(Exception ex, WebRequest request)
			throws Exception {
		ApiError errorDetails = new ApiError(LocalDateTime.now(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<ApiError>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {

		ApiError apiError = new ApiError(LocalDateTime.now(), "Total validation errors: " + ex.getErrorCount()
				+ " First error: " + ex.getFieldError().getDefaultMessage(), request.getDescription(false));
		return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
	}

}
