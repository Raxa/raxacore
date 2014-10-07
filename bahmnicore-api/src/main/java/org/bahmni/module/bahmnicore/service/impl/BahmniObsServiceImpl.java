package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.dao.PersonObsDao;
import org.bahmni.module.bahmnicore.service.BahmniObsService;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BahmniObsServiceImpl implements BahmniObsService {
    private PersonObsDao personObsDao;

    @Autowired
    public BahmniObsServiceImpl(PersonObsDao personObsDao) {
        this.personObsDao = personObsDao;
    }

    @Override
    public List<Obs> getObsForPerson(String identifier) {
        return personObsDao.getNumericObsByPerson(identifier);
    }

    @Override
    public List<Obs> observationsFor(String patientUuid, List<Concept> concepts, Integer numberOfVisits) {
        return personObsDao.getObsFor(patientUuid, getAllConceptNames(concepts), numberOfVisits, true);
    }

    @Override
    public List<Obs> getLatest(String patientUuid, List<String> conceptNames) {
        List<Obs> latestObs = new ArrayList<>();
        for (String conceptName : conceptNames) {
            latestObs.addAll(personObsDao.getLatestObsFor(patientUuid, conceptName, 1));
        }
        return latestObs;
    }

    @Override
    public List<Concept> getNumericConceptsForPerson(String personUUID) {
        return personObsDao.getNumericConceptsForPerson(personUUID);
    }

    private List<String> getAllConceptNames(List<Concept> concepts) {
        List<String> conceptNames = new ArrayList<>();
        for (Concept concept: concepts) {
            conceptNames.add(concept.getName().getName());
            if(concept.isSet()) {
                conceptNames.addAll(getAllConceptNames(concept.getSetMembers()));
            }
        }
        return conceptNames;
    }
}
