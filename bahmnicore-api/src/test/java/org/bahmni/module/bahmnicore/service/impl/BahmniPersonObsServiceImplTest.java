package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.dao.PersonObsDao;
import org.bahmni.module.bahmnicore.mapper.builder.ConceptBuilder;
import org.bahmni.module.bahmnicore.service.BahmniObsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.util.LocaleUtility;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Locale;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LocaleUtility.class)
public class BahmniPersonObsServiceImplTest {

    BahmniObsService personObsService;
    @Mock
    PersonObsDao personObsDao;

    private String personUUID = "12345";

    @Before
    public void setUp(){
        mockStatic(LocaleUtility.class);
        when(LocaleUtility.getDefaultLocale()).thenReturn(Locale.ENGLISH);

        initMocks(this);
        personObsService = new BahmniObsServiceImpl(personObsDao);
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
        Concept bloodPressureConcept = new ConceptBuilder().withName("Blood Pressure").build();
        Integer numberOfVisits = 3;
        personObsService.observationsFor(personUUID, Arrays.asList(bloodPressureConcept), numberOfVisits);
        verify(personObsDao).getObsFor(personUUID,  Arrays.asList("Blood Pressure"), numberOfVisits, true);
    }
}
