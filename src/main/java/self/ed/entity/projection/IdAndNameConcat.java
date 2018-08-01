package self.ed.entity.projection;

import org.springframework.beans.factory.annotation.Value;

public interface IdAndNameConcat {
    @Value("#{target.id + ' ' + target.name}")
    String getIdAndName();
}
