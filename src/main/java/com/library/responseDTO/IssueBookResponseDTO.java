package com.library.responseDTO;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssueBookResponseDTO {
	
	String userId;
	String bookId;
	LocalDate issueDate;
	LocalDate deadline;
	String message;
}
