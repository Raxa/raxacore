package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.contract.observation.ConceptData;
import org.bahmni.module.bahmnicore.contract.observation.ConceptDefinition;
import org.bahmni.module.bahmnicore.dao.PersonObsDao;
import org.bahmni.module.bahmnicore.service.BahmniPersonObsService;
import org.bahmni.module.bahmnicore.service.ConceptService;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BahmniPersonObsServiceImpl implements BahmniPersonObsService {
    private PersonObsDao personObsDao;
    private ConceptService conceptService;

    @Autowired
    public BahmniPersonObsServiceImpl(PersonObsDao personObsDao, ConceptService conceptService) {
        this.personObsDao = personObsDao;
        this.conceptService = conceptService;
    }

    @Override
    public List<Obs> getObsForPerson(String identifier) {
        return personObsDao.getNumericObsByPerson(identifier);
    }

    @Override
    public List<Obs> observationsFor(String patientUuid, List<String> conceptNames, Integer numberOfVisits) {
        return personObsDao.getObsFor(patientUuid, conceptNames, numberOfVisits);
    }

    @Override
    public List<Obs> getLatest(String patientUuid, List<String> conceptNames) {
        List<Obs> latestObs = new ArrayList<>();
        ConceptDefinition conceptDefinition = conceptService.conceptsFor(conceptNames);
        for (ConceptData concept : conceptDefinition.getConcepts()) {
            latestObs.addAll(personObsDao.getLatestObsFor(patientUuid, concept.getName(), 1));
        }
        return latestObs;
    }

    @Override
    public List<Concept> getNumericConceptsForPerson(String personUUID) {
        return personObsDao.getNumericConceptsForPerson(personUUID);
    }
}
