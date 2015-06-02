package org.bahmni.module.bahmnicore.util;

import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.model.searchParams.AdditionalSearchParam;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.api.AdministrationService;

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

    String transformIntoPreparedStatementFormat(String queryString){
        return  queryString.replaceAll(PARAM_PLACE_HOLDER_REGEX,"?");
    }

    public PreparedStatement constructPreparedStatement(String queryString, Map<String, String[]> params, Connection conn, AdministrationService administrationService) throws SQLException {
        if (params.get("additionalParams") != null && params.get("additionalParams") != null) {
            queryString = parseAdditionalParams(params.get("additionalParams")[0], queryString, administrationService);
        }

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

    String parseAdditionalParams(String additionalParams, String queryString, AdministrationService administrationService) {
        try {
            boolean hasReadAtLeastOneAdditionalParam = false;
            AdditionalSearchParam additionalSearchParams = new ObjectMapper().readValue(additionalParams, AdditionalSearchParam.class);
            String additionalQueryString = administrationService.getGlobalProperty(additionalSearchParams.getAdditionalSearchHandler());
            for (String test : additionalSearchParams.getTests()) {
                if (hasReadAtLeastOneAdditionalParam) {
                    queryString += " OR ";
                }
                String additionalQuery = " ";
                additionalQuery += additionalQueryString.replaceAll("\\$\\{testName\\}", test);
                queryString += additionalQuery;
                hasReadAtLeastOneAdditionalParam = true;
            }
            queryString += "))";
        } catch (IOException e) {
            log.error("Failed to parse Additional Search Parameters.");
            e.printStackTrace();
        }
        return queryString;
    }
}
