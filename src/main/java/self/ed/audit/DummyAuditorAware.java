package self.ed.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Optional.of;

@Component
public class DummyAuditorAware implements AuditorAware<String> {
    public Optional<String> getCurrentAuditor() {
        return of("dummy");
    }
}