package org.bahmni.module.bahmnicore.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.bahmni.module.bahmnicore.dao.BahmniProgramWorkflowDAO;
import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.BahmniPatientProgram;
import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.PatientProgramAttribute;
import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.ProgramAttributeType;
import org.bahmni.module.bahmnicore.service.BahmniProgramServiceValidator;
import org.bahmni.module.bahmnicore.service.BahmniProgramWorkflowService;
import org.openmrs.Encounter;
import org.openmrs.PatientProgram;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.ProgramWorkflowServiceImpl;
import org.openmrs.module.episodes.Episode;
import org.openmrs.module.episodes.service.EpisodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Transactional
public class BahmniProgramWorkflowServiceImpl extends ProgramWorkflowServiceImpl implements BahmniProgramWorkflowService {

    @Autowired
    private EpisodeService episodeService;
    @Autowired
    private List<BahmniProgramServiceValidator> bahmniProgramServiceValidators;

    public BahmniProgramWorkflowServiceImpl(BahmniProgramWorkflowDAO programWorkflowDAO, EpisodeService episodeService) {
        this.episodeService = episodeService;
        this.dao = programWorkflowDAO;
    }

    //Default constructor to satisfy Spring
    public BahmniProgramWorkflowServiceImpl() {
    }

    @Override
    public List<ProgramAttributeType> getAllProgramAttributeTypes() {
        return ((BahmniProgramWorkflowDAO) dao).getAllProgramAttributeTypes();
    }

    @Override
    public ProgramAttributeType getProgramAttributeType(Integer id) {
        return ((BahmniProgramWorkflowDAO) dao).getProgramAttributeType(id);
    }

    @Override
    public ProgramAttributeType getProgramAttributeTypeByUuid(String uuid) {
        return ((BahmniProgramWorkflowDAO) dao).getProgramAttributeTypeByUuid(uuid);
    }

    @Override
    public ProgramAttributeType saveProgramAttributeType(ProgramAttributeType type) {
        return ((BahmniProgramWorkflowDAO) dao).saveProgramAttributeType(type);
    }

    @Override
    public void purgeProgramAttributeType(ProgramAttributeType type) {
        ((BahmniProgramWorkflowDAO) dao).purgeProgramAttributeType(type);
    }

    @Override
    public PatientProgramAttribute getPatientProgramAttributeByUuid(String uuid) {
        return ((BahmniProgramWorkflowDAO) dao).getPatientProgramAttributeByUuid(uuid);
    }

    @Override
    public Collection<Encounter> getEncountersByPatientProgramUuid(String patientProgramUuid) {
        PatientProgram patientProgram = dao.getPatientProgramByUuid(patientProgramUuid);
        Episode episode = episodeService.getEpisodeForPatientProgram(patientProgram);
        return episode == null ? Collections.EMPTY_LIST : episode.getEncounters();
    }

    @Override
    public PatientProgram savePatientProgram(PatientProgram patientProgram) throws APIException {
        preSaveValidation(patientProgram);
        if (patientProgram.getOutcome() != null && patientProgram.getDateCompleted() == null) {
            patientProgram.setDateCompleted(new Date());
        }
        BahmniPatientProgram bahmniPatientProgram = (BahmniPatientProgram)super.savePatientProgram(patientProgram);
        createEpisodeIfRequired(bahmniPatientProgram);
        return bahmniPatientProgram;
    }

    @Override
    public Map<Object, Object> getPatientProgramAttributeByAttributeName(List<Integer> patients, String attributeName){
        return ((BahmniProgramWorkflowDAO) dao).getPatientProgramAttributeByAttributeName(patients, attributeName);
    }
    @Override
    public List<BahmniPatientProgram> getPatientProgramByAttributeNameAndValue(String attributeName, String attributeValue) {
        return ((BahmniProgramWorkflowDAO)dao).getPatientProgramByAttributeNameAndValue(attributeName, attributeValue);
    }

    private void preSaveValidation(PatientProgram patientProgram) {
        if(CollectionUtils.isNotEmpty(bahmniProgramServiceValidators)) {
            for (BahmniProgramServiceValidator bahmniProgramServiceValidator : bahmniProgramServiceValidators) {
                bahmniProgramServiceValidator.validate(patientProgram);
            }
        }
    }

    private void createEpisodeIfRequired(BahmniPatientProgram bahmniPatientProgram) {
        if (episodeService.getEpisodeForPatientProgram(bahmniPatientProgram) != null) return;
        Episode episode = new Episode();
        episode.addPatientProgram(bahmniPatientProgram);
        episodeService.save(episode);
    }
}
