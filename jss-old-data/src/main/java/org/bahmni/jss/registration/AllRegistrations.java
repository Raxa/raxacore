package org.bahmni.jss.registration;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bahmni.address.sanitiser.AddressSanitiser;
import org.bahmni.address.sanitiser.SanitizerPersonAddress;
import org.bahmni.datamigration.PatientData;
import org.bahmni.datamigration.PatientEnumerator;
import org.bahmni.datamigration.request.patient.*;
import org.bahmni.datamigration.session.AllPatientAttributeTypes;

import java.io.*;
import java.util.Map;

import static org.bahmni.jss.registration.RegistrationFields.sentenceCase;

public class AllRegistrations implements PatientEnumerator {
    private CSVReader csvReader;
    private CSVWriter csvWriter;
    static int count =0;
    private AllPatientAttributeTypes allPatientAttributeTypes;
    private Map<String, AllLookupValues> lookupValuesMap;
    private AddressSanitiser addressSanitiser;

    public AllRegistrations(String csvLocation, String fileName, AllPatientAttributeTypes allPatientAttributeTypes, Map<String,
            AllLookupValues> lookupValuesMap, AddressSanitiser addressSanitiser) throws IOException {
        File file = new File(csvLocation, fileName);
        FileReader fileReader = new FileReader(file);

        File errorFile = new File(csvLocation, fileName + ".err.csv");
        FileWriter fileWriter = new FileWriter(errorFile);
        init(allPatientAttributeTypes, lookupValuesMap, fileReader, fileWriter, addressSanitiser);
    }

    public AllRegistrations(AllPatientAttributeTypes allPatientAttributeTypes, Map<String, AllLookupValues> lookupValuesMap,
                            Reader reader, Writer writer, AddressSanitiser addressSanitiser) throws IOException {
        init(allPatientAttributeTypes, lookupValuesMap, reader, writer, addressSanitiser);
    }

    private void init(AllPatientAttributeTypes allPatientAttributeTypes, Map<String, AllLookupValues> lookupValuesMap, Reader reader, Writer writer, AddressSanitiser addressSanitiser) throws IOException {
        this.lookupValuesMap = lookupValuesMap;
        this.csvReader = new CSVReader(reader, ',','"', '\0');
        this.csvWriter = new CSVWriter(writer, ',');
        String[] headerRow = this.csvReader.readNext();//skip row
        this.csvWriter.writeNext(headerRow);
        this.allPatientAttributeTypes = allPatientAttributeTypes;
        this.addressSanitiser = addressSanitiser;
    }

    public PatientData nextPatient() {
        String[] patientRow = null;
        try {

            patientRow = csvReader.readNext();
            if (patientRow == null) return null;

            PatientRequest patientRequest = new PatientRequest();
            RegistrationNumber registrationNumber = RegistrationFields.parseRegistrationNumber(patientRow[0]);
            patientRequest.setIdentifier(registrationNumber.getCenterCode() + registrationNumber.getId());
            patientRequest.setCenterID(new CenterId(registrationNumber.getCenterCode()));

            Name name = RegistrationFields.name(patientRow[2], patientRow[3]);
            patientRequest.setName(sentenceCase(name.getGivenName()), sentenceCase(name.getFamilyName()));

            addPatientAttribute(patientRow[4], patientRequest, "primaryRelative", null, 0);
            patientRequest.setDateOfRegistration(RegistrationFields.getDate(patientRow[1]));

            patientRequest.setGender(patientRow[5]);
            patientRequest.setBirthdate(RegistrationFields.getDate(patientRow[6]));
            patientRequest.setAge(RegistrationFields.getAge(patientRow[7]));

            PatientAddress patientAddress = new PatientAddress();
            patientRequest.addPatientAddress(patientAddress);


            patientRequest.setBalance(patientRow[17]);

            addPatientAttribute(patientRow[20], patientRequest, "caste", lookupValuesMap.get("Castes"), 0);
            addPatientAttribute(patientRow[32], patientRequest, "class", lookupValuesMap.get("Classes"), 0);

            //Address information
            String gramPanchayat = patientRow[34];
            patientAddress.setAddress2(sentenceCase(gramPanchayat));

            SanitizerPersonAddress sanitizerPersonAddress = new SanitizerPersonAddress();
            String stateId = lookupValuesMap.get("Districts").getLookUpValue(patientRow[26], 0);
            if (stateId != null) {
                String state = lookupValuesMap.get("States").getLookUpValue(stateId);
                sanitizerPersonAddress.setState(sentenceCase(state));
            }

            String district = lookupValuesMap.get("Districts").getLookUpValue(patientRow[26], 2);
            sanitizerPersonAddress.setDistrict(sentenceCase(district));

            sanitizerPersonAddress.setVillage(sentenceCase(patientRow[10]));
            sanitizerPersonAddress.setTehsil(sentenceCase(patientRow[35]));
            SanitizerPersonAddress sanitisedAddress = addressSanitiser.sanitise(sanitizerPersonAddress);


            //after sanitization
            patientAddress.setStateProvince(sanitisedAddress.getState());
            patientAddress.setCountyDistrict(sanitisedAddress.getDistrict());
            patientAddress.setCityVillage(sanitisedAddress.getVillage());
            patientAddress.setAddress3(sanitisedAddress.getTehsil()); //Tehsil

            return new PatientData(patientRequest, patientRow);
        } catch (Exception e) {
            throw new RuntimeException("Cannot create request from this row: " + ArrayUtils.toString(patientRow), e);
        }
    }

    @Override
    public void failedPatient(PatientData patientData) {
        if (patientData != null)
            csvWriter.writeNext((String[]) patientData.getOriginalData());
    }

    private void addPatientAttribute(String value, PatientRequest patientRequest, String name, LookupValueProvider lookupValueProvider, int valueIndex) {
        if (lookupValueProvider != null) {
            String lookUpValue = lookupValueProvider.getLookUpValue(value, valueIndex);
            if (lookUpValue == null) return;
        }
        if (StringUtils.isEmpty(value)) return;

        PatientAttribute patientAttribute = new PatientAttribute();
        patientAttribute.setAttributeType(allPatientAttributeTypes.getAttributeUUID(name));
        patientAttribute.setName(name);
        String valueToSet = lookupValueProvider == null ? value : lookupValueProvider.getLookUpValue(value, valueIndex);
        valueToSet = name.equals("class") ? valueToSet : sentenceCase(valueToSet);
        patientAttribute.setValue(valueToSet);
        patientRequest.addPatientAttribute(patientAttribute);
    }

    public void done() throws IOException {
        csvReader.close();
        csvWriter.close();
    }
}