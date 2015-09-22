package org.openmrs.module.bahmniemrapi.encountertransaction.matcher;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.openmrs.*;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.api.impl.EncounterServiceImpl;
import org.openmrs.module.bahmniemrapi.builder.EncounterBuilder;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.EncounterTypeIdentifier;
import org.openmrs.module.emrapi.encounter.EncounterParameters;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class EncounterSessionMatcherTest {
    @Mock
    AdministrationService administrationService;
    @Mock
    EncounterTypeIdentifier encounterTypeIdentifier;
    @Mock
    EncounterServiceImpl encounterService;
    Set<Provider> providers;
    Set<EncounterProvider> encounterProviders;
    User creator;
    @Mock
    UserContext userContext;
    EncounterType encounterType;
    @Mock
    Encounter encounter;
    Person person;
    Patient patient;
    Visit visit;
    EncounterSessionMatcher encounterSessionMatcher;
    private Location location;

    @Before
    public void setUp(){
        initMocks(this);
        encounterSessionMatcher = new EncounterSessionMatcher(administrationService, encounterTypeIdentifier, encounterService);
        visit = new Visit();
        visit.setId(3);

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

        patient = new Patient();
        patient.setId(1111);
        patient.setUuid("patient_uuid");

        userContext = mock(UserContext.class);

        when(administrationService.getGlobalProperty("bahmni.encountersession.duration")).thenReturn("60");
        when(administrationService.getGlobalProperty("bahmni.encounterType.default")).thenReturn("Consultation");
        when(encounter.getCreator()).thenReturn(creator);
        when(encounter.getEncounterDatetime()).thenReturn(new Date());

        PowerMockito.mockStatic(Context.class);
        BDDMockito.given(Context.getUserContext()).willReturn(userContext);

        when(userContext.getAuthenticatedUser()).thenReturn(creator);

        when(encounterService.getEncounters(any(Patient.class), any(Location.class), any(Date.class), any(Date.class), any(Collection.class), any(Collection.class), any(Collection.class), any(Collection.class), any(Collection.class), eq(false))).thenReturn(Arrays.asList(encounter));
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
        when(encounterTypeIdentifier.getDefaultEncounterType()).thenReturn(defaultEncounterType);

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, getEncounterParameters(providers, null, null));

        assertNotNull(encounterReturned);
        assertTrue(encounter.getEncounterType().equals(defaultEncounterType));
    }

    @Test
    public void shouldGetEncounter(){
        EncounterParameters encounterParameters = getEncounterParameters(providers, location);
        encounterParameters.setEncounterDateTime(new Date());

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, encounterParameters);

        ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
        ArgumentCaptor<Location> locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        ArgumentCaptor<Date> dateArgumentCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Collection> collectionArgumentCaptor = ArgumentCaptor.forClass(Collection.class);

        verify(encounterService).getEncounters(patientArgumentCaptor.capture(), locationArgumentCaptor.capture(), dateArgumentCaptor.capture(), dateArgumentCaptor.capture(), collectionArgumentCaptor.capture(), collectionArgumentCaptor.capture(), collectionArgumentCaptor.capture(),collectionArgumentCaptor.capture(), collectionArgumentCaptor.capture(), eq(false));
        System.out.println("expected" + encounterParameters.getEncounterDateTime());
        System.out.println("actual" + dateArgumentCaptor.getAllValues().get(1));
        System.out.println("expected" + DateUtils.addMinutes(encounterParameters.getEncounterDateTime(), -60));
        System.out.println("actual" + dateArgumentCaptor.getAllValues().get(0));
        System.out.println("expected" + encounterReturned);
        assertEquals(encounterParameters.getEncounterDateTime(), dateArgumentCaptor.getAllValues().get(1));
        assertEquals(DateUtils.addMinutes(encounterParameters.getEncounterDateTime(), -60), dateArgumentCaptor.getAllValues().get(0));
        assertNotNull(encounterReturned);
    }

    @Test
    public void shouldReturnNullWhenNewlyCreatedVisitIsPassedEncounter(){
        EncounterParameters encounterParameters = getEncounterParameters(providers, location);
        encounterParameters.setEncounterDateTime(DateUtils.addDays(new Date(), -10));

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(new Visit(), encounterParameters);
        ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
        ArgumentCaptor<Location> locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        ArgumentCaptor<Date> dateArgumentCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Collection> collectionArgumentCaptor = ArgumentCaptor.forClass(Collection.class);

        verify(encounterService, times(0)).getEncounters(patientArgumentCaptor.capture(), locationArgumentCaptor.capture(), dateArgumentCaptor.capture(), dateArgumentCaptor.capture(), collectionArgumentCaptor.capture(), collectionArgumentCaptor.capture(), collectionArgumentCaptor.capture(),collectionArgumentCaptor.capture(), collectionArgumentCaptor.capture(), eq(false));
        assertNull(encounterReturned);
    }

    @Test
    public void shouldGetEncounterFromSameDay(){
        EncounterParameters encounterParameters = getEncounterParameters(providers, location);
        Date encounterDateTime = DateUtils.addMinutes(DateUtils.truncate(new Date(), Calendar.DATE), 15);
        encounterParameters.setEncounterDateTime(encounterDateTime);

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, encounterParameters);
        ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
        ArgumentCaptor<Location> locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        ArgumentCaptor<Date> dateArgumentCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Collection> collectionArgumentCaptor = ArgumentCaptor.forClass(Collection.class);

        verify(encounterService).getEncounters(patientArgumentCaptor.capture(), locationArgumentCaptor.capture(), dateArgumentCaptor.capture(), dateArgumentCaptor.capture(), collectionArgumentCaptor.capture(), collectionArgumentCaptor.capture(), collectionArgumentCaptor.capture(), collectionArgumentCaptor.capture(), collectionArgumentCaptor.capture(), eq(false));
        assertEquals(DateUtils.truncate(encounterParameters.getEncounterDateTime(), Calendar.DATE), dateArgumentCaptor.getAllValues().get(0));
        assertEquals(encounterParameters.getEncounterDateTime(), dateArgumentCaptor.getAllValues().get(1));
        assertNotNull(encounterReturned);
    }

    @Test
    public void shouldGetRetrospectiveEncounter(){
        EncounterParameters encounterParameters = getEncounterParameters(providers, location);
        encounterParameters.setEncounterDateTime(DateUtils.truncate(new Date(), Calendar.DATE));

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(visit, encounterParameters);
        ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
        ArgumentCaptor<Location> locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        ArgumentCaptor<Date> dateArgumentCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Collection> collectionArgumentCaptor = ArgumentCaptor.forClass(Collection.class);

        verify(encounterService).getEncounters(patientArgumentCaptor.capture(), locationArgumentCaptor.capture(), dateArgumentCaptor.capture(), dateArgumentCaptor.capture(), collectionArgumentCaptor.capture(), collectionArgumentCaptor.capture(), collectionArgumentCaptor.capture(), collectionArgumentCaptor.capture(), collectionArgumentCaptor.capture(), eq(false));
        assertEquals(dateArgumentCaptor.getAllValues().get(0), dateArgumentCaptor.getAllValues().get(1));
        assertEquals(encounterParameters.getEncounterDateTime(), dateArgumentCaptor.getAllValues().get(0));
        assertNotNull(encounterReturned);
    }

    @Test
    public void shouldMatchEncounterBasedOnUserWhenNoProviderIsSupplied(){
        EncounterParameters encounterParameters = getEncounterParameters(null, location);
        encounterParameters.setEncounterDateTime(DateUtils.truncate(new Date(), Calendar.DATE));


        Encounter e1 = new Encounter();
        User creator1 = new User(1);
        e1.setCreator(creator1);

        Encounter e2 = new Encounter();
        User creator2 = new User(2);
        e2.setCreator(creator2);

        when(userContext.getAuthenticatedUser()).thenReturn(creator1);
        when(encounterService.getEncounters(any(Patient.class), any(Location.class), any(Date.class), any(Date.class), any(Collection.class), any(Collection.class), any(Collection.class), any(Collection.class), any(Collection.class), eq(false))).thenReturn(Arrays.asList(e1, e2));

        Encounter encounterReturned = encounterSessionMatcher.findEncounter(null, encounterParameters);
        assertNotNull(encounterReturned);
        assertEquals(encounterReturned.getCreator(), creator1);
    }


    private EncounterParameters getEncounterParameters(Set<Provider> providers, Location location) {
        return getEncounterParameters(providers, location, this.encounterType);
    }

    private EncounterParameters getEncounterParameters(Set<Provider> providers, Location location, EncounterType encounterType) {
        EncounterParameters encounterParameters =  EncounterParameters.instance();
        encounterParameters.setPatient(patient);
        encounterParameters.setEncounterType(encounterType);
        encounterParameters.setProviders(providers);
        encounterParameters.setLocation(location);
        return encounterParameters;
    }
}
