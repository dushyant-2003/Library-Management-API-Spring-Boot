package com.library.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
	private String notificationId;
	private String userId;
	private String title;
	private String message;
	private LocalDate date;
}
