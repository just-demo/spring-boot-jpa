package self.ed.testing.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import self.ed.entity.Comment;
import self.ed.entity.Post;
import self.ed.entity.User;

/**
 * @author Anatolii
 */
@Component
public class EntityFactory {
    @Autowired
    private EntityHelper entityHelper;

    public User createUser() {
        return entityHelper.create(User.class);
    }

    public Post createPost() {
        return createPost(createUser());
    }

    public Post createPost(User author) {
        return entityHelper.create(Post.class, post -> {
            post.setAuthor(author);
            author.getPosts().add(post);
        });
    }

    public Comment createComment() {
        return createComment(createUser());
    }

    public Comment createComment(User author) {
        return createComment(author, createPost());
    }

    public Comment createComment(Post post) {
        return createComment(createUser(), post);
    }

    public Comment createComment(User author, Post post) {
        return entityHelper.create(Comment.class, comment -> {
            comment.setPost(post);
            post.getComments().add(comment);
            comment.setAuthor(author);
            author.getComments().add(comment);
        });
    }
}
