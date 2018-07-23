package self.ed.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import self.ed.entity.User;
import self.ed.repository.UserRepository;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;

/**
 * @author Anatolii
 */
@Path("/users")
@Singleton
public class UserEndpoint {

    @Autowired
    private UserRepository userRepository;

    @GET
    @Produces(APPLICATION_JSON)
    public Response getAll() {
        return Response.status(OK).entity(userRepository.findAll()).build();
    }

    @GET
    @Path("/{id}")
    @Produces(APPLICATION_JSON)
    public Response get(@PathParam("id") Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return Response.status(NOT_FOUND).build();
        }
        return Response.status(OK).entity(user).build();
    }

    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public Response create(User user) {
        user.setId(null);
        user = userRepository.save(user);
        return Response.status(CREATED).entity(user).build();
    }

    @PUT
    @Path("/{id}")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public Response update(@PathParam("id") Long id, User user) {
        if (!userRepository.existsById(id)) {
            return Response.status(NOT_FOUND).build();
        }
        user.setId(id);
        user = userRepository.save(user);
        return Response.status(OK).entity(user).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        if (!userRepository.existsById(id)) {
            return Response.status(NOT_FOUND).build();
        }
        userRepository.deleteById(id);
        return Response.status(NO_CONTENT).build();
    }
}