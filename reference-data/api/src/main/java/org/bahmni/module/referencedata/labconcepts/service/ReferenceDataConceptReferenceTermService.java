package org.bahmni.module.referencedata.labconcepts.service;

public interface ReferenceDataConceptReferenceTermService {
    public org.openmrs.ConceptReferenceTerm getConceptReferenceTerm(String referenceTermCode, String referenceTermSource);
}
