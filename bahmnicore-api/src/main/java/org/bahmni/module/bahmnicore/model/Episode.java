package org.bahmni.module.bahmnicore.model;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Encounter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Episode extends BaseOpenmrsData {
    private Integer episodeId;
    private Set<Encounter> encounters = new HashSet<>();

    public Episode(Integer episodeId, Set<Encounter> encounters) {
        this.episodeId = episodeId;
        this.encounters = encounters;
    }

    public Episode() {
    }

    public Set<Encounter> getEncounters() {
        return encounters;
    }

    public void setEncounters(Set<Encounter> encounters) {
        this.encounters = encounters;
    }

    public Integer getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(Integer episodeId) {
        this.episodeId = episodeId;
    }

    @Override
    public Integer getId() {
        return episodeId;
    }

    @Override
    public void setId(Integer id) {

    }

    public void addEncounter(Encounter encounter) {
        getEncounters().add(encounter);
    }
}
