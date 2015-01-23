package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.service.BahmniConceptService;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.emrapi.encounter.ConceptMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BahmniConceptServiceImpl implements BahmniConceptService{

    private ConceptService conceptService;
    private ConceptMapper conceptMapper;

    @Autowired
    public BahmniConceptServiceImpl(ConceptService conceptService) {
        this.conceptService = conceptService;
        this.conceptMapper = new ConceptMapper();
    }

    @Override
    public EncounterTransaction.Concept getConceptByName(String conceptName) {
        Concept concept = conceptService.getConceptByName(conceptName);
        if (concept == null) {
            return new EncounterTransaction.Concept(null, conceptName, false, null, null, null, null);
        }
        return conceptMapper.map(concept);
    }
}
