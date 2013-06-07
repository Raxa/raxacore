package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.mapper.PatientMapper;
import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.LocationService;
import org.openmrs.api.VisitService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BahmniPatientListServiceImplTest {
    @Mock
    VisitService visitService;
    @Mock
    LocationService locationService;
    @Mock
    PatientMapper patientMapper;

    private BahmniPatientListServiceImpl bahmniPatientListService;

    @Before
    public void setup(){
        initMocks(this);
        bahmniPatientListService = new BahmniPatientListServiceImpl(patientMapper);
        bahmniPatientListService.setVisitService(visitService);
        bahmniPatientListService.setLocationService(locationService);
    }

    @Test
    public void shouldReturnListOfActivePatients() {
        String locationName = "guniyari";
        Location location = new Location();
        List<Location> locations = new ArrayList<Location>();
        locations.add(location);
        Visit visit1 = new Visit();
        Patient patient = new Patient();
        visit1.setPatient(patient);
        Visit visit2 = new Visit();
        Patient patient2 = new Patient();
        visit2.setPatient(patient2);
        BahmniPatient bahmniPatient = new BahmniPatient();
        BahmniPatient bahmniPatient2 = new BahmniPatient();

        when(locationService.getLocation(locationName)).thenReturn(location);
        when(visitService.getVisits(null, null, locations, null, null, null, null, null, null, false, false)).thenReturn(Arrays.asList(visit1, visit2));
        when(patientMapper.mapFromPatient(null, patient)).thenReturn(bahmniPatient);
        when(patientMapper.mapFromPatient(null, patient2)).thenReturn(bahmniPatient2);

        List<BahmniPatient> allActivePatients = bahmniPatientListService.getAllActivePatients(locationName);

        assertEquals(2, allActivePatients.size());
        assert(allActivePatients.contains(bahmniPatient));
        assert(allActivePatients.contains(bahmniPatient2));
        verify(visitService).getVisits(null, null, locations, null, null, null, null, null, null, false, false);
        verify(locationService).getLocation(locationName);
        verify(patientMapper).mapFromPatient(null, patient);
        verify(patientMapper).mapFromPatient(null,patient2);
    }

    @Test
    public void shouldThrowExceptionWhenLocationIsNotFound() {
        String location = "ganiyari";
        when(locationService.getLocation(location)).thenReturn(null);
        try{
            bahmniPatientListService.getAllActivePatients(location);
            assert(false);
        }catch (Exception e){
            assert(true);
            assertEquals(String.format("Could not find location : %s", location), e.getMessage());
        }
    }
}
