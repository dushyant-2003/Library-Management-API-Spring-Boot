package com.library.responseDTO;

import lombok.Data;

@Data
public class BookResponseDTO {
	private String bookId;
	private String name;
	private double price;

	public BookResponseDTO(String bookId, String name, double price) {
		this.bookId = bookId;
		this.name = name; 
		this.price = price;
	}
	
	
}
