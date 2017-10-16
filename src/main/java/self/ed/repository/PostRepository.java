package self.ed.repository;

import org.springframework.data.repository.CrudRepository;
import self.ed.entity.Post;

/**
 * @author Anatolii
 */
public interface PostRepository extends CrudRepository<Post, Long> {
}
