package com.library.requestDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {

	
	private String name;


	@Email
	private String email;
	
	@Size(min = 10, max = 10, message = "Contact number must be exactly 10 digits long")
	@Pattern(regexp = "^\\d{10}$", message = "Enter valid phone number")
	private String contactNumber;

	private String department;
	private String designation;
	
	@Size(min = 8, message = "Password must be at least 8 characters long")
	@Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "Password must contain at least one uppercase letter, one digit, and one special character")
	private String password;
	
	

}
