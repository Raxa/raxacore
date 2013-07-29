package org.bahmni.module.bahmnicore.contract.encounter.request;

import org.bahmni.module.bahmnicore.contract.encounter.data.ObservationData;
import org.bahmni.module.bahmnicore.contract.encounter.data.TestOrderData;

import java.util.ArrayList;
import java.util.List;

public class CreateEncounterRequest {
    private String patientUUID;
    private String visitTypeUUID; //This can be removed when we implement location based login
    private String encounterTypeUUID;

    private List<ObservationData> observations = new ArrayList<>();

    private List<TestOrderData> testOrders = new ArrayList<>();

    public CreateEncounterRequest() {
    }

    public CreateEncounterRequest(String patientUUID, String visitTypeUUID, String encounterTypeUUID, List<ObservationData> observations, List<TestOrderData> testOrders) {
        this.patientUUID = patientUUID;
        this.visitTypeUUID = visitTypeUUID;
        this.encounterTypeUUID = encounterTypeUUID;
        this.observations = observations;
        this.testOrders = testOrders;
    }

    public String getPatientUUID() {
        return patientUUID;
    }

    public String getEncounterTypeUUID() {
        return encounterTypeUUID;
    }

    public String getVisitTypeUUID() {
        return visitTypeUUID;
    }

    public void setPatientUUID(String patientUUID) {
        this.patientUUID = patientUUID;
    }

    public void setVisitTypeUUID(String visitTypeUUID) {
        this.visitTypeUUID = visitTypeUUID;
    }

    public void setEncounterTypeUUID(String encounterTypeUUID) {
        this.encounterTypeUUID = encounterTypeUUID;
    }

    public void setObservations(List<ObservationData> observations) {
        this.observations = observations;
    }

    public List<ObservationData> getObservations() {
        return observations;
    }

    public List<TestOrderData> getTestOrders() {
        return testOrders;
    }

    public void setTestOrders(List<TestOrderData> testOrders) {
        this.testOrders = testOrders;
    }
}