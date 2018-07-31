package self.ed.repository.dbunit;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import self.ed.entity.User;
import self.ed.repository.UserRepository;
import self.ed.testing.support.EntityHelper;

import java.util.List;
import java.util.Optional;

import static com.github.springtestdbunit.assertion.DatabaseAssertionMode.NON_STRICT;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

/**
 * @author Anatolii
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestExecutionListeners(listeners = DbUnitTestExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
@DatabaseSetup(value = "user.empty.xml")
public class UserRepositoryDbUnitSpringTest {
    @Autowired
    private UserRepository instance;

    @Autowired
    private EntityHelper entityHelper;

    @Test
    @DatabaseSetup(value = "user.multiple.xml")
    public void testFindAll() {
        Iterable<User> actual = instance.findAll();

        List<User> expected = entityHelper.findAll(User.class);
        assertThat(actual).containsOnlyElementsOf(expected);
    }

    @Test
    @DatabaseSetup(value = "user.multiple.xml")
    public void testFind() {
        Optional<User> found = instance.findById(1L);

        User expected = entityHelper.find(User.class, 1L);
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get()).isEqualTo(expected);
    }

    @Test
    @ExpectedDatabase(value = "user.single.xml", table = "user", assertionMode = NON_STRICT)
    public void testCreate() {
        User user = new User();
        user.setName("user1");

        instance.save(user);
    }
}