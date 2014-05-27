package org.bahmni.jss.registration;

import org.bahmni.datamigration.AddressService;
import org.bahmni.datamigration.AllLookupValues;
import org.bahmni.openmrsconnector.AllPatientAttributeTypes;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;

// TODO : Mujir - move this test to PatientPersister
public class AllRegistrationsTest {
    @Mock
    private AllLookupValues allCastes;
    @Mock
    private AllLookupValues empty;
    @Mock
    private AllPatientAttributeTypes allPatientAttributeTypes;
    @Mock
    private AddressService addressService;

    @Test
    @Ignore
    public void nextPatient() throws IOException {
 /*       initMocks(this);
        when(allCastes.getLookUpValue("1", 0)).thenReturn("Chamar");
        when(addressService.getTehsilFor(any(FullyQualifiedTehsil.class))).thenReturn(new FullyQualifiedTehsil("Kota", "Tota", "Dota"));
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("RegistrationMaster_Sample.csv");
        InputStreamReader reader = new InputStreamReader(resourceAsStream);
        HashMap<String, AllLookupValues> lookupValuesMap = new HashMap<String, AllLookupValues>();
        lookupValuesMap.put("Castes", allCastes);
        lookupValuesMap.put("Classes", empty);
        lookupValuesMap.put("Districts", empty);
        lookupValuesMap.put("States", empty);
        lookupValuesMap.put("Tahsils", empty);
        AllRegistrations allRegistrations = new AllRegistrations(allPatientAttributeTypes, lookupValuesMap, reader, new StringWriter(), addressService);
        PatientData patientData = allRegistrations.nextPatient();
        assertNotNull(patientData);
        PatientRequest patientRequest = patientData.getPatientRequest();
        assertNotNull(patientRequest);
        assertEquals(2, patientRequest.getAttributes().size());
        assertEquals("Chamar", patientRequest.getAttributes().get(1).getValue());

        allRegistrations.nextPatient();
        allRegistrations.done();*/
    }
}