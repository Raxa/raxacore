package org.bahmni.module.bahmnicore.util;

import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.model.searchParams.AdditionalSearchParam;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
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
    private static final Logger log = Logger.getLogger(SqlQueryHelper.class);

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

    public String transformIntoPreparedStatementFormat(String queryString){
        return  queryString.replaceAll(PARAM_PLACE_HOLDER_REGEX,"?");
    }

    public PreparedStatement constructPreparedStatement(String queryString, Map<String, String[]> params, Connection conn) throws SQLException {
       String finalQueryString = queryString;
        if (params.get("additionalParams") != null && params.get("additionalParams") != null) {
            finalQueryString = parseAdditionalParams(params.get("additionalParams")[0], queryString);
        }

        List<String> paramNamesFromPlaceHolders = getParamNamesFromPlaceHolders(finalQueryString);
        String statement = transformIntoPreparedStatementFormat(finalQueryString);
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

    String parseAdditionalParams(String additionalParams, String queryString) {
        String queryWithAdditionalParams = queryString;
        try {
            AdditionalSearchParam additionalSearchParams = new ObjectMapper().readValue(additionalParams, AdditionalSearchParam.class);
            String test = additionalSearchParams.getTests();
            queryWithAdditionalParams = queryString.replaceAll("\\$\\{testName\\}", test);
        } catch (IOException e) {
            log.error("Failed to parse Additional Search Parameters.");
            e.printStackTrace();
        }
        return queryWithAdditionalParams;
    }


}
