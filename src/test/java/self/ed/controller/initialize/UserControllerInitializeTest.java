package self.ed.controller.initialize;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import self.ed.entity.User;
import self.ed.testing.support.EntityHelper;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author Anatolii
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UserControllerInitializeTest {
    private static final String PATH_USERS = "/users";
    private static final String PATH_USER = "/users/{id}";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EntityHelper entityHelper;

    @Test
    @Sql("classpath:init.users.sql")
    public void testFindAll() {
        ResponseEntity<User[]> entity = restTemplate.getForEntity(PATH_USERS, User[].class);

        assertThat(entity.getStatusCode()).isEqualTo(OK);
        List<User> expectedUsers = entityHelper.findAll(User.class);
        assertThat(entity.getBody()).containsOnlyElementsOf(expectedUsers);
    }

    @Test
    @Sql("classpath:init.users.sql")
    public void testFind() {
        ResponseEntity<User> entity = restTemplate.getForEntity(PATH_USER, User.class, 1L);

        assertThat(entity.getStatusCode()).isEqualTo(OK);
        User expectedUser = entityHelper.find(User.class, 1L);
        assertThat(entity.getBody()).isEqualTo(expectedUser);
    }

    @Test
    @Sql("classpath:init.users.sql")
    public void testCreate() {
        User user = new User();
        user.setName("user3");
        ResponseEntity<User> entity = restTemplate.postForEntity(PATH_USERS, user, User.class);

        assertThat(entity.getStatusCode()).isEqualTo(CREATED);
        User persisted = entityHelper.find(entity.getBody());
        assertThat(persisted.getName()).isEqualTo(user.getName());
    }
}