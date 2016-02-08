package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.BaseIntegrationTest;
import org.bahmni.module.bahmnicore.model.Episode;
import org.bahmni.module.bahmnicore.service.BahmniProgramWorkflowService;
import org.bahmni.module.bahmnicore.service.EpisodeService;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class EpisodeServiceImplIT extends BaseIntegrationTest {
    @Autowired
    private EpisodeService episodeService;
    private BahmniProgramWorkflowService bahmniProgramWorkflowService;
    @Before
    public void setUp() throws Exception {
        bahmniProgramWorkflowService = Context.getService(BahmniProgramWorkflowService.class);
    }

    @Test
    public void shouldCreateANewEpisode() {
        Episode episode = createAnEpisode();
        assertThat(episode.getId(), is(notNullValue()));
        Episode savedEpisode = episodeService.get(episode.getId());
        assertThat(savedEpisode.getEncounters(), is(notNullValue()));
    }

    private Episode createAnEpisode() {
        Episode episode = new Episode();
        episode.addPatientProgram(bahmniProgramWorkflowService.getPatientProgram(1));
        episodeService.save(episode);
        return episode;
    }

    @Test
    public void shouldRetrieveEpisodeForAProgram() {
        createAnEpisode();
        PatientProgram patientProgram = bahmniProgramWorkflowService.getPatientProgram(1);

        Episode episodeForPatientProgram = episodeService.getEpisodeForPatientProgram(patientProgram);

        Set<PatientProgram> patientPrograms = episodeForPatientProgram.getPatientPrograms();
        assertThat(patientPrograms.size(), is(equalTo(1)));
        assertThat(patientPrograms.iterator().next().getUuid(), is(equalTo(patientProgram.getUuid())));
    }
}