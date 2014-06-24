package org.bahmni.module.bahmnicore.util;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class SqlQueryHelperTest {
    SqlQueryHelper sqlQueryHelper;

    @Before
    public void setUp() throws Exception {
        sqlQueryHelper = new SqlQueryHelper();
    }

    @Test
    public void shouldReturnQueryParamsInOrder(){
        String queryString ="select * from encounter where date_started=${en_date_started} AND visit_id=${en_visit_id} and patient_id=${en_patient_id}";
        List<String> paramNamesFromPlaceHolders = sqlQueryHelper.getParamNamesFromPlaceHolders(queryString);
        assertEquals("en_date_started",paramNamesFromPlaceHolders.get(0));
        assertEquals("en_visit_id",paramNamesFromPlaceHolders.get(1));
        assertEquals("en_patient_id",paramNamesFromPlaceHolders.get(2));
    }

    @Test
    public void shouldTransformQueryIntoPreparedStatementFormat(){
        String queryString ="select * from encounter where date_started=${en_date_started} AND visit_id=${en_visit_id} and patient_id=${en_patient_id}";
        String expectQueryString = "select * from encounter where date_started=? AND visit_id=? and patient_id=?";
        String result = sqlQueryHelper.transformIntoPreparedStatementFormat(queryString);
        assertEquals(expectQueryString,result);
    }
}
