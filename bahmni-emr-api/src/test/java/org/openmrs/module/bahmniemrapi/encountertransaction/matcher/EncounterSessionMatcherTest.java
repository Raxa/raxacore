package org.openmrs.module.bahmniemrapi.encountertransaction.matcher;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.openmrs.*;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.bahmniemrapi.builder.EncounterBuilder;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.EncounterTypeIdentifier;
import org.openmrs.module.emrapi.encounter.EncounterParameters;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class EncounterSessionMatcherTest {
    @Mock
    AdministrationService administrationService;
    @Mock
    EncounterTypeIdentifier encounterTypeIdentifier;
    Set<Provider> providers;
    Set<EncounterProvider> encounterProviders;
    User creator;
    @Mock
    UserContext userContext;
    EncounterType encounterType;
    @Mock
    Encounter encounter;
    Person person;
    Visit visit;
    EncounterSessionMatcher encounterSessionMatcher;
    private Location location;

    @Before
    public void setUp(){
        initMocks(this);
        encounterSessionMatcher = new EncounterSessionMatcher(administrationService, encounterTypeIdentifier);
        visit = new Visit();

        providers = new HashSet<>();
        Provider provider = new Provider();
        provider.setId(1234);
        provider.setProviderId(1234);
        providers.add(provider);

        encounterProviders = new HashSet<>();
        EncounterProvider encounterProvider = new EncounterProvider();
        encounterProvider.setProvider(provider);
        encounterProviders.add(encounterProvider);

        encounterType = new EncounterType("Test", "Test");

        encounter = mock(Encounter.class);
        person = new Person();
        person.setId(1234);
        provider.setPerson(person);
        location = new Location();
        location.setUuid("location");

        creator = new User(person);
        creator.setId(1234);

        userContext = mock(UserContext.class);

        when(administrationService.getGlobalProperty("bahmni.encountersession.duration")).thenReturn("60");
        when(administrationService.getGlobalProperty("bahmni.encounterType.default")).thenReturn("Consultation");

        PowerMockito.mockStatic(Context.class);
        BDDMockito.given(Context.getUserContext()).willReturn(userContext);

        when(userContext.getAuthenticatedUser()).thenReturn(creator);
    }

    @Test
    public void shouldReturnEncounterLastUpdatedWithinEncounterSessionInterval(){
        when(encounter.getProvider()).thenReturn(person);
        when(encounter.getEncounterType()).thenReturn(encounterType);
        when(encounter.getDateChanged()).thenReturn(new Date());
        when(encounter.getDateCreated()).thenReturn(DateUtils.addHours(new Date(), -2));
        when(encounter.getLocation()).thenReturn(location);
        when(encounter.getEncounterProviders()).thenReturn(encounterProviders);
        when(encounter.getCreator()).thenReturn(creator);

        visit.addEncounter(encounter);

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, getEncounterParameters(providers, location));

        assertNotNull(encounterReturned);
        assertEquals(encounter, encounterReturned);
    }

    @Test
    public void shouldUseCreatedDateForEncounterWithOutUpdates(){
        when(encounter.getProvider()).thenReturn(person);
        when(encounter.getEncounterType()).thenReturn(encounterType);
        when(encounter.getDateChanged()).thenReturn(null);
        when(encounter.getDateCreated()).thenReturn(new Date());
        when(encounter.getLocation()).thenReturn(location);
        when(encounter.getEncounterProviders()).thenReturn(encounterProviders);
        when(encounter.getCreator()).thenReturn(creator);
        visit.addEncounter(encounter);

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, getEncounterParameters(providers, location));

        assertNotNull(encounterReturned);
        assertEquals(encounter, encounterReturned);
    }

    @Test
    public void shouldNotReturnEncounterIfOutsideEncounterSessionInterval(){
        visit.addEncounter(encounter);
        when(encounter.getProvider()).thenReturn(person);
        when(encounter.getEncounterType()).thenReturn(encounterType);
        when(encounter.getLocation()).thenReturn(location);
        when(encounter.getDateChanged()).thenReturn(DateUtils.addHours(new Date(), -2));

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, getEncounterParameters(providers, location));

        assertNull(encounterReturned);
    }

    @Test
    public void shouldNotReturnEncounterIfEncounterParametersDoesNotHaveProvider(){
        visit.addEncounter(encounter);
        when(encounter.getProvider()).thenReturn(person);
        when(encounter.getEncounterType()).thenReturn(encounterType);
        when(encounter.getLocation()).thenReturn(location);
        when(encounter.getDateChanged()).thenReturn(new Date());

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, getEncounterParameters(new HashSet<Provider>(), location));

        assertNull(encounterReturned);
    }

    @Test
    public void shouldNotReturnEncounterIfEncounterDoesNotHaveProvider(){
        visit.addEncounter(encounter);
        when(encounter.getProvider()).thenReturn(null);
        when(encounter.getEncounterType()).thenReturn(encounterType);
        when(encounter.getLocation()).thenReturn(location);
        when(administrationService.getGlobalProperty("bahmni.encountersession.duration")).thenReturn("60");
        when(encounter.getDateChanged()).thenReturn(new Date());

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, getEncounterParameters(providers, location));

        assertNull(encounterReturned);
    }

    @Test
    public void shouldNotReturnEncounterIfLocationDoesNotMatch(){
        visit.addEncounter(encounter);
        when(encounter.getProvider()).thenReturn(person);
        when(encounter.getEncounterType()).thenReturn(encounterType);
        when(encounter.getDateChanged()).thenReturn(new Date());
        when(encounter.getLocation()).thenReturn(location);
        Location nonLocation = new Location();
        nonLocation.setUuid("some");
        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, getEncounterParameters(providers, nonLocation));

        assertNull(encounterReturned);
    }

    @Test
    public void shouldReturnEncounterIfBothLocationsAreNull(){
        visit.addEncounter(encounter);
        when(encounter.getProvider()).thenReturn(person);
        when(encounter.getEncounterType()).thenReturn(encounterType);
        when(encounter.getDateChanged()).thenReturn(new Date());
        when(encounter.getLocation()).thenReturn(null);
        when(encounter.getEncounterProviders()).thenReturn(encounterProviders);
        when(encounter.getCreator()).thenReturn(creator);
        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, getEncounterParameters(providers, null));

        assertNotNull(encounterReturned);
    }

    @Test
    public void shouldReturnEncounterOfDefaultTypeIfEncounterParameterDoesNotHaveEncounterTypeAndLocationIsNotSet(){
        visit.addEncounter(encounter);
        when(encounter.getProvider()).thenReturn(person);
        EncounterType defaultEncounterType = new EncounterType();
        when(encounter.getEncounterType()).thenReturn(defaultEncounterType);
        when(encounter.getDateChanged()).thenReturn(new Date());
        when(encounter.getLocation()).thenReturn(null);
        when(encounter.getEncounterProviders()).thenReturn(encounterProviders);
        when(encounter.getCreator()).thenReturn(creator);
        when(encounterTypeIdentifier.getEncounterTypeFor(null)).thenReturn(defaultEncounterType);

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, getEncounterParameters(providers, null, null));

        assertNotNull(encounterReturned);
        assertTrue(encounter.getEncounterType().equals(defaultEncounterType));
    }

    @Test
    public void shouldGetEncounterBasedOnEncounterTypeOfLocationIfTheEncounterParametersEncounterTypeNotSet(){
        visit.addEncounter(encounter);
        EncounterType encounterType = new EncounterType();
        when(encounter.getEncounterType()).thenReturn(encounterType);
        when(encounter.getDateChanged()).thenReturn(new Date());
        when(encounter.getLocation()).thenReturn(location);
        when(encounter.getEncounterProviders()).thenReturn(encounterProviders);
        when(encounter.getCreator()).thenReturn(creator);
        EncounterParameters encounterParameters = getEncounterParameters(providers, location, null);
        when(encounterTypeIdentifier.getEncounterTypeFor("location")).thenReturn(encounterType);

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, encounterParameters);

        assertNotNull(encounterReturned);
        assertTrue(encounter.getEncounterType().equals(encounterType));
    }

    @Test
    public void shouldNotReturnEncounterIfEncounterTypeDoesNotMatch(){
        visit.addEncounter(encounter);
        when(encounter.getProvider()).thenReturn(person);
        when(encounter.getEncounterType()).thenReturn(encounterType);
        when(encounter.getDateChanged()).thenReturn(new Date());
        when(encounter.getLocation()).thenReturn(location);

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, getEncounterParameters(providers, location, new EncounterType()));

        assertNull(encounterReturned);
    }

    @Test
    public void shouldReturnEncounterBasedOnEncounterTypeMappedToLocation(){
        Encounter encounter1 = new EncounterBuilder().withEncounterType(new EncounterType()).withLocation(location).withProvider(person).withDateCreated(new Date()).withEncounterProviders(encounterProviders).withCreator(creator).build();
        Encounter encounter2 = new EncounterBuilder().withEncounterType(encounterType).withLocation(location).withProvider(person).withDateCreated(new Date()).withEncounterProviders(encounterProviders).withCreator(creator).build();
        visit.setEncounters(new LinkedHashSet<>(Arrays.asList(encounter1, encounter2)));
        EncounterParameters encounterParameters = getEncounterParameters(providers, location, null);
        when(encounterTypeIdentifier.getEncounterTypeFor(location.getUuid())).thenReturn(encounterType);

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, encounterParameters);

        assertEquals(encounter2, encounterReturned);
    }

    @Test
    public void shouldNotReturnVoidedEncounter(){
        Encounter encounter1 = new EncounterBuilder().withEncounterType(new EncounterType()).withLocation(location).withProvider(person).withDateCreated(new Date()).withEncounterProviders(encounterProviders).withCreator(creator).build();

        Encounter encounter2 = new EncounterBuilder().withEncounterType(encounterType).withLocation(location).withProvider(person).withDateCreated(new Date()).withEncounterProviders(encounterProviders).withCreator(creator).build();
        encounter2.setVoided(true);

        Encounter encounter3 = new EncounterBuilder().withEncounterType(encounterType).withLocation(location).withProvider(person).withDateCreated(new Date()).withEncounterProviders(encounterProviders).withCreator(creator).build();

        visit.setEncounters(new LinkedHashSet<>(Arrays.asList(encounter1, encounter2, encounter3)));
        EncounterParameters encounterParameters = getEncounterParameters(providers, location, null);
        when(encounterTypeIdentifier.getEncounterTypeFor(location.getUuid())).thenReturn(encounterType);

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, encounterParameters);

        assertEquals(encounter3, encounterReturned);
    }

    @Test
    public void shouldNotReturnEncounterIfEncounterParametersDateAndEncounterDateAreNotSame() {
        Date encounterDateTime = new Date();
        visit.addEncounter(encounter);
        when(encounter.getProvider()).thenReturn(person);
        when(encounter.getEncounterType()).thenReturn(encounterType);
        when(encounter.getDateCreated()).thenReturn(DateUtils.addDays(encounterDateTime, -3));
        when(encounter.getDateChanged()).thenReturn(DateUtils.addDays(encounterDateTime, -2));
        when(encounter.getEncounterDatetime()).thenReturn(encounterDateTime);
        when(encounter.getLocation()).thenReturn(location);
        when(encounter.getEncounterProviders()).thenReturn(encounterProviders);
        when(encounter.getCreator()).thenReturn(creator);

        EncounterParameters encounterParameters = getEncounterParameters(providers, location, encounterType);
        encounterParameters.setEncounterDateTime(new Date());
        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, encounterParameters);
        assertNull(encounterReturned);
    }

    @Test
    public void shouldNotCareForSessionIfTheDataIsRetrospective(){
        when(encounter.getProvider()).thenReturn(person);
        when(encounter.getEncounterType()).thenReturn(encounterType);
        when(encounter.getLocation()).thenReturn(location);
        when(encounter.getEncounterDatetime()).thenReturn(DateUtils.addDays(new Date(), -10));
        when(encounter.getEncounterProviders()).thenReturn(encounterProviders);
        when(encounter.getCreator()).thenReturn(creator);
        visit.addEncounter(encounter);

        EncounterParameters encounterParameters = getEncounterParameters(providers, location);
        encounterParameters.setEncounterDateTime(DateUtils.addDays(new Date(), -10));

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, encounterParameters);
        assertNotNull(encounterReturned);
    }

    @Test
    public void shouldReturnNullIfDifferentUserTriesToAccessExistingProviderEncounter(){
        Person person = new Person();
        person.setId(12345);
        User creator = new User(person);
        creator.setId(12345);

        Encounter encounter1 = new EncounterBuilder().withEncounterType(new EncounterType()).withLocation(location).withProvider(person).withDateCreated(new Date()).withEncounterProviders(encounterProviders).withCreator(creator).build();

        Encounter encounter2 = new EncounterBuilder().withEncounterType(encounterType).withLocation(location).withProvider(person).withDateCreated(new Date()).withEncounterProviders(encounterProviders).withCreator(creator).build();
        encounter2.setVoided(true);

        Encounter encounter3 = new EncounterBuilder().withEncounterType(encounterType).withLocation(location).withProvider(person).withDateCreated(new Date()).withEncounterProviders(encounterProviders).withCreator(creator).build();

        visit.setEncounters(new LinkedHashSet<>(Arrays.asList(encounter1, encounter2, encounter3)));
        EncounterParameters encounterParameters = getEncounterParameters(providers, location, null);
        when(encounterTypeIdentifier.getEncounterTypeFor(location.getUuid())).thenReturn(encounterType);

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, encounterParameters);

        assertNull(encounterReturned);
    }

    private EncounterParameters getEncounterParameters(Set<Provider> providers, Location location) {
        return getEncounterParameters(providers, location, this.encounterType);
    }

    private EncounterParameters getEncounterParameters(Set<Provider> providers, Location location, EncounterType encounterType) {
        EncounterParameters encounterParameters =  EncounterParameters.instance();
        encounterParameters.setEncounterType(encounterType);
        encounterParameters.setProviders(providers);
        encounterParameters.setLocation(location);
        return encounterParameters;
    }
}
