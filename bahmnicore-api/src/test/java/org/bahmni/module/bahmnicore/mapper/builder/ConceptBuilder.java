package org.bahmni.module.bahmnicore.mapper.builder;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;
import org.openmrs.util.LocaleUtility;

public class ConceptBuilder {
    private final Concept concept;

    public ConceptBuilder() {
        concept = new Concept();
    }

    public Concept build() {
        return concept;
    }

    public ConceptBuilder withName(String conceptName) {
        ConceptName name = new ConceptName(conceptName, LocaleUtility.getDefaultLocale());
        name.setConceptNameType(ConceptNameType.FULLY_SPECIFIED);
        concept.setPreferredName(name);
        return this;
    }

    public ConceptBuilder withDataType(String name) {
        withDataType(name, "hl7Abbreviation", null);
        return this;
    }

    public ConceptBuilder withDataType(String name, String hl7Abbreviation) {
        withDataType(name, hl7Abbreviation, null);
        return this;
    }

    public ConceptBuilder withUUID(String uuid) {
        concept.setUuid(uuid);
        return this;
    }

    public ConceptBuilder withClass(String conceptClassName) {
        ConceptClass conceptClass = concept.getConceptClass();
        if (conceptClass == null) {
            conceptClass = new ConceptClass();
        }
        conceptClass.setName(conceptClassName);
        concept.setConceptClass(conceptClass);
        return this;
    }

    public ConceptBuilder withClassUUID(String uuid) {
        ConceptClass conceptClass = concept.getConceptClass();
        if (conceptClass == null) {
            conceptClass = new ConceptClass();
        }
        conceptClass.setUuid(uuid);
        concept.setConceptClass(conceptClass);
        return this;
    }


    public ConceptBuilder withSetMember(Concept concept){
        concept.addSetMember(concept);
        return this;
    }

    public ConceptBuilder withDataTypeNumeric() {
        withDataType("Numeric", ConceptDatatype.NUMERIC, ConceptDatatype.NUMERIC_UUID);
        return this;
    }

    public ConceptBuilder withCodedDataType() {
        withDataType("Coded", ConceptDatatype.CODED, ConceptDatatype.CODED_UUID);
        return this;
    }

    private ConceptBuilder withDataType(String name, String hl7Abbreviation, String uuid) {
        ConceptDatatype conceptDatatype = new ConceptDatatype();
        conceptDatatype.setHl7Abbreviation(hl7Abbreviation);
        conceptDatatype.setName(name);
        conceptDatatype.setUuid(uuid);
        concept.setDatatype(conceptDatatype);
        return this;
    }
}
