package org.bahmni.address;

import org.springframework.jdbc.support.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddressQueryExecutor {
    private Connection connection;

    private static final String GET_PARENT_SQL = "select parent_id, name from address_hierarchy_entry where level_id = (select max(address_hierarchy_level_id) - %d from address_hierarchy_level) and address_hierarchy_entry_id = ?";
    private static final String GET_TEHSILS_SQL = "select parent_id, name from address_hierarchy_entry where name = ? and level_id = (select max(address_hierarchy_level_id) from address_hierarchy_level)";
    private static final String GET_ALL_VILLAGES = "select name from address_hierarchy_entry where level_id = (select max(address_hierarchy_level_id) from address_hierarchy_level)";

    public AddressQueryExecutor(Connection connection) {
        this.connection = connection;
    }

    public List<Integer> findTehsilIdsFor(String village) {
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        List<Integer> tehsilIdList = new ArrayList<Integer>();
        try {
            preparedStatement = connection.prepareStatement(GET_TEHSILS_SQL);
            preparedStatement.setString(1,village);
            resultSet = preparedStatement.executeQuery();
            while( resultSet.next()){
                tehsilIdList.add(resultSet.getInt("parent_id"));
            }
        } catch (SQLException e) {
            throw new AddressSanitiserException(e);
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(preparedStatement);
        }
        return  tehsilIdList;
    }

    public AddressHierarchyEntry findHigherLevelsHierarchyEntry(int hierarchyEntryId, int level) {
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        try {
            String sql = String.format(GET_PARENT_SQL, level);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, hierarchyEntryId);
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) throw new AddressSanitiserException(String.format("Cannot find parent entry for %d", hierarchyEntryId));
            return new AddressHierarchyEntry(resultSet.getInt("parent_id"), resultSet.getString("name"));
        } catch (SQLException e) {
            throw new AddressSanitiserException(e);
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(preparedStatement);
        }
    }

    public List<String> getAllVillages() {
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        List<String> villages = new ArrayList<String>();
        try {
            preparedStatement = connection.prepareStatement(GET_ALL_VILLAGES);
            resultSet = preparedStatement.executeQuery();
            while( resultSet.next()){
                villages.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            throw new AddressSanitiserException(e);
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(preparedStatement);
        }
        return villages;
    }
}
