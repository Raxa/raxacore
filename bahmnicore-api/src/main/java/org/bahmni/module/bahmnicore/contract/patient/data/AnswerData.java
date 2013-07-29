package org.bahmni.module.bahmnicore.contract.patient.data;

public class AnswerData {

    private String description;
    private String conceptId;

    public AnswerData(String conceptId, String description) {
        this.conceptId = conceptId;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getConceptId() {
        return conceptId;
    }
}
