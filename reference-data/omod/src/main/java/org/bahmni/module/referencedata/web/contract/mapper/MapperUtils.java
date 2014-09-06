package org.bahmni.module.referencedata.web.contract.mapper;

import org.bahmni.module.referencedata.web.contract.Department;
import org.bahmni.module.referencedata.web.contract.Sample;
import org.openmrs.Concept;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptSet;
import org.openmrs.api.context.Context;

import java.util.List;

import static org.bahmni.module.referencedata.model.event.DepartmentEvent.isDepartmentConcept;
import static org.bahmni.module.referencedata.model.event.SampleEvent.isSampleConcept;

public class MapperUtils {
    public static String getDescription(Concept concept) {
        ConceptDescription description = concept.getDescription();
        if (description != null) {
            return description.getDescription();
        }
        return null;
    }

    public static Department getDepartment(Concept concept) {
        List<ConceptSet> parentConcepts = Context.getConceptService().getSetsContainingConcept(concept);
        for (ConceptSet parentConcept : parentConcepts) {
            if (isDepartmentConcept(parentConcept.getConceptSet())) {
                DepartmentMapper departmentMapper = new DepartmentMapper();
                return departmentMapper.map(parentConcept.getConceptSet());
            }
        }
        return null;
    }


    public static Sample getSample(Concept concept) {
        List<ConceptSet> parentConcepts = Context.getConceptService().getSetsContainingConcept(concept);
        for (ConceptSet parentConcept : parentConcepts) {
            if (isSampleConcept(parentConcept.getConceptSet())) {
                SampleMapper sampleMapper = new SampleMapper();
                return sampleMapper.map(parentConcept.getConceptSet());
            }
        }
        return null;
    }
}
