package com.library.dao;

import java.util.List;

import com.library.model.Notification;

public interface InterfaceNotificationDAO {
	public boolean sendNotification(Notification notification);
	public List<Notification> readNotifications(String userId);
	public boolean deleteNotification(String notificationId);
}
