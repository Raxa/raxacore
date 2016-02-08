package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.BaseIntegrationTest;
import org.bahmni.module.bahmnicore.dao.EpisodeDAO;
import org.bahmni.module.bahmnicore.model.Episode;
import org.junit.Test;
import org.openmrs.api.EncounterService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class EpisodeDaoIT extends BaseIntegrationTest {

    @Autowired
    private EpisodeDAO episodeDAO;
    @Autowired
    private EncounterService encounterService;

    @Test
    public void shouldCreateANewEpisode() {
        Episode episode = new Episode();
        episodeDAO.save(episode);
        assertThat(episode.getId(), is(notNullValue()));
        Episode savedEpisode = episodeDAO.get(episode.getId());
        assertThat(savedEpisode.getEncounters(), is(notNullValue()));
    }

    @Test
    public void shouldCreateANewEpisodeWithEncounter() {
        Episode episode = new Episode();
        episode.addEncounter(encounterService.getEncounter(3));
        episode.addEncounter(encounterService.getEncounter(4));
        episode.addEncounter(encounterService.getEncounter(5));
        episodeDAO.save(episode);

        Episode savedEpisode = episodeDAO.get(episode.getId());
        assertThat(savedEpisode.getEncounters().size(), is(3));
    }
}
