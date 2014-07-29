package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.contract.observation.ConceptData;
import org.bahmni.module.bahmnicore.contract.observation.ConceptDefinition;
import org.bahmni.module.bahmnicore.dao.ConceptDao;
import org.bahmni.module.bahmnicore.service.ConceptService;
import org.openmrs.Concept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class BahmniConceptServiceImpl implements ConceptService {
    private ConceptDao bahmniConceptDao;

    @Autowired
    public BahmniConceptServiceImpl(ConceptDao bahmniConceptDao) {
        this.bahmniConceptDao = bahmniConceptDao;
    }

    @Override
    public ConceptDefinition conceptsFor(List<String> rootConceptNames) {
        List<Concept> rootConcepts = bahmniConceptDao.conceptFor(rootConceptNames);
        ConceptDefinition conceptDefinition = new ConceptDefinition();
        flatten(rootConcepts, conceptDefinition, null, rootConceptNames);
        return conceptDefinition;
    }

    private ConceptDefinition flatten(Collection<Concept> concepts, ConceptDefinition conceptDefinition, Concept rootConcept, List<String> rootConceptNames) {
        for (Concept aConcept : concepts) {
            rootConcept = getRootConcept(aConcept, rootConcept, rootConceptNames);

            Collection<Concept> conceptMembers = aConcept.getSetMembers();
            if (conceptMembers == null || conceptMembers.isEmpty()) {
                conceptDefinition.add(createConceptForLeaf(aConcept, rootConcept));
            } else if (isConceptDetails(aConcept)) {
                conceptDefinition.add(createConceptForGroup(aConcept, rootConcept));
            } else {
                flatten(conceptMembers, conceptDefinition, rootConcept, rootConceptNames);
            }
        }

        return conceptDefinition;
    }

    private ConceptData createConceptForGroup(Concept conceptGroup, Concept rootConcept) {
        ConceptData conceptData = null;
        for (Concept aConcept : conceptGroup.getSetMembers()) {
            if (!isDuration(aConcept) && !isAbnormal(aConcept)) {
                conceptData = createConceptForLeaf(aConcept, rootConcept);
            }
        }
        return conceptData;
    }


    private boolean isConceptDetails(Concept aConcept) {
        return aConcept.getConceptClass().getName().equals(CONCEPT_DETAILS_CONCEPT_CLASS);
    }

    private boolean isAbnormal(Concept aConcept) {
        return aConcept.getConceptClass().getName().equals(BahmniConceptServiceImpl.ABNORMAL_CONCEPT_CLASS);
    }

    private boolean isDuration(Concept aConcept) {
        return aConcept.getConceptClass().getName().equals(BahmniConceptServiceImpl.DURATION_CONCEPT_CLASS);
    }

    private ConceptData createConceptForLeaf(Concept aConcept, Concept rootConcept) {
        ConceptData conceptData = new ConceptData(aConcept);
        conceptData.setRootConcept(rootConcept.getName().getName());
        return conceptData;
    }

    private Concept getRootConcept(Concept aConcept, Concept rootConcept, List<String> rootConceptNames) {
        for (String rootConceptName : rootConceptNames) {
            if (rootConceptName.equalsIgnoreCase(aConcept.getName().getName()))
                return aConcept;
        }

        return rootConcept;
    }

}
