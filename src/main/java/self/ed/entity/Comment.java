package self.ed.entity;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Anatolii
 */
@Entity
public class Comment {
    @Id
    @GeneratedValue
    private Long id;

    private String body;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id) &&
                Objects.equals(body, comment.body) &&
                Objects.equals(author, comment.author) &&
                Objects.equals(post, comment.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, body, author, post);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
