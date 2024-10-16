package com.library.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User {
	
	@Id
	private String userId;
	
	@NotNull(message="Name cannot be null")
	private String name;
	
	@NotNull(message = "User name cannot be null")
    @Size(min = 4, message = "User name must be at least 4 characters long")
	private String userName;
	
	@NotNull(message = "Role cannot be null")
    @Enumerated(EnumType.STRING)
	private Role role;
	
	@NotNull(message = "Gender cannot be null")
    @Enumerated(EnumType.STRING)
	private Gender gender;
	
	@Past(message = "Date of birth must be in the past") 
	private LocalDate dateOfBirth;
	
	private String department;
	private String designation;
	
	@NotNull(message = "Contact number cannot be null")
    @Size(min = 10, max = 10, message = "Contact number must be exactly 10 digits long")  
	private String contactNumber;
	
	@Email
	private String email;
	
	private String address;
	private int bookIssueLimit;
	private BigDecimal fine;
	
	@NotNull(message = "Password cannot be null")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", 
             message = "Password must contain at least one uppercase letter, one digit, and one special character")
	private String password;

	public User() {

	}

	public User(String userId, String name, String userName, Role role, Gender gender, LocalDate dateOfBirth,
			String department, String designation, String contactNumber, String email, String address,
			int bookIssueLimit, BigDecimal fine, String password) {

		this.userId = userId;
		this.name = name;
		this.userName = userName;
		this.role = role;
		this.gender = gender;
		this.dateOfBirth = dateOfBirth;
		this.department = department;
		this.designation = designation;
		this.contactNumber = contactNumber;
		this.email = email;
		this.address = address;
		this.bookIssueLimit = bookIssueLimit;
		this.fine = fine;
		this.password = password;
	}

}
