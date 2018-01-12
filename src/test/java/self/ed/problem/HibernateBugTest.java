package self.ed.problem;

import org.hibernate.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import self.ed.entity.Comment;
import self.ed.entity.Post;
import self.ed.entity.User;
import self.ed.testing.support.EntityFactory;
import self.ed.testing.support.EntityHelper;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

import static javax.persistence.criteria.JoinType.INNER;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.sql.JoinType.INNER_JOIN;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HibernateBugTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private EntityHelper entityHelper;

    @Autowired
    private EntityFactory entityFactory;

    @Test
    @Transactional
    public void testOrderOfMultipleJoinsWithOnClause_JPQL_NoBug() {
        User user = entityFactory.createUser();
        Post post = entityFactory.createPost(user);
        Comment comment = entityFactory.createComment(user);

        List users = entityManager.createQuery("from User u " +
                // Switching the order of the joins does not affect the result, which is good!
                "join u.posts p on p.body = :postBody " +
                "join u.comments c on c.body = :commentBody " +
                "")
                .setParameter("postBody", post.getBody())
                .setParameter("commentBody", comment.getBody())
                .getResultList();

        assertEquals(1, users.size());
    }

    @Test
    @Transactional
    public void testOrderOfMultipleJoinsWithOnClause_CriteriaBuilder_NoBug() {
        User user = entityFactory.createUser();
        Post post = entityFactory.createPost(user);
        Comment comment = entityFactory.createComment(user);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> rootUser = cq.from(User.class);

        // Switching the order of the joins does not affect the result, which is good!
        Join<User, Post> joinUserPost = rootUser.join("posts", INNER);
        joinUserPost.on(cb.equal(joinUserPost.get("body"), post.getBody()));

        Join<User, Comment> joinUserComment = rootUser.join("comments", INNER);
        joinUserComment.on(cb.equal(joinUserComment.get("body"), comment.getBody()));

        List<User> users = entityManager.createQuery(cq.select(rootUser)).getResultList();

        assertEquals(1, users.size());
    }

    @Test
    @Transactional
    public void testOrderOfMultipleJoinsWithOnClause_HQL_NoBug() {
        User user = entityFactory.createUser();
        Post post = entityFactory.createPost(user);
        Comment comment = entityFactory.createComment(user);

        Session session = (Session) entityManager.getDelegate();
        List users = session.createQuery("from User u " +
                // Switching the order of the joins does not affect the result, which is good!
                "join u.posts p on p.body = :postBody " +
                "join u.comments c on c.body = :commentBody " +
                "")
                .setParameter("postBody", post.getBody())
                .setParameter("commentBody", comment.getBody())
                .list();

        assertEquals(1, users.size());
    }

    @Test
    @Transactional
    public void testOrderOfMultipleJoinsWithOnClause_Criteria_Bug() {
        User user = entityFactory.createUser();
        Post post = entityFactory.createPost(user);
        Comment comment = entityFactory.createComment(user);

        Session session = (Session) entityManager.getDelegate();
        List users = session.createCriteria(User.class, "u")
                // Switching the order of the joins breaks the result, which is a bug in Hibernate!
                .createAlias("u.comments", "c", INNER_JOIN, eq("c.body", comment.getBody()))
                .createAlias("u.posts", "p", INNER_JOIN, eq("p.body", post.getBody()))
                .list();

        assertEquals(1, users.size());
    }
}