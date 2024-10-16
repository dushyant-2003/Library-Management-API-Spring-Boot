package com.library.service;

import com.library.dao.BookDAO;
import com.library.dao.BookIssueDAO;
import com.library.dao.InterfaceBookDAO;
import com.library.dao.InterfaceBookIssueDAO;
import com.library.dao.InterfaceNotificationDAO;
import com.library.dao.InterfaceUserDAO;
import com.library.dao.NotificationDAO;
import com.library.dao.UserDAO;
import com.library.model.Notification;
import com.library.model.Role;
import com.library.model.User;
import com.library.service.UserService;
import com.library.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

	@Mock
	private InterfaceUserDAO userDAO;

	@Mock
	private InterfaceNotificationDAO notificationDAO;

	@Mock
	private InterfaceBookDAO bookDAO;

	@Mock
	private InterfaceBookIssueDAO bookIssueDAO;
	
	@Mock
	private Logger logger;

	@InjectMocks
	private UserService userService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		// Inject the mock logger
		userService.logger = logger;
		
	}

	@Test
	public void testAuthenticateUser_Success() {
		// Given
		String username = "testUser";
		String password = "testPassword";
		User user = new User();
		user.setUserId("1");
		user.setUserName(username);
		user.setPassword(PasswordUtil.hashPassword(password));

		when(userDAO.getUserByUserName(username)).thenReturn(user);
		when(bookIssueDAO.getIssuedBook(user.getUserId())).thenReturn(new ArrayList<>());

		// When
		User authenticatedUser = userService.authenticateUser(username, password);

		// Then
		assertNotNull(authenticatedUser);
		assertEquals(username, authenticatedUser.getUserName());
		verify(notificationDAO, never()).sendNotification(any(Notification.class));
		verify(bookIssueDAO,times(1)).getIssuedBook(user.getUserId());
	}

	@Test
	public void testAuthenticateUser_Failure() {
		// Given
		String username = "testUser";
		String password = "wrongPassword";
		User user = new User();
		user.setUserName(username);
		user.setPassword(PasswordUtil.hashPassword("correctPassword"));

		when(userDAO.getUserByUserName(username)).thenReturn(user);

		// When
		User authenticatedUser = userService.authenticateUser(username, password);

		// Then
		assertNull(authenticatedUser);
		verify(notificationDAO, never()).sendNotification(any(Notification.class));
	}

	@Test
	public void testAddUser_Success() {
		// Given
		User user = new User();
		user.setUserId("1");
		user.setEmail("test@gmail.com");
		user.setRole(Role.valueOf("ISSUER"));
		user.setUserName("Test username");
		user.setName("Test User");
		user.setPassword("testPwd");
		when(userDAO.addUser(user)).thenReturn(true);

		// When
		boolean result = userService.addUser(user);

		// Then
		assertTrue(result);
		verify(notificationDAO).sendNotification(any(Notification.class));
	}

	@Test
	public void testDeleteUser_Success() throws Exception {
		// Given
		String userId = "1";
		User user = new User();
		user.setRole(Role.valueOf("ISSUER"));

		when(userDAO.getUserByUserId(userId)).thenReturn(user);
		when(userDAO.deleteUser(userId)).thenReturn(true);

		// When
		boolean result = userService.deleteUser(userId);

		// Then
		assertTrue(result);
	}

	@Test
	public void testDeleteUser_Failure_UserNotFound() throws Exception {

		String userId = "1";
		User user = new User();
		user.setUserId("user123");
		user.setRole(Role.ADMIN);
		when(userDAO.getUserByUserId(userId)).thenReturn(user);

		boolean result = userService.deleteUser(userId);

		assertFalse(result);
	}

	@Test
	public void testDeleteUser_Failure_AdminDelete() throws Exception {

		String userId = "1";
		when(userDAO.getUserByUserId(userId)).thenReturn(null);

		boolean result = userService.deleteUser(userId);

		assertFalse(result);
	}
	@Test
	public void testPayFine_Success() {
		// Given
		User user = new User();
		user.setUserId("1");
		user.setName("Test User");
		user.setFine(new BigDecimal("100"));

		when(userDAO.updateUser(user, user.getUserId(), "fine")).thenReturn(true);

		// When
		userService.payFine(user);

		// Then
		assertEquals(BigDecimal.ZERO, user.getFine());
		verify(userDAO).updateUser(user, user.getUserId(), "fine");
		verify(logger).info("Fine paid successfully of user " + user.getName());
		verifyNoMoreInteractions(logger);
	}

	@Test
	public void testPayFine_NoPendingDues() {
		// Given
		User user = new User();
		user.setUserId("1");
		user.setName("Test User");
		user.setFine(BigDecimal.ZERO);

		// When
		userService.payFine(user);

		// Then
		assertEquals(BigDecimal.ZERO, user.getFine());
		verifyNoInteractions(userDAO);

		verifyNoMoreInteractions(logger);
	}

	@Test
	public void testPayFine_FailureToUpdate() {
		// Given
		User user = new User();
		user.setUserId("1");
		user.setName("Test User");
		user.setFine(new BigDecimal("100"));

		when(userDAO.updateUser(user, user.getUserId(), "fine")).thenReturn(false);

		// When
		userService.payFine(user);

		// Then
		assertEquals(BigDecimal.ZERO, user.getFine());
		verify(userDAO).updateUser(user, user.getUserId(), "fine");
		
	}

}