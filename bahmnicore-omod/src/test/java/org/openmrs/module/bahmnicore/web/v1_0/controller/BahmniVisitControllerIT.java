package org.openmrs.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.contract.visit.VisitSummary;
import org.bahmni.test.web.controller.BaseWebControllerTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class BahmniVisitControllerIT extends BaseWebControllerTest {

    @Autowired
    private BahmniVisitController bahmniVisitController;

    @Before
    public void setUp() throws Exception {
        executeDataSet("visitInfo.xml");
    }

    @Test
    public void shouldGetVisitSummary() {
        VisitSummary visitSummary = bahmniVisitController.getVisitInfo("1e5d5d48-6b78-11e0-933c-18a905e044cd");

        assertNotNull(visitSummary);
        assertEquals("1e5d5d48-6b78-11e0-933c-18a905e044cd", visitSummary.getUuid());
        assertEquals("2005-01-01 00:00:00.0", visitSummary.getStartDateTime().toString());
        assertEquals("2005-01-05 00:00:00.0", visitSummary.getStopDateTime().toString());
    }

    @Test
    public void shouldNotGetVisitSummaryOfVoidedVisit() {
        VisitSummary visitSummary = bahmniVisitController.getVisitInfo("e1428fea-6b78-11e0-93c3-18a905e044dc");

        assertNull(visitSummary);
    }

    @Test
    public void hasBeenAdmittedShouldBeTrueIfTheVisitStatusIsIPD() throws Exception {
        VisitSummary visitSummary = bahmniVisitController.getVisitInfo("1e5d5d48-6b78-11e0-933c-18a905e044cd");

        assertNotNull(visitSummary);
        assertTrue("hasBeenAdmitted should be true", visitSummary.getHasBeenAdmitted());
    }

    @Test
    public void hasBeenAdmittedShouldBeFalseIfTheVisitStatusIsOPD() throws Exception {
        VisitSummary visitSummary = bahmniVisitController.getVisitInfo("1e5d5d48-6b78-11e0-988c-18a905e044cd");

        assertNotNull(visitSummary);
        assertFalse("hasBeenAdmitted should be false", visitSummary.getHasBeenAdmitted());
    }

    @Test
    public void hasBeenAdmittedShouldBeFalseIfTheVisitStatusIsEmergency() throws Exception {
        VisitSummary visitSummary = bahmniVisitController.getVisitInfo("1e5d5d48-6b78-11e0-988c-18a905e033cd");

        assertNotNull(visitSummary);
        assertFalse("hasBeenAdmitted should be false", visitSummary.getHasBeenAdmitted());
    }
}