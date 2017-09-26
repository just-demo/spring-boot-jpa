package self.edu;

import org.glassfish.jersey.server.ResourceConfig;

import javax.persistence.Persistence;
import javax.ws.rs.ApplicationPath;

/**
 * @author Anatolii
 */
@ApplicationPath("rest")
public class ApplicationConfig extends ResourceConfig {
    public ApplicationConfig() {
        register(TestService.class);
//        register(Persistence.createEntityManagerFactory("self-edu-persistence-unit"));
    }
}
