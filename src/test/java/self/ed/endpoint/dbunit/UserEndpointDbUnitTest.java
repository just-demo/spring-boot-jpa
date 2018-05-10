package self.ed.endpoint.dbunit;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import self.ed.entity.User;
import self.ed.testing.support.EntityHelper;

import java.util.List;

import static com.github.springtestdbunit.assertion.DatabaseAssertionMode.NON_STRICT;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author Anatolii
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class UserEndpointDbUnitTest {
    private static final String PATH_USERS = "/rest/users";
    private static final String PATH_USER = "/rest/users/{id}";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EntityHelper entityHelper;

    @Test
    @DatabaseSetup(value = "user.multiple.xml")
    public void testFindAll() {
        ResponseEntity<User[]> entity = restTemplate.getForEntity(PATH_USERS, User[].class);

        assertThat(entity.getStatusCode()).isEqualTo(OK);
        List<User> expectedUsers = entityHelper.findAll(User.class);
        assertThat(entity.getBody()).containsOnlyElementsOf(expectedUsers);
    }

    @Test
    @DatabaseSetup(value = "user.multiple.xml")
    public void testFind() {
        ResponseEntity<User> entity = restTemplate.getForEntity(PATH_USER, User.class, 1L);

        assertThat(entity.getStatusCode()).isEqualTo(OK);
        User expectedUser = entityHelper.find(User.class, 1L);
        assertThat(entity.getBody()).isEqualTo(expectedUser);
    }

    @Test
    @DatabaseSetup(value = "user.empty.xml")
    @ExpectedDatabase(value = "user.single.xml", table = "user", assertionMode = NON_STRICT)
    public void testCreate() {
        User user = new User();
        user.setName("user1");
        ResponseEntity<User> entity = restTemplate.postForEntity(PATH_USERS, user, User.class);

        assertThat(entity.getStatusCode()).isEqualTo(CREATED);
    }
}