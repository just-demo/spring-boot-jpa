package self.ed.controller.dbunit;

import org.apache.commons.beanutils.BeanUtils;
import org.dbunit.Assertion;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import self.ed.entity.User;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.dbunit.dataset.filter.DefaultColumnFilter.includedColumnsTable;
import static org.dbunit.operation.DatabaseOperation.CLEAN_INSERT;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author Anatolii
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UserControllerDbUnitTest {
    private static final String PATH_USERS = "/users";
    private static final String PATH_USER = "/users/{id}";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DataSource dataSource;

    private IDatabaseConnection connection;

    @Before
    public void setUp() throws Exception {
        this.connection = new DatabaseDataSourceConnection(dataSource);
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void testFindAll() throws Exception {
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(this.getClass().getResource("user.multiple.xml"));
        CLEAN_INSERT.execute(connection, dataSet);

        ResponseEntity<User[]> entity = restTemplate.getForEntity(PATH_USERS, User[].class);

        assertThat(entity.getStatusCode()).isEqualTo(OK);

        List<User> expectedUsers = convertTableToList(dataSet.getTable("user"), User.class);
        assertThat(entity.getBody()).containsOnlyElementsOf(expectedUsers);
    }

    @Test
    public void testFind() throws Exception {
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(this.getClass().getResource("user.multiple.xml"));
        CLEAN_INSERT.execute(connection, dataSet);

        ResponseEntity<User> entity = restTemplate.getForEntity(PATH_USER, User.class, 1L);

        assertThat(entity.getStatusCode()).isEqualTo(OK);

        User expectedUser = convertTableToList(dataSet.getTable("user"), User.class).stream()
                .filter(user -> user.getId().equals(1L))
                .findAny()
                .orElseThrow(RuntimeException::new);
        assertThat(entity.getBody()).isEqualTo(expectedUser);
    }

    @Test
    public void testCreate() throws Exception {
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(this.getClass().getResource("user.empty.xml"));
        CLEAN_INSERT.execute(connection, dataSet);
        User user = new User();
        user.setName("user1");

        ResponseEntity<User> entity = restTemplate.postForEntity(PATH_USERS, user, User.class);

        assertThat(entity.getStatusCode()).isEqualTo(CREATED);
        ITable expectedUsers = new FlatXmlDataSetBuilder().build(this.getClass().getResource("user.single.xml")).getTable("user");
        ITable actualUsers = connection.createDataSet().getTable("user");
        actualUsers = includedColumnsTable(actualUsers, expectedUsers.getTableMetaData().getColumns());
        Assertion.assertEquals(expectedUsers, actualUsers);
    }

    private <T> List<T> convertTableToList(ITable table, Class<T> clazz) throws Exception {
        List<String> columns = stream(table.getTableMetaData().getColumns())
                .map(Column::getColumnName)
                .collect(toList());

        List<T> entities = new ArrayList<>();
        int rowCount = table.getRowCount();
        for (int row = 0; row < rowCount; row++) {
            Map<String, Object> fields = new HashMap<>();
            for (String column : columns) {
                fields.put(column, table.getValue(row, column));
            }
            T entity = clazz.newInstance();
            BeanUtils.populate(entity, fields);
            entities.add(entity);
        }
        return entities;
    }
}