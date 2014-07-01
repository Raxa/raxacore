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
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.encounter.EncounterParameters;
import org.openmrs.module.emrapi.encounter.exception.EncounterMatcherNotFoundException;
import org.openmrs.module.emrapi.encounter.matcher.BaseEncounterMatcher;
import org.openmrs.module.emrapi.encounter.matcher.DefaultEncounterMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

@Service
public class VisitDocumentServiceImpl implements VisitDocumentService {
    public static final String DOCUMENT_OBS_GROUP_CONCEPT_NAME = "Document";

    private PatientImageService patientImageService;

    private VisitService visitService;
    private ConceptService conceptService;
    private EncounterService encounterService;
	private AdministrationService administrationService;
	private Map<String, BaseEncounterMatcher> encounterMatcherMap ;

	@Autowired
	public VisitDocumentServiceImpl(PatientImageService patientImageService, VisitService visitService, ConceptService conceptService, EncounterService encounterService,@Qualifier("adminService")AdministrationService administrationService) {
        this.patientImageService = patientImageService;
        this.visitService = visitService;
        this.conceptService = conceptService;
        this.encounterService = encounterService;
		this.administrationService = administrationService;
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
                String url = document.getImage();
                parentObservation.addGroupMember(newObs(document.getObsDateTime(), encounter, imageConcept, url));
            }
        }
        encounter.setObs(observations);
    }

    private Obs findOrCreateParentObs(Encounter encounter, Date observationDateTime, Concept testConcept, String obsUuid) {
        Obs observation = findObservation(encounter.getAllObs(), obsUuid);
        return observation != null ? observation : newObs(observationDateTime, encounter, testConcept, null) ;
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
        EncounterParameters encounterParameters = EncounterParameters.instance();
		EncounterType encounterType = encounterService.getEncounterTypeByUuid(encounterTypeUUID);
		Provider providerByUuid = Context.getProviderService().getProviderByUuid(providerUuid);

		encounterParameters.setEncounterType(encounterType);
		encounterParameters.setProviders(new HashSet<Provider>(Arrays.asList(providerByUuid)));
		Encounter existingEncounter = findEncounter(visit, encounterParameters);
		if (existingEncounter != null) {
			return existingEncounter;
		}

		Encounter encounter = new Encounter();
		encounter.setPatient(patient);
		encounter.setEncounterType(encounterType);
		encounter.setEncounterDatetime(encounterDateTime);
		EncounterRole encounterRoleByUuid = Context.getEncounterService().getEncounterRoleByUuid(EncounterRole.UNKNOWN_ENCOUNTER_ROLE_UUID);
        encounter.addProvider(encounterRoleByUuid, providerByUuid);
        return encounter;
    }

  /*  private Encounter findEncounter(Visit visit, String encounterTypeUUID) {
        if (visit != null && visit.getEncounters() != null) {
            for (Encounter encounter : visit.getEncounters()) {
                if (encounterTypeUUID.equals(encounter.getEncounterType().getUuid())) {
                    return encounter;
                }
            }
        }
        return null;
    }*/

	private Encounter findEncounter(Visit visit, EncounterParameters encounterParameters) {

//		AdministrationService administrationService = Context.getAdministrationService();
		String matcherClass = administrationService.getGlobalProperty("emr.encounterMatcher");
		BaseEncounterMatcher encounterMatcher = isNotEmpty(matcherClass)? getEncounterMatcherMap().get(matcherClass) : new DefaultEncounterMatcher();
		if (encounterMatcher == null) {
			throw new EncounterMatcherNotFoundException();
		}
		return encounterMatcher.findEncounter(visit, encounterParameters);
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

	private Map<String, BaseEncounterMatcher> getEncounterMatcherMap(){
		if(encounterMatcherMap == null){
			encounterMatcherMap = new HashMap<>();
			List<BaseEncounterMatcher> encounterMatchers = Context.getRegisteredComponents(BaseEncounterMatcher.class);
			for (BaseEncounterMatcher encounterMatcher : encounterMatchers) {
				encounterMatcherMap.put(encounterMatcher.getClass().getCanonicalName(), encounterMatcher);
			}
		}
		return  encounterMatcherMap;
	}
}
