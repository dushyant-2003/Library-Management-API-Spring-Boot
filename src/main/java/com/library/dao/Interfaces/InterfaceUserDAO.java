package com.library.dao.Interfaces;

import java.sql.SQLException;
import java.util.List;

import com.library.model.User;

public interface InterfaceUserDAO {
	User getUserByUserName(String username);
    User getUserByUserId(String userId);
    List<User> getAllUsers(String role, int pageNumber, int pageSize);
    boolean deleteUser(String userId) ;
    boolean addUser(User user);
    boolean updateUser(User user);
    List<User> getAllUsers(int pageNumber, int pageSize);
    public boolean updateUser(User user, String userId, String columnToUpdate); 
}
