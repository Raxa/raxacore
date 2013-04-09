package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonName;
import org.openmrs.module.webservices.rest.SimpleObject;

import java.util.Arrays;

public class PatientMother {

    private String firstName;
    private String middleName;
    private String lastName;
    private String patientIdentifier;

    public PatientMother() {
        firstName = "first";
        lastName = "last";
        middleName = "middle";
        patientIdentifier = "GAN11223344";
    }

    public SimpleObject buildSimpleObject() {
        return new SimpleObject().add("birthdate", "01-01-2012").add("age", 21).add("gender", "M").add(
                "attributes",
                Arrays.asList(new SimpleObject().add("attributeType", "b3b6d540-a32e-44c7-91b3-292d97667518").add("value",
                        "someCaste"))).add("addresses", Arrays.asList(new SimpleObject().add("address1", "7143 Koramangala"))).add(
                "centerID", new SimpleObject().add("name", "Ganiyari")).add("names",
                Arrays.asList(new SimpleObject().add("givenName", firstName).add("familyName", lastName).add("middleName", middleName)))
                .add("patientIdentifier", patientIdentifier);
    }


    public PatientMother withName(String firstName, String middleName, String lastName) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        return this;
    }

    public PatientMother withPatientIdentifier(String patientIdentifier) {
        this.patientIdentifier = patientIdentifier;
        return this;
    }

    public Patient build() {
        Patient patient = new Patient();
        patient.addIdentifier(new PatientIdentifier(patientIdentifier, null, null));
        patient.addName(new PersonName(firstName, middleName, lastName));
        return patient;
    }
}


