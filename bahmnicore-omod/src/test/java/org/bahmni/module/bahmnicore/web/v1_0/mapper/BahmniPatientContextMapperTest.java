package org.bahmni.module.bahmnicore.web.v1_0.mapper;

import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.BahmniPatientProgram;
import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.PatientProgramAttribute;
import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.ProgramAttributeType;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.module.bahmniemrapi.patient.PatientContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BahmniPatientContextMapperTest {

    private BahmniPatientContextMapper bahmniPatientContextMapper = new BahmniPatientContextMapper();

    @Test
    public void shouldMapPatientInformationToPatientContext() {
        Patient patient = new Patient();
        patient.setBirthdate(new Date());
        patient.setGender("Male");
        patient.setNames(getPersonNames("GivenName", "MiddleName", "FamilyName"));
        patient.setIdentifiers(getPatientIdentifiers("GAN20000"));
        Set<PersonAttribute> attributes = new LinkedHashSet<>();
        attributes.add(getPersonAttribute("Caste", "Caste", "Caste Value", "java.lang.String"));
        attributes.add(getPersonAttribute("Education", "Education", "Education Value", "java.lang.String"));
        patient.setAttributes(attributes);

        PatientContext patientContext = bahmniPatientContextMapper.map(patient, new BahmniPatientProgram(), Collections.singletonList("Caste"), Collections.singletonList("IRDB Number"));

        assertNotNull(patientContext);
        assertEquals(patient.getBirthdate(), patientContext.getBirthdate());
        assertEquals(patient.getIdentifiers().iterator().next().getIdentifier(), patientContext.getIdentifier());
        assertEquals(patient.getGender(), patientContext.getGender());
        assertEquals(patient.getFamilyName(), patientContext.getFamilyName());
        assertEquals(patient.getMiddleName(), patientContext.getMiddleName());
        assertEquals(patient.getGivenName(), patientContext.getGivenName());
        assertEquals(1, patientContext.getPersonAttributes().size());
        assertEquals("Caste Value", patientContext.getPersonAttributes().get("Caste").get("value"));
        assertEquals("Caste", patientContext.getPersonAttributes().get("Caste").get("description"));
    }

    @Test
    public void shouldNotReturnPersonAttributesIfTheConfiguredAttributesAreNotExists() {
        Patient patient = new Patient();
        Set<PersonName> names = getPersonNames("GivenName", "MiddleName", "FamilyName");
        patient.setNames(names);
        LinkedHashSet<PatientIdentifier> identifiers = getPatientIdentifiers("GAN20000");
        patient.setIdentifiers(identifiers);

        PatientContext patientContext = bahmniPatientContextMapper.map(patient, new BahmniPatientProgram(), Collections.singletonList("Caste"), Arrays.asList("IRDB Number"));

        assertNotNull(patientContext);
        assertEquals(0, patientContext.getPersonAttributes().size());
    }

    @Test
    public void shouldMapProgramAttributesToPatientContext() {
        Patient patient = new Patient();
        Set<PersonName> names = getPersonNames("GivenName", "MiddleName", "FamilyName");
        patient.setNames(names);
        LinkedHashSet<PatientIdentifier> identifiers = getPatientIdentifiers("GAN20000");
        patient.setIdentifiers(identifiers);

        BahmniPatientProgram patientProgram = new BahmniPatientProgram();
        HashSet<PatientProgramAttribute> patientProgramAttributes = new HashSet<>();
        patientProgramAttributes.add(getPatientProgramAttribute("IRDB Number", "IRDB Number Description", "1234", "String"));
        patientProgramAttributes.add(getPatientProgramAttribute("TSRT Number", "TSRT Number", "9876", "String"));
        patientProgram.setAttributes(patientProgramAttributes);
        PatientContext patientContext = bahmniPatientContextMapper.map(patient, patientProgram, Collections.singletonList("Caste"), Collections.singletonList("IRDB Number"));

        assertNotNull(patientContext);
        assertEquals(1, patientContext.getProgramAttributes().size());
        assertEquals("1234", patientContext.getProgramAttributes().get("IRDB Number").get("value"));
    }

    @Test
    public void shouldNotReturnProgramAttributesIfTheConfiguredAttributesAreNotExists() {
        Patient patient = new Patient();
        Set<PersonName> names = getPersonNames("GivenName", "MiddleName", "FamilyName");
        patient.setNames(names);
        LinkedHashSet<PatientIdentifier> identifiers = getPatientIdentifiers("GAN20000");
        patient.setIdentifiers(identifiers);

        PatientContext patientContext = bahmniPatientContextMapper.map(patient, new BahmniPatientProgram(), Collections.singletonList("Caste"), Collections.singletonList("IRDB Number"));

        assertNotNull(patientContext);
        assertEquals(0, patientContext.getProgramAttributes().size());
    }

    @Test
    public void shouldNotReturnProgramAttributesIfTheProgramDoesntExists() {
        Patient patient = new Patient();
        Set<PersonName> names = getPersonNames("GivenName", "MiddleName", "FamilyName");
        patient.setNames(names);
        LinkedHashSet<PatientIdentifier> identifiers = getPatientIdentifiers("GAN20000");
        patient.setIdentifiers(identifiers);

        PatientContext patientContext = bahmniPatientContextMapper.map(patient, null, Collections.singletonList("Caste"), Collections.singletonList("IRDB Number"));

        assertNotNull(patientContext);
        assertEquals(0, patientContext.getProgramAttributes().size());
    }

    @Test
    public void shouldNotReturnProgramAttributesIfNoConfiguredAttributesAreSent() {
        Patient patient = new Patient();
        Set<PersonName> names = getPersonNames("GivenName", "MiddleName", "FamilyName");
        patient.setNames(names);
        LinkedHashSet<PatientIdentifier> identifiers = getPatientIdentifiers("GAN20000");
        patient.setIdentifiers(identifiers);

        PatientContext patientContext = bahmniPatientContextMapper.map(patient, new BahmniPatientProgram(), Collections.singletonList("Caste"), null);

        assertNotNull(patientContext);
        assertEquals(0, patientContext.getProgramAttributes().size());
    }

    @Test
    public void shouldNotReturnPersonAttributesIfNoConfiguredAttributesAreSent() {
        Patient patient = new Patient();
        Set<PersonName> names = getPersonNames("GivenName", "MiddleName", "FamilyName");
        patient.setNames(names);
        LinkedHashSet<PatientIdentifier> identifiers = getPatientIdentifiers("GAN20000");
        patient.setIdentifiers(identifiers);

        PatientContext patientContext = bahmniPatientContextMapper.map(patient, new BahmniPatientProgram(), null, Collections.singletonList("IRDTB Number"));

        assertNotNull(patientContext);
        assertEquals(0, patientContext.getProgramAttributes().size());
    }

    private Set<PersonName> getPersonNames(String givenName, String middleName, String familyName) {
        Set<PersonName> names = new LinkedHashSet<>();
        names.add(new PersonName(givenName, middleName, familyName));
        return names;
    }

    private LinkedHashSet<PatientIdentifier> getPatientIdentifiers(String identifier) {
        LinkedHashSet<PatientIdentifier> identifiers = new LinkedHashSet<>();
        identifiers.add(new PatientIdentifier(identifier, null, null));
        return identifiers;
    }

    private PatientProgramAttribute getPatientProgramAttribute(String typeName, String typeDescription, String value, String dataTypeClassName) {
        PatientProgramAttribute patientProgramAttribute = new PatientProgramAttribute();
        ProgramAttributeType attributeType = new ProgramAttributeType();
        attributeType.setName(typeName);
        attributeType.setDescription(typeDescription);
        attributeType.setDatatypeClassname(dataTypeClassName);
        patientProgramAttribute.setAttributeType(attributeType);
        patientProgramAttribute.setValueReferenceInternal(value);
        return patientProgramAttribute;
    }

    private PersonAttribute getPersonAttribute(String typeName, String typeDescription, String value, String format) {
        PersonAttributeType attributeType = new PersonAttributeType();
        attributeType.setName(typeName);
        attributeType.setDescription(typeDescription);
        attributeType.setFormat(format);
        return new PersonAttribute(attributeType, value);
    }
}