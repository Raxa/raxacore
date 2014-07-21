package org.bahmni.module.bahmnicore.dao;

import org.openmrs.Concept;

import java.util.List;

public interface ConceptDao {
    List<Concept> conceptFor(String[] conceptNames);
}
