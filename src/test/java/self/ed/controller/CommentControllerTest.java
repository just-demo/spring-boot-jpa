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
import self.ed.testing.support.EntityFactory;
import self.ed.testing.support.EntityHelper;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
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
public class CommentControllerTest {
    private static final String PATH_COMMENTS = "/comments";
    private static final String PATH_COMMENT = "/comments/{id}";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EntityHelper entityHelper;

    @Autowired
    private EntityFactory entityFactory;

    @Test
    public void testFindAll() {
        IntStream.range(0, 3).forEach(i -> entityFactory.createComment());

        ResponseEntity<Comment[]> entity = restTemplate.getForEntity(PATH_COMMENTS, Comment[].class);

        assertThat(entity.getStatusCode()).isEqualTo(OK);
        List<Comment> expectedComments = entityHelper.findAll(Comment.class);
        assertThat(entity.getBody()).containsOnlyElementsOf(expectedComments);
    }

    @Test
    public void testFind() {
        Comment comment = entityFactory.createComment();

        ResponseEntity<Comment> entity = restTemplate.getForEntity(PATH_COMMENT, Comment.class, comment.getId());

        assertThat(entity.getStatusCode()).isEqualTo(OK);
        assertThat(entity.getBody()).isEqualTo(comment);
    }

    @Test
    public void testFind_NotFound() {
        ResponseEntity<String> entity = restTemplate.getForEntity(PATH_COMMENT, String.class, Long.MAX_VALUE);

        assertThat(entity.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    public void testCreate() {
        Comment comment = random(Comment.class, "id");
        comment.setAuthor(entityFactory.createUser());
        comment.setPost(entityFactory.createPost());

        ResponseEntity<Comment> entity = restTemplate.postForEntity(PATH_COMMENTS, comment, Comment.class);

        assertThat(entity.getStatusCode()).isEqualTo(CREATED);
        Comment returned = entity.getBody();
        assertThat(returned.getBody()).isEqualTo(comment.getBody());
        assertThat(returned.getAuthor()).isEqualTo(comment.getAuthor());
        assertThat(returned.getPost()).isEqualTo(comment.getPost());
        assertThat(returned.getId()).isNotNull();
        Comment persisted = entityHelper.find(returned);
        assertThat(persisted.getBody()).isEqualTo(comment.getBody());
        assertThat(persisted.getAuthor()).isEqualTo(comment.getAuthor());
        assertThat(persisted.getPost()).isEqualTo(comment.getPost());
    }

    @Test
    public void testUpdate() {
        Comment comment = entityFactory.createComment();
        comment.setBody(random(String.class));

        ResponseEntity<Comment> entity = restTemplate.exchange(PATH_COMMENT, PUT, new HttpEntity<>(comment), Comment.class, comment.getId());

        assertThat(entity.getStatusCode()).isEqualTo(OK);
        Comment returned = entity.getBody();
        assertThat(returned.getBody()).isEqualTo(comment.getBody());
        assertThat(returned.getId()).isEqualTo(comment.getId());
        Comment persisted = entityHelper.find(comment);
        assertThat(persisted.getBody()).isEqualTo(comment.getBody());
    }

    @Test
    public void testUpdate_NotFound() {
        Comment comment = random(Comment.class);
        comment.setId(Long.MAX_VALUE);

        ResponseEntity<String> entity = restTemplate.exchange(PATH_COMMENT, PUT, new HttpEntity<>(comment), String.class, comment.getId());

        assertThat(entity.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    public void testDelete() {
        Comment comment = entityFactory.createComment();

        ResponseEntity<String> entity = restTemplate.exchange(PATH_COMMENT, DELETE, EMPTY, String.class, comment.getId());

        assertThat(entity.getStatusCode()).isEqualTo(NO_CONTENT);
        assertThat(entityHelper.find(comment)).isNull();
    }

    @Test
    public void testDelete_NotFound() {
        ResponseEntity<String> entity = restTemplate.exchange(PATH_COMMENT, DELETE, EMPTY, String.class, Long.MAX_VALUE);

        assertThat(entity.getStatusCode()).isEqualTo(NOT_FOUND);
    }
}