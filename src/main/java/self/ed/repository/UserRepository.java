package self.ed.repository;

import org.springframework.data.repository.CrudRepository;
import self.ed.entity.User;

/**
 * @author Anatolii
 */
public interface UserRepository extends CrudRepository<User, Long> {
}
