package com.library.responseDTO;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturnBookResponseDTO {
	String userId;
	String bookId;
	LocalDate issueDate;
	LocalDate deadline;
	LocalDate returnDate;
	double fine;
	String message;
}
