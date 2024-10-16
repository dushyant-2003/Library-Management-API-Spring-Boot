package com.library.constants;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.library.model.Notification;
import com.library.model.User;
import com.library.responseDTO.UserResponseDTO;
import com.library.util.IdGenerator;
import com.library.util.PasswordUtil;

public class BookConstants {
	public static final String BOOK_TABLE_NAME = "Book";
	public static final String BOOK_ID_COLUMN = "bookId";
	public static final String NAME_COLUMN = "name";
	public static final String AUTHOR_COLUMN = "author";
	public static final String PUBLICATION_COLUMN = "publication";
	public static final String EDITION_COLUMN = "edition";
	public static final String PRICE_COLUMN = "price";
	public static final String SHELF_COLUMN = "shelfLocation";
	public static final String STATUS_COLUMN = "status";
	
}
