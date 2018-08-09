package self.ed;

import org.springframework.boot.devtools.RemoteSpringApplication;

public class RemoteClientApplication {
    public static void main(String[] args) {
        // 1. run SpringBootJpaApplication as java -jar
        // 2. run RemoteClientApplication from IDE
        // 3. Build > Rebuild Project
        // 4. The changes are pushed to SpringBootJpaApplication
        RemoteSpringApplication.main(new String[]{"http://localhost:8080"});
    }
}
