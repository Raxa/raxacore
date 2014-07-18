package org.bahmni.module.bahmnicore.contract.observation;

import java.util.List;

public class LinkData {
    private String visitURI;
    private String encounterURI;
    private String patientURI;
    private List<String> providerURIs;

    public LinkData() {
    }

    public LinkData(String visitURI, String encounterURI, String patientURI, List<String> providerURIs) {
        this.visitURI = visitURI;
        this.encounterURI = encounterURI;
        this.patientURI = patientURI;
        this.providerURIs = providerURIs;
    }

    public List<String> getProviderURIs() {
        return providerURIs;
    }

    public void setProviderURIs(List<String> providerURIs) {
        this.providerURIs = providerURIs;
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
