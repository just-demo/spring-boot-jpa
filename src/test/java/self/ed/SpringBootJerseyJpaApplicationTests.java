//package self.ed;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.junit4.SpringRunner;
//import self.ed.entity.User;
//import self.ed.testing.support.EntityHelper;
//
//import java.util.List;
//import java.util.stream.IntStream;
//
//import static org.assertj.core.api.Java6Assertions.assertThat;
//import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = RANDOM_PORT)
//public class SpringBootJerseyJpaApplicationTests {
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Autowired
//    private EntityHelper entityHelper;
//
//    @Test
//    public void contextLoads() {
//        IntStream.range(0, 3).forEach(i -> createUser());
//        ResponseEntity<User[]> entity = restTemplate.getForEntity("/users", User[].class);
//        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        List<User> allUsers = entityHelper.findAll(User.class);
//        assertThat(entity.getBody()).containsOnlyElementsOf(allUsers);
//    }
//
//    private User createUser() {
//        return entityHelper.create(User.class);
//    }
//}
