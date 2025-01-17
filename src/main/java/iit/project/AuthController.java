package iit.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.mindrot.jbcrypt.BCrypt;
import iit.project.config.DBConnection;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("auth")
public class AuthController {

	private static Connection conn = DBConnection.getConnection();

	private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

	private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

	private boolean isPasswordValid(String password) {
		return PASSWORD_PATTERN.matcher(password).matches();
	}

	public static String hashPassword(String plainPassword) {
		return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
	}

	public static boolean verifyPassword(String plainPassword, String hashedPassword) {
		return BCrypt.checkpw(plainPassword, hashedPassword);
	}

	private boolean isUsernameExists(String username) throws SQLException {
		String query = "SELECT id FROM users WHERE username = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, username);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		}
	}

	@POST
	@Path("login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(Map<String, String> request) {
		String username = request.get("username");
		String password = request.get("password");

		if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("{\"error\":\"Username and password are required!\"}").build();
		}

		String query = "SELECT * FROM users WHERE username = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, username);
			
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next() && verifyPassword(password, rs.getString("password"))) {
					
					Map<String, Object> response = new HashMap<>();
					response.put("id", rs.getInt("id"));
					response.put("username",rs.getString("username"));
					return Response.ok(response).build(); 
				}else {
					return Response.status(Response.Status.UNAUTHORIZED)
							.entity("{\"error\":\"Invalid username or password!\"}").build();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"error\":\"Internal server error!\"}").build();
		}
	}

	@POST
	@Path("register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(Map<String, String> request) {
		String username = request.get("username");
		String password = request.get("password");

		if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("{\"error\":\"Username and password are required!\"}").build();
		}

		if (!isPasswordValid(password)) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("{\"error\":\"Password must be at least 8 characters long, "
							+ "include an uppercase letter, a lowercase letter, a number, "
							+ "and a special character!\"}")
					.build();
		}

		try {
			if (isUsernameExists(username)) {
				return Response.status(Response.Status.CONFLICT).entity("{\"error\":\"Username already exists!\"}")
						.build();
			}

			String query = "INSERT INTO users (username, password) VALUES (?, ?)";
			try (PreparedStatement stmt = conn.prepareStatement(query)) {
				stmt.setString(1, username);
				stmt.setString(2,  hashPassword(password)); 
				stmt.executeUpdate();
				return Response.status(Response.Status.CREATED)
						.entity("{\"message\":\"User registered successfully!\"}").build();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"error\":\"Internal server error!\"}").build();
		}
	}
}
