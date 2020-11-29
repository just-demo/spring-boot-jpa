package self.ed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import self.ed.entity.User;
import self.ed.repository.UserRepository;

import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

/**
 * @author Anatolii
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public Iterable<User> getAll() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<User> get(@PathVariable("id") Long id) {
        return userRepository.findById(id);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public User create(@RequestBody User user) {
        user.setId(null);
        return userRepository.save(user);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable("id") Long id, @RequestBody User user) {
        if (!userRepository.existsById(id)) {
            throw new EmptyResultDataAccessException("User not found", 1);
        }
        user.setId(id);
        return userRepository.save(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        userRepository.deleteById(id);
    }

    @GetMapping("/page")
    HttpEntity<PagedModel<EntityModel<User>>> getAllPage(Pageable pageable, PagedResourcesAssembler<User> assembler) {
        Page<User> users = userRepository.findAll(pageable);
        return new ResponseEntity<>(assembler.toModel(users), OK);
    }
}