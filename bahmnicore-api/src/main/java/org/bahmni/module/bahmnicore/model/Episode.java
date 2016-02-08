package org.bahmni.module.bahmnicore.model;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Encounter;
import org.openmrs.PatientProgram;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Episode extends BaseOpenmrsData {
    private Integer episodeId;
    private Set<Encounter> encounters = new HashSet<>();
    private Set<PatientProgram> patientPrograms = new HashSet<>();

    public Episode(Integer episodeId, Set<Encounter> encounters, Set<PatientProgram> patientPrograms) {
        this.episodeId = episodeId;
        this.encounters = encounters;
        this.patientPrograms = patientPrograms;
    }

    public Episode() {
    }

    public Set<Encounter> getEncounters() {
        return encounters;
    }

    public Integer getEpisodeId() {
        return episodeId;
    }

    @Override
    public Integer getId() {
        return episodeId;
    }

    @Override
    public void setId(Integer id) {

    }

    public Set<PatientProgram> getPatientPrograms() {
        return patientPrograms;
    }

    public void addEncounter(Encounter encounter) {
        getEncounters().add(encounter);
    }

    public void addPatientProgram(PatientProgram patientProgram) {
        getPatientPrograms().add(patientProgram);
    }

    public void setEpisodeId(Integer episodeId) {
        this.episodeId = episodeId;
    }

    public void setEncounters(Set<Encounter> encounters) {
        this.encounters = encounters;
    }

    public void setPatientPrograms(Set<PatientProgram> patientPrograms) {
        this.patientPrograms = patientPrograms;
    }
}
