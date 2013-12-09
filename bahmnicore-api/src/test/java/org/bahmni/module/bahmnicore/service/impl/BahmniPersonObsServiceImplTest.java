package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.dao.PersonObsDao;
import org.bahmni.module.bahmnicore.service.BahmniPersonObsService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class BahmniPersonObsServiceImplTest extends BaseModuleWebContextSensitiveTest {

    BahmniPersonObsService personObsService;
    @Mock
    PersonObsDao personObsDao;
    private String personUUID = "12345";

    @Before
    public void setUp(){
        initMocks(this);
        personObsService = new BahmniPersonObsServiceImpl(personObsDao);
    }

    @Test
    public void shouldGetPersonObs() throws Exception {
        List<Obs> obsForPerson = personObsService.getObsForPerson(personUUID);
        verify(personObsDao).getObsByPerson(personUUID);
    }

    @Test
    public void shouldGetNumericConcepts() throws Exception {
        List<Concept> conceptList = personObsService.getNumericConceptsForPerson(personUUID);
        verify(personObsDao).getNumericConceptsForPerson(personUUID);
    }
}





