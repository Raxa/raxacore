package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.dao.PersonObsDao;
import org.bahmni.module.bahmnicore.service.BahmniPersonObsService;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BahmniPersonObsServiceImpl implements BahmniPersonObsService {
    private PersonObsDao personObsDao;

    @Autowired
    public BahmniPersonObsServiceImpl(PersonObsDao personObsDao) {
        this.personObsDao = personObsDao;
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
        for (String name : conceptNames) {
            latestObs.addAll(personObsDao.getLatestObsFor(patientUuid, name, 1));
        }
        return latestObs;
    }

    @Override
    public List<Concept> getNumericConceptsForPerson(String personUUID) {
        return personObsDao.getNumericConceptsForPerson(personUUID);
    }
}
