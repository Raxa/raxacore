package org.bahmni.module.bahmnicore.contract.patient.mapper;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.bahmnicore.contract.patient.response.PatientResponse;
import org.openmrs.*;
import org.openmrs.api.APIException;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.encountertransaction.command.impl.BahmniVisitAttributeService;
import org.openmrs.module.bahmniemrapi.visitlocation.BahmniVisitLocationServiceImpl;
import org.openmrs.util.DateUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class PatientResponseMapper {
    private PatientResponse patientResponse;

    public PatientResponseMapper() {
    }

    public PatientResponse map(Patient patient, String loginLocationUuid, String[] searchResultFields, String[] addressResultFields, Object programAttributeValue, Boolean filterPatientsByLocation) {
        List<String> patientSearchResultFields = searchResultFields != null ? Arrays.asList(searchResultFields) : new ArrayList<>();
        List<String> addressSearchResultFields = addressResultFields != null ? Arrays.asList(addressResultFields) : new ArrayList<>();

        patientResponse = new PatientResponse();
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

        mapExtraIdentifiers(patient, primaryIdentifier);
        mapPersonAttributes(patient, patientSearchResultFields);
        mapPersonAddress(patient, programAttributeValue, addressSearchResultFields);

        BahmniVisitLocationServiceImpl bahmniVisitLocationService = new BahmniVisitLocationServiceImpl(Context.getLocationService());
        Integer visitLocationId = bahmniVisitLocationService.getVisitLocation(loginLocationUuid).getLocationId();
        VisitService visitService = Context.getVisitService();
        List<Visit> activeVisitsByPatient = visitService.getActiveVisitsByPatient(patient);
        if(activeVisitsByPatient.isEmpty() && filterPatientsByLocation) {
            return null;
        }
        mapVisitSummary(visitLocationId, activeVisitsByPatient);

        return patientResponse;
    }

    private void mapExtraIdentifiers(Patient patient, PatientIdentifier primaryIdentifier) {
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
    }

    private void mapPersonAttributes(Patient patient, List<String> patientSearchResultFields) {
        String queriedPersonAttributes = patientSearchResultFields.stream()
                .map(attributeName -> {
                    PersonAttribute attribute = patient.getAttribute(attributeName);
                    return attribute == null ? null : formKeyPair(attributeName, attribute.getValue());
                })
                .collect(Collectors.joining(","));
        patientResponse.setCustomAttribute(formJsonString(queriedPersonAttributes));
    }

    private void mapPersonAddress(Patient patient, Object programAttributeValue, List<String> addressSearchResultFields) {
        String queriedAddressFields = addressSearchResultFields.stream()
                .map(addressField -> {
                    String address = getPersonAddressFieldValue(addressField, patient.getPersonAddress());
                    return address == null ? null : formKeyPair(addressField, address);
                })
                .collect(Collectors.joining(","));
        patientResponse.setAddressFieldValue(formJsonString(queriedAddressFields));
        patientResponse.setPatientProgramAttributeValue(programAttributeValue);
    }

    private void mapVisitSummary(Integer visitLocationId, List<Visit> activeVisitsByPatient) {
        patientResponse.setHasBeenAdmitted(false);
        for(Visit visit:activeVisitsByPatient){
            if(visit.getLocation().getLocationId().equals(visitLocationId)){
                patientResponse.setActiveVisitUuid(visit.getUuid());
                Set<VisitAttribute> visitAttributeSet=visit.getAttributes();
                for(VisitAttribute visitAttribute:visitAttributeSet){
                    if(visitAttribute.getAttributeType().getName().equalsIgnoreCase(BahmniVisitAttributeService.ADMISSION_STATUS_ATTRIBUTE_TYPE)
                            && visitAttribute.getValueReference().equalsIgnoreCase("Admitted")){
                        patientResponse.setHasBeenAdmitted(true);
                        return;
                    }
                }
            }
        }
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
