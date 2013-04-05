package org.raxa.module.raxacore.web.v1_0.model;

import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class PersonTest {
    
    @Test
    public void shouldCreateAPersonFromASimpleObject() throws ParseException {
        String birthdate = "01-01-2012";
        String centerName = "Ganiyari";
        SimpleObject personObject = new SimpleObject().add("birthdate", birthdate).add("age", "21")
                .add("attributes", Arrays.asList(new SimpleObject().add("attributetype", "caste").add("value", "someCaste")))
                .add("addresses", Arrays.asList(new SimpleObject().add("address1", "7143 Koramangala")))
                .add("centerID", new SimpleObject().add("name", centerName))
                .add("names", Arrays.asList(new SimpleObject().add("givenName", "first").add("familyName", "Last")))
                .add("patientIdentifier", "someIdentifier");

        Person person = new Person(personObject);

        Date date = new SimpleDateFormat("dd-MM-yyyy").parse(birthdate);
        assertEquals(date, person.getBirthdate());
        assertEquals("someIdentifier", person.getPatientIdentifier());
        assertEquals(1, person.getAttributes().size());
        assertEquals(1, person.getAddresses().size());
        assertEquals(1, person.getNames().size());
        assertEquals(centerName, person.getCenterName());
    }
}
