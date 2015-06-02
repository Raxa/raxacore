package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.bahmni.module.bahmnicore.service.BahmniObsService;
import org.bahmni.test.builder.ConceptBuilder;
import org.bahmni.test.builder.VisitBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.*;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.VisitService;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.ETObsToBahmniObsMapper;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.OMRSObsToBahmniObsMapper;
import org.openmrs.module.emrapi.encounter.ObservationMapper;
import org.openmrs.module.emrapi.encounter.matcher.ObservationTypeMatcher;
import org.openmrs.util.LocaleUtility;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LocaleUtility.class)
public class BahmniObsServiceImplTest {

    BahmniObsService bahmniObsService;

    private String personUUID = "12345";

    @Mock
    ObsDao obsDao;
    @Mock
    private ObservationTypeMatcher observationTypeMatcher;
    @Mock
    private ObservationMapper observationMapper;
    @Mock
    private VisitService visitService;
    @Mock
    private ObsService obsService;
    @Mock
    private ConceptService conceptService;

    @Before
    public void setUp() {
        initMocks(this);

        mockStatic(LocaleUtility.class);
        when(LocaleUtility.getDefaultLocale()).thenReturn(Locale.ENGLISH);
        when(observationTypeMatcher.getObservationType(any(Obs.class))).thenReturn(ObservationTypeMatcher.ObservationType.OBSERVATION);
        bahmniObsService = new BahmniObsServiceImpl(obsDao, new OMRSObsToBahmniObsMapper(new ETObsToBahmniObsMapper(null), observationTypeMatcher, observationMapper), visitService, obsService, conceptService);
    }

    @Test
    public void shouldGetPersonObs() throws Exception {
        bahmniObsService.getObsForPerson(personUUID);
        verify(obsDao).getNumericObsByPerson(personUUID);
    }

    @Test
    public void shouldGetNumericConcepts() throws Exception {
        bahmniObsService.getNumericConceptsForPerson(personUUID);
        verify(obsDao).getNumericConceptsForPerson(personUUID);
    }

    @Test
    public void shouldGetObsByPatientUuidConceptNameAndNumberOfVisits() throws Exception {
        Concept bloodPressureConcept = new ConceptBuilder().withName("Blood Pressure").build();
        Integer numberOfVisits = 3;
        bahmniObsService.observationsFor(personUUID, Arrays.asList(bloodPressureConcept), numberOfVisits, null, false);
        verify(obsDao).getObsFor(personUUID, Arrays.asList("Blood Pressure"), numberOfVisits, null, false);
    }

    @Test
    public void shouldGetInitialObservations() throws Exception {
        Concept weightConcept = new ConceptBuilder().withName("Weight").build();
        Integer limit = 1;
        VisitBuilder visitBuilder = new VisitBuilder();
        Visit visit = visitBuilder.withUUID("visitId").withEncounter(new Encounter(1)).withPerson(new Person()).build();
        List<String> obsIgnoreList = new ArrayList<>();
        bahmniObsService.getInitialObsByVisit(visit, Arrays.asList(weightConcept), obsIgnoreList, true);
        verify(obsDao).getInitialObsByVisit(visit, "Weight", limit, obsIgnoreList, true);
    }

    @Test
    public void shouldGetAllObsForOrder() throws Exception {
        bahmniObsService.getObservationsForOrder("orderUuid");
        verify(obsDao, times(1)).getObsForOrder("orderUuid");
    }
}
