package org.bahmni.module.bahmnicore.contract.diseasetemplate;

import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.util.Date;
import java.util.List;

public class ObservationTemplate {
    
    private EncounterTransaction.Concept concept;
    
    private Date visitStartDate;
    
    private List<BahmniObservation> bahmniObservations;

    public EncounterTransaction.Concept getConcept() {
        return concept;
    }

    public void setConcept(EncounterTransaction.Concept concept) {
        this.concept = concept;
    }

    public List<BahmniObservation> getBahmniObservations() {
        return bahmniObservations;
    }

    public void setBahmniObservations(List<BahmniObservation> bahmniObservations) {
        this.bahmniObservations = bahmniObservations;
    }

    public Date getVisitStartDate() {
        return visitStartDate;
    }

    public void setVisitStartDate(Date visitStartDate) {
        this.visitStartDate = visitStartDate;
    }
}
