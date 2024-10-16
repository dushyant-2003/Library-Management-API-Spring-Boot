package com.library.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.library.constants.StringConstants;
import com.library.dao.InterfaceNotificationDAO;
import com.library.dao.InterfaceUserDAO;
import com.library.exception.UserNotFoundException;
import com.library.model.Notification;
import com.library.model.User;
import com.library.responseDTO.ApiResponse;
import com.library.responseDTO.Status;
import com.library.responseDTO.UserResponseDTO;
import com.library.util.IdGenerator;
import com.library.util.LoggingUtil;
import com.library.util.PasswordUtil;

@Service
public class UserService {

	private InterfaceUserDAO userDAO;
	private InterfaceNotificationDAO notificationDAO;
	public static Logger logger = LoggingUtil.getLogger(UserService.class);

	@Value("${book.issue.limit}")
	private int bookIssueLimit;
	
	@Autowired
	public UserService(InterfaceUserDAO userDAO, InterfaceNotificationDAO notificationDAO) {
		this.userDAO = userDAO;
		this.notificationDAO = notificationDAO;
	}

	public List<User> getAllUsers(String role,int pageNumber, int pageSize) {

		if (StringConstants.STAFF_ROLE.equalsIgnoreCase(role) || StringConstants.ISSUER_ROLE.equalsIgnoreCase(role)) {
			List<User> list = userDAO.getAllUsers(role,pageNumber, pageSize);
			return list;
		}

		return null;
	}

	public List<User> getAllUsers(int pageNumber, int pageSize) {

		return userDAO.getAllUsers(pageNumber, pageSize);
	}

	public boolean deleteUser(String userId) {

		User user = null;
		
		user = userDAO.getUserByUserId(userId);
		
		if (user == null|| user.getRole().toString().equalsIgnoreCase(StringConstants.ADMIN_ROLE)) {
			throw new UserNotFoundException(userId); 
		}
		
		return userDAO.deleteUser(userId);
	}

	public UserResponseDTO addUser(User user) {

		String userId = IdGenerator.generateUserId();
		user.setUserId(userId);

		String password = user.getPassword();
		String hashedPassword = PasswordUtil.hashPassword(password);
		user.setPassword(hashedPassword);

		user.setFine(new BigDecimal(0.0));
		user.setBookIssueLimit(bookIssueLimit);

		UserResponseDTO responseDTO = null;

		boolean userAddStatus = userDAO.addUser(user);
		if (!userAddStatus) {
			return null;
		}

		responseDTO = new UserResponseDTO(user.getUserId(), user.getUserName(), user.getName(),
				user.getEmail());

		String notificationId = IdGenerator.generateNotificationId();

		String title = "Welcome";
		String message = "Hi " + user.getName();
		LocalDate todayDate = LocalDate.now();
		Notification welcomeMessage = new Notification(notificationId, user.getUserId(), title, message, todayDate);
		notificationDAO.sendNotification(welcomeMessage);
		return responseDTO;

	}
//	
	public User getUserByUserName(String userName) {
		User user =  userDAO.getUserByUserName(userName);
		
		if(user == null) {
			throw new UserNotFoundException(userName);
		}
		return user;
	}

	public boolean updateUser(User user, String userId, String columnToUpdate) {

		return userDAO.updateUser(user, userId, columnToUpdate);

	}

	public boolean payFine(User user) {
		BigDecimal fine = user.getFine();
		BigDecimal zeroValue = new BigDecimal("0");

		if (fine.compareTo(zeroValue) == 0) {
			System.out.println("You have no pending dues");
			return false;
		}
		System.out.println("Your fine is Rs. " + fine.toString());

		user.setFine(zeroValue);

		boolean fineUpdateStatus = updateUser(user, user.getUserId(), "fine");
		if (fineUpdateStatus) {
			logger.info("Fine paid successfully of user " + user.getName());
			return true;
		} else {
			logger.severe("Failed to pay fine for the user " + user.getName());
			return false;
		}
	}
	

	public void updateUser() {

	}
}
