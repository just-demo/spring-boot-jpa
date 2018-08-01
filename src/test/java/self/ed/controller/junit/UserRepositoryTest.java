package self.ed.controller.junit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import self.ed.entity.User;
import self.ed.repository.UserRepository;
import self.ed.testing.support.EntityFactory;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Anatolii
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {UserRepositoryTest.TestConfig.class})
public class UserRepositoryTest {
    @ComponentScan("self.ed")
    static class TestConfig {
    }

    @Autowired
    private UserRepository instance;

    @Autowired
    private EntityFactory entityFactory;

    @Test
    public void testFind() {
        User user = entityFactory.createUser();

        Optional<User> found = instance.findById(user.getId());

        assertThat(found).contains(user);
    }
}
