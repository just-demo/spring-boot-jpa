package self.ed.entity;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document
public class Note {
    @Id
    private String id;
    private String body;

    public Note() {
    }

    public Note(String body) {
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(id, note.id) &&
                Objects.equals(body, note.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, body);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
