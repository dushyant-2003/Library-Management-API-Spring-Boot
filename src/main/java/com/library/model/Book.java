package com.library.model;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Book {
	
	@Id
	private String bookId;
	
	@NotNull(message="Book name can't be null")
	private String name;
	
	@NotNull(message="Author name can't be null")
	private String author;
	
	private String publication;
	private String edition;
	
	@NotNull(message="Price can't be null")
	private double price;
	private String shelfLocation;
	
	private Status status;
	
	public Book() {
		
	}
	
	public Book(String bookId, String name, String author, String publication, String edition, double price,
			String shelfLocation, Status status) {
		super();
		this.bookId = bookId;
		this.name = name;
		this.author = author;
		this.publication = publication;
		this.edition = edition;
		this.price = price;
		this.shelfLocation = shelfLocation;
		this.status = status;
	}
	
}
