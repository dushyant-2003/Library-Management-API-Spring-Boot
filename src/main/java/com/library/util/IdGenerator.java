package com.library.util;

import java.util.UUID;

import com.library.constants.StringConstants;

public class IdGenerator {
	public static String generateNotificationId() {
		String randomString = UUID.randomUUID().toString();
		int desiredLength = StringConstants.ID_LENGTH;
		if (desiredLength > randomString.length()) {
			desiredLength = randomString.length();
		}
		String notificationId = randomString.substring(0, desiredLength);
		notificationId = 'N' + notificationId;
		return notificationId;
	}

	public static String generateBookId() {
		String randomString = UUID.randomUUID().toString();
		int desiredLength = StringConstants.ID_LENGTH;
		if (desiredLength > randomString.length()) {
			desiredLength = randomString.length();
		}
		String bookId = randomString.substring(0, desiredLength);
		bookId = 'B' + bookId;
		return bookId;
	}

	public static String generateUserId() {
		String randomString = UUID.randomUUID().toString();
		int desiredLength = StringConstants.ID_LENGTH;
		if (desiredLength > randomString.length()) {

			desiredLength = randomString.length();
		}
		String userId = randomString.substring(0, desiredLength);
		userId = 'U' + userId;
		return userId;
	}

	public static String generateIssueId() {
		// TODO Auto-generated method stub
		String randomString = UUID.randomUUID().toString();
		int desiredLength = StringConstants.ID_LENGTH;
		if (desiredLength > randomString.length()) {

			desiredLength = randomString.length();
		}
		String issueId = randomString.substring(0, desiredLength);
		issueId = 'I' + issueId;
		return issueId;
	}
}
