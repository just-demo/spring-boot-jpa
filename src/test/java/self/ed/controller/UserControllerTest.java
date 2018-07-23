package self.ed.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import self.ed.entity.Comment;
import self.ed.entity.Post;
import self.ed.entity.User;
import self.ed.testing.support.EntityFactory;
import self.ed.testing.support.EntityHelper;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpEntity.EMPTY;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.*;
import static self.ed.testing.support.RandomUtils.random;

/**
 * @author Anatolii
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UserControllerTest {
    private static final String PATH_USERS = "/users";
    private static final String PATH_USER = "/users/{id}";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EntityHelper entityHelper;

    @Autowired
    private EntityFactory entityFactory;

    @Test
    public void testFindAll() {
        IntStream.range(0, 3).forEach(i -> entityFactory.createUser());

        ResponseEntity<User[]> entity = restTemplate.getForEntity(PATH_USERS, User[].class);

        assertThat(entity.getStatusCode()).isEqualTo(OK);
        List<User> expectedUsers = entityHelper.findAll(User.class);
        assertThat(entity.getBody()).containsOnlyElementsOf(expectedUsers);
    }

    @Test
    public void testFind() {
        User user = entityFactory.createUser();

        ResponseEntity<User> entity = restTemplate.getForEntity(PATH_USER, User.class, user.getId());

        assertThat(entity.getStatusCode()).isEqualTo(OK);
        assertThat(entity.getBody()).isEqualTo(user);
    }

    @Test
    public void testFind_PostsNotIncluded() {
        User user = entityFactory.createUser();
        entityFactory.createPost(user);

        ResponseEntity<User> entity = restTemplate.getForEntity(PATH_USER, User.class, user.getId());

        assertThat(entity.getStatusCode()).isEqualTo(OK);
        assertThat(entity.getBody().getPosts()).isNull();
    }

    @Test
    public void testFind_CommentsNotIncluded() {
        User user = entityFactory.createUser();
        entityFactory.createComment(user);

        ResponseEntity<User> entity = restTemplate.getForEntity(PATH_USER, User.class, user.getId());

        assertThat(entity.getStatusCode()).isEqualTo(OK);
        assertThat(entity.getBody().getComments()).isNull();
    }

    @Test
    public void testFind_NotFound() {
        ResponseEntity<String> entity = restTemplate.getForEntity(PATH_USER, String.class, Long.MAX_VALUE);

        assertThat(entity.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    public void testCreate() {
        User user = random(User.class, "id");

        ResponseEntity<User> entity = restTemplate.postForEntity(PATH_USERS, user, User.class);

        assertThat(entity.getStatusCode()).isEqualTo(CREATED);
        User returned = entity.getBody();
        assertThat(returned.getName()).isEqualTo(user.getName());
        assertThat(returned.getId()).isNotNull();
        User persisted = entityHelper.find(returned);
        assertThat(persisted.getName()).isEqualTo(user.getName());
    }

    @Test
    public void testUpdate() {
        User user = entityFactory.createUser();
        user.setName(random(String.class));

        ResponseEntity<User> entity = restTemplate.exchange(PATH_USER, PUT, new HttpEntity<>(user), User.class, user.getId());

        assertThat(entity.getStatusCode()).isEqualTo(OK);
        User returned = entity.getBody();
        assertThat(returned.getName()).isEqualTo(user.getName());
        assertThat(returned.getId()).isEqualTo(user.getId());
        User persisted = entityHelper.find(user);
        assertThat(persisted.getName()).isEqualTo(user.getName());
    }

    @Test
    public void testUpdate_NotFound() {
        User user = random(User.class);
        user.setId(Long.MAX_VALUE);

        ResponseEntity<String> entity = restTemplate.exchange(PATH_USER, PUT, new HttpEntity<>(user), String.class, user.getId());

        assertThat(entity.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    public void testDelete() {
        User user = entityFactory.createUser();

        ResponseEntity<String> entity = restTemplate.exchange(PATH_USER, DELETE, EMPTY, String.class, user.getId());

        assertThat(entity.getStatusCode()).isEqualTo(NO_CONTENT);
        assertThat(entityHelper.find(user)).isNull();
    }

    @Test
    public void testDelete_WithPosts() {
        User user = entityFactory.createUser();
        Post post = entityFactory.createPost(user);

        ResponseEntity<String> entity = restTemplate.exchange(PATH_USER, DELETE, EMPTY, String.class, user.getId());

        assertThat(entity.getStatusCode()).isEqualTo(NO_CONTENT);
        assertThat(entityHelper.find(user)).isNull();
        assertThat(entityHelper.find(post)).isNull();
    }

    @Test
    public void testDelete_WithComments() {
        User user = entityFactory.createUser();
        Comment comment = entityFactory.createComment(user);

        ResponseEntity<String> entity = restTemplate.exchange(PATH_USER, DELETE, EMPTY, String.class, user.getId());

        assertThat(entity.getStatusCode()).isEqualTo(NO_CONTENT);
        assertThat(entityHelper.find(user)).isNull();
        assertThat(entityHelper.find(comment)).isNull();
    }

    @Test
    public void testDelete_NotFound() {
        ResponseEntity<String> entity = restTemplate.exchange(PATH_USER, DELETE, EMPTY, String.class, Long.MAX_VALUE);

        assertThat(entity.getStatusCode()).isEqualTo(NOT_FOUND);
    }
}