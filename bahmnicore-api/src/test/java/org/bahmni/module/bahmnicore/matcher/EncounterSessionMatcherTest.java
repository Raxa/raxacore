package org.bahmni.module.bahmnicore.matcher;

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

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
        encounterSessionMatcher = new EncounterSessionMatcher(administrationService);
        visit = new Visit();

        providers = new HashSet<Provider>();
        Provider provider = new Provider();
        provider.setId(1234);
        providers.add(provider);
        encounterType = new EncounterType("Test", "Test");

        encounter = mock(Encounter.class);
        person = new Person();
        person.setId(1234);


    }

    @Test
    public void shouldReturnEncounterWithinEncounterSessionInterval(){
        when(encounter.getProvider()).thenReturn(person);
        when(encounter.getEncounterType()).thenReturn(encounterType);
        when(encounter.getEncounterDatetime()).thenReturn(new Date());
        when(administrationService.getGlobalProperty("bahmni.encountersession.duration")).thenReturn("60");


        Set<Encounter> encounters = new HashSet<Encounter>();
        encounters.add(encounter);
        visit.setEncounters(encounters);

        EncounterParameters encounterParameters =  EncounterParameters.instance();

        encounterParameters.setEncounterType(encounterType);
        encounterParameters.setProviders(providers);

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, encounterParameters);
        assertNotNull(encounterReturned);
    }

    @Test
    public void shouldNotReturnEncounterIfOutsideEncounterSessionInterval(){
        EncounterSessionMatcher encounterSessionMatcher = new EncounterSessionMatcher(administrationService);
        when(administrationService.getGlobalProperty("bahmni.encountersession.duration")).thenReturn("60");

        Visit visit = new Visit();

        Set<Provider> providers = new HashSet<Provider>();
        Provider provider = new Provider();
        provider.setId(1234);
        providers.add(provider);
        EncounterType encounterType = new EncounterType("Test", "Test");


        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, -2);
        Date timeBefore2Hours = cal.getTime();

        Encounter encounter = mock(Encounter.class);
        Person person = new Person();
        person.setId(1234);
        when(encounter.getProvider()).thenReturn(person);
        when(encounter.getEncounterType()).thenReturn(encounterType);
        when(encounter.getEncounterDatetime()).thenReturn(timeBefore2Hours);

        Set<Encounter> encounters = new HashSet<Encounter>();
        encounters.add(encounter);
        visit.setEncounters(encounters);

        EncounterParameters encounterParameters =  EncounterParameters.instance();

        encounterParameters.setEncounterType(encounterType);
        encounterParameters.setProviders(providers);

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, encounterParameters);
        assertNull(encounterReturned);

    }

    }
