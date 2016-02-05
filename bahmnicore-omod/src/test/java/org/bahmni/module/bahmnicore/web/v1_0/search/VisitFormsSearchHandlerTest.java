package org.bahmni.module.bahmnicore.web.v1_0.search;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.*;
import org.openmrs.api.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import org.openmrs.Patient;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@PrepareForTest(Context.class)
@RunWith(PowerMockRunner.class)

public class VisitFormsSearchHandlerTest {

    private VisitFormsSearchHandler visitFormsSearchHandler;
    @Mock
    RequestContext context;
    @Mock
    PatientService patientService;
    @Mock
    ConceptService conceptService;
    @Mock
    EncounterService encounterService;
    @Mock
    VisitService visitService;
    @Mock
    ObsService obsService;

    @Mock
    Encounter encounter;
    Patient patient;
    Concept concept;
    Visit visit;
    Obs obs;


    @Before
    public void before() throws Exception {
        initMocks(this);
        setUp();
        visitFormsSearchHandler = new VisitFormsSearchHandler();
    }

    public Concept createConcept(String conceptName, String locale) {
        concept = new Concept();
        concept.setFullySpecifiedName(new ConceptName(conceptName, new Locale(locale)));
        return concept;
    }

    public Obs createObs(Concept concept) {
        obs = new Obs();
        obs.setConcept(concept);
        return obs;
    }

    public void setUp() throws Exception {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(context.getLimit()).thenReturn(3);
        when(context.getRequest()).thenReturn(req);
        when(context.getRequest().getParameter("patient")).thenReturn("patientUuid");
        when(context.getRequest().getParameter("numberOfVisits")).thenReturn("10");

        String[] conceptNames = {"Vitals"};
        when(context.getRequest().getParameterValues("conceptNames")).thenReturn(conceptNames);
        patient = new Patient();
        patient.setId(1);
        patient.setUuid("patient-uuid");

        PowerMockito.mockStatic(Context.class);
        PowerMockito.when(Context.getPatientService()).thenReturn(patientService);
        when(patientService.getPatientByUuid("patientUuid")).thenReturn(patient);
        PowerMockito.when(Context.getConceptService()).thenReturn(conceptService);
        concept = createConcept("Vitals", "English");

        visit = new Visit();
        PowerMockito.when(Context.getVisitService()).thenReturn(visitService);
        PowerMockito.when(Context.getVisitService().getVisitsByPatient(patient)).thenReturn(Arrays.asList(visit));

        PowerMockito.when(Context.getEncounterService()).thenReturn(encounterService);
        encounter = mock(Encounter.class);
        PowerMockito.when(encounterService.getEncounters(any(Patient.class), any(Location.class), any(Date.class), any(Date.class), any(Collection.class), any(Collection.class), any(Collection.class), any(Collection.class), any(Collection.class), eq(false))).thenReturn(Arrays.asList(encounter));
        PowerMockito.when(Context.getObsService()).thenReturn(obsService);
        obs = createObs(concept);
    }

    @Test
    public void testGetSearchConfig() throws Exception {
        SearchConfig searchConfig = visitFormsSearchHandler.getSearchConfig();
        assertThat(searchConfig.getId(), is(equalTo("byPatientUuid")));

    }

    @Test
    public void shouldSupportVersions1_10To1_12() {
        SearchConfig searchConfig = visitFormsSearchHandler.getSearchConfig();
        assertTrue(searchConfig.getSupportedOpenmrsVersions().contains("1.10.*"));
        assertTrue(searchConfig.getSupportedOpenmrsVersions().contains("1.11.*"));
        assertTrue(searchConfig.getSupportedOpenmrsVersions().contains("1.12.*"));
    }

    @Test
    public void shouldReturnConceptSpecificObsIfConceptNameIsSpecified() throws Exception {

        PowerMockito.when(conceptService.getConcept("All Observation Templates")).thenReturn(concept);
        PowerMockito.when(conceptService.getConceptByName("Vitals")).thenReturn(concept);

        PowerMockito.when(obsService.getObservations(any(List.class), any(List.class), any(List.class), any(List.class), any(List.class), any(List.class), any(List.class), any(Integer.class), any(Integer.class), any(Date.class), any(Date.class), eq(false))).thenReturn(Arrays.asList(obs));
        NeedsPaging<Obs> searchResults = (NeedsPaging<Obs>) visitFormsSearchHandler.search(context);
        assertThat(searchResults.getPageOfResults().size(), is(equalTo(1)));
    }

    @Test
    public void shouldReturnAllObsIfConceptNameIsNotSpecified() throws Exception {

        when(context.getRequest().getParameterValues("conceptNames")).thenReturn(null);

        Concept parentConcept = new Concept();
        parentConcept.addSetMember(concept);
        Concept historyConcept = createConcept("History and Examination", "English");
        parentConcept.addSetMember(historyConcept);

        PowerMockito.when(conceptService.getConcept("All Observation Templates")).thenReturn(parentConcept);
        PowerMockito.when(conceptService.getConceptByName("Vitals")).thenReturn(concept);
        Obs obs2 = createObs(historyConcept);

        PowerMockito.when(obsService.getObservations(any(List.class), any(List.class), any(List.class), any(List.class), any(List.class), any(List.class), any(List.class), any(Integer.class), any(Integer.class), any(Date.class), any(Date.class), eq(false))).thenReturn(Arrays.asList(obs, obs2));
        NeedsPaging<Obs> searchResults = (NeedsPaging<Obs>) visitFormsSearchHandler.search(context);
        assertThat(searchResults.getPageOfResults().size(), is(equalTo(2)));
    }

    @Test

    public void getConceptsShouldReturnEmptyConceptSetIfConceptIsNotFound() throws Exception {

        String[] conceptNames = {null, null};
        when(context.getRequest().getParameterValues("conceptNames")).thenReturn(conceptNames);

        Concept parentConcept = new Concept();
        parentConcept.addSetMember(concept);
        Concept historyConcept = createConcept("History and Examination", "English");
        parentConcept.addSetMember(historyConcept);

        PowerMockito.when(conceptService.getConcept("All Observation Templates")).thenReturn(parentConcept);
        PowerMockito.when(Context.getConceptService()).thenReturn(conceptService);
        PowerMockito.when(conceptService.getConceptByName(null)).thenReturn(null);

        Obs obs2 = createObs(historyConcept);

        PowerMockito.when(obsService.getObservations(any(List.class), any(List.class), any(List.class), any(List.class), any(List.class), any(List.class), any(List.class), any(Integer.class), any(Integer.class), any(Date.class), any(Date.class), eq(false))).thenReturn(Arrays.asList(obs, obs2));
        NeedsPaging<Obs> searchResults = (NeedsPaging<Obs>) visitFormsSearchHandler.search(context);
        assertThat(searchResults.getPageOfResults().size(), is(equalTo(2)));
    }
}