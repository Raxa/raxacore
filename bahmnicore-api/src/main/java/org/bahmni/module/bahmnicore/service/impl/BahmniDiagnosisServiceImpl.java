package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.service.BahmniDiagnosisService;
import org.openmrs.*;
import org.openmrs.api.*;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.openmrs.module.bahmniemrapi.diagnosis.helper.BahmniDiagnosisHelper;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.BahmniDiagnosisMapper;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.diagnosis.Diagnosis;
import org.openmrs.module.emrapi.diagnosis.DiagnosisService;
import org.openmrs.module.emrapi.encounter.DateMapper;
import org.openmrs.module.emrapi.encounter.DiagnosisMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


import java.text.*;
import java.util.*;

import static org.openmrs.module.bahmniemrapi.diagnosis.helper.BahmniDiagnosisHelper.BAHMNI_INITIAL_DIAGNOSIS;

@Component
public class BahmniDiagnosisServiceImpl implements BahmniDiagnosisService {
    private EncounterService encounterService;

    private ObsService obsService;

    @Autowired
    private EmrApiProperties emrApiProperties;

    @Autowired
    private VisitService visitService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private DiagnosisMapper diagnosisMapper;

    @Autowired
    private BahmniDiagnosisMapper bahmniDiagnosisMapper;

    @Autowired
    private DiagnosisService diagnosisService;

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private BahmniDiagnosisHelper bahmniDiagnosisHelper;

    @Autowired
    public BahmniDiagnosisServiceImpl(EncounterService encounterService, ObsService obsService) {
        this.encounterService = encounterService;
        this.obsService = obsService;
    }

    @Override
    public void delete(String visitDiagnosesObservationUuid) {
        Obs visitDiagnosisObs = obsService.getObsByUuid(visitDiagnosesObservationUuid);
        String initialVisitDiagnosisUuid = findInitialDiagnosisUuid(visitDiagnosisObs);
        voidAllDiagnosisWithSameInitialDiagnosis(initialVisitDiagnosisUuid, visitDiagnosisObs);
    }

    private String findInitialDiagnosisUuid(Obs visitDiagnosisObs) {
        for (Obs obs : visitDiagnosisObs.getGroupMembers()) {
            if (obs.getConcept().getName().getName().equals(BAHMNI_INITIAL_DIAGNOSIS)) {
                return obs.getValueText();
            }
        }
        return null;
    }

    private void voidAllDiagnosisWithSameInitialDiagnosis(String initialVisitDiagnosisUuid, Obs visitDiagnosisObs) {
        //find observations for this patient and concept
        List<Obs> observations = obsService.getObservationsByPersonAndConcept(visitDiagnosisObs.getPerson(), visitDiagnosisObs.getConcept());
        for (Obs observation : observations) {
            for (Obs obs : observation.getGroupMembers()) {
                if (initialVisitDiagnosisUuid.equals(obs.getValueText())) {
                    voidDiagnosis(observation);
                    break;
                }
            }
        }
    }


    private List<Diagnosis> getDiagnosisByPatient(Patient patient, Visit visit){
        List<Diagnosis> diagnoses = new ArrayList<Diagnosis>();

        List<Obs> observations = obsService.getObservations(Arrays.asList((Person) patient), new ArrayList<Encounter>(visit.getEncounters()),
                Arrays.asList(emrApiProperties.getDiagnosisMetadata().getDiagnosisSetConcept()),
                null, null, null, Arrays.asList("obsDatetime"),
                null, null, null, null, false);

        for (Obs obs : observations) {
            Diagnosis diagnosis = bahmniDiagnosisHelper.buildDiagnosisFromObsGroup(obs);
            if(diagnosis != null) {
                diagnoses.add(diagnosis);
            }
        }
        return diagnoses;
    }

