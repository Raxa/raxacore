package org.openmrs.module.bahmniemrapi.encountertransaction.contract;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openmrs.module.bahmniemrapi.accessionnote.contract.AccessionNote;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.openmrs.module.emrapi.diagnosis.Diagnosis;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.module.emrapi.utils.CustomJsonDateSerializer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BahmniEncounterTransaction{

    private EncounterTransaction encounterTransaction = new EncounterTransaction();

    private List<BahmniDiagnosisRequest> bahmniDiagnoses = new ArrayList<>();
    private List<BahmniObservation> observations = new ArrayList<>();
    private List<AccessionNote> accessionNotes;
    private String encounterType;
    private String visitType;

    public BahmniEncounterTransaction() {
    }

    public BahmniEncounterTransaction(EncounterTransaction encounterTransaction) {
        this.encounterTransaction = encounterTransaction;
    }

    public List<BahmniDiagnosisRequest> getBahmniDiagnoses() {
        return bahmniDiagnoses;
    }

    public void setBahmniDiagnoses(List<BahmniDiagnosisRequest> bahmniDiagnoses) {
        this.bahmniDiagnoses = bahmniDiagnoses;
        List<EncounterTransaction.Diagnosis> newDiagnoses = new ArrayList<>();
        for (BahmniDiagnosisRequest bahmniDiagnose : bahmniDiagnoses) {
            newDiagnoses.add(bahmniDiagnose);
        }
        encounterTransaction.setDiagnoses(newDiagnoses);
    }

    public String getVisitUuid() {
        return encounterTransaction.getVisitUuid();
    }

    public void setVisitUuid(String visitUuid) {
        encounterTransaction.setVisitUuid(visitUuid);
    }


    public String getEncounterUuid() {
        return encounterTransaction.getEncounterUuid();
    }


    public void setEncounterUuid(String encounterUuid) {
        encounterTransaction.setEncounterUuid(encounterUuid);
    }


    public void addObservation(BahmniObservation observation) {
        observations.add(observation);
    }


    public void addTestOrder(EncounterTransaction.TestOrder testOrder) {
        encounterTransaction.addTestOrder(testOrder);
    }


    public void addDrugOrder(EncounterTransaction.DrugOrder drugOrder) {
        encounterTransaction.addDrugOrder(drugOrder);
    }


    public void addBahmniDiagnosis(BahmniDiagnosisRequest diagnosis) {
        bahmniDiagnoses.add(diagnosis);
        encounterTransaction.addDiagnosis(diagnosis);
    }


    public Set<EncounterTransaction.Provider> getProviders() {
        return encounterTransaction.getProviders();
    }


    public void setProviders(Set<EncounterTransaction.Provider> providers) {
        encounterTransaction.setProviders(providers);
    }


    public EncounterTransaction.Disposition getDisposition() {
        return encounterTransaction.getDisposition();
    }


    public void setDisposition(EncounterTransaction.Disposition disposition) {
        encounterTransaction.setDisposition(disposition);
    }


    public String getPatientUuid() {
        return encounterTransaction.getPatientUuid();
    }


    public String getEncounterTypeUuid() {
        return encounterTransaction.getEncounterTypeUuid();
    }


    public String getVisitTypeUuid() {
        return encounterTransaction.getVisitTypeUuid();
    }


    public EncounterTransaction setPatientUuid(String patientUuid) {
        return encounterTransaction.setPatientUuid(patientUuid);
    }


    public EncounterTransaction setVisitTypeUuid(String visitTypeUuid) {
        return encounterTransaction.setVisitTypeUuid(visitTypeUuid);
    }


    public EncounterTransaction setEncounterTypeUuid(String encounterTypeUuid) {
        return encounterTransaction.setEncounterTypeUuid(encounterTypeUuid);
    }

    public List<BahmniObservation> getObservations() {
        return this.observations;
    }

    public void setObservations(List<BahmniObservation> observations){
        this.observations = observations;
    }


    public List<EncounterTransaction.TestOrder> getTestOrders() {
        return encounterTransaction.getTestOrders();
    }


    public void setTestOrders(List<EncounterTransaction.TestOrder> testOrders) {
        encounterTransaction.setTestOrders(testOrders);
    }


    public List<EncounterTransaction.DrugOrder> getDrugOrders() {
        return encounterTransaction.getDrugOrders();
    }


    public void setDrugOrders(List<EncounterTransaction.DrugOrder> drugOrders) {
        encounterTransaction.setDrugOrders(drugOrders);
    }


    @JsonSerialize(using = CustomJsonDateSerializer.class)
    public Date getEncounterDateTime() {
        return encounterTransaction.getEncounterDateTime();
    }


    public EncounterTransaction setEncounterDateTime(Date encounterDateTime) {
        return encounterTransaction.setEncounterDateTime(encounterDateTime);
    }


    public String getLocationUuid() {
        return encounterTransaction.getLocationUuid();
    }


    public EncounterTransaction setLocationUuid(String locationUuid) {
        return encounterTransaction.setLocationUuid(locationUuid);
    }

    public List<AccessionNote> getAccessionNotes() {
        return accessionNotes;
    }

    public void setAccessionNotes(List<AccessionNote> accessionNotes) {
        this.accessionNotes = accessionNotes;
    }

    public void setEncounterType(String encounterType) {
        this.encounterType = encounterType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public String getEncounterType() {
        return encounterType;
    }

    public String getVisitType() {
        return visitType;
    }

    public EncounterTransaction toEncounterTransaction(){
        encounterTransaction.setObservations(BahmniObservation.toETObsFromBahmniObs(this.observations));
        return encounterTransaction;
    }
}

