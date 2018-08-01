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
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import self.ed.entity.Comment;
import self.ed.entity.Post;
import self.ed.entity.User;
import self.ed.testing.support.EntityFactory;
import self.ed.testing.support.EntityHelper;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.concurrent.TimeUnit.SECONDS;
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
    private EntityManager entityManager;

    @Autowired
    private EntityHelper entityHelper;

    @Autowired
    private EntityFactory entityFactory;

    @Test
    public void findAll() {
        IntStream.range(0, 3).forEach(i -> entityFactory.createUser());

        Iterable<User> found = instance.findAll();

        List<User> expected = entityHelper.findAll(User.class);
        assertThat(found).containsOnlyElementsOf(expected);
    }

    @Test
    public void find() {
        User user = entityFactory.createUser();

        Optional<User> found = instance.findById(user.getId());

        assertThat(found).contains(user);
    }

    @Test
    public void find_NotFound() {
        Optional<User> found = instance.findById(Long.MAX_VALUE);

        assertThat(found).isEmpty();
    }

    @Test
    public void create() {
        User user = random(User.class, "id");

        User returned = instance.save(user);

        assertThat(returned).isEqualToIgnoringGivenFields(user, "id");
        assertThat(returned.getId()).isNotNull();
        assertThat(entityHelper.find(returned)).isEqualTo(returned);
    }

    @Test
    public void update() {
        User user = entityFactory.createUser();
        user.setName(random(String.class));

        User returned = instance.save(user);

        assertThat(returned).isEqualTo(user);
        assertThat(entityHelper.find(returned)).isEqualTo(returned);
    }

    @Test(expected = Exception.class)
    @Ignore
    public void update_NotFound() {
        User user = random(User.class);
        user.setId(Long.MAX_VALUE);

        instance.save(user);
    }

    @Test
    public void delete() {
        User user = entityFactory.createUser();

        instance.delete(user);

        assertThat(entityHelper.find(user)).isNull();
    }

    @Test
    public void delete_WithPosts() {
        User user = entityFactory.createUser();
        Post post = entityFactory.createPost(user);

        instance.delete(user);

        assertThat(entityHelper.find(user)).isNull();
        assertThat(entityHelper.find(post)).isNull();
    }

    @Test
    @Ignore
    public void delete_WithComments() {
        User user = entityFactory.createUser();
        Comment comment = entityFactory.createComment(user);

        instance.delete(user);

        assertThat(entityHelper.find(user)).isNull();
        assertThat(entityHelper.find(comment)).isNull();
    }

    @Test(expected = Exception.class)
    @Ignore
    public void delete_NotFound() {
        User user = random(User.class);
        user.setId(Long.MAX_VALUE);

        instance.delete(user);
    }

    @Test
    public void deleteById() {
        User user = entityFactory.createUser();

        instance.deleteById(user.getId());

        assertThat(entityHelper.find(user)).isNull();
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void deleteById_NotFound() {
        instance.deleteById(Long.MAX_VALUE);
    }

    @Test
    public void deleteById_WithComments() {
        User user = entityFactory.createUser();
        Comment comment = entityFactory.createComment(user);

        instance.deleteById(user.getId());

        assertThat(entityHelper.find(user)).isNull();
        assertThat(entityHelper.find(comment)).isNull();
    }

    @Test
    public void findAll_Page() {
        IntStream.range(0, 3 * 5).forEach(i -> entityFactory.createUser());

        Page<User> page = instance.findAll(PageRequest.of(1, 5));

        List<User> expected = entityHelper.findAll(User.class);
        assertThat(page.getTotalElements()).isEqualTo(expected.size());
        assertThat(page.getTotalPages()).isEqualTo((int) Math.ceil(expected.size() / 5d));
        assertThat(page.getContent().size()).isEqualTo(5);
    }

    @Test
    public void findAll_Sort() {
        IntStream.range(0, 3 * 5).forEach(i -> entityFactory.createUser());

        Iterable<User> actual = instance.findAll(Sort.by("id"));

        List<User> expected = entityHelper.findAll(User.class).stream()
                .sorted(comparing(User::getId))
                .collect(toList());
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    public void findAll_SortAndPage() {
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
    public void findAllByName_Page() {
        IntStream.range(0, 3 * 5).forEach(i -> entityFactory.createUser());
        User user = entityFactory.createUser();

        Page<User> page = instance.findAllByName(user.getName(), PageRequest.of(0, 5));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getContent()).containsExactly(user);
    }

    @Test
    public void findAllByNameIn() {
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
    public void findAllByNameIn_Pageable() {
        List<String> searchNames = IntStream.range(0, 3)
                .mapToObj(i -> entityFactory.createUser())
                .map(User::getName)
                .collect(toList());

        List<User> found = instance.findAllByNameIn(searchNames, PageRequest.of(0, 2));

        assertThat(found).size().isEqualTo(2);
    }

    @Test
    public void countByName() {
        IntStream.range(0, 3).forEach(i -> entityFactory.createUser());
        User user = entityFactory.createUser();

        long count = instance.countByName(user.getName());

        assertThat(count).isEqualTo(1L);
    }

    @Test
    public void deleteByName() {
        User user = entityFactory.createUser();

        long deleted = instance.deleteByName(user.getName());

        assertThat(deleted).isEqualTo(1L);
    }

    @Test
    public void deleteByName_NoMatches() {
        long deleted = instance.deleteByName(random(String.class));

        assertThat(deleted).isEqualTo(0);
    }

    @Test
    public void removeByName() {
        User user = entityFactory.createUser();

        List<User> removed = instance.removeByName(user.getName());

        assertThat(removed).containsOnly(user);
    }

    @Test
    public void removeByName_NoMatches() {
        List<User> removed = instance.removeByName(random(String.class));

        assertThat(removed).isEmpty();
    }

    @Test
    public void getByName() {
        User user = entityFactory.createUser();

        User found = instance.getByName(user.getName());

        assertThat(found).isEqualTo(user);
    }

    @Test
    public void getByName_NotFound() {
        User found = instance.getByName(random(String.class));

        assertThat(found).isNull();
    }

    @Test
    public void getById() {
        User user = entityFactory.createUser();

        User found = instance.getById(user.getId());

        assertThat(found).isEqualTo(user);
    }

    @Ignore
    @Test(expected = EmptyResultDataAccessException.class)
    public void getById_NotFound() {
        instance.getById(Long.MAX_VALUE);
    }

    @Test
    public void findDistinctByIdOrNameIgnoreCaseOrderByIdDesc() {
        User user1 = entityFactory.createUser();
        User user2 = entityFactory.createUser();

        List<User> found = instance.findDistinctByIdOrNameIgnoreCaseOrderByIdDesc(user1.getId(), user2.getName().toUpperCase());

        assertThat(found).containsExactly(user2, user1);
    }

    @Test
    public void findByNameLike() {
        User user = entityFactory.createUser();

        List<User> found = instance.findByNameLike("%" + user.getName().substring(1, 9) + "%");

        assertThat(found).containsExactly(user);
    }

    @Test
    public void findByPostsCommentsBody() {
        Comment comment = entityFactory.createComment();
        User user = comment.getPost().getAuthor();

        List<User> found = instance.findByPostsCommentsBody(comment.getBody());

        assertThat(found).containsExactly(user);
    }

    @Test
    public void findByPosts_Comments_Body() {
        Comment comment = entityFactory.createComment();
        User user = comment.getPost().getAuthor();

        List<User> found = instance.findByPosts_Comments_Body(comment.getBody());

        assertThat(found).containsExactly(user);
    }

    @Test
    public void findByIdBetween() {
        List<User> users = IntStream.range(0, 10)
                .mapToObj(i -> entityFactory.createUser())
                .collect(toList());

        List<User> found = instance.findByIdBetween(users.get(0).getId(), users.get(2).getId());

        assertThat(found).containsExactlyInAnyOrderElementsOf(users.subList(0, 3));
    }

    @Test
    public void findFirst3ByIdBetween() {
        IntStream.range(0, 10).forEach(i -> entityFactory.createUser());

        List<User> found = instance.findFirst3ByIdBetween(Long.MIN_VALUE, Long.MAX_VALUE);

        assertThat(found).size().isEqualTo(3);
    }

    @Test
    @Transactional
    public void findWithCustomQueryAndStream() {
        IntStream.range(0, 3).forEach(i -> entityFactory.createUser());

        Stream<User> found = instance.findWithCustomQueryAndStream();

        List<User> expected = entityHelper.findAll(User.class);
        assertThat(found).containsOnlyElementsOf(expected);
    }

    @Test
    public void findWithCustomQueryAndPage() {
        IntStream.range(0, 3).forEach(i -> entityFactory.createUser());

        List<User> found = instance.findWithCustomQueryAndPage(PageRequest.of(0, 1));

        assertThat(found).size().isEqualTo(1);
    }

    @Test
    public void findByName() throws Exception {
        User user = entityFactory.createUser();

        Future<User> found = instance.findByName(user.getName());

        assertThat(found.get(1, SECONDS)).isEqualTo(user);
    }

    @Test
    public void findOneById() throws Exception {
        User user = entityFactory.createUser();

        CompletableFuture<User> found = instance.findOneById(user.getId());

        assertThat(found.get(1, SECONDS)).isEqualTo(user);
    }

    @Test
    public void findOneByName() throws Exception {
        User user = entityFactory.createUser();

        ListenableFuture<User> found = instance.findOneByName(user.getName());

        assertThat(found.get(1, SECONDS)).isEqualTo(user);
    }

    @Test
    @Ignore // Stopped working after adding CustomRepository
    public void findById_StandaloneUsage() {
        RepositoryFactorySupport factory = new JpaRepositoryFactory(entityManager);
        UserRepository instance = factory.getRepository(UserRepository.class);
        User user = entityFactory.createUser();

        Optional<User> found = instance.findById(user.getId());

        assertThat(found).contains(user);
    }

    @Test
    public void customMethod() {
        User user = entityFactory.createUser();

        User result = instance.customMethod(user);

        assertThat(result).isEqualTo(user);
    }
}