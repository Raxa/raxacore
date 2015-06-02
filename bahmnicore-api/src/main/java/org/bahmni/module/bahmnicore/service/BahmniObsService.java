package org.bahmni.module.bahmnicore.service;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Visit;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;

import java.util.Collection;
import java.util.List;

public interface BahmniObsService {
    public List<Obs> getObsForPerson(String identifier);
    public Collection<BahmniObservation> getInitial(String patientUuid, Collection<Concept> conceptNames,Integer numberOfVisits,List<String> obsIgnoreList, Boolean filterOutOrderObs);
    Collection<BahmniObservation> getInitialObsByVisit(Visit visit, List<Concept> rootConcepts, List<String> obsIgnoreList, Boolean filterObsWithOrders);
    public Collection<BahmniObservation> observationsFor(String patientUuid, Collection<Concept> concepts, Integer numberOfVisits, List<String> obsIgnoreList, Boolean filterOutOrderObs);
    public Collection<BahmniObservation> getLatest(String patientUuid, Collection<Concept> conceptNames,Integer numberOfVisits, List<String> obsIgnoreList, Boolean filterOutOrderObs);
    public List<Concept> getNumericConceptsForPerson(String personUUID);
    public Collection<BahmniObservation> getLatestObsForConceptSetByVisit(String patientUuid, String conceptName, Integer visitId);
    Collection<BahmniObservation> getObservationForVisit(String visitUuid, List<String> conceptNames,  Collection<Concept> obsIgnoreList, boolean filterOutOrders);
    Collection<BahmniObservation> getLatestObsByVisit(Visit visit, Collection<Concept> concepts, List<String> obsIgnoreList, Boolean filterObsWithOrders);
    Collection<BahmniObservation> getObservationsForOrder(String orderUuid);
}
