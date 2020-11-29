package self.ed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import self.ed.entity.Note;
import self.ed.repository.NoteRepository;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.*;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

// To test NoteRouter @Configuration annotation must be enabled and spring-boot-starter-web must be commented out
// in gradle script (a few other entity endpoints will fail then)
// TODO: make NoteController and NoteRouter interchangeable without a need to comment out classes manually
//@Configuration
public class NoteRouter {

    @Autowired
    private NoteRepository noteRepository;

    @Bean
    public RouterFunction<ServerResponse> noteRoute() {
        return route(GET("/notes"), this::getAll)
                .andRoute(POST("/notes"), this::create)
                .andRoute(GET("/notes/{id}"), this::get)
                .andRoute(PUT("/notes/{id}"), this::update)
                .andRoute(DELETE("/notes/{id}"), this::delete);
    }

    private Mono<ServerResponse> getAll(ServerRequest ignored) {
        return ok().contentType(APPLICATION_JSON).body(noteRepository.findAll(), Note.class);
    }

    private Mono<ServerResponse> get(ServerRequest request) {
        String id = request.pathVariable("id");
        return noteRepository.findById(id)
                .flatMap(note -> ok().contentType(APPLICATION_JSON).body(fromValue(note)))
                .switchIfEmpty(notFound().build());
    }

    private Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(Note.class)
                .flatMap(note -> noteRepository.save(note))
                .flatMap(note -> created(fromPath("/notes/" + note.getId()).build().toUri())
                        .contentType(APPLICATION_JSON)
                        .body(fromValue(note)));
    }

    private Mono<ServerResponse> update(ServerRequest request) {
        String id = request.pathVariable("id");
        return noteRepository.existsById(id)
                .filter(exists -> exists)
                .flatMap(ignored -> request.bodyToMono(Note.class).flatMap(note -> {
                    note.setId(id);
                    return noteRepository.save(note);
                }))
                .flatMap(note -> ok().contentType(APPLICATION_JSON).body(fromValue(note)))
                .switchIfEmpty(notFound().build());
    }

    private Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");
        return noteRepository
                .findById(id)
                .flatMap(note -> noContent().build(noteRepository.delete(note)))
                .switchIfEmpty(notFound().build());
    }
}
