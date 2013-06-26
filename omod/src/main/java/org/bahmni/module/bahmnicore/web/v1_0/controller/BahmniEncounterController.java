package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.apache.commons.lang.StringUtils;
import org.bahmni.module.bahmnicore.contract.encounterdata.*;
import org.joda.time.DateMidnight;
import org.joda.time.LocalDateTime;
import org.openmrs.*;
import org.openmrs.api.*;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.*;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/bahmniencounter")
public class BahmniEncounterController extends BaseRestController {
    @Autowired
    private VisitService visitService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private ConceptService conceptService;
    @Autowired
    private EncounterService encounterService;
    @Autowired
    private ObsService obsService;

    public BahmniEncounterController(VisitService visitService, PatientService patientService, ConceptService conceptService, EncounterService encounterService,
                                     ObsService obsService) {
        this.visitService = visitService;
        this.patientService = patientService;
        this.conceptService = conceptService;
        this.encounterService = encounterService;
        this.obsService = obsService;
    }

    public BahmniEncounterController() {
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public EncounterObservations get(GetObservationsRequest getObservationsRequest) {
        Visit visit = getActiveVisit(getObservationsRequest.getPatientUUID());
        ArrayList<ObservationData> observations = new ArrayList<ObservationData>();
        if (visit == null) return new EncounterObservations(observations);

        Encounter encounter = getMatchingEncounter(visit, getObservationsRequest.getEncounterTypeUUID());
        if (encounter == null) return new EncounterObservations(observations);

        Set<Obs> allObs = encounter.getAllObs();
        for (Obs obs : allObs) {
            Concept concept = obs.getConcept();
            ConceptDatatype datatype = concept.getDatatype();
            Object value = datatype.isNumeric() ? obs.getValueNumeric() : obs.getValueAsString(Locale.getDefault());
            observations.add(new ObservationData(concept.getUuid(), concept.getName().getName(), value));
        }
        return new EncounterObservations(observations);
    }

    @RequestMapping(method = RequestMethod.GET, value = "config")
    @ResponseBody
    public EncounterConfig getConfig(String callerContext) {
        EncounterConfig encounterConfig = new EncounterConfig();
        List<VisitType> visitTypes = visitService.getAllVisitTypes();
        for (VisitType visitType : visitTypes) {
            encounterConfig.addVisitType(visitType.getName(), visitType.getUuid());
        }
        List<EncounterType> allEncounterTypes = encounterService.getAllEncounterTypes(false);
        for (EncounterType encounterType : allEncounterTypes) {
            encounterConfig.addEncounterType(encounterType.getName(), encounterType.getUuid());
        }
        Concept conceptSetConcept = conceptService.getConcept(callerContext);
        if (conceptSetConcept != null) {
            List<Concept> conceptsByConceptSet = conceptService.getConceptsByConceptSet(conceptSetConcept);
            for (Concept concept : conceptsByConceptSet) {
                ConceptData conceptData = new ConceptData(concept.getUuid());
                encounterConfig.addConcept(concept.getName().getName(), conceptData);
            }
        }
        return encounterConfig;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public EncounterDataResponse create(@RequestBody EncounterData encounterData)
            throws Exception {
        Patient patient = patientService.getPatientByUuid(encounterData.getPatientUUID());
        Visit visit = getActiveVisit(encounterData.getPatientUUID());
        Date encounterDatetime = new Date();
        if (visit == null) {
            visit = new Visit();
            visit.setPatient(patient);
            visit.setVisitType(visitService.getVisitTypeByUuid(encounterData.getVisitTypeUUID()));
            visit.setStartDatetime(encounterDatetime);
            visit.setEncounters(new HashSet<Encounter>());
            visit.setUuid(UUID.randomUUID().toString());
        }
        Encounter encounter = getMatchingEncounter(visit, encounterData.getEncounterTypeUUID());
        if (encounter == null) {
            encounter = new Encounter();
            encounter.setPatient(patient);
            encounter.setEncounterType(encounterService.getEncounterTypeByUuid(encounterData.getEncounterTypeUUID()));
            encounter.setEncounterDatetime(encounterDatetime);
            encounter.setUuid(UUID.randomUUID().toString());
            encounter.setObs(new HashSet<Obs>());
            //should use addEncounter method here, which seems to be have been added later
            visit.getEncounters().add(encounter);
        }
        addOrOverwriteObservations(encounterData, encounter, patient, encounterDatetime);
        visitService.saveVisit(visit);
        return new EncounterDataResponse(visit.getUuid(), encounter.getUuid(), "");
    }

    private void addOrOverwriteObservations(EncounterData encounterData, Encounter encounter, Patient patient, Date encounterDateTime) throws ParseException {
        Set<Obs> existingObservations = encounter.getAllObs();
        for (ObservationData observationData : encounterData.getObservations()) {
            Obs observation = getMatchingObservation(existingObservations, observationData.getConceptUUID());
            Object value = observationData.getValue();
            boolean observationValueSpecified = value != null && StringUtils.isNotEmpty(value.toString());
            if (observation == null && observationValueSpecified) {
                observation = new Obs();
                Concept concept = conceptService.getConceptByUuid(observationData.getConceptUUID());
                observation.setConcept(concept);
                observation.setUuid(UUID.randomUUID().toString());
                observation.setPerson(patient);
                observation.setEncounter(encounter);
                encounter.addObs(observation);
            }
            if (observation != null && observationValueSpecified) {
                setObservationValue(observationData, observation);
                observation.setObsDatetime(encounterDateTime);
            }
            if (observation != null && !observationValueSpecified) {
                encounter.removeObs(observation);
                obsService.purgeObs(observation);
            }
        }
    }

    private void setObservationValue(ObservationData observationData, Obs observation) throws ParseException {
        if (observation.getConcept().getDatatype().isNumeric()) {
            observation.setValueNumeric(Double.parseDouble(observationData.getValue().toString()));
        } else {
            observation.setValueAsString((String) observationData.getValue());
        }
    }

    private Obs getMatchingObservation(Set<Obs> existingObservations, String conceptUUID) {
        for (Obs obs : existingObservations) {
            if (StringUtils.equals(obs.getConcept().getUuid(), conceptUUID)) return obs;
        }
        return null;
    }

    private Encounter getMatchingEncounter(Visit visit, String encounterTypeUUID) {
        Set<Encounter> encounters = visit.getEncounters();
        for (Encounter encounter : encounters) {
            if (StringUtils.equals(encounter.getEncounterType().getUuid(), encounterTypeUUID)) {
                return encounter;
            }
        }
        return null;
    }

    private Visit getActiveVisit(String patientUuid) {
        Patient patient = patientService.getPatientByUuid(patientUuid);
        List<Visit> activeVisitsByPatient = visitService.getActiveVisitsByPatient(patient);

        for (Visit visit : activeVisitsByPatient) {
            if (visit.getStartDatetime().after(DateMidnight.now().toDate())) {
                return visit;
            }
        }
        return null;
    }
}
