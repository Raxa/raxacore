package org.bahmni.module.bahmnicore.contract.patient.mapper;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.bahmnicore.contract.patient.response.PatientResponse;
import org.bahmni.module.bahmnicore.service.BahmniProgramWorkflowService;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PatientResponseMapper {

    public PatientResponse map(Patient patient, String[] searchResultFields, String[] addressResultFields) {
        List<String> patientSearchResultFields = searchResultFields != null ? Arrays.asList(searchResultFields) : new ArrayList<>();
        List<String> addressSearchResultFields = addressResultFields != null ? Arrays.asList(addressResultFields) : new ArrayList<>();

        PatientResponse patientResponse = new PatientResponse();
        patientResponse.setUuid(patient.getUuid());
        patientResponse.setPersonId(patient.getPatientId());
        patientResponse.setBirthDate(patient.getBirthdate());
        patientResponse.setDeathDate(patient.getDeathDate());
        patientResponse.setDateCreated(patient.getDateCreated());
        patientResponse.setGivenName(patient.getGivenName());
        patientResponse.setMiddleName(patient.getMiddleName());
        patientResponse.setFamilyName(patient.getFamilyName());
        patientResponse.setGender(patient.getGender());
        PatientIdentifier primaryIdentifier = patient.getPatientIdentifier();
        patientResponse.setIdentifier(primaryIdentifier.getIdentifier());

        // extra identifiers
        String extraIdentifiers = patient.getActiveIdentifiers().stream()
                .map(patientIdentifier -> {
                    if (patientIdentifier != primaryIdentifier) {
                        String identifier = patientIdentifier.getIdentifier();
                        return identifier == null ? ""
                                : formKeyPair(patientIdentifier.getIdentifierType().getName(), identifier);

                    }
                    return "";
                })
                .collect(Collectors.joining(","));
        patientResponse.setExtraIdentifiers(formJsonString(extraIdentifiers));


        //person attribute
        String queriedPersonAttributes = patientSearchResultFields.stream()
                .map(attributeName -> {
                    PersonAttribute attribute = patient.getAttribute(attributeName);
                    return attribute == null ? null : formKeyPair(attributeName, attribute.getValue());
                })
                .collect(Collectors.joining(","));
        patientResponse.setCustomAttribute(formJsonString(queriedPersonAttributes));

        //address
        String queriedAddressFields = addressSearchResultFields.stream()
                .map(addressField -> {
                    String address = getPersonAddressFieldValue(addressField, patient.getPersonAddress());
                    return address == null ? null : formKeyPair(addressField, address);
                })
                .collect(Collectors.joining(","));
        patientResponse.setAddressFieldValue(formJsonString(queriedAddressFields));
        return patientResponse;
    }

    private String formJsonString(String keyPairs) {

        return "{" + keyPairs + "}";
    }

    private String formKeyPair(String Key, String value) {
        return "\"" + Key + "\" : \"" + value + "\"";
    }

    private String getPersonAddressFieldValue(String addressField, PersonAddress personAddress) {
        String address = "";
        try {
            String[] split = addressField.split("_");
            String propertyName = split.length > 1 ? split[0] + StringUtils.capitalize(split[1]) : addressField;
            address = (String) PropertyUtils.getProperty(personAddress, propertyName);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new APIException("cannot get value for address field" + addressField, e);
        }
        return address;
    }

}
