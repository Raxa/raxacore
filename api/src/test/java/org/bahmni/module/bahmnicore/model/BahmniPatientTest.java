package org.bahmni.module.bahmnicore.model;

import junit.framework.Assert;
import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BahmniPatientTest {

    @Test
    public void shouldCreateAPersonFromASimpleObject() throws ParseException {
        String birthdate = "01-01-2012";
        String centerName = "Ganiyari";
        double expectedBalance = 123;
        SimpleObject personObject = new SimpleObject().add("birthdate", birthdate).add("age", 21).add("gender", "M").add(
                "attributes", Arrays.asList(new SimpleObject().add("attributeType", "caste").add("value", "someCaste"))).add(
                "addresses", Arrays.asList(new SimpleObject().add("address1", "7143 Koramangala"))).add("centerID",
                new SimpleObject().add("name", centerName))
                .add("names", Arrays.asList(new SimpleObject().add("givenName", "first").add("familyName", "Last")))
                .add("patientIdentifier", "someIdentifier")
                .add("balance", "123");


        BahmniPatient person = new BahmniPatient(personObject);

        Date date = new SimpleDateFormat("dd-MM-yyyy").parse(birthdate);
        Assert.assertEquals(date, person.getBirthdate());
        Assert.assertEquals("M", person.getGender());
        Assert.assertEquals("someIdentifier", person.getPatientIdentifier());
        Assert.assertEquals(1, person.getAttributes().size());
        Assert.assertEquals(1, person.getAddresses().size());
        Assert.assertEquals(1, person.getNames().size());
        Assert.assertEquals(centerName, person.getCenterName());
        Assert.assertEquals(expectedBalance, person.getBalance());
    }

    @Test
    public void hasBalance() {
        BahmniPatient bahmniPatient = new BahmniPatient();
        bahmniPatient.setBalance("0.0");
        assertFalse(bahmniPatient.hasBalance());

        bahmniPatient.setBalance("0.1");
        assertTrue(bahmniPatient.hasBalance());
    }
}
