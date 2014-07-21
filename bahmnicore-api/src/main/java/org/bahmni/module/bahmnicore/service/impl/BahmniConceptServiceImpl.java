package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.contract.observation.ConceptData;
import org.bahmni.module.bahmnicore.contract.observation.ConceptDefinition;
import org.bahmni.module.bahmnicore.dao.ConceptDao;
import org.bahmni.module.bahmnicore.service.ConceptService;
import org.openmrs.Concept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
public class BahmniConceptServiceImpl implements ConceptService {
    private ConceptDao bahmniConceptDao;
    private List<String> rootConceptNames;

    @Autowired
    public BahmniConceptServiceImpl(ConceptDao bahmniConceptDao) {
        this.bahmniConceptDao = bahmniConceptDao;
    }

    @Override
    public ConceptDefinition conceptsFor(String[] rootConceptNames) {
        this.rootConceptNames = Arrays.asList(rootConceptNames);

        List<Concept> rootConcepts = bahmniConceptDao.conceptFor(rootConceptNames);
        ConceptDefinition conceptDefinition = new ConceptDefinition();
        flatten(rootConcepts, conceptDefinition, null);
        return conceptDefinition;
    }

    private ConceptDefinition flatten(Collection<Concept> concepts, ConceptDefinition conceptDefinition, Concept rootConcept) {
        for (Concept aConcept : concepts) {
            rootConcept = getRootConcept(aConcept, rootConcept);

            Collection<Concept> conceptMembers = aConcept.getSetMembers();
            if (conceptMembers == null || conceptMembers.isEmpty()) {
                conceptDefinition.add(createConceptForLeaf(aConcept, rootConcept));
            } else if (isConceptDetails(aConcept)) {
                conceptDefinition.add(createConceptForGroup(aConcept, rootConcept));
            } else {
                flatten(conceptMembers, conceptDefinition, rootConcept);
            }
        }

        return conceptDefinition;
    }

    private ConceptData createConceptForGroup(Concept conceptGroup, Concept rootConcept) {
        ConceptData conceptData = null;
        for (Concept aConcept : conceptGroup.getSetMembers()) {
            if (isDuration(aConcept) || isAbnormal(aConcept)) {
            } else {
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

    public Concept getRootConcept(Concept aConcept, Concept rootConcept) {
        for (String rootConceptName : rootConceptNames) {
            if (rootConceptName.equalsIgnoreCase(aConcept.getName().getName()))
                return aConcept;
        }

        return rootConcept;
    }

}
