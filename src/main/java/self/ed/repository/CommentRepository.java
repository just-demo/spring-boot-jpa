package self.ed.repository;

import org.springframework.data.repository.CrudRepository;
import self.ed.entity.Comment;

/**
 * @author Anatolii
 */
public interface CommentRepository extends CrudRepository<Comment, Long> {
}
