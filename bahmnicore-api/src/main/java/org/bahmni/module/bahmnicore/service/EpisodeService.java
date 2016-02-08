package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.model.Episode;
import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.BahmniPatientProgram;
import org.openmrs.PatientProgram;

public interface EpisodeService {
    void save(Episode episode);

    Episode get(Integer episodeId);

    Episode getEpisodeForPatientProgram(PatientProgram patientProgram);
}
