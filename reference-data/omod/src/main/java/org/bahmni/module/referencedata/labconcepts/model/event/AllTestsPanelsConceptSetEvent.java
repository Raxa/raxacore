package org.bahmni.module.referencedata.labconcepts.model.event;

import org.bahmni.module.referencedata.labconcepts.contract.LabTest;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;

public class AllTestsPanelsConceptSetEvent extends ConceptOperationEvent {


    public AllTestsPanelsConceptSetEvent(String url, String category, String title) {
        super(url, category, title);
    }

    @Override
    public boolean isResourceConcept(Concept concept) {
        return isTestPanelConcept(concept);
    }

    private boolean isTestPanelConcept(Concept concept) {
        return concept.getName(Context.getLocale()) != null &&
                concept.getName(Context.getLocale()).getName().equals(LabTest.TEST_PARENT_CONCEPT_NAME);
    }

}
