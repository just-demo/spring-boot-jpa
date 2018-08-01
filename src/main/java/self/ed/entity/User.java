package self.ed.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.SEQUENCE;

/**
 * @author Anatolii
 */
@Entity
@NamedQuery(name = "User.findByNameDifferentFrom", query = "select u from User u where u.name <> ?1")
public class User {
    @Id
    // Cannot use IDENTITY because of quoted "user": PostgreSQL would try to use an incorrect
    // sequence name "user"_"id"_seq when identifiers are quoted either globally or locally
    @GeneratedValue(strategy = SEQUENCE, generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq")
    private Long id;
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "author", fetch = LAZY, cascade = ALL)
    private List<Post> posts;

    @JsonIgnore
    @OneToMany(mappedBy = "author", fetch = LAZY, cascade = ALL)
    private List<Comment> comments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toStringExclude(this, "posts", "comments");
    }
}