package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.dao.EpisodeDAO;
import org.bahmni.module.bahmnicore.model.Episode;
import org.bahmni.module.bahmnicore.service.EpisodeService;
import org.openmrs.PatientProgram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class EpisodeServiceImpl implements EpisodeService {
    @Autowired
    private EpisodeDAO episodeDAO;

    @Override
    public void save(Episode episode) {
        episodeDAO.save(episode);
    }

    @Override
    public Episode get(Integer episodeId) {
        return episodeDAO.get(episodeId);
    }

    @Override
    public Episode getEpisodeForPatientProgram(PatientProgram patientProgram) {
        return episodeDAO.getEpisodeForPatientProgram(patientProgram);
    }
}
