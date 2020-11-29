package self.ed.controller;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import self.ed.entity.Note;
import self.ed.repository.NoteRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static self.ed.testing.support.RandomUtils.random;

/**
 * @author Anatolii
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
public class NoteControllerTest {
    private static final String PATH_NOTES = "/notes";
    private static final String PATH_NOTE = "/notes/{id}";

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testFindAll() {
        Note note = createNote();

        webTestClient.get().uri(PATH_NOTES)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Note.class)
                .contains(note);
    }

    @Test
    public void testFind() {
        Note note = createNote();

        webTestClient.get().uri(PATH_NOTE, note.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Note.class)
                .isEqualTo(note);
    }

    @Test
    public void testFind_NotFound() {
        webTestClient.get().uri(PATH_NOTE, random(String.class))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testCreate() {
        Note note = random(Note.class, "id");

        Note returned = webTestClient.post().uri(PATH_NOTES)
                .bodyValue(note)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Note.class)
                .returnResult().getResponseBody();

        assertThat(returned.getBody()).isEqualTo(note.getBody());
        assertThat(returned.getId()).isNotNull();
        Note persisted = find(returned);
        assertThat(persisted).isEqualTo(returned);
    }

    @Test
    public void testUpdate() {
        Note note = createNote();
        note.setBody(random(String.class));

        webTestClient.put().uri(PATH_NOTE, note.getId())
                .bodyValue(note)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(note.getId())
                .jsonPath("$.body").isEqualTo(note.getBody());

        Note persisted = find(note);
        assertThat(persisted).isEqualTo(note);
    }

    @Test
    public void testUpdate_NotFound() {
        Note note = random(Note.class);

        webTestClient.put().uri(PATH_NOTE, note.getId())
                .bodyValue(note)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testDelete() {
        Note note = createNote();

        webTestClient.delete().uri(PATH_NOTE, note.getId())
                .exchange()
                .expectStatus().isNoContent();

        assertThat(find(note)).isNull();
    }

    @Test
    @Ignore // TODO: fix
    public void testDelete_NotFound() {
        webTestClient.delete().uri(PATH_NOTE, random(String.class))
                .exchange()
                .expectStatus().isNotFound();
    }

    private Note createNote() {
        Note note = random(Note.class, "id");
        return mongoTemplate.save(note);
    }

    private Note find(Note note) {
        return mongoTemplate.findById(note.getId(), Note.class);
    }
}