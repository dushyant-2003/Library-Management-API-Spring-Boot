package com.library.service.Interfaces;

import java.util.List;

import com.library.model.User;
import com.library.requestDTO.UserUpdateDTO;
import com.library.responseDTO.UserResponseDTO;

public interface InterfaceUserService {
	
	public List<User> getAllUsers(String role,int pageNumber, int pageSize);
	public List<User> getAllUsers(int pageNumber, int pageSize);
	public boolean deleteUser(String userId);
	public UserResponseDTO addUser(User user);
	public User getUserByUserName(String userName);
	public boolean payFine(User user);
	public boolean updateUser(String userId, UserUpdateDTO userUpdateDTO);
	public boolean updateUser(User user, String userId, String columnToUpdate);
	boolean payFine(String userId);
}
