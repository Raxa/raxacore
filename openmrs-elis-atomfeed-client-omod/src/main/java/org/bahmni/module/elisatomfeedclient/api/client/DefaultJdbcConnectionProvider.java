package org.bahmni.module.elisatomfeedclient.api.client;

import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DefaultJdbcConnectionProvider implements JdbcConnectionProvider {

    private DataSource dataSource;

    public DefaultJdbcConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DataSourceUtils.doGetConnection(dataSource);
    }

    @Override
    public void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }
}
