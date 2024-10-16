package com.library.dao;

import com.library.constants.NotificationConstants;
import com.library.model.Notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationDAOTest {

	@Mock
	private Connection mockConnection;

	@Mock
	private PreparedStatement mockPreparedStatement;

	@Mock
	private ResultSet mockResultSet;

	@InjectMocks
	private NotificationDAO notificationDAO;

	@BeforeEach
	void setUp() throws SQLException {
		MockitoAnnotations.openMocks(this);
		when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);
	}

	@Test
	void testSendNotification() throws SQLException, ClassNotFoundException {
		Notification notification = new Notification();
		notification.setNotificationId("1");
		notification.setUserId("user1");
		notification.setTitle("Test Title");
		notification.setMessage("Test Message");
		notification.setDate(java.time.LocalDate.now());

		String sqlQuery = String.format(
				"INSERT INTO notification (notificationId, userId, type, message, date) VALUES ('%s','%s','%s','%s','%s')",
				notification.getNotificationId(), notification.getUserId(), notification.getTitle(),
				notification.getMessage(), java.sql.Date.valueOf(notification.getDate()));

		when(mockConnection.prepareStatement(sqlQuery)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Simulating successful insert


		boolean result = notificationDAO.sendNotification(notification);

		assertTrue(result);
		verify(mockPreparedStatement, times(1)).executeUpdate();
	}

	@Test
	void testReadNotifications() throws SQLException, ClassNotFoundException {
		String userId = "user1";
		String sqlQuery = String.format("SELECT * FROM Notification WHERE userId = '%s'", userId);
		
		when(mockConnection.prepareStatement(sqlQuery)).thenReturn(mockPreparedStatement);
		when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
	
		
		when(mockResultSet.next()).thenReturn(true).thenReturn(false);
		when(mockResultSet.getString(NotificationConstants.NOTIFICATION_ID_COLUMN)).thenReturn("1");
		when(mockResultSet.getString(NotificationConstants.USER_ID_COLUMN)).thenReturn(userId);
		when(mockResultSet.getString(NotificationConstants.TITLE_COLUMN)).thenReturn("Test Title");
		when(mockResultSet.getString(NotificationConstants.MESSAGE_COLUMN)).thenReturn("Test Message");
		when(mockResultSet.getDate(NotificationConstants.DATE_COLUMN))
				.thenReturn(java.sql.Date.valueOf(java.time.LocalDate.now()));
		

		List<Notification> notifications = notificationDAO.readNotifications(userId);

		assertEquals(1, notifications.size());
		Notification notification = notifications.get(0);
		assertEquals("1", notification.getNotificationId());
		assertEquals(userId, notification.getUserId());
		assertEquals("Test Title", notification.getTitle());
		assertEquals("Test Message", notification.getMessage());
	}

	@Test
	void testDeleteNotification() throws SQLException, ClassNotFoundException {
		String notificationId = "1";
		String sqlQuery = String.format("DELETE FROM notification WHERE notificationId = \"%s\"", notificationId);

		when(mockConnection.prepareStatement(sqlQuery)).thenReturn(mockPreparedStatement);
		when(mockPreparedStatement.executeUpdate()).thenReturn(1);

		boolean result = notificationDAO.deleteNotification(notificationId);

		assertTrue(result);
		
	}
}
