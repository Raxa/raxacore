package org.bahmni.module.bahmnicore.contract.observation;

public class LinkData {
    private String visitURI;
    private String encounterURI;
    private String patientURI;

    public LinkData() {
    }

    public LinkData(String visitURI, String encounterURI, String patientURI) {
        this.visitURI = visitURI;
        this.encounterURI = encounterURI;
        this.patientURI = patientURI;
    }

    public String getVisitURI() {
        return visitURI;
    }

    public void setVisitURI(String visitURI) {
        this.visitURI = visitURI;
    }

    public String getEncounterURI() {
        return encounterURI;
    }

    public void setEncounterURI(String encounterURI) {
        this.encounterURI = encounterURI;
    }

    public String getPatientURI() {
        return patientURI;
    }

    public void setPatientURI(String patientURI) {
        this.patientURI = patientURI;
    }
}
