package com.library.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.library.model.Notification;
import com.library.requestDTO.SendNotificationRequestDTO;
import com.library.responseDTO.ApiResponse;
import com.library.responseDTO.Status;
import com.library.service.NotificationService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/api")
public class NotificationController {

	private NotificationService notificationService;

	public NotificationController() {

	}

	@Autowired
	public NotificationController(NotificationService notificationService) {
		this.notificationService = notificationService;

	}

	@PostMapping("/notifications")
	public ResponseEntity<ApiResponse<String>> sendNotification(
			@Valid @RequestBody SendNotificationRequestDTO notificationDTO) {

		boolean sendStatus = notificationService.sendNotification(notificationDTO);
		if (sendStatus) {
			ApiResponse<String> successResponse = ApiResponse.<String>builder().status(Status.SUCCESS)
					.message("Notification sent successfully").build();
			return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
		}
		ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(Status.ERROR)
				.message("Notification not sent").build();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);

	}

	@GetMapping("/users/{userName}/notifications")
	public ResponseEntity<ApiResponse<List<Notification>>> readNotifications(@PathVariable String userName) {

		List<Notification> notificationList = notificationService.readNotifications(userName);
		ApiResponse<List<Notification>> response = ApiResponse.<List<Notification>>builder().status(Status.SUCCESS)
				.message("Notification retreived successfully").data(notificationList).build();
		return ResponseEntity.status(HttpStatus.OK).body(response);

	}

	public boolean deleteNotification(String notificationId) {

		return notificationService.deleteNotification(notificationId);

	}

}
