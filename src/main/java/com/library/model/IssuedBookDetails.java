package com.library.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssuedBookDetails {
	private String userId;
	private String userName;
	private String bookId;
	private String issuerName;
	private String email;
	private String bookName;
	private String author;
	private double price;
	private LocalDate issueDate;
	private LocalDate deadline;
	private double fine;

}
