package com.library.dao;

import com.library.constants.UserConstants;
import com.library.model.Gender;
import com.library.model.Role;
import com.library.model.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserDAOTest {

	@Mock
	private Connection mockConnection;

	@Mock
	private PreparedStatement mockPreparedStatement;

	@Mock
	private ResultSet mockResultSet;

	@InjectMocks
	private UserDAO userDAO;

	@BeforeEach
	void setUp() throws SQLException {
		MockitoAnnotations.openMocks(this);
		when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);
	}

	@Test
	public void testGetUserByUserName_Success() throws SQLException, ClassNotFoundException {
		String query = "SELECT * FROM User WHERE userName = \"testUser\"";

		when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
		when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

		when(mockResultSet.next()).thenReturn(true);
		when(mockResultSet.getString(UserConstants.USER_ID_COLUMN)).thenReturn("user123");
		when(mockResultSet.getString(UserConstants.NAME_COLUMN)).thenReturn("Test User");
		when(mockResultSet.getString(UserConstants.USERNAME_COLUMN)).thenReturn("testUser");
		when(mockResultSet.getString(UserConstants.ROLE_COLUMN)).thenReturn("ISSUER");
		when(mockResultSet.getString(UserConstants.GENDER_COLUMN)).thenReturn("MALE");
		when(mockResultSet.getDate(UserConstants.DATE_OF_BIRTH)).thenReturn(Date.valueOf(LocalDate.of(1990, 1, 1)));
		when(mockResultSet.getString(UserConstants.DEPARTMENT_COLUMN)).thenReturn("IT");
		when(mockResultSet.getString(UserConstants.CONTACT_NO_COLUMN)).thenReturn("1234567890");
		when(mockResultSet.getBigDecimal(UserConstants.FINE_COLUMN)).thenReturn(BigDecimal.ZERO);

		User user = userDAO.getUserByUserName("testUser");

		// Assertions
		assertNotNull(user);
		assertEquals("user123", user.getUserId());
		assertEquals("Test User", user.getName());
		assertEquals("testUser", user.getUserName());
	}

	
	@Test
	public void testGetUserByUserName_Failure() throws SQLException, ClassNotFoundException {
		String query = "SELECT * FROM User WHERE userName = \"unknownUser\"";

		when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
		when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

		// Simulate ResultSet behavior
		when(mockResultSet.next()).thenReturn(false); // No data found

		User user = userDAO.getUserByUserName("unknownUser");

		// Assertions
		assertNull(user);
	}

	// Success case for addUser
	@Test
	public void testAddUser_Success() throws SQLException, ClassNotFoundException {
		User user = new User();
		user.setUserId("user123");
		user.setName("Test User");
		user.setUserName("testUser");
		user.setRole(Role.ISSUER);
		user.setGender(Gender.MALE);
		user.setDateOfBirth(LocalDate.of(1990, 1, 1));
		user.setDepartment("IT");
		user.setContactNumber("1234567890");
		user.setFine(BigDecimal.ZERO);
		user.setPassword("hashedPassword");

		String sqlQuery = String.format(
				"INSERT INTO User (userId, name, username, role, gender, dateOfBirth, department, designation, contactNumber, email, address, bookIssueLimit, password) VALUES ('%s','%s','%s','%s','%s','%s','%s', '%s','%s','%s','%s', '%s','%s')",
				user.getUserId(), user.getName(), user.getUserName(), user.getRole().toString(),
				user.getGender().toString(), java.sql.Date.valueOf(user.getDateOfBirth()), user.getDepartment(),
				user.getDesignation(), user.getContactNumber(), user.getEmail(), user.getAddress(),
				user.getBookIssueLimit(), user.getPassword());

		when(mockConnection.prepareStatement(sqlQuery)).thenReturn(mockPreparedStatement);
		when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Simulating successful insert

		boolean result = userDAO.addUser(user);

		assertTrue(result);
	}
	
	@Test
	public void testAddUser_Failure() throws SQLException, ClassNotFoundException {
		User user = new User();
		user.setUserId("user123");
		user.setName("Test User");
		user.setUserName("testUser");
		user.setRole(Role.ISSUER);
		user.setGender(Gender.MALE);
		user.setDateOfBirth(LocalDate.of(1990, 1, 1));
		user.setDepartment("IT");
		user.setContactNumber("1234567890");
		user.setFine(BigDecimal.ZERO);
		user.setPassword("hashedPassword");

		String sqlQuery = String.format(
				"INSERT INTO User (userId, name, username, role, gender, dateOfBirth, department, designation, contactNumber, email, address, bookIssueLimit, password) VALUES ('%s','%s','%s','%s','%s','%s','%s', '%s','%s','%s','%s', '%s','%s')",
				user.getUserId(), "", "", "", "", "", "", "", "", "", "", "", "");

		when(mockConnection.prepareStatement(sqlQuery)).thenReturn(mockPreparedStatement);
		when(mockPreparedStatement.executeUpdate()).thenReturn(0); // Simulating failure insert

		boolean result = userDAO.addUser(user);

		// Assertions
		assertFalse(result);
	}

	@Test
	void testGetAllUserByRole() throws SQLException, ClassNotFoundException {
		String role = "ISSUER";
		String sqlQuery = "SELECT * FROM user where role = \"" + role + "\"";

		when(mockConnection.prepareStatement(sqlQuery)).thenReturn(mockPreparedStatement);
		when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

		mockResultSetFunction();

		// Call the method under test
		List<User> users = userDAO.getAllUser(role);

		// Assert that the result is correct
		assertEquals(1, users.size());
		User user = users.get(0);
		assertEquals("1", user.getUserId());
		assertEquals("test user name", user.getUserName());
		assertEquals(role, user.getRole().toString());

		// Verify that the query was executed
		verify(mockConnection).prepareStatement(sqlQuery);
		verify(mockPreparedStatement).executeQuery();
		verify(mockResultSet, times(2)).next(); // first true, then false
	}

	@Test
	void testGetAllUser() throws SQLException, ClassNotFoundException {
		String sqlQuery = "SELECT * FROM user";
		when(mockConnection.prepareStatement(sqlQuery)).thenReturn(mockPreparedStatement);
		when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

		mockResultSetFunction();

		// Call the method under test
		List<User> users = userDAO.getAll();

		assertEquals(1, users.size());
	
		verify(mockConnection).prepareStatement(sqlQuery);
		verify(mockPreparedStatement).executeQuery();
		verify(mockResultSet, times(2)).next(); 
		
	}
	public void mockResultSetFunction() throws SQLException {
		when(mockResultSet.next()).thenReturn(true).thenReturn(false); // Mock two calls, first returns true, then false
		when(mockResultSet.getString(UserConstants.USER_ID_COLUMN)).thenReturn("1");
		when(mockResultSet.getString(UserConstants.NAME_COLUMN)).thenReturn("John Doe");
		when(mockResultSet.getString(UserConstants.USERNAME_COLUMN)).thenReturn("test user name");
		when(mockResultSet.getString(UserConstants.ROLE_COLUMN)).thenReturn("ISSUER");
		when(mockResultSet.getString(UserConstants.GENDER_COLUMN)).thenReturn("MALE");
		when(mockResultSet.getDate(UserConstants.DATE_OF_BIRTH)).thenReturn(Date.valueOf(LocalDate.now()));
		when(mockResultSet.getString(UserConstants.DEPARTMENT_COLUMN)).thenReturn("CS");
		when(mockResultSet.getString(UserConstants.DESIGNATION_COLUMN)).thenReturn("student");
		when(mockResultSet.getString(UserConstants.CONTACT_NO_COLUMN)).thenReturn("0000000000");
		when(mockResultSet.getString(UserConstants.EMAIL_COLUMN)).thenReturn("abc@gmail.com");
		when(mockResultSet.getString(UserConstants.ADDRESS_COLUMN)).thenReturn("testcity");
		when(mockResultSet.getInt(UserConstants.BOOK_ISSUE_LIMIT_COLUMN)).thenReturn(3);
		when(mockResultSet.getString(UserConstants.PASSWORD_COLUMN)).thenReturn("HashedPassword");
	}
	@Test
	void testDeleteUserSuccess() throws SQLException, ClassNotFoundException {
		String userId = "1";
		String sqlQuery = String.format("DELETE FROM notification WHERE notificationId = \"%s\"", userId);

		when(mockConnection.prepareStatement(sqlQuery)).thenReturn(mockPreparedStatement);
		when(mockPreparedStatement.executeUpdate()).thenReturn(1);

		boolean result = userDAO.deleteUser(userId);

		assertTrue(result);
		
	}
	@Test
    public void testUpdateStatus_Success() throws SQLException, ClassNotFoundException {
        // Arrange
		User user = new User();
		user.setUserId("user123");
		user.setName("testname");
		String columnToUpdate = "name";
		
		when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean result = userDAO.updateUser(user,user.getUserId(), columnToUpdate);

        
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }
}
