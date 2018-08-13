package self.ed.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import self.ed.entity.Note;

import java.util.UUID;

public interface NoteRepository extends ReactiveCrudRepository<Note, UUID> {
    <S extends Note> Mono<S> save(Mono<S> entity);
}
