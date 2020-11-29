package self.ed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import self.ed.entity.Note;
import self.ed.repository.NoteRepository;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

/**
 * @author Anatolii
 */
@RestController
@RequestMapping("/notes")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    @GetMapping
    public Flux<Note> getAll() {
        return noteRepository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Note> get(@PathVariable("id") String id) {
        return noteRepository.findById(id);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Mono<Note> create(@RequestBody Note note) {
        return noteRepository.save(note);
    }

    @PutMapping("/{id}")
    public Mono<Note> update(@PathVariable("id") String id, @RequestBody Note note) {
        return noteRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                throw new EmptyResultDataAccessException("Note not found", 1);
            }
            note.setId(id);
            return noteRepository.save(note);
        });
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public Mono<Void> delete(@PathVariable("id") String id) {
        return noteRepository.deleteById(id).then();
    }
}