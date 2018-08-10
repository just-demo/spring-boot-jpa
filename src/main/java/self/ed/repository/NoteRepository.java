package self.ed.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import self.ed.entity.Note;

import java.util.UUID;

public interface NoteRepository extends ReactiveCrudRepository<Note, UUID> {
}
