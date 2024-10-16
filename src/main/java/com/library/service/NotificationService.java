package com.library.service;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.dao.InterfaceNotificationDAO;
import com.library.dao.InterfaceUserDAO;
import com.library.exception.UserNotFoundException;
import com.library.model.Notification;
import com.library.model.User;
import com.library.requestDTO.SendNotificationRequestDTO;
import com.library.util.IdGenerator;
import com.library.util.LoggingUtil;

@Service
public class NotificationService {

	private static final Logger logger = LoggingUtil.getLogger(NotificationService.class);

	private InterfaceNotificationDAO notificationDAO;

	private InterfaceUserDAO userDAO;

	@Autowired
	public NotificationService(InterfaceNotificationDAO notificationDAO, InterfaceUserDAO userDAO) {
		this.notificationDAO = notificationDAO;
		this.userDAO = userDAO;
	}

	public boolean sendNotification(SendNotificationRequestDTO notificationDTO) {

		String userName = notificationDTO.getUserName();
		User user = userDAO.getUserByUserName(userName);

		if (user == null) {
			throw new UserNotFoundException(userName);
		}

		String notificationId = IdGenerator.generateNotificationId();

		Notification notification = new Notification(notificationId, user.getUserId(), notificationDTO.getTitle(),
				notificationDTO.getMessage(), LocalDate.now());

		boolean sendStatus = notificationDAO.sendNotification(notification);
		if (sendStatus) {
			logger.log(Level.INFO, "Notification sent successfully to user: {0}", notification.getUserId());
			return true;
		} else {
			logger.log(Level.WARNING, "Failed to send notification to user: {0}", notification.getUserId());
		}
		return false;
	}

	public List<Notification> readNotifications(String userId) {
		
		User user = userDAO.getUserByUserName(userId);

		if (user == null) {
			throw new UserNotFoundException(userId);
		}
		
		List<Notification> notificationList = notificationDAO.readNotifications(user.getUserId());
		
		return notificationList;
	}

	public boolean deleteNotification(String notificationId) {
		logger.log(Level.INFO, "Attempting to delete notification with ID: {0}", notificationId);

		boolean deleteStatus = false;
		deleteStatus = notificationDAO.deleteNotification(notificationId);
		if (deleteStatus) {
			logger.log(Level.INFO, "Notification with ID: {0} deleted successfully", notificationId);
		} else {
			logger.log(Level.WARNING, "Failed to delete notification with ID: {0}", notificationId);
		}
		return deleteStatus;
	}
}
