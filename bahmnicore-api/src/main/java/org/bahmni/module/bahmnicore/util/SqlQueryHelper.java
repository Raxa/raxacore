package org.bahmni.module.bahmnicore.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlQueryHelper {
    private final Pattern paramPlaceHolderPattern;
    private static final String PARAM_PLACE_HOLDER_REGEX = "\\$\\{[^{]*\\}";

    public SqlQueryHelper() {
        this.paramPlaceHolderPattern = Pattern.compile(PARAM_PLACE_HOLDER_REGEX);
    }

    List<String> getParamNamesFromPlaceHolders(String query){
        List<String> params  = new ArrayList<>();
        Matcher matcher = paramPlaceHolderPattern.matcher(query);
        while(matcher.find()){
            params.add(stripDelimiters(matcher.group()));
        }
        return params;
    }

    private String stripDelimiters(String text) {
        return text.replaceAll("[${}]", "");
    }

    String transformIntoPreparedStatementFormat(String queryString){
        return  queryString.replaceAll(PARAM_PLACE_HOLDER_REGEX,"?");
    }

    public PreparedStatement constructPreparedStatement(String queryString,Map<String, String[]> params,Connection conn) throws SQLException {
        List<String> paramNamesFromPlaceHolders = getParamNamesFromPlaceHolders(queryString);
        String statement = transformIntoPreparedStatementFormat(queryString);
        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        if(params != null ){
            int i=1;
            for (String paramName : paramNamesFromPlaceHolders) {
                String paramValue = params.get(paramName)[0];
                preparedStatement.setObject(i++,paramValue);
            }
        }
        return preparedStatement;
    }
}
