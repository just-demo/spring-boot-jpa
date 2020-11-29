package self.ed.repository.dbunit;

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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import self.ed.entity.User;
import self.ed.repository.UserRepository;

import javax.sql.DataSource;
import java.util.*;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.dbunit.dataset.filter.DefaultColumnFilter.includedColumnsTable;
import static org.dbunit.operation.DatabaseOperation.CLEAN_INSERT;

/**
 * @author Anatolii
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryDbUnitTest {
    @Autowired
    private UserRepository instance;

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
    @Ignore //TODO: started failing with "Referential integrity constraint violation" when being run with other tests
    public void testFindAll() throws Exception {
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(this.getClass().getResource("user.multiple.xml"));
        CLEAN_INSERT.execute(connection, dataSet);

        Iterable<User> actual = instance.findAll();

        List<User> expected = convertTableToList(dataSet.getTable("user"), User.class);
        assertThat(actual).hasSameElementsAs(expected);
    }

    @Test
    @Ignore //TODO: started failing with "Referential integrity constraint violation" when being run with other tests
    public void testFind() throws Exception {
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(this.getClass().getResource("user.multiple.xml"));
        CLEAN_INSERT.execute(connection, dataSet);

        Optional<User> found = instance.findById(1L);

        User expected = convertTableToList(dataSet.getTable("user"), User.class).stream()
                .filter(user -> user.getId().equals(1L))
                .findAny()
                .orElseThrow(RuntimeException::new);
        assertThat(found).contains(expected);
    }

    @Test
    public void testSave() throws Exception {
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(this.getClass().getResource("user.empty.xml"));
        CLEAN_INSERT.execute(connection, dataSet);
        User user = new User();
        user.setName("user1");

        instance.save(user);

        ITable expected = new FlatXmlDataSetBuilder().build(this.getClass().getResource("user.single.xml")).getTable("user");
        ITable actual = connection.createDataSet().getTable("user");
        actual = includedColumnsTable(actual, expected.getTableMetaData().getColumns());
        Assertion.assertEquals(expected, actual);
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
            T entity = clazz.getDeclaredConstructor().newInstance();
            BeanUtils.populate(entity, fields);
            entities.add(entity);
        }
        return entities;
    }
}