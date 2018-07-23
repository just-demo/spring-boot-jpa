package self.ed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.*;
import self.ed.entity.Post;
import self.ed.repository.PostRepository;

import java.util.Optional;

import static java.util.Arrays.stream;
import static org.apache.commons.lang3.ArrayUtils.contains;
import static org.springframework.http.HttpStatus.*;

/**
 * @author Anatolii
 */
@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping
    public Iterable<Post> getAll() {
        return postRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Post> get(@PathVariable("id") Long id) {
        return postRepository.findById(id);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Post create(@RequestBody Post post) {
        post.setId(null);
        return postRepository.save(post);
    }

    @PutMapping("/{id}")
    public Post update(@PathVariable("id") Long id, @RequestBody Post post) {
        Post existingPost = postRepository.findById(id).orElseThrow(() -> new EmptyResultDataAccessException("Post not found", 1));

        if (getMood(existingPost) != getMood(post)) {
            throw new IllegalArgumentException("Don't change your mood!");
        }

        post.setId(id);
        return postRepository.save(post);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        postRepository.deleteById(id);
    }

    /**
     * @return 1 - positive, 0 - neutral, -1 negative
     */
    private int getMood(Post post) {
        String[] positiveWords = {"wonderful"};
        String[] negativeWords = {"awful"};
        return Integer.compare(stream(post.getTitle().split("\\W+"))
                .map(String::toLowerCase)
                .mapToInt(word -> contains(positiveWords, word) ? 1 : contains(negativeWords, word) ? -1 : 0)
                .sum(), 0);
    }
}