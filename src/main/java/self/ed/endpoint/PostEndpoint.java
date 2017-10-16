package self.ed.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import self.ed.entity.Post;
import self.ed.repository.PostRepository;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static java.util.Arrays.stream;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.apache.commons.lang3.ArrayUtils.contains;

/**
 * @author Anatolii
 */
@Path("/posts")
@Singleton
public class PostEndpoint {

    @Autowired
    private PostRepository postRepository;

    @GET
    @Produces(APPLICATION_JSON)
    public Response getAll() {
        return Response.status(OK).entity(postRepository.findAll()).build();
    }

    @GET
    @Path("/{id}")
    @Produces(APPLICATION_JSON)
    public Response get(@PathParam("id") Long id) {
        Post post = postRepository.findOne(id);
        if (post == null) {
            return Response.status(NOT_FOUND).build();
        }
        return Response.status(OK).entity(post).build();
    }

    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public Response create(Post post) {
        post.setId(null);
        post = postRepository.save(post);
        return Response.status(CREATED).entity(post).build();
    }

    @PUT
    @Path("/{id}")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public Response update(@PathParam("id") Long id, Post post) {
        Post existingPost = postRepository.findOne(id);
        if (existingPost == null) {
            return Response.status(NOT_FOUND).build();
        }

        if (getMood(existingPost) != getMood(post)) {
            return Response.status(BAD_REQUEST).entity("Don't change your mood!").build();
        }

        post.setId(id);
        post = postRepository.save(post);
        return Response.status(OK).entity(post).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        if (!postRepository.exists(id)) {
            return Response.status(NOT_FOUND).build();
        }
        postRepository.delete(id);
        return Response.status(NO_CONTENT).build();
    }

    /**
     * @return 1 - positive, 0 - neutral, -1 negative
     */
    private int getMood(Post post) {
        String[] positiveWords = {"wonderful"};
        String[] negativeWords = {"awful"};
        return Integer.compare(stream(post.getTitle().split("\\W+"))
                .map(String::toLowerCase)
                .mapToInt(word -> contains(positiveWords, word) ? 1 : contains(negativeWords, word) ? -1 : 0)
                .sum(), 0);
    }
}