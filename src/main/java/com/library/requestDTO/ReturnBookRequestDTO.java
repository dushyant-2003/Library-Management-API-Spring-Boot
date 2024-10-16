package com.library.requestDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReturnBookRequestDTO {
	@NotNull(message="User Id can't be null")
	String userId;
	
	@NotNull(message="Book Id can't be null")
	String bookId;
	
	boolean bookLostStatus;
}
