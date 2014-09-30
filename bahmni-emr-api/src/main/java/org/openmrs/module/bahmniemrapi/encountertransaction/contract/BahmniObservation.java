package org.openmrs.module.bahmniemrapi.encountertransaction.contract;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openmrs.Obs;
import org.openmrs.module.bahmniemrapi.obsrelation.contract.ObsRelationship;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.module.emrapi.utils.CustomJsonDateSerializer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BahmniObservation {

    public static final String CONCEPT_DETAILS_CONCEPT_CLASS = "Concept Details";
    public static final String ABNORMAL_CONCEPT_CLASS = "Abnormal";
    public static final String DURATION_CONCEPT_CLASS = "Duration";

    private ObsRelationship targetObsRelation;
    private EncounterTransaction.Observation encounterTransactionObservation;
    private List<BahmniObservation> groupMembers = new ArrayList<>();
    public Set<EncounterTransaction.Provider> providers;
    private boolean isAbnormal;
    private String type;
    private Long duration;

    public BahmniObservation(EncounterTransaction.Observation encounterTransactionObservation) {
        this(encounterTransactionObservation, false);
    }

    public BahmniObservation(EncounterTransaction.Observation encounterTransactionObservation, boolean flatten) {
        this.encounterTransactionObservation = encounterTransactionObservation;
        if (CONCEPT_DETAILS_CONCEPT_CLASS.equals(encounterTransactionObservation.getConcept().getConceptClass()) && flatten) {
            for (EncounterTransaction.Observation member : encounterTransactionObservation.getGroupMembers()) {
                if (member.getVoided()) {
                    continue;
                }
                if (member.getConcept().getConceptClass().equals(ABNORMAL_CONCEPT_CLASS)) {
                    this.setAbnormal(Boolean.parseBoolean(((EncounterTransaction.Concept) member.getValue()).getName()));
                } else if (member.getConcept().getConceptClass().equals(DURATION_CONCEPT_CLASS)) {
                    this.setDuration(new Double(member.getValue().toString()).longValue());
                } else {
                    this.setValue(member.getValue());
                    this.setType(member.getConcept().getDataType());
                }
            }
        } else {
            for (EncounterTransaction.Observation groupMember : encounterTransactionObservation.getGroupMembers()) {
                addGroupMember(new BahmniObservation(groupMember, flatten));
            }
        }
    }

    public BahmniObservation() {
        encounterTransactionObservation = new EncounterTransaction.Observation();
    }

    public EncounterTransaction.Concept getConcept() {
        return encounterTransactionObservation.getConcept();
    }

    public BahmniObservation setConcept(EncounterTransaction.Concept concept) {
        encounterTransactionObservation.setConcept(concept);
        return this;
    }

    public Object getValue() {
        return encounterTransactionObservation.getValue();
    }

    public BahmniObservation setValue(Object value) {
        encounterTransactionObservation.setValue(value);
        return this;
    }

    public String getComment() {
        return encounterTransactionObservation.getComment();
    }

    public BahmniObservation setComment(String comment) {
        encounterTransactionObservation.setComment(comment);
        return this;
    }

    public BahmniObservation setVoided(boolean voided) {
        encounterTransactionObservation.setVoided(voided);
        return this;
    }

    public boolean getVoided() {
        return encounterTransactionObservation.getVoided();
    }

    public String getVoidReason() {
        return encounterTransactionObservation.getVoidReason();
    }

    public BahmniObservation setVoidReason(String voidReason) {
        encounterTransactionObservation.setVoidReason(voidReason);
        return this;
    }

    public List<BahmniObservation> getGroupMembers() {
        return this.groupMembers;
    }

    public void setGroupMembers(List<BahmniObservation> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public void addGroupMember(BahmniObservation observation) {
        groupMembers.add(observation);
    }

    public String getOrderUuid() {
        return encounterTransactionObservation.getOrderUuid();
    }

    public BahmniObservation setOrderUuid(String orderUuid) {
        encounterTransactionObservation.setOrderUuid(orderUuid);
        return this;
    }

    public BahmniObservation setObservationDateTime(Date observationDateTime) {
        encounterTransactionObservation.setObservationDateTime(observationDateTime);
        return this;
    }

    public String getUuid() {
        return encounterTransactionObservation.getUuid();
    }

    public BahmniObservation setUuid(String uuid) {
        encounterTransactionObservation.setUuid(uuid);
        return this;
    }

    @JsonSerialize(using = CustomJsonDateSerializer.class)
    public Date getObservationDateTime() {
        return encounterTransactionObservation.getObservationDateTime();
    }

    public boolean isSameAs(EncounterTransaction.Observation encounterTransactionObservation) {
        return this.getUuid().equals(encounterTransactionObservation.getUuid());
    }

    public ObsRelationship getTargetObsRelation() {
        return targetObsRelation;
    }

    public void setTargetObsRelation(ObsRelationship targetObsRelation) {
        this.targetObsRelation = targetObsRelation;
    }

    public EncounterTransaction.Observation toETObservation() {
        if (encounterTransactionObservation.getGroupMembers().size() == 0) {
            for (BahmniObservation groupMember : this.groupMembers) {
                encounterTransactionObservation.addGroupMember(groupMember.toETObservation());
            }
        }
        return this.encounterTransactionObservation;
    }

    public String getConceptUuid() {
        return encounterTransactionObservation.getConceptUuid();
    }

    public boolean isSameAs(Obs obs) {
        return this.getUuid().equals(obs.getUuid());
    }

    public static List<EncounterTransaction.Observation> toETObsFromBahmniObs(List<BahmniObservation> bahmniObservations) {
        List<EncounterTransaction.Observation> etObservations = new ArrayList<>();
        for (BahmniObservation bahmniObservation : bahmniObservations) {
            etObservations.add(bahmniObservation.toETObservation());
        }
        return etObservations;
    }

    public boolean hasTargetObsRelation() {
        return targetObsRelation != null && targetObsRelation.getTargetObs() != null;
    }

    public Set<EncounterTransaction.Provider> getProviders() {
        return providers;
    }

    public void setProviders(Set<EncounterTransaction.Provider> providers) {
        this.providers = providers;
    }
    
    public boolean getIsAbnormal() {
        return isAbnormal;
    }

    public boolean isAbnormal() {
        return isAbnormal;
    }

    public void setAbnormal(boolean abnormal) {
        isAbnormal = abnormal;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }
}
