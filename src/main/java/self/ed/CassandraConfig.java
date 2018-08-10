package self.ed;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.cassandraunit.utils.EmbeddedCassandraServerHelper.startEmbeddedCassandra;
import static org.springframework.data.cassandra.config.SchemaAction.CREATE_IF_NOT_EXISTS;
import static org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification.createKeyspace;

@Configuration
public class CassandraConfig extends AbstractCassandraConfiguration {
    public CassandraConfig() throws Exception {
        // TODO: find a better place to start embedded Cassandra
        startEmbeddedCassandra();
    }

    @Override
    protected int getPort() {
        return 9142;
    }

    @Override
    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        return singletonList(createKeyspace(getKeyspaceName()).ifNotExists());
    }

    @Override
    public SchemaAction getSchemaAction() {
        return CREATE_IF_NOT_EXISTS;
    }

    @Override
    protected String getKeyspaceName() {
        return "test_keyspace";
    }
}
