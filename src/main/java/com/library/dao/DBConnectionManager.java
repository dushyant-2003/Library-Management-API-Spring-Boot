package com.library.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.library.constants.DatabaseConstants;

public class DBConnectionManager {

	private static Connection connection;

	public static Connection getConnection() throws ClassNotFoundException {
		if (connection == null) {
			try {
				Class.forName(DatabaseConstants.JDBC_DRIVER);
				connection = DriverManager.getConnection(DatabaseConstants.JDBC_CONNECTION_URL,
						DatabaseConstants.DB_USERNAME, DatabaseConstants.DB_PASSWORD);
			} catch (SQLException e) {
				System.out.println(DatabaseConstants.FAILED_DB_CONNECTION);
			}
		}
		return connection;
	}
}
