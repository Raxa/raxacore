package org.bahmni.module.elisatomfeedclient.api.client;

import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.openmrs.util.DatabaseUpdater;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DefaultJdbcConnectionProvider implements JdbcConnectionProvider {

    @Override
    public Connection getConnection() throws SQLException {
        try {
            return DatabaseUpdater.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }
}
