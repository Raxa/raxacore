package org.bahmni.module.referencedata.labconcepts.model;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;

public class ConceptMetaData {
    private Concept existingConcept;
    private ConceptDatatype conceptDatatype;
    private ConceptClass conceptClass;

    public ConceptMetaData(Concept existingConcept, ConceptDatatype conceptDatatype, ConceptClass conceptClass) {
        this.existingConcept = existingConcept;
        this.conceptDatatype = conceptDatatype;
        this.conceptClass = conceptClass;
    }

    public Concept getExistingConcept() {
        return existingConcept;
    }

    public void setExistingConcept(Concept existingConcept) {
        this.existingConcept = existingConcept;
    }

    public ConceptDatatype getConceptDatatype() {
        return conceptDatatype;
    }

    public void setConceptDatatype(ConceptDatatype conceptDatatype) {
        this.conceptDatatype = conceptDatatype;
    }

    public ConceptClass getConceptClass() {
        return conceptClass;
    }

    public void setConceptClass(ConceptClass conceptClass) {
        this.conceptClass = conceptClass;
    }
}
