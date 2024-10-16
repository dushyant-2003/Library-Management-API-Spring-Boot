package com.library.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import com.library.constants.StringConstants;
import com.library.dao.NotificationDAO;
import com.library.model.Notification;
import com.library.service.NotificationService;

public class NotificationServiceTest {

    @Mock
    private NotificationDAO notificationDAO;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    

    @Test
    public void testReadNotifications() {
        // Given
        String userId = "user1";
        List<Notification> expectedNotifications = new ArrayList<>();
        expectedNotifications.add(new Notification("1", userId, "title1", "message1", null));
        expectedNotifications.add(new Notification("2", userId, "title2", "message2", null));

        when(notificationDAO.readNotifications(userId)).thenReturn(expectedNotifications);

        // When
        List<Notification> notifications = notificationService.readNotifications(userId);

        // Then
        assertNotNull(notifications);
        assertEquals(2, notifications.size());
        assertEquals(expectedNotifications, notifications);
        verify(notificationDAO).readNotifications(userId);
    }

    @Test
    public void testDeleteNotification_Success() throws SQLException {
        // Given
        String notificationId = "1";
        when(notificationDAO.deleteNotification(notificationId)).thenReturn(true);

        // When
        boolean result = notificationService.deleteNotification(notificationId);

        // Then
        assertTrue(result);
        verify(notificationDAO).deleteNotification(notificationId);
    }

    @Test
    public void testDeleteNotification_Failure() throws SQLException {
        // Given
        String notificationId = "1";
        when(notificationDAO.deleteNotification(notificationId)).thenThrow(new SQLException());

        // When
        boolean result = notificationService.deleteNotification(notificationId);

        // Then
        assertFalse(result);
        verify(notificationDAO).deleteNotification(notificationId);
    }
    
    @Test
    public void testSendNotification_Success() throws Exception {
        // Given
        Notification notification = new Notification("1", "user1", "title", "message", null);
        when(notificationDAO.sendNotification(notification)).thenReturn(true);

        // When
        String output = SystemLambda.tapSystemOut(() -> notificationService.sendNotification(notification));

        // Then
        assertEquals(StringConstants.NOTIFICATION_SENT_SUCCESSFULLY, output.trim());
    }

   
}
