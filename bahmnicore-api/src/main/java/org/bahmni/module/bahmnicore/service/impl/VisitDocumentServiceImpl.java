package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.contract.visitDocument.VisitDocumentRequest;
import org.bahmni.module.bahmnicore.model.Document;
import org.bahmni.module.bahmnicore.service.PatientImageService;
import org.bahmni.module.bahmnicore.service.VisitDocumentService;
import org.openmrs.*;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.encounter.EncounterParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VisitDocumentServiceImpl implements VisitDocumentService {
    public static final String DOCUMENT_OBS_GROUP_CONCEPT_NAME = "Document";

    private PatientImageService patientImageService;

    @Autowired
    public VisitDocumentServiceImpl(PatientImageService patientImageService) {
        this.patientImageService = patientImageService;
    }

    @Override
    public Visit upload(VisitDocumentRequest visitDocumentRequest) {
        Patient patient = Context.getPatientService().getPatientByUuid(visitDocumentRequest.getPatientUuid());
        Visit visit = createVisit(visitDocumentRequest.getVisitTypeUuid(), visitDocumentRequest.getVisitStartDate(), visitDocumentRequest.getVisitEndDate(), patient);
        Encounter encounter = createEncounter(visit, visitDocumentRequest.getEncounterTypeUuid(), visitDocumentRequest.getEncounterDateTime(), patient, visitDocumentRequest.getProviderUuid());
        Set<Obs> observations = createObservationGroup(visitDocumentRequest.getEncounterDateTime(), visitDocumentRequest.getDocuments(), patient, encounter);
        encounter.setObs(observations);
        return Context.getVisitService().saveVisit(visit);
    }

    private Set<Obs> createObservationGroup(Date encounterDateTime, List<Document> documents, Patient patient, Encounter encounter) {
        Set<Obs> observations = new HashSet<>();

        ConceptService conceptService = Context.getConceptService();
        Concept imageConcept = conceptService.getConceptByName(DOCUMENT_OBS_GROUP_CONCEPT_NAME);

        for (Document document : documents) {
            Concept testConcept = conceptService.getConceptByUuid(document.getTestUuid());

            Obs parentObservation = createOrFindObservation(observations, encounterDateTime, encounter, testConcept);
            List<Obs> childObservations = createObservationsWithImageUrl(patient, document, encounterDateTime, encounter, imageConcept);

            for (Obs childObservation : childObservations) {
                parentObservation.addGroupMember(childObservation);
            }
        }
        return observations;
    }

    private Obs createOrFindObservation(Set<Obs> observations, Date encounterDateTime, Encounter encounter, Concept testConcept) {
        for (Obs observation : observations) {
            if (observation.getConcept().equals(testConcept)) {
                return observation;
            }
        }
        Obs observation = createNewObservation(encounterDateTime, encounter, testConcept, null);
        observations.add(observation);
        return observation;
    }

    private List<Obs> createObservationsWithImageUrl(Patient patient, Document document, Date encounterDateTime, Encounter encounter, Concept concept) {
        String url = null;
        List<Obs> imageObservation = new ArrayList<>();
        if (document != null) {
            url = patientImageService.saveDocument(patient.getId(), encounter.getEncounterType().getName(), document.getImage(), document.getFormat());
        }
        imageObservation.add(createNewObservation(encounterDateTime, encounter, concept, url));
        return imageObservation;
    }

    private Obs createNewObservation(Date encounterDateTime, Encounter encounter, Concept concept, String url) {
        Obs observation = new Obs();
        observation.setPerson(encounter.getPatient());
        observation.setEncounter(encounter);
        observation.setConcept(concept);
        observation.setObsDatetime(encounterDateTime);
        if (url != null) {
            observation.setValueText(url);
        }
        return observation;
    }

    private Encounter createEncounter(Visit visit, String encounterTypeUUID, Date encounterDateTime, Patient patient, String providerUuid) {
        EncounterType encounterType = Context.getEncounterService().getEncounterTypeByUuid(encounterTypeUUID);
        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setEncounterType(encounterType);
        encounter.setEncounterDatetime(encounterDateTime);
        EncounterRole encounterRoleByUuid = Context.getEncounterService().getEncounterRoleByUuid(EncounterRole.UNKNOWN_ENCOUNTER_ROLE_UUID);
        Provider providerByUuid = Context.getProviderService().getProviderByUuid(providerUuid);
        encounter.addProvider(encounterRoleByUuid, providerByUuid);
        visit.addEncounter(encounter);
        return encounter;
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
}
