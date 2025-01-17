package iit.project;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import iit.project.config.DBConnection;
import iit.project.model.Entreprise;
import iit.project.model.Technologie;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Controller for managing Entreprises
 */
@Path("entreprises")
public class EntrepriseController {

	private static Connection conn = DBConnection.getConnection();

	private static Date dateFormat(String dateString) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = new Date(formatter.parse(dateString).getTime());
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Entreprise> getAllEntreprises() {
		String query = "SELECT * FROM entreprise";
		List<Entreprise> entreprises = new ArrayList<>();

		try (PreparedStatement statement = conn.prepareStatement(query);
				ResultSet resultSet = statement.executeQuery()) {
			List<Technologie> listTech = new ArrayList<>();
			while (resultSet.next()) {

				listTech = TechnologieController.getTechnologiesByEntrepriseId(resultSet.getInt("id"));
				Entreprise e = new Entreprise(resultSet.getInt("id"), resultSet.getString("nomEntreprise"),
						resultSet.getString("email"), resultSet.getString("numTel"), resultSet.getString("description"),
						resultSet.getDate("dateDeCreation"), resultSet.getString("logo"), listTech);
				entreprises.add(e);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null; // Considérez de retourner une réponse d'erreur appropriée
		}
		return entreprises;
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEntrepriseById(@PathParam("id") int id) {
		String query = "SELECT * FROM entreprise WHERE id = ?";
		try (PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				List<Technologie> ListTech = TechnologieController.getTechnologiesByEntrepriseId(id);
				Entreprise e = new Entreprise(resultSet.getInt("id"), resultSet.getString("nomEntreprise"),
						resultSet.getString("email"), resultSet.getString("numTel"), resultSet.getString("description"),
						resultSet.getDate("dateDeCreation"), resultSet.getString("logo"), ListTech);
				return Response.ok(e).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Entreprise not found!\"}")
						.build();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"error\":\"Internal server error!\"}").build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createEntreprise(Map<String, Object> request) {
		String nom = (String) request.get("nomEntreprise");
		String email = (String) request.get("email");
		String numTel = (String) request.get("numTel");
		String description = (String) request.get("description");
		String logo = (String) request.get("logo");
		Date dateDeCreation = request.get("dateDeCreation") != null
				? dateFormat((String) request.get("dateDeCreation"))
				: null;
		List<String> listTech = (List<String>) request.get("listTech");
		if (nom == null || nom.isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Nom est requis!\"}").build();
		}

		String query = "INSERT INTO entreprise (nomEntreprise, email, numTel, description, dateDeCreation, logo) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, nom);
			stmt.setString(2, email);
			stmt.setString(3, numTel);
			stmt.setString(4, description);
			stmt.setDate(5, dateDeCreation);
			stmt.setString(6, logo);
			stmt.executeUpdate();
			ResultSet generatedKeys = stmt.getGeneratedKeys();
			int id_entreprise = 0;
			if (generatedKeys.next()) {
				id_entreprise = generatedKeys.getInt(1);
			}
			if (listTech != null && id_entreprise != 0) {
				for (String s : listTech) {
					String query2 = "INSERT INTO entreprise_technologie (id_entreprise, id_tech)" + " values(?,?)";
					int id_tech = Integer.parseInt(s);
					try (PreparedStatement stmt1 = conn.prepareStatement(query2)) {
						stmt1.setInt(1, id_entreprise);
						stmt1.setInt(2, id_tech);
						stmt1.executeUpdate();
					}
				}
			}
			return Response.status(Response.Status.CREATED).entity("{\"message\":\"Entreprise created successfully!\"}")
					.build();
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"error\":\"Internal server error!\"}").build();
		}

	}

	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateEntreprise(@PathParam("id") int id, Map<String, Object> request) {
		String nom = (String) request.get("nomEntreprise");
		String email = (String) request.get("email");
		String numTel = (String) request.get("numTel");
		String description = (String) request.get("description");
		String logo = (String) request.get("logo");
		java.sql.Date dateDeCreation = request.get("dateDeCreation") != null
				? dateFormat((String) request.get("dateDeCreation"))
				: null;
		List<String> listTech = (List<String>) request.get("listTech");

		if (nom == null || nom.isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Nom est requis!\"}").build();
		}

		String query = "UPDATE entreprise SET nomEntreprise = ?, email = ?, numTel = ?, description = ?, dateDeCreation = ?, logo = ? WHERE id = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, nom);
			stmt.setString(2, email);
			stmt.setString(3, numTel);
			stmt.setString(4, description);
			stmt.setDate(5, dateDeCreation);
			stmt.setString(6, logo);
			stmt.setInt(7, id);

			int rowsUpdated = stmt.executeUpdate();
			if (rowsUpdated > 0) {
				// Si les technologies sont présentes dans la requête, les mettre à jour
				if (listTech != null) {
					// Supprimer toutes les associations actuelles de technologies de cette
					// entreprise
					String deleteQuery = "DELETE FROM entreprise_technologie WHERE id_entreprise = ?";
					try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
						deleteStmt.setInt(1, id);
						deleteStmt.executeUpdate();
					}

					// Ajouter les nouvelles technologies
					for (String s : listTech) {
						String insertQuery = "INSERT INTO entreprise_technologie (id_entreprise, id_tech) VALUES (?, ?)";
						int id_tech = Integer.parseInt(s); // Assurez-vous que les ID de technologies sont des entiers
						try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
							insertStmt.setInt(1, id);
							insertStmt.setInt(2, id_tech);
							insertStmt.executeUpdate();
						}
					}
				}
				return Response.ok("{\"message\":\"Entreprise updated successfully!\"}").build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Entreprise not found!\"}")
						.build();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"error\":\"Internal server error!\"}").build();
		}
	}

	@DELETE
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteEntreprise(@PathParam("id") int id) {
		 String query1 = "DELETE FROM entreprise_technologie WHERE id_entreprise = ?;";
			try {
				PreparedStatement stmt1 = conn.prepareStatement(query1);
				stmt1.setInt(1, id);
	            stmt1.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
		String query = "DELETE FROM entreprise WHERE id = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, id);
			int rowsDeleted = stmt.executeUpdate();
			if (rowsDeleted > 0) {
				return Response.ok("{\"message\":\"Entreprise deleted successfully!\"}").build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Entreprise not found!\"}")
						.build();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"error\":\"Internal server error!\"}").build();
		}
	}

	@GET
	@Path("/search/{nom}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEntrepriseByNom(@PathParam("nom") String nom) {
		String query = "SELECT * FROM entreprise WHERE nomEntreprise like ?";
		try (PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setString(1, "%" + nom + "%");
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				Entreprise e = new Entreprise(resultSet.getInt("id"), resultSet.getString("nomEntreprise"),
						resultSet.getString("email"), resultSet.getString("numTel"), resultSet.getString("description"),
						resultSet.getDate("dateDeCreation"), resultSet.getString("logo"), null // listTech sera chargé
																								// ultérieurement
				);
				return Response.ok(e).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Entreprise not found!\"}")
						.build();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"error\":\"Internal server error!\"}").build();
		}
	}

}
