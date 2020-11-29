package self.ed.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import self.ed.entity.Note;

public interface NoteRepository extends ReactiveCrudRepository<Note, String> {
}
