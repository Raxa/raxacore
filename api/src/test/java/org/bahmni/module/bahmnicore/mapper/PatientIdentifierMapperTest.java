package org.bahmni.module.bahmnicore.mapper;

import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;

import java.util.Arrays;
import java.util.HashSet;

import static junit.framework.Assert.assertEquals;

public class PatientIdentifierMapperTest {
    @Test
    public void shouldMapIdentifierFromPatientToBahmniPatient() {
        PatientIdentifier identifier = new PatientIdentifier("GAN001", new PatientIdentifierType(), new Location());
        Patient patient = new Patient();
        patient.setIdentifiers(new HashSet<PatientIdentifier>(Arrays.asList(identifier)));

        BahmniPatient bahmniPatient = new PatientIdentifierMapper().mapFromPatient(null, patient);

        assertEquals(patient.getPatientIdentifier().getIdentifier(), bahmniPatient.getIdentifier());

    }
}
