package org.bahmni.module.admin.csv.service;

import org.apache.commons.lang.StringUtils;
import org.bahmni.csv.KeyValue;
import org.bahmni.module.admin.csv.models.PatientRow;
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

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d");
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
        PersonName personName = new PersonName(patientRow.getFirstName(), patientRow.getMiddleName(), patientRow.getLastName());
        personName.setPreferred(true);
        patient.addName(personName);

        if (!StringUtils.isBlank(patientRow.getBirthdate())) {
            patient.setBirthdate(simpleDateFormat.parse(patientRow.getBirthdate()));
        } else if (!StringUtils.isBlank(patientRow.getAge())) {
            patient.setBirthdateFromAge(Integer.parseInt(patientRow.getAge()), new Date());
        }
        patient.setGender(patientRow.getGender());
        patient.addIdentifier(new PatientIdentifier(patientRow.getRegistrationNumber(), getPatientIdentifierType(), null));

        List<KeyValue> addressParts = patientRow.getAddressParts();
        PersonAddress personAddress = csvAddressService.getPersonAddress(addressParts);
        if(personAddress != null){
            patient.addAddress(personAddress);
        }

        return patientService.savePatient(patient);
    }

    private PatientIdentifierType getPatientIdentifierType() {
        String globalProperty = administrationService.getGlobalProperty(EMR_PRIMARY_IDENTIFIER_TYPE);
        PatientIdentifierType patientIdentifierByUuid = patientService.getPatientIdentifierTypeByUuid(globalProperty);
        return patientIdentifierByUuid;
    }
}
