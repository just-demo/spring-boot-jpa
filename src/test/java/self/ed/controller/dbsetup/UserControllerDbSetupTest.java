package self.ed.controller.dbsetup;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import self.ed.entity.User;
import self.ed.testing.support.EntityHelper;

import javax.sql.DataSource;
import java.util.List;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.generator.ValueGenerators.sequence;
import static com.ninja_squad.dbsetup.generator.ValueGenerators.stringSequence;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author Anatolii
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UserControllerDbSetupTest {
    private static final String PATH_USERS = "/users";
    private static final String PATH_USER = "/users/{id}";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EntityHelper entityHelper;

    @Autowired
    private DataSource dataSource;

    @Before
    public void setUp() {
        // Deleting all tables because another tests may populated dependent entities
        launch(deleteAllFrom("comment", "post", "user"));
    }

    @Test
    public void testFindAll() {
        // Don't really need such complexity here, just an example
        launch(sequenceOf(
                deleteAllFrom("user"),
                insertInto("user")
                        .withGeneratedValue("id", sequence().startingAt(1L))
                        .withGeneratedValue("name", stringSequence("user").startingAt(1L))
                        .repeatingValues().times(3)
                        .build()));

        ResponseEntity<User[]> entity = restTemplate.getForEntity(PATH_USERS, User[].class);

        assertThat(entity.getStatusCode()).isEqualTo(OK);
        List<User> expectedUsers = entityHelper.findAll(User.class);
        assertThat(entity.getBody()).containsOnlyElementsOf(expectedUsers);
    }

    @Test
    public void testFind() {
        launch(insertInto("user")
                .columns("id", "name")
                .values(1L, "user1")
                .values(2L, "user2")
                .build());

        ResponseEntity<User> entity = restTemplate.getForEntity(PATH_USER, User.class, 1L);

        assertThat(entity.getStatusCode()).isEqualTo(OK);
        User expectedUser = entityHelper.find(User.class, 1L);
        assertThat(entity.getBody()).isEqualTo(expectedUser);
    }

    @Test
    public void testCreate() {
        User user = new User();
        user.setName("user1");
        ResponseEntity<User> entity = restTemplate.postForEntity(PATH_USERS, user, User.class);

        assertThat(entity.getStatusCode()).isEqualTo(CREATED);
        User persisted = entityHelper.find(entity.getBody());
        assertThat(persisted.getName()).isEqualTo(user.getName());
    }

    private void launch(Operation operation) {
        new DbSetup(new DataSourceDestination(dataSource), operation).launch();
    }
}