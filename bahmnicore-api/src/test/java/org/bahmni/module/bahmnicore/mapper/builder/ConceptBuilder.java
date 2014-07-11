package org.bahmni.module.bahmnicore.mapper.builder;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;
import org.openmrs.util.LocaleUtility;

import java.util.ArrayList;
import java.util.List;

public class ConceptBuilder {
    private final Concept concept;

    public ConceptBuilder() {
        concept = new Concept();
    }

    public Concept build() {
        return concept;
    }

    public ConceptBuilder withName(String conceptName) {
        List<ConceptName> conceptNames = new ArrayList<>();
        conceptNames.add(new ConceptName(conceptName, LocaleUtility.getDefaultLocale()));
        concept.setNames(conceptNames);
        return this;
    }

    public ConceptBuilder withDataType(String dataType) {
        ConceptDatatype conceptDatatype = new ConceptDatatype();
        conceptDatatype.setHl7Abbreviation(dataType);
        conceptDatatype.setName(dataType);
        concept.setDatatype(conceptDatatype);
        return this;
    }

    public ConceptBuilder withUUID(String uuid) {
        concept.setUuid(uuid);
        return this;
    }

    public ConceptBuilder withClass(String conceptClassName) {
        ConceptClass conceptClass = new ConceptClass();
        conceptClass.setName(conceptClassName);
        concept.setConceptClass(conceptClass);
        return this;
    }
}
