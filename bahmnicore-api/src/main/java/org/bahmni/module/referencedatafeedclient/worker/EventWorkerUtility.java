package org.bahmni.module.referencedatafeedclient.worker;

import org.openmrs.Concept;
import org.openmrs.ConceptSet;
import org.openmrs.api.ConceptService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
public class EventWorkerUtility {
    public void removeChildFromExistingParent(Concept conceptToRemove, Concept rootConcept, String conceptIdToRemove, String parentId, ConceptService conceptService) {
        Concept parentConcept = findChildOfRootWithThisId(rootConcept, conceptIdToRemove);
        if (parentConcept != null && !parentId.equals(parentConcept.getUuid())) {
            removeChildFromOldParent(parentConcept, conceptToRemove, conceptService);
        }
    }

    private void removeChildFromOldParent(Concept parentConcept, Concept childConcept, ConceptService conceptService) {
        Collection<ConceptSet> conceptSets = parentConcept.getConceptSets();
        ConceptSet matchingOldChildConceptSet = getMatchingConceptSet(conceptSets, childConcept);
        if (matchingOldChildConceptSet != null) {
            conceptSets.remove(matchingOldChildConceptSet);
            parentConcept.setConceptSets(conceptSets);
            conceptService.saveConcept(parentConcept);
        }
    }

    private  ConceptSet getMatchingConceptSet(Collection<ConceptSet> conceptSets, Concept childConcept) {
        for (ConceptSet conceptSet : conceptSets) {
            if (conceptSet.getConcept().equals(childConcept)) {
                return conceptSet;
            }
        }
        return null;
    }

    private Concept findChildOfRootWithThisId(Concept rootConcept, String idToMatch) {
        for (Concept childOfRootConcept : rootConcept.getSetMembers()) {
            for (Concept conceptToMatch : childOfRootConcept.getSetMembers()) {
                if (conceptToMatch.getUuid().equals(idToMatch)) {
                    return childOfRootConcept;
                }
            }
        }
        return null;
    }

    public Set<String> getExistingChildUuids(String conceptIdToSearch, ConceptService conceptService) {
        Concept existingParentConcept = conceptService.getConceptByUuid(conceptIdToSearch);
        if (existingParentConcept == null)
            return new HashSet<>();

        Set<String> existingChildUuids = new HashSet<>();
        for (Concept childConcept : existingParentConcept.getSetMembers()) {
            existingChildUuids.add(childConcept.getUuid());
        }
        return existingChildUuids;
    }
}
