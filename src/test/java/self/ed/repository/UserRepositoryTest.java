package self.ed.repository;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringRunner;
import self.ed.entity.Comment;
import self.ed.entity.Post;
import self.ed.entity.User;
import self.ed.testing.support.EntityFactory;
import self.ed.testing.support.EntityHelper;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static self.ed.testing.support.RandomUtils.random;

/**
 * @author Anatolii
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository instance;

    @Autowired
    private EntityHelper entityHelper;

    @Autowired
    private EntityFactory entityFactory;

    @Test
    public void testFindAll() {
        IntStream.range(0, 3).forEach(i -> entityFactory.createUser());

        Iterable<User> found = instance.findAll();

        List<User> expected = entityHelper.findAll(User.class);
        assertThat(found).containsOnlyElementsOf(expected);
    }

    @Test
    public void testFind() {
        User user = entityFactory.createUser();

        Optional<User> found = instance.findById(user.getId());

        assertThat(found).contains(user);
    }

    @Test
    public void testFind_NotFound() {
        Optional<User> found = instance.findById(Long.MAX_VALUE);

        assertThat(found).isEmpty();
    }

    @Test
    public void testCreate() {
        User user = random(User.class, "id");

        User returned = instance.save(user);

        assertThat(returned).isEqualToIgnoringGivenFields(user, "id");
        assertThat(returned.getId()).isNotNull();
        assertThat(entityHelper.find(returned)).isEqualTo(returned);
    }

    @Test
    public void testUpdate() {
        User user = entityFactory.createUser();
        user.setName(random(String.class));

        User returned = instance.save(user);

        assertThat(returned).isEqualTo(user);
        assertThat(entityHelper.find(returned)).isEqualTo(returned);
    }

    @Test(expected = Exception.class)
    @Ignore
    public void testUpdate_NotFound() {
        User user = random(User.class);
        user.setId(Long.MAX_VALUE);

        instance.save(user);
    }

    @Test
    public void testDelete() {
        User user = entityFactory.createUser();

        instance.delete(user);

        assertThat(entityHelper.find(user)).isNull();
    }

    @Test
    public void testDelete_WithPosts() {
        User user = entityFactory.createUser();
        Post post = entityFactory.createPost(user);

        instance.delete(user);

        assertThat(entityHelper.find(user)).isNull();
        assertThat(entityHelper.find(post)).isNull();
    }

    @Test
    @Ignore
    public void testDelete_WithComments() {
        User user = entityFactory.createUser();
        Comment comment = entityFactory.createComment(user);

        instance.delete(user);

        assertThat(entityHelper.find(user)).isNull();
        assertThat(entityHelper.find(comment)).isNull();
    }

    @Test(expected = Exception.class)
    @Ignore
    public void testDelete_NotFound() {
        User user = random(User.class);
        user.setId(Long.MAX_VALUE);

        instance.delete(user);
    }

    @Test
    public void testDeleteById() {
        User user = entityFactory.createUser();

        instance.deleteById(user.getId());

        assertThat(entityHelper.find(user)).isNull();
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void testDeleteById_NotFound() {
        instance.deleteById(Long.MAX_VALUE);
    }

    @Test
    public void testDeleteById_WithComments() {
        User user = entityFactory.createUser();
        Comment comment = entityFactory.createComment(user);

        instance.deleteById(user.getId());

        assertThat(entityHelper.find(user)).isNull();
        assertThat(entityHelper.find(comment)).isNull();
    }
}