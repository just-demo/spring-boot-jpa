package self.ed.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;
import self.ed.entity.User;

import java.util.Collection;
import java.util.List;

/**
 * @author Anatolii
 */
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    Page<User> findAllByName(String name, Pageable pageable);
    List<User> findAllByNameIn(Collection<String> names);
    long countByName(String name);
    @Transactional
    long deleteByName(String name);
    @Transactional
    List<User> removeByName(String name);
}
