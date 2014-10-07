package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.bahmni.module.bahmnicore.service.BahmniObsService;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BahmniObsServiceImpl implements BahmniObsService {
    private ObsDao obsDao;

    @Autowired
    public BahmniObsServiceImpl(ObsDao obsDao) {
        this.obsDao = obsDao;
    }

    @Override
    public List<Obs> getObsForPerson(String identifier) {
        return obsDao.getNumericObsByPerson(identifier);
    }

    @Override
    public List<Obs> observationsFor(String patientUuid, List<Concept> concepts, Integer numberOfVisits) {
        return obsDao.getObsFor(patientUuid, getAllConceptNames(concepts), numberOfVisits, true);
    }

    @Override
    public List<Obs> getLatest(String patientUuid, List<String> conceptNames) {
        List<Obs> latestObs = new ArrayList<>();
        for (String conceptName : conceptNames) {
            latestObs.addAll(obsDao.getLatestObsFor(patientUuid, conceptName, 1));
        }
        return latestObs;
    }

    @Override
    public List<Concept> getNumericConceptsForPerson(String personUUID) {
        return obsDao.getNumericConceptsForPerson(personUUID);
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
