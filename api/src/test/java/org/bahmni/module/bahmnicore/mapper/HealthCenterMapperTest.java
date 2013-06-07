package org.bahmni.module.bahmnicore.mapper;

import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;

import java.util.Arrays;
import java.util.HashSet;

import static junit.framework.Assert.assertEquals;

public class HealthCenterMapperTest {
    @Test
    public void shouldMapPatientHealthCenterFromPatientAttribute() {

        Patient patient = new Patient();
        PersonAttribute attribute = new PersonAttribute();
        PersonAttributeType personAttributeType = new PersonAttributeType();
        personAttributeType.setName("Health Center");
        String value = "ganiyari";
        attribute.setValue(value);
        attribute.setAttributeType(personAttributeType);
        patient.setAttributes(new HashSet<PersonAttribute>(Arrays.asList(attribute)));

        BahmniPatient bahmniPatient = new HealthCenterMapper().mapFromPatient(null, patient);

        assertEquals(value,bahmniPatient.getCenterName());
    }
}
