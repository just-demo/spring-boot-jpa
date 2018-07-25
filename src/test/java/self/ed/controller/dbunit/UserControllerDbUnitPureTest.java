package self.ed.controller.dbunit;

import org.dbunit.Assertion;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.ColumnFilterTable;
import org.dbunit.dataset.DataSetException;
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
import self.ed.testing.support.EntityHelper;

import javax.sql.DataSource;
import java.net.URL;
import java.util.Set;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.CREATED;

/**
 * @author Anatolii
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
//@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
//@DatabaseSetup(value = "user.empty.xml")
public class UserControllerDbUnitPureTest {
    private static final String PATH_USERS = "/users";
    private static final String PATH_USER = "/users/{id}";

    @Autowired
    private DataSource dataSource;

    private IDatabaseTester databaseTester;

    @Before
    public void setUp() {
        databaseTester = new DataSourceDatabaseTester(dataSource);
    }

    @After
    public void tearDown() throws Exception {
        databaseTester.onTearDown();
    }
//
//    protected void setUp2() throws Exception
//    {
//
//        // Execute the tested code that modify the database here
//        ...
//
//
//        // Fetch database data after executing your code
//        IDataSet databaseDataSet = getConnection().createDataSet();
//        ITable actualTable = databaseDataSet.getTable("TABLE_NAME");
//
//        // Load expected data from an XML dataset
//        IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(new File("expectedDataSet.xml"));
//        ITable expectedTable = expectedDataSet.getTable("TABLE_NAME");
//
//        // Assert actual database table match expected table
//        Assertion.assertEquals(expectedTable, actualTable);
//
//
//        ITable actualJoinData = getConnection().createQueryTable("RESULT_NAME",
//                "SELECT * FROM TABLE1, TABLE2 WHERE ...");
//
//
//        ITable filteredTable = DefaultColumnFilter.includedColumnsTable(actual,
//                expected.getTableMetaData().getColumns());
//        Assertion.assertEquals(expected, filteredTable);
//
//        super.setUp();
//
//        // initialize your database connection here
//        IDatabaseConnection connection = null;
//        // ...
//
//        // initialize your dataset here
//        IDataSet dataSet = null;
//        // ...
//
//        try1
//        {
//            DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
//        }
//        finally
//        {
//            connection.close();
//        }
//    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EntityHelper entityHelper;
//
//    @Test
//    public void testFindAll() {
//        databaseTester = new DataSourceDatabaseTester(dataSource);
//        IDataSet dataSet = new FlatXmlDataSetBuilder().build(new File("user.multiple.xml"));
//        databaseTester.setDataSet( dataSet );
//        databaseTester.onSetup();
//
//        ResponseEntity<User[]> entity = restTemplate.getForEntity(PATH_USERS, User[].class);
//
//        assertThat(entity.getStatusCode()).isEqualTo(OK);
//
//        IDataSet databaseDataSet = databaseTester.getConnection().createDataSet();
//        ITable actualUsers = databaseDataSet.getTable("user");
//
//        ITable expectedUsers = new FlatXmlDataSetBuilder().build(new File("user.multiple.xml")).getTable("user");
//
//
//        entity.getBody()
//
//        List<User> expectedUsers = entityHelper.findAll(User.class);
//        assertThat(entity.getBody()).containsOnlyElementsOf(expectedUsers);
//    }
//
//    @Test
//    @DatabaseSetup(value = "user.multiple.xml")
//    public void testFind() {
//        ResponseEntity<User> entity = restTemplate.getForEntity(PATH_USER, User.class, 1L);
//
//        assertThat(entity.getStatusCode()).isEqualTo(OK);
//        User expectedUser = entityHelper.find(User.class, 1L);
//        assertThat(entity.getBody()).isEqualTo(expectedUser);
//    }

    @Test
    public void testCreate() throws Exception {
        databaseTester = new DataSourceDatabaseTester(dataSource);
        IDataSet dataSet = readFile("user.empty.xml");
        databaseTester.setDataSet(dataSet);
        databaseTester.onSetup();
        User user = new User();
        user.setName("user1");

        ResponseEntity<User> entity = restTemplate.postForEntity(PATH_USERS, user, User.class);

        assertThat(entity.getStatusCode()).isEqualTo(CREATED);
        ITable actualUsers = databaseTester.getConnection().createDataSet().getTable("user");
        actualUsers = filterColumns(actualUsers, "name");
        ITable expectedUsers = readFile("user.single.xml").getTable("user");
        //        ITable actualJoinData = getConnection().createQueryTable("RESULT_NAME",
        //                "SELECT * FROM TABLE1, TABLE2 WHERE ...");
        Assertion.assertEquals(expectedUsers, actualUsers);
    }

    private ITable filterColumns(ITable table, String... columns) throws DataSetException {
        Set<String> columnSet = stream(columns)
                .map(String::toLowerCase)
                .collect(toSet());
        return new ColumnFilterTable(table, (tableName, column) -> columnSet.contains(column.getColumnName().toLowerCase()));
    }

    private IDataSet readFile(String fileName) throws DataSetException {
        return new FlatXmlDataSetBuilder().build(getResource(fileName));
    }

    private URL getResource(String fileName) {
        return this.getClass().getResource(fileName);
    }
}