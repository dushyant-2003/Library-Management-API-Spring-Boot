package com.library.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookIssued {
	private String issueId;
	private String userId;
	private String bookId;

	private LocalDate issueDate;
	private LocalDate deadlineDate;
	private LocalDate returnDate;
	private Status status;

	public BookIssued() {
		
	}
	
	public BookIssued(String issueId, String userId, String bookId, LocalDate issueDate, LocalDate deadlineDate,
			LocalDate returnDate, Status status) {
		super();
		this.issueId = issueId;
		this.userId = userId;
		this.bookId = bookId;
		this.issueDate = issueDate;
		this.deadlineDate = deadlineDate;
		this.returnDate = returnDate;
		this.status = status;
	}
	public BookIssued(String issueId, String userId, String bookId, LocalDate issueDate, LocalDate deadlineDate,
			Status status) {
		super();
		this.issueId = issueId;
		this.userId = userId;
		this.bookId = bookId;
		this.issueDate = issueDate;
		this.deadlineDate = deadlineDate;
		this.returnDate = returnDate;
		this.status = status;
	}

}
