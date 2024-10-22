package com.library.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.library.constants.StringConstants;
import com.library.dao.Interfaces.InterfaceNotificationDAO;
import com.library.dao.Interfaces.InterfaceUserDAO;
import com.library.exception.GeneralException;
import com.library.exception.UserNotFoundException;
import com.library.model.Notification;
import com.library.model.User;
import com.library.requestDTO.UserUpdateDTO;
import com.library.responseDTO.ApiResponse;
import com.library.responseDTO.Status;
import com.library.responseDTO.UserResponseDTO;
import com.library.service.Interfaces.InterfaceUserService;
import com.library.util.IdGenerator;
import com.library.util.LoggingUtil;
import com.library.util.PasswordUtil;

@Service
public class UserService implements InterfaceUserService {

	private InterfaceUserDAO userDAO;
	private InterfaceNotificationDAO notificationDAO;
	public static Logger logger = LoggingUtil.getLogger(UserService.class);
	
	@Autowired
	private PasswordEncoder encoder;

	@Value("${book.issue.limit}")
	private int bookIssueLimit;

	@Autowired
	public UserService(InterfaceUserDAO userDAO, InterfaceNotificationDAO notificationDAO) {
		this.userDAO = userDAO;
		this.notificationDAO = notificationDAO;
	}

	@Override
	public List<User> getAllUsers(String role, int pageNumber, int pageSize) {

		if (StringConstants.STAFF_ROLE.equalsIgnoreCase(role) || StringConstants.ISSUER_ROLE.equalsIgnoreCase(role)) {
			List<User> list = userDAO.getAllUsers(role, pageNumber, pageSize);
			return list;
		}

		return null;
	}

	@Override
	public List<User> getAllUsers(int pageNumber, int pageSize) {

		return userDAO.getAllUsers(pageNumber, pageSize);
	}

	@Override
	public boolean deleteUser(String userId) {

		User user = null;

		user = userDAO.getUserByUserId(userId);

		if (user == null || user.getRole().toString().equalsIgnoreCase(StringConstants.ADMIN_ROLE)) {
			throw new UserNotFoundException(userId);
		}

		return userDAO.deleteUser(userId);
	}

	@Override
	public UserResponseDTO addUser(User user) {

		String userId = IdGenerator.generateUserId();
		user.setUserId(userId);

		String password = user.getPassword();
		String hashedPassword = PasswordUtil.hashPassword(password);
		user.setPassword(hashedPassword);

		user.setFine(new BigDecimal(0.0));
		user.setBookIssueLimit(bookIssueLimit);

		UserResponseDTO responseDTO = null;

		if (user.getRole().toString().equalsIgnoreCase("ADMIN")) {
			throw new GeneralException("ADMIN cannot be added");
		}

		boolean userAddStatus = userDAO.addUser(user);
		if (!userAddStatus) {
			return null;
		}

		responseDTO = new UserResponseDTO(user.getUserId(), user.getUserName(), user.getName(), user.getEmail());

		String notificationId = IdGenerator.generateNotificationId();

		String title = "Welcome";
		String message = "Hi " + user.getName();
		LocalDate todayDate = LocalDate.now();
		Notification welcomeMessage = new Notification(notificationId, user.getUserId(), title, message, todayDate);
		notificationDAO.sendNotification(welcomeMessage);
		return responseDTO;

	}

	@Override
	public User getUserByUserName(String userName) {
		User user = userDAO.getUserByUserName(userName);

		if (user == null) {
			throw new UserNotFoundException(userName);
		}
		return user;
	}

	@Override
	public boolean payFine(User user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateUser(String userId, UserUpdateDTO userUpdateDTO) {

		User user = userDAO.getUserByUserId(userId);

		if (user == null) {
			throw new UserNotFoundException(userId);
		}

		if (userUpdateDTO.getName() != null) {
			user.setName(userUpdateDTO.getName());
		}
		if (userUpdateDTO.getEmail() != null) {
			user.setEmail(userUpdateDTO.getEmail());
		}
		if (userUpdateDTO.getContactNumber() != null) {
			user.setContactNumber(userUpdateDTO.getContactNumber());
		}
		if (userUpdateDTO.getDepartment() != null) {
			user.setDepartment(userUpdateDTO.getDepartment());
		}
		if (userUpdateDTO.getDesignation() != null) {
			user.setDesignation(userUpdateDTO.getDesignation());
		}
		if (userUpdateDTO.getPassword() != null) {
			user.setPassword(PasswordUtil.hashPassword(userUpdateDTO.getPassword()));
		}
			
		return userDAO.updateUser(user);
	}
	
	@Override
	public boolean updateUser(User user, String userId, String columnToUpdate) {

		return userDAO.updateUser(user, userId, columnToUpdate);

	}

	@Override
	public boolean payFine(String userId) {
		
		User user = userDAO.getUserByUserId(userId);
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

}
