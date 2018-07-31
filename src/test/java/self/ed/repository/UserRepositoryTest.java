package self.ed.repository;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;
import self.ed.entity.Comment;
import self.ed.entity.Post;
import self.ed.entity.User;
import self.ed.testing.support.EntityFactory;
import self.ed.testing.support.EntityHelper;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
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

    @Test
    public void testFindAll_Page() {
        IntStream.range(0, 3 * 5).forEach(i -> entityFactory.createUser());

        Page<User> page = instance.findAll(PageRequest.of(1, 5));

        List<User> expected = entityHelper.findAll(User.class);
        assertThat(page.getTotalElements()).isEqualTo(expected.size());
        assertThat(page.getTotalPages()).isEqualTo((int) Math.ceil(expected.size() / 5d));
        assertThat(page.getContent().size()).isEqualTo(5);
    }

    @Test
    public void testFindAll_Sort() {
        IntStream.range(0, 3 * 5).forEach(i -> entityFactory.createUser());

        Iterable<User> actual = instance.findAll(Sort.by("id"));

        List<User> expected = entityHelper.findAll(User.class).stream()
                .sorted(comparing(User::getId))
                .collect(toList());
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    public void testFindAll_SortAndPage() {
        IntStream.range(0, 3 * 5).forEach(i -> entityFactory.createUser());

        Page<User> page = instance.findAll(PageRequest.of(1, 5, Sort.by("id")));

        List<User> all = entityHelper.findAll(User.class);
        List<User> expected = entityHelper.findAll(User.class).stream()
                .sorted(comparing(User::getId))
                .skip(5)
                .limit(5)
                .collect(toList());
        assertThat(page.getTotalElements()).isEqualTo(all.size());
        assertThat(page.getTotalPages()).isEqualTo((int) Math.ceil(all.size() / 5d));
        assertThat(page.getContent()).containsExactlyElementsOf(expected);
    }

    @Test
    public void testFindAllByName_Page() {
        IntStream.range(0, 3 * 5).forEach(i -> entityFactory.createUser());
        User user = entityFactory.createUser();

        Page<User> page = instance.findAllByName(user.getName(), PageRequest.of(0, 5));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getContent()).containsExactly(user);
    }

    @Test
    public void testFindAllByNameIn() {
        IntStream.range(0, 3).forEach(i -> entityFactory.createUser());
        List<User> searchUsers = IntStream.range(0, 3)
                .mapToObj(i -> entityFactory.createUser())
                .collect(toList());
        List<String> searchNames = searchUsers.stream()
                .map(User::getName)
                .collect(toList());

        List<User> found = instance.findAllByNameIn(searchNames);

        assertThat(found).containsOnlyElementsOf(searchUsers);
    }

    @Test
    public void testCountByName() {
        IntStream.range(0, 3).forEach(i -> entityFactory.createUser());
        User user = entityFactory.createUser();

        long count = instance.countByName(user.getName());

        assertThat(count).isEqualTo(1L);
    }

    @Test
    public void testDeleteByName() {
        User user = entityFactory.createUser();

        long deleted = instance.deleteByName(user.getName());

        assertThat(deleted).isEqualTo(1L);
    }

    @Test
    public void testDeleteByName_NoMatches() {
        long deleted = instance.deleteByName(random(String.class));

        assertThat(deleted).isEqualTo(0);
    }

    @Test
    public void testRemoveByName() {
        User user = entityFactory.createUser();

        List<User> removed = instance.removeByName(user.getName());

        assertThat(removed).containsOnly(user);
    }

    @Test
    public void testRemoveByName_NoMatches() {
        List<User> removed = instance.removeByName(random(String.class));

        assertThat(removed).isEmpty();
    }

}