package iit.project;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import iit.project.config.DBConnection;
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
 * Controller for managing Technologies
 */
@Path("technologies")
public class TechnologieController {

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
    public List<Technologie> getAllTechnologies() {
        String query = "SELECT * FROM technologie";
        List<Technologie> technologies = new ArrayList<>();

        try (PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Technologie t = new Technologie(
                    resultSet.getInt("id"),
                    resultSet.getString("nomTech"),
                    resultSet.getString("version"),
                    resultSet.getString("description"),
                    resultSet.getString("plateformSupporte"),
                    resultSet.getString("siteWeb"),
                    resultSet.getDate("dateDerniereMAJ"),
                    resultSet.getString("logo")
                );
                technologies.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; 
        }
        return technologies;
    }
    
    @GET
    @Path("/ByEntreprise/{entrepriseId}")
    @Produces(MediaType.APPLICATION_JSON)
    public static List<Technologie> getTechnologiesByEntrepriseId(@PathParam("entrepriseId") int entrepriseId) {
        String query = "SELECT t.* FROM technologie t " +
                       "JOIN entreprise_technologie et ON t.id = et.id_tech " +
                       "WHERE et.id_entreprise = ?";
        List<Technologie> technologies = new ArrayList<>();

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, entrepriseId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Technologie t = new Technologie(
                        resultSet.getInt("id"),
                        resultSet.getString("nomTech"),
                        resultSet.getString("version"),
                        resultSet.getString("description"),
                        resultSet.getString("plateformSupporte"),
                        resultSet.getString("siteWeb"),
                        resultSet.getDate("dateDerniereMAJ"),
                        resultSet.getString("logo")
                        
                    );
                    technologies.add(t);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return technologies;
    }


    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTechnologieById(@PathParam("id") int id) {
        String query = "SELECT * FROM technologie WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Technologie t = new Technologie(
                    resultSet.getInt("id"),
                    resultSet.getString("nomTech"),
                    resultSet.getString("version"),
                    resultSet.getString("description"),
                    resultSet.getString("plateformSupporte"),
                    resultSet.getString("siteWeb"),
                    resultSet.getDate("dateDerniereMAJ"),
                    resultSet.getString("logo")
                );
                return Response.ok(t).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Technologie not found!\"}")
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Internal server error!\"}")
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTechnologie(Map<String, String> request) {
        String nom = request.get("nomTech");
        String version = request.get("version");
        String description = request.get("description");
        String platforme = request.get("plateformSupporte");
        String siteWeb = request.get("siteWeb");
        String logo = request.get("logo");
        Date dateDerniereMAJ = request.get("dateDerniereMAJ")!= null? dateFormat(request.get("dateDerniereMAJ"))
        		:null;

        if (nom == null || nom.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Nom est requis!\"}")
                    .build();
        }

        String query = "INSERT INTO technologie (nomTech, version, description, plateformSupporte, siteWeb, dateDerniereMAJ, logo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nom);
            stmt.setString(2, version);
            stmt.setString(3, description);
            stmt.setString(4, platforme);
            stmt.setString(5, siteWeb);
            stmt.setDate(6, dateDerniereMAJ);
            stmt.setString(7, logo);
            stmt.executeUpdate();
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\":\"Technologie created successfully!\"}")
                    .build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Internal server error!\"}")
                    .build();
        }
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTechnologie(@PathParam("id") int id, Map<String, String> request) {
        String nom = request.get("nomTech");
        String version = request.get("version");
        String description = request.get("description");
        String platforme = request.get("plateformSupporte");
        String siteWeb = request.get("siteWeb");
        String logo = request.get("logo");
        Date dateDerniereMAJ = request.get("dateDerniereMAJ")!= null? dateFormat(request.get("dateDerniereMAJ"))
        		:null;


        if (nom == null || nom.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Nom est requis!\"}")
                    .build();
        }

        String query = "UPDATE technologie SET nomTech = ?, version = ?, description = ?, plateformSupporte = ?, siteWeb = ?, dateDerniereMAJ = ?, logo = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nom);
            stmt.setString(2, version);
            stmt.setString(3, description);
            stmt.setString(4, platforme);
            stmt.setString(5, siteWeb);
            stmt.setDate(6, dateDerniereMAJ);
            stmt.setString(7, logo);
            stmt.setInt(8, id);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                return Response.ok("{\"message\":\"Technologie updated successfully!\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Technologie not found!\"}")
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Internal server error!\"}")
                    .build();
        }
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTechnologie(@PathParam("id") int id) {
    	 String query1 = "DELETE FROM entreprise_technologie WHERE id_tech = ?;";
		try {
			PreparedStatement stmt1 = conn.prepareStatement(query1);
			stmt1.setInt(1, id);
            stmt1.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
             
        String query = "DELETE FROM technologie WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                return Response.ok("{\"message\":\"Technologie deleted successfully!\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Technologie not found!\"}")
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Internal server error!\"}")
                    .build();
        }
    }
}
