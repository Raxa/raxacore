package org.bahmni.module.elisatomfeedclient.api.client;

import org.bahmni.module.bahmnicore.model.BahmniAddress;
import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.bahmni.module.bahmnicore.service.BahmniPatientService;
import org.bahmni.module.elisatomfeedclient.api.FeedProperties;
import org.ict4h.atomfeed.client.domain.Event;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.openmrs.PersonAddress;

import java.net.URI;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OpenElisPatientEventWorkerTest {

    @Mock
    private BahmniPatientService bahmniPatientService;
    @Mock
    private WebClient webClient;
    @Mock
    private FeedProperties feedProperties;

    private OpenElisPatientEventWorker openElisPatientEventWorker;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        openElisPatientEventWorker = new OpenElisPatientEventWorker(bahmniPatientService, webClient, feedProperties);
        when(feedProperties.getOpenElisUri()).thenReturn("http://localhost:8085");
    }

    @Test
    public void shouldCreatePatient() throws Exception {
        LocalDate birthDate = LocalDate.now();
        final String patientIdentifier = "GAN909";
        String patientUrl = "/openelis/ws/rest/patient/GAN909";
        String patientResponse = "{\"patientIdentifier\": \"" + patientIdentifier + "\", \"firstName\":\"Ram\"," +
                " \"lastName\":\"Singh\", \"gender\":\"M\", \"address1\":\"address1\", \"address3\": \"address3\"," +
                " \"cityVillage\": \"cityVillage\", \"countyDistrict\": \"\", \"stateProvince\": \"\", " +
                " \"dateOfBirth\": \"" + birthDate.toString("yyyy-MM-dd") + "\", \"address2\": \"address2\"}";

        when(webClient.get(eq(new URI("http://localhost:8085" + patientUrl)), anyMap())).thenReturn(patientResponse);
        openElisPatientEventWorker.process(new Event("id", patientUrl));

        ArgumentCaptor<BahmniPatient> bahmniPatientArgumentCaptor = ArgumentCaptor.forClass(BahmniPatient.class);
        verify(bahmniPatientService).createPatient(bahmniPatientArgumentCaptor.capture());

        BahmniPatient bahmniPatient = bahmniPatientArgumentCaptor.getValue();
        assertEquals(patientIdentifier, bahmniPatient.getIdentifier());
        assertEquals("Ram", bahmniPatient.getNames().get(0).getGivenName());
        assertEquals("Singh", bahmniPatient.getNames().get(0).getFamilyName());
        assertEquals("M", bahmniPatient.getGender());
        assertEquals(birthDate.toDate(), bahmniPatient.getBirthdate());
        BahmniAddress address = bahmniPatient.getAddresses().get(0);
        assertEquals("address1", address.getAddress1());
        assertEquals("address2", address.getAddress2());
        assertEquals("address3", address.getAddress3());
        assertEquals("cityVillage", address.getCityVillage());
    }

}