package com.library.requestDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssueBookRequestDTO {
	
	@NotNull(message="User Id cannot be null")
	private String userId;
	
	@NotNull(message="Book Id cannot be null")
	private String bookId;
}
