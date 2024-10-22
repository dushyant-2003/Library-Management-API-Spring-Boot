package com.library.dao;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.library.constants.NotificationConstants;
import com.library.dao.Interfaces.InterfaceNotificationDAO;
import com.library.model.Notification;

@Repository
public class NotificationDAO extends GenericDAO<Notification> implements InterfaceNotificationDAO{

	public NotificationDAO() {
	}

	public boolean sendNotification(Notification notification) {
		String sendNotificationQuery = String.format(
				"insert into notification (notificationId, userId, type, message, date) values ('%s','%s','%s','%s','%s')",
				notification.getNotificationId(), notification.getUserId(), notification.getTitle(),
				notification.getMessage(), Date.valueOf(notification.getDate()));

		try {
			return executeUpdateQuery(sendNotificationQuery);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public List<Notification> readNotifications(String userId) {
		String readNotificationQuery = String.format("SELECT * FROM Notification WHERE userId = '%s'", userId);
		List<Notification> notificationList = new ArrayList<>();

		try {
			return executeGetAllQuery(readNotificationQuery);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return notificationList;
	}

	public boolean deleteNotification(String notificationId) {

		String deleteNotificationQuery = "DELETE FROM notification WHERE notificationId = \"" + notificationId + "\"";
		try {
			return executeUpdateQuery(deleteNotificationQuery);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected Notification mapResultSetToEntity(ResultSet resultSet) throws SQLException {
		// TODO Auto-generated method stub
		Notification notification = new Notification();
		notification.setNotificationId(resultSet.getString(NotificationConstants.NOTIFICATION_ID_COLUMN));
		notification.setUserId(resultSet.getString(NotificationConstants.USER_ID_COLUMN));
		notification.setTitle(resultSet.getString(NotificationConstants.TITLE_COLUMN));
		notification.setMessage(resultSet.getString(NotificationConstants.MESSAGE_COLUMN));
		notification.setDate(resultSet.getDate(NotificationConstants.DATE_COLUMN).toLocalDate());

		return notification;
	}
}
