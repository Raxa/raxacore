package org.openmrs.module.bahmniemrapi.encountertransaction.matcher;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.emrapi.encounter.EncounterParameters;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EncounterSessionMatcherTest {
    @Mock
    AdministrationService administrationService;
    Set<Provider> providers;
    EncounterType encounterType;
    @Mock
    Encounter encounter;
    Person person;
    Visit visit;
    EncounterSessionMatcher encounterSessionMatcher;

    @Before
    public void setUp(){
        initMocks(this);
        encounterSessionMatcher = new EncounterSessionMatcher();
        encounterSessionMatcher.setAdministrationService(administrationService);
        visit = new Visit();

        providers = new HashSet<>();
        Provider provider = new Provider();
        provider.setId(1234);
        providers.add(provider);
        encounterType = new EncounterType("Test", "Test");

        encounter = mock(Encounter.class);
        person = new Person();
        person.setId(1234);
        provider.setPerson(person);
    }

    @Test
    public void shouldReturnEncounterLastUpdatedWithinEncounterSessionInterval(){
        when(encounter.getProvider()).thenReturn(person);
        when(encounter.getEncounterType()).thenReturn(encounterType);
        when(encounter.getDateChanged()).thenReturn(new Date());
        when(encounter.getDateCreated()).thenReturn(DateUtils.addHours(new Date(), -2));
        when(administrationService.getGlobalProperty("bahmni.encountersession.duration")).thenReturn("60");
        visit.addEncounter(encounter);

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, getEncounterParameters(providers));

        assertNotNull(encounterReturned);
        assertEquals(encounter, encounterReturned);
    }

    @Test
    public void shouldUseCreatedDateForEncounterWithOutUpdates(){
        when(encounter.getProvider()).thenReturn(person);
        when(encounter.getEncounterType()).thenReturn(encounterType);
        when(encounter.getDateChanged()).thenReturn(null);
        when(encounter.getDateCreated()).thenReturn(new Date());
        when(administrationService.getGlobalProperty("bahmni.encountersession.duration")).thenReturn("60");
        visit.addEncounter(encounter);

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, getEncounterParameters(providers));

        assertNotNull(encounterReturned);
        assertEquals(encounter, encounterReturned);
    }

    @Test
    public void shouldNotReturnEncounterIfOutsideEncounterSessionInterval(){
        visit.addEncounter(encounter);
        when(encounter.getProvider()).thenReturn(person);
        when(encounter.getEncounterType()).thenReturn(encounterType);
        when(administrationService.getGlobalProperty("bahmni.encountersession.duration")).thenReturn("60");
        when(encounter.getDateChanged()).thenReturn(DateUtils.addHours(new Date(), -2));

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, getEncounterParameters(providers));

        assertNull(encounterReturned);
    }

    @Test
    public void shouldNotReturnEncounterIfEncounterParametersDoesNotHaveProvider(){
        visit.addEncounter(encounter);
        when(encounter.getProvider()).thenReturn(person);
        when(encounter.getEncounterType()).thenReturn(encounterType);
        when(administrationService.getGlobalProperty("bahmni.encountersession.duration")).thenReturn("60");
        when(encounter.getDateChanged()).thenReturn(new Date());

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, getEncounterParameters(new HashSet<Provider>()));

        assertNull(encounterReturned);
    }

    @Test
    public void shouldNotReturnEncounterIfEncounterDoesNotHaveProvider(){
        visit.addEncounter(encounter);
        when(encounter.getProvider()).thenReturn(null);
        when(encounter.getEncounterType()).thenReturn(encounterType);
        when(administrationService.getGlobalProperty("bahmni.encountersession.duration")).thenReturn("60");
        when(encounter.getDateChanged()).thenReturn(new Date());

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, getEncounterParameters(providers));

        assertNull(encounterReturned);
    }



    private EncounterParameters getEncounterParameters(Set<Provider> providers) {
        EncounterParameters encounterParameters =  EncounterParameters.instance();
        encounterParameters.setEncounterType(encounterType);
        encounterParameters.setProviders(providers);
        return encounterParameters;
    }
}
