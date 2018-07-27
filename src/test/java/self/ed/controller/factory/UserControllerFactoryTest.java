package self.ed.controller.factory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import self.ed.entity.User;
import self.ed.testing.support.EntityFactory;
import self.ed.testing.support.EntityHelper;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static self.ed.testing.support.RandomUtils.random;

/**
 * @author Anatolii
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UserControllerFactoryTest {
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
    public void testCreate() {
        User user = random(User.class, "id");

        ResponseEntity<User> entity = restTemplate.postForEntity(PATH_USERS, user, User.class);

        assertThat(entity.getStatusCode()).isEqualTo(CREATED);
        User persisted = entityHelper.find(entity.getBody());
        assertThat(persisted.getName()).isEqualTo(user.getName());
    }
}