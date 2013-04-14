package org.bahmni.jss.registration;

import au.com.bytecode.opencsv.CSVReader;
import org.bahmni.datamigration.PatientReader;
import org.bahmni.datamigration.request.patient.CenterId;
import org.bahmni.datamigration.request.patient.PatientAddress;
import org.bahmni.datamigration.request.patient.PatientAttribute;
import org.bahmni.datamigration.request.patient.PatientRequest;
import org.bahmni.datamigration.session.AllPatientAttributeTypes;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class AllRegistrations implements PatientReader {
    private CSVReader reader;
    private AllPatientAttributeTypes allPatientAttributeTypes;
    private AllLookupValues allCastes;

    public AllRegistrations(String csvLocation, String fileName, AllPatientAttributeTypes allPatientAttributeTypes, AllLookupValues allCastes) throws IOException {
        this.allPatientAttributeTypes = allPatientAttributeTypes;
        this.allCastes = allCastes;
        File file = new File(csvLocation, fileName);
        reader = new CSVReader(new FileReader(file), ',');
        reader.readNext(); //skip row
    }

    public PatientRequest nextPatient() throws IOException {
        String[] patientRow = reader.readNext();

        if (patientRow == null) return null;

        PatientRequest patientRequest = new PatientRequest();
        RegistrationNumber registrationNumber = RegistrationFields.parseRegistrationNumber(patientRow[0]);
        patientRequest.setPatientIdentifier(registrationNumber.getCenterCode() + registrationNumber.getId());
        patientRequest.setCenterID(new CenterId(registrationNumber.getCenterCode()));
        patientRequest.setName(patientRow[2], patientRow[3]);

        addPatientAttribute(patientRow[4], patientRequest, "primaryRelative", null);

        patientRequest.setGender(patientRow[5]);
        patientRequest.setBirthdate(RegistrationFields.getDate(patientRow[6]));
        patientRequest.setAge(Integer.parseInt(patientRow[7]));

        PatientAddress patientAddress = new PatientAddress();
        patientAddress.setCityVillage(RegistrationFields.sentenceCase(patientRow[7]));

        addPatientAttribute(patientRow[20], patientRequest, "caste", allCastes);
        return patientRequest;
    }

    private void addPatientAttribute(String value, PatientRequest patientRequest, String name, LookupValueProvider lookupValueProvider) {
        PatientAttribute patientAttribute = new PatientAttribute();
        patientAttribute.setAttributeType(allPatientAttributeTypes.getAttributeUUID(name));
        patientAttribute.setName(name);
        patientAttribute.setValue(lookupValueProvider == null ? value : lookupValueProvider.getLookUpValue(value));
        patientRequest.addPatientAttribute(patientAttribute);
    }
}