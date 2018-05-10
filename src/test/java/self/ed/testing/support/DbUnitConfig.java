package self.ed.testing.support;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

//TODO: remove
@Configuration
public class DbUnitConfig {
    @Bean
    public DatabaseConfigBean dbUnitDatabaseConfig() {
        DatabaseConfigBean config = new DatabaseConfigBean();
        config.setEscapePattern("\"");
        config.setCaseSensitiveTableNames(true);
        return config;
    }

    //@Bean
    public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection(DataSource dataSource, DatabaseConfigBean dbUnitDatabaseConfig) {
        DatabaseDataSourceConnectionFactoryBean connection = new DatabaseDataSourceConnectionFactoryBean(dataSource);
        connection.setDatabaseConfig(dbUnitDatabaseConfig);
        return connection;
    }
}
