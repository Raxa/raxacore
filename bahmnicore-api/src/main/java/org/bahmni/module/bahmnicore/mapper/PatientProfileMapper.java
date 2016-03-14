package org.bahmni.module.bahmnicore.mapper;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.encounter.DateMapper;
import org.openmrs.module.emrapi.patient.PatientProfile;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PersonResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.RelationShipTypeResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.RelationshipResource1_8;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("patientProfileMapper")
public class PatientProfileMapper {

    public PatientProfile mapForCreatePatient(SimpleObject propertiesToCreate) {
        final Object patientProperty = propertiesToCreate.get("patient");
        if (propertiesToCreate.get("patient") == null || !(propertiesToCreate.get("patient") instanceof Map)) {
            throw new ConversionException("The patient property is missing");
        }

        PatientProfile delegate = new PatientProfile();
        PatientResource1_8 patientResource1_9 = (PatientResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(Patient.class);
        delegate.setPatient(patientResource1_9.getPatient(new SimpleObject() {{
            putAll((Map<String, Object>) patientProperty);
        }}));
        propertiesToCreate.removeProperty("patient");
        delegate.setRelationships(getRelationships(propertiesToCreate, delegate.getPatient()));
        propertiesToCreate.removeProperty("relationships");
        return delegate;
    }

    public PatientProfile mapForUpdatePatient(String uuid, SimpleObject propertiesToUpdate) {
        if (propertiesToUpdate.get("patient") == null || !(propertiesToUpdate.get("patient") instanceof Map)) {
            throw new ConversionException("The patient property is missing");
        }

        PatientProfile delegate = new PatientProfile();

        PatientResource1_8 patientResource1_9 = (PatientResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(Patient.class);
        Patient patient = patientResource1_9.getPatientForUpdate(uuid, (Map<String, Object>) propertiesToUpdate.get("patient"));
        delegate.setPatient(patient);

        propertiesToUpdate.removeProperty("patient");
        delegate.setRelationships(getRelationships(propertiesToUpdate, delegate.getPatient()));

        return delegate;
    }

    private List<Relationship> getRelationships(SimpleObject propertiesToCreate, Person currentPerson) {
        Object relationshipsList = propertiesToCreate.get("relationships");
        List<Relationship> relationships = new ArrayList<Relationship>();
        List<Map<String, Object>> relationshipProperties = (List<Map<String, Object>>) relationshipsList;
        for (final Map<String, Object> relationshipProperty : relationshipProperties) {
            String uuid = getValueFromMap(relationshipProperty, "uuid");
            Relationship relationship;
            if (StringUtils.isBlank(uuid)) {
                relationship = createRelationship(currentPerson, relationshipProperty);
            } else {
                relationship = updateRelationship(relationshipProperty);
            }
            relationships.add(relationship);
        }
        return relationships;
    }

    private String getValueFromMap(Map<String, Object> jsonMap, String key) {
        Object value = jsonMap.get(key);
        return ObjectUtils.toString(value);
    }

    private Relationship createRelationship(Person currentPerson, Map<String, Object> relationshipJson) {
        Relationship relationship = new Relationship(currentPerson,
                getPerson((Map<String, Object>) relationshipJson.get("personB")),
                getRelationshipType((Map<String, Object>) relationshipJson.get("relationshipType")));
        relationship.setEndDate(new DateMapper().convertUTCToDate(getValueFromMap(relationshipJson, "endDate")));

        return relationship;
    }

    private Person getPerson(Map<String, Object> personJson) {
        String personUuid = getValueFromMap(personJson, "uuid");

        if (StringUtils.isBlank(personUuid)) {
            throw new ConversionException("The personUuid is not present.");
        }

        return getPersonFromUuid(personUuid);
    }

    private Person getPersonFromUuid(String personUuid) {
        PersonResource1_8 personResource = (PersonResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(Person.class);
        Person person = personResource.getByUniqueId(personUuid);

        if (person == null) {
            throw new ConversionException("The person does not exist.");
        }
        return person;
    }

    private RelationshipType getRelationshipType(Map<String, Object> relationshipTypeJson) {

        String relationshipTypeUuid = getValueFromMap(relationshipTypeJson, "uuid");

        if (StringUtils.isBlank(relationshipTypeUuid)) {
            throw new ConversionException("The relationshipTypeUuid is not present");
        }

        RelationShipTypeResource1_8 relationshipResource = (RelationShipTypeResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(RelationshipType.class);
        RelationshipType relationshipType = relationshipResource.getByUniqueId(relationshipTypeUuid);

        if (relationshipType == null) {
            throw new ConversionException("The relationship type does not exist.");
        }

        return relationshipType;
    }

    private Relationship updateRelationship(final Map<String, Object> relationshipJson) {
        String relationshipUuid = getValueFromMap(relationshipJson, "uuid");

        if (StringUtils.isBlank(relationshipUuid)) {
            throw new ConversionException("The relationshipUuid is not present");
        }

        RelationshipResource1_8 relationshipResource = (RelationshipResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(Relationship.class);
        Relationship relationship = relationshipResource.getByUniqueId(relationshipUuid);

        if (null == relationship) {
            throw new ConversionException("Invalid relationship for relationshipUuid " + relationshipUuid);
        }

        relationshipResource.setConvertedProperties(relationship, relationshipJson, relationshipResource.getUpdatableProperties(), true);

        RelationshipType updatedRelationshipType = getRelationshipType((Map<String, Object>) relationshipJson.get("relationshipType"));
        relationship.setRelationshipType(updatedRelationshipType);

        return relationship;
    }
}
