package org.bahmni.module.bahmnicore.contract.encounter.request;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BahmniDiagnosis extends EncounterTransaction.Diagnosis {
    private EncounterTransaction.Concept diagnosisStatusConcept;
    private BahmniDiagnosis firstDiagnosis;
    private boolean revised;

    public EncounterTransaction.Concept getDiagnosisStatusConcept() {
        return diagnosisStatusConcept;
    }

    public void setDiagnosisStatusConcept(EncounterTransaction.Concept diagnosisStatusConcept) {
        this.diagnosisStatusConcept = diagnosisStatusConcept;
    }

    public BahmniDiagnosis getFirstDiagnosis() {
        return firstDiagnosis;
    }

    public void setFirstDiagnosis(BahmniDiagnosis firstDiagnosis) {
        this.firstDiagnosis = firstDiagnosis;
    }

    public boolean isRevised() {
        return revised;
    }

    public void setRevised(boolean revised) {
        this.revised = revised;
    }
}
