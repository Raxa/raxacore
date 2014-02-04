package org.bahmni.module.elisatomfeedclient.api.mapper;

import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.builder.OpenElisAccessionBuilder;
import org.bahmni.module.elisatomfeedclient.api.builder.OpenElisTestDetailBuilder;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisAccession;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisTestDetail;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class AccessionMapperIT extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private ElisAtomFeedProperties properties;

    private AccessionMapper accessionMapper;

    @Test
    public void shouldReturnEncounterIfEncounterExistForAProviderAndVisitForLabResultEncounterType() throws Exception {
        executeDataSet("labOrderEncounter.xml");

        accessionMapper = new AccessionMapper(properties);

        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder().withTestUuid("test1").withResult("10")
                .withProviderUuid("331c6bf8-7846-11e3-a96a-0900271c1b75").build();
        OpenElisTestDetail test2 = new OpenElisTestDetailBuilder().withTestUuid("test2").withResult("20")
                .withProviderUuid("331c6bf8-7846-11e3-a96a-0900271c1b75").build();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(test1, test2))).build();

        Visit visit = Context.getVisitService().getVisit(1);

        Encounter encounterForTestResult1 = accessionMapper.getEncounterForTestResult(openElisAccession, visit, test1.getProviderUuid());
        assertNotNull(encounterForTestResult1);
        assertEquals("LAB_RESULT", encounterForTestResult1.getEncounterType().getName());

        Encounter encounterForTestResult2 = accessionMapper.getEncounterForTestResult(openElisAccession, visit, test2.getProviderUuid());
        assertNotNull(encounterForTestResult2);
        assertEquals("LAB_RESULT", encounterForTestResult2.getEncounterType().getName());
        assertEquals(encounterForTestResult1.getId(), encounterForTestResult2.getId());
    }

}
