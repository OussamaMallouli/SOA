package iit.project.config;

import java.sql.Connection;
import java.sql.DriverManager;

import java.util.Properties;
import java.io.InputStream;

public class DBConnection {

	private static Connection connection;

	// Load connection parameters from the properties file
	public static Connection getConnection() {
		if (connection == null) {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				System.out.println("MySQL JDBC Driver not found");
				e.printStackTrace();
			}

			try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
				Properties properties = new Properties();
				if (input == null) {
					System.out.println("Sorry, unable to find db.properties");
					return null;
				}
				properties.load(input);

				String url = properties.getProperty("db.url");
				String user = properties.getProperty("db.user");
				String password = properties.getProperty("db.password");

				// Initialize connection
				connection = DriverManager.getConnection(url, user, password);
				System.out.println("Database connected!");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return connection;
	}
}
