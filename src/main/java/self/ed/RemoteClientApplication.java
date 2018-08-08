package self.ed;

import org.springframework.boot.devtools.RemoteSpringApplication;

public class RemoteClientApplication {
    public static void main(String[] args) {
        RemoteSpringApplication.main(new String[]{"https://localhost:8080"});
    }
}
