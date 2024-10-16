package com.library.responseDTO;


import lombok.Data;

@Data
public class UserResponseDTO {
	private String userId;
	private String userName;
	private String name;
	private String email;
	public UserResponseDTO(String userId, String userName, String name, String email) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.name = name;
		this.email = email;
	}
	
}
