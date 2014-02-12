package org.bahmni.module.elisatomfeedclient.api.worker;

import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.builder.OpenElisAccessionBuilder;
import org.bahmni.module.elisatomfeedclient.api.builder.OpenElisTestDetailBuilder;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisAccession;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisTestDetail;
import org.bahmni.module.elisatomfeedclient.api.mapper.AccessionHelper;
import org.ict4h.atomfeed.client.domain.Event;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.*;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.bahmni.webclients.HttpClient;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class OpenElisAccessionEventWorkerIT  extends BaseModuleWebContextSensitiveTest {

    @Mock
    HttpClient httpClient;
    @Autowired
    private ElisAtomFeedProperties properties;

    private OpenElisAccessionEventWorker openElisAccessionEventWorker;
    private String openElisUrl = "http://localhost:8080/";
    private Event event = new Event("id", "openelis/accession/12-34-56-78", "title", "feedUri");

    @Before
    public void setUp() {
        initMocks(this);
        this.openElisAccessionEventWorker = new OpenElisAccessionEventWorker(
                properties,
                httpClient,
                Context.getEncounterService(),
                Context.getConceptService(),
                new AccessionHelper(properties),
                Context.getProviderService(),
                Context.getVisitService());
    }

    @Test
    public void shouldCreateResultEncounterAndObsForTestWithResultAndOtherValues() throws Exception {
        executeDataSet("labResult.xml");

        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder()
                .withTestUuid("7923d0e0-8734-11e3-baa7-0800200c9a66")
                .withResult("10.5")
                .withProviderUuid("331c6bf8-7846-11e3-a96a-09xD371c1b75")
                .withMinNormal("10")
                .withMaxNormal("20.2")
                .withAbnormal("false")
                .withDateTime("2014-01-30T11:50:18+0530")
                .withResultType("N")
                .build();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withDateTime("2014-01-30T11:50:18+0530").withTestDetails(new HashSet<>(Arrays.asList(test1))).build();
        openElisAccession.setAccessionUuid("6d0af4567-707a-4629-9850-f15206e63ab0");

        when(httpClient.get(openElisUrl + event.getContent(), OpenElisAccession.class)).thenReturn(openElisAccession);

        openElisAccessionEventWorker.associateTestResultsToOrder(openElisAccession);

        Visit visit = Context.getVisitService().getVisit(2);
        Encounter labEncounter = null;
         Set<Encounter> encounters = visit.getEncounters();
        for (Encounter encounter : encounters) {
            if(encounter.getEncounterType().getName().equals("LAB_RESULT")) {
                labEncounter = encounter;
            }
        }

        assertEquals(2, encounters.size());
        assertNotNull(labEncounter);
        Set<Obs> topLevelObs = labEncounter.getAllObs();
        assertEquals(1, topLevelObs.size());
        final Set<Obs> testLevelObs = getGroupMembersForObs(topLevelObs);
        assertEquals(1, testLevelObs.size());
        final Set<Obs> resultMembers = getGroupMembersForObs(testLevelObs);
        assertEquals(4, resultMembers.size());
    }

    @Test
    public void shouldCreateResultEncounterWithSystemProvider() throws Exception {
        executeDataSet("labResult.xml");

        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder()
                .withTestUuid("7923d0e0-8734-11e3-baa7-0800200c9a66")
                .withResult("10.5")
                .withMinNormal("10")
                .withMaxNormal("20.2")
                .withAbnormal("false")
                .withDateTime("2014-01-30T11:50:18+0530")
                .withResultType("N")
                .build();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withDateTime("2014-01-30T11:50:18+0530").withTestDetails(new HashSet<>(Arrays.asList(test1))).build();
        openElisAccession.setAccessionUuid("6d0af4567-707a-4629-9850-f15206e63ab0");

        when(httpClient.get(openElisUrl + event.getContent(), OpenElisAccession.class)).thenReturn(openElisAccession);

        openElisAccessionEventWorker.associateTestResultsToOrder(openElisAccession);

        Visit visit = Context.getVisitService().getVisit(2);
        Encounter labEncounter = null;
         Set<Encounter> encounters = visit.getEncounters();
        for (Encounter encounter : encounters) {
            if(encounter.getEncounterType().getName().equals("LAB_RESULT")) {
                labEncounter = encounter;
            }
        }

        assertEquals(2, encounters.size());
        assertNotNull(labEncounter);
        assertEquals("system", labEncounter.getEncounterProviders().iterator().next().getProvider().getIdentifier());

        Set<Obs> topLevelObs = labEncounter.getAllObs();
        assertEquals(1, topLevelObs.size());
        final Set<Obs> testLevelObs = getGroupMembersForObs(topLevelObs);
        assertEquals(1, testLevelObs.size());
        final Set<Obs> resultMembers = getGroupMembersForObs(testLevelObs);
        assertEquals(4, resultMembers.size());
    }

    @Test
    public void shouldCreateResultEncounterAndObsForPanelWithOnetestWithResultAndOtherValues() throws Exception {
        executeDataSet("labResult.xml");

         String panelConceptUuid = "cfc5056c-3f8e-11e3-968c-0800271c1b75";
         String haemoglobinConceptUuid = "7f7379ba-3ca8-11e3-bf2b-0800271c1b75";
        
        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder()
                .withPanelUuid(panelConceptUuid)
                .withTestUuid(haemoglobinConceptUuid)
                .withResult("10.5")
                .withProviderUuid("331c6bf8-7846-11e3-a96a-09xD371c1b75")
                .withMinNormal("10")
                .withMaxNormal("20.2")
                .withAbnormal("false")
                .withDateTime("2014-01-30T11:50:18+0530")
                .withResultType("N")
                .build();

        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withDateTime("2014-01-30T11:50:18+0530").withTestDetails(new HashSet<>(Arrays.asList(test1))).build();
        openElisAccession.setAccessionUuid("6d0af4567-707a-4629-9850-f15206e63ab0");

        when(httpClient.get(openElisUrl + event.getContent(), OpenElisAccession.class)).thenReturn(openElisAccession);

        openElisAccessionEventWorker.associateTestResultsToOrder(openElisAccession);

        Visit visit = Context.getVisitService().getVisit(2);
        Encounter labEncounter = null;
         Set<Encounter> encounters = visit.getEncounters();
        for (Encounter encounter : encounters) {
            if(encounter.getEncounterType().getName().equals("LAB_RESULT")) {
                labEncounter = encounter;
            }
        }

        assertEquals(2, encounters.size());
        assertNotNull(labEncounter);

        Set<Obs> obs = labEncounter.getAllObs();
        assertEquals(1, obs.size());
        Obs panelResultObs = getObsByConceptUuid(obs, panelConceptUuid);
        assertNotNull(panelResultObs);
        Set<Obs> panel1ResultMembers = panelResultObs.getGroupMembers();
        assertEquals(1,panel1ResultMembers.size());

        Set<Obs> topLevelObs = panel1ResultMembers;
        assertEquals(1, topLevelObs.size());
        final Set<Obs> testLevelObs = getGroupMembersForObs(topLevelObs);
        assertEquals(1, testLevelObs.size());
        final Set<Obs> resultMembers = getGroupMembersForObs(testLevelObs);
        assertEquals(4, resultMembers.size());

        Obs testResultObs = getObsByConceptUuid(testLevelObs, haemoglobinConceptUuid);
        assertNotNull(testResultObs);
        assertEquals(4, testResultObs.getGroupMembers().size());

    }

    @Test
    public void shouldCreateResultEncounterAndObsForPanelWithMoreThanOnetestWithResultAndOtherValues() throws Exception {
        //same provider for both tests in panel
        executeDataSet("labResult.xml");

         String panelConceptUuid = "cfc5056c-3f8e-11e3-968c-0800271c1b75";
         String haemoglobinConceptUuid = "7f7379ba-3ca8-11e3-bf2b-0800271c1b75";
         String providerUuid = "331c6bf8-7846-11e3-a96a-09xD371c1b75";
        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder()
                .withPanelUuid(panelConceptUuid)
                .withTestUuid(haemoglobinConceptUuid)
                .withResult("10.5")
                .withProviderUuid(providerUuid)
                .withMinNormal("10")
                .withMaxNormal("20.2")
                .withAbnormal("false")
                .withDateTime("2014-01-30T11:50:18+0530")
                .withResultType("N")
                .build();

         String esrConceptUuid = "a04c36be-3f90-11e3-968c-0800271c1b75";
        OpenElisTestDetail test2 = new OpenElisTestDetailBuilder()
                .withPanelUuid(panelConceptUuid)
                .withTestUuid(esrConceptUuid)
                .withResult("16")
                .withProviderUuid(providerUuid)
                .withMinNormal("10")
                .withMaxNormal("20.2")
                .withAbnormal("false")
                .withDateTime("2014-01-30T11:50:18+0530")
                .withResultType("N")
                .build();

        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(test1, test2))).withDateTime("2014-01-30T11:50:18+0530").build();
        openElisAccession.setAccessionUuid("6d0af4567-707a-4629-9850-f15206e63ab0");

        when(httpClient.get(openElisUrl + event.getContent(), OpenElisAccession.class)).thenReturn(openElisAccession);

        openElisAccessionEventWorker.associateTestResultsToOrder(openElisAccession);

        Visit visit = Context.getVisitService().getVisit(2);
        Encounter labEncounter = null;
         Set<Encounter> encounters = visit.getEncounters();
        for (Encounter encounter : encounters) {
            if(encounter.getEncounterType().getName().equals("LAB_RESULT")) {
                labEncounter = encounter;
            }
        }

        assertEquals(2, encounters.size());
        assertNotNull(labEncounter);
        Set<Obs> obs = labEncounter.getAllObs();
        assertEquals(1, obs.size());
        Obs panelResultObs = getObsByConceptUuid(obs, panelConceptUuid);
        assertNotNull(panelResultObs);
        Set<Obs> panel1ResultMembers = panelResultObs.getGroupMembers();
        assertEquals(2,panel1ResultMembers.size());

        Obs haemoglobinTestResultObs = getObsByConceptUuid(panel1ResultMembers, haemoglobinConceptUuid);
        assertNotNull(haemoglobinTestResultObs);
        Set<Obs> testLevelObs = haemoglobinTestResultObs.getGroupMembers();
        assertEquals(1, testLevelObs.size());
        Set<Obs> resultMembers = getGroupMembersForObs(testLevelObs);
        assertEquals(4, resultMembers.size());

        Obs esrTestResultObs = getObsByConceptUuid(panel1ResultMembers, esrConceptUuid);
        assertNotNull(esrTestResultObs);
        testLevelObs = esrTestResultObs.getGroupMembers();
        assertEquals(1, testLevelObs.size());
        resultMembers = getGroupMembersForObs(testLevelObs);
        assertEquals(4, resultMembers.size());
    }

    @Test
    public void shouldCreateResultEncounterForPanelAndTest() throws Exception {
        executeDataSet("labResult.xml");

        String panelConceptUuid = "cfc5056c-3f8e-11e3-968c-0800271c1b75";
        String haemoglobinConceptUuid = "7f7379ba-3ca8-11e3-bf2b-0800271c1b75";
        String providerUuid = "331c6bf8-7846-11e3-a96a-09xD371c1b75";
        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder()
                .withPanelUuid(panelConceptUuid)
                .withTestUuid(haemoglobinConceptUuid)
                .withResult("10.5")
                .withProviderUuid(providerUuid)
                .withMinNormal("10")
                .withMaxNormal("20.2")
                .withAbnormal("false")
                .withDateTime("2014-01-30T11:50:18+0530")
                .withResultType("N")
                .build();

        final String nitroUreaConceptUuid = "7923d0e0-8734-11e3-baa7-0800200c9a66";
        OpenElisTestDetail test2 = new OpenElisTestDetailBuilder()
                .withTestUuid(nitroUreaConceptUuid)
                .withResult("10.5")
                .withProviderUuid(providerUuid)
                .withMinNormal("10")
                .withMaxNormal("20.2")
                .withAbnormal("false")
                .withDateTime("2014-01-30T11:50:18+0530")
                .withResultType("N")
                .build();

        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(test1, test2))).withDateTime("2014-01-30T11:50:18+0530").build();
        openElisAccession.setAccessionUuid("6d0af4567-707a-4629-9850-f15206e63ab0");

        when(httpClient.get(openElisUrl + event.getContent(), OpenElisAccession.class)).thenReturn(openElisAccession);

        openElisAccessionEventWorker.associateTestResultsToOrder(openElisAccession);

        Visit visit = Context.getVisitService().getVisit(2);
        Encounter labEncounter = null;
        Set<Encounter> encounters = visit.getEncounters();
        for (Encounter encounter : encounters) {
            if(encounter.getEncounterType().getName().equals("LAB_RESULT")) {
                labEncounter = encounter;
            }
        }

        assertEquals(2, encounters.size());
        assertNotNull(labEncounter);
        Set<Obs> obs = labEncounter.getAllObs();
        assertEquals(2, obs.size());
        Obs panelResultObs = getObsByConceptUuid(obs, panelConceptUuid);
        assertNotNull(panelResultObs);
        Set<Obs> panel1ResultMembers = panelResultObs.getGroupMembers();
        assertEquals(1,panel1ResultMembers.size());

        Obs haemoglobinTestResultObs = getObsByConceptUuid(panel1ResultMembers, haemoglobinConceptUuid);
        assertNotNull(haemoglobinTestResultObs);
        Set<Obs> testLevelObs = haemoglobinTestResultObs.getGroupMembers();
        assertEquals(1, testLevelObs.size());
        Set<Obs> resultMembers = getGroupMembersForObs(testLevelObs);
        assertEquals(4, resultMembers.size());

        Obs nirtoTestResultObs = getObsByConceptUuid(obs, nitroUreaConceptUuid);
        assertNotNull(nitroUreaConceptUuid);
        testLevelObs = nirtoTestResultObs.getGroupMembers();
        assertEquals(1, testLevelObs.size());
        resultMembers = getGroupMembersForObs(testLevelObs);
        assertEquals(4, resultMembers.size());
    }

    @Test
    public void shouldUpdateValueForAlreadyExistingTestResult() throws Exception {
        executeDataSet("labResult.xml");

        final String nitroUreaConceptUuid = "7923d0e0-8734-11e3-baa7-0800200c9a66";
        final String accessionUuid = "6d0af4567-707a-4629-9850-f15206e63ab0";

        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder()
                .withTestUuid(nitroUreaConceptUuid)
                .withResult("10.5")
                .withProviderUuid("331c6bf8-7846-11e3-a96a-09xD371c1b75")
                .withMinNormal("10")
                .withMaxNormal("20.2")
                .withAbnormal("false")
                .withDateTime("2014-01-30T11:50:18+0530")
                .withResultType("N")
                .build();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withDateTime("2014-01-30T11:50:18+0530").withTestDetails(new HashSet<>(Arrays.asList(test1))).build();
        openElisAccession.setAccessionUuid(accessionUuid);

        when(httpClient.get(openElisUrl + event.getContent(), OpenElisAccession.class)).thenReturn(openElisAccession);

        openElisAccessionEventWorker.associateTestResultsToOrder(openElisAccession);

        // on update of value new openElisAccession response
        test1 = new OpenElisTestDetailBuilder()
                .withTestUuid(nitroUreaConceptUuid)
                .withResult("20")
                .withProviderUuid("331c6bf8-7846-11e3-a96a-09xD371c1b75")
                .withMinNormal("10")
                .withMaxNormal("20.2")
                .withAbnormal("false")
                .withDateTime("2014-01-30T11:55:18+0530") //date changed
                .withResultType("N")
                .build();
        openElisAccession = new OpenElisAccessionBuilder().withDateTime("2014-01-30T11:50:18+0530").withTestDetails(new HashSet<>(Arrays.asList(test1))).build();
        openElisAccession.setAccessionUuid(accessionUuid);

        when(httpClient.get(openElisUrl + event.getContent(), OpenElisAccession.class)).thenReturn(openElisAccession);

        openElisAccessionEventWorker.associateTestResultsToOrder(openElisAccession);

        Visit visit = Context.getVisitService().getVisit(2);
        Encounter labEncounter = null;
        Set<Encounter> encounters = visit.getEncounters();
        for (Encounter encounter : encounters) {
            if(encounter.getEncounterType().getName().equals("LAB_RESULT")) {
                labEncounter = encounter;
            }
        }

        assertEquals(2, encounters.size());
        assertNotNull(labEncounter);
        final Set<Obs> allObs = labEncounter.getAllObs(true);
        assertEquals(2, allObs.size());

        ArrayList<Obs> voidedObservations = getVoidedObservations(allObs);
        assertEquals(1, voidedObservations.size());


        Set<Obs> nonVoidedObs = labEncounter.getAllObs(false);
        assertEquals(1, nonVoidedObs.size());

        Obs nitroTestResultObs = getObsByConceptUuid(nonVoidedObs, nitroUreaConceptUuid);
        assertNotNull(nitroTestResultObs);

        Set<Obs> testLevelObs = nitroTestResultObs.getGroupMembers();
        assertEquals(1, testLevelObs.size());
        Set<Obs> resultMembers = getGroupMembersForObs(testLevelObs);
        assertEquals(4, resultMembers.size());
        Obs resultObs = getObsByConceptUuid(resultMembers, nitroUreaConceptUuid);
        assertEquals(new Double(20.0), resultObs.getValueNumeric());
    }

    @Test
    public void shouldUpdateResultForPanelWithMultipleTests() throws Exception {
        //same provider updates all results
        executeDataSet("labResult.xml");

        String panelConceptUuid = "cfc5056c-3f8e-11e3-968c-0800271c1b75";
        String haemoglobinConceptUuid = "7f7379ba-3ca8-11e3-bf2b-0800271c1b75";
        String providerUuid = "331c6bf8-7846-11e3-a96a-09xD371c1b75";
        OpenElisTestDetail hbTest = new OpenElisTestDetailBuilder()
                .withPanelUuid(panelConceptUuid)
                .withTestUuid(haemoglobinConceptUuid)
                .withResult("10.5")
                .withProviderUuid(providerUuid)
                .withMinNormal("10")
                .withMaxNormal("20.2")
                .withAbnormal("false")
                .withDateTime("2014-01-30T11:50:18+0530")
                .withResultType("N")
                .build();

        String esrConceptUuid = "a04c36be-3f90-11e3-968c-0800271c1b75";
        OpenElisTestDetail esrTest = new OpenElisTestDetailBuilder()
                .withPanelUuid(panelConceptUuid)
                .withTestUuid(esrConceptUuid)
                .build();

        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(hbTest, esrTest))).withDateTime("2014-01-30T11:50:18+0530").build();
        openElisAccession.setAccessionUuid("6d0af4567-707a-4629-9850-f15206e63ab0");

        when(httpClient.get(openElisUrl + event.getContent(), OpenElisAccession.class)).thenReturn(openElisAccession);

        openElisAccessionEventWorker.associateTestResultsToOrder(openElisAccession);

        Visit visit = Context.getVisitService().getVisit(2);
        Encounter labEncounter = null;
        Set<Encounter> encounters = visit.getEncounters();
        for (Encounter encounter : encounters) {
            if(encounter.getEncounterType().getName().equals("LAB_RESULT")) {
                labEncounter = encounter;
            }
        }

        assertEquals(2, encounters.size());
        assertNotNull(labEncounter);
        Set<Obs> obs = labEncounter.getAllObs();
        assertEquals(1, obs.size());
        Obs panelResultObs = getObsByConceptUuid(obs, panelConceptUuid);
        assertNotNull(panelResultObs);
        Set<Obs> panel1ResultMembers = panelResultObs.getGroupMembers();
        assertEquals(1,panel1ResultMembers.size()); //only one test has results


        OpenElisTestDetail hbTestUpdated = new OpenElisTestDetailBuilder()
                .withPanelUuid(panelConceptUuid)
                .withTestUuid(haemoglobinConceptUuid)
                .withResult("9.0")
                .withProviderUuid(providerUuid)
                .withMinNormal("10")
                .withMaxNormal("20.2")
                .withAbnormal("true")
                .withDateTime("2014-01-30T12:50:18+0530")
                .withResultType("N")
                .build();

        OpenElisTestDetail esrTestUpdated = new OpenElisTestDetailBuilder()
                .withPanelUuid(panelConceptUuid)
                .withTestUuid(esrConceptUuid)
                .withResult("16")
                .withProviderUuid(providerUuid)
                .withMinNormal("10")
                .withMaxNormal("20.2")
                .withAbnormal("false")
                .withDateTime("2014-01-30T12:50:18+0530")
                .withResultType("N")
                .build();

        openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(hbTestUpdated, esrTestUpdated))).withDateTime("2014-01-30T11:50:18+0530").build();
        openElisAccession.setAccessionUuid("6d0af4567-707a-4629-9850-f15206e63ab0");
        when(httpClient.get(openElisUrl + event.getContent(), OpenElisAccession.class)).thenReturn(openElisAccession);
        openElisAccessionEventWorker.associateTestResultsToOrder(openElisAccession);

        visit = Context.getVisitService().getVisit(2);
        labEncounter = null;
        encounters = visit.getEncounters();
        for (Encounter encounter : encounters) {
            if(encounter.getEncounterType().getName().equals("LAB_RESULT")) {
                labEncounter = encounter;
            }
        }

        assertEquals(2, encounters.size());
        assertNotNull(labEncounter);
        Set<Obs> allObs = labEncounter.getAllObs(true);
        assertEquals(1, allObs.size());
        Obs panelObs = getObsByConceptUuid(allObs, panelConceptUuid);
        final Set<Obs> testObservations = panelObs.getGroupMembers(true);
        assertEquals(3, testObservations.size()); //one voided, 1 updated, 1 new
        assertEquals(1, getVoidedObservations(testObservations).size());
        final Set<Obs> unvoidedObservations = panelObs.getGroupMembers(false);
        assertEquals(2, unvoidedObservations.size());
        final Obs resultsForHaemoglobin = getObsByConceptUuid(unvoidedObservations, haemoglobinConceptUuid);
        assertEquals(new Double(9.0), getConceptResultObs(resultsForHaemoglobin.getGroupMembers(), haemoglobinConceptUuid).getValueNumeric());

    }

    @Test
    public void shouldUpdateResultForPanelWithMultipleTestsWithDiffProviders() throws Exception {
        executeDataSet("labResult.xml");

        String panelConceptUuid = "cfc5056c-3f8e-11e3-968c-0800271c1b75";
        String haemoglobinConceptUuid = "7f7379ba-3ca8-11e3-bf2b-0800271c1b75";
        String labTechProviderUuid = "331c6bf8-7846-11e3-a96a-09xD371c1b75";
        String systemProviderUuid = "331c6bf8-7846-11e3-a96a-0909871c1b75";

        OpenElisTestDetail hbTest = new OpenElisTestDetailBuilder()
                .withPanelUuid(panelConceptUuid)
                .withTestUuid(haemoglobinConceptUuid)
                .withResult("10.5")
                .withProviderUuid(labTechProviderUuid)
                .withMinNormal("10")
                .withMaxNormal("20.2")
                .withAbnormal("false")
                .withDateTime("2014-01-30T11:50:18+0530")
                .withResultType("N")
                .build();

        String esrConceptUuid = "a04c36be-3f90-11e3-968c-0800271c1b75";
        OpenElisTestDetail esrTest = new OpenElisTestDetailBuilder()
                .withPanelUuid(panelConceptUuid)
                .withTestUuid(esrConceptUuid)
                .build();

        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(hbTest, esrTest))).withDateTime("2014-01-30T11:50:18+0530").build();
        String accessionUuid = "6d0af4567-707a-4629-9850-f15206e63ab0";
        openElisAccession.setAccessionUuid(accessionUuid);

        Event firstEvent = stubHttpClientToGetOpenElisAccession(accessionUuid, openElisAccession);
        openElisAccessionEventWorker.process(firstEvent);

        Visit visit = Context.getVisitService().getVisit(2);
        Encounter labEncounter = null;
        Set<Encounter> encounters = visit.getEncounters();
        for (Encounter encounter : encounters) {
            if(encounter.getEncounterType().getName().equals("LAB_RESULT")) {
                labEncounter = encounter;
            }
        }

        assertEquals(2, encounters.size());
        assertNotNull(labEncounter);
        Set<Obs> obs = labEncounter.getAllObs();
        assertEquals(1, obs.size());
        Obs panelResultObs = getObsByConceptUuid(obs, panelConceptUuid);
        assertNotNull(panelResultObs);
        Set<Obs> panel1ResultMembers = panelResultObs.getGroupMembers();
        assertEquals(1,panel1ResultMembers.size());
        assertNotNull(getObsByConceptUuid(panel1ResultMembers,haemoglobinConceptUuid));


        OpenElisTestDetail hbTestUpdated = new OpenElisTestDetailBuilder()
                .withPanelUuid(panelConceptUuid)
                .withTestUuid(haemoglobinConceptUuid)
                .withResult("9.0")
                .withProviderUuid(systemProviderUuid)
                .withMinNormal("10")
                .withMaxNormal("20.2")
                .withAbnormal("true")
                .withDateTime("2014-01-30T12:50:18+0530")
                .withResultType("N")
                .build();

        OpenElisTestDetail esrTestUpdated = new OpenElisTestDetailBuilder()
                .withPanelUuid(panelConceptUuid)
                .withTestUuid(esrConceptUuid)
                .withResult("16")
                .withProviderUuid(systemProviderUuid)
                .withMinNormal("10")
                .withMaxNormal("20.2")
                .withAbnormal("false")
                .withDateTime("2014-01-30T12:50:18+0530")
                .withResultType("N")
                .build();

        openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(hbTestUpdated, esrTestUpdated))).withDateTime("2014-01-30T11:50:18+0530").build();
        openElisAccession.setAccessionUuid(accessionUuid);
        firstEvent = stubHttpClientToGetOpenElisAccession(accessionUuid, openElisAccession);
        openElisAccessionEventWorker.process(firstEvent);

        visit = Context.getVisitService().getVisit(2);
        List<Encounter> labEncounters = new ArrayList<>();
        encounters = visit.getEncounters();
        for (Encounter encounter : encounters) {
            if(encounter.getEncounterType().getName().equals("LAB_RESULT")) {
                labEncounters.add(encounter);
            }
        }

        assertEquals(3, encounters.size());
        assertEquals(2, labEncounters.size());

        List<Encounter> encountersByLabTech = findEncountersForProvider(labEncounters, labTechProviderUuid);
        assertEquals(1, encountersByLabTech.size());
        final Set<Obs> panelObsByLabTech = encountersByLabTech.get(0).getAllObs(true);
        assertEquals(1, panelObsByLabTech.size());
        Set<Obs> topLevelTestsByLabTech = panelObsByLabTech.iterator().next().getGroupMembers(true);
        assertEquals(1, topLevelTestsByLabTech.size());
        final ArrayList<Obs> voidedObservations = getVoidedObservations(topLevelTestsByLabTech);
        assertEquals(1, voidedObservations.size());
        final Set<Obs> testObs = voidedObservations.get(0).getGroupMembers(true);
        assertEquals(1, testObs.size());
        final Set<Obs> testResults = testObs.iterator().next().getGroupMembers(true);
        for (Obs testOb : testResults) {
            assertTrue(testOb.getVoided());
        }

        List<Encounter> encountersBySystem = findEncountersForProvider(labEncounters, systemProviderUuid);
        assertEquals(1, encountersBySystem.size());
        final Set<Obs> panelObsBySystem = encountersBySystem.get(0).getAllObs(true);
        assertEquals(1, panelObsBySystem.size());
        Set<Obs> topLevelPanelTestsBySystem = panelObsBySystem.iterator().next().getGroupMembers(true);
        assertEquals(2, topLevelPanelTestsBySystem.size());
        assertEquals(0, getVoidedObservations(topLevelPanelTestsBySystem).size());
    }

    @Test
    public void shouldNotVoidObsIfTimeDidntChange() throws Exception {
        executeDataSet("labResult.xml");

        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder()
                .withTestUuid("7923d0e0-8734-11e3-baa7-0800200c9a66")
                .withResult("10.5")
                .withProviderUuid("331c6bf8-7846-11e3-a96a-09xD371c1b75")
                .withMinNormal("10")
                .withMaxNormal("20.2")
                .withAbnormal("false")
                .withDateTime("2014-01-30T11:50:18+0530")
                .withResultType("N")
                .build();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withDateTime("2014-01-30T11:50:18+0530").withTestDetails(new HashSet<>(Arrays.asList(test1))).build();
        openElisAccession.setAccessionUuid("6d0af4567-707a-4629-9850-f15206e63ab0");

        when(httpClient.get(openElisUrl + event.getContent(), OpenElisAccession.class)).thenReturn(openElisAccession);

        openElisAccessionEventWorker.associateTestResultsToOrder(openElisAccession);

        Visit visit = Context.getVisitService().getVisit(2);
        Encounter labEncounter = null;
        Set<Encounter> encounters = visit.getEncounters();
        for (Encounter encounter : encounters) {
            if(encounter.getEncounterType().getName().equals("LAB_RESULT")) {
                labEncounter = encounter;
            }
        }

        assertEquals(2, encounters.size());
        assertNotNull(labEncounter);
        Set<Obs> obs = labEncounter.getAllObs();
        assertEquals(1, obs.size());

        openElisAccessionEventWorker.associateTestResultsToOrder(openElisAccession);
        visit = Context.getVisitService().getVisit(2);
        labEncounter = null;
        encounters = visit.getEncounters();
        for (Encounter encounter : encounters) {
            if(encounter.getEncounterType().getName().equals("LAB_RESULT")) {
                labEncounter = encounter;
            }
        }

        assertEquals(2, encounters.size());
        assertNotNull(labEncounter);
        obs = labEncounter.getAllObs(true);
        assertEquals(0, getVoidedObservations(obs).size());
        assertEquals(1, obs.size());
    }

    @Test
    public void shouldCreateOrderEncounterAndAssociateResultsForNewAccession() throws Exception {
        executeDataSet("labResult.xml");
        EncounterService encounterService = Context.getEncounterService();

        String panelConceptUuid = "cfc5056c-3f8e-11e3-968c-0800271c1b75";
        String haemoglobinConceptUuid = "7f7379ba-3ca8-11e3-bf2b-0800271c1b75";
        String accessionUuid = "6xfe4567-707a-4629-9850-f15206e9b0eX";
        String patientUuidWithNoOrders = "75e04d42-3ca8-11e3-bf2b-ab87271c1b75";

        assertNull(encounterService.getEncounterByUuid(accessionUuid));

        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder()
                .withPanelUuid(panelConceptUuid)
                .withTestUuid(haemoglobinConceptUuid)
                .withResult("10.5")
                .withProviderUuid("331c6bf8-7846-11e3-a96a-09xD371c1b75")
                .withMinNormal("10")
                .withMaxNormal("20.2")
                .withAbnormal("false")
                .withDateTime("2014-01-30T11:50:18+0530")
                .withResultType("N")
                .build();

        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder()
                .withDateTime("2014-01-30T11:50:18+0530")
                .withTestDetails(new HashSet<>(Arrays.asList(test1)))
                .withPatientUuid(patientUuidWithNoOrders)
                .build();
        openElisAccession.setAccessionUuid(accessionUuid);

        Event firstEvent = stubHttpClientToGetOpenElisAccession(accessionUuid, openElisAccession);
        openElisAccessionEventWorker.process(firstEvent);

        Visit visit = encounterService.getEncounterByUuid(accessionUuid).getVisit();
        Encounter labEncounter = null;
        Set<Encounter> encounters = visit.getEncounters();
        for (Encounter encounter : encounters) {
            if(encounter.getEncounterType().getName().equals("LAB_RESULT")) {
                labEncounter = encounter;
            }
        }

        assertEquals(2, encounters.size());
        assertNotNull(labEncounter);

        Set<Obs> obs = labEncounter.getAllObs();
        assertEquals(1, obs.size());
        Obs panelResultObs = getObsByConceptUuid(obs, panelConceptUuid);
        assertNotNull(panelResultObs);
        Set<Obs> panel1ResultMembers = panelResultObs.getGroupMembers();
        assertEquals(1,panel1ResultMembers.size());

        Set<Obs> topLevelObs = panel1ResultMembers;
        assertEquals(1, topLevelObs.size());
        final Set<Obs> testLevelObs = getGroupMembersForObs(topLevelObs);
        assertEquals(1, testLevelObs.size());
        final Set<Obs> resultMembers = getGroupMembersForObs(testLevelObs);
        assertEquals(4, resultMembers.size());

        Obs testResultObs = getObsByConceptUuid(testLevelObs, haemoglobinConceptUuid);
        assertNotNull(testResultObs);
        assertEquals(4, testResultObs.getGroupMembers().size());

    }

    @Test
    public void shouldCreateOrderEncounterAndAssociateResultsForNewAccessionWhenTheVisitToOrderEncounterIsClosed() throws Exception {
        executeDataSet("labResult.xml");
        EncounterService encounterService = Context.getEncounterService();

        String panelConceptUuid = "cfc5056c-3f8e-11e3-968c-0800271c1b75";
        String haemoglobinConceptUuid = "7f7379ba-3ca8-11e3-bf2b-0800271c1b75";
        String accessionUuid = "6xfe4567-707a-4629-9850-f15206e9b0eX";
        String patientUuidWithNoOrders = "75e04d42-3ca8-11e3-bf2b-ab87271c1b75";

        assertNull(encounterService.getEncounterByUuid(accessionUuid));

        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder()
                .withPanelUuid(panelConceptUuid)
                .withTestUuid(haemoglobinConceptUuid)
                .build();

        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder()
                .withDateTime("2014-01-30T11:50:18+0530")
                .withTestDetails(new HashSet<>(Arrays.asList(test1)))
                .withPatientUuid(patientUuidWithNoOrders)
                .build();
        openElisAccession.setAccessionUuid(accessionUuid);

        Event firstEvent = stubHttpClientToGetOpenElisAccession(accessionUuid, openElisAccession);
        openElisAccessionEventWorker.process(firstEvent);

        Visit visit = encounterService.getEncounterByUuid(accessionUuid).getVisit();
        Encounter labEncounter = null;
        Set<Encounter> encounters = visit.getEncounters();
        for (Encounter encounter : encounters) {
            if(encounter.getEncounterType().getName().equals("LAB_RESULT")) {
                labEncounter = encounter;
            }
        }

        assertEquals(1, encounters.size());
        assertNull(labEncounter);

        visit.setStopDatetime(new Date());
        Context.getVisitService().saveVisit(visit);

        test1 = new OpenElisTestDetailBuilder()
                .withPanelUuid(panelConceptUuid)
                .withTestUuid(haemoglobinConceptUuid)
                .withResult("10.5")
                .withProviderUuid("331c6bf8-7846-11e3-a96a-09xD371c1b75")
                .withMinNormal("10")
                .withMaxNormal("20.2")
                .withAbnormal("false")
                .withDateTime("2014-01-30T11:50:18+0530")
                .withResultType("N")
                .build();

        openElisAccession = new OpenElisAccessionBuilder()
                .withDateTime("2014-01-30T11:50:18+0530")
                .withTestDetails(new HashSet<>(Arrays.asList(test1)))
                .withPatientUuid(patientUuidWithNoOrders)
                .build();
        openElisAccession.setAccessionUuid(accessionUuid);

        firstEvent = stubHttpClientToGetOpenElisAccession(accessionUuid, openElisAccession);
        openElisAccessionEventWorker.process(firstEvent);

        Encounter orderEncounter = encounterService.getEncounterByUuid(accessionUuid);
        labEncounter = null;
        EncounterType labResultEncounterType = encounterService.getEncounterType("LAB_RESULT");

        List<Encounter> encounterList = encounterService.getEncounters(orderEncounter.getPatient(),
                null, orderEncounter.getEncounterDatetime(), null, null,
                Arrays.asList(labResultEncounterType),
                null, null, null, false);

        assertEquals(1, encounterList.size());
        labEncounter = encounterList.get(0);
        assertNotNull(labEncounter);

        Set<Obs> obs = labEncounter.getAllObs();
        assertEquals(1, obs.size());
        Obs panelResultObs = getObsByConceptUuid(obs, panelConceptUuid);
        assertNotNull(panelResultObs);
        Set<Obs> panel1ResultMembers = panelResultObs.getGroupMembers();
        assertEquals(1,panel1ResultMembers.size());

        Set<Obs> topLevelObs = panel1ResultMembers;
        assertEquals(1, topLevelObs.size());
        final Set<Obs> testLevelObs = getGroupMembersForObs(topLevelObs);
        assertEquals(1, testLevelObs.size());
        final Set<Obs> resultMembers = getGroupMembersForObs(testLevelObs);
        assertEquals(4, resultMembers.size());

        Obs testResultObs = getObsByConceptUuid(testLevelObs, haemoglobinConceptUuid);
        assertNotNull(testResultObs);
        assertEquals(4, testResultObs.getGroupMembers().size());

    }

    @Test
    public void shouldTestNewOrdersAndResultsAreCreatedInRightVisit() throws Exception {
        executeDataSet("labResultForOldVisits.xml");

        String patientUuid = "75e04d42-3ca8-11e3-bf2b-0800271c1b75";

        OpenElisTestDetail ureaNitrogenTest = new OpenElisTestDetailBuilder()
                .withTestUuid("7923d0e0-8734-11e3-baa7-0800200c9a66")
                .build();
        OpenElisTestDetail haemoglobinTest = new OpenElisTestDetailBuilder()
                .withTestUuid("7f7379ba-3ca8-11e3-bf2b-0800271c1b75")
                .build();

        String accessionDateStr = "2014-01-02T11:50:18+0530";
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder()
                .withDateTime(accessionDateStr)
                .withPatientUuid(patientUuid)
                .withTestDetails(new HashSet<>(Arrays.asList(ureaNitrogenTest, haemoglobinTest)))
                .build();
        openElisAccession.setAccessionUuid("NA0af4567-707a-4629-9850-f15206e63ab0");

        when(httpClient.get(openElisUrl + event.getContent(), OpenElisAccession.class)).thenReturn(openElisAccession);
        openElisAccessionEventWorker.process(event);

        VisitService visitService = Context.getVisitService();
        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        List<Visit> visits = visitService.getVisitsByPatient(patient, true, false);
        assertEquals(3, visits.size());
        Visit orderVisit = getVisitByStartDate(visits, DateTime.parse(accessionDateStr).toDate());
        assertNotNull(orderVisit);

        String ureaNitrogenTestDateStr = "2014-01-30T11:50:18+0530";
        ureaNitrogenTest = new OpenElisTestDetailBuilder()
                .withTestUuid("7923d0e0-8734-11e3-baa7-0800200c9a66")
                .withResult("10.5")
                .withProviderUuid("331c6bf8-7846-11e3-a96a-09xD371c1b75")
                .withMinNormal("10")
                .withMaxNormal("20.2")
                .withAbnormal("false")
                .withDateTime(ureaNitrogenTestDateStr)
                .withResultType("N")
                .build();
        String haemoglobinTestDateStr = "2014-02-01T11:50:18+0530";
        haemoglobinTest = new OpenElisTestDetailBuilder()
                .withTestUuid("7f7379ba-3ca8-11e3-bf2b-0800271c1b75")
                .withResult("120")
                .withProviderUuid("331c6bf8-7846-11e3-a96a-09xD371c1b75")
                .withMinNormal("100")
                .withMaxNormal("200")
                .withAbnormal("false")
                .withDateTime(haemoglobinTestDateStr)
                .withResultType("N")
                .build();

        openElisAccession = new OpenElisAccessionBuilder()
                .withDateTime(accessionDateStr)
                .withPatientUuid(patientUuid)
                .withTestDetails(new HashSet<>(Arrays.asList(ureaNitrogenTest, haemoglobinTest)))
                .build();
        openElisAccession.setAccessionUuid("NA0af4567-707a-4629-9850-f15206e63ab0");

        when(httpClient.get(openElisUrl + event.getContent(), OpenElisAccession.class)).thenReturn(openElisAccession);
        openElisAccessionEventWorker.process(event);

        List<Visit> allVisits = visitService.getVisitsByPatient(patient, true, false);
        assertEquals(4, allVisits.size());
        orderVisit = getVisitByStartDate(allVisits, DateTime.parse(accessionDateStr).toDate());
        assertNotNull(orderVisit);

        Visit visitForUreaResult = getVisitByStartDate(allVisits, DateTime.parse(ureaNitrogenTestDateStr).toDate());
        assertNotNull(visitForUreaResult);

        Visit visitForHaemoglobinResult = getVisitByStartDate(allVisits, DateTime.parse(haemoglobinTestDateStr).toDate());
        assertNotNull(visitForHaemoglobinResult);

    }

    private Visit getVisitByStartDate(List<Visit> visits, Date date) {
        for (Visit visit : visits) {
            if ((visit.getStartDatetime().compareTo(date) <= 0) &&
                   ((visit.getStopDatetime() == null) || (visit.getStopDatetime().compareTo(date) >= 0))) {
                return visit;
            }
        }
        return null;
    }

    private Event stubHttpClientToGetOpenElisAccession(String accessionUuid, OpenElisAccession openElisAccession) throws java.io.IOException {
        Event firstEvent = new Event("id", "openelis/accession/" + accessionUuid, "title", "feedUri");
        when(httpClient.get(properties.getOpenElisUri() + firstEvent.getContent(), OpenElisAccession.class)).thenReturn(openElisAccession);
        return firstEvent;
    }

    private Obs getObsByConceptUuid(Set<Obs> panel1ResultMembers, String conceptUuid) {
        Obs testResultObs = null;
        for (Obs testObs : panel1ResultMembers) {
            if (testObs.getConcept().getUuid().equals(conceptUuid)) {
                testResultObs = testObs;
                break;
            }
        }
        return testResultObs;
    }

    private List<Encounter> findEncountersForProvider(List<Encounter> labEncounters, String providerUuid) {
        List<Encounter> encounters = new ArrayList<>();
        for (Encounter encounter : labEncounters) {
            String encProviderUuid = encounter.getEncounterProviders().iterator().next().getProvider().getUuid();
            if (encProviderUuid.equals(providerUuid)) {
                encounters.add(encounter);
            }
        }
        return encounters;
    }

    private Obs getConceptResultObs(Set<Obs> members, String conceptUuid) {
        Obs obs = getObsByConceptUuid(members, conceptUuid);
        return getObsByConceptUuid(obs.getGroupMembers(), conceptUuid);
    }


    private ArrayList<Obs> getVoidedObservations(Set<Obs> allObs) {
        ArrayList<Obs> voidedObs = new ArrayList<Obs>();
        for (Obs obs : allObs) {
            if (obs.isVoided()) {
                //for individual test
                voidedObs.add(obs);
            } else if (obs.getConcept().isSet()) {
                //for tests in panel
                for (Obs member : obs.getGroupMembers()) {
                    if (member.isVoided()) {
                        voidedObs.add(member);
                    }
                }
            }
        }
        return voidedObs;
    }

    private Set<Obs> getGroupMembersForObs(Set<Obs> obs) {
        Obs testObs = obs.iterator().next();
        return testObs.getGroupMembers();
    }

}
