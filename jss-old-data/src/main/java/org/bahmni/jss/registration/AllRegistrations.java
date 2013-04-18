package org.bahmni.jss.registration;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
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
    private AllPatientAttributeTypes allPatientAttributeTypes;
    private Map<String, AllLookupValues> lookupValuesMap;

    public AllRegistrations(String csvLocation, String fileName, AllPatientAttributeTypes allPatientAttributeTypes, Map<String,
            AllLookupValues> lookupValuesMap) throws IOException {
        File file = new File(csvLocation, fileName);
        FileReader fileReader = new FileReader(file);

        File errorFile = new File(csvLocation, fileName + ".err.csv");
        FileWriter fileWriter = new FileWriter(errorFile);
        init(allPatientAttributeTypes, lookupValuesMap, fileReader, fileWriter);
    }

    public AllRegistrations(AllPatientAttributeTypes allPatientAttributeTypes, Map<String, AllLookupValues> lookupValuesMap,
                            Reader reader, Writer writer) throws IOException {
        init(allPatientAttributeTypes, lookupValuesMap, reader, writer);
    }

    private void init(AllPatientAttributeTypes allPatientAttributeTypes, Map<String, AllLookupValues> lookupValuesMap, Reader reader, Writer writer) throws IOException {
        this.lookupValuesMap = lookupValuesMap;
        this.csvReader = new CSVReader(reader, ',');
        this.csvWriter = new CSVWriter(writer, ',');
        String[] headerRow = this.csvReader.readNext();//skip row
        this.csvWriter.writeNext(headerRow);
        this.allPatientAttributeTypes = allPatientAttributeTypes;
    }

    public PatientData nextPatient() {
        String[] patientRow = null;
        try {
            patientRow = csvReader.readNext();
            if (patientRow == null) return null;

            PatientRequest patientRequest = new PatientRequest();
            RegistrationNumber registrationNumber = RegistrationFields.parseRegistrationNumber(patientRow[0]);
            patientRequest.setPatientIdentifier(registrationNumber.getCenterCode() + registrationNumber.getId());
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

            patientAddress.setCityVillage(sentenceCase(patientRow[10]));

            patientRequest.setBalance(patientRow[17]);

            addPatientAttribute(patientRow[20], patientRequest, "caste", lookupValuesMap.get("Castes"), 0);

            String tahsil = lookupValuesMap.get("Tahsils").getLookUpValue(patientRow[25]);
            patientAddress.setAddress3(sentenceCase(tahsil));

            String district = lookupValuesMap.get("Districts").getLookUpValue(patientRow[26], 2);
            patientAddress.setCountyDistrict(sentenceCase(district));
            String state = lookupValuesMap.get("States").getLookUpValue(patientRow[26]);
            patientAddress.setStateProvince(sentenceCase(state));

            addPatientAttribute(patientRow[32], patientRequest, "class", lookupValuesMap.get("Classes"), 0);
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
        patientAttribute.setValue(sentenceCase(valueToSet));
        patientRequest.addPatientAttribute(patientAttribute);
    }

    public void done() throws IOException {
        csvReader.close();
        csvWriter.close();
    }
}