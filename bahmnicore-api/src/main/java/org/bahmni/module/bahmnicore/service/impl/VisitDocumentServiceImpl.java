package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.contract.visitDocument.VisitDocumentRequest;
import org.bahmni.module.bahmnicore.model.Document;
import org.bahmni.module.bahmnicore.service.PatientImageService;
import org.bahmni.module.bahmnicore.service.VisitDocumentService;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VisitDocumentServiceImpl implements VisitDocumentService {
    public static final String DOCUMENT_OBS_GROUP_CONCEPT_NAME = "Document";

    private PatientImageService patientImageService;

    private VisitService visitService;
    private ConceptService conceptService;
    private EncounterService encounterService;

    @Autowired
    public VisitDocumentServiceImpl(PatientImageService patientImageService, VisitService visitService, ConceptService conceptService, EncounterService encounterService) {
        this.patientImageService = patientImageService;
        this.visitService = visitService;
        this.conceptService = conceptService;
        this.encounterService = encounterService;
    }

    @Override
    public Visit upload(VisitDocumentRequest visitDocumentRequest) {
        Patient patient = Context.getPatientService().getPatientByUuid(visitDocumentRequest.getPatientUuid());

        Visit visit = findOrCreateVisit(visitDocumentRequest, patient);

        Encounter encounter = findOrCreateEncounter(visit, visitDocumentRequest.getEncounterTypeUuid(), visitDocumentRequest.getEncounterDateTime(), patient, visitDocumentRequest.getProviderUuid());
        visit.addEncounter(encounter);

        updateEncounter(encounter, visitDocumentRequest.getEncounterDateTime(), visitDocumentRequest.getDocuments());

        return Context.getVisitService().saveVisit(visit);
    }

    private void updateEncounter(Encounter encounter, Date encounterDateTime, List<Document> documents) {
        LinkedHashSet<Obs> observations = new LinkedHashSet<>(encounter.getAllObs());
        for (Document document : documents) {
            Concept testConcept = conceptService.getConceptByUuid(document.getTestUuid());

            Obs parentObservation = findOrCreateParentObs(encounter, document.getObsDateTime(), testConcept, document.getObsUuid());
            parentObservation.setConcept(testConcept);
            observations.add(parentObservation);

            Concept imageConcept = conceptService.getConceptByName(DOCUMENT_OBS_GROUP_CONCEPT_NAME);
            if (document.isVoided()) {
                voidDocumentObservation(encounter.getAllObs(), document.getObsUuid());
            } else if(document.getObsUuid() == null) {
                String url = saveDocument(encounter, document);
                parentObservation.addGroupMember(newObs(document.getObsDateTime(), encounter, imageConcept, url));
            }
        }
        encounter.setObs(observations);
    }

    private Obs findOrCreateParentObs(Encounter encounter, Date observationDateTime, Concept testConcept, String obsUuid) {
        Obs observation = findObservation(encounter.getAllObs(), obsUuid);
        return observation != null ? observation : newObs(observationDateTime, encounter, testConcept, null) ;
    }

    private String saveDocument(Encounter encounter, Document document) {
        String url = null;
        if (document != null) {
            url = patientImageService.saveDocument(encounter.getPatient().getId(), encounter.getEncounterType().getName(), document.getImage(), document.getFormat());
        }
        return url;
    }

    private void voidDocumentObservation(Set<Obs> allObs, String obsUuid) {
        Obs observation = findObservation(allObs, obsUuid);
        if(observation != null)
            observation.setVoided(true);
    }

    private Obs findObservation(Set<Obs> allObs, String obsUuid) {
        for (Obs obs : allObs) {
            if (obs.getUuid().equals(obsUuid)) {
                return obs;
            }
        }
        return null;
    }

    private Obs newObs(Date obsDate, Encounter encounter, Concept concept, String value) {
        Obs observation = new Obs();
        observation.setPerson(encounter.getPatient());
        observation.setEncounter(encounter);
        observation.setConcept(concept);
        observation.setObsDatetime(obsDate);
        if (value != null) {
            observation.setValueText(value);
        }
        return observation;
    }

    private Encounter findOrCreateEncounter(Visit visit, String encounterTypeUUID, Date encounterDateTime, Patient patient, String providerUuid) {
        Encounter existingEncounter = findEncounter(visit, encounterTypeUUID);
        if (existingEncounter != null) {
            return existingEncounter;
        }

        EncounterType encounterType = encounterService.getEncounterTypeByUuid(encounterTypeUUID);
        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setEncounterType(encounterType);
        encounter.setEncounterDatetime(encounterDateTime);
        EncounterRole encounterRoleByUuid = Context.getEncounterService().getEncounterRoleByUuid(EncounterRole.UNKNOWN_ENCOUNTER_ROLE_UUID);
        Provider providerByUuid = Context.getProviderService().getProviderByUuid(providerUuid);
        encounter.addProvider(encounterRoleByUuid, providerByUuid);
        return encounter;
    }

    private Encounter findEncounter(Visit visit, String encounterTypeUUID) {
        if (visit != null && visit.getEncounters() != null) {
            for (Encounter encounter : visit.getEncounters()) {
                if (encounterTypeUUID.equals(encounter.getEncounterType().getUuid())) {
                    return encounter;
                }
            }
        }
        return null;
    }

    private Visit createVisit(String visitTypeUUID, Date visitStartDate, Date visitEndDate, Patient patient) {
        VisitType visitType = Context.getVisitService().getVisitTypeByUuid(visitTypeUUID);
        Visit visit = new Visit();
        visit.setPatient(patient);
        visit.setVisitType(visitType);
        visit.setStartDatetime(visitStartDate);
        visit.setStopDatetime(visitEndDate);
        visit.setEncounters(new HashSet<Encounter>());
        return visit;
    }

    private Visit findOrCreateVisit(VisitDocumentRequest request, Patient patient) {
        if (request.getVisitUuid() != null) {
            return visitService.getVisitByUuid(request.getVisitUuid());
        }
        return createVisit(request.getVisitTypeUuid(), request.getVisitStartDate(), request.getVisitEndDate(), patient);
    }
}
