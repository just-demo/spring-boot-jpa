package self.ed.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import self.ed.entity.Comment;
import self.ed.repository.CommentRepository;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;

/**
 * @author Anatolii
 */
@Path("/comments")
@Singleton
public class CommentEndpoint {

    @Autowired
    private CommentRepository commentRepository;

    @GET
    @Produces(APPLICATION_JSON)
    public Response getAll() {
        return Response.status(OK).entity(commentRepository.findAll()).build();
    }

    @GET
    @Path("/{id}")
    @Produces(APPLICATION_JSON)
    public Response get(@PathParam("id") Long id) {
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment == null) {
            return Response.status(NOT_FOUND).build();
        }
        return Response.status(OK).entity(comment).build();
    }

    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public Response create(Comment comment) {
        comment.setId(null);
        comment = commentRepository.save(comment);
        return Response.status(CREATED).entity(comment).build();
    }

    @PUT
    @Path("/{id}")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public Response update(@PathParam("id") Long id, Comment comment) {
        if (!commentRepository.existsById(id)) {
            return Response.status(NOT_FOUND).build();
        }
        comment.setId(id);
        comment = commentRepository.save(comment);
        return Response.status(OK).entity(comment).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        if (!commentRepository.existsById(id)) {
            return Response.status(NOT_FOUND).build();
        }
        commentRepository.deleteById(id);
        return Response.status(NO_CONTENT).build();
    }
}