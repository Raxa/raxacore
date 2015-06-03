package org.bahmni.module.bahmnicore.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.api.AdministrationService;

import java.sql.PreparedStatement;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class SqlQueryHelperTest {

    @Mock
    private AdministrationService administrationService;
    SqlQueryHelper sqlQueryHelper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
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

    @Test
    public void shouldParseAdditionalParams(){
        String queryString ="select distinct concat(pn.given_name,' ', pn.family_name) as name, pi.identifier as identifier, concat(\"\",p.uuid) as uuid, concat(\"\",v.uuid) as activeVisitUuid\n" +
                "FROM obs o\n" +
                "INNER JOIN concept_name cn ON o.concept_id = cn.concept_id AND cn.concept_name_type='FULLY_SPECIFIED' AND o.voided = 0\n" +
                "inner join obs o1 on o.obs_group_id = o1.obs_group_id and o1.voided=0\n" +
                "inner join concept_name cn1 on o1.concept_id = cn1.concept_id and cn1.name='LAB_ABNORMAL' and  cn1.concept_name_type='FULLY_SPECIFIED' and o1.value_coded=1 WHERE ";

        String additionalParams = "{\"additionalSearchHandler\":\"emrapi.sqlSearch.additionalSearchHandler\",\"tests\":[\"HIV (Blood)\",\"Gram Stain (Sputum)\"]}";

        when(administrationService.getGlobalProperty("emrapi.sqlSearch.additionalSearchHandler")).thenReturn(" cn.name = '${testName}'");

        String expectedQueryString ="select distinct concat(pn.given_name,' ', pn.family_name) as name, pi.identifier as identifier, concat(\"\",p.uuid) as uuid, concat(\"\",v.uuid) as activeVisitUuid\n" +
                "FROM obs o\n" +
                "INNER JOIN concept_name cn ON o.concept_id = cn.concept_id AND cn.concept_name_type='FULLY_SPECIFIED' AND o.voided = 0\n" +
                "inner join obs o1 on o.obs_group_id = o1.obs_group_id and o1.voided=0\n" +
                "inner join concept_name cn1 on o1.concept_id = cn1.concept_id and cn1.name='LAB_ABNORMAL' and  cn1.concept_name_type='FULLY_SPECIFIED' and o1.value_coded=1 WHERE  " +
                " cn.name = 'HIV (Blood)' OR   cn.name = 'Gram Stain (Sputum)'))";

        String result = sqlQueryHelper.parseAdditionalParams(additionalParams, queryString, administrationService);

        assertEquals(expectedQueryString,result);
    }
}
