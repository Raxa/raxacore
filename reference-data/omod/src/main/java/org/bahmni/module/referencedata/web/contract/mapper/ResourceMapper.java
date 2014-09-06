package org.bahmni.module.referencedata.web.contract.mapper;

import org.bahmni.module.referencedata.web.contract.Department;
import org.bahmni.module.referencedata.web.contract.Resource;
import org.openmrs.Concept;
import org.openmrs.ConceptSet;
import org.openmrs.api.context.Context;

import java.util.List;

public abstract class ResourceMapper {
    String parentConceptName;

    protected ResourceMapper(String parentConceptName) {
        this.parentConceptName = parentConceptName;
    }

    public abstract <T extends Resource> T map(Concept concept);


    <T extends Resource> T mapResource(Resource resource, Concept concept) {
        resource.setName(concept.getName(Context.getLocale()).getName());
        resource.setIsActive(!concept.isRetired());
        resource.setId(concept.getUuid());
        resource.setDateCreated(concept.getDateCreated());
        resource.setLastUpdated(concept.getDateChanged());
        resource.setSortOrder(getSortWeight(concept));
        return (T) resource;
    }

    double getSortWeight(Concept concept) {
        List<ConceptSet> conceptSets = Context.getConceptService().getSetsContainingConcept(concept);
        for (ConceptSet conceptSet : conceptSets) {
            if (conceptSet.getConceptSet().getName(Context.getLocale()).getName().equals(parentConceptName)) {
                return conceptSet.getSortWeight() != null ? conceptSet.getSortWeight() : Double.MAX_VALUE;
            }
        }
        return Double.MAX_VALUE;
    }

}
