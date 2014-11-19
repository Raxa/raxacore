package org.bahmni.module.admin.csv.service;

import org.apache.commons.lang.StringUtils;
import org.bahmni.csv.KeyValue;
import org.bahmni.module.admin.csv.models.PatientRow;
import org.bahmni.module.admin.csv.utils.CSVUtils;
import org.openmrs.*;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CSVPatientService {

    private static final String EMR_PRIMARY_IDENTIFIER_TYPE = "emr.primaryIdentifierType";

    private PatientService patientService;
    private PersonService personService;
    private AdministrationService administrationService;
    private CSVAddressService csvAddressService;

    public CSVPatientService(PatientService patientService, PersonService personService, AdministrationService administrationService, CSVAddressService csvAddressService) {
        this.patientService = patientService;
        this.personService = personService;
        this.administrationService = administrationService;
        this.csvAddressService = csvAddressService;
    }

    public Patient save(PatientRow patientRow) throws ParseException {
        Patient patient = new Patient();
        PersonName personName = new PersonName(patientRow.firstName, patientRow.middleName, patientRow.lastName);
        personName.setPreferred(true);
        patient.addName(personName);

        addPersonAttributes(patient, patientRow);

        if (!StringUtils.isBlank(patientRow.birthdate)) {
            // All csv imports use the same date format
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CSVUtils.ENCOUNTER_DATE_PATTERN);
            simpleDateFormat.setLenient(false);

            patient.setBirthdate(simpleDateFormat.parse(patientRow.birthdate));
        } else if (!StringUtils.isBlank(patientRow.age)) {
            patient.setBirthdateFromAge(Integer.parseInt(patientRow.age), new Date());
        }
        patient.setGender(patientRow.gender);
        patient.addIdentifier(new PatientIdentifier(patientRow.registrationNumber, getPatientIdentifierType(), null));

        List<KeyValue> addressParts = patientRow.addressParts;
        PersonAddress personAddress = csvAddressService.getPersonAddress(addressParts);
        if (personAddress != null) {
            patient.addAddress(personAddress);
        }

        patient.setPersonDateCreated(patientRow.getRegistrationDate());

        return patientService.savePatient(patient);
    }

    private void addPersonAttributes(Patient patient, PatientRow patientRow) {
        for (KeyValue attribute : patientRow.attributes) {
            patient.addAttribute(new PersonAttribute(findAttributeType(attribute.getKey()), attribute.getValue()));
        }
    }

    private PersonAttributeType findAttributeType(String key) {
        for (PersonAttributeType personAttributeType  : personService.getAllPersonAttributeTypes(false)) {
            if(key.equalsIgnoreCase(personAttributeType.getName())) {
                return personAttributeType;
            }
        }

        throw new RuntimeException(String.format("Person Attribute %s not found", key));
    }

    private PatientIdentifierType getPatientIdentifierType() {
        String globalProperty = administrationService.getGlobalProperty(EMR_PRIMARY_IDENTIFIER_TYPE);
        PatientIdentifierType patientIdentifierByUuid = patientService.getPatientIdentifierTypeByUuid(globalProperty);
        return patientIdentifierByUuid;
    }
}
