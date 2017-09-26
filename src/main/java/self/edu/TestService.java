package self.edu;

import self.edu.entity.TestEntity;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * @author Anatolii
 */
@Path("/test")
@Singleton
public class TestService {
//    @Inject
//    private EntityManagerFactory entityManagerFactory;

    @PersistenceUnit(unitName = "self-edu-persistence-unit")
    private EntityManagerFactory entityManagerFactory;

    @PersistenceUnit(unitName = "self-edu-persistence-unit")
    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @PostConstruct
    public void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("self-edu-persistence-unit");
    }

    @GET
    @Path("/entities")
    @Produces(APPLICATION_JSON)
    public Response getAll() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        List<TestEntity> entities = entityManager.createQuery("from TestEntity", TestEntity.class).getResultList();
        entityManager.getTransaction().commit();
        entityManager.close();
        return Response.status(200).entity(entities).build();
    }

    @GET
    @Path("/entity/{id}")
    @Produces(APPLICATION_JSON)
    public Response get(@PathParam("id") Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        TestEntity entity = entityManager.find(TestEntity.class, id);
        entityManager.getTransaction().commit();
        entityManager.close();
        return Response.status(200).entity(entity).build();
    }

    @POST
    @Path("/entity")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public Response create(TestEntity entity) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
        entityManager.close();
        return Response.status(200).entity(entity).build();
    }

    @GET
    @Path("/alive")
    public Response isAlive() {
        return Response.status(200).entity("I am alive!").build();
    }
}