package self.ed.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import self.ed.entity.User;
import self.ed.entity.projection.IdAndNameConcat;
import self.ed.entity.projection.NameOnly;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.LOAD;

/**
 * @author Anatolii
 */
public interface UserRepository extends PagingAndSortingRepository<User, Long>, CustomRepository<User> {
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

    List<User> findByNameContaining(String name);

    List<User> findByPostsCommentsBody(String body);

    List<User> findByPosts_Comments_Body(String body);

    List<User> findByIdBetween(Long from, Long to);

    List<User> findFirst3ByIdBetween(Long from, Long to);

    @Query("select u from User u")
    Stream<User> findWithCustomQueryAndStream();

    @Query("select u from User u")
    List<User> findWithCustomQueryAndPage(Pageable pageable);

    @Query("select u from User u where name = ?1")
    List<User> findByNameQuery(String name);

    @Query("select u from User u where name = :name")
    List<User> findByNameQueryNamed(@Param("name") String name);

    @Query(value = "select * from user where name = ?1", nativeQuery = true)
    List<User> findByNameNative(String name);

    @Query(value = "select * from user where name = ?1",
            countQuery = "select count(*) from user where name = ?1",
            nativeQuery = true)
    Page<User> findByNameNativePage(String name, Pageable pageable);

    // Uses named query
    List<User> findByNameDifferentFrom(String name);

    //@Async
    Future<User> findByName(String name);

    //@Async
    CompletableFuture<User> findOneById(Long id);

    //@Async
    ListenableFuture<User> findOneByName(String name);

    @Query("select count(e) from #{#entityName} e")
    long countWithSpEL();

    @Transactional
    @Modifying
    @Query("update User u set u.name = ?2 where u.name = ?1")
    int updateNameWithQuery(String sourceName, String targetName);

    @Transactional
    @Modifying
    @Query("delete from User where name = ?1")
    void deleteByNameWithQuery(String name);

    @EntityGraph(value = "User.posts", type = LOAD)
    User findTopByName(String name);

    @EntityGraph(attributePaths = {"posts"})
    User findFirstByName(String name);

    List<NameOnly> findByIdIn(Long... ids);

    List<IdAndNameConcat> findByNameIn(String... names);

    <T> Collection<T> findTopById(Long id, Class<T> type);
}
