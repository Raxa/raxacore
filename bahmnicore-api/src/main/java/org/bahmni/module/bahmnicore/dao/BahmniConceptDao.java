package org.bahmni.module.bahmnicore.dao;


import org.openmrs.Concept;

import java.util.Collection;
import java.util.List;

public interface BahmniConceptDao {
    Collection<Concept> searchByQuestion(Concept questionConcept, String searchQuery);
}
