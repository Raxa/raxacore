package org.bahmni.module.bahmnicore.contract.diseasetemplate;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.module.emrapi.utils.CustomJsonDateSerializer;

import java.util.ArrayList;
import java.util.Collections;
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
        if(bahmniObservations == null){
            bahmniObservations = new ArrayList<>();
        }
        return Collections.unmodifiableList(bahmniObservations);
    }

    public void setBahmniObservations(List<BahmniObservation> bahmniObservations) {
        this.bahmniObservations = bahmniObservations;
    }

    public void removeBahmniObservation(BahmniObservation bahmniObservation) {
        this.bahmniObservations.remove(bahmniObservation);
    }

    public void removeBahmniObservations(List<BahmniObservation> bahmniObservations) {
        this.bahmniObservations.removeAll(bahmniObservations);
    }

    @JsonSerialize(using = CustomJsonDateSerializer.class)
    public Date getVisitStartDate() {
        return visitStartDate;
    }

    public void setVisitStartDate(Date visitStartDate) {
        this.visitStartDate = visitStartDate;
    }

    public void addBahmniObservation(BahmniObservation bahmniObservation){
        if(bahmniObservations == null){
            bahmniObservations = new ArrayList<BahmniObservation>();
        }
        bahmniObservations.add(bahmniObservation);
    }
}
