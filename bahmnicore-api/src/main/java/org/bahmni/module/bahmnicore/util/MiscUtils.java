package org.bahmni.module.bahmnicore.util;

import org.apache.commons.collections.CollectionUtils;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;

import java.util.ArrayList;
import java.util.List;

public class MiscUtils {
    public static List<Concept> getConceptsForNames(List<String> conceptNames, ConceptService conceptService) {
        //Returning null for the sake of UTs
        if (CollectionUtils.isNotEmpty(conceptNames)) {
            List<Concept> rootConcepts = new ArrayList<>();
            for (String rootConceptName : conceptNames) {
                Concept concept = conceptService.getConceptByName(rootConceptName);
                if (concept != null) {
                    rootConcepts.add(concept);
                }
            }
            return rootConcepts;
        }
        return null;
    }
}
