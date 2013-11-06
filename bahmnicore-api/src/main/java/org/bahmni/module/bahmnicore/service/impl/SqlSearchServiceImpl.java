package org.bahmni.module.bahmnicore.service.impl;

import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.service.RowMapper;
import org.bahmni.module.bahmnicore.service.SqlSearchService;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.util.DatabaseUpdater;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SqlSearchServiceImpl implements SqlSearchService {
    private AdministrationService administrationService;

    private static Logger logger = Logger.getLogger(SqlSearchServiceImpl.class);

    public void setAdministrationService(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @Override
    public List<SimpleObject>  search(String queryId, Map<String, String[]> params) {
        List<SimpleObject> results = new ArrayList<>();
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            conn = DatabaseUpdater.getConnection();
            statement = conn.prepareStatement(getSql(queryId, params));
            resultSet = statement.executeQuery();

            RowMapper rowMapper = new RowMapper();
            while (resultSet.next()) {
                results.add(rowMapper.mapRow(resultSet));
            }
            return results;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                logger.warn("Could not close db statement or resultset", e);
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.warn("Could not close db connection", e);
            }
        }
    }

    private String getSql(String queryId, Map<String, String[]> params) {
        String query = administrationService.getGlobalProperty(queryId);
        if (query == null) throw new RuntimeException("No such query:" + queryId);
        for (String key : params.keySet()) {
            query = query.replace("@" + key, params.get(key)[0]);
        }
        return query;
    }
}
