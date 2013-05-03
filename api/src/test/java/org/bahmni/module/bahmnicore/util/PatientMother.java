package org.bahmni.module.bahmnicore.util;

import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.module.webservices.rest.SimpleObject;

import java.text.ParseException;
import java.util.Arrays;

public class PatientMother {

    private String patientIdentifier;
    private NameMother nameMother = new NameMother();
    private AddressMother addressMother = new AddressMother();
    private String balance;

    public PatientMother() {
        patientIdentifier = "GAN11223344";
    }

    public SimpleObject buildSimpleObject() {
        SimpleObject simpleObject = new SimpleObject().add("birthdate", "01-01-2012")
                .add("age", 21)
                .add("gender", "M")
                .add("attributes", Arrays.asList(new SimpleObject()
                        .add("attributeType", "b3b6d540-a32e-44c7-91b3-292d97667518")
                        .add("value", "someCaste")))
                .add("addresses", Arrays.asList(addressMother.getSimpleObjectForAddress()))
                .add("centerID", new SimpleObject().add("name", "Ganiyari"))
                .add("names", Arrays.asList(nameMother.getSimpleObjectForName()))
                .add("identifier", patientIdentifier);
        if(balance != null){
            simpleObject.add("balance", balance);
        }

        return simpleObject;
    }

    public PatientMother withName(String firstName, String middleName, String lastName) {
        nameMother = nameMother.withName(firstName, middleName, lastName);
        return this;
    }

    public PatientMother withPatientIdentifier(String patientIdentifier) {
        this.patientIdentifier = patientIdentifier;
        return this;
    }

    public PatientMother withBalance(String balance) {
        this.balance = balance;
        return this;
    }

    public Patient build() {
        Patient patient = new Patient();
        patient.addIdentifier(new PatientIdentifier(patientIdentifier, null, null));
        patient.addName(nameMother.build());
        return patient;
    }

    public BahmniPatient buildBahmniPatient() throws ParseException {
        return new BahmniPatient(buildSimpleObject());
    }
}


