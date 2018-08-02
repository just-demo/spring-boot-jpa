package self.ed.repository.dbsetup;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import self.ed.entity.User;
import self.ed.repository.UserRepository;
import self.ed.testing.support.EntityHelper;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.generator.ValueGenerators.sequence;
import static com.ninja_squad.dbsetup.generator.ValueGenerators.stringSequence;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * @author Anatolii
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UserRepositoryDbSetupTest {

    @Autowired
    private UserRepository instance;

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

        Iterable<User> actual = instance.findAll();

        List<User> expected = entityHelper.findAll(User.class);
        assertThat(actual).containsOnlyElementsOf(expected);
    }

    @Test
    public void testFind() {
        launch(insertInto("user")
                .columns("id", "name")
                .values(1L, "user1")
                .values(2L, "user2")
                .build());

        Optional<User> actual = instance.findById(1L);

        User expected = entityHelper.find(User.class, 1L);
        assertThat(actual).contains(expected);
    }

    @Test
    public void testCreate() {
        User user = new User();
        user.setName("user1");

        User returned = instance.save(user);

        assertThat(returned).isEqualToIgnoringGivenFields(user, "id");
        assertThat(returned.getId()).isNotNull();
        assertThat(entityHelper.find(returned)).isEqualTo(returned);
    }

    private void launch(Operation operation) {
        new DbSetup(new DataSourceDestination(dataSource), operation).launch();
    }
}