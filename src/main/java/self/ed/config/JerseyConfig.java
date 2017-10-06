package self.ed.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;
import self.ed.endpoint.UserEndpoint;

import javax.ws.rs.ApplicationPath;

/**
 * @author Anatolii
 */
@Component
@ApplicationPath("/rest")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(UserEndpoint.class);
    }

}