    private void addDiagnosisToCollectionIfRecent(List<BahmniDiagnosisRequest> bahmniDiagnosisRequests, BahmniDiagnosisRequest bahmniDiagnosisRequestNew){
        String existingObsForNewDiag = bahmniDiagnosisRequestNew.getFirstDiagnosis().getExistingObs();
        Iterator<BahmniDiagnosisRequest> bahmniIterator = bahmniDiagnosisRequests.iterator();
        boolean addFlag = true;
        while (bahmniIterator.hasNext()) {
            BahmniDiagnosisRequest bahmniDiagnosisRequestFromList = bahmniIterator.next();
            String existingObsOfDiagFromList = bahmniDiagnosisRequestFromList.getFirstDiagnosis().getExistingObs();
            if(existingObsOfDiagFromList.equals(existingObsForNewDiag)) {
                if (bahmniDiagnosisRequestFromList.getDiagnosisDateTime().getTime() > bahmniDiagnosisRequestFromList.getDiagnosisDateTime().getTime()) {
                    bahmniIterator.remove();
                    break;
                } else {
                    addFlag = false;
                    break;
                }
            }
        }
        if(addFlag){
            bahmniDiagnosisRequests.add(bahmniDiagnosisRequestNew);
        }
    }

    public List<BahmniDiagnosisRequest> getBahmniDiagnosisByPatientAndVisit(String patientUuid,String visitUuid){
        Assert.notNull(visitUuid,"VisitUuid should not be null");
        Patient patient = patientService.getPatientByUuid(patientUuid);
        Visit visit = visitService.getVisitByUuid(visitUuid);

        List<Diagnosis> diagnosisByVisit = getDiagnosisByPatient(patient,visit);

        List<BahmniDiagnosisRequest> bahmniDiagnosisRequests = new ArrayList<>();

        for(Diagnosis diagnosis: diagnosisByVisit){

            EncounterTransaction.Diagnosis etDiagnosis = diagnosisMapper.convert(diagnosis);
            Diagnosis latestDiagnosis = bahmniDiagnosisHelper.getLatestBasedOnAnyDiagnosis(diagnosis); //buildDiagnosisFromObsGroup(getBahmniDiagnosisHelper().getLatestBasedOnAnyDiagnosis(diagnosis));
            EncounterTransaction.Diagnosis etLatestDiagnosis = diagnosisMapper.convert(latestDiagnosis);
            addDiagnosisToCollectionIfRecent(bahmniDiagnosisRequests, bahmniDiagnosisMapper.mapBahmniDiagnosis(etDiagnosis, etLatestDiagnosis, true, false));
        }

        return bahmniDiagnosisRequests;
    }

    public List<BahmniDiagnosisRequest> getBahmniDiagnosisByPatientAndDate(String patientUuid, String date) throws ParseException {
        Patient patient = patientService.getPatientByUuid(patientUuid);

        Date fromDate = date!=null ? new SimpleDateFormat("yyyy-MM-dd").parse(date) : null;
        List<Diagnosis> diagnosisByPatientAndDate = diagnosisService.getDiagnoses(patient, fromDate);

        List<BahmniDiagnosisRequest> bahmniDiagnosisRequests = new ArrayList<>();

        for(Diagnosis diagnosis: diagnosisByPatientAndDate){
            EncounterTransaction.Diagnosis etDiagnosis = diagnosisMapper.convert(diagnosis);
            BahmniDiagnosisRequest bahmniDiagnosisRequest = bahmniDiagnosisMapper.mapBahmniDiagnosis(etDiagnosis, null, true, false);

            if(!bahmniDiagnosisRequest.isRevised()){
                bahmniDiagnosisRequests.add(bahmniDiagnosisRequest);
            }
        }

        return bahmniDiagnosisRequests;
    }

    private void voidDiagnosis(Obs observation) {
        voidObsAndItsChildren(observation);
        encounterService.saveEncounter(observation.getEncounter());
    }

    private void voidObsAndItsChildren(Obs obs) {
        obs.setVoided(true);
        if (obs.getGroupMembers() == null)
            return;
        for (Obs childObs : obs.getGroupMembers()) {
            voidObsAndItsChildren(childObs);
        }
    }


    public void setEmrApiProperties(EmrApiProperties emrApiProperties) {
        this.emrApiProperties = emrApiProperties;
    }

    public void setVisitService(VisitService visitService) {
        this.visitService = visitService;
    }

    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }

    public void setBahmniDiagnosisHelper(BahmniDiagnosisHelper bahmniDiagnosisHelper) {
        this.bahmniDiagnosisHelper = bahmniDiagnosisHelper;
    }

    public void setDiagnosisMapper(DiagnosisMapper diagnosisMapper) {
        this.diagnosisMapper = diagnosisMapper;
    }

    public void setBahmniDiagnosisMapper(BahmniDiagnosisMapper bahmniDiagnosisMapper) {
        this.bahmniDiagnosisMapper = bahmniDiagnosisMapper;
    }




}
