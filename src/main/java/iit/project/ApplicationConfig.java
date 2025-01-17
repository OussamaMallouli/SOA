package iit.project;


	
	import org.glassfish.jersey.server.ResourceConfig;


import iit.project.config.CorsFilter;
import jakarta.ws.rs.ApplicationPath;

	@ApplicationPath("")
	public class ApplicationConfig extends ResourceConfig {
	    public ApplicationConfig() {
	        packages("iit.project");  // Register your resource packages
	        register(CorsFilter.class); // Register the CORS filter globally
	    }
	}



