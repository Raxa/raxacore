package org.bahmni.jss.registration;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bahmni.address.sanitiser.SanitizerPersonAddress;
import org.bahmni.datamigration.*;
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
    private AddressService addressService;

    public AllRegistrations(String csvLocation, String fileName, AllPatientAttributeTypes allPatientAttributeTypes, Map<String,
            AllLookupValues> lookupValuesMap, AddressService addressService) throws IOException {
        File file = new File(csvLocation, fileName);
        FileReader fileReader = new FileReader(file);

        File errorFile = new File(csvLocation, fileName + ".err.csv");
        FileWriter fileWriter = new FileWriter(errorFile);
        init(allPatientAttributeTypes, lookupValuesMap, fileReader, fileWriter, addressService);
    }

    public AllRegistrations(AllPatientAttributeTypes allPatientAttributeTypes, Map<String, AllLookupValues> lookupValuesMap,
                            Reader reader, Writer writer, AddressService addressService) throws IOException {
        init(allPatientAttributeTypes, lookupValuesMap, reader, writer, addressService);
    }

    private void init(AllPatientAttributeTypes allPatientAttributeTypes, Map<String, AllLookupValues> lookupValuesMap, Reader reader, Writer writer,
                      AddressService addressService) throws IOException {
        this.lookupValuesMap = lookupValuesMap;
        this.addressService = addressService;
        this.csvReader = new CSVReader(reader, ',', '"', '\0');
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
            CenterId centerID = new CenterId(registrationNumber.getCenterCode());
            patientRequest.setIdentifier(centerID.getName() + registrationNumber.getId());
            patientRequest.setCenterID(centerID);

            Name name = RegistrationFields.name(patientRow[2], patientRow[3]);
            patientRequest.setName(sentenceCase(name.getGivenName()), sentenceCase(name.getFamilyName()));

            addPatientAttribute(patientRow[4], patientRequest, "primaryRelative", null, 0);
            patientRequest.setDateOfRegistration(RegistrationFields.getDate(patientRow[1]));

            patientRequest.setGender(patientRow[5]);
            String birthdate = RegistrationFields.getDate(patientRow[6]);
            patientRequest.setBirthdate(birthdate == null ? RegistrationFields.UnknownDateOfBirthAsString : birthdate);
            patientRequest.setAge(RegistrationFields.getAge(patientRow[7]));

            PatientAddress patientAddress = new PatientAddress();
            patientRequest.addPatientAddress(patientAddress);

            patientRequest.setBalance(patientRow[17]);

            addPatientAttribute(patientRow[20], patientRequest, "caste", lookupValuesMap.get("Castes"), 0);
            addPatientAttribute(patientRow[32], patientRequest, "class", lookupValuesMap.get("Classes"), 0);

            //Address information
            String gramPanchayat = patientRow[34];
            patientAddress.setAddress2(sentenceCase(gramPanchayat));

            FullyQualifiedTehsil fullyQualifiedTehsil = new FullyQualifiedTehsil();
            String stateId = lookupValuesMap.get("Districts").getLookUpValue(patientRow[26], 0);
            if (stateId != null) {
                String state = lookupValuesMap.get("States").getLookUpValue(stateId);
                fullyQualifiedTehsil.setState(sentenceCase(state));
            }

            String district = lookupValuesMap.get("Districts").getLookUpValue(patientRow[26], 2);
            fullyQualifiedTehsil.setDistrict(sentenceCase(district));

            String village = patientRow[10];
            patientAddress.setCityVillage(sentenceCase(village));

            String tehsil = patientRow[35];
            fullyQualifiedTehsil.setTehsil(sentenceCase(tehsil));

            FullyQualifiedTehsil correctedFullyQualifiedTehsil = addressService.getTehsilFor(fullyQualifiedTehsil);
            setPatientAddressFrom(correctedFullyQualifiedTehsil, patientAddress);
            return new PatientData(patientRequest, patientRow);
        } catch (Exception e) {
            throw new RuntimeException("Cannot create request from this row: " + ArrayUtils.toString(patientRow), e);
        }
    }

    private void setPatientAddressFrom(FullyQualifiedTehsil fullyQualifiedTehsil, PatientAddress patientAddress) {
        patientAddress.setStateProvince(fullyQualifiedTehsil.getState());
        patientAddress.setCountyDistrict(fullyQualifiedTehsil.getDistrict());
        patientAddress.setAddress3(fullyQualifiedTehsil.getTehsil());
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