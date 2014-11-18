package org.bahmni.module.admin.csv.service;

import org.apache.commons.lang.StringUtils;
import org.bahmni.csv.KeyValue;
import org.bahmni.module.admin.csv.models.PatientRow;
import org.bahmni.module.admin.csv.utils.CSVUtils;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CSVPatientService {

    private static final String EMR_PRIMARY_IDENTIFIER_TYPE = "emr.primaryIdentifierType";

    private PatientService patientService;
    private AdministrationService administrationService;
    private CSVAddressService csvAddressService;

    public CSVPatientService(PatientService patientService, AdministrationService administrationService, CSVAddressService csvAddressService) {
        this.patientService = patientService;
        this.administrationService = administrationService;
        this.csvAddressService = csvAddressService;
    }

    public Patient save(PatientRow patientRow) throws ParseException {
        Patient patient = new Patient();
        PersonName personName = new PersonName(patientRow.firstName, patientRow.middleName, patientRow.lastName);
        personName.setPreferred(true);
        patient.addName(personName);

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

    private PatientIdentifierType getPatientIdentifierType() {
        String globalProperty = administrationService.getGlobalProperty(EMR_PRIMARY_IDENTIFIER_TYPE);
        PatientIdentifierType patientIdentifierByUuid = patientService.getPatientIdentifierTypeByUuid(globalProperty);
        return patientIdentifierByUuid;
    }
}
