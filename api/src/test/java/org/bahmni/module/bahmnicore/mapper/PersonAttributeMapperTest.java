package org.bahmni.module.bahmnicore.mapper;

import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.bahmni.module.bahmnicore.model.BahmniPersonAttribute;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;

import java.util.Arrays;
import java.util.HashSet;

import static junit.framework.Assert.assertEquals;

public class PersonAttributeMapperTest {
    @Test
    public void shouldMapPersonAttributesToPatientAttributes() {
        Patient patient = new Patient();
        PersonAttribute attribute = new PersonAttribute();
        PersonAttributeType attributeType = new PersonAttributeType();
        attributeType.setUuid("myuuid");
        attribute.setAttributeType(attributeType);
        attribute.setValue("blah");

        patient.setAttributes(new HashSet<PersonAttribute>(Arrays.asList(attribute)));

        BahmniPatient bahmniPatient = new PersonAttributeMapper().mapFromPatient(null, patient);

        BahmniPersonAttribute bahmniPersonAttribute = bahmniPatient.getAttributes().get(0);
        assertEquals(attribute.getAttributeType().getUuid(), bahmniPersonAttribute.getPersonAttributeUuid());
        assertEquals(attribute.getValue(), bahmniPersonAttribute.getValue());
    }
}
