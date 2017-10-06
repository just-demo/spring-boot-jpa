package self.ed.endpoint;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import self.ed.entity.User;
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
public class UserEndpointTest {
    private static final String PATH_USERS = "/rest/users";
    private static final String PATH_USER = "/rest/users/{id}";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EntityHelper entityHelper;

    @Test
    public void testFindAll() {
        IntStream.range(0, 3).forEach(i -> createUser());
        ResponseEntity<User[]> entity = restTemplate.getForEntity(PATH_USERS, User[].class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
        List<User> allUsers = entityHelper.findAll(User.class);
        assertThat(entity.getBody()).containsOnlyElementsOf(allUsers);
    }

    @Test
    public void testFind() {
        User user = createUser();
        ResponseEntity<User> entity = restTemplate.getForEntity(PATH_USER, User.class, user.getId());
        assertThat(entity.getStatusCode()).isEqualTo(OK);
        assertThat(entity.getBody()).isEqualTo(user);
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
        User user = createUser();
        user.setName(random(String.class));
        ResponseEntity<User> entity = restTemplate.exchange(PATH_USER, PUT, new HttpEntity<>(user), User.class, user.getId());
        assertThat(entity.getStatusCode()).isEqualTo(OK);
        User returned = entity.getBody();
        assertThat(returned.getName()).isEqualTo(user.getName());
        assertThat(returned.getId()).isEqualTo(user.getId());
        User persisted = entityHelper.find(user);
        assertThat(persisted.getName()).isEqualTo(user.getName());
        assertThat(persisted.getId()).isEqualTo(user.getId());
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
        User user = createUser();
        ResponseEntity<String> entity = restTemplate.exchange(PATH_USER, DELETE, EMPTY, String.class, user.getId());
        assertThat(entity.getStatusCode()).isEqualTo(NO_CONTENT);
        User persisted = entityHelper.find(user);
        assertThat(persisted).isNull();
    }

    @Test
    public void testDelete_NotFound() {
        ResponseEntity<String> entity = restTemplate.exchange(PATH_USER, DELETE, EMPTY, String.class, Long.MAX_VALUE);
        assertThat(entity.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    private User createUser() {
        return entityHelper.create(User.class);
    }
}