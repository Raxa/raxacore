package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.contract.observation.ConceptDefinition;
import org.bahmni.module.bahmnicore.dao.PersonObsDao;
import org.bahmni.module.bahmnicore.service.BahmniPersonObsService;
import org.bahmni.module.bahmnicore.service.ConceptService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;

import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BahmniPersonObsServiceImplTest {

    BahmniPersonObsService personObsService;
    @Mock
    PersonObsDao personObsDao;

    @Mock
    ConceptService conceptService;

    private String personUUID = "12345";
    private Integer numberOfVisits = 3;

    @Before
    public void setUp(){
        initMocks(this);
        personObsService = new BahmniPersonObsServiceImpl(personObsDao,conceptService);
    }

    @Test
    public void shouldGetPersonObs() throws Exception {
        personObsService.getObsForPerson(personUUID);
        verify(personObsDao).getNumericObsByPerson(personUUID);
    }

    @Test
    public void shouldGetNumericConcepts() throws Exception {
        personObsService.getNumericConceptsForPerson(personUUID);
        verify(personObsDao).getNumericConceptsForPerson(personUUID);
    }

    @Test
    public void shouldGetObsByPatientUuidConceptNameAndNumberOfVisits() throws Exception {
        when(conceptService.conceptsFor(anyList())).thenReturn(new ConceptDefinition());
        personObsService.observationsFor(personUUID, Arrays.asList("Blood Pressure"), numberOfVisits);
        verify(personObsDao).getObsFor(personUUID,  Arrays.asList("Blood Pressure"), numberOfVisits);
    }
}
