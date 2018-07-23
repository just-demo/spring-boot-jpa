package self.ed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.*;
import self.ed.entity.Comment;
import self.ed.repository.CommentRepository;

import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

/**
 * @author Anatolii
 */
@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping
    public Iterable<Comment> getAll() {
        return commentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Comment> get(@PathVariable("id") Long id) {
        return commentRepository.findById(id);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Comment create(@RequestBody Comment comment) {
        comment.setId(null);
        return commentRepository.save(comment);
    }

    @PutMapping("/{id}")
    public Comment update(@PathVariable("id") Long id, @RequestBody Comment comment) {
        if (!commentRepository.existsById(id)) {
            throw new EmptyResultDataAccessException("Comment not found", 1);
        }
        comment.setId(id);
        return commentRepository.save(comment);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        commentRepository.deleteById(id);
    }
}