package self.ed.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import self.ed.entity.User;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Stream;

/**
 * @author Anatolii
 */
public interface UserRepository extends PagingAndSortingRepository<User, Long>, CustomUserRepository {
    Page<User> findAllByName(String name, Pageable pageable);

    List<User> findAllByNameIn(Collection<String> names);

    List<User> findAllByNameIn(Collection<String> names, Pageable pageable);

    long countByName(String name);

    @Transactional
    long deleteByName(String name);

    @Transactional
    List<User> removeByName(String name);

    User getByName(String name);

    @NonNull
    User getById(Long id);

    List<User> findDistinctByIdOrNameIgnoreCaseOrderByIdDesc(Long id, String name);

    List<User> findByNameLike(String name);

    List<User> findByPostsCommentsBody(String body);

    List<User> findByPosts_Comments_Body(String body);

    List<User> findByIdBetween(Long from, Long to);

    List<User> findFirst3ByIdBetween(Long from, Long to);

    @Query("select u from User u")
    Stream<User> findWithCustomQueryAndStream();

    @Query("select u from User u")
    List<User> findWithCustomQueryAndPage(Pageable pageable);


    //@Async
    Future<User> findByName(String name);

    //@Async
    CompletableFuture<User> findOneById(Long id);

    //@Async
    ListenableFuture<User> findOneByName(String name);
}
