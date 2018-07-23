package self.ed.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import self.ed.entity.Comment;
import self.ed.entity.Post;
import self.ed.testing.support.EntityFactory;
import self.ed.testing.support.EntityHelper;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpEntity.EMPTY;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.*;
import static self.ed.testing.support.RandomUtils.random;

/**
 * @author Anatolii
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class PostControllerTest {
    private static final String PATH_POSTS = "/posts";
    private static final String PATH_POST = "/posts/{id}";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EntityHelper entityHelper;

    @Autowired
    private EntityFactory entityFactory;

    @Test
    public void testFindAll() {
        IntStream.range(0, 3).forEach(i -> entityFactory.createPost());

        ResponseEntity<Post[]> entity = restTemplate.getForEntity(PATH_POSTS, Post[].class);

        assertThat(entity.getStatusCode()).isEqualTo(OK);
        List<Post> expectedPosts = entityHelper.findAll(Post.class);
        assertThat(entity.getBody()).containsOnlyElementsOf(expectedPosts);
    }

    @Test
    public void testFind() {
        Post post = entityFactory.createPost();

        ResponseEntity<Post> entity = restTemplate.getForEntity(PATH_POST, Post.class, post.getId());

        assertThat(entity.getStatusCode()).isEqualTo(OK);
        assertThat(entity.getBody()).isEqualTo(post);
    }

    @Test
    public void testFind_CommentsNotIncluded() {
        Post post = entityFactory.createPost();
        entityFactory.createComment(post);

        ResponseEntity<Post> entity = restTemplate.getForEntity(PATH_POST, Post.class, post.getId());

        assertThat(entity.getStatusCode()).isEqualTo(OK);
        assertThat(entity.getBody().getComments()).isNull();
    }

    @Test
    public void testFind_NotFound() {
        ResponseEntity<String> entity = restTemplate.getForEntity(PATH_POST, String.class, Long.MAX_VALUE);

        assertThat(entity.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    public void testCreate() {
        Post post = random(Post.class, "id");
        post.setAuthor(entityFactory.createUser());

        ResponseEntity<Post> entity = restTemplate.postForEntity(PATH_POSTS, post, Post.class);

        assertThat(entity.getStatusCode()).isEqualTo(CREATED);
        Post returned = entity.getBody();
        assertThat(returned.getTitle()).isEqualTo(post.getTitle());
        assertThat(returned.getBody()).isEqualTo(post.getBody());
        assertThat(returned.getAuthor()).isEqualTo(post.getAuthor());
        assertThat(returned.getComments()).isNullOrEmpty();
        assertThat(returned.getId()).isNotNull();
        Post persisted = entityHelper.find(returned, "comments");
        assertThat(persisted.getTitle()).isEqualTo(post.getTitle());
        assertThat(persisted.getBody()).isEqualTo(post.getBody());
        assertThat(persisted.getAuthor()).isEqualTo(post.getAuthor());
        assertThat(persisted.getComments()).isEmpty();
    }

    @Test
    public void testUpdate() {
        Post post = entityFactory.createPost();
        post.setTitle(random(String.class));
        post.setBody(random(String.class));

        ResponseEntity<Post> entity = restTemplate.exchange(PATH_POST, PUT, new HttpEntity<>(post), Post.class, post.getId());

        assertThat(entity.getStatusCode()).isEqualTo(OK);
        Post returned = entity.getBody();
        assertThat(returned.getTitle()).isEqualTo(post.getTitle());
        assertThat(returned.getBody()).isEqualTo(post.getBody());
        assertThat(returned.getId()).isEqualTo(post.getId());
        Post persisted = entityHelper.find(post);
        assertThat(persisted.getTitle()).isEqualTo(post.getTitle());
        assertThat(persisted.getBody()).isEqualTo(post.getBody());
    }

    @Test
    public void testUpdate_CannotChangeMood() {
        String positiveTitle = "Such a wonderful day!";
        String negativeTitle = "The weather is awful...";
        Post post = createPost(positiveTitle);
        post.setTitle(negativeTitle);

        ResponseEntity<String> entity = restTemplate.exchange(PATH_POST, PUT, new HttpEntity<>(post), String.class, post.getId());

        assertThat(entity.getStatusCode()).isEqualTo(BAD_REQUEST);
        Post persisted = entityHelper.find(post);
        assertThat(persisted.getTitle()).isEqualTo(positiveTitle);
    }

    @Test
    public void testUpdate_NotFound() {
        Post post = random(Post.class);
        post.setId(Long.MAX_VALUE);

        ResponseEntity<String> entity = restTemplate.exchange(PATH_POST, PUT, new HttpEntity<>(post), String.class, post.getId());

        assertThat(entity.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    public void testDelete() {
        Post post = entityFactory.createPost();

        ResponseEntity<String> entity = restTemplate.exchange(PATH_POST, DELETE, EMPTY, String.class, post.getId());

        assertThat(entity.getStatusCode()).isEqualTo(NO_CONTENT);
        assertThat(entityHelper.find(post)).isNull();
    }

    @Test
    public void testDelete_WithComments() {
        Post post = entityFactory.createPost();
        Comment comment = entityFactory.createComment(post);

        ResponseEntity<String> entity = restTemplate.exchange(PATH_POST, DELETE, EMPTY, String.class, post.getId());

        assertThat(entity.getStatusCode()).isEqualTo(NO_CONTENT);
        assertThat(entityHelper.find(post)).isNull();
        assertThat(entityHelper.find(comment)).isNull();
    }


    @Test
    public void testDelete_NotFound() {
        ResponseEntity<String> entity = restTemplate.exchange(PATH_POST, DELETE, EMPTY, String.class, Long.MAX_VALUE);

        assertThat(entity.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    private Post createPost(String title) {
        Post post = entityFactory.createPost();
        post.setTitle(title);
        entityHelper.merge(post);
        return post;
    }
}